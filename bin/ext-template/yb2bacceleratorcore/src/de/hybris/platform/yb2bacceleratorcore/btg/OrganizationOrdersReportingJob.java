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
package de.hybris.platform.yb2bacceleratorcore.btg;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.yb2bacceleratorcore.model.btg.OrganizationOrderStatisticsModel;
import de.hybris.platform.yb2bacceleratorcore.model.btg.OrganizationOrdersReportingCronJobModel;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class OrganizationOrdersReportingJob extends AbstractJobPerformable<OrganizationOrdersReportingCronJobModel>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(OrganizationOrdersReportingJob.class);
	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
	protected CommonI18NService commonI18NService;
	protected List<String> currencies;
	protected List<String> categories;
	protected String catalog;
	private CatalogVersionService catalogVersionService;


	@Override
	public PerformResult perform(final OrganizationOrdersReportingCronJobModel cronJob)
	{

		for (final B2BUnitModel unit : getAllRootUnits())
		{
			final Set<B2BUnitModel> branch = b2BUnitService.getBranch(unit);
			final Date today = new Date();
			catalogVersionService.setSessionCatalogVersion(catalog, "Online");

			for (final String currencyIsoCode : currencies)
			{
				final CurrencyModel currency = commonI18NService.getCurrency(currencyIsoCode);

				if (CollectionUtils.isNotEmpty(categories))
				{
					for (final String category : categories)
					{
						// clear stats if exist for a unit for the same data and currency
						final List<OrganizationOrderStatisticsModel> stats = getOrganizationOrderStatistics(unit, currency, today,
								category);
						if (CollectionUtils.isNotEmpty(stats))
						{
							modelService.removeAll(stats);
						}
						final Double total = getTotalOfOrderEntriesFilteredByProductCategory(branch, currency, today, category);

						final OrganizationOrderStatisticsModel organizationStats = modelService
								.create(OrganizationOrderStatisticsModel.class);
						organizationStats.setUnit(unit);
						organizationStats.setDate(today);
						organizationStats.setCurrency(currency);
						organizationStats.setOrderTotal(total);
						organizationStats.setCategory(category);
						organizationStats.setCatalog(catalog);
						modelService.save(organizationStats);
					}
				}
				else
				{
					// clear stats if exist for a unit for the same data and currency
					final List<OrganizationOrderStatisticsModel> stats = getOrganizationOrderStatistics(unit, currency, today, null);
					if (CollectionUtils.isNotEmpty(stats))
					{
						modelService.removeAll(stats);
					}
					final Double total = getTotalOrdersForBranch(branch, currency, today);


					final OrganizationOrderStatisticsModel organizationStats = modelService
							.create(OrganizationOrderStatisticsModel.class);
					organizationStats.setUnit(unit);
					organizationStats.setDate(today);
					organizationStats.setCurrency(currency);
					organizationStats.setOrderTotal(total);
					organizationStats.setCatalog(catalog);
					modelService.save(organizationStats);
				}
			}
		}

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

	}

	private List<OrganizationOrderStatisticsModel> getOrganizationOrderStatistics(final B2BUnitModel unit,
			final CurrencyModel currency, Date today, final String category)
	{
		String queryString = " select {stats:pk} from {OrganizationOrderStatistics as stats}   "
				+ " where {stats:currency} = ?currency and {stats:unit} = ?unit                      "
				+ " and {stats:date} >= ?today and {stats:date} < ?tomorrow 						 ";
		if (StringUtils.isNotEmpty(category))
		{
			queryString += " and {category} = ?category";
		}

		today = DateUtils.setHours(today, 0);
		today = DateUtils.setMinutes(today, 0);
		today = DateUtils.setSeconds(today, 0);
		Date tomorrow = DateUtils.addDays(today, 1);
		tomorrow = DateUtils.setHours(tomorrow, 0);
		tomorrow = DateUtils.setMinutes(tomorrow, 0);
		tomorrow = DateUtils.setSeconds(tomorrow, 0);


		final Map<String, Object> attr = new HashMap<String, Object>(4);
		attr.put(OrderModel.CURRENCY, currency);
		attr.put("unit", unit);
		attr.put("today", today);
		attr.put("tomorrow", tomorrow);
		attr.put("category", category);


		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.getQueryParameters().putAll(attr);
		final SearchResult<OrganizationOrderStatisticsModel> searchResult = flexibleSearchService.search(query);
		return searchResult.getResult();

	}

	protected Double getTotalOrdersForBranch(final Set<B2BUnitModel> branch, final CurrencyModel currency, Date today)
	{
		final String queryString = " select sum({order:totalPrice}) from {Order as order}            		"
				+ " where {order:currency} = ?currency and {order:unit} in (?branch)                 		"
				+ " and {order:date} >= ?today and {order:date} < ?tomorrow	 AND {order:versionID} is null	";

		today = DateUtils.setHours(today, 0);
		today = DateUtils.setMinutes(today, 0);
		today = DateUtils.setSeconds(today, 0);
		Date tomorrow = DateUtils.addDays(today, 1);
		tomorrow = DateUtils.setHours(tomorrow, 0);
		tomorrow = DateUtils.setMinutes(tomorrow, 0);
		tomorrow = DateUtils.setSeconds(tomorrow, 0);


		final Map<String, Object> attr = new HashMap<String, Object>(4);
		attr.put(OrderModel.CURRENCY, currency);
		attr.put("branch", branch);
		attr.put("today", today);
		attr.put("tomorrow", tomorrow);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.getQueryParameters().putAll(attr);
		query.setResultClassList(Collections.singletonList(Double.class));
		final SearchResult<Double> total = flexibleSearchService.search(query);
		final Double orderTotal = total.getResult().iterator().hasNext() ? total.getResult().iterator().next() : Double.valueOf(0D);
		return (orderTotal != null ? orderTotal : Double.valueOf(0D));

	}

	protected Double getTotalOfOrderEntriesFilteredByProductCategory(final Set<B2BUnitModel> branch, final CurrencyModel currency,
			Date today, final String categoryCode)
	{
		final String queryString = " SELECT sum({entry:totalPrice}) from			  "
				+ " {OrderEntry AS entry                                              "
				+ " JOIN Order AS order ON {order.pk} = {entry:order}                 "
				+ " AND {order:versionId} IS NULL                                     "
				+ " AND {order:date} >= ?today and {order:date} < ?tomorrow           " + " AND {order:unit} IN (?branch)									  "
				+ " AND {order:currency} = ?currency							      "
				+ " JOIN Product AS p ON {p.PK} = {entry:product}                     "
				+ " JOIN CategoryProductRelation AS rel ON {p.PK} = {rel.target}      "
				+ " JOIN Category AS cat ON {cat.PK} = {rel.source}                   "
				+ " AND {cat.code} = ?category                                        "
				+ " } 				                                                  ";
		today = DateUtils.setHours(today, 0);
		today = DateUtils.setMinutes(today, 0);
		today = DateUtils.setSeconds(today, 0);
		Date tomorrow = DateUtils.addDays(today, 1);
		tomorrow = DateUtils.setHours(tomorrow, 0);
		tomorrow = DateUtils.setMinutes(tomorrow, 0);
		tomorrow = DateUtils.setSeconds(tomorrow, 0);


		final Map<String, Object> attr = new HashMap<String, Object>(5);
		attr.put(OrderModel.CURRENCY, currency);
		attr.put("branch", branch);
		attr.put("today", today);
		attr.put("tomorrow", tomorrow);
		attr.put("category", categoryCode);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.getQueryParameters().putAll(attr);
		query.setResultClassList(Collections.singletonList(Double.class));
		final SearchResult<Double> total = flexibleSearchService.search(query);
		final Double orderTotal = total.getResult().iterator().hasNext() ? total.getResult().iterator().next() : Double.valueOf(0D);
		return (orderTotal != null ? orderTotal : Double.valueOf(0D));

	}

	protected List<B2BUnitModel> getAllRootUnits()
	{
		final String queryString = " select {unit:pk} from {b2bunit as unit}                  "
				+ " where {unit:pk} not in                                                    "
				+ " (                                                                         "
				+ " {{ select {unit_rel:source}  from                                         "
				+ " {                                                                         "
				+ "  PrincipalGroupRelation as unit_rel                                       "
				+ "  JOIN B2BUnit as unit_source ON {unit_rel:source} = {unit_source:pk}      "
				+ "  JOIN B2BUnit as unit_target ON {unit_rel:target} = {unit_target:pk}      "
				+ " }                                                                         "
				+ "  }}                                                                       "
				+ " )                                                                         ";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		final SearchResult<B2BUnitModel> units = flexibleSearchService.search(query);
		return units.getResult();


	}

	@Required
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService)
	{
		this.b2BUnitService = b2BUnitService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	@Required
	public void setCurrencies(final List<String> currencies)
	{
		this.currencies = currencies;
	}

	public void setCategories(final List<String> categories)
	{
		this.categories = categories;
	}

	public void setCatalog(final String catalog)
	{
		this.catalog = catalog;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
