# Java 笔记

> Java 学习笔记、API 用法、踩坑记录

---

## 数组

> 数组基础到泛型陷阱

---

## 2026-09-08 20:30

# Java 数组完全笔记

> 从声明初始化到内存模型，从 Arrays 工具类到泛型陷阱，一篇讲透 Java 数组。

## 一、数组是什么

数组（Array）是**相同类型数据的有序集合**，在 Java 中它是**引用类型**（对象），而不是基本类型。

一句话概括：**数组是存储在堆上的一块连续内存，栈上的变量只是指向它的引用。**

| 特性 | 说明 |
| --- | --- |
| 长度固定 | 一旦创建，`length` 不可变，想"扩容"只能创建新数组再拷贝 |
| 类型统一 | 元素必须是声明类型或其子类型 |
| 连续存储 | 内存地址连续，因此支持 `O(1)` 随机访问 |

> ⚠️ `length` 是**属性**不是方法：`arr.length` ✅ / `arr.length()` ❌（`String` 才是 `str.length()`）

## 二、声明与初始化

### 2.1 两种声明写法

```java
int[] arr;      // ✅ 推荐
int arr[];      // ✅ 合法，C/C++ 沿袭写法，不推荐
```

### 2.2 三种初始化方式

```java
// ① 静态初始化：指定内容，长度由 JVM 推算
int[] a = {1, 2, 3, 4, 5};
int[] b = new int[]{1, 2, 3, 4, 5};

// ② 动态初始化：指定长度，内容填默认值
int[] c = new int[5];        // [0, 0, 0, 0, 0]
String[] s = new String[3];  // [null, null, null]

// ③ 匿名数组：直接作为实参或重新赋值
printArray(new int[]{1, 2, 3});
a = new int[]{9, 8, 7};
```

> 💡 坑点：`int[] a = {1,2,3};` 大括号简写**只能在声明变量的同时用**。拆成两行 `int[] a; a = {1,2,3};` 会编译失败。

### 2.3 各类型默认值

| 数组类型 | 元素默认值 |
| --- | --- |
| `byte`/`short`/`int`/`long` | `0` |
| `float`/`double` | `0.0` |
| `char` | `'\u0000'`（空字符，不是空格） |
| `boolean` | `false` |
| 引用类型（如 `String`） | `null` |

> ⚠️ 元素有默认值，但**数组变量本身没有**。局部变量 `int[] a;` 未初始化就访问 `a.length` 会编译报错。

## 三、内存模型（面试高频）

```java
int[] a = new int[3];
a[0] = 10;
int[] b = a;        // b 和 a 指向同一个数组对象
b[1] = 20;
System.out.println(a[1]);   // 输出 20
```

```
栈 (Stack)              堆 (Heap)
┌────────┐         ┌──────────────────┐
│  a ────┼────────▶│  int[3] 对象      │
└────────┘         │  [0]=10 [1]=20   │
┌────────┐         │  [2]=0  length=3 │
│  b ────┼────────▶│                  │
└────────┘         └──────────────────┘
```

关键结论：

1. **数组对象在堆上**，栈上变量存的是引用地址。
2. **赋值 `b = a` 是引用传递，不是拷贝数据**。
3. 方法传参同理——传的是引用的副本：

```java
static void modify(int[] arr) {
    arr[0] = 99;             // ✅ 会影响外部
    arr = new int[]{1,2,3};  // ❌ 只改了局部引用，外部无感
}
```

4. 长度理论上限 `Integer.MAX_VALUE`，实际受 JVM 内存和对象头限制，远小于此。

## 四、访问与遍历

下标范围 `[0, length-1]`，越界抛 `ArrayIndexOutOfBoundsException`。

```java
int[] arr = {1, 2, 3, 4, 5};

// ① 普通 for —— 需要下标时
for (int i = 0; i < arr.length; i++) { ... }

// ② 增强 for —— 只读遍历
for (int num : arr) { ... }

// ③ Stream（JDK 8+）
Arrays.stream(arr).forEach(System.out::println);
```

> 💡 增强 for 对数组会被编译器改写成普通 for + length，性能无损。
> ⚠️ 增强 for 拿不到下标，且修改 `num` 不会影响 `arr[i]`（值是副本）；但元素若是对象，改 `num.xxx` 会生效。

## 五、多维数组

