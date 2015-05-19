/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.populators;



import de.hybris.platform.chinaaccelerator.services.model.invoice.InvoiceModel;
import de.hybris.platform.commercefacades.order.data.InvoiceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


public class InvoicePopulator<SOURCE extends InvoiceModel, TARGET extends InvoiceData> implements
		Populator<InvoiceModel, InvoiceData>
{


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final InvoiceModel source, final InvoiceData target) throws ConversionException
	{
		if (source != null)
		{
			target.setInvoicedTitle(source.getTitle() == null ? null : source.getTitle().getCode());
			target.setInvoicedName(source.getInvoicedName());
			target.setInvoicedCategory(source.getCategory() == null ? null : source.getCategory().getCode());
		}
	}

}
