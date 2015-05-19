package de.hybris.platform.licence.sap;

import static junit.framework.Assert.fail;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.platform.licence.internal.SAPLicenseValidator;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.util.Utilities;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.security.core.server.likey.Persistence;


@IntegrationTest
public class HybrisAdminTest extends HybrisJUnit4Test
{
	private SAPLicenseValidator validator;
	private PropertyBasedTestPersistence persistence;

	private String hwKeyBackup;

	@Before
	public void setUp() throws Exception
	{
		persistence = new PropertyBasedTestPersistence();

		// Need to patch hardware key to match test license file !!!
		hwKeyBackup = changeHardwareKeyTo("A0000000000");
		validator = new SAPLicenseValidator()
		{

			@Override
			protected Persistence getPersistence()
			{
				return persistence;
			}
		};
		System.setProperty("persistence.impl", PropertyBasedTestPersistence.class.getCanonicalName());
	}

	@After
	public void tearDown() throws Exception
	{
		restoreHardwareKey(hwKeyBackup);
		System.clearProperty("persistence.impl");
		persistence.removePersistenceFile();
	}

	String changeHardwareKeyTo(final String key)
	{
		return (String) Utilities.loadPlatformProperties().setProperty("license.hardware.key", key);
	}

	void restoreHardwareKey(final String original)
	{
		if (original == null)
		{
			Utilities.loadPlatformProperties().remove("license.hardware.key");
		}
		else
		{
			Utilities.loadPlatformProperties().setProperty("license.hardware.key", original);
		}
	}

	@Test
	public void shouldInstallTempLicense() throws Exception
	{
		// given
		final String[] args = new String[]
		{ "-t", "CPS_HDB" };
		assertThat(validator.validateLicense("CPS_HDB").isValid()).isFalse();

		// when
		HybrisAdmin.main(args);

		// then
		assertThat(validator.validateLicense("CPS_HDB").isValid()).isTrue();
	}

	@Test
	public void shouldInstallLicenseFromFile() throws Exception
	{
		// given
		final String licenseFileLocation = getLicenseFileLocation();
		writeStandardLicenseFile(licenseFileLocation);
		final String[] args = new String[]
		{ "-i", licenseFileLocation };
		assertThat(validator.validateLicense("CPS_HDB").isValid()).isFalse();

		// when
		HybrisAdmin.main(args);

		// then
		assertThat(validator.validateLicense("CPS_HDB").isValid()).isTrue();
		FileUtils.deleteQuietly(new File(licenseFileLocation));
	}

	@Test
	public void shouldDeleteExistingLicense() throws Exception
	{
		// given
		HybrisAdmin.main(new String[]
		{ "-t", "CPS_HDB" });
		final String[] deleteArgs = new String[]
		{ "-d", "CPS", "A0000000000", "CPS_HDB" };

		// when
		HybrisAdmin.main(deleteArgs);

		// then
		assertThat(validator.validateLicense("CPS_HDB").isValid()).isFalse();
	}

	private String getLicenseFileLocation()
	{
		return ConfigUtil.getPlatformConfig(HybrisAdminTest.class).getSystemConfig().getTempDir() + "/testLicense.txt";
	}

	private void writeStandardLicenseFile(final String location)
	{
		final File file = new File(location);
		try
		{
			FileUtils.writeStringToFile(file, getStandardLicenceFileContent());
		}
		catch (final IOException e)
		{
			fail(e.getMessage());
		}
	}

	private String getStandardLicenceFileContent()
	{
		return "----- Begin SAP License -----\n"
				+ "SAPSYSTEM=CPS\n"
				+ "HARDWARE-KEY=A0000000000\n"
				+ "INSTNO=SAP-INTERN\n"
				+ "BEGIN=20140813\n"
				+ "EXPIRATION=20150814\n"
				+ "LKEY=MIIBOgYJKoZIhvcNAQcCoIIBKzCCAScCAQExCzAJBgUrDgMCGgUAMAsGCSqGSIb3DQEHATGCAQYwggECAgEBMFgwUjELMAkGA1UEBhMCREUxHDAaBgNVBAoTE215U0FQLmNvbSBXb3JrcGxhY2UxJTAjBgNVBAMTHG15U0FQLmNvbSBXb3JrcGxhY2UgQ0EgKGRzYSkCAgGhMAkGBSsOAwIaBQCgXTAYBgkqhkiG9w0BCQMxCwYJKoZIhvcNAQcBMBwGCSqGSIb3DQEJBTEPFw0xNDA4MTQxMzMzMTVaMCMGCSqGSIb3DQEJBDEWBBRreAQ3rZmQKxKjeNh5qHx6pSAVdzAJBgcqhkjOOAQDBC4wLAIUKb6k1fKfiSBsWlx3MflEYGhluEICFB3wRSRexRpjmohKk0uBviNawyXo\n"
				+ "SWPRODUCTNAME=CPS_HDB\n" + "SWPRODUCTLIMIT=2147483647\n" + "SYSTEM-NR=000000000311440630\n";
	}

}
