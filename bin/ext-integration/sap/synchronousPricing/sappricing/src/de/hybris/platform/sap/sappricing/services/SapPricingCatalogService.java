package de.hybris.platform.sap.sappricing.services;

import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;

public interface SapPricingCatalogService extends PriceService
{
	
	public List<PriceInformation> getPriceInformationForProducts(List<ProductModel> models);

}
