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

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.sap.conn.jco.ConversionException;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionHelper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LoadOperation;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.CustomizingHelper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.GetAllReadParameters;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Responsible to map header attributes from the LO-API response to the header
 * BO representation. Used for reading and writing data to ERP.
 */
public class HeaderMapper extends BaseMapper {

	/**
	 * LO-API ID of header segment
	 */
	public static final String OBJECT_ID_HEAD = "HEAD";
	/**
	 * Logging instance
	 */
	public static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(HeaderMapper.class.getName());

	/**
	 * Factory to access SAP session beans
	 */
	protected GenericFactory genericFactory = null;
	/**
	 * Converter BO
	 */
	protected Converter converter;
	CommonI18NService commonI18NService;

	/**
	 * @param converter
	 */
	public void setConverter(final Converter converter) {
		this.converter = converter;
	}

	/**
	 * Injected generic factory.
	 * 
	 * @param genericFactory
	 */
	public void setGenericFactory(final GenericFactory genericFactory) {
		this.genericFactory = genericFactory;
	}

	@Override
	public void init() {
		/* nothing to initialize */
	}

	/**
	 * Fill header BOL attributes from the LO-API response
	 * 
	 * @param esHeadComV
	 *            JCO structure
	 * @param esHeadComR
	 *            JCO structure
	 * @param ttHeadCondV
	 *            Header condition table
	 * @param backendState
	 *            Contains backend stateful attributes
	 * @param shop
	 *            Configuration
	 * @param readParams
	 *            Read parameters
	 * @param headerPriceAttribs
	 * @param itemsPropMap
	 * @param headerPropMap
	 * @param salesDocument
	 *            BOL cart or order
	 * @param header
	 *            BOL header of cart or order
	 */
	public void read(final JCoStructure esHeadComV, //
			final JCoStructure esHeadComR, //
			final JCoTable ttHeadCondV, //
			final BackendState backendState, //
			final TransactionConfiguration shop, //
			final GetAllReadParameters readParams, //
			final Map<String, String> headerPriceAttribs, //
			final Map<String, String> itemsPropMap, //
			final Map<String, String> headerPropMap, //
			final SalesDocument salesDocument, //
			final Header header) {

		readEsHeadComV(esHeadComV, header, readParams.setIpcPriceAttributes,
				readParams.shippingConditionsAsText, salesDocument,
				backendState, esHeadComR);

		// changeability
		header.setChangeable(backendState.getLoadState().isChangeable());

		readEsHeadComR(esHeadComR, header, readParams, headerPropMap,
				headerPriceAttribs, itemsPropMap, shop);
		readTtHeadCondV(ttHeadCondV, esHeadComR, header,
				readParams.headerCondTypeFreight);

		// calculate "Net Value without Freight" and
		// "Gross Value without Freight"
		calculateValuesWOFreight(header);
	}

