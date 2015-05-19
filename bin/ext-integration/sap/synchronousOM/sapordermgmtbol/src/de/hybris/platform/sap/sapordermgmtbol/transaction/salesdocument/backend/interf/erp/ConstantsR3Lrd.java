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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp;

import java.util.Date;

/**
 * Interface containing names of function modules and UI Elements, RFC
 * Constants, field names <br>
 * 
 * @version 1.0
 */
public interface ConstantsR3Lrd {

    /**
     * Represents an initial date for the ERP backend layer.
     */
    public static final Date DATE_INITIAL = new Date(0);

    /**
     * ID of function module ERP_LORD_SET to set sales document data in ERP.
     */
    public static final String FM_LO_API_SET = "ERP_LORD_SET";

    /**
     * ID of function module ERP_LORD_DO_ACTIONS to perform LO-API actions.
     */
    public static final String FM_LO_API_DO_ACTIONS = "ERP_LORD_DO_ACTIONS";

    /**
     * ID of function module ERP_LORD_LOAD which starts a LO-API session in ERP.
     */
    public static final String FM_LO_API_LOAD = "ERP_WEC_ORDER_LOAD";

    /**
     * ID of function module ERP_LORD_SAVE which saves the sales document data
     * in ERP.
     */
    public static final String FM_LO_API_SAVE = "ERP_LORD_SAVE";

    /**
     * ID of function module ERP_LORD_SET_ACTIVE_FIELDS which sets the LORD
     * active fields in ERP.
     */
    public static final String FM_LO_API_SET_ACTIVE_FIELDS = "ERP_LORD_SET_ACTIVE_FIELDS";

    /**
     * ID of function module ERP_LORD_SET_VCFG_ALL which sets data of
     * configurable products in ERP.
     */
    public static final String FM_LO_API_SET_VCFG_ALL = "ERP_LORD_SET_VCFG_ALL";

    /**
     * ID of function module ERP_WEC_GET_VCFG_ALL which gets data of
     * configurable products in ERP.
     */
    public static final String FM_WEC_API_GET_VCFG_ALL = "ERP_WEC_GET_VCFG_ALL";

    /**
     * ID of function module ERP_WEC_ORDER_GET which reads all necessary
     * attributes of a sales document via LO-API.
     */
    public static final String FM_LO_API_WEC_ORDER_GET = "ERP_WEC_ORDER_GET";

    /**
     * ID of function module ERP_LORD_CLOSE which closes a LO-API session
     */
    public static final String FM_LO_API_CLOSE = "ERP_LORD_CLOSE";

    /**
     * ID of function module ERP_LORD_COPY which copies a LO-API document, e.g.
     * when an order is created referring to a quotation
     */
    public static final String FM_LO_API_COPY = "ERP_LORD_COPY";

    /**
     * Ignore message variables for message comparison. This attribute needs to
     * be passed for the first message variable. The contents of the other ones
     * are then ignored.
     */
    public static final String MESSAGE_IGNORE_VARS = "*";

    /* Attributes for dynamic field control */
    /** UI element */
    public static String UI_ELEMENT_PURCHASE_ORDER_EXT = "order.purchaseOrderExt";
    /** UI element */
    public static String UI_ELEMENT_SHIPPING_METHOD = "order.shippingMethod";
    /** UI element */
    public static String UI_ELEMENT_HEADER_TEXT = "order.headerText";
    /** UI element */
    public static String UI_ELEMENT_SHIPPING_COUNTRY = "order.shippingCountry";
    /** UI element */
    public static String UI_ELEMENT_DELIVERY_REGION = "order.deliveryRegion";
    /** UI element */
    public static String UI_ELEMENT_DELIVERY_TYPE = "order.deliveryType";
    /** UI element */
    public static String UI_ELEMENT_DISCOUNTS = "order.rebatesValue";
    /** UI element */
    public static String UI_ELEMENT_HEADER_USERSTATUS = "order.userStatusList";
    /** UI element */
    public static String UI_ELEMENT_REFERENCES = "order.extRefNumbers";
    /** UI element */
    public static String UI_ELEMENT_RECALL_ID = "order.recallId";
    /** UI element */
    public static String UI_ELEMENT_EXT_REF_OBJECTS = "order.extRefObjects";
    /** UI element */
    public static String UI_ELEMENT_ITEM_REQDELIVDATE = "item.reqDeliveryDate";
    /** UI element */
    public static String UI_ELEMENT_ITEM_OVERALLSTATUS = "item.overallStatus";

    // not there in UI control now
    /** UI element */
    public static String UI_ELEMENT_ITEM_DESCRIPTION = "item.description";

    /**
     * UI element: Item text
     */
    public static String UI_ELEMENT_ITEM_TEXT = "item.itemText";
    /** UI element */
    public static String UI_ELEMENT_ITEM_PRODUCT = "item.productID";
    /** UI element */
    public static String UI_ELEMENT_ITEM_PRODUCT_ID = "item.productGUID";

    /** UI element */
    // not there in UI control now
    public static String UI_ELEMENT_ITEM_LATESTDELIVDATE = "item.latestDelivDate"; // not
    /** UI element */
    public static String UI_ELEMENT_ITEM_PAYTERMS = "item.paymentterms";
    /** UI element */
    public static String UI_ELEMENT_ITEM_QTY = "item.quantity";
    /** UI element */
    public static String UI_ELEMENT_ITEM_UNIT = "item.unit";

    /** UI element */
    // not there in UI control now
    public static String UI_ELEMENT_ITEM_DELIVPRIO = "item.deliveryPriority";
    /** UI element */
    public static String UI_ELEMENT_ITEM_DELIVERTO = "item.deliverTo";
    /** UI element */
    public static String UI_ELEMENT_ITEM_CONFIG = "item.configuration";

    /** UI element */
    public static String UI_GROUP_ORDER = "order.header";
    /** UI element */
    public static String UI_GROUP_ITEM = "order.item";

    /** UI element */
    public static String UI_ELEMENT_ITEM_USERSTATUS = "item.userStatusList";

    /** text control constant */
    public static String LF = "\n";
    /** text control constant */
    public static String SEPARATOR = "/";

    /** RFC constant. */
    public static String BAPI_RETURN_ERROR = "E";

    /** RFC constant. */
    public static String BAPI_RETURN_WARNING = "W";

    /** RFC constant. */
    public static String BAPI_RETURN_INFO = "I";

    /** RFC constant. */
    public static String BAPI_RETURN_ABORT = "A";

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

    /**
     * The scenario ID for the LO-API call. Will not be persistet, but can be
     * used to control LO-API processing.
     */
    public static String scenario_LO_API_WEC = "CRM_WEC";

    /** ABAP constant */
    public static String ABAP_TRUE = "X";

    /*
     * A list of commonly used RFC constants, to save memory via reducing the
     * number of static strings
     */
    /** field name */
    public static String FIELD_HANDLE = "HANDLE";

    /**
     * Represents item number in SD
     */
    public static String FIELD_POSNR = "POSNR";
    /** field name */
    public static String FIELD_HANDLE_PARENT = "HANDLE_PARENT";
    /** field name */
    public static String FIELD_OBJECT_ID = "OBJECT_ID";
    /** field name */
    public static String FIELD_ID = "ID";
    /** field name */
    public static String FIELD_SPRAS_ISO = "SPRAS_ISO";
    /** field name */
    public static String FIELD_TEXT_STRING = "TEXT_STRING";
    /** field name */
    public static String FIELD_HANDLE_ITEM = "HANDLE_ITEM";

}
