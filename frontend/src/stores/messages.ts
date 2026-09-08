import { defineStore } from 'pinia'
import { ref } from 'vue'
import { messageService } from '../services/messageService'
import type { Message, MessagePayload } from '../types'

export const useMessageStore = defineStore('messages', () => {
  const messages = ref<Message[]>([])
  const loading = ref(false)
  const error = ref('')

  async function fetchAll() {
    loading.value = true
    error.value = ''
    try {
      messages.value = await messageService.getAll()
    } catch (e: any) {
      error.value = e.response?.data?.message || 'Failed to load messages'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function fetchById(id: number | string) {
    return messageService.getById(id)
  }

  async function create(payload: MessagePayload) {
    const data = await messageService.create(payload)
    messages.value.unshift(data)
    return data
  }

  async function update(id: number | string, payload: MessagePayload) {
    const data = await messageService.update(id, payload)
    const idx = messages.value.findIndex(m => m.id === id)
    if (idx !== -1) messages.value[idx] = data
    return data
  }

  async function remove(id: number | string) {
    await messageService.remove(id)
    messages.value = messages.value.filter(m => m.id !== id)
  }

  return { messages, loading, error, fetchAll, fetchById, create, update, remove }
})
