import { ref, computed } from 'vue'

export function useCarousel(items) {
  const activeIndex = ref(0)
  const isExpanded = ref(false)

  const anglePerItem = computed(() => 360 / items.value.length)

  const getItemAngle = (index) => {
    return index * anglePerItem.value
  }

  const isVisible = (index, total) => {
    const diff = Math.abs(index - activeIndex.value)
    const wrappedDiff = Math.min(diff, total - diff)
    return wrappedDiff <= 2
  }

  const rotateNext = () => {
    if (isExpanded.value) return
    activeIndex.value = (activeIndex.value + 1) % items.value.length
  }

  const rotatePrev = () => {
    if (isExpanded.value) return
    activeIndex.value = (activeIndex.value - 1 + items.value.length) % items.value.length
  }

  const navigateTo = (index, router) => {
    if (index === activeIndex.value) return
    activeIndex.value = index
    if (items.value[index]?.path) {
      router.push(items.value[index].path)
    }
  }

  const onKeydown = (e) => {
    if (e.key === 'ArrowRight') { rotateNext(); e.preventDefault() }
    if (e.key === 'ArrowLeft') { rotatePrev(); e.preventDefault() }
  }

  const syncFromRoute = (path) => {
    const idx = items.value.findIndex(item => item.path === path)
    if (idx !== -1) {
      activeIndex.value = idx
    }
  }

  return {
    activeIndex, isExpanded,
    getItemAngle, isVisible, rotateNext, rotatePrev, navigateTo,
    onKeydown, syncFromRoute
  }
}
