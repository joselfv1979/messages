import { describe, it, expect, beforeEach, vi } from 'vitest'
import { nextTick } from 'vue'

describe('useTheme', () => {
  beforeEach(() => {
    localStorage.clear()
    document.documentElement.classList.remove('dark')
    // Re-import the real module on every test so the internal singleton
    // does not leak state between tests
    vi.resetModules()
  })

  it('defaults to light theme when no preference is stored', async () => {
    const { useTheme } = await import('../../composables/useTheme')
    const { isDark } = useTheme()
    expect(isDark.value).toBe(false)
    expect(document.documentElement.classList.contains('dark')).toBe(false)
  })

  it('applies dark theme when preference is stored', async () => {
    localStorage.setItem('theme', 'dark')
    const { useTheme } = await import('../../composables/useTheme')
    useTheme()
    expect(document.documentElement.classList.contains('dark')).toBe(true)
  })

  it('toggles between light and dark', async () => {
    const { useTheme } = await import('../../composables/useTheme')
    const { isDark, toggle } = useTheme()
    toggle()
    await nextTick()
    expect(isDark.value).toBe(true)
    expect(document.documentElement.classList.contains('dark')).toBe(true)
    expect(localStorage.getItem('theme')).toBe('dark')
    toggle()
    await nextTick()
    expect(isDark.value).toBe(false)
    expect(document.documentElement.classList.contains('dark')).toBe(false)
    expect(localStorage.getItem('theme')).toBe('light')
  })
})