Java 的多维数组本质是**"数组的数组"**。

```java
int[][] matrix = new int[3][4];   // 3 行 4 列

int[][] triangle = new int[3][];  // 只指定行数
triangle[0] = new int[1];
triangle[1] = new int[2];
triangle[2] = new int[3];         // 不规则/锯齿数组 ✅

int[][] m = {{1,2}, {3,4,5}, {6}};
```

`matrix.length` 是行数，`matrix[i].length` 是第 i 行列数，**每行列数可不同**。

```java
for (int i = 0; i < matrix.length; i++)
    for (int j = 0; j < matrix[i].length; j++)
        System.out.print(matrix[i][j] + " ");

for (int[] row : matrix)
    for (int val : row)
        System.out.print(val + " ");
```

> ⚠️ `int[][] a = new int[][4];` 是编译错误——行数必须先确定。

## 六、Arrays 工具类

```java
import java.util.Arrays;

Arrays.toString(arr);           // 打印一维：[5, 3, 1, 4, 2]
Arrays.deepToString(matrix);    // 打印多维：[[1, 2], [3, 4]]

Arrays.sort(arr);               // 升序，原地修改
Arrays.parallelSort(arr);       // 并行排序（JDK 8+）

int idx = Arrays.binarySearch(arr, 4);      // 二分查找，必须先排序！
int[] copy = Arrays.copyOf(arr, 10);        // 拷贝并指定新长度
int[] part = Arrays.copyOfRange(arr, 1, 3); // 拷贝 [1, 3)

Arrays.fill(arr, 0);            // 全部填充
Arrays.equals(a1, a2);          // 一维比较
Arrays.deepEquals(m1, m2);      // 多维比较

List<Integer> list = Arrays.asList(1, 2, 3);
Arrays.stream(arr).sum();
```

**`binarySearch` 返回值**：找到返回下标（`>=0`）；未找到返回 `-(插入点) - 1`。

**排序算法差异（常考）**

| 排序对象 | 算法 |
| --- | --- |
| 基本类型数组 | 双轴快速排序 Dual-Pivot Quicksort |
| 对象数组 | TimSort（稳定的归并+插入） |

> 基本类型无稳定性需求，快排更快；对象排序需要稳定性，所以选 TimSort。

### ⚠️ `Arrays.asList` 的两个大坑

```java
// 坑 1：返回定长 List
List<Integer> list = Arrays.asList(1, 2, 3);
list.add(4);     // ❌ UnsupportedOperationException
list.set(0, 9);  // ✅ 修改可以

// 坑 2：基本类型数组被当成"一个元素"
int[] nums = {1, 2, 3};
List<int[]> wrong = Arrays.asList(nums);   // List 里只有 1 个元素
Integer[] boxed = {1, 2, 3};               // ✅ 用包装类型
List<Integer> right = Arrays.asList(boxed);
```

## 七、数组拷贝的四种方式

| 方式 | 示例 | 特点 |
| --- | --- |
| `System.arraycopy` | `System.arraycopy(src,0,dst,0,len)` | native 方法，**最快** |
| `Arrays.copyOf` | `Arrays.copyOf(src, len)` | 内部调 `arraycopy`，可顺便扩容 |
| `clone()` | `arr.clone()` | 最简洁，**只浅拷贝** |
| 手写循环 | `for(...) dst[i]=src[i]` | 最慢，最灵活 |

### 浅拷贝陷阱

```java
int[][] m = {{1,2}, {3,4}};
int[][] m2 = m.clone();
m2[0][0] = 99;
System.out.println(m[0][0]);  // 99 —— 原数组被改了！
```

`clone()` 只复制外层，内层数组共享引用。要真正独立需**逐行拷贝**：

```java
int[][] deep = new int[m.length][];
for (int i = 0; i < m.length; i++) deep[i] = m[i].clone();
```

> 一维**基本类型**数组的 `clone()` 效果等同深拷贝。

## 八、数组与泛型（进阶难点）

### 不能创建泛型数组

```java
List<String>[] arr = new List<String>[10];   // ❌ generic array creation
T[] arr = new T[10];                          // ❌ 类型擦除
```

原因：泛型**类型擦除**，数组**运行时保留类型信息**做检查，两者机制冲突。

```java
List<String>[] arr = (List<String>[]) new List[10];  // 能用，但有 unchecked 警告
List<List<String>> list = new ArrayList<>();          // ✅ 更推荐
```

