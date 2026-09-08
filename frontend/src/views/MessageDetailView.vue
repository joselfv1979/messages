<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessageStore } from '../stores/messages'
import type { Message } from '../types'

const router = useRouter()
const route = useRoute()
const store = useMessageStore()

const message = ref<Message | null>(null)
const loading = ref(true)
const error = ref('')
const deleting = ref(false)
const deleteError = ref('')

const messageId = String(route.params.id)

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

async function loadMessage() {
  loading.value = true
  error.value = ''
  try {
    message.value = await store.fetchById(messageId)
  } catch (e: any) {
    if (e.response?.status === 404) {
      error.value = 'Message not found'
    } else {
      error.value = e.response?.data?.message || 'Failed to load message'
    }
  } finally {
    loading.value = false
  }
}

async function handleDelete() {
  if (!confirm('Are you sure you want to delete this message?')) return
  deleting.value = true
  deleteError.value = ''
  try {
    await store.remove(messageId)
    router.push('/')
  } catch {
    deleteError.value = 'Failed to delete the message'
  } finally {
    deleting.value = false
  }
}

onMounted(loadMessage)
</script>

<template>
  <div class="max-w-3xl mx-auto">
    <div class="mb-6">
      <RouterLink
        to="/"
        class="inline-flex items-center gap-1.5 text-sm text-navy-500 dark:text-navy-300 hover:text-navy-700 dark:hover:text-navy-200 transition-colors font-medium"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/></svg>
        Back to messages
      </RouterLink>
    </div>

    <div v-if="loading" role="status" class="flex justify-center py-20" aria-label="Loading message">
      <div class="animate-spin rounded-full h-10 w-10 border-4 border-navy-200 dark:border-navy-700 border-t-navy-600"></div>
    </div>

    <div v-else-if="error" role="alert" class="text-center py-16 bg-red-50 dark:bg-red-900/30 rounded-2xl border border-red-200 dark:border-red-900">
      <div class="text-lg font-medium text-red-600 dark:text-red-300">Something went wrong</div>
      <p class="text-red-500 dark:text-red-400 mt-1">{{ error }}</p>
      <button
        @click="loadMessage"
        class="mt-4 inline-flex items-center gap-2 bg-navy-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-navy-500 transition-all"
      >
        Retry
      </button>
    </div>

    <article v-else-if="message" class="bg-white/90 dark:bg-navy-800/90 backdrop-blur-sm rounded-2xl border border-navy-200 dark:border-navy-700 shadow-lg shadow-navy-100/20 dark:shadow-black/30">
      <div class="p-8">
        <h1 class="text-3xl font-bold text-navy-900 dark:text-navy-100 break-words">{{ message.title }}</h1>
        <p class="mt-2 text-sm text-navy-400 dark:text-navy-500">
          Created {{ formatDate(message.createdAt) }}
          <span v-if="message.updatedAt !== message.createdAt" class="text-navy-300 dark:text-navy-600">
            · Edited {{ formatDate(message.updatedAt) }}
          </span>
        </p>

        <div class="mt-6 border-t border-navy-100 dark:border-navy-700 pt-6">
          <p class="text-navy-700 dark:text-navy-200 leading-relaxed whitespace-pre-wrap">{{ message.body }}</p>
        </div>

        <p v-if="deleteError" role="alert" class="mt-6 text-red-500 text-sm bg-red-50 dark:bg-red-900/30 px-4 py-3 rounded-lg">{{ deleteError }}</p>

        <div class="mt-8 flex items-center gap-3">
          <button
            @click="router.push(`/messages/${message.id}/edit`)"
            class="inline-flex items-center gap-2 bg-navy-600 text-white px-5 py-2.5 rounded-lg font-medium hover:bg-navy-500 transition-all"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
            Edit Message
          </button>
          <button
            @click="handleDelete"
            :disabled="deleting"
            class="inline-flex items-center gap-2 px-5 py-2.5 rounded-lg font-medium text-red-600 dark:text-red-400 border border-red-200 dark:border-red-900 hover:bg-red-50 dark:hover:bg-red-900/30 transition-all disabled:opacity-50"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
            {{ deleting ? 'Deleting...' : 'Delete Message' }}
          </button>
        </div>
      </div>
    </article>
  </div>
</template>