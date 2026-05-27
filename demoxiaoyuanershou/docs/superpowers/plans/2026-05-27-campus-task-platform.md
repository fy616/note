# 校园任务发布平台实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将校园代扔垃圾平台扩展为通用校园任务发布平台，支持多任务类型、分类审核、截止时间和聊天功能

**Architecture:** 扩展现有 Order 表新增 category/deadline 字段，新建 TaskCategory 和 Message 实体，前端增加分类筛选和聊天弹窗

**Tech Stack:** Spring Boot 3.2.1, JPA, MySQL, Vue 3, Vite, Pinia

---

## 文件结构

### 后端 (server-java)

**新建文件:**
- `src/main/java/com/campustrash/entity/TaskCategory.java` - 任务分类实体
- `src/main/java/com/campustrash/entity/Message.java` - 聊天消息实体
- `src/main/java/com/campustrash/repository/TaskCategoryRepository.java` - 分类仓库
- `src/main/java/com/campustrash/repository/MessageRepository.java` - 消息仓库
- `src/main/java/com/campustrash/dto/CategoryRequest.java` - 分类请求DTO
- `src/main/java/com/campustrash/dto/MessageRequest.java` - 消息请求DTO
- `src/main/java/com/campustrash/service/CategoryService.java` - 分类服务
- `src/main/java/com/campustrash/service/MessageService.java` - 消息服务
- `src/main/java/com/campustrash/controller/CategoryController.java` - 分类控制器
- `src/main/java/com/campustrash/controller/MessageController.java` - 消息控制器
- `src/main/java/com/campustrash/config/CategorySeeder.java` - 预设分类数据

**修改文件:**
- `src/main/java/com/campustrash/entity/Order.java` - 新增 category/deadline 字段
- `src/main/java/com/campustrash/dto/OrderRequest.java` - 新增 category/deadline 参数
- `src/main/java/com/campustrash/service/OrderService.java` - 修改创建逻辑支持分类审核
- `src/main/java/com/campustrash/repository/OrderRepository.java` - 新增分类筛选方法

### 前端 (client/src)

**新建文件:**
- `views/AdminCategories.vue` - 分类管理页面
- `components/ChatModal.vue` - 聊天弹窗组件

**修改文件:**
- `views/Home.vue` - 增加分类筛选栏、截止时间显示、聊天入口
- `views/CreateOrder.vue` - 增加分类选择、截止时间选择
- `views/MyOrders.vue` - 增加聊天入口
- `views/AdminReview.vue` - 显示分类信息
- `router/index.js` - 新增分类管理路由
- `api/index.js` - 新增分类和消息API

---

## Task 1: 新增 TaskCategory 实体和预设数据

**Files:**
- Create: `server-java/src/main/java/com/campustrash/entity/TaskCategory.java`
- Create: `server-java/src/main/java/com/campustrash/repository/TaskCategoryRepository.java`
- Create: `server-java/src/main/java/com/campustrash/config/CategorySeeder.java`

- [ ] **Step 1: 创建 TaskCategory 实体**

```java
package com.campustrash.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "task_categories")
public class TaskCategory {
    @Id
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 10)
    private String icon;

    @Column(name = "need_review")
    private Boolean needReview = true;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_custom")
    private Boolean isCustom = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        id = String.valueOf(System.currentTimeMillis());
        createdAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 2: 创建 TaskCategoryRepository**

```java
package com.campustrash.repository;

import com.campustrash.entity.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskCategoryRepository extends JpaRepository<TaskCategory, String> {
    List<TaskCategory> findAllByOrderBySortOrderAsc();
    boolean existsByName(String name);
}
```

- [ ] **Step 3: 创建 CategorySeeder 预设数据**

```java
package com.campustrash.config;

