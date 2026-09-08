import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

const push = vi.fn()

vi.mock('vue-router', () => ({
  useRouter: () => ({ push }),
  RouterLink: { template: '<a><slot /></a>' }
}))

vi.mock('../../services/messageService', () => ({
  messageService: { create: vi.fn() }
}))

import { messageService } from '../../services/messageService'
import CreateMessageView from '../CreateMessageView.vue'

const mocked = vi.mocked(messageService)

describe('CreateMessageView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('renders the create form', () => {
    const wrapper = mount(CreateMessageView)
    expect(wrapper.find('h1').text()).toContain('New Message')
    expect(wrapper.find('input[type="text"]').exists()).toBe(true)
    expect(wrapper.find('textarea').exists()).toBe(true)
  })

  it('shows error when fields are empty', async () => {
    const wrapper = mount(CreateMessageView)
    await wrapper.find('form').trigger('submit')
    await flushPromises()
    expect(wrapper.find('[role="alert"]').text()).toContain('Both title and body are required')
    expect(mocked.create).not.toHaveBeenCalled()
  })

  it('creates a message and navigates home', async () => {
    mocked.create.mockResolvedValue({
      id: 'm1', title: 'Hello', body: 'World', userId: 'u1',
      createdAt: '', updatedAt: ''
    })
    const wrapper = mount(CreateMessageView)
    await wrapper.find('input[type="text"]').setValue('Hello')
    await wrapper.find('textarea').setValue('World')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(mocked.create).toHaveBeenCalledWith({ title: 'Hello', body: 'World' })
    expect(push).toHaveBeenCalledWith('/')
  })

  it('shows an error when creation fails', async () => {
    const err = Object.assign(new Error('create failed'), {
      response: { data: { message: 'Failed to create message' } }
    })
    mocked.create.mockRejectedValue(err)
    const wrapper = mount(CreateMessageView)
    await wrapper.find('input[type="text"]').setValue('Hello')
    await wrapper.find('textarea').setValue('World')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(wrapper.find('[role="alert"]').text()).toContain('Failed to create message')
    expect(push).not.toHaveBeenCalled()
  })
})
