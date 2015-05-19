package de.hybris.platform.ycommercewebservices.v2.controller;

import de.hybris.platform.commercefacades.product.ProductExportFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductResultData;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductListWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.ycommercewebservices.constants.YcommercewebservicesConstants;
import de.hybris.platform.ycommercewebservices.formatters.WsDateFormatter;
import de.hybris.platform.ycommercewebservices.product.data.ProductDataList;
import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * Web Services Controller to expose the functionality of the
 * {@link de.hybris.platform.commercefacades.product.ProductFacade} and SearchFacade.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/export/products")
public class ExportController extends BaseController
{
	private static final Set<ProductOption> OPTIONS;
	private static final String DEFAULT_PAGE_VALUE = "0";
	private static final String MAX_INTEGER = "2147483647";
	private static final Logger LOG = Logger.getLogger(ExportController.class);
	private static String PRODUCT_OPTIONS = "";
	@Resource(name = "cwsProductExportFacade")
	private ProductExportFacade productExportFacade;
	@Resource(name = "wsDateFormatter")
	private WsDateFormatter wsDateFormatter;
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
	 * Methods for product export. Depends on timestamp parameter, it can return all products or only products modified
	 * after given time.
	 * 
	 * @queryparam fields Response configuration (list of fields, which should be returned in response)
	 * @queryparam currentPage The current result page requested.
	 * @queryparam pageSize The number of results returned per page.
	 * @queryparam catalog Catalog from which get products. Must be provided along with version.
	 * @queryparam version Catalog version. Must be provided along with catalog.
	 * @queryparam timestamp When this parameter is set, only products modified after given time will be returned.This
	 *             parameter should be in RFC-8601 format.
	 * @return List of products
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProductListWsDTO exportProducts(@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize,
			@RequestParam(required = false) final String catalog, @RequestParam(required = false) final String version,
			@RequestParam(required = false) final String timestamp)
	{
		if (StringUtils.isEmpty(catalog))
		{
			if (!StringUtils.isEmpty(version))
			{
				throw new RequestParameterException("Both 'catalog' and 'version' parameters have to be provided or ignored.",
						RequestParameterException.MISSING, catalog == null ? "catalog" : "version");
			}
		}

		if (StringUtils.isEmpty(version))
		{
			if (!StringUtils.isEmpty(catalog))
			{
				throw new RequestParameterException("Both 'catalog' and 'version' parameters have to be provided or ignored.",
						RequestParameterException.MISSING, catalog == null ? "catalog" : "version");
			}
		}

		if (StringUtils.isEmpty(timestamp))
		{
			return fullExport(fields, currentPage, pageSize, catalog, version);
		}
		else
		{
			return incrementalExport(fields, currentPage, pageSize, catalog, version, timestamp);
		}
	}

	private ProductListWsDTO incrementalExport(final String fields, final int currentPage, final int pageSize,
			final String catalog, final String version, final String timestamp)
	{
		final Date timestampDate;
		try
		{
			timestampDate = wsDateFormatter.toDate(timestamp);
		}
		catch (final IllegalArgumentException e)
		{
			throw new RequestParameterException("Wrong time format. The only accepted format is ISO-8601.",
					RequestParameterException.INVALID, "timestamp", e);
		}

		final ProductResultData modifiedProducts = productExportFacade.getOnlyModifiedProductsForOptions(catalog, version,
				timestampDate, OPTIONS, currentPage, pageSize);

		final ProductDataList result = convertResultset(currentPage, pageSize, catalog, version, modifiedProducts);
		return dataMapper.map(result, ProductListWsDTO.class, fields);
	}

	private ProductListWsDTO fullExport(final String fields, final int currentPage, final int pageSize, final String catalog,
			final String version)
	{
		final ProductResultData products = productExportFacade.getAllProductsForOptions(catalog, version, OPTIONS, currentPage,
				pageSize);

		//addUrlsToResult(catalog, version, products);
		final ProductDataList result = convertResultset(currentPage, pageSize, catalog, version, products);
		return dataMapper.map(result, ProductListWsDTO.class, fields);
	}

	private ProductDataList convertResultset(final int page, final int pageSize, final String catalog, final String version,
			final ProductResultData modifiedProducts)
	{
		final ProductDataList result = new ProductDataList();
		result.setProducts(modifiedProducts.getProducts());
		if (pageSize > 0)
		{
			result.setTotalPageCount((modifiedProducts.getTotalCount() % pageSize == 0) ? modifiedProducts.getTotalCount()
					/ pageSize : modifiedProducts.getTotalCount() / pageSize + 1);
		}
		result.setCurrentPage(page);
		result.setTotalProductCount(modifiedProducts.getTotalCount());
		result.setCatalog(catalog);
		result.setVersion(version);
		return result;
	}
}