### 数组协变（Covariance）

```java
String[] strs = {"a", "b"};
Object[] objs = strs;   // ✅ 编译通过
objs[0] = 123;          // ✅ 编译通过，运行时抛 ArrayStoreException
```

> 对比：泛型**不协变**，`List<String>` 不是 `List<Object>` 的子类型。

## 九、数组 vs 集合

| 维度 | `int[]` | `ArrayList<Integer>` |
| --- | --- | --- |
| 长度 | 固定 | 动态扩容 |
| 元素类型 | 支持基本类型，无装箱开销 | 只能存对象，有装箱开销 |
| 性能 | 更高，内存紧凑 | 略低，但便利 |
| 功能 | 依赖 `Arrays` | API 丰富 |
| 类型安全 | 运行时检查（协变有坑） | 编译期泛型检查 |

**选型**：长度已知、追求性能、基本类型 → 数组；需要增删改 → ArrayList。

```java
String[] arr = list.toArray(new String[0]);                 // 集合 → 数组
List<String> fixed = Arrays.asList(arr);                    // 定长视图
List<String> flex = new ArrayList<>(Arrays.asList(arr));    // 可变集合 ✅
```

## 十、常见异常速查

| 异常 | 触发场景 |
| --- | --- |
| `ArrayIndexOutOfBoundsException` | 下标 `< 0` 或 `>= length` |
| `NullPointerException` | 数组引用为 `null`，或元素为 `null` 时调用方法 |
| `ArrayStoreException` | 存入类型不符的对象（协变场景） |
| `NegativeArraySizeException` | `new int[-1]` |

## 十一、面试 & 实战高频点

1. `length` 是属性不是方法，`String` 才是 `length()`
2. 数组传递是引用传递，方法内改元素会影响外部
3. `Arrays.asList` 返回定长 List，且基本类型数组会被当一个元素
4. 多维数组 `clone()` 是浅拷贝，需逐行拷贝
5. `Arrays.sort` 基本类型用快排、对象用 TimSort
6. 数组协变导致 `ArrayStoreException`，泛型不协变
7. 不能创建泛型数组（类型擦除）
8. `binarySearch` 前必须排序，负返回值表示插入点
9. 数组没重写 `toString()`，直接打印得到 `[I@1b6d3586`
10. 增强 for 中修改元素值无效，改对象属性有效

## 附：速查代码模板

```java
import java.util.Arrays;

public class ArrayTemplate {
    public static void main(String[] args) {
        int[] arr = {3, 1, 4, 1, 5};

        Arrays.sort(arr);
        System.out.println(Arrays.toString(arr));         // [1, 1, 3, 4, 5]
        System.out.println(Arrays.binarySearch(arr, 4));  // 3

        int[] bigger = Arrays.copyOf(arr, arr.length * 2); // 模拟扩容

        Integer[] boxed = Arrays.stream(arr).boxed().toArray(Integer[]::new);
        var list = new java.util.ArrayList<>(Arrays.asList(boxed));
    }
}
```

---

*建议配合 JVM 内存模型一起复习，理解"引用"是打通 Java 基础的关键。*

---

## 集合框架

> Java 集合（Collection）相关知识

---

## 2026-09-07 15:20

Java 集合框架复习。

- `ArrayList` 底层是 `Object[]`，查询 O(1)，插入/删除 O(n)
- `LinkedList` 底层是双向链表，插入/删除 O(1)，查询 O(n)
- `HashMap` JDK 1.8 后是数组 + 链表 + 红黑树

常用方法速记：

```java
List<String> list = new ArrayList<>();
list.add("a");
list.get(0);
list.remove(0);
list.size();
```

---

## 多线程

> Java 并发编程基础

---

## 2026-08-30 14:00

Java 异常体系。

```
Throwable
├── Error (系统错误，程序无法处理)
│   ├── OutOfMemoryError
│   └── StackOverflowError
└── Exception (程序可以处理)
    ├── IOException (checked)
    └── RuntimeException (unchecked)
        ├── NullPointerException
        ├── ArrayIndexOutOfBoundsException
        └── ClassCastException
```

---

## 2026-08-20 09:45

Java 多线程基础。

