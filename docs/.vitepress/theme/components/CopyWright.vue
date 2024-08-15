<template>
  <footer>
    Powered by
    <a href="https://github.com/clark-cui/" target="_blank" title="作者"
    >Lucifer Yuan</a
    >
    | Copyright © 2024~ | MIT License
  </footer>
</template>
<style scoped>
footer {
  text-align: center;
  position: absolute;
  width: 100%;
  bottom: 20px;
  padding: 0 20px;
  background: var(--vp-c-bg);
  color: var(--vp-c-text);
}

a {
  color: var(--vp-c-brand);
  font-weight: bold;
}

a:hover {
  text-decoration: none;
  color: var(--vp-c-hover);
}
</style>
