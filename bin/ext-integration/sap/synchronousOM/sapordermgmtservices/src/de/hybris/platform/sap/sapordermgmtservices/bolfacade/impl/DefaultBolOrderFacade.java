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
package de.hybris.platform.sap.sapordermgmtservices.bolfacade.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.interactionobjects.interf.CheckDocumentValid;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.Search;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchFilter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolOrderFacade;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;

import java.math.BigInteger;
import java.util.List;


/**
 * 
 */
public class DefaultBolOrderFacade implements BolOrderFacade
{
	private GenericFactory genericFactory;
	private CheckDocumentValid sapCheckDocumentValid;
	private SapPartnerService sapPartnerService;

	/**
	 * @return the genericFactory
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}

	/**
	 * @param genericFactory
	 *           the genericFactory to set
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/**
	 * Get order search business object
	 * 
	 * @return Order search BO implementation
	 */
	protected Search getSearch()
	{
		return genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_SEARCH);
	}

	@Override
	public Integer getSearchResultsTotalNumber()
	{
		final Search search = getSearch();
		return Integer.valueOf(search.getSearchResultsTotalNumber());
	}


	Order getSavedOrder()
	{
		return (Order) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_ORDER_HISTORY);
	}

	@Override
	public Order getSavedOrder(final String orderId)
	{
		final Order order = getSavedOrder();
		order.setTechKey(new TechKey(addLeadingZerosOrderID(orderId)));

		try
		{
			order.read();
			final boolean soldToMatches = getSapCheckDocumentValid().hasPermissions(order,
					sapPartnerService.getCurrentSapCustomerId());
			final boolean documentTypeMatches = getSapCheckDocumentValid().isDocumentSupported(order, DocumentType.ORDER);
			if ((!soldToMatches) || (!documentTypeMatches))
			{
				throw new ApplicationBaseRuntimeException("You are not allowed to see Order: " + orderId);
			}


		}
		catch (final CommunicationException e)
		{
			throw new ApplicationBaseRuntimeException("Could not read order for code: " + orderId);
		}
		return order;
	}

	/**
	 * @return the documentValidator
	 */
	public CheckDocumentValid getSapCheckDocumentValid()
	{
		if (sapCheckDocumentValid != null)
		{
			return sapCheckDocumentValid;
		}

		sapCheckDocumentValid = (CheckDocumentValid) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_INT_CHECK_DOCUMENT_VALID);

		return sapCheckDocumentValid;
	}

	/**
	 * @param documentValidator
	 *           the documentValidator to set
	 */
	public void setSapCheckDocumentValid(final CheckDocumentValid documentValidator)
	{
		this.sapCheckDocumentValid = documentValidator;
	}

	/**
	 * @return the sapPartnerService
	 */
	public SapPartnerService getSapPartnerService()
	{
		return sapPartnerService;
	}

	/**
	 * @param sapPartnerService
	 *           the sapPartnerService to set
	 */
	public void setSapPartnerService(final SapPartnerService sapPartnerService)
	{
		this.sapPartnerService = sapPartnerService;
	}

	String addLeadingZerosOrderID(final String productId)
	{

		return addLeadingZeros(productId, "0000000000");
	}

	String addLeadingZeros(final String input, final String fillString)
	{

		if (input == null)
		{
			return "";
		}

		if (input.length() > fillString.length())
		{
			return input;
		}

		try
		{
			new BigInteger(input);
		}
		catch (final NumberFormatException ex)
		{
			// $JL-EXC$
			// This exception might occur if the method is
			// called for non-numerical inputs
			return input.trim();
		}
		catch (final NullPointerException ex)
		{
			// $JL-EXC$
			// This exception might occur if the method is
			// called for non-numerical inputs
			return "";
		}

		return fillString.substring(input.length()) + input;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolOrderFacade#performSearch(de.hybris.platform.sap.
	 * sapordermgmtbol.transaction.salesdocument.search.interf.SearchFilter,
	 * de.hybris.platform.commerceservices.search.pagedata.PageableData)
	 */
	@Override
	public List<SearchResult> performSearch(final SearchFilter searchFilter, final PageableData pageableData)
	{

		final Search search = getSearch();

		return search.getSearchResult(searchFilter, pageableData);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolOrderFacade#getDateRange()
	 */
	@Override
	public int getDateRange()
	{
		final Search search = getSearch();

		return search.getDateRange();
	}


	@Override
	public List<SortData> getSearchSort()
	{
		final Search search = getSearch();
		return search.getSortOptions();
	}

	@Override
	public void setSearchDirty()
	{
		getSearch().setDirty(true);
	}
}
