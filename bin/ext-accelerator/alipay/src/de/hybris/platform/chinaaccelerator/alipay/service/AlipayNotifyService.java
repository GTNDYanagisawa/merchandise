/**
 * 
 */
package de.hybris.platform.chinaaccelerator.alipay.service;

import de.hybris.platform.core.model.order.OrderModel;


public interface AlipayNotifyService
{
	public void executeAction(final OrderModel order);
}
