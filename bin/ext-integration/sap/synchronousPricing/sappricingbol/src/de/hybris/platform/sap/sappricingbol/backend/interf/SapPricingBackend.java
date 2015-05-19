/**
 * 
 */
package de.hybris.platform.sap.sappricingbol.backend.interf;


import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;
import de.hybris.platform.sap.sappricingbol.converter.ConversionService;

import java.util.List;


/**
 * 
 */
public interface SapPricingBackend extends de.hybris.platform.sap.core.bol.backend.BackendBusinessObject
{
	/**
	 * 
	 * @param order
	 * @param partnerFunction
	 * @param conversionService TODO
	 * @throws BackendException
	 * @throws CommunicationException
	 */
	public void readPricesForCart(AbstractOrderModel order, SapPricingPartnerFunction partnerFunction, ConversionService conversionService)
			throws BackendException, CommunicationException;

	/**
	 * @param productModels
	 * @param partnerFunction
	 * @param conversionService TODO
	 * @return List<PriceInformation> 
	 * @throws BackendException
	 */
	public List<PriceInformation> readPriceInformationForProducts(
			List<ProductModel> productModels, SapPricingPartnerFunction partnerFunction, ConversionService conversionService) throws BackendException;
	
}
