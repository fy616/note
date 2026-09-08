# NoteFlow · 个人笔记

一个**本地优先**的个人笔记网站。把想记录的内容发给 ZCode，我会自动追加到本地 markdown 文件，浏览器打开就能看到。

## ✨ 特性

- 📝 **追加式记录** — 每次发消息自动加时间戳，零摩擦
- 🗂️ **模块化** — 按主题分模块（如 `java`、`算法`），每个模块下可再分子模块
- 🔍 **本地搜索** — 标题/正文/标签全文搜索，结果实时高亮
- 🌌 **星空背景** — 大厅页用 WebGL Galaxy 渲染，每次打开随机激励诗句
- 💾 **数据本地** — 纯本地 markdown 文件，可直接用任何编辑器打开
- ⚡ **极简** — Vite + React + TypeScript，gzip 后 88 KB

## 🏗️ 数据结构

```
../notes/                  ← 笔记数据（一个 .md 文件 = 一个模块）
├── java.md               # 例：java 模块
│   ├── ## 数组            # 子模块（用 ## 标题 + > 描述声明）
│   ├── ## 集合框架
│   └── ## YYYY-MM-DD HH:MM  # 单条笔记（带时间戳）
└── 算法.md
```

约定：
- **顶级模块**：每个 `.md` 文件 = 一个模块
- **子模块**：模块文件内 `## 子模块名` + 紧跟 `> 描述` 引用块
- **单条笔记**：`## YYYY-MM-DD HH:MM` 标题 + markdown 正文

## 🚀 开发

```bash
# 安装依赖
npm install

# 启动 dev server（会自动找 4173 端口）
npm run dev

# 生产构建
npm run build
```

默认打开 http://127.0.0.1:4173

## 📂 路由

| 路径 | 页面 |
|---|---|
| `/` | 大厅（模块卡片网格 + 星空 + 随机诗句） |
| `/m/<模块名>` | 模块内的子模块列表（若有） |
| `/m/<模块名>/<子模块>` | 子模块的笔记列表 |
| `/compare` | 三套主题样式对比页（Cursor / Linear / Claude） |
| `/b` `/c` | 切换其他主题风格 |

## 🎨 主题

- **Version A · Cursor 风**（默认）：暖奶白底 + 橙色强调 + Source Serif 4 正文
- **Version B · Linear 风**：近黑底 + 紫蓝单色 + Inter
- **Version C · Claude 风**：奶油底 + 珊瑚橙 + Lora 衬线

主题 CSS 在 `src/themes-*.css`，底部有切换器。

## 🛠️ 技术栈

- **Vite 8** — 构建工具
- **React 19** + **TypeScript** — UI
- **Tailwind CSS 3** — 工具类
- **Zustand** — 状态（早期版本用过，现在已迁移到纯组件状态）
- **OGL** — WebGL 渲染（Galaxy 背景）
- **Lucide** — 图标
- **idb** — IndexedDB（早期版本用过，现已改为本地文件）
- **Vite Plugin** — 自定义 `vite-plugin-note.ts` 扫描 `../notes/` 提供 API

## 💬 工作流

1. **发消息给我** — 比如「算法：今天做了 5 道链表题」
2. **我追加到 `notes/算法.md`** — 自动加时间戳
3. **浏览器打开页面** — 新条目立刻可见（按 `R` 手动刷新，或等 30 秒自动）

## 📝 License

MIT
