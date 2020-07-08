package org.redis.demo.Jedis;

import redis.clients.jedis.Jedis;

public class JedisClient {

  public static void main(String[] args) {
    Jedis jedis = new Jedis();
    jedis.set("events/city/rome", "32,15,223,828");
    String cachedResponse = jedis.get("events/city/rome");

  }
}
