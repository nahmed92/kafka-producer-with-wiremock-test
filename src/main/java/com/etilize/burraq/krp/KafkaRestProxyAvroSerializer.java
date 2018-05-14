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

import java.io.IOException;
import java.util.Map;

import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.integration.kafka.serializer.avro.AvroSerializer;

/**
 * Avro Serializer class
 *
 * @author Nasir Ahmed
 *
 */
public class KafkaRestProxyAvroSerializer implements Serializer<Record> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(final Map<String, ?> configs, final boolean isKey) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] serialize(final String topic, final Record data) {
        try {
            final DatumWriter<Record> datumWriter = new GenericDatumWriter<Record>(
                    data.getSchema());
            final AvroSerializer<Record> avroSerializer = new AvroSerializer<>();
            return avroSerializer.serialize(data, datumWriter);
        } catch (final IOException ex) {
            throw new SerializationException(
                    String.format("Can't serialize data= %s for topic= %s ", data, topic),
                    ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }
}
