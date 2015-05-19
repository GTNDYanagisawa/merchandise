package de.hybris.platform.sap.sappricingbol.converter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Common conversion tools
 * 
 * @author SAP
 */
public class ConversionTools
{

	private static Map<String, String> theLanguageMapISO2ToR3 = new HashMap<String, String>();





	/**
	 * Return the single character r3 language code for a given String.
	 * 
	 * @param langu
	 *           a <code>String</code> value
	 * @return a <code>the language in R/3 format. Case sensitive!</code> value
	 */
	public static String getR3LanguageCode(String langu)
	{
		String lang = CodePageUtils.getSapLangForJavaLanguage(langu);
		if (lang != null)
		{
			return theLanguageMapISO2ToR3.get(lang.toLowerCase(Locale.ENGLISH));
		}
		return null;
	}



	static
	{
		theLanguageMapISO2ToR3.put("en", "E");
		theLanguageMapISO2ToR3.put("de", "D");
		theLanguageMapISO2ToR3.put("fr", "F");
		theLanguageMapISO2ToR3.put("es", "S");
		theLanguageMapISO2ToR3.put("pt", "P");
		theLanguageMapISO2ToR3.put("it", "I");
		theLanguageMapISO2ToR3.put("da", "K");
		theLanguageMapISO2ToR3.put("fi", "U");
		theLanguageMapISO2ToR3.put("nl", "N");
		theLanguageMapISO2ToR3.put("no", "O");
		theLanguageMapISO2ToR3.put("sv", "V");
		// ----------------------- -2
		theLanguageMapISO2ToR3.put("sk", "Q");
		theLanguageMapISO2ToR3.put("cs", "C");
		theLanguageMapISO2ToR3.put("hu", "H");
		theLanguageMapISO2ToR3.put("pl", "L");
		// ----------------------- -5
		theLanguageMapISO2ToR3.put("ru", "R");
		theLanguageMapISO2ToR3.put("bg", "W");
		// ----------------------- -9
		theLanguageMapISO2ToR3.put("tr", "T");
		// ----------------------- -7
		theLanguageMapISO2ToR3.put("el", "G");
		// ----------------------- -8
		theLanguageMapISO2ToR3.put("he", "B");
		// -----------------------
		theLanguageMapISO2ToR3.put("ja", "J");
		theLanguageMapISO2ToR3.put("zf", "M");
		theLanguageMapISO2ToR3.put("zh", "1");
		theLanguageMapISO2ToR3.put("ko", "3");
		// ----missing ISA languages
		theLanguageMapISO2ToR3.put("hr", "6");
		theLanguageMapISO2ToR3.put("sl", "5");
		theLanguageMapISO2ToR3.put("th", "2");

		// ---missing languages, non ISA
		theLanguageMapISO2ToR3.put("sr", "0");
		theLanguageMapISO2ToR3.put("ro", "4");
		theLanguageMapISO2ToR3.put("ms", "7");
		theLanguageMapISO2ToR3.put("uk", "8");
		theLanguageMapISO2ToR3.put("et", "9");
		theLanguageMapISO2ToR3.put("ar", "A");
		theLanguageMapISO2ToR3.put("lt", "X");
		theLanguageMapISO2ToR3.put("lv", "Y");
		theLanguageMapISO2ToR3.put("af", "a");
		theLanguageMapISO2ToR3.put("id", "i");
	}


	/**
	 * Method adds leading zeros to a numeric string based on a specified total length
	 * 
	 * @param inputString
	 *           contains the numeric String
	 * @param desiredLength
	 *           specifies the total desired length of the string
	 * @return String with leading zeroes appended
	 * 
	 */
	public static String addLeadingZerosToNumericID(String inputString, int desiredLength)
	{
		int size = inputString.length();

		//check if inputString is numeric
		for (int i = 0; i < size; i++)
		{
			char ch = inputString.charAt(i);
			if (!Character.isDigit(ch))
			{
				return inputString;
			}
		}
		//if inputString is already the desired length, keep as is
		if (size >= desiredLength)
		{
			return inputString;
		}

		//pad with missing zeroes on the left
		StringBuffer buffer = new StringBuffer(size);
		while (size++ < desiredLength)
		{
			buffer.append("0");
		}
		buffer.append(inputString);
		return buffer.toString();
	}
}