	/**
	 * Handle output fields of structure ES_HEAD_COMV
	 * 
	 * @param esHeadComV
	 * @param header
	 * @param setIpcPriceAttributes
	 * @param shippingConditionAsText
	 * @param businessObjectInterface
	 * @param baseR3Lrd
	 * @param esHeadComR
	 */
	protected void readEsHeadComV(final JCoStructure esHeadComV, //
			final Header header, //
			final boolean setIpcPriceAttributes, //
			final boolean shippingConditionAsText, //
			final SalesDocument businessObjectInterface, //
			final BackendState baseR3Lrd, //
			final JCoStructure esHeadComR) {

		// Do not change tech key. Instead use handle attribute of header
		// header.setTechKey(new TechKey(JCoHelper.getString(esHeadComV,
		// "HANDLE")));

		if (sapLogger.isDebugEnabled()) {
			final StringBuffer debugOutput = new StringBuffer(75);
			debugOutput.append("handleEsHeadComV, header parameter: ");
			debugOutput.append("\n handle         :      "
					+ JCoHelper.getString(esHeadComV,
							ConstantsR3Lrd.FIELD_HANDLE));
			debugOutput.append("\n external ID    : "
					+ JCoHelper.getString(esHeadComV, "BSTKD"));
			debugOutput.append("\n shipCond       :    "
					+ JCoHelper.getString(esHeadComV, "VSBED"));
			debugOutput.append("\n req. deliv date:    "
					+ ConversionHelper.convertDateToLocalizedString(esHeadComV
							.getDate("VDATU")));
			sapLogger.debug(debugOutput);
		}
		header.setHandle(JCoHelper.getString(esHeadComV,
				ConstantsR3Lrd.FIELD_HANDLE));
		header.setProcessType(JCoHelper.getString(esHeadComV, "AUART"));
		header.setSalesDocNumber(JCoHelper.getString(esHeadComV, "VBELN"));
		header.setSalesDocumentsOrigin(""); // ERP only know ERP!
		header.setPurchaseOrderExt(JCoHelper.getString(esHeadComV, "BSTKD"));
		header.setShipCond(JCoHelper.getString(esHeadComV, "VSBED"));

		header.setIncoTerms1(JCoHelper.getString(esHeadComV, "INCO1"));
		header.setIncoTerms2(JCoHelper.getString(esHeadComV, "INCO2"));

		final DocumentType docType = DocumentTypeMapping
				.getDocumentTypeByTransactionGroup(JCoHelper.getString(
						esHeadComR, "TRVOG_R"));
		setValidToDateForQuotations(esHeadComV, header, docType);

		try {
			header.setReqDeliveryDate(esHeadComV.getDate("VDATU"));
			header.setPricingDate(esHeadComV.getDate("PRSDT"));
		} catch (final ConversionException ce) {
			sapLogger.debug("Conversion error for Date", ce);
		}

		if (!shippingConditionAsText) {
			header.setShipCond(JCoHelper.getString(esHeadComV, "VSBED"));
		}

		header.setPaymentTerms(JCoHelper.getString(esHeadComV, "ZTERM"));

		// Copy saved messages from e.g. SET function call
		final MessageList msgList = baseR3Lrd.getMessageList(header
				.getTechKey());
		if ((msgList != null) && (msgList.size() > 0)) {
			for (int j = 0; j < msgList.size(); j++) {
				// Messages are currently only visible for the BO on the UI (not
				// for the Header!)
				((BusinessObject) businessObjectInterface).addMessage(msgList
						.get(j));
				header.addMessage(msgList.get(j));
			}
			msgList.clear(); // Clear the list after the copy
		}

	}

	/**
	 * Sets the valid To date, if the document is a Quotation.<br>
	 * 
	 * @param esHeadComV
	 *            the LRD Header structure
	 * @param header
	 *            the document header
	 * @param docType
	 *            the document Type
	 */
	protected void setValidToDateForQuotations(final JCoStructure esHeadComV,
			final Header header, final DocumentType docType) {
		if (DocumentType.QUOTATION.equals(docType)) {
			Date quotValidToDate = esHeadComV.getDate("BNDDT");
			if (quotValidToDate == null) {
				sapLogger
						.debug("No valid to date returned, set default valid to date");
				// Date still initial, so set to 99991231 to be in synch with
				// document search
				quotValidToDate = RFCConstants.R3_INITIAL_VALID_TO_DATE;
			}
			header.setValidTo(quotValidToDate);
		}
	}

