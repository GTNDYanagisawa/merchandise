/**
 *
 */
package de.hybris.platform.sap.sapcarintegrationaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.sap.sapcarcommercefacades.order.CarOrderFacade;
import de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sappostransactionaddon.controllers.pages.PosTransactionPageControllerUtil;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author I813957 Multichannel transaction history controller
 */
@Controller
@RequestMapping("/my-account")
public class SapOrderHistoryPageController extends AbstractSapOrderHistoryPageController
{

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

	@Resource(name = "carOrderFacade")
	private CarOrderFacade carOrderFacade;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	protected static final String MC_TRANSACTION_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";
	protected static final String REDIRECT_MY_ACCOUNT = REDIRECT_PREFIX + "/my-account";

	protected static final String MC_TRANSACTIONS_HISTORY_CMS_PAGE = "mctransactions";
	protected static final String MC_WEB_TRANSACTION_DETAIL_CMS_PAGE = "order";
	protected static final String MC_POS_TRANSACTION_DETAIL_CMS_PAGE = "postransaction";
	protected static final String MC_SD_TRANSACTION_DETAIL_CMS_PAGE = "mcsdtransaction";


	private static final Logger LOG = Logger.getLogger(SapOrderHistoryPageController.class);


	/**
	 * @param page
	 * @param showMode
	 * @param sortCode
	 * @param model
	 * @return Multichannel order history view
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/purchases", method = RequestMethod.GET)
	@RequireHardLogIn
	public String readMultiChannelPurchases(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model)
			throws CMSItemNotFoundException
	{
		final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
		final SearchPageData<CarMultichannelOrderHistoryData> searchPageData = carOrderFacade
				.getPagedMultichannelOrderHistoryForCustomer(pageableData);
		populateModel(model, searchPageData, showMode);
		storeCmsPageInModel(model, getContentPageForLabelOrId(MC_TRANSACTIONS_HISTORY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MC_TRANSACTIONS_HISTORY_CMS_PAGE));

		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderHistory"));
		model.addAttribute("metaRobots", "noindex,nofollow");

		return getViewForPage(model);
	}

	/**
	 * @param orderCode
	 * @param model
	 * @return Hybris order details view
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/weborder/" + MC_TRANSACTION_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	@RequireHardLogIn
	public String readWebOrder(@PathVariable("orderCode") final String orderCode, final Model model)
			throws CMSItemNotFoundException
	{
		try
		{
			final OrderData orderDetails = orderFacade.getOrderDetailsForCode(orderCode);

			model.addAttribute("orderData", orderDetails);
			model.addAttribute("breadcrumbs", getBreadcrumbs(orderDetails.getCode()));

		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Attempted to load an order that does not exist or is not visible", e);
			return REDIRECT_MY_ACCOUNT;
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(MC_WEB_TRANSACTION_DETAIL_CMS_PAGE));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MC_WEB_TRANSACTION_DETAIL_CMS_PAGE));
		return getViewForPage(model);

	}

	/**
	 * @param transactionCode
	 * @param storeId
	 * @param transactionDate
	 * @param transactionIndex
	 * @param model
	 * @return POS transaction details view
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/instorepurchase/" + MC_TRANSACTION_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	@RequireHardLogIn
	public String readInStorePurchase(@PathVariable("orderCode") final String transactionCode,
			@RequestParam(value = "storeId", required = true) final String storeId,
			@RequestParam(value = "businessDayDate", required = true) final String businessDayDate,
			@RequestParam(value = "transactionIndex", required = true) final Integer transactionIndex, final Model model)
			throws CMSItemNotFoundException
	{
		try
		{
			final CarOrderHistoryData orderDetails = carOrderFacade.getOrderDetails(businessDayDate, storeId, transactionIndex);

			model.addAttribute("orderData", orderDetails);
			model.addAttribute("breadcrumbs", getBreadcrumbs(orderDetails.getPurchaseOrderNumber()));
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Attempted to load a POS Transaction that does not exist or is not visible", e);
			return REDIRECT_MY_ACCOUNT;
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(MC_POS_TRANSACTION_DETAIL_CMS_PAGE));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MC_POS_TRANSACTION_DETAIL_CMS_PAGE));
		return getViewForPage(model);
	}

	/**
	 * @param orderCode
	 * @param model
	 * @return Sales document details view
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/salesdocument/" + MC_TRANSACTION_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	@RequireHardLogIn
	public String readSalesDocument(@PathVariable("orderCode") final String orderCode, final Model model)
			throws CMSItemNotFoundException
	{
		try
		{
			final CarMultichannelOrderHistoryData orderDetails = carOrderFacade.getSalesDocumentDetails(orderCode);

			model.addAttribute("orderData", orderDetails);
			model.addAttribute("breadcrumbs", getBreadcrumbs(orderDetails.getPurchaseOrderNumber()));
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Attempted to load an ERP order that does not exist or is not visible", e);
			return REDIRECT_MY_ACCOUNT;
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(MC_SD_TRANSACTION_DETAIL_CMS_PAGE));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MC_SD_TRANSACTION_DETAIL_CMS_PAGE));

		return getViewForPage(model);
	}

	/**
	 * @param orderNumber
	 * @return Order history breadcrumbs
	 */
	protected List<Breadcrumb> getBreadcrumbs(final String orderNumber)
	{

		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);

		final String ordersLinkName = getMessageSource().getMessage("text.account.orderHistory", null, "Orders",
				getI18nService().getCurrentLocale());

		final String orderLinkName = getMessageSource().getMessage("text.account.order.orderBreadcrumb", new Object[]
		{ orderNumber }, "Order {0}", getI18nService().getCurrentLocale());

		breadcrumbs.add(new Breadcrumb("/my-account/purchases", ordersLinkName, null));
		breadcrumbs.add(new Breadcrumb("#", orderLinkName, null));

		return breadcrumbs;

	}

	protected OrderFacade getOrderFacade()
	{
		return orderFacade;
	}

	public void setOrderFacade(final OrderFacade orderFacade)
	{
		this.orderFacade = orderFacade;
	}

	protected CarOrderFacade getCarOrderFacade()
	{
		return carOrderFacade;
	}

	public void setCarOrderFacade(final CarOrderFacade carOrderFacade)
	{
		this.carOrderFacade = carOrderFacade;
	}

	protected ResourceBreadcrumbBuilder getAccountBreadcrumbBuilder()
	{
		return accountBreadcrumbBuilder;
	}

	public void setAccountBreadcrumbBuilder(final ResourceBreadcrumbBuilder accountBreadcrumbBuilder)
	{
		this.accountBreadcrumbBuilder = accountBreadcrumbBuilder;
	}

}
