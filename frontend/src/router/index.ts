import EndOfDemo0View from '@/views/EndOfDemo0View.vue'
import LoginView from '@/views/LoginView.vue'
import SignUpView from '@/views/SignUpView.vue'
import WelcomeView from '@/views/WelcomeView.vue'
import GenerateQuizView from '@/views/GenerateQuizView.vue'
import QuizInfoView from '@/views/QuizInfoView.vue'
import QuizesView from '@/views/QuizesView.vue'
import SolvingView from '@/views/SolvingView.vue'
import ShowcaseView from '@/views/ShowcaseView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { useAttrs } from 'vue'
import { useUserStore } from '@/stores/user'
import UnknownView from '@/views/UnknownView.vue'



const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'root',
      redirect: '/quizzes',
      meta: { showSidebar: true }
    },
    {
      path: '/welcome',
      name: 'welcome',
      component: WelcomeView,
      meta: { showSidebar: false }
    },
    {
      path: '/auth/signup',
      name: 'signup',
      component: SignUpView,
      meta: { showSidebar: false }
    },
    {
      path: '/auth/login',
      name: 'login',
      component: LoginView,
      meta: { showSidebar: false }
    },
    {
      path: '/end-of-demo-0',
      name: 'end-of-demo-0',
      component: EndOfDemo0View,
      meta: { showSidebar: false }
    },
    {
      path: "/quizzes/:quizId",
      name: "quiz",
      component: QuizInfoView,
      meta: { showSidebar: true }
    },
    {
      path: "/quizzes/:quizId/solving",
      name: "solving",
      component: SolvingView,
      meta: { showSidebar: true }
    },
    {
      path: "/quizzes",
      name: "quizzes",
      component: QuizesView,
      meta: { showSidebar: true }
    },
    {
      path: '/quizzes/generate',
      name: 'generating',
      component: GenerateQuizView,
      meta: { showSidebar: true }
    },
    {
      path: '/showcase',
      name: 'showcase',
      component: ShowcaseView,
      meta: { showSidebar: true }
    },
    {
      path: '/quizhub',
      name: 'quizhub',
      component: UnknownView,
      meta: { showSidebar: true}
    },
    {
      path: '/profile',
      name: 'profile',
      component: UnknownView,
      meta: { showSidebar: true}
    },
    {
      path: '/settings',
      name: 'settings',
      component: UnknownView,
      meta: { showSidebar: true}
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'unknown',
      component: UnknownView,
      meta: { showSidebar: true }
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