创建线程三种方式：
1. 继承 `Thread` 类
2. 实现 `Runnable` 接口
3. 实现 `Callable` 接口（有返回值）

线程池 `ExecutorService`：

```java
ExecutorService pool = Executors.newFixedThreadPool(5);
pool.submit(() -> {
  // task
});
pool.shutdown();
```

---

## 字符串

> 从不可变性到字符串常量池，从拼接性能到编码乱码，一篇讲透 Java 里的 String。

---

## 2026-09-09 22:20

# Java 字符串完全笔记

> 从不可变性到字符串常量池，从拼接性能到编码乱码，一篇讲透 Java 里的 String。

## 一、String 是什么

`String` 是 `java.lang` 下的 `final` 类，代表**不可变的字符序列**。

```java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {
    private final byte[] value;   // JDK 9+ 为 byte[]；JDK 8 及以前是 char[]
    private int hash;             // 缓存 hashCode
}
```

三个关键特征：

| 特征 | 说明 |
| --- | --- |
| `final` 类 | 不可被继承，杜绝子类破坏不可变性 |
| 内部数组 `private final` | 引用不可变，且不对外暴露 |
| 无修改自身的方法 | 所有"修改"操作（`concat`/`replace`/`substring`/`trim`）都返回**新对象** |

> ⚠️ `final` 只保证 `value` 引用不变，数组内容理论上可通过反射改——但别这么干，会污染常量池。

## 二、创建方式：字面量 vs new

### 2.1 两种写法

```java
String s1 = "hello";             // 字面量：走字符串常量池
String s2 = new String("hello"); // new：强制在堆上新建对象
```

```java
String a = "abc";
String b = "abc";
String c = new String("abc");
String d = new String("abc");

System.out.println(a == b);   // true  —— 同一个常量池对象
System.out.println(a == c);   // false —— 池 vs 堆
System.out.println(c == d);   // false —— 两个不同的堆对象
System.out.println(a.equals(c));  // true —— 内容相同
```

### 2.2 `new String("abc")` 创建了几个对象？

经典面试题，答案是 **1 个或 2 个**：

1. **先检查常量池**：若池中已有 `"abc"`，则不创建字面量对象；若没有，先在池中创建一个。
2. **`new` 一定会在堆上**再创建一个 String 对象。

所以：池里已有 `"abc"` → 只创建 1 个（堆上的）；池里没有 → 创建 2 个。

```java
String s = new String("abc");   // 假设此前没出现过 "abc" → 2 个对象
s.intern();                      // 把堆对象引用放入常量池
```

### 2.3 推荐写法

```java
String s = "hello";   // ✅ 优先字面量，复用常量池，省内存
```

> 💡 几乎没有任何理由使用 `new String("xxx")`，除非你**故意**需要一个与池中不同的独立对象（极少见）。

## 三、字符串常量池（String Pool）

### 3.1 是什么

字符串常量池是一个 **HashTable 结构**（`StringTable`），存放字符串对象的引用，key 是字符串内容，目的是**复用相同内容的字符串，节省内存**。

### 3.2 位置的历史变迁（常考）

| JDK 版本 | 字符串常量池位置 |
| --- | --- |
| JDK 6 | 方法区（永久代 PermGen） |
| JDK 7 | **移到堆（Heap）** |
| JDK 8+ | 永久代被元空间（Metaspace）取代，字符串常量池**仍在堆** |

> 从 JDK 7 移到堆的好处：池中字符串也能被正常 GC 回收，降低 OOM 风险。

### 3.3 什么会进入常量池

```java
String s1 = "hello";                  // ✅ 字面量直接入池
String s2 = "hel" + "lo";             // ✅ 编译期常量折叠，等价于 "hello"，入池
final String a = "hel";
String s3 = a + "lo";                 // ✅ a 是 final 常量，编译期可折叠，入池

String b = "hel";
String s4 = b + "lo";                 // ❌ b 是变量，运行期拼接，结果在堆，不入池
String s5 = new String("hello");      // ❌ 堆对象，不入池（除非调 intern）
```

### 3.4 intern() 方法

`intern()` 的作用：如果池中已有等值字符串则返回池中引用；否则**把当前对象引用放入池**并返回。

```java
String s = new String("hello");
String t = s.intern();
String u = "hello";
System.out.println(t == u);  // true
System.out.println(s == u);  // false（s 仍是堆上那个）
```

