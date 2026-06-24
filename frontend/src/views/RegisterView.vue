<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const loading = ref(false)

async function handleSubmit() {
  error.value = ''
  if (password.value !== confirmPassword.value) {
    error.value = 'Passwords do not match'
    return
  }
  loading.value = true
  try {
    await auth.register(username.value, password.value)
    router.push('/')
  } catch (e: any) {
    error.value = e.response?.data?.error || 'Registration failed'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-[80vh] flex items-center justify-center">
    <div class="w-full max-w-md">
      <div class="bg-white/90 dark:bg-navy-800/90 backdrop-blur-sm rounded-2xl shadow-xl shadow-navy-200/30 dark:shadow-black/30 p-8 border border-navy-200 dark:border-navy-700">
        <div class="text-center mb-8">
          <h1 class="text-3xl font-bold bg-gradient-to-r from-navy-700 to-navy-600 dark:from-navy-300 dark:to-navy-200 bg-clip-text text-transparent">Create Account</h1>
          <p class="text-navy-400 dark:text-navy-300 mt-2">Join MessageBoard today</p>
        </div>

        <form @submit.prevent="handleSubmit" class="space-y-5">
          <div>
            <label class="block text-sm font-medium text-navy-700 dark:text-navy-300 mb-1.5">Username</label>
            <input
              v-model="username"
              type="text"
              required
              placeholder="Choose a username"
              class="w-full px-4 py-2.5 border border-navy-300 dark:border-navy-600 bg-white/95 dark:bg-navy-800/95 text-navy-900 dark:text-navy-100 rounded-lg focus:ring-2 focus:ring-navy-500 focus:border-navy-500 outline-none transition-shadow"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-navy-700 dark:text-navy-300 mb-1.5">Password</label>
            <input
              v-model="password"
              type="password"
              required
              placeholder="Choose a password"
              class="w-full px-4 py-2.5 border border-navy-300 dark:border-navy-600 bg-white/95 dark:bg-navy-800/95 text-navy-900 dark:text-navy-100 rounded-lg focus:ring-2 focus:ring-navy-500 focus:border-navy-500 outline-none transition-shadow"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-navy-700 dark:text-navy-300 mb-1.5">Confirm Password</label>
            <input
              v-model="confirmPassword"
              type="password"
              required
              placeholder="Confirm your password"
              class="w-full px-4 py-2.5 border border-navy-300 dark:border-navy-600 bg-white/95 dark:bg-navy-800/95 text-navy-900 dark:text-navy-100 rounded-lg focus:ring-2 focus:ring-navy-500 focus:border-navy-500 outline-none transition-shadow"
            />
          </div>

          <p v-if="error" class="text-red-500 text-sm bg-red-50 dark:bg-red-900/30 px-3 py-2 rounded-lg">{{ error }}</p>

          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-navy-600 text-white py-2.5 rounded-lg font-medium hover:bg-navy-500 transition-all disabled:opacity-50"
          >
            {{ loading ? 'Creating account...' : 'Create Account' }}
          </button>
        </form>

        <p class="text-center mt-6 text-sm text-navy-400 dark:text-navy-300">
          Already have an account?
          <RouterLink to="/login" class="text-navy-600 dark:text-navy-300 hover:text-navy-700 dark:hover:text-navy-200 font-medium">Sign in</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>
