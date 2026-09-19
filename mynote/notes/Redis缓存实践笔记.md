# Redis 做缓存实践笔记

> 今天第一次用 Redis 做业务缓存，一开始完全无从下手，最后还是靠 AI 一点点把代码拼出来了。
> 把这两段核心逻辑记下来，方便以后回看。



***

## 场景一：查询店铺类型列表（缓存整个集合）

思路：先查 Redis，命中就直接返回；没命中再查数据库，查完把结果写回 Redis。



```
// 先看看 Redis 里有没有缓存

String shoptype = stringRedisTemplate.opsForValue().get("cache:shop:type");

// 有就直接返回

if (StrUtil.isNotBlank(shoptype)) {

&#x20;   List\<ShopType> shopTypes = JSONUtil.toList(shoptype, ShopType.class);

&#x20;   return shopTypes;

}

// 没有就查询数据库

List\<ShopType> shopTypes = this.list();

if (shopTypes.isEmpty()) {

&#x20;   return shopTypes;

}

// 把数据转成 JSON 字符串写入 Redis

String s = JSONUtil.toJsonStr(shopTypes);

stringRedisTemplate.opsForValue().set("cache:shop:type", s);

return shopTypes;
```

**关键点：**



* 用的是 `StringRedisTemplate.opsForValue()`，操作的是 String 类型的 KV。

* Java 对象不能直接存进 Redis，所以用 `JSONUtil.toJsonStr()` 序列化成字符串，取出来再用 `JSONUtil.toList()` 反序列化。

* 缓存 key 直接写死了 `"cache:shop:type"`，一个业务一个 key。

* 注意：这里 `this.list()` 返回空集合时直接返回，没有把空结果缓存进 Redis（后面容易踩缓存穿透的坑）。



***

## 场景二：根据 id 查询店铺详情（按单条数据缓存）

思路：和场景一一样的套路，只不过 key 里拼上了 id，做到一个店铺一个缓存。



```
// 从 Redis 查询缓存里面是不是有

String shopjson = stringRedisTemplate.opsForValue().get(CACHE\_SHOP\_KEY + id);

// Redis 里面有，直接返回

if (StrUtil.isNotBlank(shopjson)) {

&#x20;   Shop shop = JSONUtil.toBean(shopjson, Shop.class);

&#x20;   return Result.ok(shop);

}

// 缓存没有，就从数据库里面查

Shop shop = getById(id);

// 数据库里面也没有，就返回错误

if (shop == null) {

&#x20;   return Result.fail("店铺不存在");

}

// 存在的时候先存到 Redis 缓存里

stringRedisTemplate.opsForValue().set(CACHE\_SHOP\_KEY + id, JSONUtil.toJsonStr(shop));

return Result.ok(shop);
```

**关键点：**



* 缓存 key 用常量 + id 拼接：`CACHE_SHOP_KEY + id`，比如 `cache:shop:1`、`cache:shop:2`。

* 走的是 “缓存 → 数据库 → 回写缓存” 的标准流程。

* 数据库查不到时直接 `Result.fail`，没有缓存空对象（这也是后面要优化的点）。

* 反序列化单个对象用的是 `JSONUtil.toBean()`，反序列化集合用的是 `JSONUtil.toList()`。



***

## 整体套路总结

两段代码其实是同一个模板：



```
1\. 查 Redis（get key）

2\. 命中 → 反序列化 → 直接返回

3\. 没命中 → 查数据库

4\. 数据库有 → 序列化成 JSON → 写回 Redis（set key）

5\. 数据库没有 → 返回空 / 返回错误
```

**这次用到的工具类：**



* `StringRedisTemplate.opsForValue()`：操作 Redis 的 String 类型数据。

* `JSONUtil.toJsonStr()`：对象 → JSON 字符串。

* `JSONUtil.toBean()`：JSON → 单个对象。

* `JSONUtil.toList()`：JSON → 集合对象。

* `StrUtil.isNotBlank()`：判断字符串非空。



***

## 留着以后优化的点（今天先跑通）



* 缓存没有设置过期时间（`set` 时没有传 TTL），数据会一直留在 Redis 里。

* 数据库查不到时没有缓存空值，可能会被反复打到数据库（缓存穿透）。

* 没有考虑缓存更新：数据库里店铺改了，Redis 里的旧数据不会自动变（缓存一致性）。

* 没有处理热点 key、缓存击穿、缓存雪崩这些进阶问题。

> 先把 “能跑通、能看懂” 放在第一位，这些坑以后遇到了再回来填。