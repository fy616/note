<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="chat-modal">
      <div class="chat-header">
        <h3>任务聊天</h3>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>

      <div class="chat-messages" ref="messagesRef">
        <div
          v-for="msg in messages"
          :key="msg.id"
          :class="['message', { mine: msg.senderId === userId }]"
        >
          <div class="message-sender">{{ msg.senderName }}</div>
          <div class="message-content">{{ msg.content }}</div>
          <div class="message-time">{{ formatTime(msg.createdAt) }}</div>
        </div>
        <div v-if="messages.length === 0" class="empty-chat">
          暂无消息，发送第一条吧
        </div>
      </div>

      <div class="chat-input">
        <input
          v-model="newMessage"
          @keyup.enter="handleSend"
          placeholder="输入消息..."
          class="input"
        />
        <button @click="handleSend" :disabled="!newMessage.trim()" class="btn btn-primary">
          发送
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { getMessages, sendMessage } from '../api'
import { useUserStore } from '../stores/user'

const props = defineProps({
  orderId: { type: String, required: true }
})

const emit = defineEmits(['close'])

const store = useUserStore()
const userId = store.userId
const messages = ref([])
const newMessage = ref('')
const messagesRef = ref(null)

async function loadMessages() {
  try {
    const res = await getMessages(props.orderId)
    messages.value = res
    await nextTick()
    scrollToBottom()
  } catch (e) {
    console.error('加载消息失败', e)
  }
}

async function handleSend() {
  if (!newMessage.value.trim()) return

  try {
    await sendMessage(props.orderId, newMessage.value)
    newMessage.value = ''
    await loadMessages()
  } catch (e) {
    alert(e.message || '发送失败')
  }
}

function scrollToBottom() {
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

function formatTime(iso) {
  const d = new Date(iso)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

// 每5秒刷新消息
let timer = null
onMounted(() => {
  loadMessages()
  timer = setInterval(loadMessages, 5000)
})

// 组件卸载时清除定时器
onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
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

.chat-modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  height: 500px;
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.chat-header h3 {
  margin: 0;
  font-size: 16px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.message {
  margin-bottom: 12px;
  max-width: 80%;
}

.message.mine {
  margin-left: auto;
  text-align: right;
}

.message-sender {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.message-content {
  background: #f5f5f5;
  padding: 8px 12px;
  border-radius: 8px;
  display: inline-block;
}

.message.mine .message-content {
  background: #4caf50;
  color: white;
}

.message-time {
  font-size: 11px;
  color: #ccc;
  margin-top: 4px;
}

.empty-chat {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

.chat-input {
  display: flex;
  gap: 8px;
  padding: 16px;
  border-top: 1px solid #eee;
}

.chat-input .input {
  flex: 1;
}
</style>
