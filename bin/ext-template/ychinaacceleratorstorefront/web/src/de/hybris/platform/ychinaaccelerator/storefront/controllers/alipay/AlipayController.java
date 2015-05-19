/**
 * 
 */
package de.hybris.platform.ychinaaccelerator.storefront.controllers.alipay;

import de.hybris.platform.chinaaccelerator.alipay.constants.PaymentConstants;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayConfiguration;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayNotifyInfoData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayReturnData;
import de.hybris.platform.chinaaccelerator.alipay.enums.AlipayEnums.AlipayTradeStatus;
import de.hybris.platform.chinaaccelerator.alipay.util.AlipayServiceUtil;
import de.hybris.platform.chinaaccelerator.facades.order.ChinaOrderFacade;
import de.hybris.platform.ychinaaccelerator.storefront.controllers.ControllerConstants;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * AlipayController
 */
@Controller
@Scope("tenant")
public class AlipayController extends AbstractAlipayController
{
	protected static final Logger LOG = Logger.getLogger(AlipayController.class);
	private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";
	private static final String AMOUNT_PATH_VARIABLE_PATTERN = "{amount:.*}";

	@Resource(name = "alipayConfiguration")
	private AlipayConfiguration alipayConfiguration;

	/**
	 * Call for close the trade based on the Order Code, i.e. out_trade_no.
	 * */
	@RequestMapping(value = PaymentConstants.Controller.CLOSE_TRADE_URL + "/" + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String doCloseTrade(@PathVariable("orderCode") final String orderCode, final Model model,
			final HttpServletRequest request)
	{
		if (getAlipayConfiguration().getTest_mode() != null
				&& Boolean.valueOf(getAlipayConfiguration().getTest_mode()).equals(Boolean.TRUE))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("doCloseTrade..." + AlipayServiceUtil.printRequestParameters(request));
			}
			((ChinaOrderFacade) getOrderFacade()).closeTrade(orderCode);
		}
		return null;
	}

	/**
	 * Call for checking the current trade statue of the given Order code, i.e. out_trade_no.
	 * */
	@RequestMapping(value = PaymentConstants.Controller.CHECK_TRADE_URL + "/" + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String checkTradeStatus(@PathVariable("orderCode") final String orderCode, final Model model,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		if (getAlipayConfiguration().getTest_mode() != null
				&& Boolean.valueOf(getAlipayConfiguration().getTest_mode()).equals(Boolean.TRUE))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("checkTradeStatus..." + AlipayServiceUtil.printRequestParameters(request));
			}
			final AlipayTradeStatus status = ((ChinaOrderFacade) getOrderFacade()).checkTradeStatus(orderCode);
			try
			{
				response.getWriter().print(status);
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Alipay asynchronized call for Error notification the for the trade request.
	 * 
	 * @throws IOException
	 * */
	@RequestMapping(value = PaymentConstants.Controller.ERROR_NOTIFY_URL, method = RequestMethod.POST)
	public String doErrorLog(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("doErrorLog: " + AlipayServiceUtil.printRequestParameters(request));
		}
		try
		{
			final Map<String, String> errorNotify = AlipayServiceUtil.transRequestParam2Map(request.getParameterMap());
			final String orderNo = errorNotify.get("out_trade_no");
			final String errorCode = errorNotify.get("error_code");
			((ChinaOrderFacade) getOrderFacade()).handleErrorResponse(request.getParameterMap());
			response.getWriter().print("success");

			if (LOG.isDebugEnabled())
			{
				LOG.debug("POST doErrorLog... orderNo:" + orderNo + ", errorCode:" + errorCode);
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			response.getWriter().print("error");
		}
		return null;
	}


	/**
	 * Alipay asynchronized call for Notify the for the Trade status.
	 * */
	@RequestMapping(value = PaymentConstants.Controller.DIRECT_AND_EXPRESS_NOTIFY_URL, method = RequestMethod.POST)
	public String doDirectPayNotify(@ModelAttribute("AlipayNotifyInfoData") final AlipayNotifyInfoData notifyData,
			final Model model, final HttpServletRequest request, final HttpServletResponse response) throws IOException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("doDirectPayNotify..." + AlipayServiceUtil.printRequestParameters(request));
		}
		try
		{
			((ChinaOrderFacade) getOrderFacade()).handleResponse(notifyData, request.getParameterMap());
			response.getWriter().print("success"); //STOP the continues callback regardless.
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			response.getWriter().print("error");
		}
		return null;
	}

	/**
	 * Alipay synchronized call for responding the request.
	 * */
	@RequestMapping(value = PaymentConstants.Controller.DIRECT_AND_EXPRESS_RETURN_URL, method = RequestMethod.GET)
	public String doDirectPayReturnResponse(@ModelAttribute("AlipayReturnData") final AlipayReturnData returnData,
			final Model model, final HttpServletRequest request, final HttpServletResponse response) throws IOException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("doDirectPayReturnResponse..." + AlipayServiceUtil.printRequestParameters(request));
		}
		if (((ChinaOrderFacade) getOrderFacade()).handleResponse(returnData))
		{
			return REDIRECT_PREFIX + ROOT; //Can change into specific confirmation page later on.
		}
		return REDIRECT_PREFIX + ROOT;
	}


	/**
	 * Alipay execute to generate the request URL and so the system can switch to and forth between desktop and mobile
	 * device to finish up the payment for the same order.
	 * */
	@RequestMapping(value = PaymentConstants.Controller.GET_REQUEST_URL + "/" + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String doGetRequestUrl(@PathVariable("orderCode") final String orderCode, final Model model,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		final String url = ((ChinaOrderFacade) getOrderFacade()).getRequestUrl(orderCode);
		if (url.isEmpty())
		{
			// Order is not ready yet so we have to wait for
			if (LOG.isDebugEnabled())
			{
				LOG.debug("doGetRequestUrl: Order not ready waiting for !");
			}
			return ControllerConstants.Views.Pages.Alipay.wait;
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("doGetRequestUrl: " + url);
		}
		try
		{
			response.sendRedirect(url);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * @return the alipayConfiguration
	 */
	public AlipayConfiguration getAlipayConfiguration()
	{
		return alipayConfiguration;
	}

	/**
	 * @param alipayConfiguration
	 *           the alipayConfiguration to set
	 */
	public void setAlipayConfiguration(final AlipayConfiguration alipayConfiguration)
	{
		this.alipayConfiguration = alipayConfiguration;
	}
}
