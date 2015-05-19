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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.backend.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sap.tc.logging.Severity;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionHelper;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.backend.interf.SearchBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchFilter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResultList;

/**
 * 
 */
@BackendType("ERP")
public class SearchBackendERP extends BackendBusinessObjectBaseJCo implements
		SearchBackend {

	/**
	 * Name of search function module
	 */
	public static final String ERP_ISA_GEN_DOCUMENT_SEL = "ERP_ISA_GEN_DOCUMENT_SEL";
	private Integer maxHits;
	private Integer dateRangeInDays;
	private Date today;

	private static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(SearchBackendERP.class.getName());

	@Override
	public int getDateRangeInDays() {
		if (dateRangeInDays == null) {
			dateRangeInDays = Integer
					.valueOf((String) moduleConfigurationAccess
							.getProperty(SapordermgmtbolConstants.CONFIGURATION_PROPERTY_SEARCH_DATE_RANGE));
		}
		return dateRangeInDays.intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search
	 * .backend.interf.SearchBackend#getSearchResult ()
	 */
	@Override
	public SearchResultList getSearchResult(final SearchFilter searchFilter)
			throws BackendException {
		final JCoConnection connection = getDefaultJCoConnection();
		final JCoFunction function = connection
				.getFunction(ERP_ISA_GEN_DOCUMENT_SEL);
		final JCoParameterList tableParameterList = function
				.getTableParameterList();
		fillCounterOptions(tableParameterList.getTable("IT_CTR_OPT"));
		fillSelectionOptions(tableParameterList.getTable("IT_SEL_OPT"),
				searchFilter);
		fillResultFields(tableParameterList.getTable("IT_SEL_FIELDS"));

		connection.execute(function);

		return processResults(tableParameterList.getTable("ET_DOCUMENTS"));

	}

	/**
	 * Processes search result and converts it into the BOL search result
	 * representation
	 * 
	 * @param table
	 *            JCO table of type ISALES_DOCLIST_ROW
	 * @return Search Result List
	 */
	protected SearchResultList processResults(final JCoTable table) {
		final SearchResultList resultList = createSearchResultList();

		if (table.getNumRows() > 0) {
			table.firstRow();
			boolean moreEntries = true;
			while (moreEntries) {
				final SearchResult searchResult = createSearchResult();
				moreEntries = fillSearchResult(searchResult, table);
				resultList.add(searchResult);
			}
		}

		if (sapLogger.isDebugEnabled()) {
			final StringBuilder debugOutput = new StringBuilder(
					"ProcessResults, ");
			debugOutput.append("number of results: " + resultList.size());
			sapLogger.debug(debugOutput);
		}

		performLogging(resultList, getMaxHits());
		return resultList;
	}

	/**
	 * @param resultList
	 * @param maxHits
	 */
	protected void performLogging(final SearchResultList resultList,
			final int maxHits) {
		if (isErrorStatus(resultList, maxHits)) {
			sapLogger.log(Severity.ERROR, LogCategories.APPLICATIONS,
					"Result list size exceeds maximum number of hits");
		} else if (isWarningStatus(resultList, maxHits)) {
			sapLogger
					.log(Severity.WARNING, LogCategories.APPLICATIONS,
							"Result list size exceeds 90% of the maximum number of hits");
		}
	}

	private boolean fillSearchResult(final SearchResult searchResult,
			final JCoTable table) {

		searchResult.setKey(new TechKey(table.getString("VALUE")));
		tryNextRow(table);
		searchResult.setCreationDate(ConversionHelper
				.convertDateStringToDate(table.getString("VALUE")));
		tryNextRow(table);
		searchResult.setOverallStatus(table.getString("VALUE"));
		tryNextRow(table);
		searchResult.setShippingStatus(table.getString("VALUE"));
		tryNextRow(table);
		searchResult.setPurchaseOrderNumber(table.getString("VALUE"));

		return table.nextRow();
	}

	private void tryNextRow(final JCoTable table) {
		if (!table.nextRow()) {
			throw new ApplicationBaseRuntimeException(
					"No search results; search meta data has been provided inconsistently");
		}
	}

	protected SearchResultList createSearchResultList() {
		final SearchResultList resultList = (SearchResultList) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SEARCH_RESULT_LIST);
		return resultList;
	}

	/**
	 * Creates a SearchResult instance
	 * 
	 * @return SearchResult instance
	 */
	protected SearchResult createSearchResult() {
		final SearchResult result = (SearchResult) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SEARCH_RESULT_ENTRY);
		return result;
	}

	/**
	 * Fills search result attributes for the call of function module
	 * {@link SearchBackendERP#ERP_ISA_GEN_DOCUMENT_SEL}
	 * 
	 * @param table
	 *            JCO Table of type ISALES_SEL_FIELD
	 */
	protected void fillResultFields(final JCoTable table) {
		int counter = 1;
		fillKeyAttribute(table, counter++);
		fillCreationDateAttribute(table, counter++);
		fillHeaderStatusAttribute(table, counter++);
		fillShippingStatusAttribute(table, counter++);
		fillPurchaseOrderAttribute(table, counter++);

	}

	private void fillPurchaseOrderAttribute(final JCoTable table, final int i) {
		addNewEntryToResultFieldTable(table, i);
		table.setValue("FIELDNAME", "BSTNK");
	}

	private void fillShippingStatusAttribute(final JCoTable table, final int i) {
		addNewEntryToResultFieldTable(table, i);
		table.setValue("FIELDNAME", "WEC_SHIP_STATUS");
	}

	private void addNewEntryToResultFieldTable(final JCoTable table, final int i) {
		addNewEntryToSelectionTable(table);
		table.setValue("FIELD_INDEX", i);
	}

	private void fillHeaderStatusAttribute(final JCoTable table, final int i) {
		addNewEntryToResultFieldTable(table, i);
		table.setValue("FIELDNAME", "WEC_PROC_STATUS");
	}

	private void fillCreationDateAttribute(final JCoTable table, final int i) {
		addNewEntryToResultFieldTable(table, i);
		table.setValue("FIELDNAME", "ERDAT");
	}

	private void fillKeyAttribute(final JCoTable table, final int counter) {
		addNewEntryToResultFieldTable(table, counter);
		table.setValue("FIELDNAME", "VBELN");
		table.setValue("ATTRIBUTE", "ro");
	}

	/**
	 * Fills selection options for the call of function module
	 * {@link SearchBackendERP#ERP_ISA_GEN_DOCUMENT_SEL}
	 * 
	 * @param table
	 *            JCO Table of type ISALES_SELOPT
	 * @param searchFilter
	 *            Filter attributes
	 */
	protected void fillSelectionOptions(final JCoTable table,
			final SearchFilter searchFilter) {
		if (sapLogger.isDebugEnabled()) {
			sapLogger.debug("Search filter, shipping status: "
					+ searchFilter.getShippingStatus() + ", product ID: "
					+ searchFilter.getProductID());
		}

		final String soldToId = searchFilter.getSoldToId();
		if (soldToId != null) {
			fillSelectSoldTo(table, soldToId);
		} else {
			throw new ApplicationBaseRuntimeException(
					"It was tried to execute a sales order search without sold to id.");
		}
		fillSelectDate(table);
		fillBadiFilter(table);
		final String shippingStatus = searchFilter.getShippingStatus();
		if (shippingStatus != null) {
			fillSelectShippingStatus(table, shippingStatus);
		}
		final String productId = searchFilter.getProductID();
		if (productId != null) {
			fillSelectProductId(table, productId);
		}
	}

	/**
	 * @param table
	 * @param productId
	 */
	private void fillSelectProductId(final JCoTable table,
			final String productId) {
		addNewEntryToSelectionTable(table);
		table.setValue("SELECT_PARAM", "MATNR");
		table.setValue("SIGN", "I");
		table.setValue("OPTION", "EQ");
		table.setValue("LOW", productId);
	}

	private void fillSelectShippingStatus(final JCoTable table,
			final String shippingStatus) {
		addNewEntryToSelectionTable(table);
		table.setValue("SELECT_PARAM", "SHIPSTAT");
		table.setValue("SIGN", "I");
		table.setValue("OPTION", "EQ");
		table.setValue("LOW", shippingStatus);
	}

	private void fillBadiFilter(final JCoTable table) {
		addNewEntryToSelectionTable(table);
		table.setValue("SELECT_PARAM", "LIST_FUNCTION");
		table.setValue("LOW", "WEC_ORDER");
	}

	private void fillSelectDate(final JCoTable table) {
		addNewEntryToSelectionTable(table);
		table.setValue("SELECT_PARAM", "ERDAT");
		table.setValue("SIGN", "I");
		table.setValue("OPTION", "BT");
		table.setValue("LOW", getDateLowAsString());
		table.setValue("HIGH", getDateTodayAsString());
	}

	String getDateLowAsString() {
		final Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(getToday());
		calendar.add(Calendar.DATE, (-1) * getDateRangeInDays());
		final Date resultDate = calendar.getTime();
		return ConversionHelper.convertDateToDateString(resultDate);
	}

	private void fillSelectSoldTo(final JCoTable table, final String soldToId) {
		addNewEntryToSelectionTable(table);
		table.setValue("SELECT_PARAM", "KUNAG");
		table.setValue("SIGN", "I");
		table.setValue("OPTION", "EQ");
		table.setValue("LOW", soldToId);
	}

	private void addNewEntryToSelectionTable(final JCoTable table) {
		table.appendRow();
		table.setValue("HANDLE", 1);
	}

	/**
	 * Fills counter options for the call of function module
	 * {@link SearchBackendERP#ERP_ISA_GEN_DOCUMENT_SEL}
	 * 
	 * @param table
	 *            JCO Table of type ISALES_CTROPT
	 */
	protected void fillCounterOptions(final JCoTable table) {
		addNewEntryToSelectionTable(table);
		table.setValue("MAXHITS", getMaxHits());
	}

	/**
	 * @return the maxHits
	 */
	protected int getMaxHits() {
		if (maxHits == null) {
			maxHits = Integer
					.valueOf((String) moduleConfigurationAccess
							.getProperty(SapordermgmtbolConstants.CONFIGURATION_PROPERTY_SEARCH_MAX_HITS));
		}
		return maxHits.intValue();
	}

	/**
	 * This can be used for testing
	 * 
	 * @param maxHits
	 *            the maxHits to set
	 */
	protected void setMaxHits(final int maxHits) {
		this.maxHits = Integer.valueOf(maxHits);
	}

	protected void setDateRangeInDays(final int dateRange) {
		this.dateRangeInDays = Integer.valueOf(dateRange);

	}

	/**
	 * Only for testing
	 * 
	 * @param date
	 */
	public void setToday(final Date date) {
		this.today = date;
	}

	/**
	 * @return the today
	 */
	public Date getToday() {
		if (today == null) {
			today = new Date(System.currentTimeMillis());
		}
		return today;
	}

	String getDateTodayAsString() {
		return ConversionHelper.convertDateToDateString(getToday());
	}

	protected boolean isWarningStatus(final SearchResultList results,
			final int maxHits) {
		final double threshold = 0.9;
		return exceedsThreshold(results, maxHits, threshold);
	}

	protected boolean isErrorStatus(final SearchResultList results,
			final int maxHits) {
		final double threshold = 1.0;
		return exceedsThreshold(results, maxHits, threshold);
	}

	private boolean exceedsThreshold(final SearchResultList results,
			final int maxHits, final double threshold) {
		if (maxHits <= 0) {
			throw new ApplicationBaseRuntimeException(
					"maxHits needs to be positive");
		}
		final double sizeOfList = results.size();
		final double max = maxHits;
		return (sizeOfList / max > threshold);
	}

}
