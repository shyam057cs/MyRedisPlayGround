package org.redis.demo.RateLimiterJedis.Main;

import java.util.Properties;
import java.util.stream.IntStream;

/*
 * This is inspired by CloudFare's rate limiter
 * https://blog.cloudflare.com/counting-things-a-lot-of-different-things/
 *
 */
public class SlidingWindowRateLimiter extends RateLimiter {

  private final String key = "SlidingWindowRateLimiter"; //default key

  // window in seconds
  private int window = 60;
  private int actions = 4;

  /*
   * @param props: parameters within props are window and actions.
   *               These properties define how many actions are allowed
   *               within a window.
   */
  public SlidingWindowRateLimiter(Properties props) throws Exception {
    super();
    System.out.println("\n" + key);
    // deleting any previous data
    IntStream.range(0, 60).forEach(i -> jedis.del(key + i));

    String windowStr = props.getProperty("window");
    if (windowStr != null) {
      try {
        window = Integer.parseInt(windowStr);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    String actionsStr = props.getProperty("actions");
    if (actionsStr != null) {
      try {
        actions = Integer.parseInt(actionsStr);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    System.out.println("\nWindow:" + window + ", Actions: " + actions);
  }


  // Returns true if the arrival of the cell is within the acceptance
  // rate, false if not.
  public boolean arrival(String cell) {

    long currentTime = System.currentTimeMillis();
    long currentTimeInSecond = System.currentTimeMillis() / 1000;
    // current point in window , will be current second if window is 60
    long curSecond = currentTimeInSecond % window;
    // current point in window , will be current minute if window is 60
    long curMinute = (currentTimeInSecond / window) % window;
    long pastMinute = (curMinute + window - 1) % window;

    String currentKey = key + curMinute;
    String pastKey = key + pastMinute;

    jedis.incr(currentKey);
    String pastVal = jedis.get(pastKey);
    long pastCounter = 0;
    if (pastVal != null) {
      pastCounter = Long.parseLong(pastVal);
    }
    long currentCounter = Long.parseLong(jedis.get(currentKey));

    // this is an approximation, for cur second = 30, and if requests came in last 30 second of
    // past minute, we will take half of that and add to counter instead of adding full
    // but it works for most cases - 400,000 requests per second to a single domain
    long currentRate = (long) (pastCounter * ((window - curSecond) / (window * 1.0))
        + currentCounter);

    System.out.println("\nCurrent minute:" + curMinute);
    System.out.println("Current second:" + curSecond);
    System.out.println("Current Key :" + currentKey + ", current counter: " + currentCounter);
    System.out.println("Past Key :" + pastKey + ", past counter: " + pastCounter);
    System.out.println("Current Rate : " + currentRate);

    if (currentRate <= actions) {
      return true;
    }
    jedis.decr(currentKey);
    return false;
  }
}