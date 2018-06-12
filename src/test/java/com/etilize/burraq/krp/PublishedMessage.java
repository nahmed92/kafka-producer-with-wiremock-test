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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@PublishedMessage} represents the message published on kafka topic
 *
 * @author Nasir Ahmed
 *
 */
public class PublishedMessage {

    /**
     * published message field
     */
    private final String message;

    /**
     * published description field
     */
    private final String description;

    /**
     * Constructor PublishedMessage
     *
     * @param message published message
     * @param description description of message published
     */
    @JsonCreator
    public PublishedMessage(@JsonProperty("message") final String message,
            @JsonProperty("description") final String description) {
        this.message = message;
        this.description = description;
    }

    /**
     * Return published message
     * @return message published message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Return published message
     * @return published message description
     */
    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return new HashCodeBuilder() //
                .append(getMessage()) //
                .append(getDescription()) //
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
        if (!(obj instanceof PublishedMessage)) {
            return false;
        }
        final PublishedMessage message = (PublishedMessage) obj;
        return new EqualsBuilder() //
                .append(getMessage(), message.getMessage()) //
                .append(getMessage(), message.getDescription()) //
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this) //
                .append("message", getMessage()) //
                .append("description", getDescription()) //
                .toString();
    }

}
