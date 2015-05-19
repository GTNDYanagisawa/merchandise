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
package de.hybris.platform.oci.jalo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.oci.constants.OciConstants;
import de.hybris.platform.oci.jalo.exception.OciException;
import de.hybris.platform.oci.jalo.interfaces.DefaultSAPProductList;
import de.hybris.platform.oci.jalo.interfaces.SAPProductList;
import de.hybris.platform.oci.jalo.utils.OutboundSection;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;


/**
 * Test for extension Oci
 * 
 */
public class OciTest extends HybrisJUnit4TransactionalTest
{
	@Test
	public void testOci()
	{
		// DOCTODO document why empty
	}

	@Test
	public void testDefaultSAPProduct() throws JaloInvalidParameterException, JaloSecurityException, JaloBusinessException,
			OciException
	{
		final JaloSession jalosession = JaloSession.getCurrentSession();
		jalosession.setAttribute(OciConstants.IS_OCI_LOGIN, Boolean.TRUE);

		final Map param = new HashMap();
		param.put(OutboundSection.Fields.CALLER, "~caller");
		param.put(OutboundSection.Fields.OK_CODE, "~okcode");
		param.put(OutboundSection.Fields.TARGET, "~target");
		param.put(OciConstants.OCI_VERSION, "4.0");

		final OutboundSection outboundSection = new OutboundSection(param, "hook_url");
		jalosession.setAttribute(OciConstants.OUTBOUND_SECTION_DATA, outboundSection);

		final Unit testunit = ProductManager.getInstance().createUnit("unittype", "unitcode");
		assertNotNull(testunit);
		final Product product1 = ProductManager.getInstance().createProduct("p1");
		assertNotNull(product1);
		product1.setDescription("description without html");
		product1.setName("p1 name");
		product1.setUnit(testunit);
		product1.setAttribute("deliveryTime", new Double(14.0));
		final List list = new ArrayList();
		list.add(product1);
		final SAPProductList spl1 = new DefaultSAPProductList(list);

		final StringBuilder org2 = new StringBuilder(OciManager.createOciBuyerButton(spl1, true));
		final StringBuilder vergl2 = new StringBuilder();
		vergl2.append("<form action=\"null\" method=\"post\" name=\"OrderForm\" accept-charset=\"UTF-8\" target=\"~target\">\n");
		vergl2.append("<input type=\"hidden\" id=\"~OkCode\" name=\"~OkCode\" value=\"~okcode\"/>\n");
		vergl2.append("<input type=\"hidden\" id=\"~Caller\" name=\"~Caller\" value=\"~caller\"/>\n");
		vergl2.append("<input type=\"hidden\" id=\"~Target\" name=\"~Target\" value=\"~target\"/>\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-DESCRIPTION[1]\" value=\"p1 name\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-MATNR[1]\" value=\"p1\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-MATGROUP[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-QUANTITY[1]\" value=\"1.000\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-UNIT[1]\" value=\"unitcode\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-PRICE[1]\" value=\"0.000\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-PRICEUNIT[1]\" value=\"1.000\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-CURRENCY[1]\" value=\"---\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-LEADTIME[1]\" value=\"14\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-VENDOR[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-VENDORMAT[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-MANUFACTCODE[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-MANUFACTMAT[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-CONTRACT[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-CONTRACT_ITEM[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-SERVICE[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_QUOTE_ID[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_QUOTE_ITEM[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_PRODUCT_ID[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-LONGTEXT_1:132[]\" value=\"description without html\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-ATTACHMENT[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-ATTACHMENT_TITLE[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-ATTACHMENT_PURPOSE[1]\" value=\"C\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_CATEGORY_ID[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_CATEGORY[1]\" value=\"\">\n");
		vergl2.append("\n");
		vergl2.append("<input type=\"submit\" value=\"SAP OCI Buyer\" id=submit1 name=submit1>\n");
		vergl2.append("</form>\n");
		assertEquals(org2.toString(), vergl2.toString());
	}

