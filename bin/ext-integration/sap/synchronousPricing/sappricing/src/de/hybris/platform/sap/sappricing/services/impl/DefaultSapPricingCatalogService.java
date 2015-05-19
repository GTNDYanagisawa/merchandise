package de.hybris.platform.sap.sappricing.services.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commerceservices.price.impl.NetPriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.sap.sappricing.services.SapPartnerService;
import de.hybris.platform.sap.sappricing.services.SapPricingBolFactory;
import de.hybris.platform.sap.sappricing.services.SapPricingCatalogService;
import de.hybris.platform.sap.sappricing.services.SapPricingEnablementService;
import de.hybris.platform.sap.sappricingbol.converter.ConversionService;


/**
 * @author I813945
 *
 */
public class DefaultSapPricingCatalogService extends NetPriceService implements SapPricingCatalogService
{

	private SapPartnerService sapPartnerService;
	private SapPricingBolFactory bolFactory;
	private SapPricingEnablementService sapPricingEnablementService;
	
	private ConversionService conversionService;

	public ConversionService getConversionService()
	{
		return conversionService;
	}

	@Required
	public void setConversionService(ConversionService conversionService)
	{
		this.conversionService = conversionService;
	}

	public SapPartnerService getSapPartnerService() {
		return sapPartnerService;
	}

	@Required
	public void setSapPartnerService(SapPartnerService sapPartnerService) {
		this.sapPartnerService = sapPartnerService;
	}
	
	public SapPricingBolFactory getBolFactory()
	{
		return bolFactory;
	}

	@Required
	public void setBolFactory(SapPricingBolFactory bolFactory)
	{
		this.bolFactory = bolFactory;
	}

	@Override
	public List<PriceInformation> getPriceInformationsForProduct(ProductModel model)
	{
		if (sapPricingEnablementService.isCatalogPricingEnabled()) {
			return getBolFactory().getSapPricing().getPriceInformationForProducts(Arrays.<ProductModel>asList(model), sapPartnerService.getPartnerFunction(), this.getConversionService());
		}
		
		return super.getPriceInformationsForProduct(model);
	}
	
	@Override
	public List<PriceInformation> getPriceInformationForProducts(List<ProductModel> models)
	{
		if (sapPricingEnablementService.isCatalogPricingEnabled()) {
			return getBolFactory().getSapPricing().getPriceInformationForProducts(models, sapPartnerService.getPartnerFunction(), this.getConversionService());
		}
		
		return null;
	}
	


	public SapPricingEnablementService getSapPricingEnablementService() {
		return sapPricingEnablementService;
	}

	@Required
	public void setSapPricingEnablementService(
			SapPricingEnablementService sapPricingEnablementService) {
		this.sapPricingEnablementService = sapPricingEnablementService;
	}
	
}
