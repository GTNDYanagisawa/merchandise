package com.hybris.datahub.core.io;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class TextFileUnitTest
{
	private static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	private static final String FILE_NAME = "TestFile.txt";
	private static final String LS = System.lineSeparator();

	@After
	public void tearDown() throws IOException
	{
		FileUtils.cleanDirectory(new File(TMP_DIR));
	}

	@Test
	public void testCreatesTextFileWithASpecificPath()
	{
		final String path = TMP_DIR + FILE_NAME;
		final TextFile file = new TextFile(path);

		Assert.assertEquals(path, file.getPath());
		Assert.assertEquals(path, file.getFilePath().getPath());
	}

	@Test
	public void testCreatesTextFileWithASpecifiedDirectoryAndFileName()
	{
		final TextFile file = new TextFile(TMP_DIR, FILE_NAME);

		final String expectedPath = TMP_DIR + FILE_NAME;
		Assert.assertEquals(expectedPath, file.getPath());
		Assert.assertEquals(expectedPath, file.getFilePath().getPath());
	}

	@Test
	public void testCreatesTextFileWithASpecificFile()
	{
		final String path = TMP_DIR + FILE_NAME;
		final TextFile file = new TextFile(new File(path));

		Assert.assertEquals(path, file.getPath());
		Assert.assertEquals(path, file.getFilePath().getPath());
	}

	@Test
	public void testDoesNotSaveFileFileBeforeContentWasWritten()
	{
		Assert.assertFalse(testFile().getFilePath().exists());
	}

	@Test
	public void testSaveCreatesFileIfItDoesNotExist() throws IOException
	{
		final TextFile file = testFile();
		assert !file.getFilePath().exists() : "The test file not expected to exist yet";

		file.save("file content");

		Assert.assertTrue(file.getFilePath().exists());
	}

	@Test
	public void testSavePersistsTheContent() throws IOException
	{
		testFile().save("file content");
		final String content = testFile().read();

		Assert.assertEquals("file content", content);
	}

	@Test
	public void testSaveReplacesPreviousContent() throws IOException
	{
		final TextFile file = testFile();
		file.save("original content");
		file.save("new content");

		Assert.assertEquals("new content", file.read());
	}

	@Test
	public void testDeleteDoesNothingIfTheFileWasNeverSaved() throws IOException
	{
		final TextFile file = testFile();
		assert !file.getFilePath().exists() : "Test file should not exist on the file system";

		file.delete();

		Assert.assertFalse(file.getFilePath().exists());
	}

	@Test
	public void testDeleteRemovesAPreviouslySavedFile() throws IOException
	{
		final TextFile file = testFile();
		file.save("this saves the file on the file system");
		assert file.getFilePath().exists() : "Test file should exist on the file system";

		file.delete();

		Assert.assertFalse(file.getFilePath().exists());
	}

	@Test
	public void testReadReturnsEmptyTextBeforeTheFileWasEverSaved() throws IOException
	{
		Assert.assertEquals("", testFile().read());
	}

	@Test
	public void testAppendAddsToPreviousContentAndDoesNotReplaceIt() throws IOException
	{
		final TextFile file = testFile();
		file.append("this is ");
		file.append("file content");

		Assert.assertEquals("this is file content", file.read());
	}

	@Test
	public void testAppendLineAddsLineOfTextToThePreviousContent() throws IOException
	{
		final TextFile file = testFile();
		file.appendLine("line 1");
		file.appendLine("line 2");

		Assert.assertEquals("line 1" + LS + "line 2" + LS, file.read());
	}

	@Test
	public void testReadLineReturnsTextOnTheLineSpecifiedByItsNumber() throws IOException
	{
		testFile().appendLine("line 1").appendLine("line 2").appendLine("line 3");
		Assert.assertEquals("line 3", testFile().readLine(3));
	}

	@Test
	public void testReadLineReturnsNullForLineNumber0() throws IOException
	{
		testFile().save("line 1");
		Assert.assertNull(testFile().readLine(0));
	}

	@Test
	public void testReadLineReturnsNullWhenLineNumberExceedsNumberOfLinesInTheFile() throws IOException
	{
		testFile().appendLine("line 1");
		Assert.assertNull(testFile().readLine(2));
	}

	@Test
	public void testReadLineReturnsNullWhenNothingHasBeenSavedIntoTheFileYet() throws IOException
	{
		Assert.assertNull(testFile().readLine(1));
	}

	@Test
	public void testReadLineTreatsTextWithoutLineBreaksAsSingleLine() throws IOException
	{
		testFile().save("line 1");
		Assert.assertEquals("line 1", testFile().readLine(1));
	}

	@Test
	public void testReadLineReturnsEmptyStringForLinesWithNoTextPresent() throws IOException
	{
		testFile().save(LS);
		Assert.assertEquals("", testFile().readLine(1));
	}

	@Test
	public void testToStringContainsTheFilePath()
	{
		final TextFile file = testFile();
		Assert.assertTrue(file.toString().contains(file.getPath()));
	}

	@Test
	public void testFilesAreEqualWhenTheyAreBasedOnTheSamePath()
	{
		final TextFile aFile = new TextFile(TMP_DIR + FILE_NAME);
		final TextFile anotherFile = new TextFile(TMP_DIR, FILE_NAME);

		Assert.assertTrue(aFile.equals(anotherFile));
	}

	@Test
	public void testFilesAreNotEqualWhenTheyAreNotOfTheSameClass()
	{
		final TextFile textFile = new TextFile(TMP_DIR, FILE_NAME);
		final File ioFile = new File(TMP_DIR, FILE_NAME);

		Assert.assertFalse(textFile.equals(ioFile));
	}

	@Test
	public void testFilesAreNotEqualWhenTheirPathesAreDifferent()
	{
		final TextFile aFile = new TextFile(TMP_DIR, "file1.txt");
		final TextFile anotherFile = new TextFile(TMP_DIR, "file2.txt");

		Assert.assertFalse(aFile.equals(anotherFile));
	}

	@Test
	public void testHashCodeIsTheSameWhenFilesAreEqual()
	{
		final TextFile aFile = new TextFile(TMP_DIR + FILE_NAME);
		final TextFile anotherFile = new TextFile(TMP_DIR, FILE_NAME);

		Assert.assertTrue(aFile.hashCode() == anotherFile.hashCode());
	}

	@Test
	public void testHashCodeIsDifferentWhenFilesAreNotEqual()
	{
		final TextFile aFile = new TextFile(TMP_DIR, "file1.txt");
		final TextFile anotherFile = new TextFile(TMP_DIR, "file2.txt");

		Assert.assertFalse(aFile.hashCode() == anotherFile.hashCode());
	}

	private TextFile testFile()
	{
		return new TextFile(TMP_DIR, FILE_NAME);
	}

	@Test
	public void testCreatesNonExistingPathForTheFileName() throws IOException
	{
		final TextFile file = new TextFile(TMP_DIR, "junit/test/file.txt");
		assert !file.getFilePath().getParentFile().exists() : "directory should not exist for this test";

		file.save("some text to write");
		Assert.assertTrue(file.getFilePath().exists());
	}
}
