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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


public class AdvancedSearchForm
{
	private String keywords;
	private List<String> productIdsList;
	private Boolean onlyProductIds;
	private Boolean exactMatch;
	private Boolean inStockOnly;
	private String searchResultType;
	private List<String> filterSkus;
	private Boolean createOrderForm;

	public AdvancedSearchForm()
	{
		keywords = StringUtils.EMPTY;
		searchResultType = "catalog";
	}

	public String getKeywords()
	{
		return keywords;
	}

	public void setKeywords(final String keywords)
	{
		this.keywords = keywords;
	}

	public List<String> getProductIdsList()
	{
		if (StringUtils.isNotBlank(getKeywords()))
		{
			if (CollectionUtils.isEmpty(productIdsList))
			{
				this.productIdsList = new ArrayList<>();
			}
			this.productIdsList.addAll(Arrays.asList(StringUtils.split(getKeywords(), ",; ")));
		}
		return productIdsList;
	}

	public boolean isCatalogSearchResultType()
	{
		return StringUtils.equals(searchResultType, "catalog");
	}

	public boolean isOrderFormSearchResultType()
	{
		return StringUtils.equals(searchResultType, "order-form");
	}

	public boolean isCreateOrderFormSearchResultType()
	{
		return StringUtils.equals(searchResultType, "create-order-form");
	}


	public String getSearchResultType()
	{
		return searchResultType;
	}

	public void setSearchResultType(final String searchResultType)
	{
		this.searchResultType = searchResultType;
	}

	public Boolean getExactMatch()
	{
		return exactMatch;
	}

	public Boolean getInStockOnly()
	{
		return inStockOnly;
	}

	public Boolean getOnlyProductIds()
	{
		return onlyProductIds;
	}

	public void setOnlyProductIds(final Boolean onlyProductIds)
	{
		this.onlyProductIds = onlyProductIds;
	}

	public void setExactMatch(final Boolean exactMatch)
	{
		this.exactMatch = exactMatch;
	}

	public void setInStockOnly(final Boolean inStockOnly)
	{
		this.inStockOnly = inStockOnly;
	}

	public void setProductIdsList(final List<String> productIdsList)
	{
		this.productIdsList = productIdsList;
	}


	public List<String> getFilterSkus()
	{
		return filterSkus;
	}


	public void setFilterSkus(final List<String> filterSkus)
	{
		this.filterSkus = filterSkus;
	}

	public Boolean getCreateOrderForm()
	{
		return createOrderForm;
	}

	public void setCreateOrderForm(final Boolean createOrderForm)
	{
		this.createOrderForm = createOrderForm;
	}
}
