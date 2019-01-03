package org.vanbart.kubeboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
public class KubebootApplication {

	private static final Logger log = LoggerFactory.getLogger(KubebootApplication.class);

	@Bean
	public RedisTemplate<String, Float> redisTemplate(RedisConnectionFactory redisConnectionFactory, RedisProperties redisProperties) {
		log.info("Connecting to Redis on {}:{}", redisProperties.getHost(), redisProperties.getPort());
		RedisTemplate<String, Float> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new FloatRedisSerializer());
		return template;
	}

	public static void main(String[] args) {
		SpringApplication.run(KubebootApplication.class, args);
	}


	private class FloatRedisSerializer implements RedisSerializer<Float> {

		private StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

		@Override
		public byte[] serialize(Float aFloat) throws SerializationException {
			return stringRedisSerializer.serialize(Float.toString(aFloat));
		}

		@Override
		public Float deserialize(byte[] bytes) throws SerializationException {
			if (bytes == null) {
				return null;
			}
			return Float.parseFloat(stringRedisSerializer.deserialize(bytes));
		}
	}

}

