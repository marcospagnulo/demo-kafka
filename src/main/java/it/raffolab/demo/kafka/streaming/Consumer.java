package it.raffolab.demo.kafka.streaming;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.raffolab.demo.kafka.streaming.ksql.KSqlClient;
import it.raffolab.demo.kafka.streaming.ksql.RowSubscriber;

@Component
public class Consumer {

	private final Logger logger = LoggerFactory.getLogger(Consumer.class);

	@Autowired
	KSqlClient client;

	@PostConstruct
	public void init() {
		
		client.getClient().streamQuery("SELECT * FROM riderLocations EMIT CHANGES;").thenAccept(streamedQueryResult -> {
			logger.debug("Query has started. Query ID: " + streamedQueryResult.queryID());
			RowSubscriber subscriber = new RowSubscriber();
			streamedQueryResult.subscribe(subscriber);
		}).exceptionally(e -> {
			logger.debug("Request failed: " + e);
			return null;
		});
	}
}
