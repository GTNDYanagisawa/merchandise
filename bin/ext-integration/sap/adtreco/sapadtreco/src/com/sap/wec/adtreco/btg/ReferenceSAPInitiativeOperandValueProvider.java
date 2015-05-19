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
package com.sap.wec.adtreco.btg;

import de.hybris.platform.btg.condition.operand.valueproviders.CollectionOperandValueProvider;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.core.model.user.UserModel;

import java.util.ArrayList;
import java.util.Collection;

import com.sap.wec.adtreco.model.BTGReferenceSAPInitiativeOperandModel;


/**
 *
 */
public class ReferenceSAPInitiativeOperandValueProvider implements
		CollectionOperandValueProvider<BTGReferenceSAPInitiativeOperandModel>
{
	@Override
	public Object getValue(final BTGReferenceSAPInitiativeOperandModel operand, final UserModel user,
			final BTGConditionEvaluationScope evaluationScope)
	{
		final Collection result = new ArrayList<String>();
		result.addAll(operand.getInitiatives());
		return result;
	}

	@Override
	public Class getValueType(final BTGReferenceSAPInitiativeOperandModel operand)
	{
		return SAPInitiativeSet.class;
	}

	@Override
	public Class getAtomicValueType(final BTGReferenceSAPInitiativeOperandModel operand)
	{
		return String.class;
	}
}
