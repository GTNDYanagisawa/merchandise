package de.hybris.platform.sap.sapordermgmtservices.messagemappingcallback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.BackendMessage;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.math.BigDecimal;
import java.util.Locale;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultInternalNumberReplacementMsgMappingCallbackTest extends SapordermanagmentBolSpringJunitTest
{

	private final DefaultInternalNumberReplacementMsgMappingCallback classUnderTest = new DefaultInternalNumberReplacementMsgMappingCallback();
	ItemList items = new ItemListImpl();

	private final Item item = new ItemSalesDoc();
	private final String posnr = "000010";
	private final String product = "KD990KAP";

	@Override
	@Before
	public void setUp()
	{
		LocaleUtil.setLocale(new Locale("en"));

		classUnderTest.setGenericFactory(genericFactory);
		item.setProductId(product);
		item.setNumberInt(new BigDecimal(posnr).intValue());
		items.add(item);

		final ProductService productServiceMock = EasyMock.createNiceMock(ProductService.class);
		final ProductModel productModelMock = EasyMock.createNiceMock(ProductModel.class);
		EasyMock.expect(productServiceMock.getProductForCode(product)).andReturn(productModelMock);
		EasyMock.expect(productModelMock.getName(LocaleUtil.getLocale())).andReturn("Product Description");
		EasyMock.replay(productModelMock);
		EasyMock.replay(productServiceMock);
		classUnderTest.setProductService(productServiceMock);
		classUnderTest.getCart().setItemList(items);

	}


	@Test
	public void testProcess()
	{
		final BackendMessage message = new BackendMessage("E", "ID1", "123", null, null, posnr, null);
		classUnderTest.process(message);
		Assert.assertEquals("Product Description", message.getVars()[2]);
	}

	@Test
	public void testProcessNoMatchingItem()
	{

		final BackendMessage message = new BackendMessage("E", "ID1", "123", null, null, posnr, null);
		classUnderTest.getCart().setItemList(new ItemListImpl());
		assertFalse(classUnderTest.process(message));
	}

	@Test
	public void testGetId()
	{

		Assert.assertEquals(DefaultInternalNumberReplacementMsgMappingCallback.SAP_INTERNAL_NUMBER_REPLACEMENT_CALLBACK_ID,
				classUnderTest.getId());
	}

	@Test
	public void testGenericFactory()
	{
		assertNotNull(classUnderTest.getGenericFactory());
	}

	@Test
	public void tetGetCart()
	{
		final Basket cart = classUnderTest.getCart();
		assertNotNull(cart);
	}

	@Test
	public void testFindItem()
	{
		final Item itemInList = classUnderTest.findItem(items, posnr);
		assertEquals(this.item, itemInList);
	}

}
