package org.redis.demo.RateLimiterJedis.Main;

import java.util.Properties;


/*
 * Follows the factory method pattern to instantiate 
 * the appropriate object.
 * 
 */
public class RateLimiterFactory {

		
	public static RateLimiter getRateLimiter(Properties props) throws Exception {

		RateLimiter rateLimiter = null;

		String type = props.getProperty("type");
		switch (type) {
			case Constants.DISTRIBUTION_TYPE_UNIFORM:
				rateLimiter = new UniformRateLimiter(props);
				break;
			case Constants.DISTRIBUTION_TYPE_BURSTY:
				rateLimiter = new BurstyRateLimiter(props);
				break;
			case Constants.DISTRIBUTION_TYPE_GENERIC_CELL:
				rateLimiter = new SimpleCellRateLimiter(props);
				break;
			case Constants.DISTRIBUTION_TYPE_SLIDING_WINDOW:
				rateLimiter = new SlidingWindowRateLimiter(props);
				break;
		}

		return rateLimiter;
	}
}