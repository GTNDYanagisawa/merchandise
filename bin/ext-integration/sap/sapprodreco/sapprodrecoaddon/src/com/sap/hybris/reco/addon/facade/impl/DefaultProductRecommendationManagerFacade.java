/**
 * 
 */
package com.sap.hybris.reco.addon.facade.impl;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commerceservices.product.data.ReferenceData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sap.hybris.reco.addon.facade.ProductRecommendationManagerFacade;
import com.sap.hybris.reco.addon.bo.UserIdProvider;
import com.sap.hybris.reco.addon.dao.ProductRecommendation;
import com.sap.hybris.reco.addon.bo.ProductRecommendationManager;
import com.sap.hybris.reco.addon.bo.RecommendationContextProvider;



/**
 * @author Administrator
 * 
 */
public class DefaultProductRecommendationManagerFacade<REF_TARGET> implements ProductRecommendationManagerFacade
{

	private Converter<ReferenceData<ProductReferenceTypeEnum, REF_TARGET>, ProductReferenceData> referenceDataProductReferenceConverter;
	private ConfigurablePopulator<REF_TARGET, ProductData, ProductOption> referenceProductConfiguredPopulator;

	private GenericFactory genericFactory;
	private UserService userService;
	private CartService cartService;
	private UserIdProvider userIDProvider;

	protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE);


	@Override
	public List<ProductReferenceData> getProductRecommendation(final RecommendationContextProvider context)
	{
		final List<ReferenceData<ProductReferenceTypeEnum, ProductModel>> references = createReferenzList(context);

		final List<ProductReferenceData> result = new ArrayList<ProductReferenceData>();

		for (final ReferenceData<ProductReferenceTypeEnum, ProductModel> reference : references)
		{
			final ProductReferenceData productReferenceData = getReferenceDataProductReferenceConverter().convert(
					(ReferenceData<ProductReferenceTypeEnum, REF_TARGET>) reference);
			getReferenceProductConfiguredPopulator().populate((REF_TARGET) reference.getTarget(), productReferenceData.getTarget(),
					PRODUCT_OPTIONS);
			result.add(productReferenceData);
		}

		return result;
	}

	/**
	 * @param productId
	 * @return
	 */
	private List<ReferenceData<ProductReferenceTypeEnum, ProductModel>> createReferenzList(
			final RecommendationContextProvider context)
	{

		final List<ProductRecommendation> productRecommendations = getRecommendationManager().getProductRecommendation(context);

		final List<ReferenceData<ProductReferenceTypeEnum, ProductModel>> references = convertToProductReference(productRecommendations);

		return references;
	}

	/**
	 * @param productRecommendations
	 * @return
	 */
	private List<ReferenceData<ProductReferenceTypeEnum, ProductModel>> convertToProductReference(
			final List<ProductRecommendation> productRecommendations)
	{
		final List<ReferenceData<ProductReferenceTypeEnum, ProductModel>> references = new ArrayList<ReferenceData<ProductReferenceTypeEnum, ProductModel>>();

		for (final ProductRecommendation productRecommendation : productRecommendations)
		{
			final ReferenceData<ProductReferenceTypeEnum, ProductModel> referenceData = new ReferenceData<ProductReferenceTypeEnum, ProductModel>();
			referenceData.setQuantity(new Integer(1));
			referenceData.setReferenceType(ProductReferenceTypeEnum.OTHERS);
			final ProductModel product = productRecommendation.getProduct();
			referenceData.setTarget(product);
			references.add(referenceData);
		}

		return references;
	}

	@Override
	public RecommendationContextProvider createRecommendationContextProvider()
	{
		final RecommendationContextProvider contextProvider = genericFactory.getBean("sapRecommendationContextProvider");
		final UserModel currentUser = userService.getCurrentUser();
		contextProvider.setUserId(userIDProvider.getUserId(currentUser));
		final CartModel cartModel = cartService.getSessionCart();
		for (final AbstractOrderEntryModel cartEntry : cartModel.getEntries())
		{
			contextProvider.addCartItem(cartEntry.getProduct().getCode());
		}

		return contextProvider;
	}


	protected ProductRecommendationManager getRecommendationManager()
	{
		return genericFactory.getBean("sapProductRecommendationManager");
	}


	/**
	 * @return the referenceDataProductReferenceConverter
	 */
	public Converter<ReferenceData<ProductReferenceTypeEnum, REF_TARGET>, ProductReferenceData> getReferenceDataProductReferenceConverter()
	{
		return referenceDataProductReferenceConverter;
	}


	/**
	 * @param referenceDataProductReferenceConverter
	 *           the referenceDataProductReferenceConverter to set
	 */
	public void setReferenceDataProductReferenceConverter(
			final Converter<ReferenceData<ProductReferenceTypeEnum, REF_TARGET>, ProductReferenceData> referenceDataProductReferenceConverter)
	{
		this.referenceDataProductReferenceConverter = referenceDataProductReferenceConverter;
	}


	/**
	 * @return the referenceProductConfiguredPopulator
	 */
	public ConfigurablePopulator<REF_TARGET, ProductData, ProductOption> getReferenceProductConfiguredPopulator()
	{
		return referenceProductConfiguredPopulator;
	}


	/**
	 * @param referenceProductConfiguredPopulator
	 *           the referenceProductConfiguredPopulator to set
	 */
	public void setReferenceProductConfiguredPopulator(
			final ConfigurablePopulator<REF_TARGET, ProductData, ProductOption> referenceProductConfiguredPopulator)
	{
		this.referenceProductConfiguredPopulator = referenceProductConfiguredPopulator;
	}


	/**
	 * @return the genericFactory
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}


	/**
	 * @param genericFactory
	 *           the genericFactory to set
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the userIDProvider
	 */
	public UserIdProvider getUserIDProvider()
	{
		return userIDProvider;
	}

	/**
	 * @param userIDProvider
	 *           the userIDProvider to set
	 */
	public void setUserIDProvider(final UserIdProvider userIDProvider)
	{
		this.userIDProvider = userIDProvider;
	}


}
