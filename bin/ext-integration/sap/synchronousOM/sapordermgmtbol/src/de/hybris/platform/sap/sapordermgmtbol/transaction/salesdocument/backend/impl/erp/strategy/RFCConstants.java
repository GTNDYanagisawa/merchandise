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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;



/**
 * Constants used for the names of remote function modules, the structure names and the field names.
 * 
 * R/3 specific constants e.g. for statuses, R/3 releases or transaction types.
 * 
 * @version 1.1
 */
public interface RFCConstants
{


	/** Indicates a new paragraph in SAPScript. */
	public final static String SAPSCRIPT_PARAGRAPH = "*";

	/** Indicates a new line in SAPScript. */
	public final static String SAPSCRIPT_NEWLINE = "/";


	/** Key indicating the ISA status 'completed R/3 inquiry' */
	public final static String STATUS_ISA_COMPLETED_INQUIRY = "R3COMPLINQ";

	/** Key indicating the ISA status 'incomplete R/3 quotation' */
	public final static String STATUS_ISA_INCOMPLETE_QUOTATION = "R3IQU";


	// public final static String R3_INITIAL_DATE = "99991231";


	/** Key indicating the R/3 backend when the R/3 plug in is available. */
	public final static String BACKEND_R3_PI = "R3_PI";


	//**********************************************************************
	// Release Information
	//**********************************************************************

	//----------------------------------------------------------------------
	// The following release information consist of SAP_BASIS release
	// For example: ERP 6.0 EhP 1: SAP_BASIS = 700
	//----------------------------------------------------------------------

	/** Key indicating R/3 release 4.0b. */
	public final static String REL40B = "40B";

	/** Key indicating R/3 release 4.5b. */
	public final static String REL45B = "45B";

	/** Key indicating R/3 release 4.6b. */
	public final static String REL46B = "46B";

	/** Key indicating R/3 release 4.6c. */
	public final static String REL46C = "46C";

	/** Key indicating R/3 release 4.7. */
	public final static String REL620 = "620";

	/** Key indicating R/3 release ERP 1.0 */
	public final static String REL640 = "640";

	/** Key indicating R/3 release ERP 2.0 (ERP 2005, ECC 6.00) */
	public final static String REL700 = "700";

	/** Key indicating R/3 release ERP 6.0 */
	public final static String REL701 = "701";

	//-----------------------------------------------------------------------------------
	// The following release information is a combination of SAP_BASIS and SAP_APPL release
	// For example: ERP 6.0 EhP 1: SAP_BASIS = 700, SAP_APPL 601
	//------------------------------------------------------------------------------------

	/** Key indication R/3 release ERP 6.0 Ehp 1 */
	public final static String REL700EhP1 = "700601";

	/** Key indication R/3 release ERP 6.0 EhP 2 */
	public final static String REL700EhP2 = "700602";

	/** Key indication R/3 release ERP 6.0 EhP 3 */
	public final static String REL700EhP3 = "700603";

	/** Key indication R/3 release ERP 6.0 EhP 4 */
	public final static String REL701EhP4 = "701604";

	/** Constant for undefined Valid To Date */
	public static final Date R3_INITIAL_VALID_TO_DATE = (new GregorianCalendar(9999, 12, 31)).getTime();

	//***********************************************************************************

	/** Separator to build catalog GUID from catalog and variant. */
	public static String CATALOG_GUID_SEPARATOR = "_";

	/** Expected incoming locale for catalog prices. */
	public static Locale CAT_PRICE_INPUT_LOCALE = Locale.US;

	/** RFC constant. Indicates the reseller scenario for R/3 function modules. */
	public static String BOB_RESELLER_SCENARIO = "1";

	/** RFC constant. Indicates the agent scenario for R/3 function modules. */
	public static String BOB_AGENT_SCENARIO = "2";

	/** RFC constant. */
	public static String TRANSACTION_GROUP_ORDER = "0";

	/** RFC constant. */
	public static String TRANSACTION_GROUP_INQUIRY = "1";

	/** RFC constant. */
	public static String TRANSACTION_GROUP_QUOTATION = "2";

	/** RFC constant. */
	public static String TRANSACTION_GROUP_CONTRACT = "4";

