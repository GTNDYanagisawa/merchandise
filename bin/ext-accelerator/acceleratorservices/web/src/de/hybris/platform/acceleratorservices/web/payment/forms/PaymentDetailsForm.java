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
package de.hybris.platform.acceleratorservices.web.payment.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


/**
 */
public class PaymentDetailsForm
{
	private String paymentId;
	private String cardTypeCode;
	private String cardNumber;
	private String startMonth;
	private String startYear;
	private String expiryMonth;
	private String expiryYear;
	private String issueNumber;
	private String verificationNumber;

	private AddressForm billingAddress;

	private String mockReasonCode;

	private String originalParameters;
	private Boolean showDebugPage;

	public String getPaymentId()
	{
		return paymentId;
	}

	public void setPaymentId(final String paymentId)
	{
		this.paymentId = paymentId;
	}

	@NotNull(message = "{payment.cardType.invalid}")
	@Size(min = 1, max = 255, message = "{payment.cardType.invalid}")
	public String getCardTypeCode()
	{
		return cardTypeCode;
	}

	public void setCardTypeCode(final String cardTypeCode)
	{
		this.cardTypeCode = cardTypeCode;
	}

	@NotNull(message = "{payment.cardNumber.invalid}")
	@Size(min = 16, max = 16, message = "{payment.cardNumber.invalid}")
	@Pattern(regexp = "(^$|^?\\d*$)", message = "{payment.cardNumber.invalid}")
	public String getCardNumber()
	{
		return cardNumber;
	}

	public void setCardNumber(final String cardNumber)
	{
		this.cardNumber = cardNumber;
	}

	public String getStartMonth()
	{
		return startMonth;
	}

	public void setStartMonth(final String startMonth)
	{
		this.startMonth = startMonth;
	}

	public String getStartYear()
	{
		return startYear;
	}

	public void setStartYear(final String startYear)
	{
		this.startYear = startYear;
	}

	@NotNull(message = "{payment.expiryMonth.invalid}")
	@Size(min = 1, max = 2, message = "{payment.expiryMonth.invalid}")
	public String getExpiryMonth()
	{
		return expiryMonth;
	}

	public void setExpiryMonth(final String expiryMonth)
	{
		this.expiryMonth = expiryMonth;
	}

	@NotNull(message = "{payment.expiryYear.invalid}")
	@Size(min = 2, max = 4, message = "{payment.expiryYear.invalid}")
	public String getExpiryYear()
	{
		return expiryYear;
	}

	public void setExpiryYear(final String expiryYear)
	{
		this.expiryYear = expiryYear;
	}

	@Pattern(regexp = "(^$|^?\\d*$)", message = "{payment.issueNumber.invalid}")
	@Size(min = 1, max = 16, message = "{payment.issueNumber.invalid}")
	public String getIssueNumber()
	{
		return issueNumber;
	}

	public void setIssueNumber(final String issueNumber)
	{
		this.issueNumber = issueNumber;
	}

	//	@Valid
	public AddressForm getBillingAddress()
	{
		return billingAddress;
	}

	public void setBillingAddress(final AddressForm billingAddress)
	{
		this.billingAddress = billingAddress;
	}

	/**
	 * @return the verificationNumber
	 */
	@NotNull(message = "{payment.verificationNumber.invalid}")
	@Size(min = 3, max = 4, message = "{payment.verificationNumber.invalid}")
	public String getVerificationNumber()
	{
		return verificationNumber;
	}

	/**
	 * @param verificationNumber
	 *           the verificationNumber to set
	 */
	public void setVerificationNumber(final String verificationNumber)
	{
		this.verificationNumber = verificationNumber;
	}

	/**
	 * @return the mockReasonCode
	 */
	public String getMockReasonCode()
	{
		return mockReasonCode;
	}

	/**
	 * @param mockReasonCode
	 *           the mockResponse to set
	 */
	public void setMockReasonCode(final String mockReasonCode)
	{
		this.mockReasonCode = mockReasonCode;
	}

	/**
	 * @return the originalParameters
	 */
	public String getOriginalParameters()
	{
		return originalParameters;
	}

	/**
	 * @param originalParameters
	 *           the originalParameters to set
	 */
	public void setOriginalParameters(final String originalParameters)
	{
		this.originalParameters = originalParameters;
	}

	/**
	 * @return the showDebugPage
	 */
	public Boolean getShowDebugPage()
	{
		return showDebugPage;
	}

	/**
	 * @param showDebugPage
	 *           the showDebugPage to set
	 */
	public void setShowDebugPage(final Boolean showDebugPage)
	{
		this.showDebugPage = showDebugPage;
	}
}
