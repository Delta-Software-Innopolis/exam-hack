import EndOfDemo0View from '@/views/EndOfDemo0View.vue'
import LoginView from '@/views/LoginView.vue'
import SignUpView from '@/views/SignUpView.vue'
import WelcomeView from '@/views/WelcomeView.vue'
import OneQuizView from '@/views/OneQuizView.vue'
import QuizesView from '@/views/QuizesView.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
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
      path: "/quizzes",
      name: "quizzes",
      component: QuizesView,
      meta: {
        headerInfo: "Your quizzes",
        headerClass: "header-container"
      }
    },
  ],
})

export default router