	/** RFC constant. */
	public static String TRANSACTION_TYPE_CONTRACT = "G";

	/** RFC constant. */
	public static String TRANSACTION_TYPE_ORDER = "C";

	/** RFC constant. */
	public static String TRANSACTION_TYPE_QUOTATION = "B";

	/** RFC constant. */
	public static String TRANSACTION_TYPE_INQUIRY = "A";

	/** RFC constant. */
	public static String TRANSACTION_TYPE_DELIVERY = "J";

	/** RFC constant. */
	public static String TRANSACTION_TYPE_INVOICE = "M";

	/** RFC constant. */
	public static String TRANSACTION_TYPE_INVOICE_CANCEL = "N";

	/** RFC constant. */
	public static String TRANSACTION_TYPE_CREDIT_MEMO = "O";

	/** RFC constant. */
	public static String TRANSACTION_TYPE_CREDIT_MEMO_CANCEL = "S";

	/** RFC constant. */
	public static String TRANSACTION_TYPE_DEBIT_MEMO = "P";

	/** RFC constant. */
	public static String TRANSACTION_GROUP_NOT_SUPPORTED_IN_R3 = "9";

	/** RFC constant. */
	public static String STATUS_NOT_YET_PROCESSED = "A";

	/** RFC constant. */
	public static String STATUS_PARTIALLY_PROCESSED = "B";

	/** RFC constant. */
	public static String STATUS_COMPLETELY_PROCESSED = "C";

	/** RFC constant. */
	public static String ROLE_CONTACT = "AP";

	/** RFC constant. */
	public static String ROLE_SOLDTO = "AG";

	/** RFC constant. */
	public static String ROLE_SHIPTO = "WE";

	/** RFC constant. */
	public static String ROLE_BILLPARTY = "RE";

	/** RFC constant. */
	public static String ROLE_PAYER = "RG";

	/** RFC constant. */
	public static String X = "X";

	/** RFC constant. */
	public static String U = "U";

	/** RFC constant. */
	public static String CONDTYPE_FREIGHT = "F";

	/** RFC constant. */
	public static String SCHEDULE_INITIAL_KEY = "0001";

	/** RFC constant. */
	public static String BAPI_RETURN_ERROR = "E";

	/** RFC constant. */
	public static String BAPI_RETURN_WARNING = "W";

	/** RFC constant. */
	public static String BAPI_RETURN_INFO = "I";

	/** RFC constant. */
	public static String BAPI_RETURN_ABORT = "A";

	/** RFC constant. This means rather 'status' than 'success' */
	public static String BAPI_RETURN_SUCCESS = "S";

	/** RFC constant. */
	public static String BAPI_RETURN_TYPE_FIELD = "TYPE";

	/** RFC constant. */
	public static String ITEM_NUMBER_EMPTY = "000000";

	/** Constant for empty value in data element CUOBJ_VA */
	public static String EMPTY_NUMC_18 = "000000000000000000";

	/**
	 * Interface that holds the names of all RFC modules that are used within the ISA R/3 application.
	 * 
	 * <p>
	 * All RFC's have to be accessed using this interface.
	 * </p>
	 * 
	 */
	public static interface RfcName
	{

		/**
		 * Executes user login checks for user identification depending on login type and password
		 */
		public final static String ERP_ISA_LOGIN_R3USER_CHECKS = "ERP_ISA_LOGIN_R3USER_CHECKS";

		/**
		 * Reading the cross selling materials from the backend.
		 */
		public final static String ISA_CROSS_SELLING_DETERMINE = "ISA_CROSS_SELLING_DETERMINE";

		/**
		 * Retrieves IPC header attributes from the backend.
		 */
		public final static String ISA_PRICING_ITMDATA_GET = "ISA_PRICING_ITMDATA_GET";
		/**
		 * Retrieves IPC header attributes from the backend.
		 */
		public final static String ISA_PRICING_HDRDATA_GET = "ISA_PRICING_HDRDATA_GET";

		/**
		 * Converts material numbers from internal to external format and vice versa.
		 */
		public final static String ISA_MATNR_CONVERT = "ISA_MATNR_CONVERT";

