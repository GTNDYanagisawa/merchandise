package de.hybris.platform.sap.sappricing.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.sap.sappricing.services.SapPricingCartService;
import de.hybris.platform.sap.sappricing.services.SapPricingEnablementService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.TaxValue;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;


/**
 * SapCartCalculationService
 */
public class SapCartCalculationService extends DefaultCalculationService
{

	private OrderRequiresCalculationStrategy orderRequiresCalculationStrategy;
	private CommonI18NService commonI18NService;
	private UserService userService;
	private SapPricingCartService sapPricingCartService;
	private SapPricingEnablementService sapPricingEnablementService;

	@Override
	public void calculate(final AbstractOrderModel order) throws CalculationException
	{
		if (orderRequiresCalculationStrategy.requiresCalculation(order))
		{
			// update prices from sap backend.
			updateOrder(order);
			super.recalculate(order);
		}
	}

	@Override
	public void recalculate(final AbstractOrderModel order) throws CalculationException
	{
		// update prices from sap backend.
		updateOrder(order);

		super.recalculate(order);
	}

	@Override
	public void calculateTotals(final AbstractOrderModel order, final boolean recalculate) throws CalculationException
	{
		// update prices from sap backend.
		updateOrder(order);

		super.calculateTotals(order, recalculate, calculateSubtotal(order, recalculate));
	}

	/***
	 * update prices from sap backend
	 * 
	 * @return
	 */
	protected boolean updateOrder(final AbstractOrderModel order)
	{
		// read prices from backend if cart pricing is active in hmc 
		if (sapPricingEnablementService.isCartPricingEnabled())
		{
			// set order currency to session currency
			order.setCurrency(commonI18NService.getCurrentCurrency());
			// prevent backend call with empty items in cart, this happens when session cart is cleared. 
			if (!order.getEntries().isEmpty())
			{
				getSapPricingCartService().getPriceInformationForCart(order);
				return true;
			}
		}

		return false;
	}

	private void recalculateOrderEntryIfNeeded(final AbstractOrderEntryModel entry, final boolean forceRecalculation)
			throws CalculationException
	{
		if (forceRecalculation || orderRequiresCalculationStrategy.requiresCalculation(entry))
		{
			if (!sapPricingEnablementService.isCartPricingEnabled())
			{
				super.resetAllValues(entry);
			}
			super.calculateTotals(entry, true);
		}
	}

	@Override
	public void recalculate(final AbstractOrderEntryModel entry) throws CalculationException
	{
		this.recalculateOrderEntryIfNeeded(entry, false);
	}

	@Override
	public void calculateEntries(final AbstractOrderModel order, final boolean forceRecalculate) throws CalculationException
	{
		double subtotal = 0.0;
		for (final AbstractOrderEntryModel e : order.getEntries())
		{
			this.recalculateOrderEntryIfNeeded(e, forceRecalculate);
			subtotal += e.getTotalPrice().doubleValue();
		}
		order.setTotalPrice(Double.valueOf(subtotal));

	}




	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}


	@Override
	protected void resetAdditionalCosts(final AbstractOrderModel order, final Collection<TaxValue> relativeTaxValues)
	{
		/**
		 * additional costs include delivery costs + payment costs, for now we disable this since we are using erp
		 * calculation.
		 */
		if (!sapPricingEnablementService.isCartPricingEnabled())
		{
			super.resetAdditionalCosts(order, relativeTaxValues);
		}

	}

	@Override
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
		super.setCommonI18NService(commonI18NService);
	}


	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public SapPricingCartService getSapPricingCartService()
	{
		return sapPricingCartService;
	}

	@Required
	public void setSapPricingCartService(final SapPricingCartService sapPricingCartService)
	{
		this.sapPricingCartService = sapPricingCartService;
	}

	public OrderRequiresCalculationStrategy getOrderRequiresCalculationStrategy()
	{
		return orderRequiresCalculationStrategy;
	}

	@Override
	@Required
	public void setOrderRequiresCalculationStrategy(final OrderRequiresCalculationStrategy orderRequiresCalculationStrategy)
	{
		super.setOrderRequiresCalculationStrategy(orderRequiresCalculationStrategy);
		this.orderRequiresCalculationStrategy = orderRequiresCalculationStrategy;
	}


	public SapPricingEnablementService getSapPricingEnablementService()
	{
		return sapPricingEnablementService;
	}

	@Required
	public void setSapPricingEnablementService(final SapPricingEnablementService sapPricingEnablementService)
	{
		this.sapPricingEnablementService = sapPricingEnablementService;
	}


}
