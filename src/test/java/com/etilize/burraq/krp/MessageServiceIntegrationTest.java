/*
 * #region
 * kafka-rest-proxy
 * %%
 * Copyright (C) 2018 Etilize
 * %%
 * NOTICE: All information contained herein is, and remains the property of ETILIZE.
 * The intellectual and technical concepts contained herein are proprietary to
 * ETILIZE and may be covered by U.S. and Foreign Patents, patents in process, and
 * are protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from ETILIZE. Access to the source code contained herein
 * is hereby forbidden to anyone except current ETILIZE employees, managers or
 * contractors who have executed Confidentiality and Non-disclosure agreements
 * explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication
 * or disclosure of this source code, which includes information that is confidential
 * and/or proprietary, and is a trade secret, of ETILIZE. ANY REPRODUCTION, MODIFICATION,
 * DISTRIBUTION, PUBLIC PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS
 * SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF ETILIZE IS STRICTLY PROHIBITED,
 * AND IN VIOLATION OF APPLICABLE LAWS AND INTERNATIONAL TREATIES. THE RECEIPT
 * OR POSSESSION OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR
 * IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO
 * MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
 * #endregion
 */

package com.etilize.burraq.krp;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.schema.SchemaNotFoundException;

import com.etilize.avro.utils.AvroInteropUtils;
import com.etilize.burraq.krp.test.AbstractIntegrationTest;

public class MessageServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MessageServiceImpl messageService;

    @Autowired
    private AvroInteropUtils avroInteropUtils;

    @Test
    public void shouldPublishMessage() throws Exception {
        // send the message
        final String avroSchema = "{\"type\":\"record\",\"name\":\"MessageSchema\","
                + "\"namespace\":\"com.etilize.burraq.krp\",\"fields\":[{\"name\""
                + ":\"message\",\"type\":{\"type\":\"string\",\"avro.java.string\""
                + ":\"String\"},\"doc\":\"Testing 1\"},{\"name\":\"description\",\"type\""
                + ":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"Testing 3\"}]}";
        final JSONObject message = new JSONObject();
        message.put("message", "Test 1st Message");
        message.put("description", "Test Message Description");
        final Message kafkaMessage = new Message(message, 662, Message_TOPIC);
        messageService.publish(kafkaMessage);
        // check that the message was received
        final ConsumerRecord<String, byte[]> received = records.poll(10,
                TimeUnit.SECONDS);
        final PublishedMessage publishedMessage = avroInteropUtils.decode(
                received.value(), avroSchema, PublishedMessage.class);
        // hamcrest matchers to check the value
        assertThat(publishedMessage.getMessage(), is("Test 1st Message"));
        assertThat(publishedMessage.getDescription(), is("Test Message Description"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenMessageIsNull() {
        messageService.publish(null);
    }

    @Test(expected = SchemaNotFoundException.class)
    public void shouldThrowExceptionWhenMessageSchemaIsNotFound() {
        // send the message
        final JSONObject message = new JSONObject();
        message.put("message", "Test 1st Message");
        message.put("description", "Test 2nd Message");
        final Message kafkaMessage = new Message(message, 661, Message_TOPIC);
        messageService.publish(kafkaMessage);
    }

}
