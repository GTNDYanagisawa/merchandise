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
package de.hybris.platform.yb2bacceleratorstorefront.forms;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BDaysOfWeekData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;

import java.util.Collection;
import java.util.Date;


/**
 * Pojo for 'schedule replenishment' form.
 */
public class ScheduleReplenishmentForm
{
	Date replenishmentStartDate;
	Integer nDays;
	Integer nWeeks;
	Integer nthDayOfMonth;
	B2BReplenishmentRecurrenceEnum replenishmentRecurrence;
	Collection<B2BDaysOfWeekData> daysOfWeek;


	/**
	 * @return the nDays
	 */
	public Integer getnDays()
	{
		return nDays;
	}

	/**
	 * @param nDays
	 *           the nDays to set
	 */
	public void setnDays(final Integer nDays)
	{
		this.nDays = nDays;
	}

	/**
	 * @return the nWeeks
	 */
	public Integer getnWeeks()
	{
		return nWeeks;
	}

	/**
	 * @param nWeeks
	 *           the nWeeks to set
	 */
	public void setnWeeks(final Integer nWeeks)
	{
		this.nWeeks = nWeeks;
	}

	/**
	 * @return the replenishmentRecurrence
	 */
	public B2BReplenishmentRecurrenceEnum getReplenishmentRecurrence()
	{
		return replenishmentRecurrence;
	}

	/**
	 * @param replenishmentRecurrence
	 *           the replenishmentRecurrence to set
	 */
	public void setReplenishmentRecurrence(final B2BReplenishmentRecurrenceEnum replenishmentRecurrence)
	{
		this.replenishmentRecurrence = replenishmentRecurrence;
	}

	/**
	 * @return the daysOfWeek
	 */
	public Collection<B2BDaysOfWeekData> getDaysOfWeek()
	{
		return daysOfWeek;
	}

	/**
	 * @param daysOfWeek
	 *           the daysOfWeek to set
	 */
	public void setDaysOfWeek(final Collection<B2BDaysOfWeekData> daysOfWeek)
	{
		this.daysOfWeek = daysOfWeek;
	}

	/**
	 * @return the replenishmentStartDate
	 */
	public Date getReplenishmentStartDate()
	{
		return replenishmentStartDate;
	}

	/**
	 * @param replenishmentStartDate
	 *           the replenishmentStartDate to set
	 */
	public void setReplenishmentStartDate(final Date replenishmentStartDate)
	{
		this.replenishmentStartDate = replenishmentStartDate;
	}

	/**
	 * @return the nthDayOfMonth
	 */
	public Integer getNthDayOfMonth()
	{
		return nthDayOfMonth;
	}

	/**
	 * @param nthDayOfMonth
	 *           the nthDayOfMonth to set
	 */
	public void setNthDayOfMonth(final Integer nthDayOfMonth)
	{
		this.nthDayOfMonth = nthDayOfMonth;
	}
}
