/**
 * 
 */
package cn.alipay.payment.util;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;

import cn.alipay.payment.util.httpclient.HttpProtocolHandler;
import cn.alipay.payment.util.httpclient.HttpRequest;
import cn.alipay.payment.util.httpclient.HttpResponse;
import cn.alipay.payment.util.httpclient.HttpResultType;
import de.hybris.platform.chinaaccelerator.alipay.constants.PaymentConstants;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayCheckTradeStatusData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayCloseTradeData;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayConfiguration;
import de.hybris.platform.chinaaccelerator.alipay.data.AlipayRequestData;
import de.hybris.platform.chinaaccelerator.alipay.data.BaseRequestData;



public class AlipayUtil
{
	private static Map<String, String> buildRequestPara(final Map<String, String> sParaTemp, final String key)
	{

		final Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);

		final String mysign = AlipayCore.buildMysign(sPara, key);

		sPara.put("sign", mysign);
		sPara.put("sign_type", PaymentConstants.Basic.SIGN_TYPE);

		return sPara;
	}


	public static String buildForm(final Map<String, String> sParaTemp, final String gateway, final String key,
			final String strMethod, final String strButtonName)
	{

		final Map<String, String> sPara = buildRequestPara(sParaTemp, key);
		final List<String> keys = new ArrayList<String>(sPara.keySet());

		final StringBuffer sbHtml = new StringBuffer();

		sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + gateway + "_input_charset="
				+ PaymentConstants.Basic.INPUT_CHARSET + "\" method=\"" + strMethod + "\">");

		for (int i = 0; i < keys.size(); i++)
		{
			final String name = keys.get(i);
			final String value = sPara.get(name);

			sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
		}

		sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\"></form>");

		return sbHtml.toString();
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

	public static String getParameterPath(final Map<String, String> sParaTemp, final AlipayConfiguration alipayConfig)
			throws Exception
	{
		final Map<String, String> sPara = buildRequestPara(sParaTemp, alipayConfig.getWeb_key());
		final StringBuffer strResult = new StringBuffer();
		strResult.append(alipayConfig.getWeb_gateway());
		strResult.append(AlipayCore.createLinkString(sPara));
		return strResult.toString();
	}
	
    /**
     * Simulate the HTTP POST request, use this to get the XML response from Alipay
     * @param sParaTemp Request Parameters
     * @param gateway 
     * @return XML response returned by Alipay
     * @throws Exception
     */
	public static String sendPostInfo(final Map<String, String> sParaTemp, final AlipayConfiguration alipayConfig)
			throws Exception
	{
		final Map<String, String> sPara = buildRequestPara(sParaTemp, alipayConfig.getWeb_key());
		final HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
		final HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		request.setCharset(PaymentConstants.Basic.INPUT_CHARSET);
		request.setParameters(generatNameValuePair(sPara)); 
		request.setUrl(alipayConfig.getWeb_gateway() + "_input_charset=" + PaymentConstants.Basic.INPUT_CHARSET);
		final HttpResponse response = httpProtocolHandler.execute(request);
		if (response == null)
		{
			return null;
		}
		final String strResult = response.getStringResult();
		return strResult;
	}

