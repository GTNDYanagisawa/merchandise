/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.order.strategies;

import de.hybris.platform.chinaaccelerator.alipay.service.AlipayPaymentService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.strategies.SubmitOrderStrategy;



public class AlipaySubmitOrderStrategy implements SubmitOrderStrategy
{
	private AlipayPaymentService alipayPaymentService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.order.strategies.SubmitOrderStrategy#submitOrder(de.hybris.platform.core.model.order.OrderModel
	 * )
	 */
	@Override
	public void submitOrder(final OrderModel order)
	{
		getAlipayPaymentService().initiate(order);
	}

	/**
	 * @return the alipayPaymentService
	 */
	public AlipayPaymentService getAlipayPaymentService()
	{
		return alipayPaymentService;
	}

	/**
	 * @param alipayPaymentService
	 *           the alipayPaymentService to set
	 */
	public void setAlipayPaymentService(final AlipayPaymentService alipayPaymentService)
	{
		this.alipayPaymentService = alipayPaymentService;
	}

}
