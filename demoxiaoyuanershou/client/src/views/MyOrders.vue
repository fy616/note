<template>
  <div class="my-orders-page">
    <div class="page-header">
      <h2 class="page-title">我的订单</h2>
    </div>

    <div class="tabs">
      <button :class="['tab', { active: currentTab === 'published' }]" @click="currentTab = 'published'">我发布的</button>
      <button :class="['tab', { active: currentTab === 'taken' }]" @click="currentTab = 'taken'">我接单的</button>
    </div>

    <div v-if="loading" class="empty-state">
      <div class="loading-skeleton">
        <div v-for="n in 2" :key="n" class="skeleton-card"></div>
      </div>
    </div>
    <div v-else-if="orders.length === 0" class="empty-state">
      <div class="empty-icon">📋</div>
      <div class="empty-title">{{ currentTab === 'published' ? '还没有发布过订单' : '还没有接过订单' }}</div>
      <div class="empty-desc">{{ currentTab === 'published' ? '去发布你的第一个代扔订单吧' : '去订单大厅看看有没有可以接的订单' }}</div>
      <router-link v-if="currentTab === 'published'" to="/create" class="btn btn-primary" style="margin-top: 20px;">发布订单</router-link>
      <router-link v-else to="/" class="btn btn-primary" style="margin-top: 20px;">去看看</router-link>
    </div>
    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.id"
           :class="['card order-card', { 'is-rejected': order.status === 'rejected', 'is-pending-review': order.status === 'pending_review' }]">
        <div class="order-top">
          <div class="order-user-info">
            <span class="order-avatar">{{ order.username[0] }}</span>
            <span class="order-username">{{ order.username }}</span>
          </div>
          <span :class="['tag', statusClass(order.status)]">{{ statusText(order.status) }}</span>
        </div>

        <p class="order-desc">{{ order.description }}</p>

        <div class="order-tags">
          <span class="order-tag">📍 {{ order.location }}</span>
          <span v-if="order.reward" class="order-tag reward">🎁 {{ order.reward }} 积分</span>
          <span v-if="order.takerName" class="order-tag">🤝 {{ order.takerName }}</span>
          <span v-if="order.contact" class="order-tag">📞 {{ order.contact }}</span>
        </div>

        <div v-if="order.reviewMessage" class="review-banner">
          <div class="review-banner-icon">💡</div>
          <div class="review-banner-text">
            <strong>驳回原因：</strong>{{ order.reviewMessage }}
          </div>
        </div>

        <div class="order-bottom">
          <span class="order-time">{{ formatTime(order.createdAt) }}</span>
          <div class="action-btns">
            <button v-if="order.status === 'rejected' && order.userId === store.userId"
                    class="btn btn-warning btn-sm" @click="editOrder(order)">
              修改并重新提交
            </button>
            <button v-if="order.status === 'pending_review' && order.userId === store.userId"
                    class="btn btn-ghost btn-sm" disabled>⏳ 等待审核</button>
            <button v-if="order.status === 'approved' && order.userId === store.userId && order.status !== 'taken'"
                    class="btn btn-link btn-sm" @click="handleDelete(order.id)">删除</button>
            <button v-if="order.status === 'taken' && order.takerId === store.userId"
                    class="btn btn-outline btn-sm" @click="handleComplete(order.id)">完成</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="editingOrder" class="modal-overlay" @click.self="editingOrder = null">
      <div class="modal">
        <h3>修改订单</h3>
        <p style="font-size:14px;color:var(--gray-500);margin-bottom:20px;">根据驳回原因修改信息后重新提交审核</p>
        <form @submit.prevent="handleResubmit">
          <div class="form-group">
            <label class="form-label">垃圾描述</label>
            <textarea v-model="editForm.description" class="textarea" required></textarea>
          </div>
          <div class="form-group">
            <label class="form-label">位置</label>
            <input v-model="editForm.location" class="input" required />
          </div>
          <div class="form-row">
            <div class="form-group" style="flex:1">
              <label class="form-label">悬赏积分</label>
              <input v-model.number="editForm.reward" type="number" min="0" class="input" />
            </div>
            <div class="form-group" style="flex:1">
              <label class="form-label">联系方式</label>
              <input v-model="editForm.contact" class="input" />
            </div>
          </div>
          <transition name="fade">
            <p v-if="editError" class="error-msg">{{ editError }}</p>
          </transition>
          <div class="modal-actions">
            <button type="button" class="btn btn-outline" @click="editingOrder = null">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="editLoading">
              <span v-if="editLoading" class="spinner"></span>
              <span v-else>重新提交</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useUserStore } from '../stores/user'
import { getOrders, deleteOrder, completeOrder, resubmitOrder } from '../api'

const store = useUserStore()
const currentTab = ref('published')
const orders = ref([])
const loading = ref(true)

const editingOrder = ref(null)
const editForm = reactive({ description: '', location: '', reward: 0, contact: '' })
const editError = ref('')
const editLoading = ref(false)

async function loadOrders() {
  loading.value = true
  try {
    const res = await getOrders()
    const all = res.orders
    if (currentTab.value === 'published') {
      orders.value = all.filter(o => o.userId === store.userId)
    } else {
      orders.value = all.filter(o => o.takerId === store.userId)
    }
  } catch {
    orders.value = []
  } finally {
    loading.value = false
  }
}

function editOrder(order) {
  editingOrder.value = order
  editForm.description = order.description
  editForm.location = order.location
  editForm.reward = order.reward || 0
  editForm.contact = order.contact || ''
  editError.value = ''
}

