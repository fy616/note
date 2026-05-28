<template>
  <header class="carousel-nav" role="navigation" aria-label="主导航">
    <div class="carousel-nav-inner">
      <!-- Logo -->
      <router-link to="/home" class="carousel-logo" @click.prevent="goHome">
        <span class="carousel-logo-icon">♻</span>
        <span class="carousel-logo-text">校园帮扔</span>
      </router-link>

      <!-- 轮盘导航 -->
      <div
        class="carousel-scene"
        @keydown="carousel.onKeydown"
        tabindex="0"
      >
        <button class="carousel-arrow carousel-arrow-left" @click="carousel.rotatePrev" aria-label="上一个">‹</button>
        <div class="carousel-track">
          <CarouselItem
            v-for="(item, idx) in allItems"
            :key="item.path || item.label"
            :icon="item.icon"
            :label="item.label"
            :angle="getRelativeAngle(idx)"
            :is-active="idx === carousel.activeIndex.value"
            :visible="carousel.isVisible(idx, allItems.length)"
            :radius="radius"
            @select="handleSelect(idx)"
          />

          <!-- 管理扇区（仅管理员可见） -->
          <SectorGroup
            v-if="isAdmin"
            :is-expanded="carousel.isExpanded.value"
            :is-active="adminSectorActive"
            :admin-items="adminItems"
            :active-path="currentPath"
            :base-angle="getRelativeAngle(allItems.length - 1)"
            :radius="radius"
            @expand="handleSectorExpand"
            @collapse="handleSectorCollapse"
            @navigate="handleAdminNavigate"
          />
        </div>
        <button class="carousel-arrow carousel-arrow-right" @click="carousel.rotateNext" aria-label="下一个">›</button>
      </div>

      <!-- 用户信息 -->
      <div class="carousel-user">
        <span class="carousel-username">{{ username }}</span>
        <button class="carousel-logout" @click="handleLogout">退出</button>
      </div>
    </div>

    <!-- 移动端管理抽屉 -->
    <Teleport to="body">
      <div
        v-if="isMobileDrawer"
        class="mobile-drawer-overlay"
        @click="handleSectorCollapse"
      >
        <div class="mobile-drawer" @click.stop>
          <div class="mobile-drawer-header">
            <span>管理菜单</span>
            <button class="mobile-drawer-close" @click="handleSectorCollapse">✕</button>
          </div>
          <div class="mobile-drawer-items">
            <div
              v-for="item in adminItems"
              :key="item.path"
              class="mobile-drawer-item"
              :class="{ active: currentPath === item.path }"
              @click="handleAdminNavigate(item.path)"
            >
              <span>{{ item.icon }}</span>
              <span>{{ item.label }}</span>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </header>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useCarousel } from '../composables/useCarousel'
import CarouselItem from './CarouselItem.vue'
import SectorGroup from './SectorGroup.vue'

const props = defineProps({
  items: { type: Array, required: true },
  adminItems: { type: Array, default: () => [] },
  isAdmin: { type: Boolean, default: false },
  username: { type: String, default: '' }
})

const router = useRouter()
const route = useRoute()
const store = useUserStore()

// 普通菜单项 + 管理占位
const allItems = computed(() => {
  const base = [...props.items]
  if (props.isAdmin) {
    base.push({ label: '管理', icon: '⚙️', path: null, isSector: true })
  }
  return base
})

const carousel = useCarousel(allItems)

const currentPath = computed(() => route.path)

// 响应式半径
const isMobile = ref(window.innerWidth < 768)
const radius = computed(() => isMobile.value ? '80px' : '120px')

const adminSectorActive = computed(() => {
  return carousel.isExpanded.value ||
    ['/admin/review', '/admin/users', '/admin/categories'].includes(route.path)
})

// 计算相对角度
const getRelativeAngle = (index) => {
  const diff = index - carousel.activeIndex.value
  return diff * (360 / allItems.value.length)
}

