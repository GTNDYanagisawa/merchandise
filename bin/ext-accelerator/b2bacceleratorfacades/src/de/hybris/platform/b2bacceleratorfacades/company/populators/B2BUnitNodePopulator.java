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
package de.hybris.platform.b2bacceleratorfacades.company.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorfacades.company.data.B2BUnitNodeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalModel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.convert.converter.Converter;


/**
 * Converter for {@link B2BUnitModel}.
 */
public class B2BUnitNodePopulator implements Populator<B2BUnitModel, B2BUnitNodeData>
{
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
	private Converter<B2BUnitModel, B2BUnitNodeData> childNodeConverter;

	@Override
	public void populate(final B2BUnitModel b2BUnitModel, final B2BUnitNodeData b2BUnitNodeData)
	{
		b2BUnitNodeData.setActive(Boolean.TRUE.equals(b2BUnitModel.getActive()));
		b2BUnitNodeData.setId(b2BUnitModel.getUid());
		b2BUnitNodeData.setName((b2BUnitModel.getName() != null ? b2BUnitModel.getName() : b2BUnitModel.getUid()));
		final B2BUnitModel parent = getB2BUnitService().getParent(b2BUnitModel);
		if (parent != null)
		{
			b2BUnitNodeData.setParent(parent.getUid());
		}

		// Convert the child nodes
		final List<B2BUnitNodeData> childNodes = new ArrayList<B2BUnitNodeData>();
		for (final PrincipalModel principalModel : b2BUnitModel.getMembers())
		{
			if (principalModel instanceof B2BUnitModel)
			{
				childNodes.add(getChildNodeConverter().convert((B2BUnitModel) principalModel));
			}
		}
		b2BUnitNodeData.setChildren(childNodes);
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService()
	{
		return b2BUnitService;
	}

	@Required
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService)
	{
		this.b2BUnitService = b2BUnitService;
	}

	protected Converter<B2BUnitModel, B2BUnitNodeData> getChildNodeConverter()
	{
		if (childNodeConverter == null)
		{
			childNodeConverter = lookupChildNodeConverter();
		}
		return childNodeConverter;
	}

	protected Converter<B2BUnitModel, B2BUnitNodeData> lookupChildNodeConverter()
	{
		throw new IllegalStateException("specify lookupChildNodeConverter via <lookup-method> in spring config.");
	}
}
