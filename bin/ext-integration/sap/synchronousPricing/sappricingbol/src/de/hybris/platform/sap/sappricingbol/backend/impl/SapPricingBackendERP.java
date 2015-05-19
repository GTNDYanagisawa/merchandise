package de.hybris.platform.sap.sappricingbol.backend.impl;


import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory;
import de.hybris.platform.sap.core.jco.connection.JCoStateful;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.sappricingbol.backend.interf.SapPricingBackend;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;
import de.hybris.platform.sap.sappricingbol.constants.SappricingbolConstants;
import de.hybris.platform.sap.sappricingbol.converter.ConversionService;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;


/**
 * 
 */
@BackendType("ERP")
public class SapPricingBackendERP extends BackendBusinessObjectBaseJCo implements SapPricingBackend
{
	static final private Log4JWrapper sapLogger = Log4JWrapper.getInstance(SapPricingBackendERP.class.getName());

	@Resource(name = "sapCoreJCoManagedConnectionFactory")
	protected JCoManagedConnectionFactory managedConnectionFactory; //NOPMD



	private SapPricingBaseMapper baseMapper;
	private SapPricingHeaderMapper headerMapper;
	private SapPricingItemMapper itemMapper;
	private SapPricingCachedBackendERP cacheAccess;

	@Override
	public List<PriceInformation> readPriceInformationForProducts(List<ProductModel> productModels,
			SapPricingPartnerFunction partnerFunction, ConversionService conversionService) throws BackendException
	{

		sapLogger.entering("readPriceInformationForProducts(...)");

		List<PriceInformation> priceInformationList = cacheAccess.readCachedPriceInformationForProducts(productModels,
				partnerFunction);
		JCoConnection connection = null;

		// check if the price is cached
		if (priceInformationList != null)
		{
			return priceInformationList;
		}
		try
		{
			// get Jco connection
			connection = managedConnectionFactory.getManagedConnection(getDefaultConnectionName(), this.getClass().getName());

			// get function module
			final JCoFunction function = connection.getFunction(SappricingbolConstants.FM_PIQ_CALCULATE);

			// Fill import parameters
			final JCoParameterList importParameters = function.getImportParameterList();

			// fills gloabal import parameters
			baseMapper.fillImportParameters(importParameters);

			// fills header import parameters
			headerMapper.fillImportParameters(importParameters, partnerFunction);

			// fill item import parameters
			itemMapper.fillImportParameters(importParameters, productModels, conversionService);

			// execute 
			connection.execute(function);

			// read parameter list
			final JCoParameterList exportParameterList = function.getExportParameterList();

			// read backend messages
			final JCoTable etMessage = function.getExportParameterList().getTable("ET_MESSAGE");
			logMesages(etMessage);

			// read the price 
			final JCoTable resultTable = exportParameterList.getTable("ET_RESULT");

			priceInformationList = itemMapper.readPrices(resultTable);

			// cache the price
			cacheAccess.cachePriceInformationForProducts(productModels, partnerFunction, priceInformationList);
		}
		finally
		{
			if (connection != null)
			{
				((JCoStateful) connection).destroy();
			}
		}
		sapLogger.exiting();

		return priceInformationList;
	}

	@Override
	public void readPricesForCart(final AbstractOrderModel order, final SapPricingPartnerFunction partnerFunction,
			ConversionService conversionService) throws BackendException, CommunicationException
	{
		sapLogger.entering("readPriceInformationForProduct(...)");

		// get Jco connection
		JCoConnection connection = null;

		try
		{
			connection = managedConnectionFactory.getManagedConnection(getDefaultConnectionName(), this.getClass().getName());

			// get function module
			final JCoFunction function = connection.getFunction(SappricingbolConstants.FM_PIQ_CALCULATE);

			// Fill import parameters
			final JCoParameterList importParameters = function.getImportParameterList();

			// fills gloabal import parameters
			baseMapper.fillImportParameters(importParameters);

			// fills header import parameters
			headerMapper.fillImportParameters(importParameters, partnerFunction, order);

			// fill item import parameters
			itemMapper.fillImportParameters(order, importParameters, conversionService);

			// excute 
			connection.execute(function);

			// read parameter list
			final JCoParameterList exportParameterList = function.getExportParameterList();

			// read backend messages
			final JCoTable etMessage = function.getExportParameterList().getTable("ET_MESSAGE");
			logMesages(etMessage);

			// read price 
			final JCoTable result = exportParameterList.getTable("ET_RESULT");
			itemMapper.readPrices(order, result, conversionService);
		}
		finally
		{
			if (connection != null)
			{
				((JCoStateful) connection).destroy();
			}
		}
		sapLogger.exiting();
	}

	protected void logMesages(final JCoTable etMessage)
	{
		if (!etMessage.isEmpty())
		{
			for (int i = 0; i < etMessage.getNumRows(); i++)
			{
				etMessage.setRow(i);
				if (etMessage.getString("TYPE").contentEquals("E"))
				{
					sapLogger.log(LogSeverity.ERROR, LogCategories.APPLICATIONS, etMessage.getString("MESSAGE"));
				}
			}
		}
	}

	/**
	 * @return SapPricingBaseMapper
	 */
	public SapPricingBaseMapper getBaseMapper()
	{
		return baseMapper;
	}

	/**
	 * @param baseMapper
	 */
	@Required
	public void setBaseMapper(final SapPricingBaseMapper baseMapper)
	{
		this.baseMapper = baseMapper;
	}

	/**
	 * @return SapPricingHeaderMapper
	 */
	public SapPricingHeaderMapper getHeaderMapper()
	{
		return headerMapper;
	}

	/**
	 * @param headerMapper
	 */
	@Required
	public void setHeaderMapper(final SapPricingHeaderMapper headerMapper)
	{
		this.headerMapper = headerMapper;
	}

	/**
	 * @return SapPricingItemMapper
	 */
	public SapPricingItemMapper getItemMapper()
	{
		return itemMapper;
	}

	/**
	 * @param itemMapper
	 */
	@Required
	public void setItemMapper(final SapPricingItemMapper itemMapper)
	{
		this.itemMapper = itemMapper;
	}

	/**
	 * @return CacheAccess
	 */
	public SapPricingCachedBackendERP getCacheAccess()
	{
		return cacheAccess;
	}

	/**
	 * @param cacheAccess
	 */
	@Required
	public void setCacheAccess(final SapPricingCachedBackendERP cacheAccess)
	{
		this.cacheAccess = cacheAccess;
	}

	public void setManagedConnectionFactory(JCoManagedConnectionFactory managedConnectionFactory)
	{
		this.managedConnectionFactory = managedConnectionFactory;
	}


}