//	public static Map<String, String> transBean2Map(final Object obj)
//	{
//		final Map<String, String> map = new LinkedHashMap<String, String>();
//		Field[] fields = obj.getClass().getDeclaredFields();
//		for (int i = 0; i < fields.length; i++)
//		{
//			try
//			{
//				fields[i].setAccessible(true);
//				final String key = fields[i].getName();
//				String val = "";
//				if (fields[i].get(obj) instanceof Float)
//				{
//					final Float fval = (Float) fields[i].get(obj);
//					if (fval.floatValue() > 0)
//					{
//						val = String.valueOf(fval);
//					}
//				}
//				else if (fields[i].get(obj) instanceof Integer)
//				{
//					final Integer ival = (Integer) fields[i].get(obj);
//					if (ival.intValue() > 0)
//					{
//						val = String.valueOf(ival);
//					}
//				}
//				else
//				{
//					val = (fields[i].get(obj) == null) ? "" : String.valueOf(fields[i].get(obj));
//				}
//				map.put(key, val);
//			}
//			catch (final IllegalArgumentException e)
//			{
//				e.printStackTrace();
//			}
//			catch (final IllegalAccessException e)
//			{
//				e.printStackTrace();
//			}
//		}
//		if (obj.getClass().getSuperclass() != null)
//		{
//			fields = obj.getClass().getSuperclass().getDeclaredFields();
//			for (int i = 0; i < fields.length; i++)
//			{
//				try
//				{
//					fields[i].setAccessible(true);
//					final String key = fields[i].getName();
//					String val = "";
//					if (fields[i].get(obj) instanceof Float)
//					{
//						final Float fval = (Float) fields[i].get(obj);
//						if (fval.floatValue() > 0)
//						{
//							val = String.valueOf(fval);
//						}
//					}
//					else if (fields[i].get(obj) instanceof Integer)
//					{
//						final Integer ival = (Integer) fields[i].get(obj);
//						if (ival.intValue() > 0)
//						{
//							val = String.valueOf(ival);
//						}
//					}
//					else
//					{
//						val = (fields[i].get(obj) == null) ? "" : String.valueOf(fields[i].get(obj));
//					}
//					map.put(key, val);
//				}
//				catch (final IllegalArgumentException e)
//				{
//					e.printStackTrace();
//				}
//				catch (final IllegalAccessException e)
//				{
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return map;
//	}


	// REDDRA-39 
