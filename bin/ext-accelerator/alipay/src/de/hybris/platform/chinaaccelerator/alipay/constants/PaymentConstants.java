/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.chinaaccelerator.alipay.constants;

/**
 * Global class for all alipay constants.
 */	
@SuppressWarnings("PMD")
public interface PaymentConstants
{
	/**
	 * Basic request constants
	 * */
	interface Basic
	{
		String EXTENSIONNAME = "alipay";

		String BANK_PAY_METHOD = "bankPay";

		String INSTANT_PAY_METHOD = "directPay";

		String EXPRESS_PAY_METHOD = "expressGateway";

		String SEC_ID = "0001"; //sec_id  0001: RSA; MD5: MD5

		String INPUT_CHARSET = "utf-8";

		String SIGN_TYPE = "MD5";

		String DEFAULT_LOGIN = "Y";

		String PAYMENT_PROVIDER = "Alipay"; //TODO where to define this?
		
		String MOBILE_FORMAT = "xml";
		
		String MOBILE_REQUEST_VERSION = "2.0";
		
		String MOBILE_REQUEST_TYPE = "POST";
	}

	interface ErrorHandler
	{
		String OUT_TRADE_NO = "out_trade_no";
		String ERROR_CODE = "error_code";
	}
	/**
	 * Controller constants
	 * */
	interface Controller
	{
		String _Prefix = "/alipay/";
		String _Suffix = "Controller"; 
		
		String DIRECT_AND_EXPRESS_RETURN_URL = _Prefix + "returnresponse" + _Suffix;
		String DIRECT_AND_EXPRESS_NOTIFY_URL = _Prefix + "notify" + _Suffix;
		String ERROR_NOTIFY_URL = _Prefix + "error" + _Suffix;
		String WAP_RETURN_URL = _Prefix + "mobile/returnresponse" + _Suffix;
		String WAP_NOTIFY_URL = _Prefix + "mobile/notify" + _Suffix;
		String CLOSE_TRADE_URL = _Prefix + "closetrade" + _Suffix;
		String CHECK_TRADE_URL = _Prefix + "checktrade" + _Suffix;
		String GET_REQUEST_URL = _Prefix + "request" + _Suffix;
		String GET_REFUND_URL = _Prefix + "refund" + _Suffix;
		String REFUND_NOTIFY_URL = _Prefix + "refundnotify" + _Suffix;
	}

	interface AlipayCheckTradeStatusDataFields
	{
		static final String OUT_TRADE_NO = "out_trade_no";
		static final String TRADE_NO = "trade_no";
	}

	interface AlipayCloseTradeDataFields
	{
		static final String SIGN = "sign";
		static final String SIGN_TYPE = "sign_type";
		static final String TRADE_NO = "trade_no";
		static final String OUT_ORDER_NO = "out_order_no";
		static final String IP = "ip";
		static final String TRADE_ROLE = "trade_role";
	}

	interface BaseRequestDataFields
	{
		static final String SERVICE = "service";
		static final String PARTNER = "partner";
		static final String INPUT_CHARSET = "_input_charset";
	}

	interface AlipayRequestDataFields
	{
		static final String SIGN_TYPE = "sign_type";
		static final String SIGN = "sign";
		static final String NOTIFY_URL = "notify_url";
		static final String RETURN_URL = "return_url";
		static final String ERROR_NOTIFY_URL = "error_notify_url";
		static final String OUT_TRADE_NO = "out_trade_no";
		static final String SUBJECT = "subject";
		static final String PAYMENT_TYPE = "payment_type";
		static final String DEFAULTBANK = "defaultbank";
		static final String PAYMETHOD = "paymethod";
		static final String SELLER_EMAIL = "seller_email";
		static final String BUYER_EMAIL = "buyer_email";
		static final String SELLER_ID = "seller_id";
		static final String BUYER_ID = "buyer_id";
		static final String SELLER_ACCOUNT_NAME = "seller_account_name";
		static final String BUYER_ACCOUNT_NAME = "buyer_account_name";
		static final String PRICE = "price";
		static final String TOTAL_FEE = "total_fee";
		static final String QUANTITY = "quantity";
		static final String BODY = "body";
		static final String SHOW_URL = "show_url";
		static final String NEED_CTU_CHECK = "need_ctu_check";
		static final String ROYALTY_TYPE = "royalty_type";
		static final String ROYALTY_PARAMETERS = "royalty_parameters";
		static final String ANTI_PHISHING_KEY = "anti_phishing_key";
		static final String EXTER_INVOKE_IP = "exter_invoke_ip";
		static final String EXTRA_COMMON_PARAM = "extra_common_param";
		static final String EXTEND_PARAM = "extend_param";
		static final String IT_B_PAY = "it_b_pay";
		static final String DEFAULT_LOGIN = "default_login";
		static final String PRODUCT_TYPE = "product_type";
		static final String TOKEN = "token";
		static final String ITEM_ORDERS_INFO = "item_orders_info";
	}
}
