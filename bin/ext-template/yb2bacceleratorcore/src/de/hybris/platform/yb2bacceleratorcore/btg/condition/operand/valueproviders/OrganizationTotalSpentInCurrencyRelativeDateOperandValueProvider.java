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
package de.hybris.platform.yb2bacceleratorcore.btg.condition.operand.valueproviders;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.yb2bacceleratorcore.model.btg.BTGOrganizationTotalSpentInCurrencyRelativeDatesOperandModel;
import de.hybris.platform.btg.condition.operand.OperandValueProvider;
import de.hybris.platform.btg.condition.operand.valueproviders.OperandValueProviderUtils;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Date;


public class OrganizationTotalSpentInCurrencyRelativeDateOperandValueProvider extends AbstractTotalSpentByOrgOperandProvider
		implements OperandValueProvider<BTGOrganizationTotalSpentInCurrencyRelativeDatesOperandModel>
{

	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Override
	public Object getValue(final BTGOrganizationTotalSpentInCurrencyRelativeDatesOperandModel operand, final UserModel user,
			final BTGConditionEvaluationScope evaluationScope)
	{
		if (user instanceof B2BCustomerModel)
		{
			Date fromDate = null;
			if ((operand.getUnit() != null) && (operand.getValue() != null))
			{
				fromDate = OperandValueProviderUtils.getDateFromInterval(operand.getUnit(), operand.getValue().intValue());
			}

			final B2BUnitModel unit = getB2bUnitService().getRootUnit(getB2bUnitService().getParent((B2BCustomerModel) user));
			final double total = getTotalSpentByBranch(unit, operand.getCurrency(), fromDate, null, operand.getProductCatalogId(),
					operand.getCategoryCode());
			return Double.valueOf(total);
		}
		return Double.valueOf(0);
	}

	@Override
	public Class getValueType(final BTGOrganizationTotalSpentInCurrencyRelativeDatesOperandModel operand)
	{
		return Double.class;
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
