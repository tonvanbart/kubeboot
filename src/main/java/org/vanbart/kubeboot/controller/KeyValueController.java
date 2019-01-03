package org.vanbart.kubeboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class KeyValueController {

    private static final Logger log = LoggerFactory.getLogger(KeyValueController.class);

    private RedisTemplate<String, Float> redisTemplate;

    @Autowired
    public KeyValueController(RedisTemplate<String, Float> redisTemplate) {
        log.info("KeyValueController({})", redisTemplate);
        this.redisTemplate = redisTemplate;
    }

    @RequestMapping(value = "/value/{key}", method = GET)
    public Float getValue(@PathVariable("key") String key) {
        log.info("getValue({})", key);
        Optional<Float> result = Optional.ofNullable(redisTemplate.opsForValue().get(key));
        result.ifPresent(res -> log.info("got result: {}", res));
        return result.orElse(-1F);
    }

    @RequestMapping(value = "/value/{key}", method = POST)
    public ResponseEntity saveValue(@PathVariable("key") String key, @RequestParam("value") Float value) {
        log.info("saveValue({},{})", key, value);
        redisTemplate.opsForValue().set(key, value);
        return new ResponseEntity(CREATED);
    }
}