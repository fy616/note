# 校园任务发布平台 - 设计文档

## 概述

将现有的"校园代扔垃圾服务平台"扩展为通用的"校园任务发布平台"，支持多种任务类型、分类审核、任务截止时间和任务聊天功能。

## 核心变更

### 1. 任务分类系统

**新增实体：TaskCategory**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 主键 |
| name | String | 分类名称（唯一） |
| icon | String | 图标（emoji） |
| needReview | Boolean | 是否需要审核 |
| sortOrder | Integer | 排序权重 |
| isCustom | Boolean | 是否为用户自定义分类 |
| createdAt | LocalDateTime | 创建时间 |

**预设分类**：

| 分类名 | 图标 | 是否需审核 |
|--------|------|-----------|
| 代扔垃圾 | 🗑️ | 是 |
| 代拿快递 | 📦 | 否 |
| 代打饭 | 🍜 | 否 |
| 代占座 | 💺 | 否 |
| 代打印 | 🖨️ | 否 |
| 搬家帮忙 | 🏠 | 是 |
| 其他 | 📌 | 是 |

**Order 表变更**：
- 新增 `category` 字段（String，分类名称）

**审核逻辑**：
- 创建订单时，根据分类的 `needReview` 决定初始状态
- `needReview=true` → `pending_review`
- `needReview=false` → `approved`（直接上线）

### 2. 任务截止时间

**Order 表变更**：
- 新增 `deadline` 字段（LocalDateTime，可为空）

**业务规则**：
- 发布任务时可选择截止时间（可选）
- 截止时间到达后，任务自动变为 `expired` 状态
- 已过期的任务不能被接单
- 首页显示剩余时间

**状态机扩展**：
```
pending_review → approved → taken → completed
                 ↓
              expired (自动)
```

### 3. 任务聊天

**新增实体：Message**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 主键 |
| orderId | String | 关联任务 |
| senderId | String | 发送者ID |
| senderName | String | 发送者名称（冗余） |
| content | String | 消息内容 |
| createdAt | LocalDateTime | 发送时间 |

**业务规则**：
- 只有任务发布者和接单者可以聊天
- 任务状态为 `taken` 时才能发消息
- 消息按时间正序排列

### 4. 前端页面改造

**首页 (Home.vue)**：
- 顶部增加分类筛选栏（横向滚动图标 + "全部"）
- 任务卡片显示分类图标和名称
- 截止时间显示（倒计时或"已过期"）
- 聊天入口按钮

**发布页 (CreateOrder.vue)**：
- 新增分类选择（图标网格 + 自定义输入框）
- 新增截止时间选择器
- 根据分类自动提示是否需要审核

**我的订单 (MyOrders.vue)**：
- 增加分类筛选
- 聊天入口

**管理员页面**：
- 新增"分类管理"页面
- 审核页面显示分类信息

**新路由**：
- `/admin/categories` — 分类管理

## API 变更

### 新增端点

| Method | Endpoint | 说明 |
|--------|----------|------|
| GET | `/api/categories` | 获取所有分类 |
| POST | `/api/categories` | 创建分类（管理员） |
| PUT | `/api/categories/{id}` | 更新分类（管理员） |
| DELETE | `/api/categories/{id}` | 删除分类（管理员） |
| GET | `/api/orders/{id}/messages` | 获取任务消息 |
| POST | `/api/orders/{id}/messages` | 发送消息 |
| GET | `/api/orders/{id}/messages/unread` | 获取未读消息数 |

### 修改端点

- `POST /api/orders` — 新增 `category` 和 `deadline` 参数
- `GET /api/orders` — 新增 `category` 筛选参数

## 数据库变更

### 新增表

**task_categories**：
```sql
CREATE TABLE task_categories (
  id VARCHAR(255) PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  icon VARCHAR(10),
  need_review BOOLEAN DEFAULT TRUE,
  sort_order INT DEFAULT 0,
  is_custom BOOLEAN DEFAULT FALSE,
  created_at DATETIME
);
```

**messages**：
```sql
CREATE TABLE messages (
  id VARCHAR(255) PRIMARY KEY,
  order_id VARCHAR(255) NOT NULL,
  sender_id VARCHAR(255) NOT NULL,
  sender_name VARCHAR(50),
  content TEXT NOT NULL,
  created_at DATETIME,
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (sender_id) REFERENCES users(id)
);
```

### 修改表

**orders**：
```sql
ALTER TABLE orders ADD COLUMN category VARCHAR(50);
ALTER TABLE orders ADD COLUMN deadline DATETIME;
```

## 实现优先级

1. **P0 - 任务分类**：新增 TaskCategory 实体、修改 Order、前端分类选择
2. **P0 - 分类审核**：根据分类决定是否需要审核
3. **P1 - 截止时间**：新增 deadline 字段、过期定时任务
4. **P1 - 任务聊天**：新增 Message 实体、聊天 API、前端聊天弹窗
5. **P2 - 分类管理**：管理员分类管理页面