		/**
		 * ISA function module for searching sales documents.
		 */
		public final static String ISA_SALESDOCUMENTS_SEARCH_NEW = "ISA_SALESDOCUMENTS_SEARCH_NEW";


		/**
		 * ISA function module for reading sales documents.
		 */
		public final static String BAPI_ISAORDER_GETDETAILEDLIST = "BAPI_ISAORDER_GETDETAILEDLIST";

		/**
		 * Checks if a given input string is a valid SU01 password.
		 */
		public final static String ISA_USER_PW_VALIDITY = "ISA_USER_PW_VALIDITY";

		/**
		 * Reads the SU01 user attached to a specific customer.
		 */
		public final static String ISA_USER_GET_FROM_CUSTOMER = "ISA_USER_GET_FROM_CUSTOMER";

		/**
		 * The function module for retrieving the units per material.
		 */
		public final static String ISA_GET_UNITS_FOR_MATERIALS = "ISA_GET_UNITS_FOR_MATERIALS";

		//function modules for Useradmin
		/**
		 * Deletion of user (+ attached contact person).
		 */
		public final static String ISA_USER_DELETE = "ISA_USER_DELETE";

		/**
		 * Creation of an ISA user (means: SU01 with customer or contact person attached).
		 */
		public final static String ISA_USER_CREATE = "ISA_USER_CREATE";

		/**
		 * Change an ISA (SU01) user.
		 */
		public final static String ISA_USER_CHANGE = "ISA_USER_CHANGE";

		/**
		 * Search for contact persons in ISA context. (means: only contact persons without any user attached).
		 */
		public final static String ISA_CONTACT_GETLIST = "ISA_CONTACT_GETLIST";

		/**
		 * Search for ISA (SU01) users (users with customer or contact persons attached).
		 */
		public final static String ISA_USER_SEARCH = "ISA_USER_SEARCH";

		/**
		 * Reads list of academic titles.
		 */
		public final static String ISA_ACAD_TITLES_GET = "ISA_ACAD_TITLES_GET";

		/**
		 * Reading the contact persons address data.
		 */
		public final static String BAPI_ADDRESSCONTPART_GETDETAIL = "BAPI_ADDRESSCONTPART_GETDETAIL";

		/**
		 * Reading a customer address from R/3.
		 */
		public final static String BAPI_CUSTOMER_GETLIST = "BAPI_CUSTOMER_GETLIST";

		/**
		 * This function module is not present in the standard R/3 system. If catalog views are to be used, it has to be
		 * created in the customers name space, the name mapping has to be done in modification-config.xml. Used for
		 * reading customer view attributes. <br>
		 * See OSS note 677319.
		 * */
		public final static String ISA_CUSTOMER_READ_CAT_VIEWS = "ISA_CUSTOMER_READ_CAT_VIEWS";

		/**
		 * Converting SAP UOM to ISO UOM.
		 */
		public final static String ISA_CONVERT_UOM = "ISA_CONVERT_UOM";
		/**
		 * Reading customers sales areas / sales area bundling.
		 */
		public final static String ISA_CUSTOMER_GETSALESAREAS = "ISA_CUSTOMER_GETSALESAREAS";

		/** RFC constant. Convert currency ISA codes into currency SAP codes. */
		public final static String ISA_CURRENCY_CONVERT = "ISA_CURRENCY_CONVERT";

		/** RFC constant. */
		public final static String ISA_GET_TRACKINGURL = "ISA_GET_TRACKINGURL";

		/** RFC constant. Check plant view of a material. */
		public final static String BAPI_MATERIAL_GET_DETAIL = "BAPI_MATERIAL_GET_DETAIL";

		/** RFC constant. Read customer info record. */
		public final static String BAPI_CUSTMATINFO_GETDETAILM = "BAPI_CUSTMATINFO_GETDETAILM";

		/** RFC constant. */
		public final static String ISA_CUSTOMER_SALES_READ = "ISA_CUSTOMER_SALES_READ";

		/** RFC constant. */
		public final static String ISA_CUSTOMER_SEARCH = "ISA_CUSTOMER_SEARCH";

		/** RFC constant. Read the catalog with documents and longtexts */
		public final static String ISA_READ_CATALOG_COMPLETE = "ISA_READ_CATALOG_COMPLETE";

