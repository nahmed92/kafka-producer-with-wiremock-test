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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;

import com.etilize.burraq.krp.test.AbstractRestIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KRPRestIntegrationTest extends AbstractRestIntegrationTest {

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper mapper;

    @Test
    public void shouldPublishMessage() throws Exception {
        // send the message
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Test 1st Message");
        jsonObject.put("description", "Test 2nd Message");
        final Message message = new Message(jsonObject, 662, MESSAGE_TOPIC);
        final String content = mapper.writeValueAsString(message);
        mockMvc.perform(post("/kafka_rest_proxy") //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(content)) //
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatusBadRequestWhenRequiredFieldsAreNull() throws Exception {
        final Message message = new Message(null, null, null);
        mockMvc.perform(post("/kafka_rest_proxy") //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(mapper.writeValueAsString(message))) //
                .andExpect(status().isBadRequest()) //
                .andExpect(
                        jsonPath("$.errors[*].message",
                                containsInAnyOrder("schemaId cannot be null",
                                        "topic cannot be null",
                                        "payload cannot be null")));
    }

    @Test
    public void shouldReturnStatusBadRequestWhenSchemaNotFound() throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Test 1st Message");
        jsonObject.put("description", "Test 2nd Message");
        final Message message = new Message(jsonObject, 721, MESSAGE_TOPIC);
        mockMvc.perform(post("/kafka_rest_proxy") //
                .contentType(MediaType.APPLICATION_JSON) //
                .content(mapper.writeValueAsString(message))) //
                .andExpect(status().isBadRequest()) //
                .andExpect(
                        jsonPath("$.message", is("Could not find schema with id: 721")));
    }
}
