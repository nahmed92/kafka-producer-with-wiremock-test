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

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Message POJO that needs to send to kafka.
 *
 * @author Nasir Ahmed
 *
 */
public class Message {

    @NotNull(message = "payload cannot be null")
    private final JSONObject payload;

    @NotNull(message = "schemaId cannot be null")
    private final Integer schemaId;

    @NotBlank(message = "topic cannot be null")
    private final String topic;

    /**
     * Constructor to create message
     *
     * @param payload {@link JsonObject}
     * @param schemaId message schemaId
     * @param topic message topic
     */
    @JsonCreator
    public Message(@JsonProperty("payload") final JSONObject payload,
            @JsonProperty("schemaId") final Integer schemaId,
            @JsonProperty("topic") final String topic) {
        this.payload = payload;
        this.schemaId = schemaId;
        this.topic = topic;
    }

    /**
     * @return the message
     */
    public JSONObject getPayload() {
        return payload;
    }

    /**
     * @return the schemaId
     */
    public Integer getSchemaId() {
        return schemaId;
    }

    /**
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return new HashCodeBuilder() //
                .append(getSchemaId()) //
                .append(getTopic()) //
                .append(getPayload()) //
                .hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Message)) {
            return false;
        }
        final Message message = (Message) obj;
        return new EqualsBuilder() //
                .append(getSchemaId(), message.getSchemaId()) //
                .append(getTopic(), message.getTopic()) //
                .append(getPayload(), message.getPayload()) //
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this) //
                .append("SchemaId", getSchemaId()) //
                .append("Topic", getTopic()) //
                .append("Payload", getPayload().toJSONString()) //
                .toString();
    }
}