//	public static Map<String, String> convertBean2Map(final Object bean) {
	public static Map<String, String> transBean2Map(final Object bean) {
		if (bean instanceof AlipayCheckTradeStatusData) {
			return convertAlipayCheckTradeStatusDataBean2Map((AlipayCheckTradeStatusData) bean);
		} else if (bean instanceof AlipayCloseTradeData) {
			return convertAlipayCloseTradeDataBean2Map((AlipayCloseTradeData) bean);
		} else if (bean instanceof AlipayRequestData) {
			return convertAlipayRequestDataBean2Map((AlipayRequestData) bean);
		} else {
			return Collections.emptyMap();
		}
	}
	
	private static Map<String, String> convertBaseRequestDataBean2Map(final BaseRequestData bean){
		final Map<String, String> map = new LinkedHashMap<String, String>();
		
		map.put(PaymentConstants.BaseRequestDataFields.INPUT_CHARSET, StringUtils.isEmpty(bean.get_input_charset()) ? "" : bean.get_input_charset());
		map.put(PaymentConstants.BaseRequestDataFields.SERVICE, StringUtils.isEmpty(bean.getService()) ? "" : bean.getService());
		map.put(PaymentConstants.BaseRequestDataFields.PARTNER, StringUtils.isEmpty(bean.getPartner()) ? "" : bean.getPartner());
		return map;
	}
		
	private static Map<String, String> convertAlipayCheckTradeStatusDataBean2Map(final AlipayCheckTradeStatusData bean){

		final Map<String, String> map = new LinkedHashMap<String, String>();
		
		map.put(PaymentConstants.AlipayCheckTradeStatusDataFields.OUT_TRADE_NO, StringUtils.isEmpty(bean.getOut_trade_no()) ? "" : bean.getOut_trade_no());
		map.put(PaymentConstants.AlipayCheckTradeStatusDataFields.TRADE_NO, StringUtils.isEmpty(bean.getTrade_no()) ? "" : bean.getTrade_no());
		
		final Map<String, String> mapBase = convertBaseRequestDataBean2Map((BaseRequestData)bean);
		map.putAll(mapBase);
		
		return map;
	}

	private static Map<String, String> convertAlipayCloseTradeDataBean2Map(final AlipayCloseTradeData bean){

		final Map<String, String> map = new LinkedHashMap<String, String>();
		
		map.put(PaymentConstants.AlipayCloseTradeDataFields.SIGN, StringUtils.isEmpty(bean.getSign()) ? "" : bean.getSign());
		map.put(PaymentConstants.AlipayCloseTradeDataFields.SIGN_TYPE, StringUtils.isEmpty(bean.getSign_type()) ? "" : bean.getSign_type());
		map.put(PaymentConstants.AlipayCloseTradeDataFields.TRADE_NO, StringUtils.isEmpty(bean.getTrade_no()) ? "" : bean.getTrade_no());
		map.put(PaymentConstants.AlipayCloseTradeDataFields.OUT_ORDER_NO, StringUtils.isEmpty(bean.getOut_order_no()) ? "" : bean.getOut_order_no());
		map.put(PaymentConstants.AlipayCloseTradeDataFields.IP, StringUtils.isEmpty(bean.getIp()) ? "" : bean.getIp());
		map.put(PaymentConstants.AlipayCloseTradeDataFields.TRADE_ROLE, StringUtils.isEmpty(bean.getTrade_role()) ? "" : bean.getTrade_role());

		
		final Map<String, String> mapBase = convertBaseRequestDataBean2Map((BaseRequestData)bean);
		map.putAll(mapBase);
		
		return map;
	}

	private static Map<String, String> convertAlipayRequestDataBean2Map(final AlipayRequestData bean){

		final Map<String, String> map = new LinkedHashMap<String, String>();
		
		map.put(PaymentConstants.AlipayRequestDataFields.SIGN_TYPE, StringUtils.isEmpty(bean.getSign_type()) ? "" : bean.getSign_type());
		map.put(PaymentConstants.AlipayRequestDataFields.SIGN, StringUtils.isEmpty(bean.getSign()) ? "" : bean.getSign());
		map.put(PaymentConstants.AlipayRequestDataFields.NOTIFY_URL, StringUtils.isEmpty(bean.getNotify_url()) ? "" : bean.getNotify_url());
		map.put(PaymentConstants.AlipayRequestDataFields.RETURN_URL, StringUtils.isEmpty(bean.getReturn_url()) ? "" : bean.getReturn_url());
		map.put(PaymentConstants.AlipayRequestDataFields.ERROR_NOTIFY_URL, StringUtils.isEmpty(bean.getError_notify_url()) ? "" : bean.getError_notify_url());
		
		map.put(PaymentConstants.AlipayRequestDataFields.OUT_TRADE_NO, StringUtils.isEmpty(bean.getOut_trade_no()) ? "" : bean.getOut_trade_no());
		map.put(PaymentConstants.AlipayRequestDataFields.SUBJECT, StringUtils.isEmpty(bean.getSubject()) ? "" : bean.getSubject());
		map.put(PaymentConstants.AlipayRequestDataFields.PAYMENT_TYPE, StringUtils.isEmpty(bean.getPayment_type()) ? "" : bean.getPayment_type());
		map.put(PaymentConstants.AlipayRequestDataFields.DEFAULTBANK, StringUtils.isEmpty(bean.getDefaultbank()) ? "" : bean.getDefaultbank());
		map.put(PaymentConstants.AlipayRequestDataFields.PAYMETHOD, StringUtils.isEmpty(bean.getPaymethod()) ? "" : bean.getPaymethod());

		map.put(PaymentConstants.AlipayRequestDataFields.SELLER_EMAIL, StringUtils.isEmpty(bean.getSeller_email()) ? "" : bean.getSeller_email());
		map.put(PaymentConstants.AlipayRequestDataFields.BUYER_EMAIL, StringUtils.isEmpty(bean.getBuyer_email()) ? "" : bean.getBuyer_email());
		map.put(PaymentConstants.AlipayRequestDataFields.SELLER_ID, StringUtils.isEmpty(bean.getSeller_id()) ? "" : bean.getSeller_id());
		map.put(PaymentConstants.AlipayRequestDataFields.BUYER_ID, StringUtils.isEmpty(bean.getBuyer_id()) ? "" : bean.getBuyer_id());
		map.put(PaymentConstants.AlipayRequestDataFields.SELLER_ACCOUNT_NAME, StringUtils.isEmpty(bean.getSeller_account_name()) ? "" : bean.getSeller_account_name());

		map.put(PaymentConstants.AlipayRequestDataFields.BUYER_ACCOUNT_NAME, StringUtils.isEmpty(bean.getBuyer_account_name()) ? "" : bean.getBuyer_account_name());
		map.put(PaymentConstants.AlipayRequestDataFields.PRICE, StringUtils.isEmpty(bean.getPrice()) ? "" : bean.getPrice());
		map.put(PaymentConstants.AlipayRequestDataFields.TOTAL_FEE, StringUtils.isEmpty(bean.getTotal_fee()) ? "" : bean.getTotal_fee());

		map.put(PaymentConstants.AlipayRequestDataFields.QUANTITY, (bean.getQuantity() > 0 ? String.valueOf(bean.getQuantity()) : ""));
		
		map.put(PaymentConstants.AlipayRequestDataFields.BODY, StringUtils.isEmpty(bean.getBody()) ? "" : bean.getBody());
		map.put(PaymentConstants.AlipayRequestDataFields.SHOW_URL, StringUtils.isEmpty(bean.getShow_url()) ? "" : bean.getShow_url());
		map.put(PaymentConstants.AlipayRequestDataFields.NEED_CTU_CHECK, StringUtils.isEmpty(bean.getNeed_ctu_check()) ? "" : bean.getNeed_ctu_check());
		map.put(PaymentConstants.AlipayRequestDataFields.ROYALTY_TYPE, StringUtils.isEmpty(bean.getRoyalty_type()) ? "" : bean.getRoyalty_type());
		map.put(PaymentConstants.AlipayRequestDataFields.ROYALTY_PARAMETERS, StringUtils.isEmpty(bean.getRoyalty_parameters()) ? "" : bean.getRoyalty_parameters());

		map.put(PaymentConstants.AlipayRequestDataFields.ANTI_PHISHING_KEY, StringUtils.isEmpty(bean.getAnti_phishing_key()) ? "" : bean.getAnti_phishing_key());
		map.put(PaymentConstants.AlipayRequestDataFields.EXTER_INVOKE_IP, StringUtils.isEmpty(bean.getExter_invoke_ip()) ? "" : bean.getExter_invoke_ip());
		map.put(PaymentConstants.AlipayRequestDataFields.EXTRA_COMMON_PARAM, StringUtils.isEmpty(bean.getExtra_common_param()) ? "" : bean.getExtra_common_param());
		map.put(PaymentConstants.AlipayRequestDataFields.EXTEND_PARAM, StringUtils.isEmpty(bean.getExtend_param()) ? "" : bean.getExtend_param());
		map.put(PaymentConstants.AlipayRequestDataFields.IT_B_PAY, StringUtils.isEmpty(bean.getIt_b_pay()) ? "" : bean.getIt_b_pay());

		map.put(PaymentConstants.AlipayRequestDataFields.DEFAULT_LOGIN, StringUtils.isEmpty(bean.getDefault_login()) ? "" : bean.getDefault_login());
		map.put(PaymentConstants.AlipayRequestDataFields.PRODUCT_TYPE, StringUtils.isEmpty(bean.getProduct_type()) ? "" : bean.getProduct_type());
		map.put(PaymentConstants.AlipayRequestDataFields.TOKEN, StringUtils.isEmpty(bean.getToken()) ? "" : bean.getToken());
		map.put(PaymentConstants.AlipayRequestDataFields.ITEM_ORDERS_INFO, StringUtils.isEmpty(bean.getItem_orders_info()) ? "" : bean.getItem_orders_info());
		
		
		final Map<String, String> mapBase = convertBaseRequestDataBean2Map((BaseRequestData)bean);
		map.putAll(mapBase);
		
		return map;
	}

}
