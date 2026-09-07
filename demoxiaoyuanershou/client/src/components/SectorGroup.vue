<template>
  <div
    class="sector-trigger"
    :class="{ active: isActive }"
    :style="triggerStyle"
    role="menuitem"
    :aria-expanded="false"
    @click="$emit('expand')"
  >
    <span class="sector-trigger-icon">⚙️</span>
    <span class="sector-trigger-label">管理</span>
    <span class="sector-trigger-arrow">▸</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  isExpanded: { type: Boolean, default: false },
  isActive: { type: Boolean, default: false },
  adminItems: { type: Array, default: () => [] },
  activePath: { type: String, default: '' },
  baseAngle: { type: Number, default: 0 },
  radius: { type: String, default: '120px' }
})

defineEmits(['expand', 'collapse', 'navigate'])

const getOffsetX = (angle) => {
  const angleRad = (angle * Math.PI) / 180
  return Math.sin(angleRad) * 100
}

const triggerStyle = computed(() => {
  const offsetX = getOffsetX(props.baseAngle)
  return {
    transform: `translateX(${offsetX}px)`,
    zIndex: 5
  }
})
</script>

<style scoped>
.sector-trigger {
  position: absolute;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 7px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
  background: #fff;
  border: 1.5px dashed var(--orange-500);
  color: var(--orange-600);
  transition: all 800ms cubic-bezier(0.25, 0.1, 0.25, 1);
  will-change: transform;
  left: 50%;
  top: 50%;
  margin-left: -40px;
  margin-top: -16px;
}

.sector-trigger:hover {
  background: var(--orange-50);
  border-style: solid;
}

.sector-trigger-arrow {
  font-size: 11px;
  opacity: 0.7;
}
</style>
