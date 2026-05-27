<template>
  <div class="admin-categories">
    <div class="header">
      <h2>分类管理</h2>
      <button class="btn btn-primary" @click="showAdd = true">添加分类</button>
    </div>

    <div class="category-list">
      <div v-for="cat in categories" :key="cat.id" class="category-card card">
        <div class="category-info">
          <span class="cat-icon">{{ cat.icon }}</span>
          <span class="cat-name">{{ cat.name }}</span>
          <span v-if="!cat.needReview" class="badge badge-success">免审核</span>
          <span v-if="cat.isCustom" class="badge badge-info">自定义</span>
        </div>
        <div class="category-actions">
          <button class="btn btn-sm btn-outline" @click="editCategory(cat)">编辑</button>
          <button class="btn btn-sm btn-danger" @click="handleDelete(cat.id)">删除</button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑弹窗 -->
    <div v-if="showAdd || editingCategory" class="modal-overlay" @click.self="closeModal">
      <div class="modal card">
        <h3>{{ editingCategory ? '编辑分类' : '添加分类' }}</h3>
        <div class="form-group">
          <label class="label">分类名称</label>
          <input v-model="form.name" class="input" placeholder="输入分类名称" />
        </div>
        <div class="form-group">
          <label class="label">图标（emoji）</label>
          <input v-model="form.icon" class="input" placeholder="如：📦" />
        </div>
        <div class="form-group">
          <label class="checkbox-label">
            <input type="checkbox" v-model="form.needReview" />
            需要审核
          </label>
        </div>
        <div class="modal-actions">
          <button class="btn btn-outline" @click="closeModal">取消</button>
          <button class="btn btn-primary" @click="handleSubmit">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategories, createCategory, updateCategory, deleteCategory } from '../api'

const categories = ref([])
const showAdd = ref(false)
const editingCategory = ref(null)
const form = ref({ name: '', icon: '', needReview: true })

async function loadCategories() {
  try {
    const res = await getCategories()
    categories.value = res
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

function editCategory(cat) {
  editingCategory.value = cat
  form.value = { name: cat.name, icon: cat.icon, needReview: cat.needReview }
}

function closeModal() {
  showAdd.value = false
  editingCategory.value = null
  form.value = { name: '', icon: '', needReview: true }
}

async function handleSubmit() {
  try {
    if (editingCategory.value) {
      await updateCategory(editingCategory.value.id, form.value)
    } else {
      await createCategory(form.value)
    }
    closeModal()
    await loadCategories()
  } catch (e) {
    alert(e.message || '操作失败')
  }
}

async function handleDelete(id) {
  if (!confirm('确定删除此分类？')) return
  try {
    await deleteCategory(id)
    await loadCategories()
  } catch (e) {
    alert(e.message || '删除失败')
  }
}

onMounted(loadCategories)
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.category-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cat-icon {
  font-size: 24px;
}

.cat-name {
  font-weight: 600;
}

.badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.badge-success {
  background: #e8f5e9;
  color: #2e7d32;
}

.badge-info {
  background: #e3f2fd;
  color: #1565c0;
}

.category-actions {
  display: flex;
  gap: 8px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  width: 90%;
  max-width: 400px;
  padding: 24px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}
</style>
