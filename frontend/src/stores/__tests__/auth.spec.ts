import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '../auth'
import { authService } from '../../services/authService'

vi.mock('../../services/authService', () => ({
  authService: {
    login: vi.fn(),
    register: vi.fn()
  }
}))

const mockedAuthService = vi.mocked(authService)

describe('auth store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('is not authenticated by default', () => {
    const store = useAuthStore()
    expect(store.isAuthenticated).toBe(false)
    expect(store.token).toBe('')
    expect(store.username).toBe('')
  })

  it('restores session from localStorage', () => {
    localStorage.setItem('user', JSON.stringify({
      id: 'u1', username: 'jose', token: 'token'
    }))
    const store = useAuthStore()
    expect(store.isAuthenticated).toBe(true)
    expect(store.username).toBe('jose')
    expect(store.token).toBe('token')
  })

  it('logs in and persists the session', async () => {
    const response = { id: 'u1', username: 'jose', token: 'jwt' }
    mockedAuthService.login.mockResolvedValue(response)

    const store = useAuthStore()
    await store.login('jose', 'password')

    expect(store.isAuthenticated).toBe(true)
    expect(store.username).toBe('jose')
    expect(JSON.parse(localStorage.getItem('user')!)).toEqual(response)
  })

  it('registers and persists the session', async () => {
    const response = { id: 'u1', username: 'jose', token: 'jwt' }
    mockedAuthService.register.mockResolvedValue(response)

    const store = useAuthStore()
    await store.register('jose', 'password')

    expect(store.isAuthenticated).toBe(true)
    expect(JSON.parse(localStorage.getItem('user')!)).toEqual(response)
  })

  it('logs out and clears the session', async () => {
    localStorage.setItem('user', JSON.stringify({
      id: 'u1', username: 'jose', token: 'jwt'
    }))
    const store = useAuthStore()
    store.logout()
    expect(store.isAuthenticated).toBe(false)
    expect(localStorage.getItem('user')).toBeNull()
  })
})
