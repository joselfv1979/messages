import { ref, watch } from 'vue'

const isDark = ref(false)

let initialized = false

export function useTheme() {
  if (!initialized) {
    initialized = true
    isDark.value = localStorage.getItem('theme') === 'dark'
    applyTheme()
    watch(isDark, (val) => {
      localStorage.setItem('theme', val ? 'dark' : 'light')
      applyTheme()
    })
  }

  function applyTheme() {
    if (isDark.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  function toggle() {
    isDark.value = !isDark.value
  }

  return { isDark, toggle }
}
