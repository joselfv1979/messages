<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessageStore } from '../stores/messages'

const router = useRouter()
const store = useMessageStore()

const title = ref('')
const body = ref('')
const error = ref('')
const loading = ref(false)

async function handleSubmit() {
  if (!title.value.trim() || !body.value.trim()) {
    error.value = 'Both title and body are required'
    return
  }
  error.value = ''
  loading.value = true
  try {
    await store.create({ title: title.value, body: body.value })
    router.push('/')
  } catch (e: any) {
    error.value = e.response?.data?.message || 'Failed to create message'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="max-w-2xl mx-auto">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-navy-900 dark:text-navy-100">New Message</h1>
      <p class="text-navy-400 dark:text-navy-300 mt-1">Create a new message</p>
    </div>

    <div class="bg-white/90 dark:bg-navy-800/90 backdrop-blur-sm rounded-2xl border border-navy-200 dark:border-navy-700 p-8 shadow-lg shadow-navy-100/20 dark:shadow-black/30">
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div>
          <label for="create-title" class="block text-sm font-medium text-navy-700 dark:text-navy-300 mb-1.5">Title</label>
          <input
            id="create-title"
            v-model="title"
            type="text"
            required
            placeholder="Enter message title"
            class="w-full px-4 py-2.5 border border-navy-300 dark:border-navy-600 bg-white/95 dark:bg-navy-800/95 text-navy-900 dark:text-navy-100 rounded-lg focus:ring-2 focus:ring-navy-500 focus:border-navy-500 outline-none transition-shadow"
          />
        </div>

        <div>
          <label for="create-body" class="block text-sm font-medium text-navy-700 dark:text-navy-300 mb-1.5">Body</label>
          <textarea
            id="create-body"
            v-model="body"
            required
            rows="6"
            placeholder="Write your message here..."
            class="w-full px-4 py-2.5 border border-navy-300 dark:border-navy-600 bg-white/95 dark:bg-navy-800/95 text-navy-900 dark:text-navy-100 rounded-lg focus:ring-2 focus:ring-navy-500 focus:border-navy-500 outline-none transition-shadow resize-none"
          ></textarea>
        </div>

        <p v-if="error" role="alert" class="text-red-500 text-sm bg-red-50 dark:bg-red-900/30 px-3 py-2 rounded-lg">{{ error }}</p>

        <div class="flex items-center gap-3">
          <button
            type="submit"
            :disabled="loading"
            class="bg-navy-600 text-white px-6 py-2.5 rounded-lg font-medium hover:bg-navy-500 transition-all disabled:opacity-50"
          >
            {{ loading ? 'Creating...' : 'Create Message' }}
          </button>
          <RouterLink
            to="/"
            class="px-6 py-2.5 text-navy-500 dark:text-navy-300 hover:text-navy-700 dark:hover:text-navy-200 font-medium transition-colors"
          >
            Cancel
          </RouterLink>
        </div>
      </form>
    </div>
  </div>
</template>
