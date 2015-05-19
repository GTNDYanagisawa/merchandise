package de.hybris.platform.ychinaaccelerator.storefront.controllers.alipay;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.order.OrderService;

import javax.annotation.Resource;


//import de.hybris.platform.ychinaaccelerator.storefront.controllers.AbstractController;

public class AbstractAlipayController extends AbstractController
{

	@Resource(name = "acceleratorCheckoutFacade")
	private CheckoutFacade checkoutFacade;

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

	@Resource(name = "orderService")
	private OrderService orderService;

	protected CheckoutFacade getCheckoutFacade()
	{
		return checkoutFacade;
	}

	protected OrderFacade getOrderFacade()
	{
		return orderFacade;
	}

	/**
	 * @return the orderService
	 */
	public OrderService getOrderService()
	{
		return orderService;
	}

	/**
	 * @param orderService
	 *           the orderService to set
	 */
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}

}