// 事件处理
const handleSelect = (idx) => {
  const item = allItems.value[idx]
  if (item.isSector) {
    if (isMobile.value) {
      isMobileDrawer.value = true
    } else {
      carousel.isExpanded.value = true
    }
    return
  }
  carousel.navigateTo(idx, router)
}

const goHome = () => {
  const idx = allItems.value.findIndex(i => i.path === '/home')
  if (idx !== -1) carousel.navigateTo(idx, router)
}

const handleSectorExpand = () => {
  if (isMobile.value) {
    isMobileDrawer.value = true
  } else {
    carousel.isExpanded.value = true
  }
}

const handleSectorCollapse = () => {
  carousel.isExpanded.value = false
  isMobileDrawer.value = false
}

const handleAdminNavigate = (path) => {
  carousel.isExpanded.value = false
  isMobileDrawer.value = false
  router.push(path)
}

const handleLogout = () => {
  store.logout()
  window.__toast?.('已退出登录', 'info')
  setTimeout(() => router.push('/'), 300)
}

// 移动端抽屉
const isMobileDrawer = ref(false)

// 路由同步
watch(() => route.path, (path) => {
  carousel.syncFromRoute(path)
}, { immediate: true })

// 响应式监听
const onResize = () => {
  isMobile.value = window.innerWidth < 768
}
onMounted(() => window.addEventListener('resize', onResize))
onUnmounted(() => window.removeEventListener('resize', onResize))
</script>

<style scoped>
.carousel-nav {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 12px rgba(0, 0, 0, 0.04);
}

.carousel-nav-inner {
  max-width: 960px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.carousel-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  flex-shrink: 0;
}

.carousel-logo-icon {
  font-size: 28px;
  line-height: 1;
}

.carousel-logo-text {
  font-size: 19px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--green-600), var(--green-800));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.5px;
}

/* 轮盘场景 */
.carousel-scene {
  overflow: visible;
  height: 60px;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  outline: none;
}

.carousel-track {
  position: relative;
  width: 0;
  height: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-arrow {
  background: none;
  border: 1.5px solid var(--green-300);
  color: var(--green-600);
  font-size: 20px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s ease;
  flex-shrink: 0;
  line-height: 1;
  padding: 0;
}

.carousel-arrow:hover {
  background: var(--green-50);
  border-color: var(--green-400);
}

/* 用户信息 */
.carousel-user {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.carousel-username {
  font-size: 13px;
  color: var(--gray-700);
  font-weight: 500;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.carousel-logout {
  background: none;
  border: none;
  color: var(--gray-500);
  font-size: 13px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.15s ease;
}

.carousel-logout:hover {
  color: var(--red-600);
  background: var(--red-50);
}

/* 移动端抽屉 */
.mobile-drawer-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 200;
  animation: fadeIn 0.2s ease;
}

.mobile-drawer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  border-radius: 20px 20px 0 0;
  padding: 20px;
  transform: translateY(0);
  animation: drawerSlideUp 0.3s ease-out;
  z-index: 201;
}

.mobile-drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 700;
  color: var(--gray-800);
}

.mobile-drawer-close {
  background: none;
  border: none;
  font-size: 18px;
  color: var(--gray-500);
  cursor: pointer;
  padding: 4px 8px;
}

.mobile-drawer-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mobile-drawer-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 12px;
  font-size: 15px;
  color: var(--gray-700);
  cursor: pointer;
  transition: all 0.15s ease;
}

.mobile-drawer-item:hover,
.mobile-drawer-item.active {
  background: var(--blue-50);
  color: #1565c0;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes drawerSlideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

/* 响应式 */
@media (max-width: 768px) {
  .carousel-logo-text {
    font-size: 16px;
  }
  .carousel-username {
    max-width: 50px;
  }
}

@media (max-width: 640px) {
  .carousel-nav-inner {
    padding: 0 14px;
    height: 56px;
  }
}

/* 减少动画 */
@media (prefers-reduced-motion: reduce) {
  .carousel-track {
    transition: none;
  }
  .mobile-drawer {
    animation: none;
  }
  .mobile-drawer-overlay {
    animation: none;
  }
}
</style>
