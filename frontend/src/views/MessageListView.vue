<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessageStore } from '../stores/messages'

const store = useMessageStore()
const router = useRouter()
const deleteError = ref('')

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

async function handleDelete(id: string) {
  if (confirm('Are you sure you want to delete this message?')) {
    deleteError.value = ''
    try {
      await store.remove(id)
    } catch {
      deleteError.value = 'Failed to delete the message'
    }
  }
}

onMounted(() => {
  store.fetchAll().catch(() => {})
})
</script>

<template>
  <div>
    <div class="flex items-center justify-between mb-8">
      <div>
        <h1 class="text-3xl font-bold text-navy-900 dark:text-navy-100">Messages</h1>
        <p class="text-navy-400 dark:text-navy-300 mt-1">Manage your messages</p>
      </div>
      <RouterLink
        to="/messages/new"
        class="inline-flex items-center gap-2 bg-navy-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-navy-500 transition-all shadow-lg shadow-navy-200/30 dark:shadow-black/30"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
        New Message
      </RouterLink>
    </div>

    <div v-if="store.error" role="alert" class="text-center py-12 bg-red-50 dark:bg-red-900/30 rounded-2xl border border-red-200 dark:border-red-900">
      <div class="text-lg font-medium text-red-600 dark:text-red-300">Something went wrong</div>
      <p class="text-red-500 dark:text-red-400 mt-1">{{ store.error }}</p>
      <button
        @click="store.fetchAll().catch(() => {})"
        class="mt-4 inline-flex items-center gap-2 bg-navy-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-navy-500 transition-all"
      >
        Retry
      </button>
    </div>

    <p v-if="deleteError" role="alert" class="mb-4 text-red-500 text-sm bg-red-50 dark:bg-red-900/30 px-4 py-3 rounded-lg">{{ deleteError }}</p>

    <div v-if="store.loading" role="status" class="flex justify-center py-20" aria-label="Loading messages">
      <div class="animate-spin rounded-full h-10 w-10 border-4 border-navy-200 dark:border-navy-700 border-t-navy-600"></div>
    </div>

    <div v-else-if="!store.error && store.messages.length === 0" class="text-center py-20 bg-white/90 dark:bg-navy-800/90 backdrop-blur-sm rounded-2xl border border-dashed border-navy-300 dark:border-navy-700">
      <div class="text-navy-300 dark:text-navy-500 mb-4">
        <svg class="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/></svg>
      </div>
      <h3 class="text-lg font-medium text-navy-600 dark:text-navy-300">No messages yet</h3>
      <p class="text-navy-400 dark:text-navy-500 mt-1">Create your first message to get started</p>
      <RouterLink to="/messages/new" class="inline-block mt-4 text-navy-600 dark:text-navy-300 hover:text-navy-700 dark:hover:text-navy-200 font-medium">Create a message →</RouterLink>
    </div>

    <div v-else-if="!store.error" class="grid gap-4">
      <div
        v-for="message in store.messages"
        :key="message.id"
        @click="router.push(`/messages/${message.id}`)"
        class="bg-white/90 dark:bg-navy-800/90 backdrop-blur-sm rounded-xl border border-navy-200 dark:border-navy-700 p-6 hover:shadow-lg hover:shadow-navy-100/30 dark:hover:shadow-black/20 hover:border-navy-300 dark:hover:border-navy-600 transition-all duration-200 cursor-pointer"
      >
        <div class="flex items-start justify-between gap-4">
          <div class="flex-1 min-w-0">
            <h3 class="text-lg font-semibold text-navy-900 dark:text-navy-100 truncate">{{ message.title }}</h3>
            <p class="mt-2 text-navy-500 dark:text-navy-300 line-clamp-2 whitespace-pre-wrap">{{ message.body }}</p>
            <p class="mt-3 text-xs text-navy-400 dark:text-navy-500">{{ formatDate(message.createdAt) }}</p>
          </div>
          <div class="flex items-center gap-2 shrink-0">
            <button
              @click.stop="router.push(`/messages/${message.id}/edit`)"
              class="p-2 text-navy-400 dark:text-navy-500 hover:text-navy-600 dark:hover:text-navy-300 hover:bg-navy-100 dark:hover:bg-navy-700 rounded-lg transition-colors"
              title="Edit"
              aria-label="Edit message"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
            </button>
            <button
              @click.stop="handleDelete(message.id)"
              class="p-2 text-navy-400 dark:text-navy-500 hover:text-red-600 dark:hover:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/30 rounded-lg transition-colors"
              title="Delete"
              aria-label="Delete message"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