**JDK 6 vs JDK 7+ 的行为差异（高频考点）**

```java
String s = new String("a") + new String("b");  // 堆上新建 "ab"，此时池中只有 "a"、"b"
s.intern();
String t = "ab";
System.out.println(s == t);
```

- **JDK 6**：`intern()` 把 `"ab"` **复制**到永久代 → `s`（堆）`!= t`（永久代）→ **false**
- **JDK 7+**：`intern()` 只在池中**记录堆对象的引用**（不再复制）→ `s == t` → **true**

## 四、为什么 String 要设计成不可变

### 4.1 实现不可变的手段

1. 类 `final`，禁止继承
2. 内部 `value` 数组 `private final`，不提供任何修改它的 public 方法
3. 所有看似"修改"的方法都返回新 String
4. JDK 9+ `value` 为 `byte[]` 且配合 `coder` 标志，同样受保护

### 4.2 不可变带来的四大好处

| 好处 | 说明 |
| --- | --- |
| **支持字符串常量池** | 只有当字符串不可变，多个引用共享同一对象才安全 |
| **缓存 hashCode** | `hash` 字段只算一次，HashMap 里做 key 性能极高 |
| **线程安全** | 天然不可变，无需同步，可自由共享 |
| **安全性** | 类名、路径、URL、数据库连接参数等敏感信息传进来不会被中途篡改 |

## 五、String 常用 API 全览

### 5.1 判断类

```java
"hello".equals("hello");        // true —— 内容比较
"hello".equalsIgnoreCase("HELLO");  // true —— 忽略大小写
"hello".startsWith("he");       // true
"hello".endsWith("lo");         // true
"hello".contains("ell");        // true
"".isEmpty();                   // true —— 仅判断 length == 0
"  ".isBlank();                 // true —— JDK 11+，判断是否为空白
```

> ⚠️ `equals` 要**常量写前面**防 NPE：`"hello".equals(s)` ✅ / `s.equals("hello")` ❌（s 为 null 就炸）

### 5.2 获取类

```java
String s = "Hello,Java";
s.length();            // 10  —— UTF-16 码元数（不是"字符"数！）
s.charAt(0);           // 'H'
s.indexOf("Java");     // 6   —— 找不到返回 -1
s.substring(0, 5);     // "Hello" —— [0, 5) 左闭右开
```

> ⚠️ **substring 的经典变迁**：JDK 6 中 `substring` 复用原 `char[]`，导致"取一小段却持有整个大字符串"，可能**内存泄漏**；JDK 7+ 改为复制出新数组，问题解决。

### 5.3 转换类

```java
"Hello".toLowerCase();     // "hello"
"  hi  ".strip();          // "hi"  —— JDK 11+，支持 Unicode 空白 ✅ 推荐
"a,b,c".split(",");        // ["a","b","c"]
"hello".getBytes(StandardCharsets.UTF_8);  // byte[] —— 务必显式指定字符集
```

> ⚠️ `trim()` 去不掉中文全角空格（`\u3000`），`strip()` 可以。

### 5.4 替换类

```java
"a.b.c".replace(".", "-");          // "a-b-c" —— 字面量替换
"a1b2c3".replaceAll("\d", "*");    // "a*b*c*" —— ⚠️ 参数是正则
"a.b.c".replaceAll(".", "-");       // "-----" —— 💥 "." 是正则的任意字符！
```

> 💡 记住：`replace` 是**字面量**，`replaceAll` / `replaceFirst` 是**正则**。

### 5.5 split 的坑

```java
"a,b,c".split(",");            // ["a", "b", "c"]
"a,b,c,,".split(",");          // ["a", "b", "c"]  ⚠️ 末尾空串被丢弃！
"a,b,c,,".split(",", -1);      // ["a", "b", "c", "", ""]  ✅ 加 limit=-1 保留
"192.168.1.1".split(".");      // []  💥 "." 是正则，必须转义
"192.168.1.1".split("\.");    // ["192","168","1","1"] ✅
```

## 六、字符串拼接（性能核心）

### 6.1 三种方式对比

| 维度 | `String` | `StringBuilder` | `StringBuffer` |
| --- | --- | --- | --- |
| 可变性 | 不可变 | 可变 | 可变 |
| 线程安全 | 安全（不可变） | ❌ 不安全 | ✅ 安全（`synchronized`） |
| 性能 | 最低（拼接场景） | 最高 | 中等（有锁开销） |
| 适用场景 | 少量拼接、常量 | **单线程大量拼接** ✅ | 多线程共享拼接 |

