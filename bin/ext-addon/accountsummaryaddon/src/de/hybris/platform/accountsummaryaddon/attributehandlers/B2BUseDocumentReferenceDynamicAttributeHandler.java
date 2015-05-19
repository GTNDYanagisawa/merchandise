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
 *
 *  
 */
package de.hybris.platform.accountsummaryaddon.attributehandlers;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.accountsummaryaddon.model.B2BDocumentPaymentInfoModel;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class B2BUseDocumentReferenceDynamicAttributeHandler extends AbstractDynamicAttributeHandler<String, B2BDocumentPaymentInfoModel>
{
	 @Override
	 public String get(final B2BDocumentPaymentInfoModel ruleSet)
	 {
			final boolean usingDocument = StringUtils.isBlank(ruleSet.getCcTransactionNumber());

			return usingDocument ? ruleSet.getUseDocument().getDocumentNumber() : ruleSet.getCcTransactionNumber();
	 }
	 
	 @Override
	 public void set(final B2BDocumentPaymentInfoModel paramMODEL, final String paramVALUE)
	 {
	  // Ignore
	 }
}
