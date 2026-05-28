import { ref } from 'vue'

const navOrder = ['/home', '/create', '/my-orders']
const adminOrder = ['/admin/review', '/admin/users', '/admin/categories']

export function usePageTransition() {
  const transitionName = ref('page-forward')

  const getOrderIndex = (path) => {
    const idx = navOrder.indexOf(path)
    if (idx !== -1) return idx
    const adminIdx = adminOrder.indexOf(path)
    if (adminIdx !== -1) return navOrder.length + adminIdx
    return -1
  }

  const onBeforeRouteChange = (to, from) => {
    const toIdx = getOrderIndex(to)
    const fromIdx = getOrderIndex(from)
    if (toIdx >= 0 && fromIdx >= 0) {
      transitionName.value = toIdx > fromIdx ? 'page-forward' : 'page-backward'
    } else {
      transitionName.value = 'page-forward'
    }
  }

  return { transitionName, onBeforeRouteChange }
}
