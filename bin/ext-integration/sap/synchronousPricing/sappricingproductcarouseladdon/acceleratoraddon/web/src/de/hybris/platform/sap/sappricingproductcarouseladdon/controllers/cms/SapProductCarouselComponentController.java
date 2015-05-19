package de.hybris.platform.sap.sappricingproductcarouseladdon.controllers.cms;

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.sappricing.services.SapPricingCatalogService;
import de.hybris.platform.sap.sappricingproductcarouseladdon.controllers.SappricingproductcarouseladdonControllerConstants;
import de.hybris.platform.sap.sappricingproductcarouseladdon.model.SapProductCarouselComponentModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * SAP Product Carousel Controller
 */
@Controller(SappricingproductcarouseladdonControllerConstants.CONTROLLER)
@RequestMapping(value = SappricingproductcarouseladdonControllerConstants.VIEW)
public class SapProductCarouselComponentController extends AbstractCMSAddOnComponentController<SapProductCarouselComponentModel>
{

	protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC);

	@Resource(name = "b2bProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;

	@Resource(name = "priceService")
	private SapPricingCatalogService priceService;

	@Resource(name = "priceDataFactory")
	private PriceDataFactory priceDataFactory;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final SapProductCarouselComponentModel component)
	{
		final List<ProductData> products = new ArrayList<ProductData>();

		products.addAll(collectLinkedProducts(component));
		products.addAll(collectSearchProducts(component));

		model.addAttribute("title", component.getTitle());
		model.addAttribute("productData", products);
	}

	protected List<ProductData> collectLinkedProducts(final SapProductCarouselComponentModel component)
	{
		final List<ProductModel> productModels = new ArrayList<ProductModel>();

		for (final ProductModel productModel : component.getProducts())
		{
			productModels.add(productModel);
		}

		for (final CategoryModel categoryModel : component.getCategories())
		{
			for (final ProductModel productModel : categoryModel.getProducts())
			{
				productModels.add(productModel);
			}
		}

		final List<PriceInformation> priceInformationList = priceService.getPriceInformationForProducts(productModels);

		final Iterator<ProductModel> productIterator = productModels.iterator();
		final Iterator<PriceInformation> priceIterator = priceInformationList.iterator();

		final List<ProductData> products = new ArrayList<ProductData>();

		while (productIterator.hasNext() && priceIterator.hasNext())
		{

			final ProductModel productModel = productIterator.next();
			final PriceInformation priceInformation = priceIterator.next();

			final ProductData productData = productFacade.getProductForOptions(productModel, PRODUCT_OPTIONS);
			populateProductWithSapPrice(productModel, productData, priceInformation);

			products.add(productData);

		}

		return products;
	}

	protected void populateProductWithSapPrice(final ProductModel productModel, final ProductData productData,
			final PriceInformation priceInformation) throws ConversionException
	{
		final PriceDataType priceType;

		if (CollectionUtils.isEmpty(productModel.getVariants()))
		{
			priceType = PriceDataType.BUY;
		}
		else
		{
			priceType = PriceDataType.FROM;
		}

		if (priceInformation != null)
		{
			final PriceData priceData = priceDataFactory
					.create(priceType, BigDecimal.valueOf(priceInformation.getPriceValue().getValue()), priceInformation
							.getPriceValue().getCurrencyIso());

			productData.setPrice(priceData);
		}
		else
		{
			productData.setPurchasable(Boolean.FALSE);
		}

	}

	protected List<ProductData> collectSearchProducts(final SapProductCarouselComponentModel component)
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(component.getSearchQuery());
		final String categoryCode = component.getCategoryCode();

		if (searchQueryData.getValue() != null && categoryCode != null)
		{
			final SearchStateData searchState = new SearchStateData();
			searchState.setQuery(searchQueryData);

			final PageableData pageableData = new PageableData();
			pageableData.setPageSize(100);

			return productSearchFacade.categorySearch(categoryCode, searchState, pageableData).getResults();
		}

		return Collections.emptyList();
	}

	@Override
	protected String getView(final SapProductCarouselComponentModel component)
	{
		return StringUtils.lowerCase("cms/" + ProductCarouselComponentModel._TYPECODE);
	}

}
