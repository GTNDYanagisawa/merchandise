/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-$year hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

package de.hybris.platform.chinaaccelerator.services.order.daos.impl;

import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;
import de.hybris.platform.chinaaccelerator.services.order.daos.ChinaZoneDeliveryModeValueDao;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.deliveryzone.constants.ZoneDeliveryModeConstants;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;
import de.hybris.platform.jalo.link.Link;
import de.hybris.platform.order.daos.impl.DefaultZoneDeliveryModeValueDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.collections.CollectionUtils;


public class DefaultChinaZoneDeliveryModeValueDao extends DefaultZoneDeliveryModeValueDao implements
		ChinaZoneDeliveryModeValueDao
{
	@Override
	public Collection<ZoneDeliveryModeValueModel> findDeliveryValues(final DeliveryModeModel mode, final DistrictModel district)
	{
		final StringBuilder strBdr = new StringBuilder();
		strBdr.append("SELECT {dmv.").append(ZoneDeliveryModeValueModel.PK).append("} ").append("FROM {")
				.append(ZoneDeliveryModeValueModel._TYPECODE).append(" AS dmv}, ").append("{").append(ZoneModel._TYPECODE)
				.append(" AS z}, ").append("{").append(ZoneDeliveryModeModel._TYPECODE).append(" AS zdm}, ").append("{")
				.append(CurrencyModel._TYPECODE).append(" AS cur}, ").append("{").append(DistrictModel._TYPECODE).append(" AS d} ")
				.append("WHERE {dmv.").append(ZoneDeliveryModeValueModel.CURRENCY).append("} = {cur.").append(CurrencyModel.PK)
				.append("} ").append("AND {dmv.").append(ZoneDeliveryModeValueModel.DELIVERYMODE).append("} = {zdm.")
				.append(ZoneDeliveryModeModel.PK).append("} ").append("AND {dmv.").append(ZoneDeliveryModeValueModel.ZONE)
				.append("} = {z.").append(ZoneModel.PK).append("} ").append("AND {z.").append(ZoneModel.DISTRICT).append("} = {d.")
				.append(DistrictModel.PK).append("} ").append("AND {cur.").append(CurrencyModel.ISOCODE).append("} = 'CNY' ")
				.append("AND {d.").append(DistrictModel.PK).append("} = ?district ").append("AND {zdm.")
				.append(ZoneDeliveryModeModel.PK).append("} = ?mode ").append("AND {z.").append(ZoneModel.REGION)
				.append("} IS NULL ").append("AND {z.").append(ZoneModel.CITY).append("} IS NULL");

		final HashMap<String, Object> params = new HashMap<>();
		params.put("mode", mode);
		params.put("district", district);

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(strBdr.toString());
		fQuery.addQueryParameters(params);
		fQuery.setResultClassList(Collections.singletonList(ZoneDeliveryModeValueModel.class));
		final SearchResult<ZoneDeliveryModeValueModel> result = getFlexibleSearchService().search(fQuery);
		final Collection<ZoneDeliveryModeValueModel> vs1 = result.getResult();

		if (CollectionUtils.isNotEmpty(vs1))
		{
			return vs1;
		}
		else
		{
			final CityModel city = district.getCity();
			final Collection<ZoneDeliveryModeValueModel> vs2 = findDeliveryValues(mode, city);

			if (CollectionUtils.isNotEmpty(vs2))
			{
				return vs2;
			}
			else
			{
				final RegionModel region = city.getRegion();
				final Collection<ZoneDeliveryModeValueModel> vs3 = findDeliveryValues(mode, region);

				if (CollectionUtils.isNotEmpty(vs3))
				{
					return vs3;
				}
				else
				{
					return findDeliveryValues(mode);
				}
			}
		}
	}

	/**
	 * Finds all delivery cost values for China in CNY for the specified delivery mode
	 * 
	 * @param mode
	 *           the delivery mode
	 * @return all found {@link de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel}s, or empty list if not
	 *         found.
	 */
	protected Collection<ZoneDeliveryModeValueModel> findDeliveryValues(final DeliveryModeModel mode)
	{
		final StringBuilder strBdr = new StringBuilder();
		strBdr.append("SELECT {dmv.").append(ZoneDeliveryModeValueModel.PK).append("} ").append("FROM {")
				.append(ZoneDeliveryModeValueModel._TYPECODE).append(" AS dmv JOIN ")
				.append(ZoneDeliveryModeConstants.Relations.ZONECOUNTRYRELATION).append(" z2cRel ON {dmv.")
				.append(ZoneDeliveryModeValueModel.ZONE).append("} = {z2cRel.").append(Link.SOURCE).append("}}, ").append("{")
				.append(ZoneDeliveryModeModel._TYPECODE).append(" AS zdm}, ").append("{").append(CurrencyModel._TYPECODE)
				.append(" AS cur}, ").append("{").append(CountryModel._TYPECODE).append(" AS c} ").append("WHERE {dmv.")
				.append(ZoneDeliveryModeValueModel.CURRENCY).append("} = {cur.").append(CurrencyModel.PK).append("} ")
				.append("AND {dmv.").append(ZoneDeliveryModeValueModel.DELIVERYMODE).append("} = {zdm.")
				.append(ZoneDeliveryModeModel.PK).append("} ").append("AND {z2cRel.").append(Link.TARGET).append("} = {c.")
				.append(CountryModel.PK).append("} ").append("AND {cur.").append(CurrencyModel.ISOCODE).append("} = 'CNY' ")
				.append("AND {c.").append(CountryModel.ISOCODE).append("} = 'CN' ").append("AND {zdm.")
				.append(ZoneDeliveryModeModel.PK).append("} = ?mode ");

		final HashMap<String, Object> params = new HashMap<>();
		params.put("mode", mode);

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(strBdr.toString());
		fQuery.addQueryParameters(params);
		fQuery.setResultClassList(Collections.singletonList(ZoneDeliveryModeValueModel.class));
		final SearchResult<ZoneDeliveryModeValueModel> result = getFlexibleSearchService().search(fQuery);
		final Collection<ZoneDeliveryModeValueModel> values = result.getResult();
		return values;
	}

	/**
	 * Finds all delivery cost values for a given province in CNY for the specified delivery mode
	 * 
	 * @param mode
	 *           the delivery mode
	 * @param region
	 *           the province
	 * @return all found {@link de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel}s, or empty list if not
	 *         found.
	 */
	protected Collection<ZoneDeliveryModeValueModel> findDeliveryValues(final DeliveryModeModel mode, final RegionModel region)
	{
		final StringBuilder strBdr = new StringBuilder();
		strBdr.append("SELECT {dmv.").append(ZoneDeliveryModeValueModel.PK).append("} ").append("FROM {")
				.append(ZoneDeliveryModeValueModel._TYPECODE).append(" AS dmv}, ").append("{").append(ZoneModel._TYPECODE)
				.append(" AS z}, ").append("{").append(ZoneDeliveryModeModel._TYPECODE).append(" AS zdm}, ").append("{")
				.append(CurrencyModel._TYPECODE).append(" AS cur}, ").append("{").append(RegionModel._TYPECODE).append(" AS r} ")
				.append("WHERE {dmv.").append(ZoneDeliveryModeValueModel.CURRENCY).append("} = {cur.").append(CurrencyModel.PK)
				.append("} ").append("AND {dmv.").append(ZoneDeliveryModeValueModel.DELIVERYMODE).append("} = {zdm.")
				.append(ZoneDeliveryModeModel.PK).append("} ").append("AND {dmv.").append(ZoneDeliveryModeValueModel.ZONE)
				.append("} = {z.").append(ZoneModel.PK).append("} ").append("AND {z.").append(ZoneModel.REGION).append("} = {r.")
				.append(RegionModel.PK).append("} ").append("AND {cur.").append(CurrencyModel.ISOCODE).append("} = 'CNY' ")
				.append("AND {r.").append(RegionModel.PK).append("} = ?region ").append("AND {zdm.").append(ZoneDeliveryModeModel.PK)
				.append("} = ?mode ").append("AND {z.").append(ZoneModel.CITY).append("} IS NULL ").append("AND {z.")
				.append(ZoneModel.DISTRICT).append("} IS NULL");

		final HashMap<String, Object> params = new HashMap<>();
		params.put("mode", mode);
		params.put("region", region);

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(strBdr.toString());
		fQuery.addQueryParameters(params);
		fQuery.setResultClassList(Collections.singletonList(ZoneDeliveryModeValueModel.class));
		final SearchResult<ZoneDeliveryModeValueModel> result = getFlexibleSearchService().search(fQuery);
		final Collection<ZoneDeliveryModeValueModel> values = result.getResult();
		return values;
	}

	/**
	 * Finds all delivery cost values for a given city in CNY for the specified delivery mode
	 * 
	 * @param mode
	 *           the delivery mode
	 * @param city
	 *           the city
	 * @return all found {@link de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel}s, or empty list if not
	 *         found.
	 */
	protected Collection<ZoneDeliveryModeValueModel> findDeliveryValues(final DeliveryModeModel mode, final CityModel city)
	{
		final StringBuilder strBdr = new StringBuilder();
		strBdr.append("SELECT {dmv.").append(ZoneDeliveryModeValueModel.PK).append("} ").append("FROM {")
				.append(ZoneDeliveryModeValueModel._TYPECODE).append(" AS dmv}, ").append("{").append(ZoneModel._TYPECODE)
				.append(" AS z}, ").append("{").append(ZoneDeliveryModeModel._TYPECODE).append(" AS zdm}, ").append("{")
				.append(CurrencyModel._TYPECODE).append(" AS cur}, ").append("{").append(CityModel._TYPECODE).append(" AS c} ")
				.append("WHERE {dmv.").append(ZoneDeliveryModeValueModel.CURRENCY).append("} = {cur.").append(CurrencyModel.PK)
				.append("} ").append("AND {dmv.").append(ZoneDeliveryModeValueModel.DELIVERYMODE).append("} = {zdm.")
				.append(ZoneDeliveryModeModel.PK).append("} ").append("AND {dmv.").append(ZoneDeliveryModeValueModel.ZONE)
				.append("} = {z.").append(ZoneModel.PK).append("} ").append("AND {z.").append(ZoneModel.CITY).append("} = {c.")
				.append(CityModel.PK).append("} ").append("AND {cur.").append(CurrencyModel.ISOCODE).append("} = 'CNY' ")
				.append("AND {c.").append(CityModel.PK).append("} = ?city ").append("AND {zdm.").append(ZoneDeliveryModeModel.PK)
				.append("} = ?mode ").append("AND {z.").append(ZoneModel.REGION).append("} IS NULL ").append("AND {z.")
				.append(ZoneModel.DISTRICT).append("} IS NULL");

		final HashMap<String, Object> params = new HashMap<>();
		params.put("mode", mode);
		params.put("city", city);

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(strBdr.toString());
		fQuery.addQueryParameters(params);
		fQuery.setResultClassList(Collections.singletonList(ZoneDeliveryModeValueModel.class));
		final SearchResult<ZoneDeliveryModeValueModel> result = getFlexibleSearchService().search(fQuery);
		final Collection<ZoneDeliveryModeValueModel> values = result.getResult();
		return values;
	}
}
