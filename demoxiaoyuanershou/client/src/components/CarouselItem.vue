<template>
  <div
    class="carousel-item"
    :class="{ active: isActive, hidden: !visible }"
    :style="itemStyle"
    :role="'menuitem'"
    :aria-current="isActive ? 'page' : undefined"
    @click="$emit('select')"
    @transitionend="onTransitionEnd"
  >
    <span class="carousel-item-icon">{{ icon }}</span>
    <span class="carousel-item-label">{{ label }}</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  angle: { type: Number, required: true },
  isActive: { type: Boolean, default: false },
  visible: { type: Boolean, default: true },
  icon: { type: String, default: '' },
  label: { type: String, default: '' },
  radius: { type: String, default: '120px' },
  isDragging: { type: Boolean, default: false }
})

defineEmits(['select', 'transitionend'])

const itemStyle = computed(() => {
  if (props.isActive) {
    return {
      '--angle': '0',
      '--radius': props.radius,
      opacity: 1,
      zIndex: 10
    }
  }
  const dist = Math.abs(props.angle)
  const scale = dist <= 90 ? 1 - (dist / 90) * 0.3 : 0.7
  const opacity = dist <= 90 ? 1 - (dist / 90) * 0.7 : 0.3
  return {
    '--angle': props.angle,
    '--radius': props.radius,
    opacity: props.visible ? opacity : 0,
    transform: `rotateY(calc(var(--angle) * 1deg)) translateZ(var(--radius)) scale(${scale})`,
    transition: props.isDragging ? 'none' : undefined
  }
})

const onTransitionEnd = (e) => {
  if (e.propertyName === 'transform') {
    e.stopPropagation()
  }
}
</script>

<style scoped>
.carousel-item {
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
  border: 1.5px solid var(--green-300);
  color: var(--gray-700);
  transition: all 400ms cubic-bezier(0.4, 0, 0.2, 1);
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  will-change: transform, opacity;
  user-select: none;
}

.carousel-item:hover:not(.active) {
  background: var(--green-50);
  border-color: var(--green-400);
}

.carousel-item.active {
  background: linear-gradient(135deg, var(--green-500), var(--green-700));
  color: #fff;
  border-color: transparent;
  box-shadow: 0 4px 16px rgba(76, 175, 80, 0.4);
  transform: rotateY(0deg) translateZ(var(--radius)) scale(1.15);
  font-weight: 700;
}

.carousel-item.hidden {
  pointer-events: none;
  visibility: hidden;
}

.carousel-item-icon {
  font-size: 16px;
  line-height: 1;
}

.carousel-item-label {
  font-size: 13px;
}
</style>
