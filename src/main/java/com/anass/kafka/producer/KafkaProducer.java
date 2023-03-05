package com.anass.kafka.producer;

import static com.anass.kafka.configuration.KafkaConfiguration.loadProducerProperties;
import java.io.IOException;
import java.time.Instant;
import java.util.Properties;
import org.anass.kafka.Person;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaProducer {
    Properties properties;

    public KafkaProducer() throws IOException {
        properties = loadProducerProperties();
    }

    public KafkaProducer(Properties properties){
        this.properties = properties;
    }

    public void produce(Person person){

        Producer<String, Person> producer = new org.apache.kafka.clients.producer.KafkaProducer<>(properties);
            final String inputTopic = properties.getProperty("person.topic") ;
            Callback callback = callback();
            ProducerRecord<String, Person> producerRecord = new ProducerRecord<>(inputTopic,
                    0,
                    Instant.now().getEpochSecond(),
                    person.getId(),
                    person);
            /*
            We send the data asynchronously to the topic with a call back
             */
            producer.send(producerRecord, callback);
    }

    private Callback callback() {
            return (metadata, exception) -> {
                if (exception != null) {
                    System.out.printf("Producing records encountered error %s %n", exception);
                } else {
                    System.out.printf("Record produced - offset - %d timestamp - %d %n", metadata.offset(),
                            metadata.timestamp());
                }

            };
        }
}
