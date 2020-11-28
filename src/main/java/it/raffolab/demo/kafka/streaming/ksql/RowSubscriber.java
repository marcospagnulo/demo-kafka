package it.raffolab.demo.kafka.streaming.ksql;

import io.confluent.ksql.api.client.Row;
import it.raffolab.demo.kafka.streaming.Consumer;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RowSubscriber implements Subscriber<Row> {

	private final Logger logger = LoggerFactory.getLogger(Consumer.class);

	private Subscription subscription;

	public RowSubscriber() {
	}

	@Override
	public synchronized void onSubscribe(Subscription subscription) {
		logger.debug("Subscriber is subscribed.");
		this.subscription = subscription;

		// Request the first row
		subscription.request(1);
	}

	@Override
	public synchronized void onNext(Row row) {
		logger.debug("Received a row!");
		logger.debug("Row: " + row.values());

		// Request the next row
		subscription.request(1);
	}

	@Override
	public synchronized void onError(Throwable t) {
		logger.debug("Received an error: " + t);
	}

	@Override
	public synchronized void onComplete() {
		logger.debug("Query has ended.");
	}
}