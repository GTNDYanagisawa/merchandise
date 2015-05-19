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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoTable;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.BackendMessageMapper;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

@UnitTest
@SuppressWarnings("javadoc")
public class BackendMessageMapperTest extends
		SapordermanagmentBolSpringJunitTest {

	protected IMocksControl mc;

	JCoRecord mockRecord;
	JCoTable mockTable;

	@Override
	@Before
	public void setUp() {
		mc = EasyMock.createNiceControl();

	}

	protected BackendMessageMapperImpl cutForRules(final String xmlFileContent) {
		final MessageMappingRulesLoaderImpl rulesLoader = new MessageMappingRulesLoaderImpl();
		rulesLoader.setMessageMappingFileInputStream(new ByteArrayInputStream(
				xmlFileContent.getBytes()));

		final MessageMappingRulesContainerImpl rulesContainer = genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER);
		rulesContainer.setMessageMappingRulesLoader(rulesLoader);
		rulesContainer.messageMappingCacheAccess = new MockCacheAccess();
		rulesContainer.init();

		final BackendMessageMapperImpl backendMessageMapper = new BackendMessageMapperImpl();
		backendMessageMapper.messageMappingRulesContainer = rulesContainer;
		return backendMessageMapper;
	}

	@Test
	public void testSeverityScale() {

		assertTrue(BackendMessageMapperImpl
				.severityScale(BackendMessageMapperImpl.BAPI_RETURN_INFO) < BackendMessageMapperImpl
				.severityScale(BackendMessageMapperImpl.BAPI_RETURN_SUCCESS));

		assertTrue(BackendMessageMapperImpl
				.severityScale(BackendMessageMapperImpl.BAPI_RETURN_SUCCESS) < BackendMessageMapperImpl
				.severityScale(BackendMessageMapperImpl.BAPI_RETURN_WARNING));

		assertTrue(BackendMessageMapperImpl
				.severityScale(BackendMessageMapperImpl.BAPI_RETURN_WARNING) < BackendMessageMapperImpl
				.severityScale(BackendMessageMapperImpl.BAPI_RETURN_ERROR));

		assertTrue(BackendMessageMapperImpl
				.severityScale(BackendMessageMapperImpl.BAPI_RETURN_ERROR) < BackendMessageMapperImpl
				.severityScale(BackendMessageMapperImpl.BAPI_RETURN_ABORT));
	}

	@Test
	public void testTypeConverionABAP2Java() {
		assertEquals(Message.INITIAL,
				BackendMessageMapperImpl.msgTypeBe2Java("X"));
		assertEquals(Message.SUCCESS,
				BackendMessageMapperImpl.msgTypeBe2Java("S"));
		assertEquals(Message.WARNING,
				BackendMessageMapperImpl.msgTypeBe2Java("W"));
		assertEquals(Message.INFO, BackendMessageMapperImpl.msgTypeBe2Java("I"));
		assertEquals(Message.ERROR,
				BackendMessageMapperImpl.msgTypeBe2Java("E"));
		assertEquals(Message.ERROR,
				BackendMessageMapperImpl.msgTypeBe2Java("A"));
	}

	protected JCoRecord mockedRecordForKey(final TechKey key) {
		return BackendMessageTest.mockRecord(mc, "W", "ZVXX", "999",
				new String[] { "a1", "a2", "a3", "a4" },
				BackendMessage.FIELDS.TEXT[0], "text",
				BackendMessage.FIELDS.REF_TECH_KEY[0], key);
	}

	@Test
	public void testCreateBackendMessage() throws BackendException {
		final TechKey key = TechKey.generateKey();
		mockRecord = BackendMessageTest.mockRecord(mc, "E", "ZVXX", "999",
				new String[] { "a1", "a2", "a3", "a4" },
				BackendMessage.FIELDS.TEXT[0], "text",
				BackendMessage.FIELDS.REF_TECH_KEY[0], key);
		mc.replay();

		final BackendMessageMapperImpl cut = new BackendMessageMapperImpl();

		final BackendMessage beMsg = cut.createBackendMessage(mockRecord);

		assertEquals("E", beMsg.getBeSeverity());
		assertEquals("ZVXX", beMsg.getBeClass());
		assertEquals("999", beMsg.getBeNumber());
		assertEquals("a1", beMsg.getVars()[0]);
		assertEquals("a2", beMsg.getVars()[1]);
		assertEquals("a3", beMsg.getVars()[2]);
		assertEquals("a4", beMsg.getVars()[3]);
	}

	@Test
	public void testPassMessage() throws BackendException {
		final TechKey key = TechKey.generateKey();
		mockRecord = BackendMessageTest.mockRecord(mc, "E", "ZVXX", "999",
				new String[] { "a1", "a2", "a3", "a4" },
				BackendMessage.FIELDS.TEXT[0], "text",
				BackendMessage.FIELDS.REF_TECH_KEY[0], key);
		mc.replay();

		final BackendMessageMapperImpl cut = new BackendMessageMapperImpl();
		final BackendMessage erpMsg = cut.createBackendMessage(mockRecord);
		final Message msg = cut.passMessage(erpMsg);

		assertNotNull(msg);
		assertEquals(Message.ERROR, msg.getType());
		assertEquals(key, msg.getRefTechKey());
		assertEquals("ZVXX 999", msg.getTechKey().toString());

		assertEquals("text", msg.getDescription());

		assertTrue(msg.getResourceKey() == null
				|| msg.getResourceKey().isEmpty());

		assertEquals("a1", msg.getResourceArgs()[0]);
		assertEquals("a2", msg.getResourceArgs()[1]);
		assertEquals("a3", msg.getResourceArgs()[2]);
		assertEquals("a4", msg.getResourceArgs()[3]);

		mc.verify();
	}

	@Test
	public void testTransformBeMessageByRule_ReplaceSeverity()
			throws BackendException, SAXException, IOException,
			SAPHybrisCacheException {
		final TechKey key = TechKey.generateKey();
		mockRecord = BackendMessageTest.mockRecord(mc, "W", "ZV2", "015",
				new String[] { "a1", "a2", "a3", "a4" },
				BackendMessage.FIELDS.TEXT[0], "text",
				BackendMessage.FIELDS.REF_TECH_KEY[0], key);
		mc.replay();

		final BackendMessageMapperImpl cut = new BackendMessageMapperImpl();
		final MessageMappingRulesContainerImpl rulesContainer = genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER);
		cut.messageMappingRulesContainer = rulesContainer;

		final Message msg = cut.transformMessageByRule(new BackendMessage(
				mockRecord),
				MessageMappingRulesParserTest.RULE_ZV_E_REPLACE_SEVERITY);

		assertNotNull(msg);

		assertEquals(Message.ERROR, msg.getType());

		assertEquals(key, msg.getRefTechKey());
		assertEquals("ZV2 015", msg.getTechKey().toString());

		assertEquals("text", msg.getDescription());
		assertTrue(msg.getResourceKey() == null
				|| msg.getResourceKey().isEmpty());

		assertEquals("a1", msg.getResourceArgs()[0]);
		assertEquals("a2", msg.getResourceArgs()[1]);
		assertEquals("a3", msg.getResourceArgs()[2]);
		assertEquals("a4", msg.getResourceArgs()[3]);

		mc.verify();
	}

	@Test
	public void testTransformBeMessageByRule_ReplaceKey()
			throws BackendException {
		final TechKey key = TechKey.generateKey();
		mockRecord = BackendMessageTest.mockRecord(mc, "W", "ZV2", "013",
				new String[] { "a1", "a2", "a3", "a4" },
				BackendMessage.FIELDS.TEXT[0], "text",
				BackendMessage.FIELDS.REF_TECH_KEY[0], key);
		mc.replay();

		final BackendMessageMapperImpl cut = new BackendMessageMapperImpl();
		final MessageMappingRulesContainerImpl rulesContainer = genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER);
		cut.messageMappingRulesContainer = rulesContainer;

		final Message msg = cut.transformMessageByRule(new BackendMessage(
				mockRecord),
				MessageMappingRulesParserTest.RULE_ZV_E_REPLACE_KEY);

		assertNotNull(msg);
		assertEquals("some.key4", msg.getResourceKey());
		assertTrue(msg.getDescription() == null
				|| msg.getDescription().isEmpty());

		assertEquals(Message.WARNING, msg.getType());

		assertEquals(key, msg.getRefTechKey());
		assertEquals("ZV2 013", msg.getTechKey().toString());

		assertEquals("a1", msg.getResourceArgs()[0]);
		assertEquals("a2", msg.getResourceArgs()[1]);
		assertEquals("a3", msg.getResourceArgs()[2]);
		assertEquals("a4", msg.getResourceArgs()[3]);

		mc.verify();
	}

	@Test
	public void testTransformBeMessageByRule_ReplaceSeverityAndKey()
			throws BackendException {
		final TechKey key = TechKey.generateKey();
		mockRecord = BackendMessageTest.mockRecord(mc, "W", "ZV2", "015",
				new String[] { "a1", "a2", "a3", "a4" },
				BackendMessage.FIELDS.TEXT[0], "text",
				BackendMessage.FIELDS.REF_TECH_KEY[0], key);
		mc.replay();

		final BackendMessageMapperImpl cut = new BackendMessageMapperImpl();
		final MessageMappingRulesContainerImpl rulesContainer = genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER);
		cut.messageMappingRulesContainer = rulesContainer;

		final Message msg = cut.transformMessageByRule(new BackendMessage(
				mockRecord), MessageMappingRulesParserTest.RULE_ZV_E_MAP_ALL);

		assertNotNull(msg);

		assertEquals(Message.ERROR, msg.getType());

		assertEquals("some.key4", msg.getResourceKey());
		assertTrue(msg.getDescription() == null
				|| msg.getDescription().isEmpty());

		assertEquals(key, msg.getRefTechKey());
		assertEquals("ZV2 015", msg.getTechKey().toString());

		assertEquals("a1", msg.getResourceArgs()[0]);
		assertEquals("a2", msg.getResourceArgs()[1]);
		assertEquals("a3", msg.getResourceArgs()[2]);
		assertEquals("a4", msg.getResourceArgs()[3]);

		mc.verify();
	}

	SalesDocument doc = null;
	Header header = null;
	Item[] items = new Item[3];

	protected void createSalesDocWith3Items() {

		header = (Header) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER);
		header.setTechKey(TechKey.generateKey());

		doc = (SalesDocument) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BO_ORDER);
		doc.setHeader(header);
		doc.setTechKey(header.getTechKey());

		for (int i = 0; i < items.length; i++) {
			items[i] = (Item) genericFactory
					.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
			items[i].setTechKey(TechKey.generateKey());
			doc.addItem(items[i]);
		}
	}

	@Test
	public void testFindReferencedBO_messageWithoutKey()
			throws BackendException {

		createSalesDocWith3Items();
		mockRecord = mockedRecordForKey(new TechKey(""));

		mc.replay();

		final BackendMessageMapperImpl cut = new BackendMessageMapperImpl();
		final BackendMessage beMes = cut.createBackendMessage(mockRecord);
		final BusinessObject bo = cut.findReferredBO(doc, beMes);
		assertNotNull(bo);
		assertSame(doc, bo);

		mc.verify();
	}

	@Test
	public void testFindReferencedBO_notFound() throws BackendException {

		createSalesDocWith3Items();
		mockRecord = mockedRecordForKey(TechKey.generateKey());

		mc.replay();

		final BackendMessageMapperImpl cut = new BackendMessageMapperImpl();
		final BackendMessage beMes = cut.createBackendMessage(mockRecord);
		final BusinessObject bo = cut.findReferredBO(doc, beMes);
		assertNotNull(bo);
		assertSame(doc, bo);

		mc.verify();
	}

	@Test
	public void testFindReferencedBO_foundRoot() throws BackendException {

		createSalesDocWith3Items();
		mockRecord = mockedRecordForKey(doc.getTechKey());

		mc.replay();

		final BackendMessageMapperImpl cut = new BackendMessageMapperImpl();
		final BackendMessage beMes = cut.createBackendMessage(mockRecord);
		final BusinessObject bo = cut.findReferredBO(doc, beMes);
		assertNotNull(bo);
		assertSame(doc, bo);

		mc.verify();
	}

	@Test
	public void testFindReferencedBO_foundChild() throws BackendException {

		createSalesDocWith3Items();
		mockRecord = mockedRecordForKey(items[2].getTechKey());
		mc.replay();

		final BackendMessageMapperImpl cut = new BackendMessageMapperImpl();
		final BackendMessage beMes = cut.createBackendMessage(mockRecord);
		final BusinessObject bo = cut.findReferredBO(doc, beMes);
		assertNotNull(bo);
		assertSame(items[2], bo);

		mc.verify();
	}

	@Test
	public void testMap_RuledBeMsgSeverityA() throws CommunicationException,
			BackendException {

		createSalesDocWith3Items();
		mockRecord = BackendMessageTest.mockRecord(mc, "A", "ZV2", "011",
				new String[] { "a1", "a2", "a3", "a4" },
				BackendMessage.FIELDS.TEXT[0], "text",
				BackendMessage.FIELDS.REF_TECH_KEY[0], doc.getTechKey());
		mc.replay();

		final BackendMessageMapperImpl cut = cutForRules(MessageMappingRulesParserTest.XML_FILE_CONTENT_NRULE);
		try {
			cut.map(doc, mockRecord);

			fail("exception not thrown");
		} catch (final BackendException e) {
			// right way
			mc.verify();
		}

	}

	@Test
	public void testTransformBeMessageByRule_Hide() throws BackendException {
		final TechKey key = TechKey.generateKey();
		mockRecord = BackendMessageTest.mockRecord(mc, "W", "ZV2", "015",
				new String[] { "a1", "a2", "a3", "a4" },
				BackendMessage.FIELDS.TEXT[0], "text",
				BackendMessage.FIELDS.REF_TECH_KEY[0], key);
		mc.replay();

		final BackendMessageMapperImpl cut = cutForRules(MessageMappingRulesParserTest.XML_FILE_CONTENT_NRULE);
		final Message msg = cut.transformMessageByRule(new BackendMessage(
				mockRecord), MessageMappingRulesParserTest.RULE_ZV_W_HIDE);

		assertNull(msg);

		mc.verify();
	}

	public void testMap_RuledTransformedWarning()
			throws CommunicationException, BackendException {

		createSalesDocWith3Items();

		mockRecord = BackendMessageTest.mockRecord(mc, "W", "ZV2", "015",
				new String[] { "a1", "a2", "a3", "a4" },
				BackendMessage.FIELDS.TEXT[0], "text",
				BackendMessage.FIELDS.REF_TECH_KEY[0], doc.getTechKey());
		mc.replay();

		final BackendMessageMapperImpl cut = cutForRules(MessageMappingRulesParserTest.XML_FILE_CONTENT_NRULE);
		final Message msg = cut.map(doc, mockRecord);

		assertNotNull(msg);
		assertEquals(Message.ERROR, msg.getType());
		assertEquals("some.key4", msg.getResourceKey());
		assertEquals(1, doc.getMessageList().size());
		assertTrue(doc.getMessageList().contains(msg));

		mc.verify();
	}

	@Test
	public void testMap_NotRuledPassedError() throws CommunicationException,
			BackendException {

		createSalesDocWith3Items();

		mockRecord = BackendMessageTest.mockRecord(mc, "E", "ZVXX", "999",
				new String[] { "a1", "a2", "a3", "a4" },
				BackendMessage.FIELDS.TEXT[0], "text",
				BackendMessage.FIELDS.REF_TECH_KEY[0], doc.getTechKey());
		// mockSalesDoc = mockSalesDocumentWith3Items(mc, key, new Item[0]);
		mc.replay();

		final BackendMessageMapperImpl cut = cutForRules(MessageMappingRulesParserTest.XML_FILE_CONTENT_NRULE);
		final Message msg = cut.map(doc, mockRecord);

		assertNotNull(msg);
		assertEquals(Message.ERROR, msg.getType());
		assertEquals("text", msg.getDescription());
		assertEquals(1, doc.getMessageList().size());
		assertTrue(doc.getMessageList().contains(msg));

		mc.verify();

	}

	@Test
	public void testMap_NoRuleFoundAndRemoveWarning()
			throws CommunicationException, BackendException {

		createSalesDocWith3Items();

		mockRecord = BackendMessageTest.mockRecord(mc, "W", "ZVXX", "999",
				new String[] { "a1", "a2", "a3", "a4" },
				BackendMessage.FIELDS.TEXT[0], "text",
				BackendMessage.FIELDS.REF_TECH_KEY[0], doc.getTechKey());

		mc.replay();

		final BackendMessageMapperImpl cut = cutForRules(MessageMappingRulesParserTest.XML_FILE_CONTENT_0RULE);
		final Message msg = cut.map(doc, mockRecord);

		assertNull(msg);
		assertEquals(0, doc.getMessageList().size());

		mc.verify();
	}

	@Test
	public void testMap_EmptyBeMessage() throws CommunicationException,
			BackendException {

		createSalesDocWith3Items();

		mockRecord = BackendMessageTest.mockRecord(mc, "", "", "",
				new String[] { "", "", "", "" }, BackendMessage.FIELDS.TEXT[0],
				"", BackendMessage.FIELDS.REF_TECH_KEY[0], doc.getTechKey());

		mc.replay();

		final BackendMessageMapperImpl cut = cutForRules(MessageMappingRulesParserTest.XML_FILE_CONTENT_NRULE);
		final Message msg = cut.map(doc, mockRecord);

		assertNull(msg);
		assertEquals(0, doc.getMessageList().size());

		mc.verify();
	}

	@Test
	public void testMap_null_null() throws CommunicationException,
			BackendException {

		createSalesDocWith3Items();

		mc.replay();

		final BackendMessageMapperImpl cut = cutForRules(MessageMappingRulesParserTest.XML_FILE_CONTENT_NRULE);
		cut.map(doc, (JCoRecord) null, (JCoTable) null);

		final MessageList ml = doc.getMessageList();
		assertEquals(0, ml.size());

		mc.verify();
	}

	@Test
	public void testBeanInitialization() throws CommunicationException {
		final BackendMessageMapper cut = (BackendMessageMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_BACKEND_MESSAGE_MAPPER);
		Assert.assertTrue(cut instanceof BackendMessageMapperImpl);
		Assert.assertNotNull(((BackendMessageMapperImpl) cut).messageMappingRulesContainer);
	}

}
