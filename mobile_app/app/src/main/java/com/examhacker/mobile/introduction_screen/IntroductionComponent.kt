package com.examhacker.mobile.introduction_screen

import androidx.compose.material3.TimePicker
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.examhacker.mobile.util.IPermissionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Clock
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

interface IIntroductionComponent {
    val model: Value<Model>

    data class Model(
        val isNotificationGranted: Boolean = false,
        val isOverlayGranted: Boolean = false
    )

    fun requestNotificationPermission()
    fun requestOverlayPermission()
    fun proceedToAuthentication()
}

class IntroductionComponent(
    componentContext: ComponentContext,
    private val permissionHandler: IPermissionHandler,
    private val startOverlayService: () -> Unit,
    private val goToAuth: () -> Unit
): IIntroductionComponent, ComponentContext by componentContext {
    private val _model = MutableValue(IIntroductionComponent.Model())
    override val model: Value<IIntroductionComponent.Model> = _model

    init {
        _model.update {
            it.copy(
                isNotificationGranted = permissionHandler.isNotificationGranted(),
                isOverlayGranted = permissionHandler.isOverlayGranted()
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun requestNotificationPermission() {
        permissionHandler.requestNotificationPermission()

        CoroutineScope(Dispatchers.IO).launch {
            val start = Clock.System.now()

            while (true) {
                val timeDifference = (Clock.System.now() - start).toLong(DurationUnit.MILLISECONDS)
                if (!permissionHandler.isNotificationGranted() &&  timeDifference < 120_000L) {
                    delay(200L)
                } else {
                    _model.update {
                        it.copy(isNotificationGranted = permissionHandler.isNotificationGranted())
                    }
                    break
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun requestOverlayPermission() {
        permissionHandler.requestOverlayPermission()

        CoroutineScope(Dispatchers.IO).launch {
            val start = Clock.System.now()

            while (true) {
                val timeDifference = (Clock.System.now() - start).toLong(DurationUnit.MILLISECONDS)
                if (!permissionHandler.isOverlayGranted() &&  timeDifference < 120_000L) {
                    delay(200L)
                } else {
                    _model.update {
                        it.copy(isOverlayGranted = permissionHandler.isOverlayGranted())
                    }
                    break
                }
            }
        }
    }

    override fun proceedToAuthentication() {
        if (permissionHandler.isNotificationGranted()
            && permissionHandler.isOverlayGranted()) {
            startOverlayService
        }

        goToAuth()
    }
}