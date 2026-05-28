<template>
  <router-view v-slot="{ Component, route }">
    <transition :name="transitionName" mode="out-in">
      <div :key="route.path" class="page-wrapper">
        <component :is="Component" />
      </div>
    </transition>
  </router-view>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const transitionName = ref('page-forward')

const navOrder = ['/home', '/create', '/my-orders', '/admin/review', '/admin/users', '/admin/categories']

const getOrderIndex = (path) => navOrder.indexOf(path)

router.beforeEach((to, from) => {
  const toIdx = getOrderIndex(to.path)
  const fromIdx = getOrderIndex(from.path)
  if (toIdx >= 0 && fromIdx >= 0) {
    transitionName.value = toIdx > fromIdx ? 'page-forward' : 'page-backward'
  } else {
    transitionName.value = 'page-forward'
  }
})
</script>

<style scoped>
.page-wrapper {
  width: 100%;
}

.page-forward-enter-active {
  transition: all 350ms cubic-bezier(0.4, 0, 0.2, 1);
}
.page-forward-leave-active {
  transition: all 250ms cubic-bezier(0.4, 0, 1, 1);
}
.page-forward-enter-from {
  opacity: 0;
  transform: scale(0.85) translateX(30px);
}
.page-forward-leave-to {
  opacity: 0;
  transform: scale(0.85) translateX(-30px);
}

.page-backward-enter-active {
  transition: all 350ms cubic-bezier(0.4, 0, 0.2, 1);
}
.page-backward-leave-active {
  transition: all 250ms cubic-bezier(0.4, 0, 1, 1);
}
.page-backward-enter-from {
  opacity: 0;
  transform: scale(0.85) translateX(-30px);
}
.page-backward-leave-to {
  opacity: 0;
  transform: scale(0.85) translateX(30px);
}

@media (prefers-reduced-motion: reduce) {
  .page-forward-enter-active,
  .page-forward-leave-active,
  .page-backward-enter-active,
  .page-backward-leave-active {
    transition-duration: 0ms;
  }
  .page-forward-enter-from,
  .page-forward-leave-to,
  .page-backward-enter-from,
  .page-backward-leave-to {
    opacity: 1;
    transform: none;
  }
}
</style>
