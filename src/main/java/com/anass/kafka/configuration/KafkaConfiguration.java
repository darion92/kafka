package com.anass.kafka.configuration;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * Kafka configuration
 */
public class KafkaConfiguration {

    /**
     * Create and load Kafka producer properties
     */
    public static Properties loadProducerProperties() throws IOException {
        Properties properties = new Properties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        try (FileInputStream fis = new FileInputStream(
                "src/main/resources/application.properties")) {
            properties.load(fis);
            return properties;
        }
    }

    /**
     * Create and load Kafka consumer properties
     */
    public static Properties loadConsumerProperties() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(
                "src/main/resources/application.properties")) {
            properties.load(fis);
            String groupId = "person-application";
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            return properties;
        }
    }


}
