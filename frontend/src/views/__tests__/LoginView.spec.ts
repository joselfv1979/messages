import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

const push = vi.fn()

vi.mock('vue-router', () => ({
  useRouter: () => ({ push }),
  RouterLink: { template: '<a><slot /></a>' }
}))

vi.mock('../../services/authService', () => ({
  authService: {
    login: vi.fn()
  }
}))

import { useAuthStore } from '../../stores/auth'
import { authService } from '../../services/authService'
import LoginView from '../LoginView.vue'

const mockedAuthService = vi.mocked(authService)

describe('LoginView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('renders the login form', () => {
    const wrapper = mount(LoginView)
    expect(wrapper.find('h1').text()).toContain('Welcome Back')
    expect(wrapper.find('input[type="text"]').exists()).toBe(true)
    expect(wrapper.find('input[type="password"]').exists()).toBe(true)
  })

  it('logs in and navigates to messages', async () => {
    mockedAuthService.login.mockResolvedValue({
      id: 'u1', username: 'jose', token: 'jwt'
    })
    const wrapper = mount(LoginView)

    await wrapper.find('input[type="text"]').setValue('jose')
    await wrapper.find('input[type="password"]').setValue('password')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(mockedAuthService.login).toHaveBeenCalledWith('jose', 'password')
    expect(push).toHaveBeenCalledWith('/')
  })

  it('shows an error message when login fails', async () => {
    const err = Object.assign(new Error('login failed'), {
      response: { data: { message: 'Invalid credentials' } }
    })
    mockedAuthService.login.mockRejectedValue(err)
    const wrapper = mount(LoginView)

    await wrapper.find('input[type="text"]').setValue('jose')
    await wrapper.find('input[type="password"]').setValue('wrong')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(wrapper.find('[role="alert"]').text()).toContain('Invalid credentials')
  })

  it('does not navigate on failed login', async () => {
    const err = Object.assign(new Error('login failed'), {
      response: { data: { message: 'Invalid credentials' } }
    })
    mockedAuthService.login.mockRejectedValue(err)
    const wrapper = mount(LoginView)

    await wrapper.find('input[type="text"]').setValue('jose')
    await wrapper.find('input[type="password"]').setValue('wrong')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(push).not.toHaveBeenCalled()
  })
})
