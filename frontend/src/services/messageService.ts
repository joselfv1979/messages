import { api } from './api'
import type { Message, MessagePayload } from '../types'

export const messageService = {
  async getAll(): Promise<Message[]> {
    const { data } = await api.get<Message[]>('/messages')
    return data
  },

  async getById(id: number | string): Promise<Message> {
    const { data } = await api.get<Message>(`/messages/${id}`)
    return data
  },

  async create(payload: MessagePayload): Promise<Message> {
    const { data } = await api.post<Message>('/messages', payload)
    return data
  },

  async update(id: number | string, payload: MessagePayload): Promise<Message> {
    const { data } = await api.put<Message>(`/messages/${id}`, payload)
    return data
  },

  async remove(id: number | string): Promise<void> {
    await api.delete(`/messages/${id}`)
  }
}
