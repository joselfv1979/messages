import { config } from '@vue/test-utils'
import { vi, beforeEach } from 'vitest'

class LocalStorageMock {
  private store: Record<string, string> = {}

  clear() {
    this.store = {}
  }

  getItem(key: string) {
    return this.store[key] ?? null
  }

  setItem(key: string, value: string) {
    this.store[key] = String(value)
  }

  removeItem(key: string) {
    delete this.store[key]
  }
}

// jsdom provides localStorage, but ensure a clean instance per test
beforeEach(() => {
  const mock = new LocalStorageMock()
  Object.defineProperty(window, 'localStorage', { value: mock, configurable: true })
  window.dispatchEvent = vi.fn()
})

config.global.stubs = {
  RouterLink: { template: '<a><slot /></a>' },
  RouterView: { template: '<div><slot /></div>' }
}

config.global.provide = {
  ...config.global.provide
}