import com.campustrash.entity.TaskCategory;
import com.campustrash.repository.TaskCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategorySeeder implements CommandLineRunner {

    private final TaskCategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) return;

        createCategory("代扔垃圾", "🗑️", true, 1);
        createCategory("代拿快递", "📦", false, 2);
        createCategory("代打饭", "🍜", false, 3);
        createCategory("代占座", "💺", false, 4);
        createCategory("代打印", "🖨️", false, 5);
        createCategory("搬家帮忙", "🏠", true, 6);
        createCategory("其他", "📌", true, 7);
    }

    private void createCategory(String name, String icon, boolean needReview, int sortOrder) {
        TaskCategory category = new TaskCategory();
        category.setId(String.valueOf(System.currentTimeMillis() + sortOrder));
        category.setName(name);
        category.setIcon(icon);
        category.setNeedReview(needReview);
        category.setSortOrder(sortOrder);
        category.setIsCustom(false);
        categoryRepository.save(category);
    }
}
```

- [ ] **Step 4: 启动后端验证表创建**

```bash
cd server-java
mvn spring-boot:run
```

Expected: 控制台显示 `task_categories` 表创建成功，预设数据已插入

- [ ] **Step 5: Commit**

```bash
git add server-java/src/main/java/com/campustrash/entity/TaskCategory.java
git add server-java/src/main/java/com/campustrash/repository/TaskCategoryRepository.java
git add server-java/src/main/java/com/campustrash/config/CategorySeeder.java
git commit -m "feat: add TaskCategory entity and preset categories"
```

---

## Task 2: 新增 CategoryService 和 CategoryController

**Files:**
- Create: `server-java/src/main/java/com/campustrash/dto/CategoryRequest.java`
- Create: `server-java/src/main/java/com/campustrash/service/CategoryService.java`
- Create: `server-java/src/main/java/com/campustrash/controller/CategoryController.java`

- [ ] **Step 1: 创建 CategoryRequest DTO**

```java
package com.campustrash.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String icon;
    private Boolean needReview = true;
    private Integer sortOrder = 0;
}
```

- [ ] **Step 2: 创建 CategoryService**

```java
package com.campustrash.service;

import com.campustrash.dto.CategoryRequest;
import com.campustrash.entity.TaskCategory;
import com.campustrash.repository.TaskCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final TaskCategoryRepository categoryRepository;

    public List<TaskCategory> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    public TaskCategory createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("分类名称已存在");
        }

        TaskCategory category = new TaskCategory();
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setNeedReview(request.getNeedReview());
        category.setSortOrder(request.getSortOrder());
        category.setIsCustom(true);

        return categoryRepository.save(category);
    }

    public TaskCategory updateCategory(String id, CategoryRequest request) {
        TaskCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setNeedReview(request.getNeedReview());
        category.setSortOrder(request.getSortOrder());

        return categoryRepository.save(category);
    }

    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }
}
```

- [ ] **Step 3: 创建 CategoryController**

```java
package com.campustrash.controller;

import com.campustrash.dto.CategoryRequest;
import com.campustrash.entity.TaskCategory;
import com.campustrash.security.JwtUser;
import com.campustrash.service.CategoryService;
import com.campustrash.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<TaskCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<TaskCategory> createCategory(
            @AuthenticationPrincipal JwtUser user,
            @Valid @RequestBody CategoryRequest request) {
        adminService.checkAdmin(user.getId());
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskCategory> updateCategory(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable String id,
            @Valid @RequestBody CategoryRequest request) {
        adminService.checkAdmin(user.getId());
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable String id) {
        adminService.checkAdmin(user.getId());
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}
```

- [ ] **Step 4: 测试分类API**

```bash
# 获取所有分类
curl http://localhost:3000/api/categories

# 登录管理员
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 创建自定义分类（需要token）
curl -X POST http://localhost:3000/api/categories \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"测试分类","icon":"🧪","needReview":false}'
```

Expected: 返回分类列表，创建成功

- [ ] **Step 5: Commit**

```bash
git add server-java/src/main/java/com/campustrash/dto/CategoryRequest.java
git add server-java/src/main/java/com/campustrash/service/CategoryService.java
git add server-java/src/main/java/com/campustrash/controller/CategoryController.java
git commit -m "feat: add CategoryService and CategoryController"
```

---

## Task 3: 修改 Order 实体支持分类和截止时间

**Files:**
- Modify: `server-java/src/main/java/com/campustrash/entity/Order.java`
- Modify: `server-java/src/main/java/com/campustrash/dto/OrderRequest.java`
- Modify: `server-java/src/main/java/com/campustrash/service/OrderService.java`

- [ ] **Step 1: 修改 Order 实体新增字段**

在 Order.java 中添加：

```java
@Column(length = 50)
private String category;

@Column(name = "deadline")
private LocalDateTime deadline;
```

- [ ] **Step 2: 修改 OrderRequest DTO**

```java
package com.campustrash.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderRequest {
    private String description;
    private String location;
    private Integer reward;
    private String contact;
    private String category;
    private LocalDateTime deadline;
}
```

- [ ] **Step 3: 修改 OrderService.createOrder 方法**

```java
public Order createOrder(String userId, OrderRequest request) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));

    if (user.getIsFrozen() == 1) {
        throw new RuntimeException("账号已被冻结，无法发布订单");
    }

    Order order = new Order();
    order.setUserId(userId);
    order.setUsername(user.getUsername());
    order.setDescription(request.getDescription());
    order.setLocation(request.getLocation());
    order.setReward(request.getReward());
    order.setContact(request.getContact());
    order.setCategory(request.getCategory());
    order.setDeadline(request.getDeadline());

    // 根据分类决定是否需要审核
    if (request.getCategory() != null) {
        TaskCategory category = categoryRepository.findByName(request.getCategory())
                .orElse(null);
        if (category != null && !category.getNeedReview()) {
            order.setStatus("approved");
        } else {
            order.setStatus("pending_review");
        }
    } else {
        order.setStatus("pending_review");
    }

    return orderRepository.save(order);
}
```

需要注入 TaskCategoryRepository：

```java
private final TaskCategoryRepository categoryRepository;
```

- [ ] **Step 4: 测试创建订单**

```bash
# 创建免审核订单（代拿快递）
curl -X POST http://localhost:3000/api/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"description":"帮我拿快递","location":"快递站","category":"代拿快递","contact":"123456"}'

