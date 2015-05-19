/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.ycommercewebservices.v2.controller;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.catalog.CatalogFacade;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.data.ProductReferencesData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.SuggestionData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.AutocompleteSuggestionData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.storefinder.StoreFinderStockFacade;
import de.hybris.platform.commercefacades.storefinder.data.StoreFinderStockSearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commercewebservicescommons.cache.CacheControl;
import de.hybris.platform.commercewebservicescommons.cache.CacheControlDirective;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductReferenceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ReviewListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ReviewWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.StockWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.SuggestionListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.SuggestionWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.queues.ProductExpressUpdateElementListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.queues.ProductExpressUpdateElementWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.StoreFinderStockSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.StockSystemException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.ycommercewebservices.constants.YcommercewebservicesConstants;
import de.hybris.platform.ycommercewebservices.formatters.WsDateFormatter;
import de.hybris.platform.ycommercewebservices.queues.data.ProductExpressUpdateElementData;
import de.hybris.platform.ycommercewebservices.queues.impl.ProductExpressUpdateQueue;
import de.hybris.platform.ycommercewebservices.stock.CommerceStockFacade;
import de.hybris.platform.ycommercewebservices.v2.helper.ProductsHelper;
import de.hybris.platform.ycommercewebservices.validator.PointOfServiceValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;


