// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.location.daos.impl;



import de.hybris.platform.chinaaccelerator.services.location.daos.CityDao;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;



//@Component(value = "cityDao")
public class DefaultCityDao implements CityDao
{
	//@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.chinaaccelerator.daos.CityDao#findCitiesByRegionCode(java.lang.String)
	 */
	@Override
	public List<CityModel> findCitiesByRegionCode(final String regionCode)
	{
		// select {c:pk},{c:name} from { City as c join Region as r on {c:region} = {r:pk} AND {r:isocode} = 'CN-42' }

		final String queryString = //
		"SELECT {c:" + CityModel.PK
				+ "}" //
				+ "FROM {" + CityModel._TYPECODE + " AS c JOIN " + RegionModel._TYPECODE + " AS r " + "ON {r:" + RegionModel.PK
				+ "} = {c:" + CityModel.REGION + "}  " + " AND {r:" + RegionModel.ISOCODE + "} =?paramRegionCode }"; //

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("paramRegionCode", regionCode);

		return flexibleSearchService.<CityModel> search(query).getResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.chinaaccelerator.daos.CityDao#findCityForCode(java.lang.String)
	 */
	@Override
	public CityModel findCityForCode(final String cityCode)
	{
		// select {c:pk} from { City as c} WHERE {c:code} = 'CN-11-1'

		final String queryString = //
		"SELECT {c:" + CityModel.PK + "}" //
				+ "FROM {" + CityModel._TYPECODE + " AS c } " + "WHERE " + "{c:" + CityModel.CODE + "}=?paramCityCode ";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("paramCityCode", cityCode);


		final SearchResult<CityModel> result = flexibleSearchService.<CityModel> search(query);//.getResult();
		if (result == null || result.getCount() == 0)
		{
			return null;
		}

		return result.getResult().get(0); // TODO: what if >1 items found?
	}
}