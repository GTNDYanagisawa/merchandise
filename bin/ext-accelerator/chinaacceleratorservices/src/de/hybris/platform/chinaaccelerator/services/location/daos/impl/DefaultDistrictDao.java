/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.location.daos.impl;

import de.hybris.platform.chinaaccelerator.services.location.daos.DistrictDao;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


//@Component(value = "districtDao")
public class DefaultDistrictDao implements DistrictDao
{
	//	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.chinaaccelerator.daos.DistrictDao#findDistrictByCode(java.lang.String)
	 */
	@Override
	public DistrictModel findDistrictByCode(final String districtCode)
	{
		// SELECT {d:pk} FROM {District AS d} WHERE {d:code} = 'CN-11-1-1'

		final String queryString = //
		"SELECT {d:" + DistrictModel.PK + "} "//
				+ "FROM {" + DistrictModel._TYPECODE + " AS d} " + "WHERE {d:code} =?paramDistrictCode ";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("paramDistrictCode", districtCode);


		final SearchResult<DistrictModel> result = flexibleSearchService.<DistrictModel> search(query);
		if (result == null || result.getCount() == 0)
		{
			return null;
		}

		return result.getResult().get(0); // TODO: what if >1 items found?
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.chinaaccelerator.daos.DistrictDao#findDistrictsByCityCode(java.lang.String)
	 */
	@Override
	public List<DistrictModel> findDistrictsByCityCode(final String cityCode)
	{
		// SELECT {d:pk} FROM {District AS d JOIN City as c on {d:city} = {c.pk} AND {c:code} = 'CN-11-1' }
		final String queryString = //
		"SELECT {d:" + DistrictModel.PK
				+ "}" //
				+ "FROM {" + DistrictModel._TYPECODE + " AS d JOIN " + CityModel._TYPECODE + " AS c " + "ON {c:" + CityModel.PK
				+ "} = {d:" + DistrictModel.CITY + "}  " + " AND {c:" + CityModel.CODE + "} =?paramCityCode }"; //

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("paramCityCode", cityCode);

		return flexibleSearchService.<DistrictModel> search(query).getResult();
	}

}