# 创建需审核订单（代扔垃圾）
curl -X POST http://localhost:3000/api/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"description":"一袋垃圾","location":"3号楼","category":"代扔垃圾","contact":"123456"}'
```

Expected: 代拿快递状态为 approved，代扔垃圾状态为 pending_review

- [ ] **Step 5: Commit**

```bash
git add server-java/src/main/java/com/campustrash/entity/Order.java
git add server-java/src/main/java/com/campustrash/dto/OrderRequest.java
git add server-java/src/main/java/com/campustrash/service/OrderService.java
git commit -m "feat: add category and deadline to Order"
```

---

## Task 4: 新增 Message 实体和聊天功能

**Files:**
- Create: `server-java/src/main/java/com/campustrash/entity/Message.java`
- Create: `server-java/src/main/java/com/campustrash/repository/MessageRepository.java`
- Create: `server-java/src/main/java/com/campustrash/dto/MessageRequest.java`
- Create: `server-java/src/main/java/com/campustrash/service/MessageService.java`
- Create: `server-java/src/main/java/com/campustrash/controller/MessageController.java`

- [ ] **Step 1: 创建 Message 实体**

```java
package com.campustrash.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    private String id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "sender_name")
    private String senderName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        id = String.valueOf(System.currentTimeMillis());
        createdAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 2: 创建 MessageRepository**

```java
package com.campustrash.repository;

import com.campustrash.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByOrderIdOrderByCreatedAtAsc(String orderId);
    long countByOrderIdAndSenderIdNot(String orderId, String senderId);
}
```

- [ ] **Step 3: 创建 MessageRequest DTO**

```java
package com.campustrash.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageRequest {
    @NotBlank(message = "消息内容不能为空")
    private String content;
}
```

- [ ] **Step 4: 创建 MessageService**

```java
package com.campustrash.service;

import com.campustrash.dto.MessageRequest;
import com.campustrash.entity.Message;
import com.campustrash.entity.Order;
import com.campustrash.entity.User;
import com.campustrash.repository.MessageRepository;
import com.campustrash.repository.OrderRepository;
import com.campustrash.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public List<Message> getMessages(String orderId, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 只有发布者和接单者可以查看消息
        if (!order.getUserId().equals(userId) && !userId.equals(order.getTakerId())) {
            throw new RuntimeException("无权查看消息");
        }

        return messageRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
    }

    public Message sendMessage(String orderId, String userId, MessageRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 只有发布者和接单者可以发消息
        if (!order.getUserId().equals(userId) && !userId.equals(order.getTakerId())) {
            throw new RuntimeException("无权发送消息");
        }

        // 只有状态为taken时才能发消息
        if (!"taken".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许聊天");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Message message = new Message();
        message.setOrderId(orderId);
        message.setSenderId(userId);
        message.setSenderName(user.getUsername());
        message.setContent(request.getContent());

        return messageRepository.save(message);
    }

    public long getUnreadCount(String orderId, String userId) {
        return messageRepository.countByOrderIdAndSenderIdNot(orderId, userId);
    }
}
```

- [ ] **Step 5: 创建 MessageController**

```java
package com.campustrash.controller;

import com.campustrash.dto.MessageRequest;
import com.campustrash.entity.Message;
import com.campustrash.security.JwtUser;
import com.campustrash.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders/{orderId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<Message>> getMessages(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable String orderId) {
        return ResponseEntity.ok(messageService.getMessages(orderId, user.getId()));
    }

    @PostMapping
    public ResponseEntity<Message> sendMessage(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable String orderId,
            @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(messageService.sendMessage(orderId, user.getId(), request));
    }

    @GetMapping("/unread")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable String orderId) {
        long count = messageService.getUnreadCount(orderId, user.getId());
        return ResponseEntity.ok(Map.of("count", count));
    }
}
```

