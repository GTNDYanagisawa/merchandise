package com.hybris.datahub.core.facades.impl;

import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.impex.ImpExResource;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;

import com.hybris.datahub.core.facades.ErrorCode;
import com.hybris.datahub.core.facades.ImportError;
import com.hybris.datahub.core.facades.ImportTestUtils;
import com.hybris.datahub.core.facades.ItemImportResult;
import com.hybris.datahub.core.facades.impl.converter.SourceAndErrorCombiningConverter;
import com.hybris.datahub.core.io.TextFile;
import com.hybris.datahub.core.services.impl.DataHubFacade;
import com.hybris.datahub.core.services.impl.DataHubImpExResourceFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * This test import result processing integration. The result received from the <code>ImportService</code> is mocked to
 * allow for various scenarios hard to produce otherwise but the rest of the processing is real. This is the reason why
 * <code>SpringJUnit4ClassRunner</code> is not used to run these tests: it would require a special test spring-beans
 * configuration to wire up the facade correctly and doing so will eliminate the production Spring configuration from
 * the test. For that reason the wiring is done directly in the test. Different cases represent parsing of all errors
 * known at the moment, which were captured by going through numerous debugging sessions simulating various conditions.
 * The duplicate lines in the simulated cases reflect what's really reported in the <code>ImportResult</code>.
 * <p/>
 * All logged errors and expected test results refer to the impex script used for the test. Source of the script can be
 * found inside {@link #impexScript()} method. If you change that method you will need to adjust the test results.
 */
@SuppressWarnings("javadoc")
@IntegrationTest
public class DefaultItemImportFacadeIntegrationTest extends ServicelayerTest
{
	private static final String IMPEX_FILE_PATH = "/impex/media/test.impex";
	private static final String TEST_POOL_NAME = "Test pool";
	private static final Long TEST_PUBLICATION_ID = 1l;
	private static final String TEST_CALLBACK_URL = "http://localhost ";
	private static final InputStream testInput = new ByteArrayInputStream(impexScript().getBytes());

	private ImportResult importResult;
	private DefaultItemImportFacade importFacade;

	@Resource
	private EventService eventService;

	@BeforeClass
	public static void saveImpexSourceBeforeAllTests() throws IOException
	{
		impexFile().save(impexScript());
	}

	@AfterClass
	public static void deleteImpexSourceAfterAllTestsDone() throws IOException
	{
		impexFile().delete();
	}

	private static TextFile impexFile()
	{
		final String dataDir = ConfigUtil.getPlatformConfig(Registry.class).getSystemConfig().getDataDir().getPath();
		final String mediaDir = dataDir + File.separator + "media";
		final String tenantDir = mediaDir + File.separator + "sys_master";
		final TextFile impex = new TextFile(tenantDir, IMPEX_FILE_PATH);
		return impex;
	}

	private static String impexScript()
	{
		// Lines of this script are used in the test results. Any changes done to the script should be reflected in the tests.
		return ImportTestUtils
				.toText(
						/* line 1: */"$baseProduct=baseProduct(code, catalogVersion(catalog(id[default='apparelProductCatalog']),version[default='Staged']))",
						/* line 2: */"$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])[unique=true,default=apparelProductCatalog:Staged]",
						/* line 3: */"",
						/* line 4: */"##########################",
						/* line 5: */"INSERT_UPDATE Category;;description[lang=en];name[lang=en];code[unique=true];$catalogVersion",
						/* line 6: */";101;;Material;3912;",
						/* line 7: */";123;;Snowwear women;;",
						/* line 8: */";222;;Open Catalogue;1;apparelProductCatalog",
						/* line 9: */"",
						/* line 10: */"##########################",
						/* line 11: */"INSERT_UPDATE Product;;description[lang=de];name[lang=de];code[unique=true];unit(code);$catalogVersion",
						/* line 12: */";321;;Andover Jacke;95385;piece;");
	}

	@Before
	public void setUp()
	{
		importResult = ImportTestUtils.importResult(IMPEX_FILE_PATH);

		importFacade = new DefaultItemImportFacade();
		importFacade.setResourceFactory(new DataHubImpExResourceFactory());
		importFacade.setResultConverter(new SourceAndErrorCombiningConverter());
		importFacade.setImportService(mockImportService(importResult));
		importFacade.setEventService(eventService);
		importFacade.setDataHubFacade(mockDataHubFacade());
	}

	private ImportService mockImportService(final ImportResult result)
	{
		final ImportService impService = Mockito.mock(ImportService.class);
		Mockito.doReturn(result).when(impService).importData(any(ImpExResource.class));
		Mockito.doReturn(result).when(impService).importData(any(ImportConfig.class));
		return impService;
	}

	private DataHubFacade mockDataHubFacade()
	{
		final DataHubFacade dataHubFacade = Mockito.mock(DataHubFacade.class);
		return dataHubFacade;
	}

	@Test
	public void testSuccessfulImportDoesNotContainErrorsInTheResult() throws Exception
	{
		ImportTestUtils.makeResultSuccessful(importResult);

		final ItemImportResult res = importFacade.runImport(testInput, TEST_POOL_NAME);

		Assert.assertTrue("Result has errors", res.getErrors().isEmpty());
		Assert.assertTrue("Result is unsuccessful", res.isSuccessful());
	}

	@Test
	public void testRequiredAttributeMissing() throws IOException, InterruptedException, ExecutionException
	{
		ImportTestUtils.makeResultWithErrors(importResult, requiredAttributeMissingLog());

		final ItemImportResult res = importFacade.runImport(testInput, TEST_POOL_NAME);
		final ImportError err = getErrorReported(res);

		Assert.assertEquals("Error code", ErrorCode.MISSING_REQUIRED_ATTRIBUTE, err.getCode());
		Assert.assertEquals("Message", "value is NULL for mandatory attribute Category.code", err.getMessage());
		Assert.assertEquals("Item ID", new Long(123), err.getCanonicalItemId());
		Assert.assertNull("Item Type", err.getItemType());
		Assert.assertEquals("Source Line", ";123;;Snowwear women;;", err.getScriptLine());
	}

	private static String requiredAttributeMissingLog()
	{
		return ImportTestUtils
				.toText(
						"17.12.2013 15:48:27: ERROR: line 7 at main script: value is NULL for mandatory attribute Category.code",
						"17.12.2013 15:48:27: ERROR: line 7 at main script: value is NULL for mandatory attribute Category.code",
						"17.12.2013 15:48:27: ERROR: line 7 at main script: Exception 'value is NULL for mandatory attribute Category.code' in handling exception: value is NULL for mandatory attribute Category.code",
						"17.12.2013 15:48:27: ERROR: line 7 at main script: Exception 'value is NULL for mandatory attribute Category.code' in handling exception: value is NULL for mandatory attribute Category.code");
	}

	@Test
	public void testNonExistingReferenceToItemId() throws Exception
	{
		ImportTestUtils.makeResultWithErrors(importResult, notResolvedItemLog(), notResolvedProductIdPreview());

		final ItemImportResult res = importFacade.runImport(testInput, TEST_POOL_NAME);
		final ImportError err = getErrorReported(res);

		Assert.assertEquals("Error code", ErrorCode.NOT_EXISTING_ITEM, err.getCode());
		Assert.assertEquals("Message", "no existing item found for update", err.getMessage());
		Assert.assertEquals("Item ID", new Long(321), err.getCanonicalItemId());
		Assert.assertNull("Item Type", err.getItemType());
		Assert.assertEquals("Source Line", ";321;;Andover Jacke;95385;piece;", err.getScriptLine());
	}

	private static String notResolvedProductIdPreview()
	{
		return ImportTestUtils
				.toText(
						"UPDATE Product;code[unique=true];name[lang=en];Unit(code);catalogVersion(catalog(id),version)[unique=true,allowNull=true];description[lang=en];approvalStatus(code);ean;manufacturerName",
						",,,no existing item found for update;321;;Andover Jacke;95385;piece;");
	}

	private static String notResolvedItemLog()
	{
		return ImportTestUtils
				.toText(
						"17.12.2013 15:51:31: ERROR: line 12 at main script: Can not resolve any more lines ... Aborting further passes (at pass 2). Finally could not import 1 lines!",
						"17.12.2013 15:51:31: ERROR: line 12 at main script: Can not resolve any more lines ... Aborting further passes (at pass 2). Finally could not import 1 lines!");
	}

	@Test
	public void testUnknownAttributeInTheHeader() throws Exception
	{
		ImportTestUtils.makeResultWithErrors(importResult, unknownAttributeLog());

		final ItemImportResult res = importFacade.runImport(testInput, TEST_POOL_NAME);
		final ImportError err = getErrorReported(res);

		Assert.assertEquals("Error code", ErrorCode.UNKNOWN_ATTRIBUTE, err.getCode());
		Assert.assertEquals("Message", "unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Category'", err.getMessage());
		Assert.assertEquals("Item Type", "Category", err.getItemType());
		Assert.assertNull("Item ID", err.getCanonicalItemId());
		Assert.assertEquals("Source Line",
				"INSERT_UPDATE Category;;description[lang=en];name[lang=en];code[unique=true];$catalogVersion", err.getScriptLine());
	}

	private static String unknownAttributeLog()
	{
		return ImportTestUtils
				.toText(
						"17.12.2013 16:34:22: ERROR: line 5 at main script: unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Category'",
						"17.12.2013 16:34:22: ERROR: line 5 at main script: unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Category'",
						"17.12.2013 16:34:22: ERROR: line 5 at main script: Exception 'unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Category'' in handling exception: unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Category'",
						"17.12.2013 16:34:22: ERROR: line 5 at main script: Exception 'unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Category'' in handling exception: unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Category'");
	}

	private ImportError getErrorReported(final ItemImportResult res)
	{
		Assert.assertFalse("Result is successful", res.isSuccessful());
		Assert.assertEquals("Number of errors", 1, res.getErrors().size());
		return res.getErrors().iterator().next();
	}

	@Test
	public void testInvalidEnumReferenceInAnItem() throws Exception
	{
		ImportTestUtils.makeResultWithErrors(importResult, invalidUnitReferenceLog(), invalidUnitReferencePreview());

		final ItemImportResult res = importFacade.runImport(testInput, TEST_POOL_NAME);
		final ImportError err = getErrorReported(res);

		Assert.assertEquals("Error code", ErrorCode.REFERENCE_VIOLATION, err.getCode());
		Assert.assertEquals("Message", "column 5: could not resolve item for piece", err.getMessage());
		Assert.assertNull("Item Type", err.getItemType());
		Assert.assertEquals("Item ID", new Long(321), err.getCanonicalItemId());
		Assert.assertEquals("Source Line", ";321;;Andover Jacke;95385;piece;", err.getScriptLine());
	}

	private static String invalidUnitReferencePreview()
	{
		return ImportTestUtils
				.toText(
						"INSERT_UPDATE Product;;code[unique=true];name[lang=en];Unit(code);catalogVersion(catalog(id),version)[unique=true,allowNull=true];description[lang=en];approvalStatus(code);ean;manufacturerName",
						",8796158623745,,, column 5: could not resolve item for piece;321;;Andover Jacke;95385;piece;");
	}

	private static String invalidUnitReferenceLog()
	{
		return ImportTestUtils
				.toText(
						"17.12.2013 15:51:31: ERROR: line 12 at main script: Can not resolve any more lines ... Aborting further passes (at pass 2). Finally could not import 1 lines!",
						"17.12.2013 15:51:31: ERROR: line 12 at main script: Can not resolve any more lines ... Aborting further passes (at pass 2). Finally could not import 1 lines!");
	}

	@Test
	public void testNotEnoughValuesForAttribute() throws Exception
	{
		ImportTestUtils.makeResultWithErrors(importResult, notEnoughValuesForAttributeLog());

		final ItemImportResult res = importFacade.runImport(testInput, TEST_POOL_NAME);
		final ImportError err = getErrorReported(res);

		Assert.assertEquals("Error code", ErrorCode.NOT_ENOUGH_ATTRIBUTE_VALUES, err.getCode());
		Assert.assertEquals("Message",
				"item reference a for attribute CatalogVersion.version does not provide enough values at position 1",
				err.getMessage());
		Assert.assertNull("Item Type", err.getItemType());
		Assert.assertEquals("Item ID", new Long(222), err.getCanonicalItemId());
		Assert.assertEquals("Source Line", ";222;;Open Catalogue;1;apparelProductCatalog", err.getScriptLine());
	}

	private static String notEnoughValuesForAttributeLog()
	{
		return ImportTestUtils
				.toText("17.12.2013 17:08:45: ERROR: line 8 at main script: item reference a for attribute CatalogVersion" +
						".version does not provide enough values at position 1",
						"17.12.2013 17:08:45: ERROR: line 8 at main script: item reference a for attribute CatalogVersion" +
								".version does not provide enough values at position 1",
						"17.12.2013 17:08:45: ERROR: line 8 at main script: Exception 'item reference a for attribute " +
								"CatalogVersion.version does not provide enough values at position 1' in handling exception: " +
								"item reference a for attribute CatalogVersion.version does not provide enough values at " +
								"position 1",
						"17.12.2013 17:08:45: ERROR: line 8 at main script: Exception 'item reference a for attribute " +
								"CatalogVersion.version does not provide enough values at position 1' in handling exception: " +
								"item reference a for attribute CatalogVersion.version does not provide enough values at " +
								"position 1");
	}

	@Test
	public void testDuplicateItemInsert() throws Exception
	{
		ImportTestUtils.makeResultWithErrors(importResult, duplicateItemLog());

		final ItemImportResult res = importFacade.runImport(testInput, TEST_POOL_NAME);
		final ImportError err = getErrorReported(res);

		Assert.assertEquals("Error code", ErrorCode.UNIQUE_KEY_VIOLATION, err.getCode());
		Assert.assertEquals(
				"Message",
				"conflict between existing item 3912(8796093972622) and line ValueLine[,line 6 at main script,null,HeaderDescriptor[line 5 at main script, insert, Category, {}, [description, name, code, catalogversion] ],{1=ValueEntry('single|Category|008043d7-fa38-4a26-96c0-52a411fde1cd'=null,unresolved=null,ignore=false), 2=ValueEntry(''=null,unresolved=null,ignore=false), 3=ValueEntry('Apparel'=null,unresolved=null,ignore=false), 4=ValueEntry('apparel'=apparel,unresolved=false,ignore=false), 5=ValueEntry(''=clothescatalog/Staged(8796093252185),unresolved=false,ignore=false)}]",
				err.getMessage());
		Assert.assertNull("Item Type", err.getItemType());
		Assert.assertEquals("Item ID", new Long(101), err.getCanonicalItemId());
		Assert.assertEquals("Source Line", ";101;;Material;3912;", err.getScriptLine());
	}

	private static String duplicateItemLog()
	{
		return ImportTestUtils
				.toText(
						"17.01.2014 11:10:53: ERROR: line 6 at main script: conflict between existing item 3912(8796093972622) and line ValueLine[,line 6 at main script,null,HeaderDescriptor[line 5 at main script, insert, Category, {}, [description, name, code, catalogversion] ],{1=ValueEntry('single|Category|008043d7-fa38-4a26-96c0-52a411fde1cd'=null,unresolved=null,ignore=false), 2=ValueEntry(''=null,unresolved=null,ignore=false), 3=ValueEntry('Apparel'=null,unresolved=null,ignore=false), 4=ValueEntry('apparel'=apparel,unresolved=false,ignore=false), 5=ValueEntry(''=clothescatalog/Staged(8796093252185),unresolved=false,ignore=false)}]",
						"17.01.2014 11:10:53: ERROR: line 6 at main script: conflict between existing item 3912(8796093972622) and line ValueLine[,line 6 at main script,null,HeaderDescriptor[line 5 at main script, insert, Category, {}, [description, name, code, catalogversion] ],{1=ValueEntry('single|Category|008043d7-fa38-4a26-96c0-52a411fde1cd'=null,unresolved=null,ignore=false), 2=ValueEntry(''=null,unresolved=null,ignore=false), 3=ValueEntry('Apparel'=null,unresolved=null,ignore=false), 4=ValueEntry('apparel'=apparel,unresolved=false,ignore=false), 5=ValueEntry(''=clothescatalog/Staged(8796093252185),unresolved=false,ignore=false)}]",
						"17.01.2014 11:10:53: ERROR: line 6 at main script: Exception 'conflict between existing item 3912(8796093972622) and line ValueLine[,line 6 at main script,null,HeaderDescriptor[line 5 at main script, insert, Category, {}, [description, name, code, catalogversion] ],{1=ValueEntry('single|Category|008043d7-fa38-4a26-96c0-52a411fde1cd'=null,unresolved=null,ignore=false), 2=ValueEntry(''=null,unresolved=null,ignore=false), 3=ValueEntry('Apparel'=null,unresolved=null,ignore=false), 4=ValueEntry('apparel'=apparel,unresolved=false,ignore=false), 5=ValueEntry(''=clothescatalog/Staged(8796093252185),unresolved=false,ignore=false)}]' in handling exception: conflict between existing item apparel(8796093972622) and line ValueLine[,line 5 at main script,null,HeaderDescriptor[line 4 at main script, insert, Category, {}, [description, name, code, catalogversion] ],{1=ValueEntry('single|Category|008043d7-fa38-4a26-96c0-52a411fde1cd'=null,unresolved=null,ignore=false), 2=ValueEntry(''=null,unresolved=null,ignore=false), 3=ValueEntry('Apparel'=null,unresolved=null,ignore=false), 4=ValueEntry('apparel'=apparel,unresolved=false,ignore=false), 5=ValueEntry(''=clothescatalog/Staged(8796093252185),unresolved=false,ignore=false)}]",
						"17.01.2014 11:10:53: ERROR: line 6 at main script: Exception 'conflict between existing item 3912(8796093972622) and line ValueLine[,line 6 at main script,null,HeaderDescriptor[line 5 at main script, insert, Category, {}, [description, name, code, catalogversion] ],{1=ValueEntry('single|Category|008043d7-fa38-4a26-96c0-52a411fde1cd'=null,unresolved=null,ignore=false), 2=ValueEntry(''=null,unresolved=null,ignore=false), 3=ValueEntry('Apparel'=null,unresolved=null,ignore=false), 4=ValueEntry('apparel'=apparel,unresolved=false,ignore=false), 5=ValueEntry(''=clothescatalog/Staged(8796093252185),unresolved=false,ignore=false)}]' in handling exception: conflict between existing item apparel(8796093972622) and line ValueLine[,line 5 at main script,null,HeaderDescriptor[line 4 at main script, insert, Category, {}, [description, name, code, catalogversion] ],{1=ValueEntry('single|Category|008043d7-fa38-4a26-96c0-52a411fde1cd'=null,unresolved=null,ignore=false), 2=ValueEntry(''=null,unresolved=null,ignore=false), 3=ValueEntry('Apparel'=null,unresolved=null,ignore=false), 4=ValueEntry('apparel'=apparel,unresolved=false,ignore=false), 5=ValueEntry(''=clothescatalog/Staged(8796093252185),unresolved=false,ignore=false)}]");
	}

	@Test
	public void testAttributeParseError() throws Exception
	{
		ImportTestUtils.makeResultWithErrors(importResult, attributeParseErrorLog());

		final ItemImportResult res = importFacade.runImport(testInput, TEST_POOL_NAME);
		final ImportError err = getErrorReported(res);

		Assert.assertEquals("Error code", ErrorCode.INVALID_DATA_FORMAT, err.getCode());
		Assert.assertEquals("Message",
				"cannot parse number 'a' with format specified pattern '#,##0.###' due to Unparseable number: \"a\"",
				err.getMessage());
		Assert.assertNull("Item Type", err.getItemType());
		Assert.assertEquals("Item ID", new Long(123), err.getCanonicalItemId());
		Assert.assertEquals("Source Line", ";123;;Snowwear women;;", err.getScriptLine());
	}

	private static String attributeParseErrorLog()
	{
		return ImportTestUtils
				.toText(
						"17.12.2013 17:34:51: ERROR: line 7 at main script: cannot parse number 'a' with format specified pattern '#,##0.###' due to Unparseable number: \"a\"",
						"17.12.2013 17:34:51: ERROR: line 7 at main script: cannot parse number 'a' with format specified pattern '#,##0.###' due to Unparseable number: \"a\"",
						"17.12.2013 17:34:51: ERROR: line 7 at main script: Exception 'cannot parse number 'a' with format specified pattern '#,##0.###' due to Unparseable number: \"a\"' in handling exception: cannot parse number 'a' with format specified pattern '#,##0.###' due to Unparseable number: \"a\"",
						"17.12.2013 17:34:51: ERROR: line 7 at main script: Exception 'cannot parse number 'a' with format specified pattern '#,##0.###' due to Unparseable number: \"a\"' in handling exception: cannot parse number 'a' with format specified pattern '#,##0.###' due to Unparseable number: \"a\"");
	}

	@Test
	public void testMoreThanOneItemFound() throws Exception
	{
		ImportTestUtils.makeResultWithErrors(importResult, moreThanOneItemFoundLog());

		final ItemImportResult res = importFacade.runImport(testInput, TEST_POOL_NAME);
		final ImportError err = getErrorReported(res);

		Assert.assertEquals("Error code", ErrorCode.MORE_THAN_ONE_ITEM_FOUND, err.getCode());
		Assert.assertEquals(
				"Message",
				"more than one item found for '1' using query 'de.hybris.platform.impex.jalo.translators.ItemExpressionTranslator$ExpressionQuery@446006a' with values {v1=apparelProductCatalog, v0=1, v2=Staged} - got LazyList[[8796365946881, 8796240379905]]",
				err.getMessage());
		Assert.assertNull("Item Type", err.getItemType());
		Assert.assertEquals("Item ID", new Long(222), err.getCanonicalItemId());
		Assert.assertEquals("Source Line", ";222;;Open Catalogue;1;apparelProductCatalog", err.getScriptLine());
	}

	private static String moreThanOneItemFoundLog()
	{
		return ImportTestUtils
				.toText(
						"17.01.2014 10:42:49: ERROR: line 8 at main script: more than one item found for '1' using query 'de.hybris.platform.impex.jalo.translators.ItemExpressionTranslator$ExpressionQuery@446006a' with values {v1=apparelProductCatalog, v0=1, v2=Staged} - got LazyList[[8796365946881, 8796240379905]]",
						"17.01.2014 10:42:49: ERROR: line 8 at main script: more than one item found for '1' using query 'de.hybris.platform.impex.jalo.translators.ItemExpressionTranslator$ExpressionQuery@446006a' with values {v1=apparelProductCatalog, v0=1, v2=Staged} - got LazyList[[8796365946881, 8796240379905]]",
						"17.01.2014 10:42:49: ERROR: line 8 at main script: Exception 'more than one item found for '1' using query 'de.hybris.platform.impex.jalo.translators.ItemExpressionTranslator$ExpressionQuery@446006a' with values {v1=apparelProductCatalog, v0=1, v2=Staged} - got LazyList[[8796365946881, 8796240379905]]' in handling exception: more than one item found for '1' using query 'de.hybris.platform.impex.jalo.translators.ItemExpressionTranslator$ExpressionQuery@446006a' with values {v1=apparelProductCatalog, v0=1, v2=Staged} - got LazyList[[8796365946881, 8796240379905]]",
						"17.01.2014 10:42:49: ERROR: line 8 at main script: Exception 'more than one item found for '1' using query 'de.hybris.platform.impex.jalo.translators.ItemExpressionTranslator$ExpressionQuery@446006a' with values {v1=apparelProductCatalog, v0=1, v2=Staged} - got LazyList[[8796365946881, 8796240379905]]' in handling exception: more than one item found for '1' using query 'de.hybris.platform.impex.jalo.translators.ItemExpressionTranslator$ExpressionQuery@446006a' with values {v1=apparelProductCatalog, v0=1, v2=Staged} - got LazyList[[8796365946881, 8796240379905]]");
	}

	@Test
	public void testUnresolvedAttributeValue() throws Exception
	{
		ImportTestUtils.makeResultWithErrors(importResult, unresolvedAttributeValueLog(), unresolvedAttributeValuePreview());

		final ItemImportResult res = importFacade.runImport(testInput, TEST_POOL_NAME);
		final ImportError err = getErrorReported(res);

		Assert.assertEquals("Error code", ErrorCode.REFERENCE_VIOLATION, err.getCode());
		Assert.assertEquals("Message", "column 5: cannot resolve value '' for attribute 'catalogversion'", err.getMessage());
		Assert.assertNull("Item Type", err.getItemType());
		Assert.assertEquals("Item ID", new Long(101), err.getCanonicalItemId());
		Assert.assertEquals("Source Line", ";101;;Material;3912;", err.getScriptLine());
	}

	private static String unresolvedAttributeValuePreview()
	{
		return ImportTestUtils
				.toText(
						"INSERT_UPDATE Category;;description[lang=de];name[lang=de];code[unique=true];catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])[unique=true,default=apparelProductCatalog:Staged]",
						",,,error finding existing item : column='catalogversion' value='', , column 5: cannot resolve value '' for attribute 'catalogversion';101;<ignore>;Material;3912;");
	}

	private static String unresolvedAttributeValueLog()
	{
		return ImportTestUtils
				.toText(
						"17.12.2013 15:51:31: ERROR: line 6 at main script: Can not resolve any more lines ... Aborting further passes (at pass 2). Finally could not import 1 lines!",
						"17.12.2013 15:51:31: ERROR: line 6 at main script: Can not resolve any more lines ... Aborting further passes (at pass 2). Finally could not import 1 lines!");
	}

	@Test
	public void testUnexpectedError_theCatchUnexpectedCase() throws Exception
	{
		final String errorLog = "some problem happened and crashed import";
		ImportTestUtils.makeResultWithErrors(importResult, errorLog);

		final ItemImportResult res = importFacade.runImport(testInput, TEST_POOL_NAME);
		final ImportError err = getErrorReported(res);

		Assert.assertEquals("Error code", ErrorCode.UNCLASSIFIED, err.getCode());
		Assert.assertEquals("Message", errorLog, err.getMessage());
		Assert.assertNull("Item Type", err.getItemType());
		Assert.assertNull("Item ID", err.getCanonicalItemId());
		Assert.assertEquals("Source Line", "", err.getScriptLine());
	}

	@Test
	public void testEventIsFiredOnSuccessfulResult() throws Exception
	{
		final DatahubAdapterTestListener listener = registerListener();
		ImportTestUtils.makeResultSuccessful(importResult);
		importFacade.importItems(TEST_POOL_NAME, TEST_PUBLICATION_ID, TEST_CALLBACK_URL, testInput);

		Assert.assertEquals(TEST_POOL_NAME, listener.getEvents().get(0).getPoolName());
		Assert.assertEquals(ItemImportResult.DatahubAdapterEventStatus.SUCCESS, listener.getEvents().get(0).getStatus());
	}

	@Test
	public void testEventIsFiredWithPartialSuccessStatusWhenImportHasErrors() throws Exception
	{
		final DatahubAdapterTestListener listener = registerListener();
		ImportTestUtils.makeResultWithErrors(importResult, requiredAttributeMissingLog());
		importFacade.importItems(TEST_POOL_NAME, TEST_PUBLICATION_ID, TEST_CALLBACK_URL, testInput);

		Assert.assertEquals(TEST_POOL_NAME, listener.getEvents().get(0).getPoolName());
		Assert.assertEquals(ItemImportResult.DatahubAdapterEventStatus.PARTIAL_SUCCESS, listener.getEvents().get(0).getStatus());
	}

	@Test
	public void testEventIsFiredWithFailureStatusWhenImportExceptionIsThrown() throws Exception
	{
		final DatahubAdapterTestListener listener = registerListener();
		mockImpExExceptionOnResourceCreation();
		importFacade.importItems(TEST_POOL_NAME, TEST_PUBLICATION_ID, TEST_CALLBACK_URL, testInput);

		Assert.assertEquals(TEST_POOL_NAME, listener.getEvents().get(0).getPoolName());
		Assert.assertEquals(ItemImportResult.DatahubAdapterEventStatus.FAILURE, listener.getEvents().get(0).getStatus());
	}

	private DatahubAdapterTestListener registerListener()
	{
		final DatahubAdapterTestListener listener = new DatahubAdapterTestListener();
		eventService.registerEventListener(listener);
		return listener;
	}

	private void mockImpExExceptionOnResourceCreation() throws ImpExException
	{
		final DataHubImpExResourceFactory resourceFactory = Mockito.mock(DataHubImpExResourceFactory.class);
		Mockito.doThrow(new ImpExException("test")).when(resourceFactory).createResource(any(InputStream.class));
		importFacade.setResourceFactory(resourceFactory);
	}
}
