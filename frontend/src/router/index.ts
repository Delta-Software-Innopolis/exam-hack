import EndOfDemo0View from '@/views/EndOfDemo0View.vue'
import LoginView from '@/views/LoginView.vue'
import SignUpView from '@/views/SignUpView.vue'
import WelcomeView from '@/views/WelcomeView.vue'
import GenerateQuizView from '@/views/GenerateQuizView.vue'
import OneQuizView from '@/views/OneQuizView.vue'
import QuizesView from '@/views/QuizesView.vue'
import SolvingView from '@/views/SolvingView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { useAttrs } from 'vue'
import { useUserStore } from '@/stores/user'



const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'root',
      redirect: '/quizzes',
    },
    {
      path: '/welcome',
      name: 'welcome',
      component: WelcomeView,
      meta: {headerClass: "hidden"}
    },
    {
      path: '/auth/signup',
      name: 'signup',
      component: SignUpView,
      meta: {headerClass: "hidden"}
    },
    {
      path: '/auth/login',
      name: 'login',
      component: LoginView,
      meta: {headerClass: "hidden"}
    },
    {
      path: '/end-of-demo-0',
      name: 'end-of-demo-0',
      component: EndOfDemo0View,
      meta: {headerClass: "hidden"}
    },
    {
      path: "/quizzes/:quizId",
      name: "quiz",
      component: OneQuizView
    },
    {
      path: "/quizzes/:quizId/solving",
      name: "solving",
      component: SolvingView
    },
    {
      path: "/quizzes",
      name: "quizzes",
      component: QuizesView
    },
    {
      path: '/quizzes/generate',
      name: 'generating',
      component: GenerateQuizView
    },
  ],
})


const AUTH_PATHS = ['welcome', 'signup', 'login']

function isNotAuthLocation(to: any) {
    return (
        typeof to.name === 'string'
        && !AUTH_PATHS.includes(to.name)
    )
}

router.beforeEach(async (to, from) => {
    const userStore = useUserStore()
    const authenticated = await userStore.isAuthenticated()
    if (!authenticated && isNotAuthLocation(to)) {
        return { name: 'welcome' }
    } else {
        return true
    }
})

export default router
