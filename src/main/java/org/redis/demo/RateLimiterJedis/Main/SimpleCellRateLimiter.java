package org.redis.demo.RateLimiterJedis.Main;

import java.util.Properties;
import org.redis.demo.RateLimiterJedis.client.RedisConnection;
import redis.clients.jedis.Jedis;


/*
 * This implementation is an example of a limit enforcer -- it queues all
 * incoming cells in a Redis List. An independent thread pops the cell
 * when it is within the rate limits.
 *
 * Note: Replace "Take action here....." and implement your own logic
 *
 */
public class SimpleCellRateLimiter extends RateLimiter implements Runnable {

	private final String key = "GenericCellRateLimiter"; //default key

	private RateLimiter uniformRateLimiter = null;

	public SimpleCellRateLimiter(Properties props) throws Exception {
		super();

		System.out.println("\n" + key);
		// deleting any previous data
		jedis.del(key);
		props.setProperty("type", Constants.DISTRIBUTION_TYPE_UNIFORM);
		uniformRateLimiter = RateLimiterFactory.getRateLimiter(props);

		Thread t = new Thread(this);
		t.start();
	}


	// This arrival method is different from the previous two examples.
	// In this example, the program queues up the cell in a List
	// data structure. A separate thread checks whether popping the cell
	// is within the acceptance rate.
	public boolean arrival(String cell) {
		System.out.println(key + ":In: " + (System.currentTimeMillis() / 1000) + " - " + cell);
		jedis.lpush(key, cell);
		return true;
	}

	public void run() {

		try {
			RedisConnection conn = RedisConnection.getRedisConnection();
			Jedis jedis = conn.getJedis();

			while (true) {
				String cell = jedis.rpop(key);
				boolean success = false;
				while (!success) {
					success = uniformRateLimiter.arrival(cell);
					if (success) {

						// Take action here.....						

						System.out.println(
								key + "-Cleanup thread: Out: " + (System.currentTimeMillis() / 1000) + " " + cell);
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}