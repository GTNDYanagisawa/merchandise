//CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.util.config.ConfigIntf;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Set;

import org.apache.log4j.Logger;


public class SystemInfoTest extends ServicelayerTransactionalTest
{
	private static final Logger LOG = Logger.getLogger(SystemInfoTest.class);

	private static final String line = "===================================================";
	private static final String line2 = "---------------------------------------------------";
	private static final String nl = "\n";


	//	@Test
	public void printSystemInfoAttributeMap()
	{
		final String title = "SYSTEM INFO : ATTRIBUTES MAP";
		LOG.info(line);
		LOG.info(title);
		LOG.info(line);
		System.out.println(line);
		System.out.println(title);
		System.out.println(line);

		//		Registry.activateMasterTenant();
		final Tenant currentTenant = Registry.getCurrentTenant();
		final Tenant t = this.jaloSession.getTenant();

		printTenant(t, "jaloSession.getTenant", "END");
		printTenant(currentTenant, "CURRENT TENANT", "END");

		printConfigIntf(t);

		final Locale loc = t.getTenantSpecificLocale();

		print(line + nl + "LOCALE" + nl + nl);
		printLocale(loc);

		print(line + nl + "AVAILABLE LOCALES" + nl + nl);
		printLocaleAvailableLocales(loc);

		print(line + nl + "DEFAULT LOCALE" + nl + nl);
		printDefaultLocale(loc.getDefault());

		print(line + nl + "ISO COUNTRIES" + nl + nl);
		printISOCountries(loc.getISOCountries());

		print(line + nl + "ISO LANGUAGES" + nl + nl);
		printISOLanguages(loc.getISOLanguages());


		final JaloSession session = this.jaloSession;

		print(line + nl + "ATTRIBUTE NAME" + nl + nl);
		final Enumeration e = session.getAttributeNames();
		while (e.hasMoreElements())
		{
			final Object object = e.nextElement();
			System.out.println(object);
			LOG.info(object);

		}

		print(line + nl + "ATTRIBUTES" + nl + nl);
		final Map<String, Object> attributes = session.getAttributes();
		for (final Map.Entry<String, Object> entry : attributes.entrySet())
		{
			final String key = entry.getKey() != null ? entry.getKey() : "KEY IS NULL";
			final Object val = entry.getValue() != null ? entry.getValue() : "VALUE IS NULL";
			final String msg = "KEY = " + key + " >> VALUE = " + val;
			LOG.info(msg);
			System.out.println(msg);
		}

		LOG.info(line);
		System.out.println(line);
	}


	protected void printISOCountries(final String[] isoCountries)
	{
		printHeader("printISOCountries", "ISO COUNTRIES");
		for (int i = 0; i < isoCountries.length; i++)
		{
			final String isoCountry = isoCountries[i];
			final String msg = "ISO COUNTRY = " + isoCountry != null ? isoCountry : "ISO COUNTRY ARRAY FIELD IS NULL";
			LOG.info(msg);
		}
		printTail("printISOCountries", "END");
	}

	protected void printISOLanguages(final String[] isoLanguages)
	{
		printHeader("printISOLanguages", "ISO LANGUAGES");
		for (int i = 0; i < isoLanguages.length; i++)
		{
			final String isoLanguage = isoLanguages[i];
			final String msg = "ISO LANGUAGE = " + isoLanguage != null ? isoLanguage : "ISO LANGUAGE ARRAY FIELD IS NULL";
			LOG.info(msg);
		}
		printTail("printISOLanguages", "END");
	}

	protected void printLocaleAvailableLocales(final Locale l)
	{
		printHeader("printLocaleAvailableLocales", "AVAILABLE LOCALES");
		final Locale[] availableLocales = l.getAvailableLocales();
		for (int i = 0; i < availableLocales.length; i++)
		{
			print(line2);
			final Locale loc = availableLocales[i];
			printLocale(loc);
		}
		printTail("printLocaleAvailableLocales", "END");
	}

	protected void printDefaultLocale(final Locale l)
	{
		printHeader("printDefaultLocale", "DEFAULT LOCALE");
		printLocale(l);
		printTail("printDefaultLocale", "END");
	}

	protected void print(final String message)
	{
		LOG.info(line2 + nl + message + nl + line2 + nl);
	}

	protected void printHeader(final String methodName, final String message)
	{
		LOG.info(line + nl + "# [" + methodName + "] " + message + nl + line2);
	}

	protected void printTail(final String methodName, final String message)
	{
		LOG.info("# [" + methodName + "] " + message + nl + line);
	}

	protected void printLocale(final Locale l)
	{
		final String country = l.getCountry();
		final String displayCountry = l.getDisplayCountry();
		final String displayLanguage = l.getDisplayLanguage();
		final String displayName = l.getDisplayName();

		String ISO3Country;
		String ISO3Language;
		try
		{
			ISO3Country = l.getISO3Country();
			ISO3Language = l.getISO3Language();
		}
		catch (final MissingResourceException e)
		{
			ISO3Country = "missing resource";
			ISO3Language = "missing resource";
		}

		final String language = l.getLanguage();

		final String msg1 = "LOCALE INFOS FOR LOCALE = " + l;
		final String msg2 = "COUNTRY = " + country;
		final String msg3 = "DISPLAYCOUNTRY = " + displayCountry;
		final String msg4 = "DISPLAY LANGUAGE =" + displayLanguage;
		final String msg5 = "DISPLAY NAME = " + displayName;
		final String msg6 = "ISO3 COUNTRY = " + ISO3Country;
		final String msg7 = "ISO3 LANGUAGE = " + ISO3Language;
		final String msg8 = "LANGUAGE = " + language;

		final String msg = msg1 + nl + msg2 + nl + msg3 + nl + msg4 + nl + msg5 + nl + msg6 + nl + msg7 + nl + msg8;
		System.out.println(msg);
		LOG.info(msg);
	}

	protected void printConfigIntf(final Tenant t)
	{
		final ConfigIntf c = t.getConfig();
		final Map<String, String> allParameters = c.getAllParameters();
		final Set<Entry<String, String>> allParamtersSet = allParameters.entrySet();
		for (final Entry<String, String> entry : allParamtersSet)
		{
			final String key = "KEY = " + entry.getKey() != null ? entry.getKey() : "KEY IS NULL";
			final String val = "VALUE = " + entry.getValue() != null ? entry.getValue() : "VALUE IS NULL";
			final String msg = key + " " + val;
			System.out.println(msg);
			LOG.info(msg);
		}

	}

	protected void printTenant(final Tenant t, final String message, final String message2)
	{
		printHeader("printTenant", message);

		System.out.println(t);
		LOG.info(t);

		final String tenantId = t.getTenantID();
		final String msgTenantId = "TENANT ID = " + tenantId;
		System.out.println(msgTenantId);
		LOG.info(msgTenantId);

		printTail("printTenant", message2);
	}

}
