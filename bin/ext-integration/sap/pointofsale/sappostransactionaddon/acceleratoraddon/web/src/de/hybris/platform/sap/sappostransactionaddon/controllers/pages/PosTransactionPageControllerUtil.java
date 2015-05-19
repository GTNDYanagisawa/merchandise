/**
 * 
 */
package de.hybris.platform.sap.sappostransactionaddon.controllers.pages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;


/**
 * 
 */
public class PosTransactionPageControllerUtil
{
	private static final Logger LOG = Logger.getLogger(PosTransactionPageControllerUtil.class);
	private static String DEFAULT_DATE_PATTERN = "yyyymmdd";

	/**
	 * Convenience method to convert String to Datet
	 * 
	 * @param strDate
	 * @return Date object
	 */
	public static final Date formatDate(final String strDate)
	{

		final SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_PATTERN);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("converting '" + strDate + "' to date with mask '" + DEFAULT_DATE_PATTERN + "'");
		}
		Date date = null;
		try
		{
			date = df.parse(strDate);
		}
		catch (final ParseException pe)
		{
			LOG.error("error while parsing date " + pe.getMessage(), pe);
		}

		return date;
	}

}
