# Kafka-Boot-Starter


## This is a sample kafka boot project which can be used to speed up the development process with kafka producer and consumer.

### Below are the steps can be followed to integrate with this project easily.
  - Build and generate the artefacts of this project.
  - It will then generate the two artefacts one for producer and second for consumer.


### Steps to use the producer:
  - Configure / Include the producer artefact dependency in your module like below:
    ```
    <dependency>
       <groupId>com.kafka.spring.producer</groupId>
       <artifactId>kafka-producer</artifactId>
       <version>1.0.0-SNAPSHOT</version>
    </dependency>
    ```
  - Configure the application.yaml file in order to provide/configure your kafka broker related values like below. Note, please change these values depending upon your project and requirement. I have just provided very basic one.
    ```
    kafka:
      producer:
        bootstrap-servers: localhost:9092
        acks: "all"
        batch-size: 1000
        linger-ms: 1
        retries: 0
        buffer-memory: 33554432
        key-serializer: org.apache.kafka.common.serialization.StringSerializer
        value-serializer: org.apache.kafka.common.serialization.StringSerializer
        
        
    test:
        topic: test-topic
            
     ```
  - Inject the below two dependencies in your controller / configuration class in order to use the kafka send config class:
     ```
     @Autowired
     private KafkaSendMessageAutoConfiguration messageAutoConfiguration;
     
     
     @Value("${test.topic}")  // You can configure and provide whatever way you want to go with. Just make sure it shoud match with what has been provided in the yaml file.
     private String topicName;
         
     ```
  - Finally, once you're done with the above config, you can use the below snippet to produce the message to the topic
     ```
     //messageAutoConfiguration.sendMessage(${message_to_send}, ${topic_name});
     messageAutoConfiguration.sendMessage("sample-topic", topicName);
     ```
  - For more methods and the way to produce the message, please see the implementation of **KafkaSendMessageAutoConfiguration** class.     
  
  
  
### Steps to use the consumer:
   - Configure / Include the producer artefact dependency in your module like below:
     ```
       <dependency>
          <groupId>com.kafka.spring.consuemr</groupId>
          <artifactId>kafka-consuemr</artifactId>
          <version>1.0.0-SNAPSHOT</version>
       </dependency>
     ```
   - Configure the application.yaml file in order to provide/configure your kafka broker related values like below. Note, please change these values depending upon your project and requirement. I have just provided very basic one.
     ```
       kafka:
         consumer:
           bootstrap-servers: localhost:9092
           group-id: "producer-1"
           enable-auto-commit: false
           auto-commit-interval-ms: "100"
           session-timeout-ms: "6000"
           max-poll-records: "1"
           consumer-timeout-ms: "-1"
           auto-offset-reset: "earliest"
           isolation-level-config:
           poll-timout: 1000
           concurrency: 3
           
           
       test:
           topic: test-topic
               
      ```
   - Create a listener to listen the message from a given topic and partition like below:
     ```
       @KafkaListener(topics = "${test.topic}", groupId = "${kafka.producer.group-id}")
        public void receive(ConsumerRecord<?, ?> record) {
            System.out.println("record -> " + record);
            System.out.println("record -> " + record.value());
            LOG.info("### Received Message {} -> ", record.value());
            LOG.info("### Received Offset  {} -> ", record.offset());
            LOG.info("### Received Message  {} -> ", record.partition());
            LOG.info("### Received Topic {}  -> ", record.topic());
            LOG.info("### Received Record {}  -> ", record);
            /*
             perform your business logic
            */
       }
     ```