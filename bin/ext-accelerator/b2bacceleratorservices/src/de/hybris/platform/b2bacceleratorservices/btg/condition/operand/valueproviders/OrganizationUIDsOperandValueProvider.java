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
package de.hybris.platform.b2bacceleratorservices.btg.condition.operand.valueproviders;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorservices.model.btg.BTGOrganizationUIDsOperandModel;
import de.hybris.platform.btg.condition.operand.OperandValueProvider;
import de.hybris.platform.btg.condition.operand.types.StringSet;
import de.hybris.platform.btg.condition.operand.valueproviders.AbstractOrderOperandValueProvider;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.core.model.user.UserModel;


public class OrganizationUIDsOperandValueProvider extends AbstractOrderOperandValueProvider implements
		OperandValueProvider<BTGOrganizationUIDsOperandModel>
{
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Override
	public Object getValue(final BTGOrganizationUIDsOperandModel operand, final UserModel user,
			final BTGConditionEvaluationScope evaluationScope)
	{

		final StringSet organizationUIDs = new StringSet();
		organizationUIDs.addAll(operand.getOrganizationUids());
		return organizationUIDs;
	}

	@Override
	public Class getValueType(final BTGOrganizationUIDsOperandModel operand)
	{

		return java.util.Collection.class;
	}


	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}
