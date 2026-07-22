package com.examhacker.quiz_create.component

import android.net.Uri
import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.items
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popToFirst
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.common.utility.FilePicker
import com.examhacker.common.utility.IFileResolver
import com.examhacker.domain.model.AnswerVariant
import com.examhacker.domain.model.Question
import com.examhacker.domain.model.QuestionCreate
import com.examhacker.domain.model.QuestionGenerated
import com.examhacker.domain.model.QuestionUpdate
import com.examhacker.domain.model.Quiz
import com.examhacker.domain.repository.IQuizRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlin.collections.plus

interface IQuizCreateComponent {
    val stack: Value<ChildStack<*, Child>>
    val model: Value<Model>

    data class Model(
        val name: String? = null,
        val description: String? = null,
        val quiz: Quiz? = null,
        val forthEnabledGenerate: Boolean = false
    )

    sealed class Child {
        internal data class Name(val component: IQuizNameComponent) : Child()
        internal data class Generate(val component: IQuizGenerateComponent) : Child()
        internal data class Edit(val component: IQuizReviewComponent) : Child()
    }
}

class QuizCreateComponent(
    componentContext: ComponentContext,
    private val quizRepository: IQuizRepository,
    private val filePicker: FilePicker,
    private val fileResolver: IFileResolver,
    private val showErrorToast: (String) -> Unit,
    private val saveQuiz: (Quiz) -> Unit,
    private val toQuizHub: () -> Unit,
    private val toProfile: () -> Unit,
    private val toSettings: () -> Unit,
    private val goBack: () -> Unit
) : IQuizCreateComponent, ComponentContext by componentContext {

    private val _model = MutableValue(IQuizCreateComponent.Model())
    override val model = _model

    private val navigation = StackNavigation<Config>()
    override val stack: Value<ChildStack<*, IQuizCreateComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Name,
            handleBackButton = false,
            childFactory = ::createChild
        )

    private fun createChild(config: Config, componentContext: ComponentContext): IQuizCreateComponent.Child =
        when(config) {
            is Config.Name ->
                IQuizCreateComponent.Child.Name(
                    QuizNameComponent(
                        componentContext = componentContext,
                        updateInfo = ::updateQuizInfo,
                        goToGenerate = ::navigateToGenerate,
                        toQuizHub = ::navigateToQuizHub,
                        toProfile = ::navigateToProfile,
                        toSettings = ::navigateToSettings,
                        goBack = ::back
                    )
                )

            is Config.Generate ->
                IQuizCreateComponent.Child.Generate(
                    QuizGenerateComponent(
                        componentContext = componentContext,
                        filePicker = filePicker,
                        isForthEnabled = model.value.forthEnabledGenerate,
                        saveQuestions = ::updateQuestions,
                        saveForthEnabled = ::updateForthEnabledGenerate,
                        generateQuestions = ::generateQuestions,
                        toReview = ::navigateToReview,
                        toQuizHub = ::navigateToQuizHub,
                        toProfile = ::navigateToProfile,
                        toSettings = ::navigateToSettings,
                        goBack = ::back
                    )
                )

            is Config.Review ->
                IQuizCreateComponent.Child.Edit(
                    QuizReviewComponent(
                        componentContext = componentContext,
                        questions = model.value.quiz!!.questions,
                        onEditQuestion = ::saveChangedQuestion,
                        onAddQuestion = ::addQuestion,
                        onDeleteQuestion = ::deleteQuestion,
                        saveQuiz = ::onSaveQuiz,
                        toQuizHub = ::navigateToQuizHub,
                        toProfile = ::navigateToProfile,
                        toSettings = ::navigateToSettings,
                        goBack = ::back
                    )
                )
        }

    private fun navigateToGenerate() {
        navigation.pushNew(
            Config.Generate(model.value.forthEnabledGenerate)
        )
    }

    private fun navigateToReview() {
        navigation.pushNew(Config.Review)
    }

    private fun navigateToQuizHub() {
        navigation.popToFirst()
        toQuizHub()
    }

    private fun navigateToProfile() {
        navigation.popToFirst()
        toProfile()
    }

    private fun navigateToSettings() {
        navigation.popToFirst()
        toSettings()
    }

    private fun back() {
        Log.d("CreateDebug", "Create stack: ${stack.items}")
        if (stack.items.size > 1) {
            navigation.pop()
        } else {
            goBack()
        }
    }

    private fun updateQuizInfo(name: String, description: String = "") {
        _model.update {
            it.copy(
                name = name,
                description = description
            )
        }

        createEmptyQuiz()
    }

    private fun updateQuestions(questions: List<Question>) {
        _model.update {
            it.copy(
                quiz = it.quiz?.copy(questions = questions)
            )
        }
    }

    private fun onSaveQuiz() {
        saveQuiz(model.value.quiz!!)
        goBack()
    }

    private fun updateForthEnabledGenerate(enabled: Boolean) {
        _model.update {
            it.copy(forthEnabledGenerate = enabled)
        }
    }

    private fun createEmptyQuiz() {
        model.value.name?.let { name ->

            CoroutineScope(Dispatchers.IO).launch {
                quizRepository.createPack(
                    name = name,
                    description = model.value.description
                )
                .onSuccess { quizInfo ->
                    _model.update {
                        it.copy(quiz = Quiz(info = quizInfo, description = model.value.description ?: "", questions = emptyList()))
                    }
                }
                .onFailure { exception ->
                    exception.message?.let {
                        Log.d("CreateDebugEmptyQuiz", "Exception: $it")
                        showErrorToast(it)
                    }
                }
            }
        }
    }

    private fun generateQuestions(uris: List<Uri>) {
        val files = uris.map {
            fileResolver.getFileFromUri(it)
        }
        if (files.any { it == null }) {
            Log.d("CreateDebug", "Null files")
            return
        }
        val questionsGenerated = createMockGeneratedQuestions()

        model.value.quiz?.info?.id?.let { quizId ->

            CoroutineScope(Dispatchers.IO).launch {
                quizRepository.createCards(
                    packId = quizId,
                    questions = questionsGenerated.map {
                        QuestionCreate(
                            description = it.description,
                            hint = it.hint ?: "Hint Text",
                            variants = it.variants
                        )
                    }
                )
                .onSuccess { questions ->
                    _model.update {
                        it.copy(quiz = it.quiz?.copy(questions = questions))
                    }

                    withContext(Dispatchers.Main) { navigateToReview() }
                }
                .onFailure { exception ->
                    exception.message?.let {
                        Log.d("CreateDebug", "Exception: $it")
                        showErrorToast(it)
                    }
                }
            }
        }
    }

    private fun saveChangedQuestion(index: Int, question: Question) {
        val newQuestions = model.value.quiz?.questions?.toMutableList()
        newQuestions?.removeAt(index)
        newQuestions?.add(index, question)

        CoroutineScope(Dispatchers.IO).launch {
            quizRepository.updateCards(
                newQuestions!!.map { question ->
                    QuestionUpdate(
                        id = question.id,
                        question = question.description,
                        options = question.variants.map { it.description },
                        correct = question.variants.mapIndexed { index, variant ->
                            if (variant.isCorrect) index else null
                        }.filterNotNull()
                    )
                }
            )
                .onSuccess {
                    _model.update {
                        it.copy(quiz = it.quiz?.copy(questions = newQuestions))
                    }
                }
                .onFailure { exception ->
                    exception.message?.let {
                        Log.d("EditDebug", "Exception: $it")
                        withContext(Dispatchers.Main) { showErrorToast(it) }
                    }
                }
        }
    }

    private fun deleteQuestion(questionId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            quizRepository.deleteCards(listOf(questionId))
                .onSuccess {
                    val newQuestions = model.value.quiz?.questions?.toMutableList()
                    newQuestions?.removeIf { it.id == questionId }

                    _model.update {
                        it.copy(quiz = it.quiz?.copy(questions = newQuestions!!))
                    }
                }
                .onFailure { exception ->
                    exception.message?.let {
                        Log.d("EditDebug", "Exception: $it")
                        withContext(Dispatchers.Main) { showErrorToast(it) }
                    }
                }
        }
    }

    private fun addQuestion(question: QuestionCreate) {
        CoroutineScope(Dispatchers.IO).launch {
            quizRepository.createCards(
                packId = model.value.quiz!!.info.id,
                questions = listOf(question)
            )
                .onSuccess { createdQuestions ->
                    _model.update {
                        it.copy(quiz = it.quiz?.copy(questions = it.quiz.questions + createdQuestions))
                    }
                }
                .onFailure { exception ->
                    exception.message?.let {
                        Log.d("EditDebug", "Exception: $it")
                        withContext(Dispatchers.Main) { showErrorToast(it) }
                    }
                }
        }
    }

    private fun createMockGeneratedQuestions(): List<QuestionGenerated> =
        listOf(
            QuestionGenerated(
                description = "Which hypotheses were validated by the interview?",
                hint = "All hypotheses that were proposed",
                variants = listOf(
                    AnswerVariant("AI support assistant", true),
                    AnswerVariant("Price comparison", true),
                    AnswerVariant("AI image search", true),
                    AnswerVariant("Referral system", false)
                )
            ),
            QuestionGenerated(
                description = "What is the postcondition of UC-1 Search and Select an Item?",
                hint = "Hint Text",
                variants = listOf(
                    AnswerVariant("Item added to cart", false),
                    AnswerVariant("Item details page opened", true),
                    AnswerVariant("Buyer receives confirmation", false),
                    AnswerVariant("Search results displayed", false)
                )
            ),
            QuestionGenerated(
                description = "Which business rule is dynamic?",
                hint = "Hint Text",
                variants = listOf(
                    AnswerVariant("SRCH-1 Every ad has unique ID", false),
                    AnswerVariant("EVAL-1 At least 10 similar listings for price calculation", false),
                    AnswerVariant("EVAL-4 If fewer than 10, hide widget", true),
                    AnswerVariant("SUPP-1 Support requests processed in order", false)
                )
            ),
            QuestionGenerated(
                description = "How long did the user wait for a support response in the interview?",
                hint = "Hint Text",
                variants = listOf(
                    AnswerVariant("5-10 minutes", false),
                    AnswerVariant("30-60 minutes", true),
                    AnswerVariant("2-3 hours", false),
                    AnswerVariant("1 day", false)
                )
            ),
            QuestionGenerated(
                description = "What happens in UC-2 if fewer than 10 similar listings are available?",
                hint = "Hint Text",
                variants = listOf(
                    AnswerVariant("System shows an error", false),
                    AnswerVariant("System hides the price widget", true),
                    AnswerVariant("System uses the available listings anyway", false),
                    AnswerVariant("System asks user to wait", false)
                )
            ),
            QuestionGenerated(
                description = "Which are true about the AI support assistant feature?",
                hint = "Hint Text",
                variants = listOf(
                    AnswerVariant("It should be available 24/7", true),
                    AnswerVariant("It replaces human operators entirely", false),
                    AnswerVariant("It can transfer to a human operator", true),
                    AnswerVariant("It only works during business hours", false)
                )
            ),
            QuestionGenerated(
                description = "What does the buyer do in the alternative flow of UC-1?",
                hint = "Hint Text",
                variants = listOf(
                    AnswerVariant("Uses voice search", false),
                    AnswerVariant("Uses photo search", true),
                    AnswerVariant("Browses by category", false),
                    AnswerVariant("Contacts seller", false)
                )
            ),
            QuestionGenerated(
                description = "Which business rules are inferences?",
                hint = "Hint Text",
                variants = listOf(
                    AnswerVariant("SRCH-3 System must analyze uploaded image", false),
                    AnswerVariant("EVAL-3 Price >20% below average is 'Great Deal'", true),
                    AnswerVariant("SUPP-2 If AI can't resolve, transfer to human", false),
                    AnswerVariant("SUPP-4 Chat abandoned after 15 min inactivity", true)
                )
            ),
            QuestionGenerated(
                description = "What did the user think about the 'Find by photo' feature?",
                hint = "Hint Text",
                variants = listOf(
                    AnswerVariant("Very useful but not completely confidential", true),
                    AnswerVariant("Not useful", false),
                    AnswerVariant("Useful but slow", false),
                    AnswerVariant("They never used it", false)
                )
            ),
            QuestionGenerated(
                description = "What is the trigger for UC-3 Resolve an Issue via Support?",
                hint = "Hint Text",
                variants = listOf(
                    AnswerVariant("Buyer wants to buy an item", false),
                    AnswerVariant("Buyer encounters a problem", true),
                    AnswerVariant("Buyer opens the app", false),
                    AnswerVariant("Buyer receives a message", false)
                )
            )
        )

    private fun createMockQuestions(): List<Question> =
        listOf(
            Question(
                id = 0,
                description = "Why do dogs think their tails are so clingy, they always want to grab it?",
                hint = "Hint text",
                variants = listOf(
                    AnswerVariant("Option 1", false),
                    AnswerVariant("Option 2", true),
                    AnswerVariant("Option 3", false),
                    AnswerVariant("Option 4", false)
                )
            ),
            Question(
                id = 1,
                description = "Why something is this thing?",
                hint = "Hint text",
                variants = listOf(
                    AnswerVariant("Option 1", false),
                    AnswerVariant("Option 2", true),
                    AnswerVariant("Option 3", false),
                    AnswerVariant("Option 4", false)
                )
            ),
            Question(
                id = 2,
                description = "Why do dogs think their tails are so clingy, they always want to grab it?",
                hint = "Hint text",
                variants = listOf(
                    AnswerVariant("Option 1", false),
                    AnswerVariant("Option 2", true),
                    AnswerVariant("Option 3", false),
                    AnswerVariant("Option 4", false)
                )
            )
        )

    @Serializable
    sealed class Config {
        @Serializable
        data object Name: Config()
        @Serializable
        data class Generate(val forthEnabled: Boolean): Config()
        @Serializable
        data object Review: Config()
    }
}