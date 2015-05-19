/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.bmecat.jalo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.xml.AbstractValueObject;
import de.hybris.bootstrap.xml.ParseAbortException;
import de.hybris.bootstrap.xml.TagListener;
import de.hybris.platform.bmecat.constants.BMECatConstants;
import de.hybris.platform.bmecat.jalo.bmecat2csv.BMECat2CSVObjectProcessor;
import de.hybris.platform.bmecat.parser.BMECatParser;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import de.hybris.platform.util.CSVReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;


public class BMECat2CSVConverterTest extends HybrisJUnit4TransactionalTest
{

	private static final Logger LOG = Logger.getLogger(BMECat2CSVConverterTest.class);
	private InputStream inputStream;

	private MyBMECat2CSVObjectProcessor proc;

	private BMECatParser bmecatparser;

	private File catalog_file;
	private File catalog_version_file;
	private File article_file;
	private File article_ref_file;
	private File keyword_file;
	private File media_file;
	private File category_file;
	private File art_2_cat_file;
	private File customer_file;
	private File address_file;
	private File aggreement_file;
	private File art_prices_file;

	// extends the BMECat2CSVObjectProcessor class because it is necessary to get the temporary csv files
	private class MyBMECat2CSVObjectProcessor extends BMECat2CSVObjectProcessor
	{
		protected MyBMECat2CSVObjectProcessor()
		{
			super();
		}

		protected MyBMECat2CSVObjectProcessor(final String encoding)
		{
			super(encoding, null, null, null, '#', ';', '"', ',');
		}

		protected File getFile(final String filename) throws IOException
		{
			return super.getOut(filename).getFile();
		}

		@Override
		public void process(final TagListener listener, final AbstractValueObject obj) throws ParseAbortException
		{
			super.process(listener, obj);
		}

		// overwritten to keep the temp csv files
		@Override
		public void finish() throws IOException, JaloBusinessException
		{
			super.finish_process();
		}
	}

	@Before
	public void setUp() throws Exception
	{
		// read the test bmecat.xml 
		inputStream = //new BufferedInputStream(new FileInputStream("/bmecat/big_bmecat_test.xml"));
		BMECat2CSVConverterTest.class.getResourceAsStream("/bmecat/big_bmecat_test.xml");
		proc = new MyBMECat2CSVObjectProcessor("windows-1252");
		bmecatparser = new BMECatParser(proc);
		try
		{
			final InputSource isBMECat = new InputSource(inputStream);
			isBMECat.setEncoding("windows-1252");
			bmecatparser.parse(isBMECat);

			catalog_file = proc.getFile(BMECatConstants.BMECat2CSV.CATALOG_OBJECT_FILENAME);
			//TODO to be added, to be tested
			catalog_version_file = proc.getFile(BMECatConstants.BMECat2CSV.CATALOG_VERSION_FILENAME);
			article_file = proc.getFile(BMECatConstants.BMECat2CSV.ARTICLE_OBJECT_FILENAME);
			article_ref_file = proc.getFile(BMECatConstants.BMECat2CSV.ARTICLEREFERENCES_FILENAME);
			keyword_file = proc.getFile(BMECatConstants.BMECat2CSV.KEYWORD_OBJECT_FILENAME);
			media_file = proc.getFile(BMECatConstants.BMECat2CSV.MIME_OBJECT_FILENAME);
			category_file = proc.getFile(BMECatConstants.BMECat2CSV.CATALOGSTRUCTUE_OBJECT_FILENAME);
			art_2_cat_file = proc.getFile(BMECatConstants.BMECat2CSV.ARTICLE2CATALOGGROUP_NEW_RELATION_FILENAME);
			customer_file = proc.getFile(BMECatConstants.BMECat2CSV.COMPANY_FILENAME);
			address_file = proc.getFile(BMECatConstants.BMECat2CSV.ADDRESS_FILENAME);
			aggreement_file = proc.getFile(BMECatConstants.BMECat2CSV.AGREEMENT_FILENAME);
			art_prices_file = proc.getFile(BMECatConstants.BMECat2CSV.ARTICLE2ARTICLEPRICE);
		}
		catch (final Exception e)
		{
			fail(e.getMessage());
		}
		finally
		{
			List<String> csvFiles = new ArrayList<String>();
			csvFiles = proc.getCsvFileNames();
			LOG.info("there are " + csvFiles.size() + " files created.");
			final Iterator<String> itCsvFiles = csvFiles.iterator();
			while (itCsvFiles.hasNext())
			{
				LOG.info("created csv file name --> " + itCsvFiles.next());
			}
			proc.finish();
		}
	}