	@Test
	public void testDefaultSAPProductWithHTMLinProductDescription() throws JaloInvalidParameterException, JaloSecurityException,
			JaloBusinessException, OciException
	{
		final JaloSession jalosession = JaloSession.getCurrentSession();
		jalosession.setAttribute(OciConstants.IS_OCI_LOGIN, Boolean.TRUE);

		final Map param = new HashMap();
		param.put(OutboundSection.Fields.CALLER, "~caller");
		param.put(OutboundSection.Fields.OK_CODE, "~okcode");
		param.put(OutboundSection.Fields.TARGET, "~target");
		param.put(OciConstants.OCI_VERSION, "4.0");

		final OutboundSection outboundSection = new OutboundSection(param, "hook_url");
		jalosession.setAttribute(OciConstants.OUTBOUND_SECTION_DATA, outboundSection);

		final Unit testunit = ProductManager.getInstance().createUnit("unittype", "unitcode");
		assertNotNull(testunit);
		final Product product1 = ProductManager.getInstance().createProduct("p1");
		assertNotNull(product1);
		product1.setDescription("<b>description <i>with</i> html</b>");
		product1.setName("p1 name");
		product1.setUnit(testunit);
		product1.setAttribute("deliveryTime", new Double(14.0));
		final List list = new ArrayList();
		list.add(product1);
		final SAPProductList spl1 = new DefaultSAPProductList(list);

		final StringBuilder org2 = new StringBuilder(OciManager.createOciBuyerButton(spl1, true));
		final StringBuilder vergl2 = new StringBuilder();
		vergl2.append("<form action=\"null\" method=\"post\" name=\"OrderForm\" accept-charset=\"UTF-8\" target=\"~target\">\n");
		vergl2.append("<input type=\"hidden\" id=\"~OkCode\" name=\"~OkCode\" value=\"~okcode\"/>\n");
		vergl2.append("<input type=\"hidden\" id=\"~Caller\" name=\"~Caller\" value=\"~caller\"/>\n");
		vergl2.append("<input type=\"hidden\" id=\"~Target\" name=\"~Target\" value=\"~target\"/>\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-DESCRIPTION[1]\" value=\"p1 name\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-MATNR[1]\" value=\"p1\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-MATGROUP[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-QUANTITY[1]\" value=\"1.000\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-UNIT[1]\" value=\"unitcode\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-PRICE[1]\" value=\"0.000\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-PRICEUNIT[1]\" value=\"1.000\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-CURRENCY[1]\" value=\"---\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-LEADTIME[1]\" value=\"14\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-VENDOR[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-VENDORMAT[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-MANUFACTCODE[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-MANUFACTMAT[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-CONTRACT[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-CONTRACT_ITEM[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-SERVICE[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_QUOTE_ID[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_QUOTE_ITEM[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_PRODUCT_ID[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-LONGTEXT_1:132[]\" value=\"&lt;b&gt;description &lt;i&gt;with&lt;/i&gt; html&lt;/b&gt;\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-ATTACHMENT[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-ATTACHMENT_TITLE[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-ATTACHMENT_PURPOSE[1]\" value=\"C\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_CATEGORY_ID[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_CATEGORY[1]\" value=\"\">\n");
		vergl2.append("\n");
		vergl2.append("<input type=\"submit\" value=\"SAP OCI Buyer\" id=submit1 name=submit1>\n");
		vergl2.append("</form>\n");
		assertEquals(org2.toString(), vergl2.toString());
	}

	@Test
	public void testDefaultSAPProductWithGivenButtonHTMLcode() throws JaloInvalidParameterException, JaloSecurityException,
			JaloBusinessException, OciException
	{
		final String buttonlabel = "my very own submit button";
		final String myOwnButton = "<div>\n<button name=\"Submit\" type=\"button\"\nvalue=\""
				+ buttonlabel
				+ "\" onclick=\"submit1\">\n<p>\n<img src=\"test.gif\" width=\"106\" height=\"109\" alt=\"Logo\"><br>\n<b>Moo?</b>\n</p>\n</button>\n</div>\n";

		final JaloSession jalosession = JaloSession.getCurrentSession();
		jalosession.setAttribute(OciConstants.IS_OCI_LOGIN, Boolean.TRUE);

		final Map param = new HashMap();
		param.put(OutboundSection.Fields.CALLER, "~caller");
		param.put(OutboundSection.Fields.OK_CODE, "~okcode");
		param.put(OutboundSection.Fields.TARGET, "~target");
		param.put(OciConstants.OCI_VERSION, "4.0");

		final OutboundSection outboundSection = new OutboundSection(param, "hook_url");
		jalosession.setAttribute(OciConstants.OUTBOUND_SECTION_DATA, outboundSection);

		final Unit testunit = ProductManager.getInstance().createUnit("unittype", "unitcode");
		assertNotNull(testunit);
		final Product product1 = ProductManager.getInstance().createProduct("p1");
		assertNotNull(product1);
		product1.setDescription("description without html");
		product1.setName("p1 name");
		product1.setUnit(testunit);
		product1.setAttribute("deliveryTime", new Double(14.0));
		final List list = new ArrayList();
		list.add(product1);
		final SAPProductList spl1 = new DefaultSAPProductList(list);

		final StringBuilder org2 = new StringBuilder(OciManager.createOciBuyerButton(spl1, myOwnButton, true));
		final StringBuilder vergl2 = new StringBuilder();
		vergl2.append("<form action=\"null\" method=\"post\" name=\"OrderForm\" accept-charset=\"UTF-8\" target=\"~target\">\n");
		vergl2.append("<input type=\"hidden\" id=\"~OkCode\" name=\"~OkCode\" value=\"~okcode\"/>\n");
		vergl2.append("<input type=\"hidden\" id=\"~Caller\" name=\"~Caller\" value=\"~caller\"/>\n");
		vergl2.append("<input type=\"hidden\" id=\"~Target\" name=\"~Target\" value=\"~target\"/>\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-DESCRIPTION[1]\" value=\"p1 name\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-MATNR[1]\" value=\"p1\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-MATGROUP[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-QUANTITY[1]\" value=\"1.000\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-UNIT[1]\" value=\"unitcode\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-PRICE[1]\" value=\"0.000\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-PRICEUNIT[1]\" value=\"1.000\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-CURRENCY[1]\" value=\"---\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-LEADTIME[1]\" value=\"14\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-VENDOR[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-VENDORMAT[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-MANUFACTCODE[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-MANUFACTMAT[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-CONTRACT[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-CONTRACT_ITEM[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-SERVICE[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_QUOTE_ID[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_QUOTE_ITEM[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_PRODUCT_ID[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-LONGTEXT_1:132[]\" value=\"description without html\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-ATTACHMENT[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-ATTACHMENT_TITLE[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-ATTACHMENT_PURPOSE[1]\" value=\"C\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_CATEGORY_ID[1]\" value=\"\">\n");
		vergl2.append("<input type=\"hidden\" name=\"NEW_ITEM-EXT_CATEGORY[1]\" value=\"\">\n");
		vergl2.append("\n");
		vergl2.append(myOwnButton + "\n");
		vergl2.append("</form>\n");
		assertEquals(org2.toString(), vergl2.toString());
	}
}
