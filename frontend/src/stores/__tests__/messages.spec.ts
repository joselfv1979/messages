import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useMessageStore } from '../messages'
import { messageService } from '../../services/messageService'

vi.mock('../../services/messageService', () => ({
  messageService: {
    getAll: vi.fn(),
    getById: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    remove: vi.fn()
  }
}))

const mocked = vi.mocked(messageService)

const sampleMessage = {
  id: 'm1',
  title: 'Title',
  body: 'Body',
  userId: 'u1',
  createdAt: '2026-01-01T00:00:00',
  updatedAt: '2026-01-01T00:00:00'
}

describe('messages store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('fetches all messages', async () => {
    mocked.getAll.mockResolvedValue([sampleMessage])
    const store = useMessageStore()

    await store.fetchAll()

    expect(store.messages).toHaveLength(1)
    expect(store.messages[0]).toEqual(sampleMessage)
    expect(store.loading).toBe(false)
  })

  it('sets error and rethrows when fetching fails', async () => {
    const err = Object.assign(new Error('fetch failed'), {
      response: { data: { message: 'Failed to load messages' } }
    })
    mocked.getAll.mockRejectedValue(err)
    const store = useMessageStore()

    await expect(store.fetchAll()).rejects.toBe(err)
    expect(store.error).toBe('Failed to load messages')
  })

  it('creates a message and prepends it', async () => {
    mocked.create.mockResolvedValue(sampleMessage)
    const store = useMessageStore()

    await store.create({ title: 'Title', body: 'Body' })

    expect(store.messages[0]).toEqual(sampleMessage)
  })

  it('updates a message in place', async () => {
    const updated = { ...sampleMessage, title: 'Updated' }
    mocked.update.mockResolvedValue(updated)
    const store = useMessageStore()
    store.messages = [sampleMessage]

    await store.update('m1', { title: 'Updated', body: 'Body' })

    expect(store.messages[0].title).toBe('Updated')
  })

  it('removes a message from the list', async () => {
    mocked.remove.mockResolvedValue()
    const store = useMessageStore()
    store.messages = [sampleMessage]

    await store.remove('m1')

    expect(store.messages).toHaveLength(0)
  })
})
