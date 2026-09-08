import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

const push = vi.fn()
const confirmMock = vi.fn()

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
import MessageDetailView from '../MessageDetailView.vue'

const mocked = vi.mocked(messageService)

const sample = {
  id: 'm1', title: 'Hello', body: 'World', userId: 'u1',
  createdAt: '2026-01-01T00:00:00', updatedAt: '2026-01-01T00:00:00'
}

describe('MessageDetailView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
    global.confirm = confirmMock
  })

  it('displays the message content', async () => {
    mocked.getById.mockResolvedValue(sample)
    const wrapper = mount(MessageDetailView)
    await flushPromises()

    expect(mocked.getById).toHaveBeenCalledWith('m1')
    expect(wrapper.find('h1').text()).toBe('Hello')
    expect(wrapper.text()).toContain('World')
    expect(wrapper.text()).toContain('Edit Message')
    expect(wrapper.text()).toContain('Delete Message')
  })

  it('shows loading state while fetching', () => {
    mocked.getById.mockReturnValue(new Promise(() => {}))
    const wrapper = mount(MessageDetailView)

    expect(wrapper.find('[role="status"]').exists()).toBe(true)
  })

  it('shows not found error for a missing message', async () => {
    const err = Object.assign(new Error('not found'), {
      response: { status: 404 }
    })
    mocked.getById.mockRejectedValue(err)
    const wrapper = mount(MessageDetailView)
    await flushPromises()

    expect(wrapper.find('[role="alert"]').text()).toContain('Message not found')
  })

  it('deletes the message after confirmation and navigates home', async () => {
    confirmMock.mockReturnValue(true)
    mocked.getById.mockResolvedValue(sample)
    mocked.remove.mockResolvedValue()
    const wrapper = mount(MessageDetailView)
    await flushPromises()

    const deleteButton = wrapper.findAll('button').find(b => b.text().includes('Delete'))
    await deleteButton!.trigger('click')
    await flushPromises()

    expect(confirmMock).toHaveBeenCalled()
    expect(mocked.remove).toHaveBeenCalledWith('m1')
    expect(push).toHaveBeenCalledWith('/')
  })

  it('does not delete when confirmation is cancelled', async () => {
    confirmMock.mockReturnValue(false)
    mocked.getById.mockResolvedValue(sample)
    const wrapper = mount(MessageDetailView)
    await flushPromises()

    const deleteButton = wrapper.findAll('button').find(b => b.text().includes('Delete'))
    await deleteButton!.trigger('click')
    await flushPromises()

    expect(mocked.remove).not.toHaveBeenCalled()
  })
})