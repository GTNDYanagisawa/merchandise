package de.hybris.platform.sap.sappricing.services;

import de.hybris.platform.core.model.order.AbstractOrderModel;



public interface SapPricingCartService
{

	void getPriceInformationForCart(AbstractOrderModel order);

}
