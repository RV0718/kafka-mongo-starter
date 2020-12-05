package com.kafka.spring.producer.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Configuration
public class KafkaSendMessageAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaSendMessageAutoConfiguration.class);

    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaSendMessageAutoConfiguration(final KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendMessage(final String message, final String topicName) {
        LOG.info("### -> sendMessage -> Sending : {} to the topic {}", message, topicName);
        LOG.info("--------------------------------");
        this.kafkaTemplate.send(topicName, message);
    }


    //this send custom message will be used if you want to send the custom message to a topic
    /*public void sendCustomMessage(final byte[] messageBytes, final String topicName) {
        LOG.info("Sending Json Serializer : {}",topicName);
        LOG.info("--------------------------------");
        this..userKafkaTemplate.send(topicName, bytes);
    }*/

    public void sendMessageWithCallback(final String message, final String topicName) {
        LOG.info("### -> sendMessageWithCallback -> Sending : {} to the topic {}", message, topicName);
        LOG.info("---------------------------------");

        final ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOG.info("Success Callback: [{}] delivered with offset -{}", message, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOG.warn("Failure Callback: Unable to deliver message [{}]. {}", message, ex.getMessage());
            }
        });
    }

    /*
    The following code segment show the way of implementing kafka producer in Producer service class(in my code).
    In kafka producer template we can configure the payload, topic and partitionId if needed for a given message.
     */
    /*public void sendMessage(Container containerMsg,final String topicname,final int partitionId) {
        Message<Container> message = MessageBuilder
                .withPayload(containerMsg)
                .setHeader(KafkaHeaders.TOPIC, topicname)
                .setHeader(KafkaHeaders.PARTITION_ID, partitionId)
                .build();
        this.kafkaTemplate.send(message);
    }*/


}
