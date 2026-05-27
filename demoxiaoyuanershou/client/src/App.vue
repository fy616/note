<template>
  <div class="app">
    <header v-if="store.isLoggedIn" class="header">
      <div class="header-inner">
        <div class="header-left">
          <router-link to="/" class="logo">
            <span class="logo-icon">♻</span>
            <span class="logo-text">校园帮扔</span>
          </router-link>
          <nav v-if="store.isAdmin" class="admin-nav">
            <router-link to="/admin/review" class="nav-link">审核订单</router-link>
            <router-link to="/admin/users" class="nav-link">用户管理</router-link>
            <router-link to="/admin/categories" class="nav-link">分类管理</router-link>
          </nav>
        </div>
        <div class="header-right">
          <router-link to="/create" class="btn btn-primary btn-sm">
            <span>＋</span> 发布订单
          </router-link>
          <router-link to="/my-orders" class="btn btn-outline btn-sm">我的订单</router-link>
          <div class="user-menu">
            <span class="username">{{ store.username }}</span>
            <button @click="handleLogout" class="btn btn-ghost btn-sm">退出</button>
          </div>
        </div>
      </div>
    </header>

    <main class="main">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <div v-if="toast.visible" :class="['toast', `toast-${toast.type}`]">
      <span class="toast-icon">{{ toastIcon }}</span>
      <span class="toast-text">{{ toast.message }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from './stores/user'

const store = useUserStore()
const router = useRouter()

const toast = reactive({ visible: false, message: '', type: 'success' })
let toastTimer = null

function showToast(message, type = 'success') {
  clearTimeout(toastTimer)
  toast.message = message
  toast.type = type
  toast.visible = true
  toastTimer = setTimeout(() => { toast.visible = false }, 2500)
}

const toastIcon = {
  success: '✓',
  error: '✗',
  info: 'ℹ',
  warning: '⚠'
}[toast.type] || '✓'

function handleLogout() {
  store.logout()
  showToast('已退出登录', 'info')
  setTimeout(() => router.push('/login'), 300)
}

window.__toast = showToast
</script>

<style>
:root {
  --green-50: #E8F5E9;
  --green-100: #C8E6C9;
  --green-200: #A5D6A7;
  --green-400: #66BB6A;
  --green-500: #4CAF50;
  --green-600: #43A047;
  --green-700: #388E3C;
  --green-800: #2E7D32;
  --red-50: #FFEBEE;
  --red-500: #EF5350;
  --red-600: #E53935;
  --red-700: #D32F2F;
  --orange-50: #FFF3E0;
  --orange-500: #FF9800;
  --orange-600: #FB8C00;
  --blue-50: #E3F2FD;
  --blue-500: #42A5F5;
  --blue-600: #1E88E5;
  --gray-50: #FAFAFA;
  --gray-100: #F5F5F5;
  --gray-200: #EEEEEE;
  --gray-300: #E0E0E0;
  --gray-400: #BDBDBD;
  --gray-500: #9E9E9E;
  --gray-600: #757575;
  --gray-700: #616161;
  --gray-800: #424242;
  --gray-900: #212121;
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 20px;
  --shadow-xs: 0 1px 2px rgba(0,0,0,0.04);
  --shadow-sm: 0 2px 8px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);
  --shadow-md: 0 4px 16px rgba(0,0,0,0.08), 0 2px 4px rgba(0,0,0,0.04);
  --shadow-lg: 0 12px 40px rgba(0,0,0,0.12), 0 4px 12px rgba(0,0,0,0.06);
  --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  --transition-fast: all 0.15s cubic-bezier(0.4, 0, 0.2, 1);
}

* { margin: 0; padding: 0; box-sizing: border-box; }

