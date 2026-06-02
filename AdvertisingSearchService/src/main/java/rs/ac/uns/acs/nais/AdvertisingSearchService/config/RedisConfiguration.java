package rs.ac.uns.acs.nais.AdvertisingSearchService.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdPhaseResponse;
import rs.ac.uns.acs.nais.AdvertisingSearchService.dto.response.AdTypeResponse;

import java.time.Duration;
import java.util.List;

@Configuration
public class RedisConfiguration implements CachingConfigurer {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheConfiguration defaultConfig = cacheConfiguration(Duration.ofMinutes(10));

        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(defaultConfig.disableCachingNullValues())
                .withCacheConfiguration("adTypesAll", listCacheConfiguration(Duration.ofMinutes(15), AdTypeResponse.class))
                .withCacheConfiguration("adPhasesAll", listCacheConfiguration(Duration.ofMinutes(15), AdPhaseResponse.class))
                .build();
    }

    private RedisCacheConfiguration cacheConfiguration(Duration duration) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(duration)
                .serializeValuesWith(SerializationPair.fromSerializer(serializer));
    }

    private <T> RedisCacheConfiguration listCacheConfiguration(Duration duration, Class<T> elementType) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
        Jackson2JsonRedisSerializer<List<T>> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, listType);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(duration)
                .serializeValuesWith(SerializationPair.fromSerializer(serializer));
    }
}