> 实际开发中 **99% 用 StringBuilder**，StringBuffer 基本只在遗留代码里见到。

### 6.2 循环里用 `+` 是真坑

```java
// ❌ 糟糕：每次循环都 new StringBuilder + toString
String s = "";
for (int i = 0; i < 10000; i++) {
    s += i;
}

// ✅ 正确：只创建一个 StringBuilder
StringBuilder sb = new StringBuilder(预估容量);
for (int i = 0; i < 10000; i++) {
    sb.append(i);
}
String s = sb.toString();
```

## 七、编码与乱码（实战必踩）

### 7.1 乱码根因

**一次编码一次解码用了不同字符集**，或者**字节流被截断**。

```java
byte[] bytes = "你好".getBytes(StandardCharsets.UTF_8);
String s = new String(bytes, StandardCharsets.UTF_8);
```

> ⚠️ `getBytes()` 和 `new String(byte[])` **不带字符集参数时会用平台默认编码**，同一份代码在 Windows（GBK）和 Linux（UTF-8）上结果不同。
> **永远显式指定 `StandardCharsets.UTF_8`** ✅

### 7.2 常见字符集

| 字符集 | 一个字符字节数 | 说明 |
| --- | --- | --- |
| UTF-8 | 1~4 变长 | 互联网事实标准，中文 3 字节，推荐 |
| GBK | 1~2 | 中文国标，Windows 中文环境默认 |
| ISO-8859-1 | 1 | 单字节，无法表示中文（会变成 `?`） |
| UTF-16 | 2 或 4 | Java 内部 `char` 的编码方式 |

### 7.3 `char` 不等于字符

Java 的 `char` 是 **UTF-16 码元**，一个 emoji 可能需要**两个 char**（代理对）：

```java
String s = "😀";       // 一个 emoji
s.length();            // 2  💥 不是 1！
s.codePointCount(0, s.length());  // 1  ✅ 真实字符数
```

## 八、面试 & 实战高频点

1. **String 不可变**：final 类 + private final value + 方法返回新对象。
2. **不可变的好处**：常量池复用、hash 缓存、线程安全、安全。
3. **`==` vs `equals`**：`==` 比地址，`equals` 比内容。
4. **`new String("abc")` 创建 1 或 2 个对象**（常量池有无）。
5. **常量池位置**：JDK 6 永久代 → JDK 7+ 堆。
6. **`intern()` 差异**：JDK 6 复制对象，JDK 7+ 记录引用。
7. **`StringBuilder` vs `StringBuffer`**：前者非线程安全快，后者 synchronized 慢。
8. **JDK 9 紧凑字符串**：`byte[]` + coder，Latin-1 省一半内存。
9. **`substring` 变迁**：JDK 6 共享数组（内存泄漏风险），JDK 7+ 复制新数组。
10. **`replace` 是字面量，`replaceAll` 是正则**。
11. **`split` 参数是正则，末尾空串默认丢弃**。
12. **hashCode 用 31 作乘子**：奇素数 + 可优化为 `(i << 5) - i`。
13. **`char` 是 UTF-16 码元**，emoji 占 2 个，用 `codePointCount` 数字符。

## 附：速查代码模板

```java
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class StringDemo {
    public static void main(String[] args) {
        // 安全比较
        String s = null;
        boolean eq = Objects.equals(s, "abc");      // false，不抛异常

        // 安全去空白（支持全角空格）
        String clean = "　 hello  ".strip();   // "hello"

        // 高效拼接
        StringBuilder sb = new StringBuilder(64);
        for (int i = 0; i < 10; i++) sb.append(i).append(',');
        sb.setLength(sb.length() - 1);    // 去掉最后一个逗号

        // 编码解码（务必显式指定字符集）
        byte[] bytes = "你好".getBytes(StandardCharsets.UTF_8);
        String decoded = new String(bytes, StandardCharsets.UTF_8);

        // 处理 emoji：数真实字符数
        String emoji = "Hi😀";
        int charCount = emoji.codePointCount(0, emoji.length());   // 3
    }
}
```

### 一句话

---

## 泛型

