<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-brand">
        <span class="auth-logo">♻</span>
        <h1 class="auth-title">校园帮扔</h1>
        <p class="auth-subtitle">登录你的账号，开始代扔服务</p>
      </div>
      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input v-model="username" class="input" placeholder="请输入用户名" required />
          </div>
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input v-model="password" type="password" class="input" placeholder="请输入密码" required />
          </div>
        </div>
        <transition name="fade">
          <p v-if="error" class="error-msg">{{ error }}</p>
        </transition>
        <button type="submit" class="btn btn-primary btn-block btn-login" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span v-else>登录</span>
        </button>
      </form>
      <div class="auth-footer">
        还没有账号？<router-link to="/register" class="auth-link">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const store = useUserStore()
const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    await store.login(username.value, password.value)
    window.__toast?.('登录成功', 'success')
    router.push('/')
  } catch (e) {
    error.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(160deg, #e8f5e9 0%, #f1f8e9 40%, #f5f5f5 100%);
}
.auth-card {
  background: #fff;
  border-radius: 24px;
  padding: 44px 40px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 16px 60px rgba(0,0,0,0.1), 0 4px 16px rgba(0,0,0,0.04);
  border: 1px solid rgba(255,255,255,0.8);
  animation: cardEnter 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}
@keyframes cardEnter {
  from { opacity: 0; transform: scale(0.92) translateY(20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
.auth-brand {
  text-align: center;
  margin-bottom: 36px;
}
.auth-logo {
  font-size: 56px;
  display: block;
  margin-bottom: 16px;
  animation: bounce 2s ease infinite;
}
@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}
.auth-title {
  font-size: 28px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--green-600), var(--green-800));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
}
.auth-subtitle {
  font-size: 14px;
  color: var(--gray-500);
  line-height: 1.5;
}
.auth-form .input-wrapper {
  position: relative;
}
.auth-form .input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  z-index: 1;
}
.auth-form .input {
  padding-left: 44px;
  height: 48px;
  border-radius: 10px;
}
.error-msg {
  color: var(--red-600);
  font-size: 13px;
  margin-bottom: 16px;
  padding: 10px 14px;
  background: var(--red-50);
  border-radius: 10px;
  border: 1px solid rgba(239,83,80,0.15);
  font-weight: 500;
}
.btn-login {
  padding: 14px;
  font-size: 15px;
  margin-top: 8px;
  border-radius: 10px;
  font-weight: 600;
  letter-spacing: 0.5px;
}
.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--gray-500);
}
.auth-link {
  color: var(--green-600);
  text-decoration: none;
  font-weight: 600;
  transition: var(--transition-fast);
}
.auth-link:hover {
  color: var(--green-700);
  text-decoration: underline;
}
.spinner {
  width: 20px; height: 20px;
  border: 2.5px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
