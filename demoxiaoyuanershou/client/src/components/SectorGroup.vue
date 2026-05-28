<template>
  <div class="sector-group">
    <div
      v-if="!isExpanded"
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

    <template v-if="isExpanded">
      <div
        v-for="(item, idx) in adminItems"
        :key="item.path"
        class="sector-child"
        :style="getChildStyle(idx)"
        role="menuitem"
        :aria-current="activePath === item.path ? 'page' : undefined"
        @click="$emit('navigate', item.path)"
      >
        <span class="sector-child-icon">{{ item.icon }}</span>
        <span class="sector-child-label">{{ item.label }}</span>
      </div>
      <div
        class="sector-back"
        :style="getChildStyle(adminItems.length)"
        role="menuitem"
        @click="$emit('collapse')"
      >
        <span class="sector-back-icon">◂</span>
        <span class="sector-back-label">返回</span>
      </div>
    </template>
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

const triggerStyle = computed(() => ({
  '--angle': props.baseAngle,
  '--radius': props.radius
}))

const getChildStyle = (idx) => {
  const angle = props.baseAngle + (idx - 1) * 30
  return {
    '--angle': angle,
    '--radius': props.radius
  }
}
</script>

<style scoped>
.sector-group {
  position: absolute;
  display: contents;
}

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
  transform: rotateY(calc(var(--angle) * 1deg)) translateZ(var(--radius));
  transition: all 400ms cubic-bezier(0.4, 0, 0.2, 1);
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  will-change: transform;
}

.sector-trigger:hover {
  background: var(--orange-50);
  border-style: solid;
}

.sector-trigger-arrow {
  font-size: 11px;
  opacity: 0.7;
}

.sector-child {
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
  border: 1.5px solid var(--blue-500);
  color: #1565c0;
  transform: rotateY(calc(var(--angle) * 1deg)) translateZ(var(--radius));
  transition: all 400ms cubic-bezier(0.4, 0, 0.2, 1);
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  animation: sectorChildEnter 0.3s ease both;
}

.sector-child:hover {
  background: var(--blue-50);
}

.sector-back {
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
  border: 1.5px solid var(--orange-500);
  color: var(--orange-600);
  transform: rotateY(calc(var(--angle) * 1deg)) translateZ(var(--radius));
  transition: all 400ms cubic-bezier(0.4, 0, 0.2, 1);
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  animation: sectorChildEnter 0.3s ease both;
}

.sector-back:hover {
  background: var(--orange-50);
}

@keyframes sectorChildEnter {
  from { opacity: 0; transform: rotateY(calc(var(--angle) * 1deg)) translateZ(var(--radius)) scale(0.8); }
  to { opacity: 1; transform: rotateY(calc(var(--angle) * 1deg)) translateZ(var(--radius)) scale(1); }
}
</style>