/**
 * Web Services Controller to expose the functionality of the
 * {@link de.hybris.platform.commercefacades.product.ProductFacade} and SearchFacade.
 * 
 * @pathparam productCode Product identifier
 * @pathparam storeName Store identifier
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/products")
public class ProductsController extends BaseController
{
	private static final String BASIC_OPTION = "BASIC";
	private static final Set<ProductOption> OPTIONS;
	private static final String MAX_INTEGER = "2147483647";
	private static final int CATALOG_ID_POS = 0;
	private static final int CATALOG_VERSION_POS = 1;
	private static final Logger LOG = Logger.getLogger(ProductsController.class);
	private static String PRODUCT_OPTIONS = "";
	@Resource
	StoreFinderStockFacade storeFinderStockFacade;
	@Resource(name = "cwsProductFacade")
	private ProductFacade productFacade;
	@Resource(name = "wsDateFormatter")
	private WsDateFormatter wsDateFormatter;
	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;
	@Resource(name = "solrSearchStateConverter")
	private Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;
	@Resource(name = "httpRequestReviewDataPopulator")
	private Populator<HttpServletRequest, ReviewData> httpRequestReviewDataPopulator;
	@Resource(name = "reviewValidator")
	private Validator reviewValidator;
	@Resource(name = "reviewDTOValidator")
	private Validator reviewDTOValidator;
	@Resource(name = "commerceStockFacade")
	private CommerceStockFacade commerceStockFacade;
	@Resource(name = "pointOfServiceValidator")
	private PointOfServiceValidator pointOfServiceValidator;
	@Resource(name = "productExpressUpdateQueue")
	private ProductExpressUpdateQueue productExpressUpdateQueue;
	@Resource(name = "catalogFacade")
	private CatalogFacade catalogFacade;
	@Resource(name = "productsHelper")
	private ProductsHelper productsHelper;

	static
	{
		for (final ProductOption option : ProductOption.values())
		{
			PRODUCT_OPTIONS = PRODUCT_OPTIONS + option.toString() + " ";
		}
		PRODUCT_OPTIONS = PRODUCT_OPTIONS.trim().replace(" ", YcommercewebservicesConstants.OPTIONS_SEPARATOR);
		OPTIONS = extractOptions(PRODUCT_OPTIONS);
	}

	private static Set<ProductOption> extractOptions(final String options)
	{
		final String optionsStrings[] = options.split(YcommercewebservicesConstants.OPTIONS_SEPARATOR);

		final Set<ProductOption> opts = new HashSet<ProductOption>();
		for (final String option : optionsStrings)
		{
			opts.add(ProductOption.valueOf(option));
		}
		return opts;
	}

	/**
	 * Returns a list of products and additional data like available facets, available sort options, and pagination
	 * options.<br>
	 * It can include spelling suggestions.To make spelling suggestions work you need to:
	 * <ul>
	 * <li>Make sure enableSpellCheck on the SearchQuery is set to true. By default it should be already set to true.</li>
	 * <li>Have indexed properties configured to be used for spell checking.</li>
	 * </ul>
	 * 
	 * @queryparam query Serialized query, free text search, facets.<br>
	 *             The format of a serialized query:
	 *             <b>freeTextSearch:sort:facetKey1:facetValue1:facetKey2:facetValue2</b>
	 * @queryparam currentPage The current result page requested.
	 * @queryparam pageSize The number of results returned per page.
	 * @queryparam sort Sorting method applied to the display search results.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return List of products
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 300)
	@ResponseBody
	public ProductSearchPageWsDTO searchProducts(@RequestParam(required = false) final String query,
			@RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@RequestParam(required = false) final String sort, @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
			final HttpServletResponse response)
	{
		final ProductSearchPageWsDTO result = productsHelper.searchProducts(query, currentPage, pageSize, sort, fields);
		// X-Total-Count header
		setTotalCountHeader(response, result.getPagination().getTotalResults().longValue());
		return result;
	}

	/**
	 * Returns {@value BaseController#HEADER_TOTAL_COUNT} header with total number of products satisfying a query. It
	 * doesn't return HTTP body.
	 * 
	 * @queryparam query Serialized query, free text search, facets.<br>
	 *             The format of a serialized query:
	 *             <b>freeTextSearch:sort:facetKey1:facetValue1:facetKey2:facetValue2</b>
	 */
	@RequestMapping(value = "/search", method = RequestMethod.HEAD)
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 300)
	public void countSearchProducts(@RequestParam(required = false) final String query, final HttpServletResponse response)
	{
		final ProductSearchPageData<SearchStateData, ProductData> result = productsHelper.searchProducts(query, 0, 1, null);
		setTotalCountHeader(response, result.getPagination().getTotalNumberOfResults());
	}

	/**
	 * Returns details of a single product specified by a product code.
	 * 
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Product details
	 */
	@RequestMapping(value = "/{productCode}", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
	@Cacheable(value = "productCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,#productCode,#fields)")
	@ResponseBody
	public ProductWsDTO getProductByCode(@PathVariable final String productCode,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getProductByCode: code=" + productCode + " | options=" + PRODUCT_OPTIONS);
		}

		final ProductData product = productFacade.getProductForCodeAndOptions(productCode, OPTIONS);
		final ProductWsDTO dto = dataMapper.map(product, ProductWsDTO.class, fields);
		return dto;
	}

	/**
	 * Method returns product's stock level for a particular store (POS).
	 * 
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Stock level information for product in store
	 * @throws WebserviceValidationException
	 *            If store doesn't exist
	 * @throws StockSystemException
	 *            When stock system is not enabled on this site
	 */
	@RequestMapping(value = "/{productCode}/stock/{storeName}", method = RequestMethod.GET)
	@ResponseBody
	public StockWsDTO getStockData(@PathVariable final String baseSiteId, @PathVariable final String productCode,
			@PathVariable final String storeName, @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException, StockSystemException
	{
		validate(storeName, "storeName", pointOfServiceValidator);
		if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED, baseSiteId);
		}
		final StockData stockData = commerceStockFacade.getStockDataForProductAndPointOfService(productCode, storeName);
		final StockWsDTO dto = dataMapper.map(stockData, StockWsDTO.class, fields);
		return dto;
	}

	/**
	 * Method returns product's stock levels sorted by distance from specific location passed by free-text parameter or
	 * longitude and latitude parameters.<br>
	 * There are two set of parameters available :
	 * <ul>
	 * <li>location (Required), currentPage (Optional), pageSize(Optional) or</li>>
	 * <li>longitude (Required), latitude (Required), currentPage (Optional), pageSize(Optional).</li>
	 * </ul>
	 * 
	 * @queryparam location Free-text location
	 * @queryparam latitude Longitude location parameter.
	 * @queryparam longitude Latitude location parameter.
	 * @queryparam currentPage The current result page requested.
	 * @queryparam pageSize The number of results returned per page.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return product's stock levels
	 */
	@RequestMapping(value = "/{productCode}/stock", method = RequestMethod.GET)
	@ResponseBody
	public StoreFinderStockSearchPageWsDTO searchProductStockByLocation(@PathVariable final String productCode,
			@RequestParam(required = false) final String location, @RequestParam(required = false) final Double latitude,
			@RequestParam(required = false) final Double longitude,
			@RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletResponse response)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getProductStockByLocation: code=" + productCode + " | location=" + location + " | latitude=" + latitude
					+ " | longitude=" + longitude);
		}

		final StoreFinderStockSearchPageData result = doSearchProductStockByLocation(productCode, location, latitude, longitude,
				currentPage, pageSize);

		// X-Total-Count header
		setTotalCountHeader(response, result.getPagination().getTotalNumberOfResults());

		return dataMapper.map(result, StoreFinderStockSearchPageWsDTO.class, fields);
	}

	/**
	 * Returns {@value BaseController#HEADER_TOTAL_COUNT} header with total number of product's stock levels. It doesn't
	 * return HTTP body.<br/>
	 * There are two set of parameters available :
	 * <ul>
	 * <li>location (Required) or</li>
	 * <li>longitude (Required), latitude (Required).</li>
	 * </ul>
	 * 
	 * @queryparam location Free-text location
	 * @queryparam latitude Longitude location parameter.
	 * @queryparam longitude Latitude location parameter.
	 */
	@RequestMapping(value = "/{productCode}/stock", method = RequestMethod.HEAD)
	public void countSearchProductStockByLocation(@PathVariable final String productCode,
			@RequestParam(required = false) final String location, @RequestParam(required = false) final Double latitude,
			@RequestParam(required = false) final Double longitude, final HttpServletResponse response)
	{
		final StoreFinderStockSearchPageData result = doSearchProductStockByLocation(productCode, location, latitude, longitude, 0,
				1);

		setTotalCountHeader(response, result.getPagination().getTotalNumberOfResults());
	}

	protected StoreFinderStockSearchPageData doSearchProductStockByLocation(final String productCode, final String location,
			final Double latitude, final Double longitude, final int currentPage, final int pageSize)
	{
		final Set<ProductOption> opts = extractOptions(BASIC_OPTION);
		StoreFinderStockSearchPageData result;
		if (latitude != null && longitude != null)
		{
			result = storeFinderStockFacade.productSearch(createGeoPoint(latitude, longitude),
					productFacade.getProductForCodeAndOptions(productCode, opts), createPageableData(currentPage, pageSize, null));
		}
		else if (location != null)
		{
			result = storeFinderStockFacade.productSearch(location, productFacade.getProductForCodeAndOptions(productCode, opts),
					createPageableData(currentPage, pageSize, null));
		}
		else
		{
			throw new RequestParameterException("You need to provide location or longitute and latitute parameters",
					RequestParameterException.MISSING, "location or longitute and latitute");
		}
		return result;
	}

	/**
	 * Method returns the reviews for a product with the given product code.
	 * 
	 * @return product's review list
	 */
	@RequestMapping(value = "/{productCode}/reviews", method = RequestMethod.GET)
	@ResponseBody
	public ReviewListWsDTO getProductReviews(@PathVariable final String productCode,
			@RequestParam(required = false) final Integer maxCount,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final ReviewListWsDTO reviewListWsDTO = new ReviewListWsDTO();
		final List<ReviewData> reviewDataList = productFacade.getReviews(productCode, maxCount);
		reviewListWsDTO.setReviews(dataMapper.mapAsList(reviewDataList, ReviewWsDTO.class, fields));
		return reviewListWsDTO;
	}

	/**
	 * Method creates a new customer review as an anonymous user.
	 * 
	 * @formparam rating This parameter is required. Value needs to be between 1 and 5.
	 * @formparam alias
	 * @formparam headline This parameter is required.
	 * @formparam comment This parameter is required.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return Created review
	 * @throws WebserviceValidationException
	 *            When given parameters are incorrect
	 */
	@RequestMapping(value = "/{productCode}/reviews", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ReviewWsDTO createReview(@PathVariable final String productCode,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletRequest request)
			throws WebserviceValidationException
	{
		final ReviewData reviewData = new ReviewData();
		httpRequestReviewDataPopulator.populate(request, reviewData);
		validate(reviewData, "reviewData", reviewValidator);
		final ReviewData reviewDataRet = productFacade.postReview(productCode, reviewData);
		final ReviewWsDTO dto = dataMapper.map(reviewDataRet, ReviewWsDTO.class, fields);
		return dto;
	}

	/**
	 * Method creates a new customer review as an anonymous user.
	 * 
	 * @param review
	 *           Object contains review details like : rating, alias, headline, comment
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @bodyparams headline,alias,rating,date,comment
	 * @return Created review
	 * @throws WebserviceValidationException
	 *            When given parameters are incorrect
	 */
	@RequestMapping(value = "/{productCode}/reviews", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ReviewWsDTO createReview(@PathVariable final String productCode, @RequestBody final ReviewWsDTO review,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws WebserviceValidationException
	{
		validate(review, "review", reviewDTOValidator);
		final ReviewData reviewData = dataMapper.map(review, ReviewData.class, "alias,rating,headline,comment");
		final ReviewData reviewDataRet = productFacade.postReview(productCode, reviewData);
		final ReviewWsDTO dto = dataMapper.map(reviewDataRet, ReviewWsDTO.class, fields);
		return dto;
	}

	/**
	 * Method returns references for a product for a given product code. Reference type specifies which references to
	 * return.
	 * 
	 * @queryparam pageSize Maximum size of returned results.
	 * @queryparam referenceType Reference type according to enum ProductReferenceTypeEnum
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return List of product references
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/{productCode}/references", method = RequestMethod.GET)
	@ResponseBody
	public ProductReferenceWsDTO exportProductReferences(@PathVariable final String productCode,
			@RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize,
			@RequestParam final String referenceType, @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final List<ProductOption> opts = Lists.newArrayList(OPTIONS);
		final ProductReferenceTypeEnum referenceTypeEnum = ProductReferenceTypeEnum.valueOf(referenceType);

		final List<ProductReferenceData> productReferences = productFacade.getProductReferencesForCode(productCode,
				referenceTypeEnum, opts, Integer.valueOf(pageSize));

		final ProductReferencesData productReferencesData = new ProductReferencesData();
		productReferencesData.setReferences(productReferences);

		final ProductReferenceWsDTO dto = dataMapper.map(productReferencesData, ProductReferenceWsDTO.class, fields);
		return dto;
	}

	private PageableData createPageableData(final int currentPage, final int pageSize, final String sort)
	{
		final PageableData pageable = new PageableData();

		pageable.setCurrentPage(currentPage);
		pageable.setPageSize(pageSize);
		pageable.setSort(sort);
		return pageable;
	}

	private GeoPoint createGeoPoint(final Double latitude, final Double longitude)
	{
		final GeoPoint point = new GeoPoint();
		point.setLatitude(latitude.doubleValue());
		point.setLongitude(longitude.doubleValue());

		return point;
	}

	/**
	 * Method returns list of all available suggestions related to the given term and limits the results to a given value
	 * of the max parameter.
	 * 
	 * @queryparam term Specified term
	 * @queryparam max Specifies the limit of results.
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return List of auto suggestions
	 */
	@RequestMapping(value = "/suggestions", method = RequestMethod.GET)
	@ResponseBody
	public SuggestionListWsDTO getSuggestions(@RequestParam(required = true, defaultValue = " ") final String term,
			@RequestParam(required = true, defaultValue = "10") final int max,
			@RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final List<SuggestionData> suggestions = new ArrayList<>();
		final SuggestionListWsDTO suggestionListWsDTO = new SuggestionListWsDTO();

		List<AutocompleteSuggestionData> autoSuggestions = productSearchFacade.getAutocompleteSuggestions(term);
		if (max < autoSuggestions.size())
		{
			autoSuggestions = autoSuggestions.subList(0, max);
		}

		for (final AutocompleteSuggestionData autoSuggestion : autoSuggestions)
		{
			final SuggestionData suggestionData = new SuggestionData();
			suggestionData.setValue(autoSuggestion.getTerm());
			suggestions.add(suggestionData);
		}

		suggestionListWsDTO.setSuggestions(dataMapper.mapAsList(suggestions, SuggestionWsDTO.class, fields));
		return suggestionListWsDTO;
	}

	/**
	 * Method returns products added to the express update feed. Returns only elements newer than timestamp.The queue is
	 * cleared using the defined cronjob.
	 * 
	 * @queryparam timestamp Only items newer than the given parameter are retrieved from the queue. This parameter
	 *             should be in RFC-8601 format.
	 * @queryparam catalog Only products from this catalog are returned. Format: <b>catalogId:catalogVersion</b>
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @return List of products added to the express update feed
	 * @throws RequestParameterException
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/expressupdate", method = RequestMethod.GET)
	@ResponseBody
	public ProductExpressUpdateElementListWsDTO expressUpdate(@RequestParam final String timestamp,
			@RequestParam(required = false) final String catalog,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields) throws RequestParameterException
	{
		final Date timestampDate;
		try
		{
			timestampDate = wsDateFormatter.toDate(timestamp);
		}
		catch (final IllegalArgumentException ex)
		{
			throw new RequestParameterException("Wrong time format. The only accepted format is ISO-8601.",
					RequestParameterException.INVALID, "timestamp", ex);
		}
		final ProductExpressUpdateElementListWsDTO productExpressUpdateElementListWsDTO = new ProductExpressUpdateElementListWsDTO();
		final List<ProductExpressUpdateElementData> products = productExpressUpdateQueue.getItems(timestampDate);
		filterExpressUpdateQueue(products, validateAndSplitCatalog(catalog));
		productExpressUpdateElementListWsDTO.setProductExpressUpdateElements(dataMapper.mapAsList(products,
				ProductExpressUpdateElementWsDTO.class, fields));
		return productExpressUpdateElementListWsDTO;
	}

	private void filterExpressUpdateQueue(final List<ProductExpressUpdateElementData> products, final List<String> catalogInfo)
	{
		if (catalogInfo.size() == 2 && StringUtils.isNotEmpty(catalogInfo.get(CATALOG_ID_POS))
				&& StringUtils.isNotEmpty(catalogInfo.get(CATALOG_VERSION_POS)) && CollectionUtils.isNotEmpty(products))
		{
			final Iterator<ProductExpressUpdateElementData> dataIterator = products.iterator();
			while (dataIterator.hasNext())
			{
				final ProductExpressUpdateElementData productExpressUpdateElementData = dataIterator.next();
				if (!catalogInfo.get(CATALOG_ID_POS).equals(productExpressUpdateElementData.getCatalogId())
						|| !catalogInfo.get(CATALOG_VERSION_POS).equals(productExpressUpdateElementData.getCatalogVersion()))
				{
					dataIterator.remove();
				}
			}
		}
	}

	protected List<String> validateAndSplitCatalog(final String catalog) throws RequestParameterException
	{
		final List<String> catalogInfo = new ArrayList<>();
		if (StringUtils.isNotEmpty(catalog))
		{
			catalogInfo.addAll(Lists.newArrayList(Splitter.on(':').trimResults().omitEmptyStrings().split(catalog)));
			if (catalogInfo.size() == 2)
			{
				catalogFacade.getProductCatalogVersionForTheCurrentSite(catalogInfo.get(CATALOG_ID_POS),
						catalogInfo.get(CATALOG_VERSION_POS), Collections.EMPTY_SET);
			}
			else if (!catalogInfo.isEmpty())
			{
				throw new RequestParameterException("Invalid format. You have to provide catalog as 'catalogId:catalogVersion'",
						RequestParameterException.INVALID, "catalog");
			}
		}
		return catalogInfo;
	}

}
