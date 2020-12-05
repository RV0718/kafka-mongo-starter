package com.kafka.spring.producer.configuration;

import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Configuration
public class KafkaProducerAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducerAutoConfiguration.class);

    @Value("${kafka.producer.bootstrap-servers}")
    private String bootStrapServers;

    @Value("${kafka.producer.acks}")
    private String acks;

    @Value("${kafka.producer.retries}")
    private String retries;

    /*@Value("${kafka.producer.batch-size}")
    private String batchSize;

    @Value("${kafka.producer.linger-ms}")
    private String lingerMs;

    @Value("${kafka.producer.buffer-memory}")
    private String bufferMemory;*/


    @Bean
    public Map<String, Object> producerConfigs() {

        LOG.info("### Creating Default ProducerConfig Object ###");

        final Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        producerProps.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ACKS_CONFIG, acks);
        producerProps.put(RETRIES_CONFIG, retries);
//        producerProps.put(BATCH_SIZE_CONFIG, batchSize);
//        producerProps.put(LINGER_MS_CONFIG, lingerMs);
//        producerProps.put(BUFFER_MEMORY_CONFIG, bufferMemory);
        return producerProps;
    }

    @Bean
    @ConditionalOnMissingBean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
