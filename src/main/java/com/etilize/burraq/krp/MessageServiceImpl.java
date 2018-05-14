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

import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.schema.client.SchemaRegistryClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * implementation of {@link MessageService}
 *
 * @author Nasir Ahmed
 *
 */
@Service
public class MessageServiceImpl implements MessageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KafkaTemplate<String, Record> kafkaTemplate;

    private final SchemaRegistryClient schemaRegistryClient;

    /**
     * MessageServiceImpl Constructor
     *
     * @param kafkaTemplate {@link Map}
     * @param schemaRegistryClient {@link SchemaRegistryClient}
     */
    @Autowired
    public MessageServiceImpl(final KafkaTemplate<String, Record> kafkaTemplate,
            final SchemaRegistryClient schemaRegistryClient) {
        Assert.notNull(kafkaTemplate, "kafkaTemplate is required.");
        Assert.notNull(schemaRegistryClient, "schemaRegistryClient is required.");
        this.kafkaTemplate = kafkaTemplate;
        this.schemaRegistryClient = schemaRegistryClient;
    }

    /**
     * Publish message to kafka
     *
     * @param message {@link Message}}
     */
    @Override
    public void publish(final Message message) {
        Assert.notNull(message, "message is required.");
        final JSONObject payload = message.getPayload();
        final Integer schemaId = message.getSchemaId();
        final String topic = message.getTopic();

        // get Schema
        final String avroSchema = schemaRegistryClient.fetch(schemaId);

        final Schema.Parser parser = new Schema.Parser();
        final Schema schema = parser.parse(avroSchema);
        final List<Field> fields = schema.getFields();
        final GenericData.Record avroRecord = new GenericData.Record(schema);
        fields.forEach(field -> {
            final String fieldName = field.name();
            avroRecord.put(fieldName, payload.get(fieldName));
        });
        logger.info("sending message='{}' to topic='{}'", avroRecord, message.getTopic());
        kafkaTemplate.send(topic, avroRecord);
    }
}
