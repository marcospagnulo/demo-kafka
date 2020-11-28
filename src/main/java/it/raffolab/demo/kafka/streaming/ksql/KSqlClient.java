package it.raffolab.demo.kafka.streaming.ksql;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;

@Component
public class KSqlClient {

	@Value("${ksql.server.host}")
	private String host;
	
	@Value("${ksql.server.port}")
	private int port;

	Client client;

	@PostConstruct
	public void init() {
		ClientOptions options = ClientOptions.create().setHost(host).setPort(port);
		this.client = Client.create(options);
	}

	public Client getClient() {
		return client;
	}

}