- [ ] **Step 6: 测试聊天API**

```bash
# 获取消息列表
curl http://localhost:3000/api/orders/<orderId>/messages \
  -H "Authorization: Bearer <token>"

# 发送消息
curl -X POST http://localhost:3000/api/orders/<orderId>/messages \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"content":"你好，我来拿快递"}'

# 获取未读消息数
curl http://localhost:3000/api/orders/<orderId>/messages/unread \
  -H "Authorization: Bearer <token>"
```

Expected: 返回消息列表，发送成功，返回未读数

- [ ] **Step 7: Commit**

```bash
git add server-java/src/main/java/com/campustrash/entity/Message.java
git add server-java/src/main/java/com/campustrash/repository/MessageRepository.java
git add server-java/src/main/java/com/campustrash/dto/MessageRequest.java
git add server-java/src/main/java/com/campustrash/service/MessageService.java
git add server-java/src/main/java/com/campustrash/controller/MessageController.java
git commit -m "feat: add Message entity and chat API"
```

---

## Task 5: 前端新增分类和消息API

**Files:**
- Modify: `client/src/api/index.js`

- [ ] **Step 1: 新增分类API**

```javascript
// 分类相关
export const getCategories = () => api.get('/categories')
export const createCategory = (data) => api.post('/categories', data)
export const updateCategory = (id, data) => api.put(`/categories/${id}`, data)
export const deleteCategory = (id) => api.delete(`/categories/${id}`)

// 消息相关
export const getMessages = (orderId) => api.get(`/orders/${orderId}/messages`)
export const sendMessage = (orderId, content) => api.post(`/orders/${orderId}/messages`, { content })
export const getUnreadCount = (orderId) => api.get(`/orders/${orderId}/messages/unread`)
```

- [ ] **Step 2: 修改 createOrder API**

```javascript
export const createOrder = (data) => api.post('/orders', data)
```

data 现在包含 category 和 deadline 字段

- [ ] **Step 3: Commit**

```bash
git add client/src/api/index.js
git commit -m "feat: add category and message API functions"
```

---

## Task 6: 前端首页增加分类筛选

**Files:**
- Modify: `client/src/views/Home.vue`

- [ ] **Step 1: 新增分类筛选栏状态**

在 script setup 中添加：

```javascript
import { getCategories } from '../api'

const categories = ref([])
const currentCategory = ref('all')

// 加载分类
async function loadCategories() {
  try {
    const res = await getCategories()
    categories.value = res.data
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

onMounted(loadCategories)
```

- [ ] **Step 2: 修改 loadOrders 支持分类筛选**

```javascript
async function loadOrders() {
  loading.value = true
  try {
    const params = {}
    if (currentTab.value !== 'all') params.status = currentTab.value
    if (currentCategory.value !== 'all') params.category = currentCategory.value
    const res = await getOrders(params)
    orders.value = res.data.orders
  } catch {
    orders.value = []
  } finally {
    loading.value = false
  }
}

// 监听分类变化
watch(currentCategory, loadOrders)
```

- [ ] **Step 3: 添加分类筛选栏模板**

在 tabs 下方添加：

```vue
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
```

- [ ] **Step 4: 添加分类筛选样式**

```css
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
```

- [ ] **Step 5: Commit**

```bash
git add client/src/views/Home.vue
git commit -m "feat: add category filter to Home page"
```

---

## Task 7: 前端发布页增加分类选择

**Files:**
- Modify: `client/src/views/CreateOrder.vue`

- [ ] **Step 1: 新增分类状态和加载**

```javascript
const categories = ref([])
const selectedCategory = ref('')

async function loadCategories() {
  try {
    const res = await getCategories()
    categories.value = res.data
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

onMounted(loadCategories)
```

- [ ] **Step 2: 添加分类选择模板**

在 form 开头添加：

```vue
<div class="form-group">
  <label class="label">任务类型</label>
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
```

- [ ] **Step 3: 添加截止时间选择**

```vue
<div class="form-group">
  <label class="label">截止时间（可选）</label>
  <input
    v-model="form.deadline"
    type="datetime-local"
    class="input"
  />
</div>
```

- [ ] **Step 4: 修改提交逻辑**

