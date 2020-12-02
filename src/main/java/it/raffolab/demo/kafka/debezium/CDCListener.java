package it.raffolab.demo.kafka.debezium;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;

/**
 * This class creates, starts and stops the EmbeddedEngine, which starts the Debezium engine. The engine also
 * loads and launches the connectors setup in the configuration.
 *
 * @author Marco Spagnulo
 */
@Component
public class CDCListener {

	@Value("${debezium.name}")
	private String engineName;

	@Value("${debezium.connector.class}")
	private String connectorClass;

	@Value("${debezium.offset.storage.class}")
	private String storageClass;

	@Value("${debezium.offset.storage.file.filename}")
	private String storageFileName;

	@Value("${debezium.offset.flush.interval.ms}")
	private String flushInterval;

	@Value("${debezium.database.hostname}")
	private String hostname;
	
	@Value("${debezium.database.port}")
	private String port;
	
	@Value("${debezium.database.user}")
	private String user;
	
	@Value("${debezium.database.password}")
	private String password;
	
	@Value("${debezium.database.table.include.list}")
	private String tableIncludeList;
	
	@Value("${debezium.database.server.id}")
	private String serverId;
	
	@Value("${debezium.database.server.name}")
	private String serverName;
	
	@Value("${debezium.database.history.class}")
	private String databaseHistoryClass;
	
	@Value("${debezium.database.history.file.filename}")
	private String databaseHistoryFile;
	
	private final Logger logger = LoggerFactory.getLogger(CDCListener.class);
	
    /**
     * Single thread pool which will run the Debezium engine asynchronously.
     */
    private final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * The Debezium engine which needs to be loaded with the configurations, Started and Stopped - for the
     * CDC to work.
     */
    private DebeziumEngine<ChangeEvent<String, String>> engine;
    
    @PostConstruct
    private void start() {
    	
    	Properties props = new Properties();
    	props.setProperty("name", engineName);
    	props.setProperty("connector.class", connectorClass);
        props.setProperty("offset.storage", storageClass);
        props.setProperty("offset.storage.file.filename", storageFileName);
        props.setProperty("offset.flush.interval.ms", flushInterval);
    	props.setProperty("database.hostname", hostname);
    	props.setProperty("database.port", port);
    	props.setProperty("database.user", user);
    	props.setProperty("database.password", password);
    	props.setProperty("table.include.list", tableIncludeList);
    	props.setProperty("database.server.id", serverId);
    	props.setProperty("database.server.name", serverName);
    	props.setProperty("database.history", databaseHistoryClass);
    	props.setProperty("database.history.file.filename", databaseHistoryFile);
    	
    	this.engine = DebeziumEngine.create(Json.class)
    	        .using(props)
    	        .notifying(this::handleEvent)
    	        .build();
    	
        this.executor.execute(engine);
    }
    
    @PreDestroy
    private void destroy() {
    	try {
			this.engine.close();
		} catch (IOException e) {
			logger.error("Unable to close debezium connector");
		}
    }

    private void handleEvent(ChangeEvent<String,String> c) {
    	logger.debug("Data changed " + c);
    }
}
