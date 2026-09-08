import { api } from './api'
import type { AuthResponse } from '../types'

export const authService = {
  async login(username: string, password: string): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/login', { username, password })
    return data
  },

  async register(username: string, password: string): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/register', { username, password })
    return data
  }
}