		/** RFC constant. */
		public final static String USER_NAME_GET = "USER_NAME_GET";

		/** RFC constant. */
		public final static String BAPI_MVKE_ARRAY_READ = "BAPI_MVKE_ARRAY_READ";

		/** RFC constant. */
		public final static String MVKE_ARRAY_READ = "MVKE_ARRAY_READ";

		/** RFC constant. */
		public final static String BAPI_MATERIAL_GETLIST = "BAPI_MATERIAL_GETLIST";

		/** RFC constant. */
		public final static String BAPI_SALESDOCUMENT_COPY = "BAPI_SALESDOCUMENT_COPY";

		/** RFC constant. */
		public final static String ISA_UNIT_OF_MEASURE_CONVERT = "ISA_UNIT_OF_MEASURE_CONVERT";

		/** RFC constant. */
		public final static String ADDR_COMM_FIND_KEY = "ADDR_COMM_FIND_KEY";

		/** Function module for un-locking users. **/
		public final static String BAPI_USER_UNLOCK = "BAPI_USER_UNLOCK";

		/** Function module for locking users. **/
		public final static String BAPI_USER_LOCK = "BAPI_USER_LOCK";

		/** RFC constant. */
		public final static String ADDR_POSTAL_CODE_CHECK = "ADDR_POSTAL_CODE_CHECK";

		/** RFC constant. */
		public final static String BAPI_ADV_MED_GET_ITEMS = "BAPI_ADV_MED_GET_ITEMS";

		/** RFC constant. */
		public final static String BAPI_ADV_MED_GET_LAYOBJ_DESCR = "BAPI_ADV_MED_GET_LAYOBJ_DESCR";

		/** RFC constant. */
		public final static String BAPI_ADV_MED_GET_LAYOBJ_DOCS = "BAPI_ADV_MED_GET_LAYOBJ_DOCS";

		/** RFC constant. */
		public final static String BAPI_ADV_MED_GET_LAYOUT = "BAPI_ADV_MED_GET_LAYOUT";

		/** RFC constant. */
		public final static String BAPI_ADV_MED_GET_LIST = "BAPI_ADV_MED_GET_LIST";

		/** RFC constant. */
		public final static String BAPI_ADV_MED_GET_VARIANT_LIST = "BAPI_ADV_MED_GET_VARIANT_LIST";

		/** RFC constant. */
		public final static String BAPI_BUSPARTNEREMPLOYE_GETLIST = "BAPI_BUSPARTNEREMPLOYE_GETLIST";

		/** RFC constant. */
		public final static String BAPI_CREDITCARD_CHECK = "BAPI_CREDITCARD_CHECK";

		/** RFC constant. */
		public final static String BAPI_CREDITCARD_CHECKNUMBER = "BAPI_CREDITCARD_CHECKNUMBER";

		/** RFC constant. */
		public final static String BAPI_CUSTOMER_CHANGEFROMDATA = "BAPI_CUSTOMER_CHANGEFROMDATA";

		/** RFC constant. */
		public final static String BAPI_CUSTOMER_CHANGEFROMDATA1 = "BAPI_CUSTOMER_CHANGEFROMDATA1";

		/** RFC constant. */
		public final static String BAPI_CUSTOMER_CHANGEPASSWORD = "BAPI_CUSTOMER_CHANGEPASSWORD";

		/** RFC constant. */
		public final static String BAPI_CUSTOMER_CHECKEXISTENCE = "BAPI_CUSTOMER_CHECKEXISTENCE";

		/** RFC constant. */
		public final static String BAPI_CUSTOMER_CHECKPASSWORD = "BAPI_CUSTOMER_CHECKPASSWORD";

		/** RFC constant. */
		public final static String BAPI_CUSTOMER_CREATEFROMDATA = "BAPI_CUSTOMER_CREATEFROMDATA";

		/** RFC constant. */
		public final static String BAPI_CUSTOMER_CREATEFROMDATA1 = "BAPI_CUSTOMER_CREATEFROMDATA1";

		/** RFC constant. */
		public final static String BAPI_CUSTOMER_EXISTENCECHECK = "BAPI_CUSTOMER_EXISTENCECHECK";

