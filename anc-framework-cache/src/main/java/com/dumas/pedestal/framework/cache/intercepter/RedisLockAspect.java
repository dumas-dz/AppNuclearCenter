package com.dumas.pedestal.framework.cache.intercepter;


import com.dumas.pedestal.framework.cache.RedisCache;
import com.dumas.pedestal.framework.cache.annotation.RedisLock;
import java.lang.reflect.Method;
import java.util.UUID;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Redis分布式锁 AOP
 *
 * @author andaren
 * @version V1.0
 * @since 2020-11-30 09:06
 */
@Aspect
@Component
public class RedisLockAspect {
    private static final Logger LOG = LoggerFactory.getLogger(RedisLockAspect.class);

    @Resource
    private RedisCache redisCache;

    // 配置织入点
    @Pointcut("@annotation(com.dumas.pedestal.framework.cache.annotation.RedisLock)")
    public void redisLockPointCut() {
    }

    @Around("redisLockPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        /**
         * TODO 如果需要 key值或者Value值 跟业务相关联，比如跟订单号关联
         *      则注解中可以添加业务属性
         *
         * {@code
         * @LockAnnotation(lockField = "lock", lockTime = 10, lockKey = "'orderSn='+#orderSn")
         * public void doBiz(String orderNo) {
         *     ......
         * }
         * }
         */
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        String value = UUID.randomUUID().toString();
        String key = redisLock.key();
        try {
            final boolean islock = redisCache.lock(key, value, redisLock.expire(), redisLock.timeUnit());
            LOG.info("redis key[{}] lock result: {}", key, islock);
            if (!islock) {
                LOG.error("获取锁失败");
                throw new RuntimeException("获取锁失败");
            }
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throw new RuntimeException("系统异常");
            }
        } finally {
            redisCache.unLockWithLua(key, value);
            LOG.info("释放锁");
        }
    }
}
