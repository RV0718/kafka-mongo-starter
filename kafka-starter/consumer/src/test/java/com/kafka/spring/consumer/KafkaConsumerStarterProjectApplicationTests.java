package com.kafka.spring.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaConsumerStarterProjectApplicationTests {

    public static final String TEST_TOPIC = "test-topic";
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaTemplate<String, String> kafkaTemplate;

    KafkaMessageListenerContainer<String, String> container;

    BlockingQueue<ConsumerRecord> records;

    @BeforeAll
    public void setUp() {
        /*producer settings*/
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        DefaultKafkaProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(configs);
        this.kafkaTemplate = new KafkaTemplate<>(producerFactory);

        /*consumer settings*/
        Map<String, Object> consumerConfigs = new HashMap<>(KafkaTestUtils.consumerProps("consumer-id-1", "false", embeddedKafkaBroker));
        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfigs, new StringDeserializer(), new StringDeserializer());
        ContainerProperties containerProperties = new ContainerProperties(TEST_TOPIC);
        this.container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        this.records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, String>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterAll
    public void tearDown() {
        container.stop();
    }


    @Test
    void testProducerStarter() throws Exception {
        this.kafkaTemplate.send( TEST_TOPIC,"test message");

        ConsumerRecord<String, String> resultingRecords = this.records.poll(100, TimeUnit.SECONDS);
        assertThat(resultingRecords).isNotNull();
        assertThat(resultingRecords.topic()).isEqualTo(TEST_TOPIC);
        assertThat(resultingRecords.value()).isEqualTo("test message");
    }

    @Test
    void testProducerStarterWithListenable() throws Exception {
        this.kafkaTemplate.send( TEST_TOPIC,"test message");

        ConsumerRecord<String, String> resultingRecords = this.records.poll(100, TimeUnit.SECONDS);
        assertThat(resultingRecords).isNotNull();
        assertThat(resultingRecords.topic()).isEqualTo(TEST_TOPIC);
        assertThat(resultingRecords.value()).isEqualTo("test message");
    }


}
