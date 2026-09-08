import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

const push = vi.fn()

vi.mock('vue-router', () => ({
  useRouter: () => ({ push }),
  RouterLink: { template: '<a><slot /></a>' }
}))

vi.mock('../../services/authService', () => ({
  authService: { register: vi.fn() }
}))

import { useAuthStore } from '../../stores/auth'
import { authService } from '../../services/authService'
import RegisterView from '../RegisterView.vue'

const mockedAuthService = vi.mocked(authService)

describe('RegisterView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('renders the registration form', () => {
    const wrapper = mount(RegisterView)
    expect(wrapper.find('h1').text()).toContain('Create Account')
    const inputs = wrapper.findAll('input')
    expect(inputs).toHaveLength(3)
  })

  it('shows an error when passwords do not match', async () => {
    const wrapper = mount(RegisterView)
    const inputs = wrapper.findAll('input')

    await inputs[0].setValue('jose')
    await inputs[1].setValue('password')
    await inputs[2].setValue('different')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(wrapper.find('[role="alert"]').text()).toContain('Passwords do not match')
    expect(mockedAuthService.register).not.toHaveBeenCalled()
  })

  it('registers and navigates on success', async () => {
    mockedAuthService.register.mockResolvedValue({
      id: 'u1', username: 'jose', token: 'jwt'
    })
    const wrapper = mount(RegisterView)
    const inputs = wrapper.findAll('input')

    await inputs[0].setValue('jose')
    await inputs[1].setValue('password')
    await inputs[2].setValue('password')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(mockedAuthService.register).toHaveBeenCalledWith('jose', 'password')
    expect(push).toHaveBeenCalledWith('/')
  })

  it('shows validation error from the backend', async () => {
    const err = Object.assign(new Error('validation'), {
      response: { data: { validationErrors: { username: 'Username taken' } } }
    })
    mockedAuthService.register.mockRejectedValue(err)
    const wrapper = mount(RegisterView)
    const inputs = wrapper.findAll('input')

    await inputs[0].setValue('jose')
    await inputs[1].setValue('password')
    await inputs[2].setValue('password')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(wrapper.find('[role="alert"]').text()).toContain('Username taken')
  })
})
