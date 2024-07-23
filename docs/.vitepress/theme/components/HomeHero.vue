<template>
  <div class="content">
    <img :src="withBase(theme.avator)" width="100" height="100" class="avator"/>
    <FireWorksAnimation/>
    <ShareCard/>
  </div>
</template>
<script lang="ts" setup>
import {useData, withBase} from "vitepress";
import ShareCard from "./ShareCard.vue";
import FireWorksAnimation from "./FireWorksAnimation.vue";

const {theme} = useData();
</script>
<style scoped>
.content {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100px;
}

.avator {
  border-radius: 50%;
  border: 2px solid var(--vp-avator-border);
}

@keyframes avator-transform {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}

.avator:hover {
  animation: 5s linear 0s infinite avator-transform;
}
</style>
