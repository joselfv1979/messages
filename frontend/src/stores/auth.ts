import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '../services/authService'
import type { AuthResponse } from '../types'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<AuthResponse | null>(JSON.parse(localStorage.getItem('user') || 'null'))

  const isAuthenticated = computed(() => user.value !== null)
  const token = computed(() => user.value?.token || '')
  const username = computed(() => user.value?.username || '')

  async function login(username: string, password: string) {
    const data = await authService.login(username, password)
    user.value = data
    localStorage.setItem('user', JSON.stringify(data))
    return data
  }

  async function register(username: string, password: string) {
    const data = await authService.register(username, password)
    user.value = data
    localStorage.setItem('user', JSON.stringify(data))
    return data
  }

  function logout() {
    user.value = null
    localStorage.removeItem('user')
  }

  return { user, isAuthenticated, token, username, login, register, logout }
})
