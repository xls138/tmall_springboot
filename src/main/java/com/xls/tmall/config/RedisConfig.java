//这个配置的作用主要是使得保存在 redis 里的key和value转换为如图所示的具有可读性的字符串，否则会是乱码，很不便于观察。
package com.xls.tmall.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
//Redis 缓存配置类
public class RedisConfig extends CachingConfigurerSupport {

    //这个注解表明下面的方法将会返回一个对象，该对象要注册为Spring应用上下文中的一个Bean。
    @Bean
    //定义了一个名为cacheManager的方法，它接收一个泛型RedisTemplate作为参数。这个方法配置了Spring缓存管理器。
    public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate) {
        //创建了一个StringRedisSerializer实例，用于字符串的序列化。
        RedisSerializer stringSerializer = new StringRedisSerializer();
        //创建了一个Jackson2JsonRedisSerializer实例，它能将Java对象序列化为JSON字符串。这里指定了Object.class，表示它可以序列化任何Java对象。
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        //创建了一个ObjectMapper实例，用于在Java对象和JSON之间进行转换。
        ObjectMapper om = new ObjectMapper();
        //配置ObjectMapper以只检测公开访问器（getter方法、字段等）。
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        //启用默认类型化，这有助于在反序列化时保留对象的类型信息，特别是对于非final类。
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        //将配置好的ObjectMapper设置给Jackson2JsonRedisSerializer实例，以用于序列化和反序列化操作。
        jackson2JsonRedisSerializer.setObjectMapper(om);
        //设置Redis键（key）的序列化方式为StringRedisSerializer，确保键以字符串形式存储。
        redisTemplate.setKeySerializer(stringSerializer);
        //同样，设置Redis哈希键（hash key）的序列化方式为StringRedisSerializer。
        redisTemplate.setHashKeySerializer(stringSerializer);

        //设置Redis值（value）的序列化方式为Jackson2JsonRedisSerializer，使值以JSON字符串形式存储。
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        //设置Redis哈希值（hash value）的序列化方式也为Jackson2JsonRedisSerializer。
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        //创建了一个RedisCacheManager实例，用于Redis缓存管理，并将配置好的RedisTemplate传递给它。
        CacheManager cacheManager = new RedisCacheManager(redisTemplate);
        //返回配置好的CacheManager实例。
        return cacheManager;

    }
}