	@After
	public void tearDown() throws Exception
	{
		// delete temp files
		if (catalog_file != null)
		{
			catalog_file.delete();
		}
		if (article_file != null)
		{
			article_file.delete();
		}
		if (article_ref_file != null)
		{
			article_ref_file.delete();
		}
		if (keyword_file != null)
		{
			keyword_file.delete();
		}
		if (media_file != null)
		{
			media_file.delete();
		}
		if (category_file != null)
		{
			category_file.delete();
		}
		if (art_2_cat_file != null)
		{
			art_2_cat_file.delete();
		}
		if (customer_file != null)
		{
			customer_file.delete();
		}
		if (address_file != null)
		{
			address_file.delete();
		}
		if (aggreement_file != null)
		{
			aggreement_file.delete();
		}
		if (art_prices_file != null)
		{
			art_prices_file.delete();
		}
		proc.deleteTempFiles();
	}

	@Test
	public void testCreatedFiles() throws IOException, JaloBusinessException, ParseException
	{
		//should other files also be tested? seems necessary
		catalogFile();
		catalogVersionFile();
		keywordFile();
		articleFile();
	}

	//test keyword set
	private void keywordFile() throws IOException
	{
		final CSVReader csvreader = new CSVReader(keyword_file, "windows-1252");
		final Set<String> set = new HashSet<String>();
		set.add("a1 1 keyword");
		set.add("a1 2 keyword");
		set.add("global 1 keyword");
		set.add("global 2 keyword");
		set.add("a2 1 keyword");
		set.add("a2 2 keyword");
		set.add("a3 1 keyword");
		set.add("a5 1 keyword");
		set.add("a5 2 keyword");
		set.add("KEYWORD_1_de");
		set.add("KEYWORD_2_de");

		Map<Integer, String> line = new HashMap<Integer, String>();
		final Set<String> keywords = new HashSet<String>();
		boolean keywordEnd = csvreader.readNextLine();
		while (keywordEnd)
		{
			line = csvreader.getLine();
			keywords.add(line.get(Integer.valueOf(0)));
			keywordEnd = csvreader.readNextLine();
		}
		assertEquals(set, keywords);

		csvreader.close();
	}

	private void catalogFile() throws IOException
	{
		final CSVReader csvreader = new CSVReader(catalog_file, "windows-1252");

		Map<Integer, String> line = null;
		final Map<Integer, String> set = new HashMap<Integer, String>();
		set.put(Integer.valueOf(0), "12348s5121");
		set.put(Integer.valueOf(1), "B\u00FCromaterial 2006");
		set.put(Integer.valueOf(2), "DUNS1234_Buyer");
		set.put(Integer.valueOf(3), "DUNS1234_Supplier");

		csvreader.readNextLine();
		line = csvreader.getLine();

		for (int i = 0; i < 4; i++)
		{
			assertEquals(set.get(Integer.valueOf(i)), line.get(Integer.valueOf(i)));
		}

		//only one catalog is allowed in one bmecat.xml
		assertFalse(csvreader.readNextLine());

		csvreader.close();
	}

	private void catalogVersionFile() throws IOException, ParseException
	{
		final CSVReader csvreader = new CSVReader(catalog_version_file, "windows-1252");

		Map<Integer, String> line = null;
		final Map<Integer, String> map = new HashMap<Integer, String>();

		map.put(Integer.valueOf(0), "12348s5121");
		map.put(Integer.valueOf(1), "7.0");
		map.put(Integer.valueOf(2), "");
		map.put(Integer.valueOf(3), "false");
		map.put(Integer.valueOf(4), "true");
		map.put(Integer.valueOf(5), "true");
		map.put(Integer.valueOf(6), "true");
		map.put(Integer.valueOf(7), "EUR");
		//Locale was set to GERMAN
		final TimeZone timeZone = TimeZone.getDefault();
		final SimpleDateFormat sdfTimeZone = new SimpleDateFormat(BMECatConstants.BMECat2CSV.DATETIMETIMEZONE_FORMAT_PATTERN,
				Locale.GERMANY);
		sdfTimeZone.setTimeZone(timeZone);
		final Date date = sdfTimeZone.parse("2000-10-24 20:38:13 +0200");
		final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY);
		map.put(Integer.valueOf(8), sdf.format(date));

