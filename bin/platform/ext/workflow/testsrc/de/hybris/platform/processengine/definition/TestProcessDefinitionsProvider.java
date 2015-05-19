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
package de.hybris.platform.processengine.definition;



public class TestProcessDefinitionsProvider extends ProcessDefinitionsProvider
{
	public TestProcessDefinitionsProvider(final XMLProcessDefinitionsReader xmlDefinitionsReader)
	{
		super(xmlDefinitionsReader, null);
	}

	@Override
	public ProcessDefinition getDefinition(final ProcessDefinitionId id)
	{
		return null;
	}

	@Override
	public ProcessDefinitionId getLatestDefinitionIdFor(final ProcessDefinitionId id)
	{
		return null;
	}
}