```javascript
async function handleSubmit() {
  if (!selectedCategory.value) {
    error.value = '请选择任务类型'
    return
  }

  loading.value = true
  try {
    await createOrder({
      ...form,
      category: selectedCategory.value
    })
    // ...
  }
}
```

- [ ] **Step 5: Commit**

```bash
git add client/src/views/CreateOrder.vue
git commit -m "feat: add category and deadline to CreateOrder page"
```

---

## Task 8: 创建聊天弹窗组件

**Files:**
- Create: `client/src/components/ChatModal.vue`

- [ ] **Step 1: 创建 ChatModal 组件**

```vue
<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="chat-modal">
      <div class="chat-header">
        <h3>任务聊天</h3>
        <button class="close-btn" @click="$emit('close')">×</button>
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
import { ref, onMounted, nextTick } from 'vue'
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
    messages.value = res.data
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
    alert(e.response?.data?.message || '发送失败')
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
import { onUnmounted } from 'vue'
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
```

- [ ] **Step 2: Commit**

```bash
git add client/src/components/ChatModal.vue
git commit -m "feat: create ChatModal component"
```

---

## Task 9: 首页集成聊天入口和截止时间显示

**Files:**
- Modify: `client/src/views/Home.vue`

- [ ] **Step 1: 导入 ChatModal**

```javascript
import ChatModal from '../components/ChatModal.vue'

const chatOrderId = ref(null)
```

- [ ] **Step 2: 在订单卡片添加聊天按钮**

```vue
<button
  v-if="order.status === 'taken' && (order.userId === store.userId || order.takerId === store.userId)"
  class="btn btn-outline btn-sm"
  @click="chatOrderId = order.id"
>
  💬 聊天
</button>
```

- [ ] **Step 3: 添加截止时间显示**

```vue
<span v-if="order.deadline" class="deadline-tag">
  ⏰ {{ formatDeadline(order.deadline) }}
</span>
```

```javascript
function formatDeadline(deadline) {
  const d = new Date(deadline)
  const now = new Date()
  const diff = d - now

  if (diff < 0) return '已过期'
  if (diff < 3600000) return `剩余 ${Math.floor(diff / 60000)} 分钟`
  if (diff < 86400000) return `剩余 ${Math.floor(diff / 3600000)} 小时`
  return `截止 ${d.getMonth() + 1}/${d.getDate()} ${d.getHours()}:${d.getMinutes().toString().padStart(2, '0')}`
}
```

- [ ] **Step 4: 添加 ChatModal 到模板**

```vue
<ChatModal
  v-if="chatOrderId"
  :order-id="chatOrderId"
  @close="chatOrderId = null"
/>
```

- [ ] **Step 5: Commit**

```bash
git add client/src/views/Home.vue
git commit -m "feat: integrate chat and deadline display in Home"
```

---

## Task 10: 创建管理员分类管理页面

**Files:**
- Create: `client/src/views/AdminCategories.vue`
- Modify: `client/src/router/index.js`

- [ ] **Step 1: 创建 AdminCategories 页面**

```vue
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
    categories.value = res.data
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
    alert(e.response?.data?.message || '操作失败')
  }
}

async function handleDelete(id) {
  if (!confirm('确定删除此分类？')) return
  try {
    await deleteCategory(id)
    await loadCategories()
  } catch (e) {
    alert(e.response?.data?.message || '删除失败')
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
```

- [ ] **Step 2: 添加路由**

在 router/index.js 中添加：

```javascript
{
  path: '/admin/categories',
  name: 'AdminCategories',
  component: () => import('../views/AdminCategories.vue'),
  meta: { requiresAuth: true, requiresAdmin: true }
}
```

- [ ] **Step 3: 在 App.vue 添加导航链接**

在 admin-nav 中添加：

```vue
<router-link to="/admin/categories" class="nav-link">分类管理</router-link>
```

- [ ] **Step 4: Commit**

```bash
git add client/src/views/AdminCategories.vue
git add client/src/router/index.js
git add client/src/App.vue
git commit -m "feat: add AdminCategories page and route"
```

---

## 实现顺序

1. Task 1-2: 后端分类系统
2. Task 3: Order 实体改造
3. Task 4: 聊天功能
4. Task 5: 前端 API
5. Task 6-7: 前端分类筛选和发布
6. Task 8-9: 聊天弹窗集成
7. Task 10: 管理员分类管理

## 测试验证

完成所有任务后：

1. 启动后端：`cd server-java && mvn spring-boot:run`
2. 启动前端：`cd client && npm run dev`
3. 测试流程：
   - 注册/登录
   - 查看分类筛选
   - 发布不同类型任务
   - 接单并聊天
   - 管理员管理分类
