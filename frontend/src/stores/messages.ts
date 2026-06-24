import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'
import type { Message, MessagePayload } from '../types'
import { useAuthStore } from './auth'

export const useMessageStore = defineStore('messages', () => {
  const messages = ref<Message[]>([])
  const loading = ref(false)

  function authHeaders() {
    const auth = useAuthStore()
    return { Authorization: `Bearer ${auth.token}` }
  }

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await axios.get<Message[]>('/api/messages', { headers: authHeaders() })
      messages.value = data
    } finally {
      loading.value = false
    }
  }

  async function fetchById(id: number) {
    const { data } = await axios.get<Message>(`/api/messages/${id}`, { headers: authHeaders() })
    return data
  }

  async function create(payload: MessagePayload) {
    const { data } = await axios.post<Message>('/api/messages', payload, { headers: authHeaders() })
    messages.value.unshift(data)
    return data
  }

  async function update(id: number, payload: MessagePayload) {
    const { data } = await axios.put<Message>(`/api/messages/${id}`, payload, { headers: authHeaders() })
    const idx = messages.value.findIndex(m => m.id === id)
    if (idx !== -1) messages.value[idx] = data
    return data
  }

  async function remove(id: number) {
    await axios.delete(`/api/messages/${id}`, { headers: authHeaders() })
    messages.value = messages.value.filter(m => m.id !== id)
  }

  return { messages, loading, fetchAll, fetchById, create, update, remove }
})
