/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.sap.wec.adtreco.btgcockpit;

import de.hybris.platform.btg.model.BTGReferencePrincipalGroupsOperandModel;
import de.hybris.platform.btgcockpit.service.label.AbstractBTGItemCollectionLabelProvider;

import java.util.Collection;

import com.sap.wec.adtreco.model.BTGReferenceSAPInitiativeOperandModel;


/**
 * Provides a text representation for given {@link BTGReferencePrincipalGroupsOperandModel}
 */
public class BTGReferencePrincipalGroupsOperandLabelProvider extends
		AbstractBTGItemCollectionLabelProvider<BTGReferenceSAPInitiativeOperandModel, String>
{
	@Override
	protected Collection<String> getItemObjectCollection(final BTGReferenceSAPInitiativeOperandModel item)
	{
		return item.getInitiatives();
	}

	@Override
	protected String getItemObjectName(final String itemObject)
	{
		return itemObject;
	}

	@Override
	protected String getMessagePrefix()
	{
		return "initiatives";
	}
}
