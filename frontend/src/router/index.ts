import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'messages',
      component: () => import('../views/MessageListView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/messages/new',
      name: 'create-message',
      component: () => import('../views/CreateMessageView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/messages/:id',
      name: 'message-detail',
      component: () => import('../views/MessageDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/messages/:id/edit',
      name: 'edit-message',
      component: () => import('../views/EditMessageView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue')
    }
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login' })
  } else {
    next()
  }
})

window.addEventListener('auth:unauthorized', () => {
  const authStore = useAuthStore()
  authStore.logout()
  if (router.currentRoute.value.meta.requiresAuth) {
    router.push({ name: 'login' })
  }
})

export default router
