package de.hybris.platform.sap.sapcarintegration.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import de.hybris.platform.sap.sapcarintegration.constants.SapcarintegrationConstants;

public class DateUtil {

	private static final Logger LOG = Logger.getLogger(DateUtil.class);

	private static final String DEFAULT_DATE_TIME_PATTERN = "yyyymmddHHmmss";
	
	 private static String DEFAULT_DATE_PATTERN = "yyyymmdd";
	 private static String DEFAULT_TIME_PATTERN = "HHmmss";
	
	/**
	 * Convenience method to convert date to String
	 * @param date
	 * @return formatted String
	 */
	public static final String formatDate(Date date) {
		
		if (LOG.isDebugEnabled()) {
        	LOG.debug("converting '" + date + "' to String with mask '"
                      + DEFAULT_DATE_PATTERN + "'");
        }

		SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
		return df.format(date);
	}
     
	
	
	/**
	 * Convenience method to convert String to Date
     * in the format you specify on input
	 * @param strDate
	 * @return
	 */
    public static final Date formatDate(String strDate) {
    	
       
    	if (strDate == null || StringUtils.isEmpty(strDate)) {
    		strDate = SapcarintegrationConstants.DEFAULT_DATE;
		}
    	
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_PATTERN);

        if (LOG.isDebugEnabled()) {
        	LOG.debug("converting '" + strDate + "' to date with mask '"
                      + DEFAULT_DATE_PATTERN + "'");
        }

        Date date = null;
        try {
        	date = df.parse(strDate);
        } catch (ParseException pe) {
        	LOG.error("error while parsing date " + pe.getMessage(), pe);
        }

        return (date);
    }
    
    
    /**
     
     * @param strDate
     * @return
     */
    public static final Date formatDateTime(String strDate) {
    	
        
    	if (strDate == null || StringUtils.isEmpty(strDate)) {
    		strDate = SapcarintegrationConstants.DEFAULT_DATE_TIME;
		}
    	
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_TIME_PATTERN);

        if (LOG.isDebugEnabled()) {
        	LOG.debug("converting '" + strDate + "' to date with mask '"
                      + DEFAULT_DATE_PATTERN + "'");
        }

        Date date = null;
        try {
        	date = df.parse(strDate);
        } catch (ParseException pe) {
        	LOG.error("error while parsing date " + pe.getMessage(), pe);
        }

        return (date);
    }
    
    
    
    /**
	 * Convenience method to convert String to Date
     * in the format you specify on input
	 * @param strTime
	 * @return
	 */
    public static final Date formatTime(String strTime) {
    	
    	if (strTime == null || StringUtils.isEmpty(strTime)) {
			strTime = SapcarintegrationConstants.DEFAULT_TIME;
		}
       
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_TIME_PATTERN);

        if (LOG.isDebugEnabled()) {
        	LOG.debug("converting '" + strTime + "' to date with mask '"
                      + DEFAULT_TIME_PATTERN + "'");
        }

        Date date = null;
        try {
        	date = df.parse(strTime);
        } catch (ParseException pe) {
        	LOG.error("error while parsing date " + pe.getMessage(), pe);
        }

        return (date);
    }
	
}
