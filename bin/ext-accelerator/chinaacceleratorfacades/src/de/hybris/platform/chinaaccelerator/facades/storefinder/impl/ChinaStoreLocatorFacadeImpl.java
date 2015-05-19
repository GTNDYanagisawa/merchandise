/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.storefinder.impl;

import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.chinaaccelerator.facades.StoreData;
import de.hybris.platform.chinaaccelerator.facades.data.CityData;
import de.hybris.platform.chinaaccelerator.facades.storefinder.ChinaStoreLocatorFacade;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


//import de.hybris.platform.core.model.user.CityModel;



public class ChinaStoreLocatorFacadeImpl extends de.hybris.platform.commercefacades.storelocator.impl.DefaultStoreLocatorFacade
		implements ChinaStoreLocatorFacade
{
	private FlexibleSearchService flexibleSearchService;

	//private static final String QUERY_CITIES_ONLY_WITH_STORES = "select {c.pk}, count({p.pk}) from {PointOfService AS p JOIN Address AS a ON {p:pk} = {a:owner} JOIN City AS c ON {c:pk} = {a:city}} WHERE {p:type} = ?t Group by {a:city} Order by {c:sortOrder} DESC";
	private static final String QUERY_CITIES_ONLY_WITH_STORES = "SELECT {c.pk}, COUNT({p.pk}), {c:sortOrder} FROM {PointOfService AS p JOIN Basestore as b on {p:basestore}={b.pk} Join "
			+ " Address AS a ON {p:pk} = {a:owner} JOIN City AS c ON {c:pk} = {a:city}} WHERE {p:type} = ?t GROUP BY {a:city}, {c.pk} ORDER BY {c:sortOrder} DESC";

	//private static final String QUERY_ALL_CITIES = "select {c.pk}, count({p.pk})  from {PointOfService AS p JOIN Address AS a ON {p:pk} = {a:owner} and {p:type} = ?t RIGHT JOIN City AS c ON {a:city} ={c:pk}} Group by {c:pk} Order by {c:sortOrder} DESC";
	private static final String QUERY_ALL_CITIES = "select {c.pk}, 0  from { City AS c } Order by {c.sortOrder} DESC";

	private static final String QUERY_STORES_OF_CITY = "select {p.pk} from {PointOfService AS p JOIN Basestore as b on {p:basestore}={b.pk} Join Address AS a ON {p:pk} = {a:owner}} WHERE {p:type} = ?t and {a:city} =?cityId Order by {p:sortOrder} DESC";

	private static final String QUERY_ALL_CITIES_WITH_AND_WITHOUT_STORES = "SELECT unionAllCitiesStoreCounts.citypk,unionAllCitiesStoreCounts.storecount FROM ({{SELECT {c.pk} AS citypk,COUNT({p.pk}) AS storecount, {c.sortOrder} AS rank FROM {PointOfService AS p JOIN Address AS a ON {p:pk}={a:owner} JOIN City AS c ON {c:pk}={a:city}} GROUP BY {c:pk}}} UNION {{SELECT {c2.pk},0,{c2.sortOrder} FROM {city AS c2 } WHERE NOT EXISTS ({{SELECT {c3.pk} FROM {PointOfService AS p3 JOIN Address AS a3 ON {p3:pk}={a3:owner} JOIN City AS c3 ON {c3:pk}={a3:city} and {c3:pk}={c2.pk}}}})}}) unionAllCitiesStoreCounts ORDER BY unionAllCitiesStoreCounts.rank DESC";


	@Override
	public List<CityData> getCitiesOnlyWithStores()
	{
		return getCities(QUERY_CITIES_ONLY_WITH_STORES);
	}

	public List<CityData> getAllCities2()
	{
		final List<CityData> allCities = getCities(QUERY_ALL_CITIES);
		final List<CityData> citiesOnlyWithStores = getCitiesOnlyWithStores();
		final Iterator<CityData> destIter = allCities.iterator();
		for (final Iterator<CityData> srcIter = citiesOnlyWithStores.iterator(); srcIter.hasNext();)
		{
			final CityData srcData = srcIter.next();

			while (destIter.hasNext())
			{
				final CityData destData = destIter.next();
				if (srcData.getCityPK().longValue() == destData.getCityPK().longValue())
				{
					destData.setStoreCount(srcData.getStoreCount());
					break;
				}
			}
		}

		return allCities;
	}

	@Override
	public List<CityData> getAllCities()
	{
		return this.getCities(QUERY_ALL_CITIES_WITH_AND_WITHOUT_STORES);
	}


	public List<CityData> getCities(final String cityQuery)
	{
		//TODO: populator for citydata? PK value or cityModel.code?
		final FlexibleSearchQuery query = new FlexibleSearchQuery(cityQuery);
		query.setResultClassList(Arrays.asList(CityModel.class, Integer.class));
		query.addQueryParameter("t", PointOfServiceTypeEnum.STORE);
		final SearchResult<List> qResult = flexibleSearchService.search(query);

		final List<CityData> result = new ArrayList<CityData>();
		for (final Iterator<List> iter = qResult.getResult().iterator(); iter.hasNext();)
		{

			//TODO: switch to CityPopuplator 
			final List row = iter.next();
			final CityModel cityModel = (CityModel) row.get(0);
			final int storesCount = (int) row.get(1);
			final CityData cityData = new CityData();
			cityData.setCityName(cityModel.getName());
			cityData.setCityPK(cityModel.getPk().getLong());
			cityData.setStoreCount(storesCount);
			final GeoPoint point = new GeoPoint();
			if (cityModel.getLatitude() != null)
			{
				point.setLatitude(cityModel.getLatitude());
			}
			if (cityModel.getLongitude() != null)
			{
				point.setLongitude(cityModel.getLongitude());
			}
			cityData.setGeoPoint(point);
			result.add(cityData);
		}
		return result;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public List<StoreData> getStoresByCities(final long cityId)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY_STORES_OF_CITY);
		query.addQueryParameter("t", PointOfServiceTypeEnum.STORE);
		query.addQueryParameter("cityId", Long.valueOf(cityId));
		final SearchResult<PointOfServiceModel> qResult = flexibleSearchService.search(query);

		final List<StoreData> result = new ArrayList<StoreData>();
		for (final Iterator<PointOfServiceModel> iter = qResult.getResult().iterator(); iter.hasNext();)
		{
			final PointOfServiceModel posModel = iter.next();
			final StoreData storeData = new StoreData();
			storeData.setStorePK(posModel.getPk().getLong());
			getPointOfServiceConverter().convert(posModel, storeData);
			result.add(storeData);
		}
		return result;
	}

}
