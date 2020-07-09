package org.redis.demo.RateLimiterJedis.Main;

import java.util.Properties;


/*
 * This is the main class for testing the framework
 */
public class RateLimiterMain {
	
	public static void main(String[] args) throws Exception {
		uniformRateLimiterTest();
		burstyRateLimiterTest();
		genericCellRateLimiterTest();
	}
	
	private static void genericCellRateLimiterTest() throws Exception {
		Properties props = new Properties();

		props.setProperty("type", Constants.DISTRIBUTION_TYPE_GENERIC_CELL);
		props.setProperty("window", "3600");
		props.setProperty("actions", "1800");

		RateLimiter rateLimiter = RateLimiterFactory.getRateLimiter(props);

		rateLimiter.arrival("test 1");
		rateLimiter.arrival("test 2");
		rateLimiter.arrival("test 3");
		rateLimiter.arrival("test 4");
		rateLimiter.arrival("test 5");
		rateLimiter.arrival("test 6");
		rateLimiter.arrival("test 7");
		rateLimiter.arrival("test 8");
		rateLimiter.arrival("test 9");
		rateLimiter.arrival("test 10");
		
	}
	
	private static void uniformRateLimiterTest() throws Exception {
		Properties props = new Properties();

		props.setProperty("type", Constants.DISTRIBUTION_TYPE_UNIFORM);
		props.setProperty("window", "5");
		props.setProperty("actions", "2");

		RateLimiter rateLimiter = RateLimiterFactory.getRateLimiter(props);

		boolean a = rateLimiter.arrival("test 1");
		System.out.println("Result 1: " + a);

		boolean b = rateLimiter.arrival("test 2");
		System.out.println("Result 2: " + b);

		boolean c = rateLimiter.arrival("test 3");
		System.out.println("Result 3: " + c);

		Thread.sleep(5000);

		boolean d = rateLimiter.arrival("test 4");
		System.out.println("Result 4: " + d);

	}	
	
	private static void burstyRateLimiterTest() throws Exception {
		Properties props = new Properties();

		props.setProperty("type", Constants.DISTRIBUTION_TYPE_BURSTY);
		props.setProperty("window", "5");
		props.setProperty("actions", "2");

		RateLimiter rateLimiter = RateLimiterFactory.getRateLimiter(props);

		boolean a = rateLimiter.arrival("test 1");
		System.out.println("Result 1: " + a);

		boolean b = rateLimiter.arrival("test 2");
		System.out.println("Result 2: " + b);

		boolean c = rateLimiter.arrival("test 3");
		System.out.println("Result 3: " + c);

		Thread.sleep(5000);

		boolean d = rateLimiter.arrival("test 4");
		System.out.println("Result 4: " + d);

	}
}