<template>
  <div class="admin-review">
    <div class="page-header">
      <h2 class="page-title">订单审核</h2>
      <p class="page-desc">审核用户提交的代扔订单，通过后即可上线</p>
    </div>

    <div class="tabs">
      <button :class="['tab', { active: currentTab === 'pending' }]" @click="currentTab = 'pending'">
        待审核 <span v-if="pendingCount > 0" class="badge">{{ pendingCount }}</span>
      </button>
      <button :class="['tab', { active: currentTab === 'rejected' }]" @click="currentTab = 'rejected'">已驳回</button>
    </div>

    <div v-if="loading" class="empty-state">
      <div class="loading-skeleton">
        <div v-for="n in 2" :key="n" class="skeleton-card"></div>
      </div>
    </div>
    <div v-else-if="filteredOrders.length === 0" class="empty-state">
      <div class="empty-icon">✅</div>
      <div class="empty-title">{{ currentTab === 'pending' ? '没有待审核的订单' : '没有已驳回的订单' }}</div>
      <div class="empty-desc">{{ currentTab === 'pending' ? '所有订单都已被处理' : '还没有驳回任何订单' }}</div>
    </div>
    <div v-else class="order-list">
      <div v-for="order in filteredOrders" :key="order.id" class="card order-card">
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
          <span v-if="order.contact" class="order-tag">📞 {{ order.contact }}</span>
        </div>

        <div v-if="order.reviewMessage" class="review-msg">
          <span class="review-label">驳回原因</span>
          {{ order.reviewMessage }}
        </div>

        <div class="order-bottom">
          <span class="order-time">{{ formatTime(order.createdAt) }}</span>
          <div v-if="order.status === 'pending_review'" class="action-btns">
            <button class="btn btn-outline btn-sm" @click="showReject(order)">驳回</button>
            <button class="btn btn-primary btn-sm" @click="handleApprove(order.id)">通过</button>
          </div>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="rejectTarget" class="modal-overlay" @click.self="rejectTarget = null">
        <div class="modal">
          <h3>驳回订单</h3>
          <p style="font-size:14px;color:var(--gray-500);margin-bottom:16px;">
            请填写驳回原因，发布者将根据原因修改后重新提交
          </p>
          <div class="form-group">
            <label class="form-label">驳回原因</label>
            <textarea v-model="rejectReason" class="textarea" placeholder="如：请补充垃圾的具体数量、描述不够清晰等" rows="3"></textarea>
          </div>
          <div class="modal-actions">
            <button class="btn btn-outline" @click="rejectTarget = null">取消</button>
            <button class="btn btn-danger" :disabled="!rejectReason.trim()" @click="handleReject">
              确认驳回
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getReviewOrders, approveOrder, rejectOrder } from '../api'

const orders = ref([])
const loading = ref(true)
const currentTab = ref('pending')

const pendingCount = computed(() => orders.value.filter(o => o.status === 'pending_review').length)
const filteredOrders = computed(() => {
  if (currentTab.value === 'pending') {
    return orders.value.filter(o => o.status === 'pending_review')
  }
  return orders.value.filter(o => o.status === 'rejected')
})

const rejectTarget = ref(null)
const rejectReason = ref('')

function showReject(order) {
  rejectTarget.value = order
  rejectReason.value = ''
}

async function loadOrders() {
  loading.value = true
  try {
    const res = await getReviewOrders()
    orders.value = res.orders
  } catch {
    orders.value = []
  } finally {
    loading.value = false
  }
}

function statusClass(status) {
  return { pending_review: 'tag-pending-review', rejected: 'tag-rejected' }[status] || ''
}

function statusText(status) {
  return { pending_review: '待审核', rejected: '已驳回' }[status] || status
}

function formatTime(iso) {
  const d = new Date(iso)
  const now = new Date()
  const diff = now - d
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

async function handleApprove(id) {
  try {
    await approveOrder(id)
    window.__toast?.('订单已通过审核', 'success')
    await loadOrders()
  } catch (e) {
    window.__toast?.(e.message || '操作失败', 'error')
  }
}

async function handleReject() {
  try {
    await rejectOrder(rejectTarget.value.id, rejectReason.value)
    rejectTarget.value = null
    window.__toast?.('已驳回订单', 'info')
    await loadOrders()
  } catch (e) {
    window.__toast?.(e.message || '操作失败', 'error')
  }
}

onMounted(loadOrders)
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

.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 22px; height: 22px;
  background: linear-gradient(135deg, var(--red-500), var(--red-600));
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  border-radius: 11px;
  padding: 0 7px;
  margin-left: 8px;
  box-shadow: 0 2px 8px rgba(239,83,80,0.3);
  animation: pulse 2s ease infinite;
}
@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
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

.review-msg {
  background: linear-gradient(135deg, var(--orange-50), #fff8f0);
  padding: 12px 16px;
  border-radius: 10px;
  font-size: 13px;
  color: var(--orange-600);
  margin-bottom: 14px;
  line-height: 1.6;
  border: 1px solid rgba(255,152,0,0.15);
}
.review-label {
  font-weight: 700;
  margin-right: 4px;
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
}

.loading-skeleton {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.skeleton-card {
  height: 160px;
  background: linear-gradient(110deg, var(--gray-100) 30%, var(--gray-50) 50%, var(--gray-100) 70%);
  background-size: 250% 100%;
  animation: shimmer 2s infinite;
  border-radius: var(--radius-md);
  border: 1px solid var(--gray-100);
}
@keyframes shimmer { 0% { background-position: 200% 0; } 100% { background-position: -200% 0; } }
</style>
