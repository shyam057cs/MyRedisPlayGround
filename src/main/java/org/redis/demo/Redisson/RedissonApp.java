package org.redis.demo.Redisson;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * Redis based demo application on Java
 *
 */
public class RedissonApp
{
  public static void main( String[] args )
  {
    Config config = new Config();
    // use single Redis server
    config.useSingleServer().setAddress("redis://127.0.0.1:6379");
    RedissonClient redisson = Redisson.create(config);
    // perform operations
    RBucket<String> bucket = redisson.getBucket("simpleObject");
    bucket.set("This is object value");
    RMap<String, String> map = redisson.getMap("simpleMap");
    map.put("mapKey", "This is map value");
    String objectValue = bucket.get();
    System.out.println("stored object value: " + objectValue);
    String mapValue = map.get("mapKey");
    System.out.println("stored map value: " + mapValue);
    redisson.shutdown();
  }
}