body {
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: linear-gradient(160deg, #f0faf0 0%, #f8faf8 30%, #f5f5f5 100%);
  color: var(--gray-800);
  min-height: 100vh;
  -webkit-font-smoothing: antialiased;
}

.app { min-height: 100vh; }

.header {
  background: rgba(255,255,255,0.92);
  backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(0,0,0,0.06);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 12px rgba(0,0,0,0.04);
}
.header-inner {
  max-width: 960px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 28px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}
.logo-icon {
  font-size: 28px;
  line-height: 1;
}
.logo-text {
  font-size: 19px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--green-600), var(--green-800));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.5px;
}
.admin-nav {
  display: flex;
  gap: 4px;
  padding-left: 20px;
  border-left: 1.5px solid var(--gray-200);
}
.nav-link {
  font-size: 14px;
  color: var(--gray-600);
  text-decoration: none;
  padding: 7px 14px;
  border-radius: var(--radius-sm);
  transition: var(--transition-fast);
  position: relative;
}
.nav-link:hover {
  background: var(--green-50);
  color: var(--green-700);
}
.nav-link.router-link-active {
  background: var(--green-50);
  color: var(--green-700);
  font-weight: 600;
}
.nav-link.router-link-active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 50%;
  transform: translateX(-50%);
  width: 16px;
  height: 2px;
  background: var(--green-600);
  border-radius: 1px;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}
.user-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 5px 10px 5px 14px;
  background: var(--gray-50);
  border-radius: 24px;
  border: 1px solid var(--gray-100);
}
.username {
  font-size: 13px;
  color: var(--gray-700);
  font-weight: 500;
}

.main {
  max-width: 960px;
  margin: 0 auto;
  padding: 28px 24px 48px;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  text-decoration: none;
  transition: var(--transition);
  font-family: inherit;
  white-space: nowrap;
  position: relative;
  overflow: hidden;
}
.btn:active { transform: scale(0.97); }
.btn:disabled { opacity: 0.5; cursor: not-allowed; transform: none; }
.btn-primary {
  background: linear-gradient(135deg, var(--green-500), var(--green-600));
  color: #fff;
  box-shadow: 0 2px 12px rgba(76,175,80,0.35);
}
.btn-primary:hover { box-shadow: 0 6px 20px rgba(76,175,80,0.45); transform: translateY(-2px); }
.btn-outline {
  background: #fff;
  color: var(--green-600);
  border: 1.5px solid var(--green-300);
  box-shadow: var(--shadow-xs);
}
.btn-outline:hover { background: var(--green-50); border-color: var(--green-400); }
.btn-ghost {
  background: none;
  color: var(--gray-500);
  padding: 6px 10px;
  font-size: 13px;
}
.btn-ghost:hover { color: var(--red-600); background: var(--red-50); }
.btn-danger {
  background: linear-gradient(135deg, var(--red-500), var(--red-600));
  color: #fff;
  box-shadow: 0 2px 12px rgba(239,83,80,0.35);
}
.btn-danger:hover { box-shadow: 0 6px 20px rgba(239,83,80,0.45); transform: translateY(-2px); }
.btn-warning {
  background: linear-gradient(135deg, var(--orange-500), var(--orange-600));
  color: #fff;
  box-shadow: 0 2px 12px rgba(255,152,0,0.35);
}
.btn-warning:hover { box-shadow: 0 6px 20px rgba(255,152,0,0.45); transform: translateY(-2px); }
.btn-sm { padding: 7px 14px; font-size: 13px; }
.btn-block { width: 100%; justify-content: center; }

.card {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 22px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
  border: 1px solid rgba(0,0,0,0.04);
  animation: cardEnter 0.4s ease both;
}
.card:hover {
  box-shadow: var(--shadow-md);
  border-color: rgba(0,0,0,0.06);
}

.input, .textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1.5px solid var(--gray-200);
  border-radius: var(--radius-sm);
  font-size: 14px;
  outline: none;
  font-family: inherit;
  transition: var(--transition);
  background: #fff;
}
.input:hover, .textarea:hover {
  border-color: var(--gray-300);
}
.input:focus, .textarea:focus {
  border-color: var(--green-400);
  background: #fff;
  box-shadow: 0 0 0 4px rgba(76,175,80,0.1);
}
.textarea { min-height: 100px; resize: vertical; }

