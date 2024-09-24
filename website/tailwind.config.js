/** @type {import('tailwindcss').Config} */
const colors = require("tailwindcss/colors");
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  safelist: [{pattern: /bg-/}, {pattern: /text-/}, {pattern: /border-/}, {pattern: /p-/}],
  theme: {
    extend: {
      colors: {
        primary: colors.orange,
      },
      backgroundImage: {
        'egyptian-temple': "url('assets/images/egyptian_temple_interior.jpg')",
        'papyrus': "url('assets/images/papyrus.jpg')",
        'cloud': "url('assets/images/cloud.jpg')",
      },
      width: {
        '100': "34em"
      },
      height: {
        '100': "34em"
      }
    },
  },
  plugins: [],
}

