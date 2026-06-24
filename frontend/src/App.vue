<script setup lang="ts">
import NavBar from './components/NavBar.vue'
import { useTheme } from './composables/useTheme'

useTheme()
</script>

<template>
  <div class="min-h-screen bg-navy-50 dark:bg-navy-950 dark-transition">
    <div class="fixed inset-0 bg-[url('/background.jpeg')] bg-cover bg-center bg-no-repeat" />
    <div class="relative z-10 flex flex-col min-h-screen">
      <NavBar />
      <main class="flex-1 max-w-5xl w-full mx-auto px-4 py-8">
        <RouterView />
      </main>
    </div>
  </div>
</template>
