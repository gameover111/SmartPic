package com.hsc.hsmartpicbackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
public class RedisStringTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedisStringOperations() {

//        stringRedisTemplate.delete("testKey");
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();

        // 1. 使用唯一 key，避免和其他测试冲突
        String key = "testKey_" + System.currentTimeMillis();
        String value = "testValue";

        // 2. 先删除可能残留的旧数据（多一层保险）
        stringRedisTemplate.delete(key);

        // 3. 写入并立即读取，打印出来确认
        valueOps.set(key, value);
        String storedValue = valueOps.get(key);
        System.out.println("实际读取到的值: [" + storedValue + "]");  // 关键：看控制台输出

        // 4. 使用三参数断言（Spring 风格）
        assertEquals("存储的值与预期不一致", value, storedValue);

        // 后续操作：更新值
        String updatedValue = "updatedValue";
        valueOps.set(key, updatedValue);
        storedValue = valueOps.get(key);
        assertEquals("更新后的值与预期不一致", updatedValue, storedValue);

        // 再次查询
        storedValue = valueOps.get(key);
        assertNotNull("查询的值为空", storedValue);
        assertEquals("查询的值与预期不一致", updatedValue, storedValue);

        // 删除后查询
        stringRedisTemplate.delete(key);
        storedValue = valueOps.get(key);
        assertNull("删除后的值不为空", storedValue);
    }
}