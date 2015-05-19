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

public class MyBrowserModel extends AbstractBrowserModel implements Cloneable
{
	private boolean foo = false;

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return new MyBrowserModel();
	}

	@Override
	public AbstractContentBrowser createViewComponent()
	{
		return new MyContentBrowser();
	}

	@Override
	public List<? extends TypedObject> getItems()
	{
		return null;
	}

	@Override
	public boolean isSearchable()
	{
		return false;
	}

	@Override
	public void updateItems()
	{
		//TODO why empty?
	}

	@Override
	public void updateItems(final int activePage)
	{
		//TODO why empty?
	}

	@Override
	public boolean isPagable()
	{
		return false;
	}

	public void setFoo(final boolean foo)
	{
		if (this.foo != foo)
		{
			this.foo = foo;
			this.fireBrowserChanged();
		}
	}

	public boolean isFoo()
	{
		return foo;
	}

}