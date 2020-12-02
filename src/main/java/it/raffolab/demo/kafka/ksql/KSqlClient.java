package it.raffolab.demo.kafka.ksql;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;

/**
 * Contain and manage the ksql client
 * 
 * @author Marco Spagnulo
 */
@Component
public class KSqlClient {

	@Value("${ksql.server.host}")
	private String host;
	
	@Value("${ksql.server.port}")
	private int port;

	private final Logger logger = LoggerFactory.getLogger(KSqlClient.class);
	
	Client client;

	/**
	 * Inizitialize the client
	 */
	@PostConstruct
	public void init() {
		logger.info("Create ksql connection");
		ClientOptions options = ClientOptions.create().setHost(host).setPort(port);
		this.client = Client.create(options);
	}

	@PreDestroy
	public Client getClient() {
		return client;
	}

	@PreDestroy
	public void closeConnection() {
		logger.info("Closing ksql connection");
		client.close();
	}

	/**
	 * Execute a void query for keep the connection alive with a fixed rate
	 */
	@Scheduled(fixedRateString="${ksql.server.wakeUpRate}")
	public void wakeUpConnection() {
		
	}
}
