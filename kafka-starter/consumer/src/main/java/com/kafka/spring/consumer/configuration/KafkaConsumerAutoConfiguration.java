package com.kafka.spring.consumer.configuration;

import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ISOLATION_LEVEL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

@Configuration
//@EnableAutoConfiguration
//@EnableKafka
public class KafkaConsumerAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerAutoConfiguration.class);

    @Value("${kafka.consumer.bootstrap-servers}")
    private String bootStrapServers;

    @Value("${kafka.consumer.group-id}")
    private String groupId;

    /*
    Setting this value to false we can commit the offset messages manually,
    which avoids crashing of the consumer if new messages are consumed
    when the currently consumed message is being processed by the consumer.
     */
    @Value("${kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;

    @Value("${kafka.consumer.auto-commit-interval-ms}")
    private String autoCommitIntervalMs;

    @Value("${kafka.consumer.session-timeout-ms}")
    private String sessionTimeoutMs;

    /*@Value("${kafka.consumer.max-poll-records}")
    private String maxPollRecords;*/

    @Value("${kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${kafka.consumer.isolation-level-config}")
    private String isolationLevelConfig;


    @Value("${kafka.consumer.poll-timout}")
    private long pollTimeout;

    @Value("${kafka.consumer.concurrency}")
    private Integer concurrency;

    @Bean
    @ConditionalOnMissingBean
    public Map<String, Object> consumerConfigs() {
        LOG.info("### Creating Default ConsumerConfigs Object ###");
        final Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        consumerProps.put(KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        consumerProps.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);

        consumerProps.put(GROUP_ID_CONFIG, groupId);
        consumerProps.put(ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        consumerProps.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMs);
        consumerProps.put(AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        consumerProps.put(ISOLATION_LEVEL_CONFIG, isolationLevelConfig);

        consumerProps.put(SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
//        consumerProps.put(MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        return consumerProps;
    }


    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setPollTimeout(pollTimeout);
        return factory;


    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }
}