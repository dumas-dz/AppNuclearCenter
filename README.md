# AppNuclearCenter

应用核心模块项目 — 基于 Spring Boot 3.x / Spring Cloud 的企业级基础框架模板。

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 基础框架 | Spring Boot | 3.5.5 |
| 微服务 | Spring Cloud | 2023.0.6 |
| 微服务(阿里) | Spring Cloud Alibaba | 2023.0.1.0 |
| 注册/配置中心 | Nacos | 2.4.3 |
| ORM | MyBatis-Plus | 3.5.14 |
| 数据库 | MySQL | 9.4.0 |
| 连接池 | Druid | 1.2.27 |
| 缓存 | Redis (Spring Data) | - |
| 消息队列 | RocketMQ | 5.3.3 |
| 文档 | Swagger (Springfox) | 2.10.0 |
| 工具 | Hutool / Guava / Jackson | 5.8.34 / 33.1.0 / 2.18.3 |
| JDK | Java 17 | - |

## 模块说明

| 模块名称 | 说明 |
|----------|------|
| anc-parent | 依赖管理，根 POM |
| anc-common-model | 通用数据模型（API VO、RPC Result、异常定义） |
| anc-common-util | 通用工具（加解密、JSON、文件、反射、SQL 防注入） |
| anc-framework-cache | Redis 缓存框架封装（Value / Hash / List / Set / 分布式锁） |
| anc-framework-config-center | 配置中心基础封装 |
| anc-framework-discovery | 注册中心基础封装 |
| anc-framework-feign | Feign 远程调用封装 |
| anc-framework-logger | 业务日志框架封装 |
| anc-framework-mongodb | MongoDB 存储封装 |
| anc-framework-mq | 消息队列框架封装 |
| anc-framework-mybatis | 数据库查询层封装（MyBatis-Plus、AES 字段加密、分页） |
| anc-framework-springboot | SpringBoot 基础框架封装 |
| anc-framework-springboot-web | Web 项目基础封装（文件上传、操作日志拦截、API 版本控制） |
| anc-framework-swagger | Swagger 在线接口文档封装 |
| anc-framework-validation | 数据校验框架封装 |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+

### 编译

```bash
mvn compile
```

### 使用

在业务项目的 `pom.xml` 中引入所需模块：

```xml
<parent>
    <groupId>com.dumas.anc</groupId>
    <artifactId>anc-parent</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</parent>

<dependency>
    <groupId>com.dumas.anc</groupId>
    <artifactId>anc-framework-springboot-web</artifactId>
</dependency>
```

## 安全说明

### 加密工具

| 工具类 | 算法 | 状态 | 建议 |
|--------|------|------|------|
| `AESUtil` | AES/GCM (推荐) / AES/ECB (兼容) | 推荐 GCM 模式 | 新代码使用 `encryptString` / `decryptBase64Str` |
| `RSAUtil` | RSA 2048-bit + OAEP | 安全 | - |
| `DESUtil` | DES/ECB | **@Deprecated** | 请使用 `AESUtil` 替代 |
| `CodecUtil` | DES/ECB + MD5 | **@Deprecated** | 请使用 `AESUtil` + SHA-256 替代 |

### AES 字段加密配置

使用 MyBatis AES 字段加密时，**必须**在配置文件中指定密钥：

```properties
aes.encrypt.enabled=true
aes.mobile.secret=<your-32-char-hex-key>
```

### 已修复的安全问题

- 文件上传路径遍历防护（`FilenameUtils.getName()`）
- SQL 注入防护（`SqlUtil` 正则校验）
- HTML XSS 防护（`EscapeUtil` 转义）
- 验证码使用 `SecureRandom` 生成
- 反序列化攻击防护（`ObjectInputFilter` 白名单）
- 敏感数据日志脱敏