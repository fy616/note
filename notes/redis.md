# Redis 笔记

> Redis 缓存、Spring Boot 集成、数据结构操作

---

## Spring Boot 操作 Redis

> StringRedisTemplate 读写字符串与对象的手动 JSON 序列化

---

## 2026-09-10 12:00

# Redis 笔记：Spring Boot 操作 Redis —— String 类型与对象存取

> 环境：Spring Boot + `spring-boot-starter-data-redis`（默认 Lettuce 客户端）+ JUnit 5
> 主题：用 `StringRedisTemplate` 读写字符串，以及对象的**手动 JSON 序列化**存取

## 1. 前置准备

### 1.1 引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 1.2 配置连接（application.yml）

```yaml
spring:
  data:
    redis:
      host: localhost   # Redis 服务器地址（本机需先启动 Redis 服务）
      port: 6379        # 默认端口
      password: ""      # 有密码就填
      database: 0       # 默认使用 0 号库
```

### 1.3 注入 StringRedisTemplate

```java
@Autowired
private StringRedisTemplate stringRedisTemplate;
```

## 2. 操作 String 类型（最常用的数据结构）

```java
@Test
void testString() {
    // 写入：对应 Redis 命令 SET name 胡歌
    stringRedisTemplate.opsForValue().set("name", "胡歌");

    // 读取：对应 Redis 命令 GET name
    String name = stringRedisTemplate.opsForValue().get("name");
    System.out.println("name = " + name);
}
```

要点：

- `opsForValue()` 拿到的是操作 **String 类型**的对象，`set()` / `get()` 分别对应 `SET`、`GET` 命令。
- 用 `StringRedisTemplate` 时，key 和 value 都按**纯字符串**存入 Redis，中文不会变成乱码，在 `redis-cli` 里可以直接看到明文。

## 3. 存取 Java 对象：手动 JSON 序列化

Redis 本身不认识 Java 对象，所以对象要先转成 JSON 字符串再存，取出后再反序列化回来。

```java
/** ObjectMapper 线程安全，建议声明为 static final 全局复用 */
private static final ObjectMapper objectMapper = new ObjectMapper();

@Test
void testUser() {
    // ① 创建对象
    User u = new User("lsfa0", 29);

    // ② 手动序列化：对象 -> JSON 字符串
    String userJson = objectMapper.writeValueAsString(u);

    // ③ 写入 Redis：SET user:021 <json>
    stringRedisTemplate.opsForValue().set("user:021", userJson);

    // ④ 读取：GET user:021，拿到 JSON 字符串
    String json = stringRedisTemplate.opsForValue().get("user:021");

    // ⑤ 手动反序列化：JSON 字符串 -> 对象
    User u2 = objectMapper.readValue(json, User.class);
    System.out.println("user = " + u2);
}
```

存入 Redis 后的实际内容：

```
SET user:021 "{\"name\":\"lsfa0\",\"age\":29}"
```

流程总结：

```
Java 对象 --ObjectMapper.writeValueAsString()--> JSON 字符串 --set()--> Redis
Redis --get()--> JSON 字符串 --ObjectMapper.readValue()--> Java 对象
```

## 4. StringRedisTemplate vs RedisTemplate

| 对比项 | StringRedisTemplate | RedisTemplate（默认配置） |
|---|---|---|
| key 序列化器 | StringRedisSerializer（明文） | JdkSerializationRedisSerializer（二进制） |
| value 序列化器 | StringRedisSerializer（明文） | JdkSerializationRedisSerializer（二进制） |
| 存储效果 | 可读，中文正常，redis-cli 能直接看 | 乱码/转义字节，可读性差 |
| 对象存储 | 需**手动** JSON 序列化 | 类需实现 Serializable，自动 JDK 序列化 |
| 推荐场景 | 绝大多数业务场景 | 一般不推荐直接用默认配置 |

> 实际项目中最常见的做法：注入 `StringRedisTemplate` + 手动（或封装工具方法）做 JSON 序列化；
> 或者自定义 `RedisTemplate` 的序列化器（如 Jackson 的 JSON 序列化器）后再使用。

## 5. 本段代码的注意点与易错点

1. **Jackson 3 的包名变了**：`tools.jackson.databind.ObjectMapper` 是 Jackson 3（Spring Boot 4 / Spring Framework 7 起默认使用）。旧教程里的 Jackson 2 是 `com.fasterxml.jackson.databind.ObjectMapper`，两者不要混用。

2. **Jackson 3 的异常改成了非受检**：`writeValueAsString()` / `readValue()` 抛出的 `JacksonException` 是 `RuntimeException`，所以测试方法里**不需要** throws 或 try-catch。若用 Jackson 2，则必须处理受检异常 `JsonProcessingException`。

3. **类名规范**：`pojo.user` 中的 `user` 违反 Java 命名规范（类名应大驼峰），应改成 `User`。

4. **变量命名**：读回来的 JSON 字符串建议命名为 `userJson` / `json`，不要和类名、对象名撞车。

5. **key 命名规范**：用冒号 `:` 分层，如 `user:021` = `业务:ID`，便于管理和在图形化客户端里按前缀浏览。

6. **ObjectMapper 复用**：`ObjectMapper` 创建成本高但线程安全，声明成 `static final` 常量是对的，不要在方法里反复 new。

7. **测试类要有 Spring 上下文**：`@SpringBootTest` 会启动完整应用上下文，才能注入 `StringRedisTemplate`；运行前确认 Redis 服务已启动、连接配置正确。

## 6. 完整整理后的代码

```java
package org.example.redisdemo1;

import org.example.redisdemo1.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
public class RedisStringTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /** Jackson 3 的 ObjectMapper，线程安全，全局复用 */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** 读写字符串 */
    @Test
    void testString() {
        stringRedisTemplate.opsForValue().set("name", "胡歌");
        String name = stringRedisTemplate.opsForValue().get("name");
        System.out.println("name = " + name);
    }

    /** 手动 JSON 序列化方式存取对象 */
    @Test
    void testUser() {
        // 创建对象
        User u = new User("lsfa0", 29);
        // 手动序列化：对象 -> JSON
        String userJson = objectMapper.writeValueAsString(u);
        // 写入数据
        stringRedisTemplate.opsForValue().set("user:021", userJson);

        // 获取数据（JSON 字符串）
        String json = stringRedisTemplate.opsForValue().get("user:021");
        // 手动反序列化：JSON -> 对象
        User u2 = objectMapper.readValue(json, User.class);
        System.out.println("user = " + u2);
    }
}
```

## 7. 延伸：RedisTemplate 家族速查

| 方法 | 对应 Redis 数据结构 | 典型场景 |
|---|---|---|
| opsForValue() | String | 缓存对象、验证码、分布式锁 |
| opsForHash() | Hash | 对象的字段级存取（hashKey → value） |
| opsForList() | List | 消息队列、最新列表 |
| opsForSet() | Set | 去重、点赞、共同好友 |
| opsForZSet() | ZSet | 排行榜、延迟队列 |

> Hash 存对象是另一种思路：`opsForHash().put("user:021", "name", "lsfa0")`，
> 好处是可以单独读取/更新某个字段，不必整体序列化。
