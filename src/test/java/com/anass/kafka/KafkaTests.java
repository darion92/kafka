package com.anass.kafka;

import com.anass.kafka.consumer.KafkaConsumer;
import com.anass.kafka.producer.KafkaProducer;
import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.anass.kafka.Person;
import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import static org.apache.kafka.clients.producer.ProducerConfig.MAX_REQUEST_SIZE_CONFIG;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Testcontainers
public class KafkaTests {
    Properties properties;
    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.partition.number", () -> 1);
        registry.add("spring.kafka.replication.factor", () -> 1);
    }

    @BeforeEach
    void beforeEach() {
        properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        properties.put(SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        properties.put(MAX_REQUEST_SIZE_CONFIG, "10000");
        properties.put("person.topic", "person-topic");
        properties.put(SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "person-application");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        assertTrue(kafkaContainer.isRunning());
    }

    @Test
    void testKafka(){
        KafkaProducer kafkaProducer = new KafkaProducer(properties);
        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
        long start = System.currentTimeMillis();
        long timeout = 10000;
        while((System.currentTimeMillis() - start) < timeout){
            Person person = Person.newBuilder().setId(UUID.randomUUID().toString()).
                    setName(UUID.randomUUID().toString()).setAddress(UUID.randomUUID().toString()).
                    setPhone(UUID.randomUUID().toString()).build();
            kafkaProducer.produce(person);
        }
        kafkaConsumer.consume();

    }
}
