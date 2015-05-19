/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.webservices.resources.methods;

import de.hybris.platform.core.dto.order.CartDTO;
import de.hybris.platform.core.dto.order.CartEntryDTO;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.webservices.HttpPostResponseBuilder;
import de.hybris.platform.webservices.ServiceLocator;

import java.util.Calendar;


public class PostCartToCarts extends HttpPostResponseBuilder<CartModel, CartDTO>
{
	private final KeyGenerator keyGenerator;


	public PostCartToCarts()
	{
		super();
		keyGenerator = (KeyGenerator) ServicelayerUtils.getApplicationContext().getBean("cartEntryCodeGenerator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.HttpPostResponseBuilder#beforeProcessing(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void beforeProcessing(final CartDTO dto, final CartModel model)
	{
		if (dto.getEntries() != null)
		{
			for (final CartEntryDTO entry : dto.getEntries())
			{
				if (entry.getOrder() == null)
				{
					entry.setOrder(dto);
				}
				if (entry.getEntryNumber() == null)
				{
					entry.setEntryNumber(Integer.valueOf((String) keyGenerator.generate()));
				}
			}
		}
	}

	@Override
	public CartModel createResource(final CartDTO reqEntity) throws Exception
	{
		final ServiceLocator serviceLocator = getServiceLocator();
		final CartModel result = serviceLocator.getModelService().create(CartModel.class);
		result.setCurrency(serviceLocator.getI18nService().getCurrentCurrency());
		result.setDate(Calendar.getInstance(serviceLocator.getI18nService().getCurrentLocale()).getTime());
		result.setCalculated(Boolean.FALSE);
		result.setNet(Boolean.TRUE);

		final UserModel user = serviceLocator.getUserService().getCurrentUser();

		result.setOwner(user);
		result.setUser(user);
		result.setPaymentAddress(user.getDefaultPaymentAddress());
		result.setDeliveryAddress(user.getDefaultShipmentAddress());
		if (user.getPaymentInfos() != null && !user.getPaymentInfos().isEmpty())
		{
			result.setPaymentInfo(user.getPaymentInfos().iterator().next());
		}
		serviceLocator.getModelService().save(result);

		return result;
	}

}
