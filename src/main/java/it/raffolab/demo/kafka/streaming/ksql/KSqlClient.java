package it.raffolab.demo.kafka.streaming.ksql;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;

@Component
public class KSqlClient {

	@Value("${ksql.server.host}")
	private String host;
	
	@Value("${ksql.server.port}")
	private int port;

	private final Logger logger = LoggerFactory.getLogger(KSqlClient.class);
	
	Client client;

	@PostConstruct
	public void init() {
		ClientOptions options = ClientOptions.create().setHost(host).setPort(port);
		this.client = Client.create(options);
	}

	public Client getClient() {
		return client;
	}

	@Scheduled(fixedRateString="${ksql.server.wakeUpRate}")
	public void wakeUpConnection() {
		logger.debug("Waking up ksql connection");
		this.getClient().executeQuery("SELECT 1");
	}
}
