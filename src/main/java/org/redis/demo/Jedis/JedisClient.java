package org.redis.demo.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class JedisClient {

  public static void main(String[] args) {

    // https://www.baeldung.com/jedis-java-redis-client-library

    Jedis jedis = new Jedis();
    jedis.set("events/city/rome", "32,15,223,828");
    String cachedResponse = jedis.get("events/city/rome");
    System.out.println("cached response " + cachedResponse);

    //list
    //The LPUSH command adds a new element into a list, on the left (at the head),
    // while the RPUSH command adds a new element into a list, on the right (at the tail).
    System.out.println("\nlist");
    jedis.lpush("queue#tasks12", "firstTask");
    jedis.lpush("queue#tasks12", "secondTask");

    String task = jedis.rpop("queue#tasks12");
    System.out.println("list rpop " + task);

    // sets
    System.out.println("\nsets");
    jedis.sadd("nicknames", "nickname#1");
    jedis.sadd("nicknames", "nickname#2");
    jedis.sadd("nicknames", "nickname#1");

    Set<String> nicknames = jedis.smembers("nicknames");
    boolean exists = jedis.sismember("nicknames", "nickname#1");
    System.out.println(nicknames);
    System.out.println(String.join(",", nicknames));

    //hashes
    System.out.println("\nhashes");
    jedis.hset("user#1", "name", "Peter");
    jedis.hset("user#1", "job", "politician");

    String name = jedis.hget("user#1", "name");
    System.out.println(name);

    Map<String, String> fields = jedis.hgetAll("user#1");
    System.out.println(fields);
    String job = fields.get("job");

    //sorted sets
    System.out.println("\nsorted sets");
    Map<String, Double> scores = new HashMap<>();

    scores.put("PlayerOne", 3000.0);
    scores.put("PlayerTwo", 1500.0);
    scores.put("PlayerThree", 8200.0);

    scores.entrySet().forEach(playerScore -> {
      jedis.zadd("ranking321", playerScore.getValue(), playerScore.getKey());
    });

    String player = jedis.zrevrange("ranking321", 0, 1).iterator().next();
    long rank = jedis.zrevrank("ranking321", "PlayerOne");
    System.out.println("rank: " + rank);
    System.out.println(String.join(",", jedis.zrange("ranking321", 0, -1)));

    //transactions
    System.out.println("\ntransaction\n");
    String friendsPrefix = "friends#";
    String userOneId = "4352523";
    String userTwoId = "5552321";

    Transaction t = jedis.multi();
    t.sadd(friendsPrefix + userOneId, userTwoId);
    t.sadd(friendsPrefix + userTwoId, userOneId);
    t.exec();

    //You can even make a transaction success dependent on a specific key by
    // “watching” it right before you instantiate your Transaction:
    jedis.watch("friends#deleted#" + userOneId);

    //pipelining
    System.out.println("pipelining");
    String userOneId1 = "4352523";
    String userTwoId2 = "4849888";

    Pipeline p = jedis.pipelined();
    p.sadd("searched#" + userOneId, "paris");
    p.zadd("ranking123", 126, userOneId1);
    p.zadd("ranking123", 325, userTwoId2);
    Response<Boolean> pipeExists = p.sismember("searched#" + userOneId1, "paris");
    Response<Set<String>> pipeRanking = p.zrange("ranking123", 0, -1);
    p.sync();

    Boolean exists2 = pipeExists.get();
    Set<String> ranking = pipeRanking.get();
    System.out.println(exists2);
    System.out.println(String.join(",", ranking));

//    //Publish/subscribe
//    System.out.println("\n\npublish/ subscribe");
//    Jedis jSubscriber = new Jedis();
//    //subscriber
//    jSubscriber.subscribe(new JedisPubSub() {
//      @Override
//      public void onMessage(String channel, String message) {
//        // handle message
//        System.out.println("Channel :" + channel);
//        System.out.println("Message :" + message);
//      }
//    }, "channel");
//
//    //publisher
//    Jedis jPublisher = new Jedis();
//    jPublisher.publish("channel", "test message");

    // more details on Jedis Pool and Redis Cluster in the blog post
    // https://www.baeldung.com/jedis-java-redis-client-library
  }
}
