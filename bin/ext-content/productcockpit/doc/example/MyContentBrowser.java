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
package mypackage;

public class MyContentBrowser extends XContentBrowser
{
	@Override
	protected AbstractBrowserComponent createMainAreaComponent()
	{
		return new MyBrowserComponent(this.getModel(), this.browserController);
	}
}
