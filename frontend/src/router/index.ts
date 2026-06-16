import EndOfDemo0View from '@/views/EndOfDemo0View.vue'
import LoginView from '@/views/LoginView.vue'
import SignUpView from '@/views/SignUpView.vue'
import WelcomeView from '@/views/WelcomeView.vue'

import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'welcome',
      component: WelcomeView
    },
    {
      path: '/auth/signup',
      name: 'signup',
      component: SignUpView
    },
    {
      path: '/auth/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/end-of-demo-0',
      name: 'end-of-demo-0',
      component: EndOfDemo0View
    },
  ],
})

export default router
