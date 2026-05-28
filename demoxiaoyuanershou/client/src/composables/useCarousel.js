import { ref, computed, watch } from 'vue'

export function useCarousel(items) {
  const activeIndex = ref(0)
  const isRotating = ref(false)
  const isExpanded = ref(false)
  const isDragging = ref(false)
  const manualRotation = ref(null)

  const anglePerItem = computed(() => 360 / items.value.length)

  const rotation = computed(() => {
    if (isDragging.value && manualRotation.value !== null) {
      return manualRotation.value
    }
    return -activeIndex.value * anglePerItem.value
  })

  const getItemAngle = (index) => {
    return index * anglePerItem.value
  }

  const isVisible = (index, total) => {
    const diff = Math.abs(index - activeIndex.value)
    const wrappedDiff = Math.min(diff, total - diff)
    return wrappedDiff <= 2
  }

  const rotateNext = () => {
    if (isRotating.value || isExpanded.value) return
    isRotating.value = true
    activeIndex.value = (activeIndex.value + 1) % items.value.length
  }

  const rotatePrev = () => {
    if (isRotating.value || isExpanded.value) return
    isRotating.value = true
    activeIndex.value = (activeIndex.value - 1 + items.value.length) % items.value.length
  }

  const navigateTo = (index, router) => {
    if (index === activeIndex.value || isRotating.value) return
    isRotating.value = true
    activeIndex.value = index
    const track = document.querySelector('.carousel-track')
    if (track) {
      const onEnd = () => {
        track.removeEventListener('transitionend', onEnd)
        isRotating.value = false
        router.push(items.value[index].path)
      }
      track.addEventListener('transitionend', onEnd)
      setTimeout(() => {
        if (isRotating.value) {
          track.removeEventListener('transitionend', onEnd)
          isRotating.value = false
          router.push(items.value[index].path)
        }
      }, 500)
    }
  }

  const onTransitionEnd = () => {
    isRotating.value = false
  }

  const onKeydown = (e) => {
    if (e.key === 'ArrowRight') { rotateNext(); e.preventDefault() }
    if (e.key === 'ArrowLeft') { rotatePrev(); e.preventDefault() }
  }

  let wheelTimer = null
  const onWheel = (e) => {
    e.preventDefault()
    if (wheelTimer) return
    wheelTimer = setTimeout(() => { wheelTimer = null }, 100)
    if (e.deltaY > 0 || e.deltaX > 0) rotateNext()
    else rotatePrev()
  }

  let startX = 0
  let startRotationVal = 0

  const onTouchStart = (e) => {
    if (isExpanded.value) return
    startX = e.touches[0].clientX
    startRotationVal = rotation.value
    isDragging.value = true
    manualRotation.value = startRotationVal
  }

  const onTouchMove = (e) => {
    if (!isDragging.value) return
    const deltaX = e.touches[0].clientX - startX
    manualRotation.value = startRotationVal + (deltaX / 100) * anglePerItem.value
  }

  const onTouchEnd = () => {
    if (!isDragging.value) return
    isDragging.value = false
    const nearest = Math.round(manualRotation.value / anglePerItem.value)
    const newIndex = ((-nearest % items.value.length) + items.value.length) % items.value.length
    activeIndex.value = newIndex
    manualRotation.value = null
  }

  const syncFromRoute = (path) => {
    const idx = items.value.findIndex(item => item.path === path)
    if (idx !== -1 && !isRotating.value) {
      activeIndex.value = idx
    }
  }

  return {
    activeIndex, rotation, isRotating, isExpanded, isDragging,
    getItemAngle, isVisible, rotateNext, rotatePrev, navigateTo,
    onTransitionEnd, onKeydown, onWheel, onTouchStart, onTouchMove,
    onTouchEnd, syncFromRoute
  }
}
