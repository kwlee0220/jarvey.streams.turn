package jarvey.streams.turn;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes.ByteArraySerde;
import org.apache.kafka.common.serialization.Serdes.StringSerde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class TurnDetectorDockerMain {
	private static final Logger s_logger = LoggerFactory.getLogger(TurnDetectorDockerMain.class);
	
	public static void main(String... args) throws Exception {
		Map<String,String> envs = System.getenv();

		String appId = envs.getOrDefault("KAFKA_APPLICATION_ID_CONFIG", "turn-detect");
		String kafkaServers = envs.getOrDefault("KAFKA_BOOTSTRAP_SERVERS_CONFIG", "localhost:9092");
		String topicLocationEvents = envs.getOrDefault("DNA_TOPIC_LOCATION_EVENTS", "location-events");
		String topicTurnEvents = envs.getOrDefault("DNA_TOPIC_TURN_EVENTS", "turn-events");
		
		if ( s_logger.isInfoEnabled() ) {
			s_logger.info("use Kafka servers: {}", kafkaServers);
			s_logger.info("use Kafka topic: {}={}", "DNA_TOPIC_LOCATION_EVENTS", topicLocationEvents);
			s_logger.info("use Kafka topic: {}={}", "DNA_TOPIC_TURN_EVENTS", topicTurnEvents);
		}
		
		Topology topology = TurnTopologyBuilder.create()
												.setLocationEventsTopic(topicLocationEvents)
												.setTurnEventsTopic(topicTurnEvents)
												.build();
		
		Properties config = new Properties();
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
		config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
		config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringSerde.class);
		config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, ByteArraySerde.class);
		config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
//		config.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
		
		KafkaStreams streams = new KafkaStreams(topology, config);
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
		
		streams.start();
	}
}
