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
package de.hybris.platform.yb2bacceleratorcore.checkout.pci;

import de.hybris.platform.yb2bacceleratorcore.enums.B2BCheckoutPciOptionEnum;


/**
 * 
 */
public interface B2BCheckoutPciStrategy
{
	/**
	 * Returns one of the possible {@link B2BCheckoutPciOptionEnum} values - to select the PCI options.
	 */
	B2BCheckoutPciOptionEnum getSubscriptionPciOption();
}
