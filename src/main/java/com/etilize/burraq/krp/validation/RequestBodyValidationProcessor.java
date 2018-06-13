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

package com.etilize.burraq.krp.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.validation.Valid;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.MethodParameter;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.data.rest.webmvc.support.RepositoryConstraintViolationExceptionMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

/**
 * Workaround class for making JSR-303 annotation validation work for controller method
 * parameters. Check the issue <a
 * href="https://jira.spring.io/browse/DATAREST-593">DATAREST-593</a>
 */
@ControllerAdvice
public class RequestBodyValidationProcessor extends RequestBodyAdviceAdapter {

    private final Validator validator;

    private final MessageSourceAccessor messageSourceAccessor;

    /**
     * Constructor for RequestBodyValidationProcessor
     *
     * @param validator It's a {@link Validator} instance that applies validation.
     * @param messageSource {@link MessageSource} resolves error messages.
     */
    public RequestBodyValidationProcessor(final Validator validator,
            final MessageSource messageSource) {
        this.validator = validator;
        this.messageSourceAccessor = new MessageSourceAccessor(messageSource);
    }

    @Override
    public boolean supports(final MethodParameter methodParameter, final Type targetType,
            final Class<? extends HttpMessageConverter<?>> converterType) {
        final Annotation[] parameterAnnotations = methodParameter.getParameterAnnotations();
        for (final Annotation annotation : parameterAnnotations) {
            if (annotation.annotationType().equals(Valid.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object afterBodyRead(final Object body, final HttpInputMessage inputMessage,
            final MethodParameter parameter, final Type targetType,
            final Class<? extends HttpMessageConverter<?>> converterType) {
        final Object obj = super.afterBodyRead(body, inputMessage, parameter, targetType,
                converterType);
        final BindingResult bindingResult = new BeanPropertyBindingResult(obj,
                obj.getClass().getSimpleName());
        validator.validate(obj, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new RepositoryConstraintViolationException(bindingResult);
        }
        return obj;
    }

    /**
     * Handles {@link RepositoryConstraintViolationException}s by returning
     * {@code 400 Bad Request}.
     *
     * @param ex the exception to handle.
     * @return a response entity with BAD_REQUEST as HTTP Status.
     */
    @ExceptionHandler
    public ResponseEntity<RepositoryConstraintViolationExceptionMessage> handleRepositoryConstraintViolationException(
            final RepositoryConstraintViolationException ex) {
        return response(HttpStatus.BAD_REQUEST, new HttpHeaders(),
                new RepositoryConstraintViolationExceptionMessage(ex,
                        messageSourceAccessor));
    }

    private static <T> ResponseEntity<T> response(final HttpStatus status,
            final HttpHeaders headers, final T body) {
        Assert.notNull(headers, "Headers must not be null!");
        Assert.notNull(status, "HttpStatus must not be null!");
        return new ResponseEntity<>(body, headers, status);
    }
}
