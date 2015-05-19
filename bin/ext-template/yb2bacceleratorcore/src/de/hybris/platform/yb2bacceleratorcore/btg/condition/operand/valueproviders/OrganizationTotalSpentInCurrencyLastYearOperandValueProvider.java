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
import de.hybris.platform.yb2bacceleratorcore.model.btg.BTGOrganizationTotalSpentInCurrencyLastYearOperandModel;
import de.hybris.platform.btg.condition.operand.OperandValueProvider;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;



public class OrganizationTotalSpentInCurrencyLastYearOperandValueProvider extends AbstractTotalSpentByOrgOperandProvider
		implements OperandValueProvider<BTGOrganizationTotalSpentInCurrencyLastYearOperandModel>
{

	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Override
	public Object getValue(final BTGOrganizationTotalSpentInCurrencyLastYearOperandModel operand, final UserModel user,
			final BTGConditionEvaluationScope evaluationScope)
	{
		if (user instanceof B2BCustomerModel)
		{
			final B2BUnitModel unit = getB2bUnitService().getRootUnit(getB2bUnitService().getParent((B2BCustomerModel) user));

			Date startDateInclusive = DateUtils.addYears(new Date(), -1);
			startDateInclusive = DateUtils.setHours(startDateInclusive, 0);
			startDateInclusive = DateUtils.setMinutes(startDateInclusive, 0);
			startDateInclusive = DateUtils.setSeconds(startDateInclusive, 0);
			startDateInclusive = DateUtils.setMonths(startDateInclusive, Calendar.JANUARY);
			startDateInclusive = DateUtils.setDays(startDateInclusive, 1);

			final Date endDateNonInclusive = DateUtils.addYears(startDateInclusive, 1);

			final double total = getTotalSpentByBranch(unit, operand.getCurrency(), startDateInclusive, endDateNonInclusive,
					operand.getProductCatalogId(), operand.getCategoryCode());
			return Double.valueOf(total);
		}
		return Double.valueOf(0);
	}

	@Override
	public Class getValueType(final BTGOrganizationTotalSpentInCurrencyLastYearOperandModel operand)
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
