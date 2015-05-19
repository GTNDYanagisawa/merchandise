/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

/**
 * 
 */
package de.hybris.platform.ycommercewebservices.oauth2;

import de.hybris.platform.commercewebservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.converters.AbstractErrorConverter;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultOAuth2ExceptionRenderer;
import org.springframework.web.context.request.ServletWebRequest;


/**
 * OAuth2ExceptionRenderer converts ResponseEntity<OAuth2Exception> to ResponseEntity<ErrorWsDTO>
 */
public class OAuth2ExceptionRenderer extends DefaultOAuth2ExceptionRenderer
{
	private AbstractErrorConverter exceptionConverter;

	@Override
	public void handleHttpEntityResponse(final HttpEntity<?> responseEntity, final ServletWebRequest webRequest) throws Exception
	{
		final ResponseEntity<OAuth2Exception> castedResponseEntity = (ResponseEntity<OAuth2Exception>) responseEntity;
		final OAuth2Exception ex = castedResponseEntity.getBody();

		final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
		errorListDto.setErrors(exceptionConverter.convert(ex));

		final ResponseEntity<ErrorListWsDTO> convertedResponseEntity = new ResponseEntity<>(errorListDto,
				castedResponseEntity.getStatusCode());

		super.handleHttpEntityResponse(convertedResponseEntity, webRequest);
	}

	@Required
	public void setExceptionConverter(AbstractErrorConverter exceptionConverter)
	{
		this.exceptionConverter = exceptionConverter;
	}
}