		/** RFC constant. */
		public final static String BAPI_CUSTOMER_GETDETAIL = "BAPI_CUSTOMER_GETDETAIL";

		/** RFC constant. For reading the customers plant from releases 4.5b onwards */
		public final static String BAPI_CUSTOMER_GETDETAIL1 = "BAPI_CUSTOMER_GETDETAIL1";

		/** RFC constant. */
		public final static String BAPI_CUSTOMER_GETSALESAREAS = "BAPI_CUSTOMER_GETSALESAREAS";

		/** RFC constant. */
		public final static String BAPI_CUSTOMER_SEARCH = "BAPI_CUSTOMER_SEARCH";

		/** RFC constant. */
		public final static String BAPI_CUSTOMER_SEARCH1 = "BAPI_CUSTOMER_SEARCH1";

		/** RFC constant. */
		public final static String BAPI_HELPVALUES_GET = "BAPI_HELPVALUES_GET";

		/** RFC constant. */
		public final static String BAPI_MATERIAL_AVAILABILITY = "BAPI_MATERIAL_AVAILABILITY";

		/** RFC constant. */
		public final static String BAPI_PAR_EMPLOYEE_CHANGEPASSWO = "BAPI_PAR_EMPLOYEE_CHANGEPASSWO";

		/** RFC constant. */
		public final static String BAPI_PAR_EMPLOYEE_CHECKEXISTEN = "BAPI_PAR_EMPLOYEE_CHECKEXISTEN";

		/** RFC constant. */
		public final static String BAPI_PAR_EMPLOYEE_CHECKPASSWOR = "BAPI_PAR_EMPLOYEE_CHECKPASSWOR";

		/** RFC constant. */
		public final static String BAPI_PRODCAT_GETITEM = "BAPI_PRODCAT_GETITEM";

		/** RFC constant. */
		public final static String BAPI_SALESORDER_GETLIST = "BAPI_SALESORDER_GETLIST";

		/** RFC constant. */
		public final static String BAPI_SALESORDER_SIMULATE = "BAPI_SALESORDER_SIMULATE";

		/** RFC constant. */
		public final static String BAPI_TRANSACTION_COMMIT = "BAPI_TRANSACTION_COMMIT";

		/** RFC constant. */
		public final static String BAPI_USER_APPLICATION_OBJ_GET = "BAPI_USER_APPLICATION_OBJ_GET";

		/** RFC constant. */
		public final static String BAPI_USER_CHANGE = "BAPI_USER_CHANGE";

		/** RFC constant. */
		public final static String BAPI_USER_GET_DETAIL = "BAPI_USER_GET_DETAIL";

		/** RFC constant. */
		public final static String BAPI_WEBINVOICE_GETDETAIL = "BAPI_WEBINVOICE_GETDETAIL";

		/** RFC constant. */
		public final static String BAPI_WEBINVOICE_GETLIST = "BAPI_WEBINVOICE_GETLIST";

		/** RFC constant. */
		public final static String BAPI_XSI_GET_VTRK_G = "BAPI_XSI_GET_VTRK_G";

		/** RFC constant. */
		public final static String BAPISDORDER_GETDETAILEDLIST = "BAPISDORDER_GETDETAILEDLIST";

		/** RFC constant. */
		public final static String DDIF_FIELDINFO_GET = "DDIF_FIELDINFO_GET";

		/** RFC constant. */
		public final static String IAC_STANDARD_SEARCH = "IAC_STANDARD_SEARCH";

		/** RFC constant. */
		public final static String ISA_ADDRESS_TAX_JUR_TABLE = "ISA_ADDRESS_TAX_JUR_TABLE";

		/** RFC constant. */
		public final static String ISA_ADDR_POSTAL_CODE_CHECK = "ISA_ADDR_POSTAL_CODE_CHECK";

		/** RFC constant. */
		public final static String ISA_CHANGE_CONTACT_PERSON = "ISA_CHANGE_CONTACT_PERSON";

		/** RFC constant. */
		public final static String ISA_CONTACT_PERSON_FROM_EMAIL = "ISA_CONTACT_PERSON_FROM_EMAIL";

