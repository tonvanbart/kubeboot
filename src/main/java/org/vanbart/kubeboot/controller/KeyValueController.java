package org.vanbart.kubeboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class KeyValueController {

    private static final Logger log = LoggerFactory.getLogger(KeyValueController.class);

    private RedisTemplate<String, Float> redisTemplate;

    @Value("${MY_POD_IP:not_found}")
    private String myPodIp;

    @Autowired
    public KeyValueController(RedisTemplate<String, Float> redisTemplate) {
        log.info("KeyValueController({})", redisTemplate);
        this.redisTemplate = redisTemplate;
    }

    @RequestMapping(value = "/value/{key}", method = GET)
    public ResponseEntity<Float> getValue(@PathVariable("key") String key) {
        log.info("getValue({})", key);
        Float result = redisTemplate.opsForValue().get(key);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Pod-IP", myPodIp);
        if (result != null) {
            return ResponseEntity.ok().headers(headers).body(result);
        } else {
            return ResponseEntity.notFound().headers(headers).build();
        }
    }

    @RequestMapping(value = "/value/{key}", method = POST)
    public ResponseEntity saveValue(@PathVariable("key") String key, @RequestParam("value") Float value) {
        log.info("saveValue({},{})", key, value);
        redisTemplate.opsForValue().set(key, value);
        return new ResponseEntity(CREATED);
    }
}