> 从类型擦除到 PECS 原则，从通配符到桥接方法，一篇讲透 Java 泛型的设计与坑点。

---

## 2026-09-10 00:30

# Java 泛型完全笔记

## 一、泛型是什么

泛型（Generics）是 JDK 5 引入的特性，本质是**参数化类型**——把"类型"当成参数传递。

三个核心价值：

| 价值 | 说明 |
| --- | --- |
| 类型安全 | 错误从运行时提前到编译期暴露 |
| 消除强制转换 | 代码更简洁，可读性更好 |
| 代码复用 | 一套逻辑适配多种类型 |

泛型的三个使用位置：**泛型类、泛型接口、泛型方法**。

## 二、泛型类与泛型接口

### 2.1 泛型类

```java
public class Box<T> {
    private T value;
    public void set(T value) { this.value = value; }
    public T get() { return value; }
}

Box<String> box = new Box<>();
box.set("hello");
String v = box.get();     // 不用强转
```

JDK 7 起支持**菱形语法** `new Box<>()`，编译器自动推断类型。

### 2.2 泛型接口

```java
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```

实现类有两种选择：
1. 实现时确定具体类型
2. 实现类继续保留泛型

### 2.3 类型参数命名约定

| 符号 | 含义 |
| --- | --- |
| T | Type，通用类型 |
| E | Element，集合元素 |
| K / V | Key / Value |
| N | Number |
| ? | 通配符，未知类型 |

> ⚠️ 泛型类型参数**不能是基本类型**：`List<int>` ❌，必须写 `List<Integer>` ✅

## 三、泛型方法

泛型方法的类型参数声明在**返回值前面**。

```java
public static <T> T getMiddle(T[] arr) {
    return arr[arr.length / 2];
}
```

**泛型方法 vs 泛型类的方法**：

```java
class Box<T> {
    public T get() { ... }                      // 泛型类的普通方法
    public <E> void print(E e) { ... }          // ✅ 泛型方法
    public static <E> void show(E e) { ... }    // ✅ 静态泛型方法
}
```

> 💡 **静态方法不能使用类上的泛型参数**，因为类泛型参数在 new 对象时才确定

## 四、类型擦除（核心难点）

Java 泛型是**伪泛型**——泛型信息只存在于编译期，编译后的字节码里会被**擦除**。

### 4.1 擦除规则

| 声明 | 擦除后 |
| --- | --- |
| `<T>`（无界） | Object |
| `<T extends Comparable>`（有界） | 第一个边界 Comparable |

```java
// 源码
public class Box<T> {
    private T value;
    public T get() { return value; }
}

// 编译后（等价）
public class Box {
    private Object value;
    public Object get() { return value; }
}
```

验证：

```java
List<String> sList = new ArrayList<>();
List<Integer> iList = new ArrayList<>();
System.out.println(sList.getClass() == iList.getClass());  // true！
```

### 4.2 桥接方法（Bridge Method）

擦除会破坏多态，编译器用桥接方法补救。

```java
class MyInt implements Comparable<Integer> {
    @Override
    public int compareTo(Integer o) { return 0; }
}
```

擦除后接口方法是 `compareTo(Object)`，而类里只有 `compareTo(Integer)`，编译器会生成桥接方法转发。

## 五、擦除带来的限制（面试高频）

| 限制 | 原因 |
| --- | --- |
| 不能 `new T()` | 擦除后变 `new Object()` |
| 不能创建泛型数组 | 数组运行时保留类型检查，与擦除冲突 |
| 不能 `instanceof` 泛型 | 运行时无泛型信息 |
| 不能用于基本类型 | 擦除到 Object，基本类型不是对象 |
| 不能重载擦除后签名相同的方法 | 方法签名冲突 |

### 变通方案

```java
// 创建泛型实例：传 Class 对象
public static <T> T create(Class<T> clazz) throws Exception {
    return clazz.getDeclaredConstructor().newInstance();
}

// 创建泛型数组：用 Array.newInstance
public static <T> T[] createArray(Class<T> clazz, int size) {
    return (T[]) Array.newInstance(clazz, size);
}
```

## 六、通配符与 PECS 原则

### 6.1 泛型是不协变的

```java
String[] strs = new String[10];
Object[] objs = strs;          // ✅ 数组协变

List<String> strList = new ArrayList<>();
List<Object> objList = strList;  // ❌ 编译错误！泛型不协变
```

