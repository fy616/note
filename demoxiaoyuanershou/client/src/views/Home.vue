<template>
  <div class="home">
    <div class="tabs">
      <button :class="['tab', { active: currentTab === 'all' }]" @click="currentTab = 'all'">全部</button>
      <button :class="['tab', { active: currentTab === 'approved' }]" @click="currentTab = 'approved'">待接单</button>
      <button :class="['tab', { active: currentTab === 'taken' }]" @click="currentTab = 'taken'">进行中</button>
    </div>

    <!-- 分类筛选栏 -->
    <div class="category-filter">
      <button
        :class="['category-btn', { active: currentCategory === 'all' }]"
        @click="currentCategory = 'all'"
      >
        全部
      </button>
      <button
        v-for="cat in categories"
        :key="cat.id"
        :class="['category-btn', { active: currentCategory === cat.name }]"
        @click="currentCategory = cat.name"
      >
        {{ cat.icon }} {{ cat.name }}
      </button>
    </div>

    <div v-if="loading" class="empty-state">
      <div class="loading-skeleton">
        <div v-for="n in 3" :key="n" class="skeleton-card"></div>
      </div>
    </div>
    <div v-else-if="orders.length === 0" class="empty-state">
      <div class="empty-icon">📦</div>
      <div class="empty-title">暂无订单</div>
      <div class="empty-desc">还没有人发布代扔订单，来发布第一个吧</div>
      <router-link to="/create" class="btn btn-primary" style="margin-top: 20px;">发布订单</router-link>
    </div>
    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.id" class="card order-card">
        <div class="order-top">
          <div class="order-user-info">
            <span class="order-avatar">{{ order.username[0] }}</span>
            <span class="order-username">{{ order.username }}</span>
          </div>
          <span :class="['tag', statusClass(order.status)]">{{ statusText(order.status) }}</span>
        </div>

        <p class="order-desc">{{ order.description }}</p>

        <div class="order-tags">
          <span v-if="order.category" class="order-tag category">🏷️ {{ order.category }}</span>
          <span class="order-tag">📍 {{ order.location }}</span>
          <span v-if="order.reward" class="order-tag reward">🎁 {{ order.reward }} 积分</span>
          <span v-if="order.contact" class="order-tag">📞 {{ order.contact }}</span>
          <span v-if="order.deadline" class="order-tag deadline">⏰ {{ formatDeadline(order.deadline) }}</span>
        </div>

        <div v-if="order.reviewMessage" class="review-banner">
          <span>驳回：{{ order.reviewMessage }}</span>
        </div>

        <div class="order-bottom">
          <span class="order-time">{{ formatTime(order.createdAt) }}</span>
          <div class="order-actions">
            <button v-if="order.status === 'approved' && order.userId !== store.userId"
                    class="btn btn-primary btn-sm" @click="handleTake(order.id)">
              接单
            </button>
            <button v-if="order.status === 'taken' && order.takerId === store.userId"
                    class="btn btn-outline btn-sm" @click="handleComplete(order.id)">
              完成
            </button>
            <button v-if="order.status === 'taken' && (order.userId === store.userId || order.takerId === store.userId)"
                    class="btn btn-outline btn-sm chat-btn" @click="chatOrderId = order.id">
              💬 聊天
            </button>
            <span v-if="order.status === 'taken' && order.takerId !== store.userId"
                  class="status-hint">🤝 {{ order.takerName }} 已接单</span>
            <span v-if="order.status === 'completed'" class="status-hint done">✅ 已完成</span>
            <span v-if="order.status === 'pending_review' && order.userId === store.userId"
                  class="status-hint">⏳ 审核中</span>
            <span v-if="order.status === 'rejected' && order.userId === store.userId"
                  class="status-hint reject">❌ 未通过</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 聊天弹窗 -->
    <ChatModal
      v-if="chatOrderId"
      :order-id="chatOrderId"
      @close="chatOrderId = null"
    />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getOrders, takeOrder, completeOrder, getCategories } from '../api'
