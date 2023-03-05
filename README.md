# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.0.2/reference/htmlsingle/#using.devtools)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/docs/3.0.2/reference/htmlsingle/#messaging.kafka)
* [Apache Kafka Streams Support](https://docs.spring.io/spring-kafka/docs/current/reference/html/#streams-kafka-streams)
* [Apache Kafka Streams Binding Capabilities of Spring Cloud Stream](https://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/#_kafka_streams_binding_capabilities_of_spring_cloud_stream)

### Guides
The following guides illustrate how to use some features concretely:

* [Samples for using Apache Kafka Streams with Spring Cloud stream](https://github.com/spring-cloud/spring-cloud-stream-samples/tree/master/kafka-streams-samples)

## Test with a local confluent platform using Docker
#### prerequisites 
``
* Docker version 1.11 or later is installed and running
* Docker Compose is installed
``
  
####Download and start Confluent Platform
1) Create an empty repo (For example named : Kafka) and go into it
2) Download or copy the contents of the Confluent Platform all-in-one Docker Compose file :
   ``
   curl --silent --output docker-compose.yml \
   https://raw.githubusercontent.com/confluentinc/cp-all-in-one/7.3.2-post/cp-all-in-one/docker-compose.yml
   ``
   
3) Start the Confluent Platform stack with the -d option to run in detached mode:
   ``
   docker-compose up -d
   ``
   Each confluent platform container is started in a separate container:
   ``
   Starting zookeeper ... done
   Starting broker    ... done
   Starting schema-registry ... done
   Starting rest-proxy      ... done
   Starting connect         ... done
   Starting ksqldb-server   ... done
   Starting control-center  ... done
   Starting ksql-datagen    ... done
   Starting ksqldb-cli      ... done
   ``
   
4) Verify that the services are up and running:
   ``
   docker-compose ps
   ``
   
   Name                    Command               State                                         Ports
   ---------------------------------------------------------------------------------------------------------------------------------------------
   broker            /etc/confluent/docker/run        Up      0.0.0.0:9092->9092/tcp,:::9092->9092/tcp, 0.0.0.0:9101->9101/tcp,:::9101->9101/tcp
   connect           /etc/confluent/docker/run        Up      0.0.0.0:8083->8083/tcp,:::8083->8083/tcp, 9092/tcp                                
   control-center    /etc/confluent/docker/run        Up      0.0.0.0:9021->9021/tcp,:::9021->9021/tcp                                          
   ksql-datagen      bash -c echo Waiting for K ...   Up                                                                                        
   ksqldb-cli        /bin/sh                          Up                                                                                        
   ksqldb-server     /etc/confluent/docker/run        Up      0.0.0.0:8088->8088/tcp,:::8088->8088/tcp                                          
   rest-proxy        /etc/confluent/docker/run        Up      0.0.0.0:8082->8082/tcp,:::8082->8082/tcp                                          
   schema-registry   /etc/confluent/docker/run        Up      0.0.0.0:8081->8081/tcp,:::8081->8081/tcp                                          
   zookeeper         /etc/confluent/docker/run        Up      0.0.0.0:2181->2181/tcp,:::2181->2181/tcp, 2888/tcp, 3888/tcp  
 