async function handleResubmit() {
  editError.value = ''
  editLoading.value = true
  try {
    await resubmitOrder(editingOrder.value.id, { ...editForm })
    editingOrder.value = null
    window.__toast?.('已重新提交，等待管理员审核', 'success')
    await loadOrders()
  } catch (e) {
    editError.value = e.message || '提交失败'
  } finally {
    editLoading.value = false
  }
}

function statusClass(status) {
  const map = { pending_review: 'tag-pending-review', approved: 'tag-approved', rejected: 'tag-rejected', pending: 'tag-pending', taken: 'tag-taken', completed: 'tag-completed' }
  return map[status] || ''
}

function statusText(status) {
  const map = { pending_review: '审核中', approved: '已通过', rejected: '未通过', pending: '待接单', taken: '进行中', completed: '已完成' }
  return map[status] || status
}

function formatTime(iso) {
  const d = new Date(iso)
  const now = new Date()
  const diff = now - d
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

async function handleDelete(id) {
  if (!confirm('确定删除此订单？')) return
  try {
    await deleteOrder(id)
    window.__toast?.('订单已删除', 'info')
    await loadOrders()
  } catch (e) {
    window.__toast?.(e.message || '删除失败', 'error')
  }
}

async function handleComplete(id) {
  try {
    await completeOrder(id)
    window.__toast?.('订单已完成，积分已到账 🎉', 'success')
    await loadOrders()
  } catch (e) {
    window.__toast?.(e.message || '操作失败', 'error')
  }
}

watch(currentTab, loadOrders, { immediate: true })
</script>

<style scoped>
.page-header {
  margin-bottom: 28px;
}
.page-title {
  font-size: 24px;
  font-weight: 800;
  color: var(--gray-800);
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-card {
  border-left: 4px solid transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.order-card.is-rejected {
  border-left-color: var(--red-500);
  background: linear-gradient(135deg, #fff 98%, var(--red-50) 100%);
}
.order-card.is-pending-review {
  border-left-color: var(--orange-500);
  background: linear-gradient(135deg, #fff 98%, var(--orange-50) 100%);
}
.order-card:hover {
  transform: translateX(6px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1), 0 4px 8px rgba(0,0,0,0.06);
}

.order-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}
.order-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.order-avatar {
  width: 36px; height: 36px; border-radius: 50%;
  background: linear-gradient(135deg, var(--green-400), var(--green-600));
  color: #fff; display: flex; align-items: center; justify-content: center;
  font-size: 15px; font-weight: 700;
  box-shadow: 0 2px 8px rgba(76,175,80,0.25);
}
.order-username {
  font-weight: 600;
  font-size: 15px;
  color: var(--gray-800);
}

.order-desc {
  font-size: 15px;
  color: var(--gray-700);
  margin-bottom: 14px;
  line-height: 1.7;
  word-break: break-word;
}

.order-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}
.order-tag {
  font-size: 13px;
  color: var(--gray-600);
  background: var(--gray-50);
  padding: 5px 12px;
  border-radius: 8px;
  border: 1px solid var(--gray-100);
  transition: var(--transition-fast);
}
.order-tag:hover { background: var(--gray-100); }
.order-tag.reward {
  background: var(--orange-50);
  color: var(--orange-600);
  border-color: rgba(255,152,0,0.15);
}

.review-banner {
  display: flex;
  gap: 10px;
  background: linear-gradient(135deg, var(--orange-50), #fff8f0);
  padding: 12px 16px;
  border-radius: 10px;
  margin-bottom: 14px;
  border: 1px solid rgba(255,152,0,0.15);
}
.review-banner-icon { font-size: 18px; flex-shrink: 0; }
.review-banner-text {
  font-size: 13px;
  color: var(--orange-600);
  line-height: 1.6;
}
.review-banner-text strong {
  display: block;
  margin-bottom: 2px;
}

.order-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 14px;
  border-top: 1px solid var(--gray-100);
}
.order-time {
  font-size: 12px;
  color: var(--gray-400);
  font-weight: 500;
}
.action-btns {
  display: flex;
  gap: 8px;
  align-items: center;
}

.form-row { display: flex; gap: 16px; }
.btn-link {
  background: none;
  border: none;
  color: var(--red-500);
  cursor: pointer;
  padding: 7px 12px;
  font-size: 13px;
  font-family: inherit;
  font-weight: 500;
  border-radius: 8px;
  transition: var(--transition-fast);
}
.btn-link:hover {
  background: var(--red-50);
  text-decoration: underline;
}
.btn-ghost:disabled { opacity: 0.5; cursor: default; }

.error-msg {
  color: var(--red-600);
  font-size: 13px;
  margin-bottom: 14px;
  padding: 10px 14px;
  background: var(--red-50);
  border-radius: 10px;
  border: 1px solid rgba(239,83,80,0.15);
  font-weight: 500;
}
.spinner {
  width: 20px; height: 20px;
  border: 2.5px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.loading-skeleton {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.skeleton-card {
  height: 140px;
  background: linear-gradient(110deg, var(--gray-100) 30%, var(--gray-50) 50%, var(--gray-100) 70%);
  background-size: 250% 100%;
  animation: shimmer 2s infinite;
  border-radius: var(--radius-md);
  border: 1px solid var(--gray-100);
}
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

@media (max-width: 640px) {
  .form-row { flex-direction: column; gap: 0; }
  .page-title { font-size: 20px; }
  .order-card:hover { transform: none; }
  .order-avatar { width: 32px; height: 32px; font-size: 13px; }
}
</style>
