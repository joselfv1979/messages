<script setup lang="ts">
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'
import { useTheme } from '../composables/useTheme'

const auth = useAuthStore()
const router = useRouter()
const { isDark, toggle } = useTheme()

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <nav class="bg-white/80 dark:bg-navy-900/80 backdrop-blur-md shadow-sm border-b border-navy-200/60 dark:border-navy-800">
    <div class="max-w-5xl mx-auto px-4 h-16 flex items-center justify-between">
      <RouterLink to="/" class="text-xl font-bold bg-gradient-to-r from-navy-700 to-navy-600 dark:from-navy-300 dark:to-navy-200 bg-clip-text text-transparent">
        MessageBoard
      </RouterLink>
      <div class="flex items-center gap-4">
        <button
          @click="toggle"
          class="p-2 rounded-lg text-navy-400 dark:text-navy-300 hover:bg-navy-100 dark:hover:bg-navy-800 transition-colors"
          :title="isDark ? 'Switch to light mode' : 'Switch to dark mode'"
          :aria-label="isDark ? 'Switch to light mode' : 'Switch to dark mode'"
        >
          <svg v-if="isDark" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"/>
          </svg>
          <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"/>
          </svg>
        </button>
        <template v-if="auth.isAuthenticated">
          <RouterLink to="/messages/new" class="inline-flex items-center gap-1.5 bg-navy-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-navy-500 transition-colors">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
            New Message
          </RouterLink>
          <span class="text-sm text-navy-400 dark:text-navy-300">Hi, <span class="font-medium text-navy-700 dark:text-navy-200">{{ auth.username }}</span></span>
          <button @click="handleLogout" class="text-sm text-navy-400 dark:text-navy-300 hover:text-navy-600 dark:hover:text-navy-200 transition-colors font-medium">
            Logout
          </button>
        </template>
      </div>
    </div>
  </nav>
</template>
