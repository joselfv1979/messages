import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

const push = vi.fn()

vi.mock('vue-router', () => ({
  useRouter: () => ({ push }),
  useRoute: () => ({ params: { id: 'm1' } }),
  RouterLink: { template: '<a><slot /></a>' }
}))

vi.mock('../../services/messageService', () => ({
  messageService: {
    getAll: vi.fn(),
    getById: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    remove: vi.fn()
  }
}))

import { messageService } from '../../services/messageService'
import EditMessageView from '../EditMessageView.vue'

const mocked = vi.mocked(messageService)

const sample = {
  id: 'm1', title: 'Hello', body: 'World', userId: 'u1',
  createdAt: '2026-01-01T00:00:00', updatedAt: '2026-01-01T00:00:00'
}

describe('EditMessageView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('loads the message and populates the form', async () => {
    mocked.getById.mockResolvedValue(sample)
    const wrapper = mount(EditMessageView)
    await flushPromises()

    const titleInput = wrapper.find('input[type="text"]')
    expect((titleInput.element as HTMLInputElement).value).toBe('Hello')
  })

  it('shows a not found error when the message does not exist', async () => {
    const err = Object.assign(new Error('not found'), {
      response: { status: 404 }
    })
    mocked.getById.mockRejectedValue(err)
    const wrapper = mount(EditMessageView)
    await flushPromises()

    expect(wrapper.find('[role="alert"]').text()).toContain('Message not found')
  })

  it('shows a generic error and retry when loading fails', async () => {
    mocked.getById.mockRejectedValue(new Error('server down'))
    const wrapper = mount(EditMessageView)
    await flushPromises()

    expect(wrapper.find('[role="alert"]').text()).toContain('Failed to load message')
    expect(wrapper.text()).toContain('Retry')
  })

  it('updates the message and navigates home', async () => {
    mocked.getById.mockResolvedValue(sample)
    mocked.update.mockResolvedValue({ ...sample, title: 'Updated' })
    const wrapper = mount(EditMessageView)
    await flushPromises()

    await wrapper.find('input[type="text"]').setValue('Updated')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(mocked.update).toHaveBeenCalledWith('m1', { title: 'Updated', body: 'World' })
    expect(push).toHaveBeenCalledWith('/')
  })

  it('shows an error when update fails', async () => {
    mocked.getById.mockResolvedValue(sample)
    const err = Object.assign(new Error('update failed'), {
      response: { data: { message: 'Failed to update message' } }
    })
    mocked.update.mockRejectedValue(err)
    const wrapper = mount(EditMessageView)
    await flushPromises()

    await wrapper.find('input[type="text"]').setValue('Updated')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(wrapper.find('[role="alert"]').text()).toContain('Failed to update message')
    expect(push).not.toHaveBeenCalled()
  })
})
