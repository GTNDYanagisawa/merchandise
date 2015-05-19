package de.hybris.platform.sap.sapmodel.daos;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.model.SAPPricingSalesAreaToCatalogModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.BaseStoreModel;
public class NetAttributeHandler implements DynamicAttributeHandler<Boolean, SAPPricingSalesAreaToCatalogModel>{

	protected static final Logger LOGGER = Logger
			.getLogger(NetAttributeHandler.class);

	@Autowired
	protected FlexibleSearchService flexibleSearchService;	//NOPMD
	
	@Override
	public Boolean get(SAPPricingSalesAreaToCatalogModel model)
	{
		
		final SAPConfigurationModel sapConfigurationModel = new SAPConfigurationModel();
		sapConfigurationModel.setSapcommon_distributionChannel(model.getDistributionChannel());
		sapConfigurationModel.setSapcommon_salesOrganization(model.getSalesOrganization());
		
		final SAPConfigurationModel foundSapConfiguration = flexibleSearchService
				.getModelByExample(sapConfigurationModel);
		
		if (foundSapConfiguration != null)
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("SAP Configuration found: "+ foundSapConfiguration.getCore_name());
			}
			
			Collection<BaseStoreModel> baseStores = foundSapConfiguration.getBaseStores();
			if (baseStores.size() == 0)
			{
				StringBuilder sb = new StringBuilder(500);
				sb.append("No Base Store Assigned to SAP Configuration: ");
				sb.append( foundSapConfiguration.getCore_name() );			
				LOGGER.error(sb);
			}
			else
			{
				for (BaseStoreModel baseStoreModel : baseStores)
				{
					//return the first base store
					return baseStoreModel.isNet();
				}
			}
		}
		else
		{
			StringBuilder sb = new StringBuilder(500);
			sb.append("No SAP Configuration assigned to the Sales Area: ");
			sb.append( model.getSalesOrganization() );
			sb.append(" with distribution channel: ");
			sb.append( model.getDistributionChannel() );
			LOGGER.error(sb);
			return Boolean.FALSE;
		}
		
		
		return Boolean.TRUE;
	}

	@Override
	public void set(SAPPricingSalesAreaToCatalogModel model, Boolean value)
	{
		 throw new UnsupportedOperationException();
		 
	}

}
