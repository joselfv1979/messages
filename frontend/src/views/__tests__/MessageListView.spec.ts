import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

const push = vi.fn()
const confirmMock = vi.fn()

vi.mock('vue-router', () => ({
  useRouter: () => ({ push }),
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

import { useMessageStore } from '../../stores/messages'
import { messageService } from '../../services/messageService'
import MessageListView from '../MessageListView.vue'

const mocked = vi.mocked(messageService)

const sample = {
  id: 'm1', title: 'Hello', body: 'World', userId: 'u1',
  createdAt: '2026-01-01T00:00:00', updatedAt: '2026-01-01T00:00:00'
}

describe('MessageListView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
    global.confirm = confirmMock
  })

  it('renders messages on load', async () => {
    mocked.getAll.mockResolvedValue([sample])
    const wrapper = mount(MessageListView)
    await flushPromises()

    expect(wrapper.text()).toContain('Hello')
    expect(wrapper.text()).toContain('World')
  })

  it('shows empty state when there are no messages', async () => {
    mocked.getAll.mockResolvedValue([])
    const wrapper = mount(MessageListView)
    await flushPromises()

    expect(wrapper.text()).toContain('No messages yet')
  })

  it('shows error state and retry when loading fails', async () => {
    const err = Object.assign(new Error('fetch failed'), {
      response: { data: { message: 'Failed to load messages' } }
    })
    mocked.getAll.mockRejectedValue(err)
    const wrapper = mount(MessageListView)
    await flushPromises()

    expect(wrapper.find('[role="alert"]').text()).toContain('Failed to load messages')
  })

  it('deletes a message after confirmation', async () => {
    confirmMock.mockReturnValue(true)
    mocked.getAll.mockResolvedValue([sample])
    mocked.remove.mockResolvedValue()
    const store = useMessageStore()
    const wrapper = mount(MessageListView)
    await flushPromises()

    await wrapper.find('[aria-label="Delete message"]').trigger('click')
    await flushPromises()

    expect(confirmMock).toHaveBeenCalled()
    expect(mocked.remove).toHaveBeenCalledWith('m1')
    expect(store.messages).toHaveLength(0)
  })

  it('does not delete when confirmation is cancelled', async () => {
    confirmMock.mockReturnValue(false)
    mocked.getAll.mockResolvedValue([sample])
    const wrapper = mount(MessageListView)
    await flushPromises()

    await wrapper.find('[aria-label="Delete message"]').trigger('click')
    await flushPromises()

    expect(mocked.remove).not.toHaveBeenCalled()
  })
})