.form-group { margin-bottom: 20px; }
.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--gray-700);
}
.form-label .required { color: var(--red-500); margin-left: 2px; }

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--gray-500);
  animation: fadeIn 0.5s ease;
}
.empty-state .empty-icon { font-size: 56px; margin-bottom: 20px; opacity: 0.7; }
.empty-state .empty-title { font-size: 18px; font-weight: 700; margin-bottom: 10px; color: var(--gray-700); }
.empty-state .empty-desc { font-size: 14px; color: var(--gray-400); line-height: 1.6; }

.tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.3px;
}
.tag-pending-review { background: var(--orange-50); color: var(--orange-600); }
.tag-approved { background: var(--green-50); color: var(--green-700); }
.tag-rejected { background: var(--red-50); color: var(--red-600); }
.tag-pending { background: var(--blue-50); color: var(--blue-600); }
.tag-taken { background: var(--blue-50); color: var(--blue-600); }
.tag-completed { background: var(--green-50); color: var(--green-700); }

.tabs {
  display: flex;
  gap: 0;
  margin-bottom: 24px;
  background: #fff;
  border-radius: var(--radius-md);
  padding: 5px;
  box-shadow: var(--shadow-sm);
  border: 1px solid rgba(0,0,0,0.04);
}
.tab {
  flex: 1;
  padding: 11px 16px;
  border: none;
  background: transparent;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  color: var(--gray-500);
  border-radius: var(--radius-sm);
  transition: var(--transition);
  font-family: inherit;
}
.tab.active {
  background: linear-gradient(135deg, var(--green-500), var(--green-600));
  color: #fff;
  box-shadow: 0 4px 12px rgba(76,175,80,0.3);
  font-weight: 600;
}
.tab:hover:not(.active) {
  color: var(--gray-700);
  background: var(--gray-50);
}

.modal-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
  animation: fadeIn 0.2s ease;
}
.modal {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 32px;
  width: 90%;
  max-width: 460px;
  box-shadow: var(--shadow-lg);
  animation: modalEnter 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.modal h3 { font-size: 20px; margin-bottom: 24px; color: var(--gray-800); font-weight: 700; }
.modal-actions { display: flex; gap: 12px; justify-content: flex-end; margin-top: 24px; }

.toast {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 999;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 28px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 600;
  box-shadow: var(--shadow-lg);
  animation: toastEnter 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  pointer-events: none;
  backdrop-filter: blur(12px);
}
.toast-success { background: linear-gradient(135deg, var(--green-600), var(--green-700)); color: #fff; }
.toast-error { background: linear-gradient(135deg, var(--red-600), var(--red-700)); color: #fff; }
.toast-info { background: linear-gradient(135deg, var(--blue-500), var(--blue-600)); color: #fff; }
.toast-warning { background: linear-gradient(135deg, var(--orange-500), var(--orange-600)); color: #fff; }
.toast-icon { font-size: 18px; font-weight: 700; }

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
@keyframes cardEnter {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes modalEnter {
  from { opacity: 0; transform: scale(0.9) translateY(20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
@keyframes toastEnter {
  from { opacity: 0; transform: translateX(-50%) translateY(-20px) scale(0.9); }
  to { opacity: 1; transform: translateX(-50%) translateY(0) scale(1); }
}
.page-enter-active, .page-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}
.page-enter-from { opacity: 0; transform: translateY(12px); }
.page-leave-to { opacity: 0; transform: translateY(-8px); }

@media (max-width: 640px) {
  .header-inner { padding: 0 14px; height: 56px; }
  .header-right .btn-sm { padding: 6px 10px; font-size: 12px; }
  .main { padding: 16px 14px 32px; }
  .card { padding: 16px; }
  .admin-nav { display: none; }
  .username { max-width: 60px; overflow: hidden; text-overflow: ellipsis; }
  .logo-text { font-size: 16px; }
  .tabs { padding: 4px; }
  .tab { padding: 9px 12px; font-size: 13px; }
}
</style>
