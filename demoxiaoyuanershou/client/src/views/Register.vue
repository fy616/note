<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-brand">
        <span class="auth-logo">♻</span>
        <h1 class="auth-title">校园帮扔</h1>
        <p class="auth-subtitle">注册新账号，开始帮扔服务</p>
      </div>
      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input v-model="form.username" class="input" placeholder="取一个用户名" required />
          </div>
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input v-model="form.password" type="password" class="input" placeholder="至少6位密码" required />
          </div>
        </div>
        <div class="form-group">
          <label class="form-label">手机号</label>
          <div class="input-wrapper">
            <span class="input-icon">📱</span>
            <input v-model="form.phone" class="input" placeholder="请输入手机号" required />
          </div>
        </div>
        <div class="form-group">
          <label class="form-label">宿舍楼 / 地址</label>
          <div class="input-wrapper">
            <span class="input-icon">🏠</span>
            <input v-model="form.dormitory" class="input" placeholder="如：学宿3号楼" required />
          </div>
        </div>
        <transition name="fade">
          <p v-if="error" class="error-msg">{{ error }}</p>
        </transition>
        <button type="submit" class="btn btn-primary btn-block btn-register" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span v-else>注册</span>
        </button>
      </form>
      <div class="auth-footer">
        已有账号？<router-link to="/login" class="auth-link">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const store = useUserStore()
const form = reactive({ username: '', password: '', phone: '', dormitory: '' })
const error = ref('')
const loading = ref(false)

async function handleRegister() {
  error.value = ''
  loading.value = true
  try {
    await store.register({ ...form })
    window.__toast?.('注册成功', 'success')
    router.push('/home')
  } catch (e) {
    error.value = e.message || '注册失败'
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
  margin-bottom: 32px;
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
.auth-form .input-wrapper { position: relative; }
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
.btn-register {
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
