<template>
  <div class="create-page">
    <div class="page-header">
      <h2 class="page-title">发布代扔订单</h2>
      <p class="page-desc">发布后需要管理员审核通过，其他同学才能接单</p>
    </div>
    <div class="card create-card">
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label class="form-label">任务类型 <span class="required">*</span></label>
          <div class="category-grid">
            <div
              v-for="cat in categories"
              :key="cat.id"
              :class="['category-item', { active: selectedCategory === cat.name }]"
              @click="selectedCategory = cat.name"
            >
              <span class="category-icon">{{ cat.icon }}</span>
              <span class="category-name">{{ cat.name }}</span>
              <span v-if="!cat.needReview" class="category-badge">免审核</span>
            </div>
          </div>
        </div>
        <div class="form-group">
          <label class="form-label">任务描述 <span class="required">*</span></label>
          <textarea v-model="form.description" class="textarea" placeholder="描述你的任务需求，如：帮我拿一下快递、代扔一袋垃圾……" required></textarea>
          <span class="hint">{{ form.description.length }} / 200</span>
        </div>
        <div class="form-group">
          <label class="form-label">位置 <span class="required">*</span></label>
          <input v-model="form.location" class="input" placeholder="如：学宿3号楼 412室" required />
        </div>
        <div class="form-row">
          <div class="form-group" style="flex:1">
            <label class="form-label">悬赏积分</label>
            <input v-model.number="form.reward" type="number" min="0" class="input" placeholder="默认1积分" />
          </div>
          <div class="form-group" style="flex:1">
            <label class="form-label">联系方式</label>
            <input v-model="form.contact" class="input" placeholder="选填，方便联系" />
          </div>
        </div>
        <div class="form-group">
          <label class="form-label">截止时间（可选）</label>
          <input v-model="form.deadline" type="datetime-local" class="input" />
        </div>
        <transition name="fade">
          <p v-if="error" class="error-msg">{{ error }}</p>
        </transition>
        <button type="submit" class="btn btn-primary btn-block btn-submit" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span v-else>📤 提交审核</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { createOrder, getCategories } from '../api'

const router = useRouter()
const categories = ref([])
const selectedCategory = ref('')
const form = reactive({ description: '', location: '', reward: 0, contact: '', deadline: '' })
const error = ref('')
const loading = ref(false)

async function loadCategories() {
  try {
    const res = await getCategories()
    categories.value = res
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

onMounted(loadCategories)

async function handleSubmit() {
  if (!selectedCategory.value) {
    error.value = '请选择任务类型'
    return
  }

  error.value = ''
  loading.value = true
  try {
    await createOrder({
      ...form,
      category: selectedCategory.value
    })
    window.__toast?.('订单已提交', 'success')
    router.push('/')
  } catch (e) {
    error.value = e.message || '发布失败'
  } finally {
    loading.value = false
  }
}
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
.create-card {
  max-width: 580px;
  animation: cardEnter 0.5s ease both;
}
@keyframes cardEnter {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}
.form-row { display: flex; gap: 16px; }
.textarea { max-height: 160px; min-height: 120px; }
.hint {
  font-size: 12px;
  color: var(--gray-400);
  float: right;
  margin-top: 6px;
  font-weight: 500;
}
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
.btn-submit {
  padding: 14px;
  font-size: 15px;
  border-radius: 10px;
  font-weight: 600;
  letter-spacing: 0.5px;
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

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 10px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.category-item:hover {
  border-color: #4caf50;
  background: #f5f5f5;
}

.category-item.active {
  border-color: #4caf50;
  background: #e8f5e9;
}

.category-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.category-name {
  font-size: 12px;
  font-weight: 500;
  text-align: center;
}

.category-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #4caf50;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 8px;
}

@media (max-width: 640px) {
  .form-row { flex-direction: column; gap: 0; }
  .page-title { font-size: 20px; }
  .category-grid { grid-template-columns: repeat(3, 1fr); }
}
</style>
