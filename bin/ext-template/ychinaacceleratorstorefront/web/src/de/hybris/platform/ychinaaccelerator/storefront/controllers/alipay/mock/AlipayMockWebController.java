/**
 * 
 */
package de.hybris.platform.ychinaaccelerator.storefront.controllers.alipay.mock;


import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.chinaaccelerator.alipay.constants.PaymentConstants;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayConfiguration;
import de.hybris.platform.core.Registry;
import de.hybris.platform.ychinaaccelerator.storefront.util.CSRFRequestDataValueProcessor;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.alipay.payment.util.AlipayCore;
import cn.alipay.payment.util.AlipayMd5Encrypt;
import cn.alipay.payment.util.httpclient.HttpProtocolHandler;
import cn.alipay.payment.util.httpclient.HttpRequest;
import cn.alipay.payment.util.httpclient.HttpResponse;
import cn.alipay.payment.util.httpclient.HttpResultType;


//import de.hybris.platform.ychinaaccelerator.storefront.controllers.AbstractController;


/**
 * 
 */
@Controller
public class AlipayMockWebController extends AbstractController
{
	protected static final Logger LOG = Logger.getLogger(AlipayMockWebController.class);

	@Autowired
	private AlipayConfiguration alipayConfiguration;

	private static final Map<String, Map<String, String>> refundBatches = new HashMap<String, Map<String, String>>();
	private static final Map<String, Map<String, String>> trades = new HashMap<String, Map<String, String>>();

	private static final String refundErrorsStr = "ILLEGAL_SIGN,ILLEGAL_DYN_MD5_KEY,ILLEGAL_ENCRYPT,ILLEGAL_ARGUMENT,ILLEGAL_SERVICE,ILLEGAL_USER,ILLEGAL_PARTNER,ILLEGAL_EXTERFACE,LLEGAL_PARTNER_EXTERFACE,ILLEGAL_SECURITY_PROFILE,ILLEGAL_AGENT,ILLEGAL_SIGN_TYPE,ILLEGAL_CHARSET,ILLEGAL_CLIENT_IP,HAS_NO_PRIVILEGE,ILLEGAL_DIGEST_TYPE,ILLEGAL_DIGEST,ILLEGAL_FILE_FORMAT,ILLEGAL_ENCODING,ILLEGAL_REQUEST_REFERER,ILLEGAL_ANTI_PHISHING_KEY,ANTI_PHISHING_KEY_TIMEOUT,ILLEGAL_EXTER_INVOKE_IP,BATCH_NUM_EXCEED_LIMIT,REFUND_DATE_ERROR,BATCH_NUM_ERROR,DUBL_ROYALTY_IN_DETAIL,BATCH_NUM_NOT_EQUAL_TOTAL,SINGLE_DETAIL_DATA_EXCEED_LIMIT,DUBL_TRADE_NO_IN_SAME_BATCH,DUPLICATE_BATCH_NO,TRADE_STATUS_ERROR,BATCH_NO_FORMAT_ERROR,PARTNER_NOT_SIGN_PROTOCOL,NOT_THIS_PARTNERS_TRADE,DETAIL_DATA_FORMAT_ERROR,SELLER_NOT_SIGN_PROTOCOL,INVALID_CHARACTER_SET,ACCOUNT_NOT_EXISTS,EMAIL_USERID_NOT_MATCH,REFUND_ROYALTY_FEE_ERROR,ROYALTYER_NOT_SIGN_PROTOCOL,RESULT_AMOUNT_NOT_VALID,REASON_REFUND_ROYALTY_ERROR,TRADE_NOT_EXISTS,WHOLE_DETAIL_FORBID_REFUND,TRADE_HAS_CLOSED,TRADE_HAS_FINISHED,NO_REFUND_CHARGE_PRIVILEDGE,RESULT_BATCH_NO_FORMAT_ERROR,BATCH_MEMO_LENGTH_EXCEED_LIMIT,REFUND_CHARGE_FEE_GREATER_THAN_LIMIT,REFUND_TRADE_FEE_ERROR,SELLER_STATUS_NOT_ALLOW,SINGLE_DETAIL_DATA_ENCODING_NOT_SUPPORT,TXN_RESULT_ACCOUNT_STATUS_NOT_VALID,TXN_RESULT_ACCOUNT_BALANCE_NOT_ENOUGH,CA_USER_NOT_USE_CA,BATCH_REFUND_LOCK_ERROR,REFUND_SUBTRADE_FEE_ERROR,NANHANG_REFUND_CHARGE_AMOUNT_ERROR,REFUND_AMOUNT_NOT_VALID,TRADE_PRODUCT_TYPE_NOT_ALLOW_REFUND,RESULT_FACE_AMOUNT_NOT_VALID,REFUND_CHARGE_FEE_ERROR,REASON_REFUND_CHARGE_ERR,RESULT_AMOUNT_NOT_VALID,DUP_ROYALTY_REFUND_ITEM,RESULT_ACCOUNT_NO_NOT_VALID,REASON_REFUND_ROYALTY_ERROR,REASON_TRADE_REFUND_FEE_ERR,REASON_HAS_REFUND_FEE_NOT_MATCH,TXN_RESULT_ACCOUNT_STATUS_NOT_VALID,TXN_RESULT_ACCOUNT_BALANCE_NOT_ENOUGH,REASON_REFUND_AMOUNT_LESS_THAN_COUPON_FEE,ATCH_REFUND_STATUS_ERROR,BATCH_REFUND_DATA_ERROR,REFUND_TRADE_FAILED,REFUND_FAIL";
	private static final Map<String, String> refundErrors;
	static
	{
		refundErrors = new HashMap<String, String>();
		final String[] errors = refundErrorsStr.split(",");
		for (int i = 0; i < errors.length; i++)
		{
			refundErrors.put(errors[i], errors[i]);
		}
	}