	/**
	 * Handle output fields of structure ES_HEAD_COMR
	 * 
	 * @param esHeadComR
	 * @param header
	 * @param readParams
	 * @param ipcHeadPropMap
	 * @param ipcHeadAttribMap
	 * @param ipcItemPropMap
	 * @param shop
	 */
	protected void readEsHeadComR(final JCoStructure esHeadComR,
			final Header header, final GetAllReadParameters readParams,
			final Map<String, String> ipcHeadPropMap,
			final Map<String, String> ipcHeadAttribMap,
			final Map<String, String> ipcItemPropMap,
			final TransactionConfiguration shop) {

		final DocumentType docType = DocumentTypeMapping
				.getDocumentTypeByTransactionGroup(JCoHelper.getString(
						esHeadComR, "TRVOG_R"));

		if (docType != null) {
			header.setDocumentType(docType);
		} else {
			header.setDocumentType(DocumentType.UNKNOWN);
			// no doctype recognized -log error
			sapLogger
					.debug("No Document type could be determined for transaction group: "
							+ docType);
		}

		header.setProcessTypeDesc(JCoHelper.getString(esHeadComR, "AUART_T"));
		header.setCreatedAt(esHeadComR.getDate("ERDAT_R"));
		setValidFromDateForQuotations(esHeadComR, header, docType);
		header.setChangedAt(esHeadComR.getDate("AEDAT_R"));
		if (readParams.shippingConditionsAsText) {
			header.setShipCond(JCoHelper.getString(esHeadComR, "VSBED_T"));
		}
		header.setPaymentTermsDesc(JCoHelper.getString(esHeadComR, "ZTERM_T"));

		// Amounts
		final String currency = JCoHelper.getString(esHeadComR, "WAERK_R");
		header.setCurrency(currency);
		final int numberOfDecimals = CustomizingHelper.getNumberOfDecimals(
				converter, currency);

		BigDecimal netValue = esHeadComR.getBigDecimal("NETWR_R");
		netValue = ConversionHelper.adjustCurrencyDecimalPoint(netValue,
				numberOfDecimals);
		header.setNetValue(netValue);

		BigDecimal taxValue = esHeadComR.getBigDecimal("MWSBK_R");
		taxValue = ConversionHelper.adjustCurrencyDecimalPoint(taxValue,
				numberOfDecimals);
		header.setTaxValue(taxValue);

		BigDecimal grossValue = esHeadComR.getBigDecimal("ENDBK_R");
		grossValue = ConversionHelper.adjustCurrencyDecimalPoint(grossValue,
				numberOfDecimals);
		header.setGrossValue(grossValue);
	}

	/**
	 * Sets the valid From date, if the document is a Quotation.<br>
	 * 
	 * @param esHeadComR
	 *            the LRD Header read only structure
	 * @param header
	 *            the document header
	 * @param docType
	 *            the document Type
	 */
	protected void setValidFromDateForQuotations(final JCoStructure esHeadComR,
			final Header header, final DocumentType docType) {
		if (DocumentType.QUOTATION.equals(docType)) {
			header.setValidFrom(esHeadComR.getDate("ERDAT_R"));
		}
	}

	/**
	 * Handle output fields of structure TT_HEAD_COND_COMV
	 * 
	 * @param ttHeadCondV
	 * 
	 * @param esHeadComR
	 * @param header
	 * @param headerCondTypeFreight
	 */
	protected void readTtHeadCondV(final JCoTable ttHeadCondV,
			final JCoStructure esHeadComR, final Header header,
			final String headerCondTypeFreight) {

		/* determine Freight Value */
		// BigDecimal.ZERO will avoid show freight value on UI with blank

		if ((headerCondTypeFreight == null) || headerCondTypeFreight.isEmpty()) {
			sapLogger
					.debug("No condition type had been defined to determine the freight value");
			header.setFreightValue(BigDecimal.ZERO);
			return;
		}

		int numRows = 0;
		ttHeadCondV.firstRow();
		while (numRows < ttHeadCondV.getNumRows()) {
			if (headerCondTypeFreight.equals(ttHeadCondV.getString("KSCHL"))) {
				BigDecimal freightValue = ttHeadCondV
						.getBigDecimal("KWERT_INT");
				final String currency = JCoHelper.getString(ttHeadCondV,
						"WAERK");
				final int numberOfDecimals = CustomizingHelper
						.getNumberOfDecimals(converter, currency);
				freightValue = ConversionHelper.adjustCurrencyDecimalPoint(
						freightValue, numberOfDecimals);
				header.setFreightValue(freightValue);
				return;
			}
			ttHeadCondV.nextRow();
			numRows++;
		}

		sapLogger.debug("No freight value found for condition \""
				+ headerCondTypeFreight + "\"");
		header.setFreightValue(BigDecimal.ZERO);
	}

	/**
	 * Calculates Net and Gross values without Freight.
	 * 
	 * @param header
	 *            BOL header of sales document
	 */
	protected void calculateValuesWOFreight(final Header header) {

		header.setNetValueWOFreight(header.getNetValue().subtract(
				header.getFreightValue()));

	}