		csvreader.readNextLine();
		line = csvreader.getLine();

		for (int i = 0; i < 9; i++)
		{
			assertEquals(map.get(Integer.valueOf(i)), line.get(Integer.valueOf(i)));
		}

		//only one catalog_version is allowed in one bmecat.xml
		assertFalse(csvreader.readNextLine());

		csvreader.close();
	}

	private void articleFile() throws IOException
	{
		final CSVReader csvreader = new CSVReader(article_file, "UTF-8");
		Map<Integer, String> line = null;
		final Map<Integer, String> map = new HashMap<Integer, String>();

		csvreader.readNextLine();
		line = csvreader.getLine();
		map.clear();
		map.put(Integer.valueOf(0), "new");
		map.put(Integer.valueOf(1), "A1");
		map.put(Integer.valueOf(2), "A1 DESCRIPTION_SHORT");
		map.put(Integer.valueOf(3), "A1 DESCRIPTION_LONG");
		map.put(Integer.valueOf(4), "A1 ean");
		map.put(Integer.valueOf(5), "A1 SUPPLIER_ALT_AID");
		map.put(Integer.valueOf(6), "a1 MANUFACTURER_AID");
		map.put(Integer.valueOf(7), "a1 MANUFACTURER_NAME");
		map.put(Integer.valueOf(8), "a1 MANUFACTURER_TYPE_DESCR");
		map.put(Integer.valueOf(9), "23");
		map.put(Integer.valueOf(10), "G67-HHH");
		map.put(Integer.valueOf(11), "0,500");
		compareSet(line.get(Integer.valueOf(12)), "a1 1 keyword,a1 2 keyword,global 1 keyword,global 2 keyword", ",");
		line.remove(Integer.valueOf(12));
		map.put(Integer.valueOf(13), "Senkrecht oder versetzt stapelbar.");
		map.put(Integer.valueOf(14), "Ordnungsmittel");
		map.put(Integer.valueOf(15), "10");
		map.put(Integer.valueOf(16), "pieces");
		map.put(Integer.valueOf(17), "1");
		map.put(Integer.valueOf(18), "1,000");
		map.put(Integer.valueOf(19), "1,000");
		map.put(Integer.valueOf(20), "1");
		map.put(Integer.valueOf(21), "1");
		compareSet(line.get(Integer.valueOf(22)), "global.jpg,global2.jpg,a1normal1.jpg,a1normal2.jpg", ",");
		line.remove(Integer.valueOf(22));
		compareSet(line.get(Integer.valueOf(23)), "a1thumb1.jpg,a1thumb2.jpg", ",");
		line.remove(Integer.valueOf(23));
		compareSet(line.get(Integer.valueOf(24)), "data1.pdf,data2.pdf", ",");
		line.remove(Integer.valueOf(24));
		map.put(Integer.valueOf(25), "");
		compareSet(line.get(Integer.valueOf(26)), "a1logo1.jpg,a1logo2.jpg,a1logo3.jpg", ",");
		line.remove(Integer.valueOf(26));
		map.put(Integer.valueOf(27), "xxx.jpg");
		map.put(Integer.valueOf(28), "");
		compareSet(line.get(Integer.valueOf(29)), "unspecified->buyerid a1 XXX;iln->buyerid a1 iln;duns->buyerid a1 duns", ";");
		line.remove(Integer.valueOf(29));
		map.put(Integer.valueOf(30), "GVVS->keine -- soll nur als Beispiel dienen");
		compareSet(line.get(Integer.valueOf(31)), "new_article->Seit dieser Saison neu;bargain->Dauertiefstpreis", ";");
		line.remove(Integer.valueOf(31));
		compareSet(line.get(Integer.valueOf(32)), "1 PK=2,12 EUR  [01.01.2001,31.07.2022], 1 PK=1,12 EUR", ", ");
		line.remove(Integer.valueOf(32));
		assertEquals(map, line);

		csvreader.readNextLine();
		line = csvreader.getLine();
		map.clear();
		map.put(Integer.valueOf(0), "");
		map.put(Integer.valueOf(1), "A2");
		map.put(Integer.valueOf(2), "A2 DESCRIPTION_SHORT");
		map.put(Integer.valueOf(3), "A2 DESCRIPTION_LONG");
		map.put(Integer.valueOf(4), "A2 ean");
		map.put(Integer.valueOf(5), "A2 SUPPLIER_ALT_AID");
		map.put(Integer.valueOf(6), "a2 MANUFACTURER_AID");
		map.put(Integer.valueOf(7), "a2 MANUFACTURER_NAME");
		map.put(Integer.valueOf(8), "a2 MANUFACTURER_TYPE_DESCR");
		map.put(Integer.valueOf(9), "a223");
		map.put(Integer.valueOf(10), "a2-G67-HHH");
		map.put(Integer.valueOf(11), "1,333");
		compareSet(line.get(Integer.valueOf(12)), "a2 1 keyword,a2 2 keyword,global 1 keyword,global 2 keyword", ",");
		line.remove(Integer.valueOf(12));
		map.put(Integer.valueOf(13), "a2 - Senkrecht oder versetzt stapelbar.");
		map.put(Integer.valueOf(14), "Ordnungsmittel");
		map.put(Integer.valueOf(15), "11");
		map.put(Integer.valueOf(16), "pieces");
		map.put(Integer.valueOf(17), "1");
		map.put(Integer.valueOf(18), "1,000");
		map.put(Integer.valueOf(19), "1,000");
		map.put(Integer.valueOf(20), "1");
		map.put(Integer.valueOf(21), "1");
		compareSet(line.get(Integer.valueOf(22)), "global.jpg,global2.jpg,a2normal1.jpg,a2normal2.jpg", ",");
		line.remove(Integer.valueOf(22));
		compareSet(line.get(Integer.valueOf(23)), "a2thumb1.jpg,a2thumb2.jpg", ",");
		line.remove(Integer.valueOf(23));
		compareSet(line.get(Integer.valueOf(24)), "data1.pdf,data2.pdf", ",");
		line.remove(Integer.valueOf(24));
		map.put(Integer.valueOf(25), "");
		compareSet(line.get(Integer.valueOf(26)), "a2logo1.jpg,a2logo2.jpg,a2logo3.jpg", ",");
		line.remove(Integer.valueOf(26));
		map.put(Integer.valueOf(27), "xxx.jpg");
		map.put(Integer.valueOf(28), "");
		compareSet(line.get(Integer.valueOf(29)), "unspecified->buyerid a2 XXX;iln->buyerid a2 iln;duns->buyerid a2 duns", ";");
		line.remove(Integer.valueOf(29));
		compareSet(line.get(Integer.valueOf(30)), "G1->a2 other special class;GVVS->a2 special class", ";");
		line.remove(Integer.valueOf(30));
		compareSet(line.get(Integer.valueOf(31)), "new_article->Seit dieser Saison neu;bargain->Dauertiefstpreis", ";");
		line.remove(Integer.valueOf(31));
		compareSet(line.get(Integer.valueOf(32)), "1 PK=3,12 EUR  [01.01.2001,31.07.2022], 1 PK=4,12 EUR", ", ");
		line.remove(Integer.valueOf(32));
		assertEquals(map, line);

		csvreader.readNextLine();
		line = csvreader.getLine();
		map.clear();
		map.put(Integer.valueOf(0), "new");
		map.put(Integer.valueOf(1), "A3");
		map.put(Integer.valueOf(2), "A3 DESCRIPTION_SHORT");
		map.put(Integer.valueOf(3), "A3 DESCRIPTION_LONG");
		map.put(Integer.valueOf(4), "A3 ean");
		map.put(Integer.valueOf(5), "A3 SUPPLIER_ALT_AID");
		map.put(Integer.valueOf(6), "a3 MANUFACTURER_AID");
		map.put(Integer.valueOf(7), "a3 MANUFACTURER_NAME");
		map.put(Integer.valueOf(8), "a3 MANUFACTURER_TYPE_DESCR");
		map.put(Integer.valueOf(9), "a3-23");
		map.put(Integer.valueOf(10), "a3-G67-HHH");
		map.put(Integer.valueOf(11), "1,000");
		compareSet(line.get(Integer.valueOf(12)), "a3 1 keyword,global 1 keyword", ",");
		line.remove(Integer.valueOf(12));
		map.put(Integer.valueOf(13), "a3");
		map.put(Integer.valueOf(14), "keyword");
		map.put(Integer.valueOf(15), "12");
		map.put(Integer.valueOf(16), "pieces");
		map.put(Integer.valueOf(17), "1");
		map.put(Integer.valueOf(18), "1,000");
		map.put(Integer.valueOf(19), "1,000");
		map.put(Integer.valueOf(20), "1");
		map.put(Integer.valueOf(21), "1");
		compareSet(line.get(Integer.valueOf(22)), "global2.jpg,a3normal1.jpg,a3normal2.jpg", ",");
		line.remove(Integer.valueOf(22));
		compareSet(line.get(Integer.valueOf(23)), "a3thumb1.jpg,a3thumb2.jpg", ",");
		line.remove(Integer.valueOf(23));
		map.put(Integer.valueOf(24), "data1.pdf");
		map.put(Integer.valueOf(25), "");
		compareSet(line.get(Integer.valueOf(26)), "a3logo1.jpg,a3logo2.jpg,a3logo3.jpg", ",");
		line.remove(Integer.valueOf(26));
		map.put(Integer.valueOf(27), "yyy.jpg");
		map.put(Integer.valueOf(28), "");
		map.put(Integer.valueOf(29), "unspecified->buyerid a3 XXX");
		map.put(Integer.valueOf(30), "GVVS->a3 keine -- soll nur als Beispiel dienen");
		compareSet(line.get(Integer.valueOf(31)), "new_article->Seit dieser Saison neu;bargain->Dauertiefstpreis", ";");
		line.remove(Integer.valueOf(31));
		map.put(Integer.valueOf(32), "1 PK=12,12 EUR  [01.01.2001,31.07.2022]");
		assertEquals(map, line);

		csvreader.readNextLine();
		line = csvreader.getLine();
		map.clear();
		map.put(Integer.valueOf(0), "new");
		map.put(Integer.valueOf(1), "A4");
		map.put(Integer.valueOf(2), "A4 DESCRIPTION_SHORT");
		map.put(Integer.valueOf(3), "A4 DESCRIPTION_LONG");
		map.put(Integer.valueOf(4), "A4 ean");
		map.put(Integer.valueOf(5), "A4 SUPPLIER_ALT_AID");
		map.put(Integer.valueOf(6), "a4 MANUFACTURER_AID");
		map.put(Integer.valueOf(7), "a4 MANUFACTURER_NAME");
		map.put(Integer.valueOf(8), "a4 MANUFACTURER_TYPE_DESCR");
		map.put(Integer.valueOf(9), "a4-23");
		map.put(Integer.valueOf(10), "a4-G67-HHH");
		map.put(Integer.valueOf(11), "1.000,001");
		compareSet(line.get(Integer.valueOf(12)), "global 1 keyword,global 2 keyword", ",");
		line.remove(Integer.valueOf(12));
		map.put(Integer.valueOf(13), "Senkrecht oder versetzt stapelbar.");
		map.put(Integer.valueOf(14), "Ordnungsmittel");
		map.put(Integer.valueOf(15), "13");
		map.put(Integer.valueOf(16), "pieces");
		map.put(Integer.valueOf(17), "1");
		map.put(Integer.valueOf(18), "1,000");
		map.put(Integer.valueOf(19), "1,000");
		map.put(Integer.valueOf(20), "1");
		map.put(Integer.valueOf(21), "1");
		compareSet(line.get(Integer.valueOf(22)), "global2.jpg,a4normal1.jpg", ",");
		line.remove(Integer.valueOf(22));
		map.put(Integer.valueOf(23), "");
		map.put(Integer.valueOf(24), "");
		map.put(Integer.valueOf(25), "");
		map.put(Integer.valueOf(26), "a4logo1.jpg");
		map.put(Integer.valueOf(27), "");
		map.put(Integer.valueOf(28), "");
		map.put(Integer.valueOf(29), "duns->buyerid a4 duns");
		map.put(Integer.valueOf(30), "GVVS->a4 -- keine - soll nur als Beispiel dienen");
		compareSet(line.get(Integer.valueOf(31)), "new_article->Seit dieser Saison neu;bargain->Dauertiefstpreis", ";");
		line.remove(Integer.valueOf(31));
		compareSet(line.get(Integer.valueOf(32)), "1 PK=42,12 EUR  [01.01.2001,31.07.2022], 1 PK=41,12 EUR", ", ");
		line.remove(Integer.valueOf(32));
		assertEquals(map, line);

		csvreader.readNextLine();
		line = csvreader.getLine();
		map.clear();
		map.put(Integer.valueOf(0), "");
		map.put(Integer.valueOf(1), "A5");
		map.put(Integer.valueOf(2), "A5 DESCRIPTION_SHORT");
		map.put(Integer.valueOf(3), "A5 DESCRIPTION_LONG");
		map.put(Integer.valueOf(4), "A5 ean");
		map.put(Integer.valueOf(5), "A5 SUPPLIER_ALT_AID");
		map.put(Integer.valueOf(6), "a5 MANUFACTURER_AID");
		map.put(Integer.valueOf(7), "a5 MANUFACTURER_NAME");
		map.put(Integer.valueOf(8), "a5 MANUFACTURER_TYPE_DESCR");
		map.put(Integer.valueOf(9), "a5-23");
		map.put(Integer.valueOf(10), "a5-G67-HHH");
		map.put(Integer.valueOf(11), "5,000");
		compareSet(line.get(Integer.valueOf(12)),
				"a5 1 keyword,a5 2 keyword,a5 2 keyword,a5 2 keyword,a5 2 keyword,global 1 keyword,global 2 keyword", ",");
		line.remove(Integer.valueOf(12));
		map.put(Integer.valueOf(13), "a5 Senkrecht oder versetzt stapelbar.");
		map.put(Integer.valueOf(14), "Ordnungsmittel");
		map.put(Integer.valueOf(15), "14");
		map.put(Integer.valueOf(16), "pieces");
		map.put(Integer.valueOf(17), "1");
		map.put(Integer.valueOf(18), "1,000");
		map.put(Integer.valueOf(19), "1,000");
		map.put(Integer.valueOf(20), "1");
		map.put(Integer.valueOf(21), "1");
		map.put(Integer.valueOf(22), "");
		map.put(Integer.valueOf(23), "");
		map.put(Integer.valueOf(24), "");
		map.put(Integer.valueOf(25), "");
		map.put(Integer.valueOf(26), "");
		map.put(Integer.valueOf(27), "");
		map.put(Integer.valueOf(28), "");
		compareSet(line.get(Integer.valueOf(29)), "unspecified->buyerid a5 XXX;iln->buyerid a5 iln;duns->buyerid a5 duns", ";");
		line.remove(Integer.valueOf(29));
		map.put(Integer.valueOf(30), "GVVS->a5-keine - soll nur als Beispiel dienen");
		compareSet(line.get(Integer.valueOf(31)), "new_article->Seit dieser Saison neu;bargain->Dauertiefstpreis", ";");
		line.remove(Integer.valueOf(31));
		compareSet(line.get(Integer.valueOf(32)), "1 PK=52,12 EUR  [01.01.2001,31.07.2022], 1 PK=51,12 EUR", ", ");
		line.remove(Integer.valueOf(32));
		assertEquals(map, line);

		csvreader.close();
	}

	// ###########################################################

	public void aTestWorksOnlyWithTNewCatalog()
	{
		boolean gotException = false;
		final SimpleDateFormat sdf = new SimpleDateFormat();
		final Date date_object = new Date(837039928046L);
		sdf.applyPattern(BMECatConstants.BMECat2CSV.DATE_FORMAT_PATTERN);
		final String date = sdf.format(date_object);
		sdf.applyPattern(BMECatConstants.BMECat2CSV.TIME_FORMAT_PATTERN);
		final String time = sdf.format(date_object);
		sdf.applyPattern(BMECatConstants.BMECat2CSV.TIMEZONE_FORMAT_PATTERN);
		final String timezone = sdf.format(date_object);
		sdf.applyPattern(BMECatConstants.BMECat2CSV.DATETIMETIMEZONE_FORMAT_PATTERN);

		final Reader reader = new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<BMECAT version=\"1.2\">\n"
				+ " <HEADER>\n" + "  <CATALOG>\n" + "   <LANGUAGE>aaa</LANGUAGE>\n" + "   <CATALOG_ID>bbb</CATALOG_ID>\n"
				+ "   <CATALOG_VERSION>ccc</CATALOG_VERSION>\n" + "   <DATETIME type=\"generation_date\">\n" + "    <DATE>" + date
				+ "</DATE>\n" + "    <TIME>" + time + "</TIME>\n" + "    <TIMEZONE>" + timezone + "</TIMEZONE>\n"
				+ "   </DATETIME>\n" + "   <CURRENCY>EUR</CURRENCY>\n" + "   <MIME_ROOT></MIME_ROOT>\n"
				+ "   <PRICE_FLAG type=\"incl_freight\">true</PRICE_FLAG>\n"
				+ "   <PRICE_FLAG type=\"incl_packing\">false</PRICE_FLAG>\n"
				+ "   <PRICE_FLAG type=\"incl_assurance\">TRUE</PRICE_FLAG>\n"
				+ "   <PRICE_FLAG type=\"incl_duty\">False</PRICE_FLAG>\n" + "  </CATALOG>\n" + " </HEADER>\n"
				+ " <T_UPDATE_PRODUCTS>" + " </T_UPDATE_PRODUCTS>" + "</BMECAT>\n");

		// read data
		final MyBMECat2CSVObjectProcessor proc = new MyBMECat2CSVObjectProcessor();
		final BMECatParser bmecatparser = new BMECatParser(proc);
		try
		{
			bmecatparser.parse(new InputSource(reader));
		}
		catch (final IllegalStateException e)
		{
			//TODO assertEquals(
			//		"This object processor can only be uses with a BMECat T_NEW_CATALOG",
			//		e.getMessage());
			gotException = true;
		}
		catch (final Exception e)
		{
			fail(e.getMessage());
		}

		assertTrue(gotException);
		gotException = false;

		final Reader reader2 = new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<BMECAT version=\"1.2\">\n"
				+ " <HEADER>\n" + "  <CATALOG>\n" + "   <LANGUAGE>aaa</LANGUAGE>\n" + "   <CATALOG_ID>bbb</CATALOG_ID>\n"
				+ "   <CATALOG_VERSION>ccc</CATALOG_VERSION>\n" + "   <DATETIME type=\"generation_date\">\n" + "    <DATE>" + date
				+ "</DATE>\n" + "    <TIME>" + time + "</TIME>\n" + "    <TIMEZONE>" + timezone + "</TIMEZONE>\n"
				+ "   </DATETIME>\n" + "   <CURRENCY>EUR</CURRENCY>\n" + "   <MIME_ROOT></MIME_ROOT>\n"
				+ "   <PRICE_FLAG type=\"incl_freight\">true</PRICE_FLAG>\n"
				+ "   <PRICE_FLAG type=\"incl_packing\">false</PRICE_FLAG>\n"
				+ "   <PRICE_FLAG type=\"incl_assurance\">TRUE</PRICE_FLAG>\n"
				+ "   <PRICE_FLAG type=\"incl_duty\">False</PRICE_FLAG>\n" + "  </CATALOG>\n" + " </HEADER>\n" + " <T_UPDATE_PRICES>"
				+ " </T_UPDATE_PRICES>" + "</BMECAT>\n");

		// read data
		final MyBMECat2CSVObjectProcessor proc2 = new MyBMECat2CSVObjectProcessor();
		final BMECatParser bmecatparser2 = new BMECatParser(proc2);
		try
		{
			bmecatparser2.parse(new InputSource(reader2));
		}
		catch (final IllegalStateException e2)
		{
			assertEquals("This object processor can only be uses with a BMECat T_NEW_CATALOG", e2.getMessage());
			gotException = true;
		}
		catch (final Exception e2)
		{
			fail(e2.getMessage());
		}
		assertTrue(gotException);
	}

	public void atest1() throws IOException, JaloBusinessException
	{
		final InputStream inputStream = BMECat2CSVConverterTest.class.getResourceAsStream("/bmecat/big_bmecat_test.xml");
		final MyBMECat2CSVObjectProcessor proc = new MyBMECat2CSVObjectProcessor("cp1252");
		final BMECatParser bmecatparser = new BMECatParser(proc);
		try
		{
			bmecatparser.parse(new InputSource(inputStream));
		}
		catch (final Exception e)
		{
			fail(e.getMessage());
		}
		finally
		{
			proc.finish();
			proc.deleteTempFiles();
		}
	}

	private void compareSet(final String src, final String dest, final String regEx)
	{
		final Set<String> srcSet = new HashSet<String>();
		final Set<String> destSet = new HashSet<String>();
		Iterator<String> itContents;

		itContents = Arrays.asList(src.split(regEx)).iterator();
		while (itContents.hasNext())
		{
			destSet.add(itContents.next());
		}

		itContents = Arrays.asList(dest.split(regEx)).iterator();
		while (itContents.hasNext())
		{
			srcSet.add(itContents.next());
		}

		assertEquals(srcSet, destSet);
	}

}
