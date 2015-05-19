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
package de.hybris.platform.sap.sapcommonbol.common.backendobject.impl;

import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.bol.cache.CacheAccess;
import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;
import de.hybris.platform.sap.sapcommonbol.common.backendobject.interf.ConverterBackend;
import de.hybris.platform.sap.sapcommonbol.constants.SapcommonbolConstants;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionTools;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import com.sap.tc.logging.Severity;

/**
 * Converter backend implementation. Performs calls to ERP and CRM and caches
 * results. <br>
 * Note this is a preliminary implementation. At the moment it is only used to
 * retrieve currency customizing.<br>
 *
 * @version 0.9
 */
public abstract class ConverterBackendERPCRM extends
		BackendBusinessObjectBaseJCo implements ConverterBackend {

	protected static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(ConverterBackendERPCRM.class.getName());
	private static final String CACHEKEY_CURRENCIES = "SAP_CURRENCIES";
	private static final String CACHEKEY_UNITS = "SAP_UNITS";

	@Resource(name = SapcommonbolConstants.BEAN_ID_CACHE_CURRENCIES)
	protected CacheAccess currencyCacheAccess;

	@Resource(name = SapcommonbolConstants.BEAN_ID_CACHE_UNITS)
	protected CacheAccess unitCacheAccess;

	/**
	 * Default constructor
	 */
	public ConverterBackendERPCRM() {
	}

	protected final static class UomLocalization {

		private final Map<String, Integer> numberOfDecimalsForUOMs;
		private final Map<String, String> internalToExternalUom;
		private final Map<String, String> externalToInternalUom;
		private final Map<String, String> internalToExternalDescriptionsUom;

		public UomLocalization(
				final Map<String, Integer> numberOfDecimalsForUOMs,
				final Map<String, String> internalToExternalUom,
				final Map<String, String> externalToInternalUom,
				final Map<String, String> internalToExternalDescriptionsUom) {
			this.numberOfDecimalsForUOMs = numberOfDecimalsForUOMs;
			this.internalToExternalUom = internalToExternalUom;
			this.externalToInternalUom = externalToInternalUom;
			this.internalToExternalDescriptionsUom = internalToExternalDescriptionsUom;
		}

		public String getExternalUom(final String internalUom) {
			return internalToExternalUom.get(internalUom);
		}

		public String getInternalUom(final String externalUom) {
			return externalToInternalUom.get(externalUom);
		}

		public String getDescription(final String internalUom) {
			return internalToExternalDescriptionsUom.get(internalUom);
		}

		public Integer getNumberOfDecimals(final String internalUom) {
			return numberOfDecimalsForUOMs.get(internalUom);
		}

		@Override
		public String toString() {
			final StringBuilder output = new StringBuilder();
			final Set<Entry<String, Integer>> entrySet = numberOfDecimalsForUOMs
					.entrySet();
			for (final Entry<String, Integer> entry : entrySet) {
				output.append(entry.getKey() + ": " + entry.getValue() + "\n");
			}
			return output.toString();
		}

	}

	protected final static class CurrencyLocalization {
		private final Map<String, String> currencyDescriptions;

		private final Map<String, Integer> numberOfDecimalsForCurrencies;

		public CurrencyLocalization(
				final Map<String, Integer> numberOfDecimalsForCurrencies,
				final Map<String, String> currencyDescriptions) {
			this.numberOfDecimalsForCurrencies = numberOfDecimalsForCurrencies;
			this.currencyDescriptions = currencyDescriptions;
		}

		public String getDescription(final String currencyCode) { // SAP
																	// currency
																	// code
			return currencyDescriptions.get(currencyCode);
		}

		// SAP currency code
		public Integer getNumberOfDecimals(final String currencyCode) {
			return numberOfDecimalsForCurrencies.get(currencyCode);
		}

		@Override
		public String toString() {
			final StringBuilder output = new StringBuilder();
			final Set<Entry<String, Integer>> entrySet = numberOfDecimalsForCurrencies
					.entrySet();
			for (final Entry<String, Integer> entry : entrySet) {
				output.append(entry.getKey() + ": " + entry.getValue() + "\n");
			}
			return output.toString();
		}

	}

	@Override
	abstract public Object loadUOMsByLanguageFromBackend(String applicationID,
			String language) throws BackendException;

	@Override
	abstract public Object loadCurrenciesByLanguageFromBackend(
			String applicationID, String language) throws BackendException;

	private CurrencyLocalization getCurrencyLocalization(final String language)
			throws BackendException {

		synchronized (ConverterBackendERPCRM.class) {
			CurrencyLocalization currencies = null;
			final String rfcCacheKey = CACHEKEY_CURRENCIES
					+ LocaleUtil.getLocale().getLanguage();

			currencies = (CurrencyLocalization) currencyCacheAccess
					.get(rfcCacheKey);
			if (currencies == null) {
				try {
					currencies = (CurrencyLocalization) loadCurrenciesByLanguageFromBackend(
							"ID", language);
					currencyCacheAccess.put(rfcCacheKey, currencies);
					if (sapLogger.isDebugEnabled()) {

						sapLogger.debug("loaded from backend currencies"
								+ currencies);
					}
				} catch (final SAPHybrisCacheException e) {
					throw new BackendRuntimeException(
							"Issue during cache access.");
				}
			}
			return currencies;
		}

	}

	@SuppressWarnings("unused")
	@Deprecated
	private CurrencyLocalization getNonNullCurrLocalization(
			final String language) throws BackendException {
		try {
			final CurrencyLocalization li = getCurrencyLocalization(language);
			if (li == null) {
				sapLogger.log(Severity.ERROR, LogCategories.APPLICATIONS,
						"The language {0} could not be found in the pricing converter data."
								+ new Object[] { language });
				throw new BackendException("The language " + language
						+ " could not be found in the pricing converter data.");
			}
			return li;
		} catch (final BackendException e) {
			// no need to log here as just forwarding
			throw new BackendException(
					"Could not initialze pricing converter data for language: ."
							+ language, e);
		}
	}

	private String getSAPLanguage() {
		final String isoLanguage = LocaleUtil.getLocale().getLanguage();
		final String sapLanguage = ConversionTools
				.getR3LanguageCode(isoLanguage);
		return sapLanguage;
	}

	private UomLocalization getUomLocalisation(final String sapLanguage)
			throws BackendException {
		synchronized (ConverterBackendERPCRM.class) {
			UomLocalization units = null;
			final String rfcCacheKey = CACHEKEY_UNITS + sapLanguage;

			units = (UomLocalization) unitCacheAccess.get(rfcCacheKey);
			if (units == null) {
				try {
					units = (UomLocalization) loadUOMsByLanguageFromBackend(
							"ID", sapLanguage);
					unitCacheAccess.put(rfcCacheKey, units);
					if (sapLogger.isDebugEnabled()) {
						sapLogger.debug("loaded from backend units " + units);
					}
				} catch (final SAPHybrisCacheException e) {
					throw new BackendRuntimeException(
							"Issue during cache access.");
				}
			}
			return units;
		}
	}

	@Override
	public String convertUnitID2UnitKey(final String unitID)
			throws BackendException {
		final UomLocalization uomLocalization = getUomLocalisation(getSAPLanguage());
		return (uomLocalization != null) ? uomLocalization
				.getInternalUom(unitID) : "";
	}

	@Override
	public String convertUnitKey2UnitID(final String unitKey)
			throws BackendException {
		final UomLocalization uomLocalization = getUomLocalisation(getSAPLanguage());
		return (uomLocalization != null) ? uomLocalization
				.getExternalUom(unitKey) : "";
	}

	@Override
	public int getCurrencyScale(final String sapCurrencyCode)
			throws BackendException {
		final CurrencyLocalization li = getCurrencyLocalization(getSAPLanguage());

		if (li == null) {
			throw new BackendException(
					"Issue with cache loader: No CurrencyLocalization for: "
							+ sapCurrencyCode + ", " + getSAPLanguage());
		}

		Integer result = null;
		int intResult = 0;
		if (li.numberOfDecimalsForCurrencies != null) {
			result = li.numberOfDecimalsForCurrencies.get(sapCurrencyCode);
			if (result == null) {
				final StringBuffer content = new StringBuffer();
				for (final Entry<String, Integer> entry : li.numberOfDecimalsForCurrencies
						.entrySet()) {
					content.append("(key=").append(entry.getKey())
							.append(" value=").append(entry.getValue())
							.append("), ");
				}
				sapLogger.debug("NumberOfDecimalsForCurrency: "
						+ li.numberOfDecimalsForCurrencies.keySet());

				throw new ApplicationBaseRuntimeException(
						"No customizing for currency " + sapCurrencyCode
								+ " in HashMap " + content + " available");
			}
			intResult = result.intValue();
		}
		return intResult;
	}

	@Override
	public int getUnitScale(final String unitKey) throws BackendException {
		// if unit key is null or blank: return 0;
		if (unitKey == null || unitKey.trim().isEmpty()) {
			return 0;
		}

		final UomLocalization uomLoc = getUomLocalisation(getSAPLanguage());

		if (uomLoc == null) {
			throw new BackendException(
					"Issue with cache loader: No UomLocalization for: "
							+ unitKey + ", " + getSAPLanguage());
		}

		Integer result = null;
		int intResult = 0;
		if (uomLoc.numberOfDecimalsForUOMs != null) {
			result = uomLoc.numberOfDecimalsForUOMs.get(unitKey);

			if (result == null) {
				throw new BackendException("No customizing for unit key "
						+ unitKey + " available");
			}
			intResult = result.intValue();
		}
		return intResult;
	}
}
