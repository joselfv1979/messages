/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        navy: {
          50: '#eef2f7',
          100: '#d0dae8',
          200: '#b1c2d9',
          300: '#93aaca',
          400: '#6b8bb0',
          500: '#4a6a8f',
          600: '#334a6a',
          700: '#23334c',
          800: '#141d2c',
          900: '#0a1120',
          950: '#040816',
        },
      },
    },
  },
  plugins: [],
}
