<template>
  <div class="admin-users">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <p class="page-desc">管理所有注册用户，可冻结或删除账号</p>
    </div>

    <div v-if="loading" class="empty-state">
      <div class="loading-skeleton">
        <div v-for="n in 3" :key="n" class="skeleton-card"></div>
      </div>
    </div>
    <div v-else class="user-list">
      <div v-for="user in users" :key="user.id" class="card user-card">
        <div class="user-avatar-wrapper">
          <span :class="['user-avatar', { 'is-admin': user.role === 'admin', 'is-frozen': user.isFrozen }]">
            {{ user.username[0] }}
          </span>
        </div>
        <div class="user-info">
          <div class="user-name-row">
            <span class="user-name">{{ user.username }}</span>
            <span v-if="user.role === 'admin'" class="tag tag-admin">管理员</span>
            <span v-if="user.isFrozen" class="tag tag-frozen">已冻结</span>
          </div>
          <div class="user-meta">
            <span>📱 {{ user.phone }}</span>
            <span>🏠 {{ user.dormitory }}</span>
            <span>⭐ {{ user.points }} 分</span>
            <span class="meta-time">注册于 {{ formatTime(user.createdAt) }}</span>
          </div>
        </div>
        <div v-if="user.role !== 'admin'" class="user-actions">
          <button v-if="!user.isFrozen" class="btn btn-outline btn-sm" @click="handleFreeze(user.id)"
                  title="冻结后用户无法登录和发布订单">
            🔒 冻结
          </button>
          <button v-else class="btn btn-outline btn-sm" style="color:var(--green-600);border-color:var(--green-400);" @click="handleUnfreeze(user.id)">
            🔓 解冻
          </button>
          <button class="btn btn-danger btn-sm" @click="handleDelete(user.id)">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminUsers, freezeUser, unfreezeUser, deleteUser } from '../api'

const users = ref([])
const loading = ref(true)

async function loadUsers() {
  loading.value = true
  try {
    const res = await getAdminUsers()
    users.value = res.users
  } catch {
    users.value = []
  } finally {
    loading.value = false
  }
}

function formatTime(iso) {
  const d = new Date(iso)
  const now = new Date()
  const diff = now - d
  if (diff < 86400000) return '今天'
  if (diff < 172800000) return '昨天'
  return `${d.getFullYear()}/${d.getMonth() + 1}/${d.getDate()}`
}

async function handleFreeze(id) {
  if (!confirm('确定冻结该用户？冻结后用户无法登录和发布订单')) return
  try {
    await freezeUser(id)
    window.__toast?.('用户已冻结', 'info')
    await loadUsers()
  } catch (e) {
    window.__toast?.(e.message || '操作失败', 'error')
  }
}

async function handleUnfreeze(id) {
  try {
    await unfreezeUser(id)
    window.__toast?.('用户已解冻', 'success')
    await loadUsers()
  } catch (e) {
    window.__toast?.(e.message || '操作失败', 'error')
  }
}

async function handleDelete(id) {
  if (!confirm('确定删除该用户？将同时删除其所有订单，操作不可恢复！')) return
  try {
    await deleteUser(id)
    window.__toast?.('用户已删除', 'info')
    await loadUsers()
  } catch (e) {
    window.__toast?.(e.message || '操作失败', 'error')
  }
}

onMounted(loadUsers)
</script>

<style scoped>
.page-header {
  margin-bottom: 28px;
}
.page-title {
  font-size: 24px;
  font-weight: 800;
  color: var(--gray-800);
  margin-bottom: 8px;
}
.page-desc {
  font-size: 14px;
  color: var(--gray-500);
  line-height: 1.6;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 18px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.user-card:hover {
  transform: translateX(6px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1), 0 4px 8px rgba(0,0,0,0.06);
}

.user-avatar-wrapper { flex-shrink: 0; }
.user-avatar {
  width: 48px; height: 48px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px; font-weight: 700; color: #fff;
  background: linear-gradient(135deg, var(--green-400), var(--green-600));
  box-shadow: 0 4px 12px rgba(76,175,80,0.25);
  transition: var(--transition);
}
.user-avatar.is-admin {
  background: linear-gradient(135deg, var(--blue-500), var(--blue-600));
  box-shadow: 0 4px 12px rgba(66,165,245,0.25);
}
.user-avatar.is-frozen {
  background: linear-gradient(135deg, var(--gray-400), var(--gray-500));
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.user-card:hover .user-avatar { transform: scale(1.08); }

.user-info { flex: 1; min-width: 0; }
.user-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}
.user-name {
  font-weight: 700;
  font-size: 16px;
  color: var(--gray-800);
}
.user-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--gray-500);
  flex-wrap: wrap;
}
.meta-time { color: var(--gray-400); }

.user-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.tag-admin {
  background: linear-gradient(135deg, var(--blue-50), #e3f2fd);
  color: var(--blue-600);
  border: 1px solid rgba(66,165,245,0.2);
}
.tag-frozen {
  background: linear-gradient(135deg, var(--gray-100), var(--gray-200));
  color: var(--gray-600);
  border: 1px solid var(--gray-200);
}

.loading-skeleton {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.skeleton-card {
  height: 80px;
  background: linear-gradient(110deg, var(--gray-100) 30%, var(--gray-50) 50%, var(--gray-100) 70%);
  background-size: 250% 100%;
  animation: shimmer 2s infinite;
  border-radius: var(--radius-md);
  border: 1px solid var(--gray-100);
}
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }

@media (max-width: 640px) {
  .user-card { flex-wrap: wrap; }
  .user-actions {
    width: 100%;
    justify-content: flex-end;
    padding-top: 12px;
    border-top: 1px solid var(--gray-100);
  }
  .user-meta { gap: 10px; }
  .user-avatar { width: 40px; height: 40px; font-size: 17px; }
}
</style>
