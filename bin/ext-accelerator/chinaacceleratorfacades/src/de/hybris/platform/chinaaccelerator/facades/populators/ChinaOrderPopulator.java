/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.populators;



import de.hybris.platform.chinaaccelerator.facades.data.PaymentModeData;
import de.hybris.platform.chinaaccelerator.facades.order.data.ExOrderData;
import de.hybris.platform.chinaaccelerator.facades.order.data.OrderModificationRecordData;
import de.hybris.platform.chinaaccelerator.services.model.invoice.InvoiceModel;
import de.hybris.platform.commercefacades.order.converters.populator.OrderPopulator;
import de.hybris.platform.commercefacades.order.data.InvoiceData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.ordercancel.model.OrderStatusUpdateRecordModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;


public class ChinaOrderPopulator extends OrderPopulator
{

	private Converter<InvoiceModel, InvoiceData> invoiceConverter;
	private Converter<PaymentModeModel, PaymentModeData> paymentModeConverter;

	/**
	 * @return the invoiceConverter
	 */
	public Converter<InvoiceModel, InvoiceData> getInvoiceConverter()
	{
		return invoiceConverter;
	}

	/**
	 * @param invoiceConverter
	 *           the invoiceConverter to set
	 */
	public void setInvoiceConverter(final Converter<InvoiceModel, InvoiceData> invoiceConverter)
	{
		this.invoiceConverter = invoiceConverter;
	}

	/**
	 * @return the paymentModeConverter
	 */
	public Converter<PaymentModeModel, PaymentModeData> getPaymentModeConverter()
	{
		return paymentModeConverter;
	}

	/**
	 * @param paymentModeConverter
	 *           the paymentModeConverter to set
	 */
	public void setPaymentModeConverter(final Converter<PaymentModeModel, PaymentModeData> paymentModeConverter)
	{
		this.paymentModeConverter = paymentModeConverter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commercefacades.order.converters.populator.OrderPopulator#populate(de.hybris.platform.core.
	 * model.order.OrderModel, de.hybris.platform.commercefacades.order.data.OrderData)
	 */
	@Override
	public void populate(final OrderModel source, final OrderData target)
	{
		super.populate(source, target);
		if (target instanceof ExOrderData)
		{
			addStatusUpdateRecords(source, (ExOrderData) target);
			addDeliveryTimeslot(source, (ExOrderData) target);
			addPaymentMode(source, (ExOrderData) target);
			addInvoice(source, (ExOrderData) target);

		}
	}

	/**
	 * @param source
	 * @param target
	 */
	private void addInvoice(final OrderModel source, final ExOrderData target)
	{
		if (source.getInvoice() != null)
		{
			target.setInvoice(invoiceConverter.convert(source.getInvoice()));
		}
	}

	/**
	 * @param source
	 * @param target
	 */
	private void addPaymentMode(final OrderModel source, final ExOrderData target)
	{
		if (source.getPaymentMode() != null)
		{
			target.setPaymentMode(paymentModeConverter.convert(source.getPaymentMode()));
		}

	}

	/**
	 * @param source
	 * @param target
	 */
	private void addDeliveryTimeslot(final OrderModel source, final ExOrderData target)
	{
		target.setDeliveryTimeslot(source.getDeliveryTimeslot());
	}

	/**
	 * @param source
	 * @param target
	 */
	protected void addStatusUpdateRecords(final OrderModel source, final ExOrderData target)
	{
		final List<OrderModificationRecordData> records = new ArrayList<OrderModificationRecordData>();
		for (final OrderModificationRecordModel model : source.getModificationRecords())
		{
			if (model instanceof OrderStatusUpdateRecordModel)
			{
				final OrderModificationRecordData data = new OrderModificationRecordData();
				data.setStatus(((OrderStatusUpdateRecordModel) model).getStatus());
				data.setCreated(model.getCreationtime());
				records.add(data);
			}
		}
		target.setStatusUpdatedRecords(records);

	}

}