	@RequestMapping(value = "alipay/mock/gateway.do/notify.verify")
	public String notifyVerify(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException
	{
		if (!isTestMode())
		{
			return "";
		}
		LOG.info("notify.verify");
		//TODO make some checks before answer
		response.getWriter().print("true");
		return null;
	}

	@SuppressWarnings("boxing")
	@RequestMapping(value = "alipay/mock/gateway.do")
	public String gatewayDo(final Model model, final HttpServletRequest request, final HttpServletResponse response)
	{
		if (!isTestMode())
		{
			return "";
		}
		LOG.info("controller:/alipay/mock/gateway.do");

		final Map<String, String[]> requestParams = request.getParameterMap();

		String action = "";
		String tradeStatus = "";
		String errorCode = "";
		String service = "";

		final String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath() + "/";
		model.addAttribute("basePath", basePath);

		if (requestParams.containsKey("service"))
		{
			service = XSSFilterUtil.filter(request.getParameterValues("service")[0]);
		}
		if (requestParams.containsKey("action"))
		{
			action = XSSFilterUtil.filter(requestParams.get("action")[0]);
		}
		if (requestParams.containsKey("trade_status"))
		{
			tradeStatus = XSSFilterUtil.filter(request.getParameterValues("trade_status")[0]);
		}
		if (requestParams.containsKey("error_code"))
		{
			errorCode = XSSFilterUtil.filter(request.getParameterValues("error_code")[0]);
		}

		final Map<String, String> params = new HashMap<String, String>();


		LOG.debug("Setting CSRF Token into params");
		this.setCSRFToken(params, request); // hook in CSRF token
		LOG.debug("Set CSRF Token into params." + params);


		for (final String key : requestParams.keySet())
		{
			if (key.equalsIgnoreCase("action") || key.equalsIgnoreCase("trade_status") || key.equalsIgnoreCase("error_code"))
			{
				continue;
			}

			final String value = requestParams.get(key)[0];
			params.put(key, value);
		}

		if (!service.isEmpty())
		{
			final String sign = getMysign(params, alipayConfiguration.getWeb_key());
			final boolean signIsValid = sign.equals(params.get("sign"));
			model.addAttribute("signIsValid", signIsValid);
			LOG.info("signature:" + signIsValid);

			if ("single_trade_query".equals(service))
			{
				// Check Trade request
				LOG.info("Single trade query request");

				final String defaultTradeStatus = Registry.getMasterTenant().getConfig()
						.getString("alipay.mock.default.trade.status", "WAIT_BUYER_PAY");
				final String outTradeNo = params.get("out_trade_no");
				final String totalFee = Registry.getMasterTenant().getConfig().getString("test.amount", "0.01");

				final Map<String, String> trade = new HashMap<String, String>();
				trade.put("out_trade_no", outTradeNo);
				trade.put("trade_status", defaultTradeStatus);
				trade.put("total_fee", totalFee);

				final String signTrade = getMysign(trade, alipayConfiguration.getWeb_key());

				final StringBuffer result = new StringBuffer();
				result.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><alipay><is_success>T</is_success>");
				result.append("<request><param name=\"_input_charset\">utf-8</param><param name=\"service\">single_trade_query</param><param name=\"partner\">"
						+ xssEncode(alipayConfiguration.getWeb_partner())
						+ "</param><param name=\"out_trade_no\">"
						+ xssEncode(outTradeNo) + "</param></request>");

				result.append("<response><trade>");
				result.append("<out_trade_no>" + xssEncode(outTradeNo) + "</out_trade_no>");
				result.append("<trade_status>" + xssEncode(defaultTradeStatus) + "</trade_status>");
				result.append("<total_fee>" + xssEncode(totalFee) + "</total_fee>");
				result.append("</trade></response>");
				result.append("<sign_type>MD5</sign_type>");
				result.append("<sign>" + signTrade + "</sign>");
				result.append("</alipay>");
				try
				{
					response.getWriter().print(result.toString());
				}
				catch (final IOException e)
				{
					// YTODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			else if ("close_trade".equals(service))
			{
				// Close Trade request
				LOG.info("Close trade request");

				//				errorCode = checkCloseTradeData(params);
				final String result = "<?xml version=\"1.0\" encoding=\"utf-8\"?><alipay><is_success>T</is_success></alipay>";
				//				if (!errorCode.isEmpty())
				//				{
				//					result = "<?xml version=\"1.0\" encoding=\"utf-8\"?><alipay><is_success>F</is_success><error>" + errorCode
				//							+ "</error></alipay>";
				//				}

				try
				{
					response.getWriter().print(result);
				}
				catch (final IOException e)
				{
					// YTODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			else if ("refund_fastpay_by_platform_nopwd".equals(service))
			{
				// Refund request
				LOG.info("refund request");

				errorCode = checkRefundData(params);

				synchronized (refundBatches)
				{
					refundBatches.put(params.get("batch_no"), params);
				}
				LOG.info("goto " + basePath + "alipay/mock/refund");

				if (errorCode.isEmpty())
				{
					// first return status
					final String result = "<?xml version=\"1.0\" encoding=\"utf-8\"?><alipay><is_success>T</is_success></alipay>";
					try
					{
						response.getWriter().print(result);
					}
					catch (final IOException e)
					{
						// YTODO Auto-generated catch block
						e.printStackTrace();
					}

					// next let send notification now
					final String defaultRefundRequestStatus = Registry.getMasterTenant().getConfig()
							.getString("alipay.mock.default.refund.request.status", "SUCCESS");
					notifyRefund(params, defaultRefundRequestStatus);

					final String totalFee = Registry.getMasterTenant().getConfig().getString("test.amount", "0.01");
					final String refundAmount = params.get("refund_amount");
					if (Float.valueOf(totalFee) != Float.valueOf(refundAmount))
					{
						params.put("payment_type", "1");
						params.put("out_trade_no", params.get("trade_no"));
						params.put("total_fee", totalFee);
						final String defaultRefundStatus = Registry.getMasterTenant().getConfig()
								.getString("alipay.mock.default.refund.status", "REFUND_SUCCESS");
						notify(params, "TRADE_SUCCESS", defaultRefundStatus);
					}
				}
				else
				{
					final String result = "<?xml version=\"1.0\" encoding=\"utf-8\"?><alipay><is_success>F</is_success><error>"
							+ errorCode + "</error></alipay>";
					try
					{
						response.getWriter().print(result);
					}
					catch (final IOException e)
					{
						// YTODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return null;
			}
			else
			// create_direct_pay_by_user
			{
				// Payment request
				LOG.info("Payment request");
				if (action.equalsIgnoreCase("notify"))
				{
					notify(params, tradeStatus);
				}
				else if (action.equalsIgnoreCase("notify_error"))
				{
					notifyError(params, errorCode);
				}
				else if (action.equalsIgnoreCase("return"))
				{
					if (returnToShop(response, params, tradeStatus))
					{
						return null;
					}
				}
				else if (tradeStatus.isEmpty())
				{
					if (signIsValid)
					{
						final String defaultTradeStatus = Registry.getMasterTenant().getConfig()
								.getString("alipay.mock.default.trade.status", "WAIT_BUYER_PAY");
						notify(params, "WAIT_BUYER_PAY");
						if (!"WAIT_BUYER_PAY".equals(defaultTradeStatus))
						{
							//							final Map<String, String> nMap = new HashMap<String, String>();
							//							this.setCSRFToken(nMap, request);
							notify(params, defaultTradeStatus);

							if ("TRADE_SUCCESS".equals(defaultTradeStatus))
							{
								returnToShop(response, params, tradeStatus);
								return null;
							}
						}
					}
				}
			}
		}

		//response.getWriter().print("success");

		// CSRF
		//		final CSRFRequestDataValueProcessor oo = new CSRFRequestDataValueProcessor();
		//		final Map<String, String> csrfHiddenField = oo.getExtraHiddenFields(request);
		//		params.putAll(csrfHiddenField);
		//		this.setCSRFToken(params, request);
		//model.addAllAttributes(csrfHiddenField);
		// CSRF


		model.addAttribute("params", params);
		model.addAttribute("out_trade_no", params.get("out_trade_no"));
		model.addAttribute("total_fee", params.get("total_fee"));
		LOG.debug("about to send back pages/alipay/mockWeb ");


		return "pages/alipay/mockWeb";
	}

	protected String xssEncode(final String value)
	{
		return (value == null) ? null : XSSFilterUtil.filter(value);
	}

	private static String getMysign(final Map<String, String> Params, final String key)
	{
		Map<String, String> sParaNew = AlipayCore.paraFilter(Params);

		//
		sParaNew = stripOffCSRFToken(sParaNew);

		return AlipayCore.buildMysign(sParaNew, key);
	}

	private String checkRefundData(final Map<String, String> params)
	{
		final String errorCode = Registry.getMasterTenant().getConfig().getString("alipay.mock.default.refund.errorCode", "");
		final String detailData = params.get("detail_data");
		final String[] tokens = detailData.split("\\^", 3);
		params.put("trade_no", tokens[0]);
		params.put("refund_amount", tokens[1]);
		params.put("refund_reason", tokens[2]);
		return errorCode;
	}

	private void notifyRefund(final Map<String, String> params, final String refundStatus)
	{
		LOG.info("action: notify");

		final Map<String, String> notify = new HashMap<String, String>();

		// Basic parameters
		notify.put("notify_id", getNotifyId());
		notify.put("notify_time", getNotifyTime());
		notify.put("notify_type", "batch_refund_notify");

		// Business Parameters
		notify.put("batch_no", params.get("batch_no"));
		if ("SUCCESS".equals(refundStatus))
		{
			notify.put("success_num", params.get("batch_num"));
		}
		else
		{
			notify.put("success_num", "0");
		}

		final String resultDetails = params.get("trade_no") + "^" + params.get("refund_amount") + "^" + refundStatus;
		notify.put("result_details", resultDetails);

		// Signature
		notify.put("sign_type", "MD5");
		notify.put("sign", getMysign(notify, alipayConfiguration.getWeb_key()));

		LOG.debug(notify.toString());

		final String url = params.get("notify_url");
		//final String url = "http://localhost:9001/cn/alipay/notifyController";
		String result;
		try
		{
			result = sendPostInfo(notify, url);
		}
		catch (final Exception e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
			result = "error";
		}

		LOG.debug("result:" + result);
	}

	private void notify(final Map<String, String> params, final String tradeStatus)
	{
		notify(params, tradeStatus, "");
	}

	private void notify(final Map<String, String> params, final String tradeStatus, final String refundStatus)
	{
		LOG.info("action: notify");

		final Map<String, String> notify = new HashMap<String, String>(params);

		notify.remove("error_notify_url");
		notify.remove("notify_url");
		notify.remove("return_url");
		notify.remove("service");

		notify.put("trade_status", tradeStatus);

		if (!refundStatus.isEmpty())
		{
			notify.put("refund_status", refundStatus);
		}
		notify.put("trade_no", params.get("out_trade_no"));
		notify.put("notify_id", getNotifyId());
		notify.put("notify_time", getNotifyTime());
		notify.put("notify_type", "trade_status_sync");
		notify.put("sign_type", "MD5");
		notify.put("sign", getMysign(notify, alipayConfiguration.getWeb_key()));


		//final String url = params.get("notify_url");
		final String url = alipayConfiguration.getBasepath() + PaymentConstants.Controller.DIRECT_AND_EXPRESS_NOTIFY_URL;
		//Registry.getMasterTenant().getConfig()
		//.getString("alipay.mock.payment.notify.url", "http://localhost:9001/cn/alipay/notifyController");
		//final String url = "http://localhost:9001/cn/alipay/notifyController"; //for Refund, the system also execute this notification, but the notify_url is passed over form the payment request and not the refund request
		String result;
		try
		{
			result = sendPostInfo(notify, url);
		}
		catch (final Exception e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
			result = "error";
		}

		LOG.debug("result:" + result);
	}

	private void notifyError(final Map<String, String> params, final String errorCode)
	{
		LOG.info("action: notify_error");

		final Map<String, String> notify = new HashMap<String, String>();

		notify.put("notify_id", getNotifyId());
		notify.put("notify_time", getNotifyTime());
		notify.put("partner", params.get("partner"));
		notify.put("out_trade_no", params.get("out_trade_no"));
		notify.put("error_code", errorCode);
		notify.put("return_url", params.get("error_notify_url"));
		notify.put("buyer_id", params.get("buyer_id"));
		notify.put("seller_email", params.get("seller_email"));
		notify.put("seller_id", params.get("seller_id"));
		notify.put("sign_type", "MD5");
		notify.put("sign", getMysign(notify, alipayConfiguration.getWeb_key()));

		final String url = params.get("error_notify_url");
		//final String url = "http://localhost:9001/cn/alipay/errorController";
		String result;
		try
		{
			result = sendPostInfo(notify, url);
		}
		catch (final Exception e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
			result = "error";
		}

		LOG.debug("result:" + result);
	}

	private boolean returnToShop(final HttpServletResponse response, final Map<String, String> params, final String tradeStatus)
	{
		try
		{
			final String returnUrl = getReturnShopUrl(params, tradeStatus);
			response.sendRedirect(returnUrl);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String getReturnShopUrl(final Map<String, String> params, final String tradeStatus) throws Exception
	{
		LOG.info("action: return");

		final Map<String, String> notify = new HashMap<String, String>(params);

		notify.remove("error_notify_url");
		notify.remove("notify_url");
		notify.remove("return_url");
		notify.remove("service");

		notify.put("is_success", "T");
		notify.put("trade_status", tradeStatus);
		notify.put("notify_id", getNotifyId());
		notify.put("notify_time", getNotifyTime());
		notify.put("notify_type", "trade_status_sync");
		notify.put("trade_no", params.get("out_trade_no"));
		notify.put("sign_type", "MD5");
		notify.put("sign", getMysign(notify, alipayConfiguration.getWeb_key()));

		final String baseUrl = params.get("return_url");
		//final String baseUrl = "http://localhost:9001/cn/alipay/returnresponseController";

		final String url = baseUrl + "?" + createLinkString(notify);

		LOG.debug("return to :" + url);
		return url;
	}

	public static String createLinkString(final Map<String, String> params) throws Exception
	{

		final List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++)
		{
			final String key = keys.get(i);
			final String value = params.get(key);

			if (i == keys.size() - 1)
			{
				prestr = prestr + key + "=" + URLEncoder.encode(value, "UTF-8");
			}
			else
			{
				prestr = prestr + key + "=" + URLEncoder.encode(value, "UTF-8") + "&";
			}
		}

		return prestr;
	}

	public static String mapToXml(final Map<String, String> map, final String root)
	{
		return mapToXml(map, root, true);
	}

	public static String mapToXml(final Map<String, String> map, final String root, final boolean addXmlSigLine)
	{
		final StringBuilder sb = new StringBuilder(addXmlSigLine ? "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" : "");
		sb.append("<");
		sb.append(root);
		sb.append(">");

		for (final Map.Entry<String, String> e : map.entrySet())
		{
			sb.append("<");
			sb.append(e.getKey());
			sb.append(">");
			sb.append(e.getValue());
			sb.append("</");
			sb.append(e.getKey());
			sb.append(">");
		}

		sb.append("</");
		sb.append(root);
		sb.append(">");

		return sb.toString();
	}

	public static String sendPostInfo(final Map<String, String> params, final String gateway) throws Exception
	{

		final HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();

		final HttpRequest request = new HttpRequest(HttpResultType.BYTES);

		request.setCharset("utf-8");


		//
		final Map<String, String> paramsWithoutCSRFToken = stripOffCSRFToken(params);


		//request.setParameters(generatNameValuePair(params));
		request.setParameters(generatNameValuePair(paramsWithoutCSRFToken));
		request.setUrl(gateway);

		final HttpResponse response = httpProtocolHandler.execute(request);
		if (response == null)
		{
			return null;
		}

		LOG.debug(response.toString());

		return response.getStringResult();
	}

	private static NameValuePair[] generatNameValuePair(final Map<String, String> properties)
	{
		final NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
		int i = 0;
		for (final Map.Entry<String, String> entry : properties.entrySet())
		{
			nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
		}

		return nameValuePair;
	}

	private String getNotifyId()
	{
		return AlipayMd5Encrypt.md5(String.valueOf(System.currentTimeMillis()));
	}

	private String getNotifyTime()
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	private boolean isTestMode()
	{
		return (alipayConfiguration.getTest_mode() != null && Boolean.valueOf(alipayConfiguration.getTest_mode()).equals(
				Boolean.TRUE));
	}

	private void setCSRFToken(final Map<String, String> m, final HttpServletRequest request)
	{
		final CSRFRequestDataValueProcessor proc = new CSRFRequestDataValueProcessor();
		final Map<String, String> csrfHiddenField = proc.getExtraHiddenFields(request);
		m.putAll(csrfHiddenField);
	}

	private static Map<String, String> stripOffCSRFToken(final Map<String, String> params)
	{
		if (params == null || params.isEmpty())
		{
			return params;
		}
		params.remove("CSRFToken");
		return params;
	}


}
