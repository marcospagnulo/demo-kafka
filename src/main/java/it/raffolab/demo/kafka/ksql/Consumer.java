package it.raffolab.demo.kafka.ksql;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Check for update on a topic and create a subscription for manage updates
 * 
 * @author Marco Spagnulo
 */
@Component
public class Consumer {

	private final Logger logger = LoggerFactory.getLogger(Consumer.class);

	@Autowired
	KSqlClient client;

	/**
	 * Create a streamed query on client and link this to a subscription 
	 */
	@PostConstruct
	public void init() {
		client.getClient().streamQuery("SELECT * FROM riderLocations EMIT CHANGES;").thenAccept(streamedQueryResult -> {
			logger.debug("Query has started. Query ID: " + streamedQueryResult.queryID());
			RowSubscriber subscriber = new RowSubscriber();
			streamedQueryResult.subscribe(subscriber);
		}).exceptionally(e -> {
			logger.debug("Request failed: " + e);
			e.printStackTrace();
			return null;
		});
	}
}
