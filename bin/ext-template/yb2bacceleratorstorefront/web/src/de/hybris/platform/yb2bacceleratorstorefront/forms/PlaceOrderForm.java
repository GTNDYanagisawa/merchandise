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

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;
import de.hybris.platform.cronjob.enums.DayOfWeek;

import javax.validation.constraints.AssertTrue;
import java.util.Date;
import java.util.List;


/**
 * Pojo for 'place order' form.
 */
public class PlaceOrderForm
{
	private String securityCode;
	private boolean termsCheck;
	private String quoteRequestDescription;
	private boolean negotiateQuote;
	private boolean replenishmentOrder;
	private Date replenishmentStartDate;
	private String nDays;
	private String nWeeks;
	private String nthDayOfMonth;
	private B2BReplenishmentRecurrenceEnum replenishmentRecurrence;
	private List<DayOfWeek> nDaysOfWeek;


	public String getQuoteRequestDescription()
	{
		return quoteRequestDescription;
	}

	public void setQuoteRequestDescription(final String quoteRequestDescription)
	{
		this.quoteRequestDescription = quoteRequestDescription;
	}

	public String getSecurityCode()
	{
		return securityCode;
	}

	public void setSecurityCode(final String securityCode)
	{
		this.securityCode = securityCode;
	}

	@AssertTrue(message = "")
	public boolean isTermsCheck()
	{
		return termsCheck;
	}

	public void setTermsCheck(final boolean termsCheck)
	{
		this.termsCheck = termsCheck;
	}

	public boolean isNegotiateQuote()
	{
		return negotiateQuote;
	}

	public void setNegotiateQuote(final boolean negotiateQuote)
	{
		this.negotiateQuote = negotiateQuote;
	}

	public Date getReplenishmentStartDate()
	{
		return replenishmentStartDate;
	}

	public void setReplenishmentStartDate(final Date replenishmentStartDate)
	{
		this.replenishmentStartDate = replenishmentStartDate;
	}

	public String getnDays()
	{
		return nDays;
	}

	public void setnDays(final String nDays)
	{
		this.nDays = nDays;
	}

	public String getnWeeks()
	{
		return nWeeks;
	}

	public void setnWeeks(final String nWeeks)
	{
		this.nWeeks = nWeeks;
	}

	public String getNthDayOfMonth()
	{
		return nthDayOfMonth;
	}

	public void setNthDayOfMonth(final String nthDayOfMonth)
	{
		this.nthDayOfMonth = nthDayOfMonth;
	}

	public B2BReplenishmentRecurrenceEnum getReplenishmentRecurrence()
	{
		return replenishmentRecurrence;
	}

	public void setReplenishmentRecurrence(final B2BReplenishmentRecurrenceEnum replenishmentRecurrence)
	{
		this.replenishmentRecurrence = replenishmentRecurrence;
	}

	public List<DayOfWeek> getnDaysOfWeek()
	{
		return nDaysOfWeek;
	}

	public void setnDaysOfWeek(final List<DayOfWeek> nDaysOfWeek)
	{
		this.nDaysOfWeek = nDaysOfWeek;
	}

	public boolean isReplenishmentOrder()
	{
		return replenishmentOrder;
	}

	public void setReplenishmentOrder(final boolean replenishmentOrder)
	{
		this.replenishmentOrder = replenishmentOrder;
	}
}