### 6.2 通配符 `?`

| 通配符 | 含义 | 能读吗 | 能写吗 |
| --- | --- | --- | --- |
| `?` | 完全未知 | ✅ 只能读成 Object | ❌ 除 null 外不能写 |
| `? extends T` | T 或 T 的子类 | ✅ 读成 T | ❌ 不能写 |
| `? super T` | T 或 T 的父类 | ⚠️ 只能读成 Object | ✅ 能写 T 及子类 |

### 6.3 PECS 原则（必背）

> **P**roducer **e**xtends, **C**onsumer **s**uper

- 你是**数据源**（不断 get 给别人）→ 用 `? extends T`
- 你是**数据消费者**（不断 add 进来）→ 用 `? super T`

JDK 源码经典例子——`Collections.copy`：

```java
public static <T> void copy(List<? super T> dest, List<? extends T> src) {
    for (int i = 0; i < src.size(); i++)
        dest.add(src.get(i));
}
```

## 七、泛型边界（Bounded Type）

```java
// 上界：T 必须是 Number 或其子类
public <T extends Number> double sum(List<T> list) {
    double s = 0;
    for (T t : list) s += t.doubleValue();
    return s;
}
```

### 多重边界

```java
// 类必须写在第一个，且最多一个类边界 + 多个接口边界
public static <T extends Comparable<T> & Serializable> T max(List<T> list)
```

## 八、泛型与可变参数：堆污染

```java
@SafeVarargs
public static <T> void addAll(List<T> list, T... elements) {
    for (T e : elements) list.add(e);
}
```

`@SafeVarargs` 只能用在 static、final 或（JDK 9+）private 方法上。

## 九、泛型实战高频场景

### 用反射获取泛型类型（ORM / JSON 框架常用）

```java
class UserDao extends BaseDao<User> { }

Type superClass = UserDao.class.getGenericSuperclass();
if (superClass instanceof ParameterizedType pt) {
    Type actual = pt.getActualTypeArguments()[0];   // User.class
}
```

### 通配符捕获

```java
// 用辅助泛型方法"捕获"通配符
public static void swap(List<?> list, int i, int j) {
    swapHelper(list, i, j);
}
private static <E> void swapHelper(List<E> list, int i, int j) {
    E tmp = list.get(i);
    list.set(i, list.get(j));
    list.set(j, tmp);
}
```

## 十、面试 & 实战高频点

1. **Java 泛型是编译期特性**，运行时类型擦除
2. **擦除规则**：无界 → Object，有界 → 第一个边界
3. **泛型不协变**，List<String> 不是 List<Object> 的子类型
4. **PECS**：生产者 extends，消费者 super
5. **不能创建泛型数组**，不能 new T()，不能 instanceof 泛型
6. **桥接方法**是编译器为保持多态合成的
7. **静态方法不能用类的泛型参数**，必须自己声明 <T>
8. **基本类型不能作泛型参数**，必须用包装类
9. **泛型信息可通过反射拿到**（Signature 属性）
10. **@SafeVarargs** 用于消除泛型可变参数的堆污染警告

## 附：速查代码模板

```java
import java.util.*;

public class GenericsDemo {
    // 泛型类
    static class Box<T> {
        private T value;
        public void set(T v) { this.value = v; }
        public T get() { return value; }
    }

    // 泛型方法：<T> 在返回值前
    static <T> T pick(T a, T b) { return a; }

    // 上界 + 多重边界
    static <T extends Comparable<? super T>> T max(List<? extends T> list) {
        T max = null;
        for (T t : list) {
            if (max == null || t.compareTo(max) > 0) max = t;
        }
        return max;
    }

    // PECS：dest 消费 → super；src 生产 → extends
    static <T> void copy(List<? super T> dest, List<? extends T> src) {
        for (int i = 0; i < src.size(); i++) dest.add(src.get(i));
    }

    public static void main(String[] args) {
        Box<String> box = new Box<>();
        box.set("hello");
        System.out.println(box.get());

        List<Integer> nums = Arrays.asList(3, 1, 4, 1, 5);
        System.out.println(max(nums));   // 5
    }
}
```

### 一句话记忆

> **泛型只在编译期存在；要读用 extends，要写用 super；能写泛型方法的，优先写泛型方法而不是泛型类。**
