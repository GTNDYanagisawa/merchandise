/**
 * 
 */
package de.hybris.platform.dynamicwebservices.service.impl;

import de.hybris.platform.dynamicwebservices.model.DynamicWebServiceModel;
import de.hybris.platform.dynamicwebservices.service.DynamicWebservicesService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Required;


/**
 * @author ag
 */
public class DefaultDynamicWebservicesService implements DynamicWebservicesService
{
	private static final Logger LOG = Logger.getLogger(DefaultDynamicWebservicesService.class.getName());

	private FlexibleSearchService flexibleSearchService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.dynamicwebservices.service.DynamicWebservicesService#findEnabledWebService(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public DynamicWebServiceModel findEnabledWebService(final String service, final String domain)
	{
		final DynamicWebServiceModel ret = queryWebService(service, domain, Boolean.TRUE);
		if (ret == null)
		{
			throw new ModelNotFoundException("Cannot find enabled service '" + service + "' in domain '" + domain + "'");
		}
		else
		{
			return ret;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.dynamicwebservices.service.DynamicWebservicesService#findWebService(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public DynamicWebServiceModel findWebService(final String service, final String domain)
	{
		final DynamicWebServiceModel ret = queryWebService(service, domain, null);
		if (ret == null)
		{
			throw new ModelNotFoundException("Cannot find service '" + service + "' in domain '" + domain + "'");
		}
		else
		{
			return ret;
		}
	}

	protected DynamicWebServiceModel queryWebService(final String service, final String domain, final Boolean enabled)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("service", service);
		params.put("domain", domain);


		final StringBuilder sb = new StringBuilder("SELECT {PK} " + //
				"FROM {" + DynamicWebServiceModel._TYPECODE + "} " + //
				"WHERE {" + DynamicWebServiceModel.SERVICE + "}=?service AND " + //
				"{" + DynamicWebServiceModel.DOMAIN + "}=?domain");

		if (enabled != null)
		{
			params.put("enabled", enabled);
			sb.append(" AND {").append(DynamicWebServiceModel.ENABLED).append("}=?enabled");
		}
		final SearchResult<DynamicWebServiceModel> sr = flexibleSearchService.search(sb.toString(), params);

		final int cnt = sr.getCount();
		if (cnt == 0)
		{
			return null;
		}
		else if (cnt == 1)
		{
			return sr.getResult().get(0);
		}
		else
		{
			LOG.warning("Found more than one DynamicWebService for service '" + service + "' and domain '" + domain + "'");
			return sr.getResult().get(0);
		}
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

}
