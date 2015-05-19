/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.populators;

import de.hybris.platform.chinaaccelerator.facades.data.PaymentModeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


public class PaymentModePopulator<SOURCE extends PaymentModeModel, TARGET extends PaymentModeData> implements
		Populator<PaymentModeModel, PaymentModeData>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final PaymentModeModel source, final PaymentModeData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setName(source.getName());
	}


}