	/**
	 * Writes header attributes to prepare the LO-API update call
	 * 
	 * @param salesDocHeader
	 * @param salesDocR3Lrd
	 * @param headComV
	 * @param headComX
	 * 
	 * @param config
	 * 
	 */
	protected void write(final Header salesDocHeader,
			final BackendState salesDocR3Lrd, final JCoStructure headComV,
			final JCoStructure headComX, final TransactionConfiguration config) {
		// Handle
		headComV.setValue(ConstantsR3Lrd.FIELD_HANDLE,
				salesDocHeader.getHandle());
		headComX.setValue(ConstantsR3Lrd.FIELD_HANDLE,
				salesDocHeader.getHandle());

		final Date reqDeliveryDate = salesDocHeader.getReqDeliveryDate();
		final String shipCond = salesDocHeader.getShipCond();
		final String currency = commonI18NService.getCurrentCurrency()
				.getSapCode();
		if (sapLogger.isDebugEnabled()) {
			final StringBuffer debugOutput = new StringBuffer(75);
			debugOutput.append("Method fillHeader");
			debugOutput.append("\n ID         :         "
					+ salesDocHeader.getTechKey());
			debugOutput.append("\n handle     :         "
					+ salesDocHeader.getHandle());
			debugOutput.append("\n external ID:         "
					+ salesDocHeader.getPurchaseOrderExt());
			debugOutput.append("\n shipping conditions: " + shipCond);
			debugOutput.append("\n req. delivery date : " + reqDeliveryDate);
			debugOutput.append("\n inco1:               "
					+ salesDocHeader.getIncoTerms1());
			debugOutput.append("\n inco2:               "
					+ salesDocHeader.getIncoTerms2());
			debugOutput.append("\n curr:                " + currency);
			sapLogger.debug(debugOutput);
		}
		headComV.setValue("BSTKD", salesDocHeader.getPurchaseOrderExt());
		headComX.setValue("BSTKD", ConstantsR3Lrd.ABAP_TRUE);

		if (currency != null) {
			headComV.setValue("WAERK", currency);
			headComX.setValue("WAERK", ConstantsR3Lrd.ABAP_TRUE);
		}

		// shipping condition
		// as one shipping condition is always active, we only set it in case
		// something is available.
		// Otherwise defaulting in backend would not work
		if (shipCond != null && !shipCond.isEmpty()) {
			headComV.setValue("VSBED", shipCond);
			headComX.setValue("VSBED", ConstantsR3Lrd.ABAP_TRUE);
		}

		// requested delivery date
		if (reqDeliveryDate != null) {
			headComX.setValue("VDATU", ConstantsR3Lrd.ABAP_TRUE);
			if (reqDeliveryDate.equals(ConstantsR3Lrd.DATE_INITIAL)) {
				final Date initial = null;
				headComV.setValue("VDATU", initial);
			} else {
				headComV.setValue("VDATU", reqDeliveryDate);
			}
		}

		// Incoterms
		if (salesDocHeader.getIncoTerms1() != null) {
			headComV.setValue("INCO1", salesDocHeader.getIncoTerms1());
			headComX.setValue("INCO1", ConstantsR3Lrd.ABAP_TRUE);
		}
		if (salesDocHeader.getIncoTerms2() != null) {
			headComV.setValue("INCO2", salesDocHeader.getIncoTerms2());
			headComX.setValue("INCO2", ConstantsR3Lrd.ABAP_TRUE);
		}

		// Delivery Block
		if (config.getDeliveryBlock() != null) {
			headComV.setValue("LIFSK", config.getDeliveryBlock());
			headComX.setValue("LIFSK", ConstantsR3Lrd.ABAP_TRUE);
		}

		// Only set the field in create mode
		if (salesDocR3Lrd.getLoadState().getLoadOperation()
				.equals(LoadOperation.create)) {
			// Customer Purch Order Type
			if (config.getDeliveryBlock() != null) {
				headComV.setValue("BSARK", config.getCustomerPurchOrderType());
				headComX.setValue("BSARK", ConstantsR3Lrd.ABAP_TRUE);
			}
		}

	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *            the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}

}
