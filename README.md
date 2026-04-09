# AppNuclearCenter

> 应用核心模块项目 — 基于 Spring Boot 3.x / Spring Cloud 的企业级基础框架模板

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/JDK-17-green.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/Dependabot-0%20alerts-success.svg)](https://github.com/dumas-dz/AppNuclearCenter/security)

## 项目简介

AppNuclearCenter 是一套企业级 Java 基础框架模板，提供微服务架构中常用的公共能力封装，包括加解密、缓存、数据库访问、消息队列、API 文档、分布式锁、日志追踪等。各模块可独立引入，按需组合。

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
| 对象存储 | 阿里云 OSS | 3.18.3 |
| API 文档 | Swagger (Springfox) | 2.10.0 |
| 序列化 | Jackson / Protobuf | 2.18.3 / 4.32.0 |
| 工具库 | Hutool / Guava / Apache Commons | 5.8.34 / 33.1.0 / 多版本 |
| JDK | Java 17 | - |

## 模块架构

```
AppNuclearCenter
├── anc-parent                    # 依赖管理（根 POM）
├── anc-common-model              # 通用数据模型
├── anc-common-util               # 通用工具集
├── anc-framework-cache           # Redis 缓存 + 分布式锁
├── anc-framework-config-center   # 配置中心基础封装
├── anc-framework-discovery       # 注册中心基础封装
├── anc-framework-feign           # Feign 远程调用封装
├── anc-framework-logger          # 业务日志框架
├── anc-framework-mongodb         # MongoDB 工具封装
├── anc-framework-mq              # 消息队列框架封装
├── anc-framework-mybatis         # MyBatis-Plus + AES 加密
├── anc-framework-springboot      # SpringBoot 核心工具
├── anc-framework-springboot-web  # Web 层（文件上传/日志/版本控制）
├── anc-framework-swagger         # Swagger API 文档
├── anc-framework-validation      # 自定义校验注解
└── docs                          # 项目文档
```

## 模块详细说明

### anc-parent

根 POM 模块，统一管理所有子模块和第三方依赖的版本号。所有业务项目继承此 POM 即可自动获得一致的依赖版本。

### anc-common-model

通用数据模型，定义了 API 响应结构、RPC 结果包装和异常体系。

| 类 | 说明 |
|----|------|
| `R` / `Ok` / `Fail` | API 统一响应包装（成功/失败） |
| `Result` / `PageResult` / `ListResult` | RPC 结果包装（单条/分页/列表） |
| `ServiceException` | 业务异常基类 |
| `ResponseCode` | 响应码接口 |
| `PageInfoDTO` / `ListInfoDTO` | 分页/列表数据传输对象 |

### anc-common-util

通用工具集，提供加解密、JSON、HTTP、文件、反射、日期等基础能力。

| 工具类 | 说明 |
|--------|------|
| `AESUtil` | AES 加解密（GCM 推荐 / ECB 兼容） |
| `RSAUtil` | RSA 非对称加解密（2048-bit + OAEP） |
| `Md5Utils` / `Md5Encrypt` | MD5 哈希 |
| `SignUtil` | 签名生成（MD5 签名、大数据签名、短信签名） |
| `JacksonUtil` | JSON 序列化/反序列化 |
| `FileUtil` | 文件读写、删除、大小格式化 |
| `CodecUtil` | 编解码（Base64、URL 编码、DES-已废弃） |
| `SqlUtil` | SQL 注入防护（order by 参数校验） |
| `EscapeUtil` | HTML 转义/反转义/XSS 清理 |
| `VerifyCodeUtils` | 图形验证码生成 |
| `BeanUtil` | 对象拷贝（浅拷贝/深拷贝） |
| `ReflectUtils` | 反射工具（getter/setter/私有字段访问） |
| `DateUtil` / `TimeUtil` / `DateFormatUtil` | 日期时间工具 |
| `IdUtils` | ID 生成工具 |
| `Threads` | 线程池管理工具 |

### anc-framework-cache

Redis 缓存框架封装，支持多种数据结构和分布式锁。

| 类 | 说明 |
|----|------|
| `RedisConfig` | Redis 连接配置（@Configuration） |
| `ValueRedisCache` | String 类型缓存操作 |
| `HashRedisCache` | Hash 类型缓存操作 |
| `ListRedisCache` | List 类型缓存操作 |
| `SetRedisCache` | Set 类型缓存操作 |
| `@RedisLock` | 分布式锁注解（基于 AOP） |
| `RedisLockHelper` | 分布式锁工具类 |

### anc-framework-mybatis

MyBatis-Plus 数据库访问层封装，集成字段加密和分页。

| 类 | 说明 |
|----|------|
| `MyBatisPlusConfig` | MyBatis-Plus 配置（分页插件、事务管理） |
| `AES` | AES 字段加密组件（CBC 模式，密钥配置化） |
| `AESEncryptHandler` | MyBatis 字段加密 TypeHandler |
| `AutoFillConfiguration` | 自动填充配置（创建人/更新人） |
| `PageUtil` | 分页工具 |
| `DbGuardConfigFilter` | 数据库密码解密过滤器 |

### anc-framework-springboot-web

Web 层基础封装，提供文件上传、操作日志、API 版本控制。

| 类 | 说明 |
|----|------|
| `FileUploadUtils` | 文件上传工具（大小/类型/路径遍历校验） |
| `@Log` | 操作日志自定义注解 |
| `OperationLog` / `OperationLogHandler` | 操作日志模型与处理接口 |
| `@ApiVersion` | API 版本控制注解 |
| `ApiVersioningRequestMappingHandlerMapping` | API 版本路由映射 |

### anc-framework-springboot

SpringBoot 核心工具。

| 类 | 说明 |
|----|------|
| `SpringContextHolder` | Spring 上下文持有者（获取 Bean） |
| `AsynWorkPoolConfiguration` | 异步线程池配置 |

### anc-framework-swagger

Swagger API 文档自动生成。

| 类 | 说明 |
|----|------|
| `SwaggerConfig` | Swagger 配置（自动扫描 Controller 生成文档） |
| `SwaggerWebMvcConfig` | Web MVC 资源映射 |

支持通过配置属性自定义标题、描述、包扫描路径和 Token 认证。

### anc-framework-validation

自定义 Bean Validation 注解。

| 注解 | 说明 |
|------|------|
| `@PhoneNumber` | 手机号格式校验 |
| `@IPAddress` | IP 地址格式校验 |
| `@InArray` | 整数数组包含校验 |
| `@InStringArray` | 字符串数组包含校验 |

### anc-framework-mongodb

MongoDB 查询工具。

| 类 | 说明 |
|----|------|
| `MongoLikeUtil` | 模糊查询工具（左模糊/右模糊/全模糊/精确匹配） |

### anc-framework-logger

业务日志框架。

| 类 | 说明 |
|----|------|
| `BusinessLogger` | 业务日志记录器 |
| `EventLogger` | 事件日志记录器 |
| `HostLogConfig` | 主机日志配置 |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+

### 编译安装

```bash
# 编译
mvn compile

# 安装到本地仓库
mvn install -DskipTests
```

### 引入依赖

在业务项目 `pom.xml` 中继承父 POM：

```xml
<parent>
    <groupId>com.dumas.anc</groupId>
    <artifactId>anc-parent</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</parent>
```

按需引入模块：

```xml
<!-- Web 项目（含 SpringBoot 核心 + 文件上传 + 日志） -->
<dependency>
    <groupId>com.dumas.anc</groupId>
    <artifactId>anc-framework-springboot-web</artifactId>
</dependency>

<!-- 数据库访问（MyBatis-Plus + 分页 + AES 加密） -->
<dependency>
    <groupId>com.dumas.anc</groupId>
    <artifactId>anc-framework-mybatis</artifactId>
</dependency>

<!-- Redis 缓存 + 分布式锁 -->
<dependency>
    <groupId>com.dumas.anc</groupId>
    <artifactId>anc-framework-cache</artifactId>
</dependency>

<!-- Swagger API 文档 -->
<dependency>
    <groupId>com.dumas.anc</groupId>
    <artifactId>anc-framework-swagger</artifactId>
</dependency>
```

## 配置参考

### AES 字段加密

使用 MyBatis AES 字段加密时，**必须**在配置文件中指定密钥：

```properties
# 启用 AES 加密
aes.encrypt.enabled=true
# 密钥（32 位十六进制字符 = 128 bit）
aes.mobile.secret=your-32-char-hex-key-here
```

在实体类字段上使用：

```java
@TableField(typeHandler = AESEncryptHandler.class)
private String phone;
```

### Swagger 文档

```properties
# 启用 Swagger
swagger.enable=true
swagger.title=我的API文档
swagger.description=项目接口说明
swagger.base-package=com.example.controller
```

### 分布式锁

```java
@RedisLock(key = "order:#{#orderId}", expire = 30)
public void processOrder(Long orderId) {
    // 业务逻辑
}
```

### 自定义校验注解

```java
public class UserRequest {
    @PhoneNumber(message = "手机号格式不正确")
    private String phone;

    @IPAddress(message = "IP 地址格式不正确")
    private String ipAddress;
}
```

## 安全说明

### 加密工具对照

| 工具类 | 算法 | 推荐场景 |
|--------|------|----------|
| `AESUtil.encryptString` | AES/GCM/NoPadding | 字符串加解密（推荐） |
| `AESUtil.encryptBytesGCM` | AES/GCM/NoPadding | 字节流加解密（推荐） |
| `AESUtil.encryptBytes` | AES/ECB/NoPadding | 兼容已有二进制协议 |
| `RSAUtil` | RSA 2048-bit + OAEP | 非对称加解密、数字签名 |
| `DESUtil` | DES/ECB | **已废弃** - 请使用 AESUtil |
| `CodecUtil` | DES/ECB + MD5 | **已废弃** - 请使用 AESUtil + SHA-256 |

### 安全防护措施

| 防护项 | 实现 |
|--------|------|
| SQL 注入 | `SqlUtil` — order by 参数正则白名单校验 |
| XSS 攻击 | `EscapeUtil` — HTML 特殊字符转义 |
| 路径遍历 | `FileUploadUtils` — `FilenameUtils.getName()` 清洗文件名 |
| 文件上传 | 大小限制（50MB）、扩展名白名单校验 |
| 反序列化 | `BeanUtil.deepCopy` — `ObjectInputFilter` 类型白名单 |
| 验证码安全 | `VerifyCodeUtils` — `SecureRandom` 生成 |
| 敏感日志 | `SignUtil` — 签名密钥脱敏，日志级别降为 DEBUG |

## 许可证

[Apache License 2.0](LICENSE)