		/** RFC constant. */
		public final static String ISA_CREDITCARD_CHECK = "ISA_CREDITCARD_CHECK";

		/** RFC constant. */
		public final static String ISA_DECIMAL_PLACES_CURRENCIES = "ISA_DECIMAL_PLACES_CURRENCIES";

		/** RFC constant. */
		public final static String ISA_FIND_PRICING_PROCEDURE = "ISA_FIND_PRICING_PROCEDURE";

		/** RFC constant. */
		public final static String ISA_GET_CONFIG_INDIC_FOR_MAT = "ISA_GET_CONFIG_INDIC_FOR_MAT";

		/** RFC constant. */
		public final static String ISA_GET_COUNTRIES_PER_SCHEME = "ISA_GET_COUNTRIES_PER_SCHEME";

		/** RFC constant. */
		public final static String ISA_GETPMNTTRMS_HELP = "ISA_GETPMNTTRMS_HELP";

		/** RFC constant. */
		public final static String ISA_GET_REGION_LIST = "ISA_GET_REGION_LIST";

		/** RFC constant. */
		public final static String ISA_GET_TITLE_LIST = "ISA_GET_TITLE_LIST";

		/** RFC constant. */
		public final static String ISA_INTERNET_USER = "ISA_INTERNET_USER";

		/** RFC constant. */
		public final static String ISA_LOCK_SALESDOCUMENTS = "ISA_LOCK_SALESDOCUMENTS";

		/** RFC constant. */
		public final static String ISA_SALES_DOCUMENTS_READ = "ISA_SALES_DOCUMENTS_READ";

		/** RFC constant. */
		public final static String ISA_SALESORDER_SIMULATE_TOTAL = "ISA_SALESORDER_SIMULATE_TOTAL";

		/** RFC constant. */
		public final static String ISA_SHIPTOS_OF_SOLDTO_GET = "ISA_SHIPTOS_OF_SOLDTO_GET";

		/** RFC constant. */
		public final static String ISA_UNLOCK_SALESDOCUMENTS = "ISA_UNLOCK_SALESDOCUMENTS";

		/** RFC constant. */
		public final static String ISA_USER_APPLICATION_OBJ_GET = "ISA_USER_APPLICATION_OBJ_GET";

		/** RFC constant. */
		public final static String RFC_SYSTEM_INFO = "RFC_SYSTEM_INFO";

		/** RFC constant. */
		public final static String SD_SALESDOCUMENT_CHANGE = "SD_SALESDOCUMENT_CHANGE";

		/** RFC constant. */
		public final static String SD_SALESDOCUMENT_CREATE = "SD_SALESDOCUMENT_CREATE";

		/** RFC constant. */
		public final static String SUSR_LOGIN_CHECK_RFC = "SUSR_LOGIN_CHECK_RFC";

		/** RFC constant. */
		public final static String SUSR_USER_CHANGE_PASSWORD_RFC = "SUSR_USER_CHANGE_PASSWORD_RFC";

		/** RFC constant. */
		public final static String WWW_USER_AUTHORITY = "WWW_USER_AUTHORITY";

		/** RFC constant. */
		public final static String ERP_ISA_REGISTER_USER = "ERP_ISA_REGISTER_USER";
	}

	/**
	 * Interface that holds some RFC fields that are used within the ISA R/3 application.
	 * 
	 */
	public static interface RfcField
	{


		/** RFC constant. */
		public final static String SUBTOT_PP = "SUBTOT_PP";

		/** RFC constant. */
		public final static String SUBTOTAL_ = "SUBTOTAL_";


		/** RFC constant. */
		public final static String VBELN = "VBELN";

		/** RFC constant. */
		public final static String DOC_TYPE = "DOC_TYPE";

		/** RFC constant. */
		public final static String SALES_ORG = "SALES_ORG";

		/** RFC constant. */
		public final static String DISTR_CHAN = "DISTR_CHAN";

		/** RFC constant. */
		public final static String DIVISION = "DIVISION";

		/** RFC constant. */
		public final static String RECORD_DATE = "REC_DATE";

		/** RFC constant. */
		public final static String CURRENCY = "CURRENCY";

		/** RFC constant. */
		public final static String DESCRIPTION = "PURCH_NO";

		//partner

