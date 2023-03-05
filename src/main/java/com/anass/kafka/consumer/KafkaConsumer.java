package com.anass.kafka.consumer;

import static com.anass.kafka.configuration.KafkaConfiguration.loadConsumerProperties;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.anass.kafka.Person;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

public class KafkaConsumer {

    Properties properties;

    public KafkaConsumer() throws IOException {
        properties = loadConsumerProperties();
    }

    public KafkaConsumer(Properties properties){
        this.properties = properties;
    }

    public void consume(){
        /*
          Create an object of KafkaConsumer leveraging consumer properties
         */
        Consumer<String, Person> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(properties);

        /*
          get a reference to the current thread
          the ShutdownHook needs to be linked that main thread in order to wait for all threads to complete before shutting down the program.
         */
        final Thread mainThread = Thread.currentThread();

        /*
          adding the shutdown hook
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Detected a shutdown, let's exit by calling consumer.wakeup()...");
                consumer.wakeup();

                /*
                  join the main thread to allow the execution of the code in the main thread
                 */
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });




        final String topic = properties.getProperty("person.topic");
        try {
        /*
          Subscribe to the topic
         */
            consumer.subscribe(Collections.singleton(topic));
            long start = System.currentTimeMillis();
            long timeout = 10000;
            while ((System.currentTimeMillis() - start) < timeout) {
            /*
              poll for new data with a specified timeout on which the consumer will be blocked before returning a response
             */
                ConsumerRecords<String, Person> records =
                        consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, Person> record : records) {
                    System.out.println("Key: " + record.key() + ", Value: " + record.value());
                    System.out.println("Partition: " + record.partition() + ", Offset:" + record.offset());
                }
            }
        } catch (WakeupException e) {
            System.out.println("Wake up exception!");
            // we ignore this as this is an expected exception when closing a consumer
        } catch (Exception e) {
            System.out.println("Unexpected exception"+e.getMessage());
        } finally {
            consumer.close(); // this will also commit the offsets if need be.
            System.out.println("The consumer is now gracefully closed.");
        }

    }

    public void consumeByTimeRanges(Long start, Long end, int partitions){
        Consumer<String, Person> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(properties);
        final String topic = properties.getProperty("person.topic");
        try {


            /*
            Create TopicPartition Objects and assign those partitions to the consumer
             */
            TopicPartition[] topicPartitions = new TopicPartition[partitions];
            Map<TopicPartition,Long> timeStamps = new HashMap<>();

            for(int i=0;i<partitions;i++) {
                TopicPartition partition = new TopicPartition(topic, i);
                topicPartitions[i] = partition;
                timeStamps.put(partition,start);
            }


            consumer.assign(Arrays.asList(topicPartitions));
            /*
            Fetch the offsets for the specified start time
             */
            Map<TopicPartition,OffsetAndTimestamp> partitionOffsetMap = consumer.offsetsForTimes(timeStamps);

            for(int i=0;i<partitions;i++) {
                OffsetAndTimestamp offsetAndTimestamp = partitionOffsetMap.get(topicPartitions[i]);
                if(offsetAndTimestamp != null){
                    long offset = offsetAndTimestamp.offset();
                    consumer.seek(topicPartitions[i],offset);
                }
            }
            /*
            Poll the consumer
             */
            boolean flag = true;

            long count = 0L;

            while (flag) {
                ConsumerRecords<String, Person> records = consumer.poll(Duration.ofMillis(1000));
                count = count + records.count();
                System.out.println("Record Fetch Count : " + count);
                if(records != null && !records.isEmpty()) {
                    for (ConsumerRecord<String, Person> record : records) {
                        System.out.println("Record is "+record);
                        System.out.println("current thread "+Thread.currentThread().getName());
                        if (record.timestamp() >= end) {
                            flag = false;
                            break;
                        }
                    }
                } else{
                    break;
                }
            }
            consumer.close();
        } catch (WakeupException e) {
            System.out.println("Wake up exception!");
            // we ignore this as this is an expected exception when closing a consumer
        } catch (Exception e) {
            System.out.println("Unexpected exception"+e.getMessage());
        } finally {
            consumer.close(); // this will also commit the offsets if need be.
            System.out.println("The consumer is now gracefully closed.");
        }
    }
}
