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

	@Value("${debezium.db_type}")
	private String dbType;

	@Value("${debezium.connector.class}")
	private String connectorClass;

	@Value("${debezium.database.connection.adapter}")
	private String connectionAdapter;

	@Value("${debezium.database.schema}")
	private String schema;
	
	@Value("${debezium.tasks.max}")
	private String tasksMax;
	
	@Value("${debezium.database.server.name}")
	private String serverName;
	
	@Value("${debezium.database.tablename.case.insensitive}")
	private String tableNameCaseInsensitive;
	
	@Value("${debezium.database.hostname}")
	private String hostname;
	
	@Value("${debezium.database.port}")
	private String port;
	
	@Value("${debezium.database.user}")
	private String user;
	
	@Value("${debezium.database.password}")
	private String password;
	
	@Value("${debezium.database.dbname}")
	private String dbName;
	
	@Value("${debezium.database.pdb.name}")
	private String pdbName;
	
	@Value("${debezium.database.out.server.name}")
	private String outServerName;
	
	@Value("${debezium.database.history.kafka.bootstrap.servers}")
	private String kafkaBootstrapServers;
	
	@Value("${debezium.database.history.kafka.topic}")
	private String kafkaTopic;
	
	@Value("${debezium.database.history.skip.unparseable.ddl}")
	private String skipUnparseableDdl;
	
	@Value("${debezium.include.schema.changes}")
	private String includeSchemaChanges;
	
	@Value("${debezium.table.include.list}")
	private String tableIncludeList;
	
	@Value("${debezium.errors.log.enable}")
	private String errorsLogEnable;

	@Value("${debezium.offset.storage.class}")
	private String storageClass;

	@Value("${debezium.offset.storage.file.filename}")
	private String storageFileName;

	@Value("${debezium.offset.flush.interval.ms}")
	private String flushInterval;
	
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
    	props.setProperty("database.connection.adapter", connectionAdapter);
    	props.setProperty("database.hostname", hostname);
    	props.setProperty("database.port", port);
    	props.setProperty("database.user", user);
    	props.setProperty("database.password", password);
    	props.setProperty("database.history.kafka.bootstrap.servers", kafkaBootstrapServers);
    	props.setProperty("database.history.kafka.topic", kafkaTopic);
    	props.setProperty("database.history.skip.unparseable.ddl", skipUnparseableDdl);
    	props.setProperty("include.schema.changes", includeSchemaChanges);
    	props.setProperty("table.include.list", tableIncludeList);
    	props.setProperty("errors.log.enable", errorsLogEnable);
    	props.setProperty("offset.storage", storageClass);
    	props.setProperty("offset.storage.file.filename", storageFileName);
    	props.setProperty("offset.flush.interval.ms", flushInterval);
    	
    	// Needed props for oracle
    	props.setProperty("database.schema", schema);
    	props.setProperty("db_type", dbType);
    	props.setProperty("tasks.max", tasksMax);
    	props.setProperty("database.server.name", serverName);
    	props.setProperty("database.tablename.case.insensitive", tableNameCaseInsensitive);
    	props.setProperty("database.dbname", dbName);
    	props.setProperty("database.pdb.name", pdbName);
    	props.setProperty("database.out.server.name", outServerName);

    	
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