		/** RFC constant. */
		public final static String PARTN_ROLE = "PARTN_ROLE";

		/** RFC constant. */
		public final static String PARTN_NUMB = "PARTN_NUMB";

		/** RFC constant. */
		public final static String ITM_NUMBER = "ITM_NUMBER";

		/** RFC constant. */
		public final static String CUSTOMER = "CUSTOMER";

		//item and schedule line

		/** RFC constant. */
		public final static String SHORT_TEXT = "SHORT_TEXT";

		/** RFC constant. */
		public final static String MATERIAL = "MATERIAL";

		/** RFC constant. */
		public final static String HG_LV_ITEM = "HG_LV_ITEM";

		/** RFC constant. */
		public final static String TARGET_QTY = "TARGET_QTY";

		/** RFC constant. */
		public final static String TARGET_QU = "TARGET_QU";

		/** RFC constant. */
		public final static String REQ_DATE = "REQ_DATE";

		/** RFC constant. */
		public final static String CONFIR_QTY = "CONFIR_QTY";

		/** RFC constant. */
		public final static String SCHED_LINE = "SCHED_LINE";

		/** RFC constant. */
		public final static String REQ_QTY = "REQ_QTY";

		/** RFC constant. */
		public final static String NET_PRICE = "NET_PRICE";

		/** RFC constant. */
		public final static String NET_VALUE = "NET_VALUE";

		/** RFC constant. */
		public final static String COND_P_UNT = "COND_P_UNT";

		/** RFC constant. */
		public final static String COND_UNIT = "COND_UNIT";

		/** RFC constant. */
		public final static String PO_ITM_NO = "PO_ITM_NO";

		/** RFC constant. */
		public final static String SALES_UNIT = "SALES_UNIT";

		/** RFC constant. */
		public final static String UPDATEFLAG = "UPDATEFLAG";

		//fields for catalog handling

		/** RFC constant. */
		public final static String ITEM = "ITEM";

		/** RFC constant. */
		public final static String NAME = "NAME";

		/** RFC constant. */
		public final static String AREA = "AREA";

		/** RFC constant. */
		public final static String DAPPL1 = "DAPPL1";

		/** RFC constant. */
		public final static String DAPPL = "DAPPL";
	}

	/**
	 * Interface that holds some table or structure names that are used within the ISA R/3 application.
	 * 
	 * 
	 */
	public static interface RfcStructure
	{

		/** RFC constant. */
		public static String ORDER_HEADER_IN = "ORDER_HEADER_IN";

		/** RFC constant. */
		public static String ORDER_ITEMS_IN = "ORDER_ITEMS_IN";

		/** RFC constant. */
		public static String ORDER_SCHEDULES_IN = "ORDER_SCHEDULES_IN";

		/** RFC constant. */
		public static String ORDER_PARTNERS = "ORDER_PARTNERS";

		/** RFC constant. */
		public static String I_BAPI_VIEW = "I_BAPI_VIEW";

		/** RFC constant. */
		public static String SALES_DOCUMENTS = "SALES_DOCUMENTS";

		/** RFC constant. */
		public static String ORDER_HEADERS_OUT = "ORDER_HEADERS_OUT";

		/** RFC constant. */
		public static String ORDER_ITEMS_OUT = "ORDER_ITEMS_OUT";

		/** RFC constant. */
		public static String ORDER_SCHEDULES_OUT = "ORDER_SCHEDULES_OUT";

		/** RFC constant. */
		public static String ORDER_BUSINESS_OUT = "ORDER_BUSINESS_OUT ";

		/** RFC constant. */
		public static String ORDER_PARTNERS_OUT = "ORDER_PARTNERS_OUT";

		/** RFC constant. */
		public static String ORDER_ADDRESS_OUT = "ORDER_ADDRESS_OUT";

		/** RFC constant. */
		public static String ORDER_STATUSHEADERS_OUT = "ORDER_STATUSHEADERS_OUT";

		/** RFC constant. */
		public static String ORDER_STATUSITEMS_OUT = "ORDER_STATUSITEMS_OUT";

		/** RFC constant. */
		public static String ORDER_CONDITIONS_OUT = "ORDER_CONDITIONS_OUT";
	}
}