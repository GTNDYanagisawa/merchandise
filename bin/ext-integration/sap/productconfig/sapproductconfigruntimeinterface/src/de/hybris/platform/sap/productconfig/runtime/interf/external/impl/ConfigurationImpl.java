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
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.CharacteristicValue;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.ContextAttribute;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Instance;
import de.hybris.platform.sap.productconfig.runtime.interf.external.PartOfRelation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Default implementation: External representation of configuration
 * 
 */
public class ConfigurationImpl implements Configuration
{

	private Instance rootInstance;
	private final List<Instance> instances = new ArrayList<>();
	private final List<PartOfRelation> partOfRelations = new ArrayList<>();
	private final List<CharacteristicValue> characteristicValues = new ArrayList<>();
	private final List<ContextAttribute> contextAttributes = new ArrayList<>();
	private KBKey kbKey;

	@Override
	public Instance getRootInstance()
	{

		return rootInstance;
	}


	@Override
	public void setRootInstance(final Instance rootInstance)
	{
		this.rootInstance = rootInstance;

	}


	@Override
	public List<Instance> getInstances()
	{
		return Collections.unmodifiableList(instances);
	}



	@Override
	public void addInstance(final Instance instance)
	{
		instances.add(instance);

	}


	@Override
	public List<PartOfRelation> getPartOfRelations()
	{

		return Collections.unmodifiableList(partOfRelations);
	}



	@Override
	public void addPartOfRelation(final PartOfRelation partOfRelation)
	{
		partOfRelations.add(partOfRelation);

	}



	@Override
	public List<CharacteristicValue> getCharacteristicValues()
	{
		return Collections.unmodifiableList(characteristicValues);
	}



	@Override
	public void addCharacteristicValue(final CharacteristicValue csticValue)
	{
		characteristicValues.add(csticValue);

	}



	@Override
	public List<ContextAttribute> getContextAttributes()
	{
		return Collections.unmodifiableList(contextAttributes);
	}


	@Override
	public void addContextAttribute(final ContextAttribute contextAttribute)
	{
		contextAttributes.add(contextAttribute);

	}

	@Override
	public KBKey getKbKey()
	{
		return kbKey;
	}


	@Override
	public void setKbKey(final KBKey kbKey)
	{
		this.kbKey = kbKey;
	}

}