import ChatModal from '../components/ChatModal.vue'

const store = useUserStore()
const currentTab = ref('all')
const currentCategory = ref('all')
const categories = ref([])
const orders = ref([])
const loading = ref(true)
const chatOrderId = ref(null)

async function loadCategories() {
  try {
    const res = await getCategories()
    categories.value = res
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

async function loadOrders() {
  loading.value = true
  try {
    const params = {}
    if (currentTab.value !== 'all') params.status = currentTab.value
    if (currentCategory.value !== 'all') params.category = currentCategory.value
    const res = await getOrders(params)
    orders.value = res.orders
  } catch {
    orders.value = []
  } finally {
    loading.value = false
  }
}

onMounted(loadCategories)

function statusClass(status) {
  const map = { pending_review: 'tag-pending-review', approved: 'tag-approved', rejected: 'tag-rejected', pending: 'tag-pending', taken: 'tag-taken', completed: 'tag-completed' }
  return map[status] || ''
}

function statusText(status) {
  const map = { pending_review: '审核中', approved: '待接单', rejected: '未通过', pending: '待接单', taken: '进行中', completed: '已完成' }
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

function formatDeadline(deadline) {
  const d = new Date(deadline)
  const now = new Date()
  const diff = d - now

  if (diff < 0) return '已过期'
  if (diff < 3600000) return `剩余 ${Math.floor(diff / 60000)} 分钟`
  if (diff < 86400000) return `剩余 ${Math.floor(diff / 3600000)} 小时`
  return `截止 ${d.getMonth() + 1}/${d.getDate()} ${d.getHours()}:${d.getMinutes().toString().padStart(2, '0')}`
}

async function handleTake(id) {
  try {
    await takeOrder(id)
    window.__toast?.('接单成功！完成订单后可获得积分', 'success')
    await loadOrders()
  } catch (e) {
    window.__toast?.(e.message || '操作失败', 'error')
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

watch(currentTab, loadOrders)
watch(currentCategory, loadOrders)
watch([currentTab, currentCategory], loadOrders, { immediate: true })
</script>

<style scoped>
.order-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-card {
  border-left: 4px solid transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: default;
}
.order-card:hover {
  border-left-color: var(--green-400);
  transform: translateX(6px);
  box-shadow: 0 8px 24px rgba(76,175,80,0.12), 0 4px 8px rgba(0,0,0,0.06);
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
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--green-400), var(--green-600));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(76,175,80,0.25);
}
.order-username { font-weight: 600; font-size: 15px; color: var(--gray-800); }

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
.order-tag.category {
  background: var(--blue-50);
  color: var(--blue-600);
  border-color: rgba(66,165,245,0.15);
}
.order-tag.deadline {
  background: var(--purple-50, #f3e5f5);
  color: var(--purple-600, #7b1fa2);
  border-color: rgba(123,31,162,0.15);
}
.chat-btn {
  margin-left: 4px;
}

.review-banner {
  background: linear-gradient(135deg, var(--red-50), #fff5f5);
  padding: 10px 14px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--red-600);
  margin-bottom: 14px;
  border: 1px solid rgba(239,83,80,0.15);
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
.order-actions { display: flex; gap: 8px; align-items: center; }
.status-hint {
  font-size: 13px;
  color: var(--gray-500);
  font-weight: 500;
}
.status-hint.done { color: var(--green-600); }
.status-hint.reject { color: var(--red-500); }

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
@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.category-filter {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 8px;
  margin-bottom: 16px;
}

.category-btn {
  flex-shrink: 0;
  padding: 8px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  background: white;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.category-btn:hover {
  border-color: #4caf50;
  color: #4caf50;
}

.category-btn.active {
  background: #4caf50;
  color: white;
  border-color: #4caf50;
}

@media (max-width: 640px) {
  .order-card:hover { transform: none; }
  .order-tags { gap: 6px; }
  .order-tag { font-size: 12px; padding: 4px 8px; }
  .order-avatar { width: 32px; height: 32px; font-size: 13px; }
}
</style>
