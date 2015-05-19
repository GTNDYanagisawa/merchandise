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
 package de.hybris.platform.lucenesearch.jalo;

import static de.hybris.platform.testframework.Assert.list;
import static de.hybris.platform.testframework.Assert.set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class LuceneTest
{
	Directory directory;
	Document docA, docB, docC;

	@Before
	public void setUp() throws Exception
	{
		directory = new RAMDirectory();
		final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_40,
				new StandardAnalyzer(Version.LUCENE_40)).setOpenMode(OpenMode.CREATE_OR_APPEND);
		final IndexWriter writer = new IndexWriter(directory, indexWriterConfig);
		docA = new Document();
		docA.add(new Field("key", "a", Field.Store.YES, Field.Index.NOT_ANALYZED));
		docA.add(new Field("text", "text zum ersten document", Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(docA);
		docB = new Document();
		docB.add(new Field("key", "b", Field.Store.YES, Field.Index.NOT_ANALYZED));
		docB.add(new Field("text", "text zum zweiten document", Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(docB);
		docC = new Document();
		docC.add(new Field("key", "c", Field.Store.YES, Field.Index.NOT_ANALYZED));
		docC.add(new Field("text", "text zum dritten document", Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(docC);
		//writer.optimize();
		writer.close();
	}

	@Test
	public void testSearch() throws IOException, ParseException
	{
		assertTermSearch(list(docA), "ersten");
		assertTermSearch(set(docA, docB, docC), "text");
		assertTermSearch(list(), "a");
		assertTermSearch(list(), "c");
	}

	@Test
	public void testReindex() throws IOException
	{
		assertTermSearch(set(docA, docB, docC), "text");

		final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_40,
				new StandardAnalyzer(Version.LUCENE_40)).setOpenMode(OpenMode.APPEND);
		final IndexWriter changewriter = new IndexWriter(directory, indexWriterConfig);

		changewriter.deleteDocuments(new Term("key", "b"));

		final Document docB2 = new Document();
		docB2.add(new Field("key", "b", Field.Store.YES, Field.Index.NOT_ANALYZED));
		docB2.add(new Field("text", "neuer texxxt zum zweiten document", Field.Store.YES, Field.Index.ANALYZED));
		changewriter.addDocument(docB2);
		changewriter.close();
		assertTermSearch(set(docA, docB2, docC), "zum");
		assertTermSearch(set(docA, docC), "text");
		assertTermSearch(set(docB2), "texxxt");
	}

	private void assertTermSearch(final Collection documents, final String term) throws IOException
	{
		final Query query = new TermQuery(new Term("text", term));
		final IndexReader reader = IndexReader.open(directory);

		final IndexSearcher searcher = new IndexSearcher(reader);
		final TopDocs hits = searcher.search(query, Integer.MAX_VALUE);
		assertHits(documents, hits, searcher);
		reader.close();
	}

	public void assertHits(final Collection documents, final TopDocs hits, final IndexSearcher searcher) throws IOException
	{
		final StringBuilder message = new StringBuilder();
		message.append("expected <");
		for (final Iterator iter = documents.iterator(); iter.hasNext();)
		{
			final Document nextDocument = (Document) iter.next();
			message.append(nextDocument.get("key"));
			if (iter.hasNext())
			{
				message.append(", ");
			}
		}
		message.append("> but found <");

		for (int i = 0; i < hits.totalHits; i++)
		{
			if (i != 0)
			{
				message.append(", ");
			}
			message.append(searcher.doc(i).get("key"));
		}
		message.append(">");
		final String messageString = message.toString();
		assertEquals(messageString, documents.size(), hits.totalHits);
		final Set documentKeys = new HashSet();
		for (final Iterator iter = documents.iterator(); iter.hasNext();)
		{
			final Document nextDocument = (Document) iter.next();
			documentKeys.add(nextDocument.get("key"));
		}
		for (int i = 0; i < hits.totalHits; i++)
		{
			final int docId = hits.scoreDocs[i].doc;
			assertTrue(messageString, documentKeys.contains(searcher.doc(docId).get("key")));
			if (documents instanceof List)
			{
				final Document expected = (Document) ((List) documents).get(i);
				assertEquals(messageString, expected.get("key"), searcher.doc(i).get("key"));
			}
		}
	}
}
