package de.hybris.platform.hmc.media;

import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.platform.hmc.jalo.HMCSystemException;
import de.hybris.platform.hmc.webchips.DisplayState;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.fest.assertions.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class ModalMediaFileUploadChipTest
{

	private static final Logger LOG = Logger.getLogger(ModalMediaFileUploadChipTest.class);

	private static final File TEMP_DIR = ConfigUtil.getPlatformConfig(ModalMediaFileUploadChip.class).getSystemConfig()
			.getTempDir();

	@Mock
	private DisplayState state;
	@Mock
	private StreamAcceptor streamAcceptor;

	private ModalMediaFileUploadChip chip;

	@Before
	public void initializeTest()
	{
		MockitoAnnotations.initMocks(this);
		DisplayState.setCurrent(state);
		chip = new ModalMediaFileUploadChip(null, streamAcceptor);
	}

	@Test
	public void testSetFileSuccesfull() throws IOException
	{
		File tempFile = new File(TEMP_DIR, "tempFile_" + System.nanoTime());
		Assertions.assertThat(tempFile.createNewFile()).isTrue();
		tempFile.deleteOnExit();
		chip.setFile(tempFile, "someName", "");
		Assertions.assertThat(tempFile.exists()).isFalse();

		final String javaIoTmpDir = System.getProperty("java.io.tmpdir");
		if (javaIoTmpDir == null)
		{
			LOG.info("java.io.tmpdir is null. Skipping.");
			return;
		}
		tempFile = new File(javaIoTmpDir, "tempFile_" + System.nanoTime());
		Assertions.assertThat(tempFile.createNewFile()).isTrue();
		tempFile.deleteOnExit();
		chip.setFile(tempFile, "someName", "");
		Assertions.assertThat(tempFile.exists()).isFalse();
	}

	@Test
	public void testSetFileNonExisting() throws IOException
	{
		final File tempFile = new File(TEMP_DIR, "tempFile_" + System.nanoTime());
		try
		{
			chip.setFile(tempFile, "someName", "");
			Assert.fail("Expected " + HMCSystemException.class + " with error message: "
					+ ModalMediaFileUploadChip.GIVEN_PATH_DOES_NOT_DENOTE_A_FILE);
		}
		catch (final HMCSystemException ex)
		{
			Assertions.assertThat(ex.getMessage()).isEqualTo(ModalMediaFileUploadChip.GIVEN_PATH_DOES_NOT_DENOTE_A_FILE);
		}
	}

	@Test
	public void testSetFileIsADirectory() throws IOException
	{
		final File tempFile = new File(TEMP_DIR, "tempFile_" + System.nanoTime());
		Assertions.assertThat(tempFile.mkdir()).isTrue();
		tempFile.deleteOnExit();
		try
		{
			chip.setFile(tempFile, "someName", "");
			Assert.fail("Expected " + HMCSystemException.class + " with error message: "
					+ ModalMediaFileUploadChip.GIVEN_PATH_DOES_NOT_DENOTE_A_FILE);
		}
		catch (final HMCSystemException ex)
		{
			Assertions.assertThat(ex.getMessage()).isEqualTo(ModalMediaFileUploadChip.GIVEN_PATH_DOES_NOT_DENOTE_A_FILE);
		}
	}

	@Test
	public void testSetFileIsNotInTheTempDir() throws IOException
	{
		final File parentFile = TEMP_DIR.getParentFile();
		Assertions.assertThat(parentFile).isNotNull();
		final File tempFile = new File(parentFile, "tempFile_" + System.nanoTime());
		Assertions.assertThat(tempFile.createNewFile()).isTrue();
		tempFile.deleteOnExit();
		try
		{
			chip.setFile(tempFile, "someName", "");
			Assert.fail("Expected " + HMCSystemException.class + " with error message: "
					+ String.format(ModalMediaFileUploadChip.ILLEGAL_ATTEMPT_TO_ACCESS_A_FILE, tempFile));
		}
		catch (final HMCSystemException ex)
		{
			Assertions.assertThat(ex.getMessage()).isEqualTo(
					String.format(ModalMediaFileUploadChip.ILLEGAL_ATTEMPT_TO_ACCESS_A_FILE, tempFile));
		}
	}

}
