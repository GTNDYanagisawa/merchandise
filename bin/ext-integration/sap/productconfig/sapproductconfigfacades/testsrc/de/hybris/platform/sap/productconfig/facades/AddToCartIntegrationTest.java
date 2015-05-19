/**
 * 
 */
package de.hybris.platform.sap.productconfig.facades;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartCalculationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImplTest.DummyPriceDataFactory;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationFacadeImpl;
import de.hybris.platform.sap.productconfig.facades.impl.CsticTypeMapperImpl;
import de.hybris.platform.sap.productconfig.facades.impl.UiTypeFinderImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ValueFormatTranslatorImpl;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;



@IntegrationTest
public class AddToCartIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource(name = "sapProductConfigFacade")
	private ConfigurationFacade productConfigurationFacade;

	@Resource(name = "sapProductConfigCartIntegrationFacade")
	private ConfigurationCartIntegrationFacade configCartIntegrationFacade;

	@Resource
	protected FlexibleSearchService flexibleSearchService;

	@Resource
	private CartService cartService;

	@Resource
	private ModelService modelService;

	@Resource
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	private KBKeyData kbKey;


	private final static String PRODUCT_CODE = "testProduct0";

	@Before
	public void setUp() throws Exception
	{

		Assert.assertNotNull("Test setup failed, configFacade is null", productConfigurationFacade);
		createCoreData();
		createDefaultCatalog();

		kbKey = new KBKeyData();
		kbKey.setProductCode(PRODUCT_CODE);
		kbKey.setKbName("YSAP_SIMPLE_POC");
		kbKey.setKbLogsys("ABC");
		kbKey.setKbVersion("123");
		final BaseSiteService baseSiteService = Mockito.mock(BaseSiteService.class);
		final BaseSiteModel baseSite = Mockito.mock(BaseSiteModel.class);

		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		((DefaultCommerceCartCalculationStrategy) commerceCartCalculationStrategy).setBaseSiteService(baseSiteService);

		final BaseStoreService baseStoreService = Mockito.mock(BaseStoreService.class);
		final CsticTypeMapper typeMapper = new CsticTypeMapperImpl();
		final UiTypeFinder uiTypeFinder = new UiTypeFinderImpl();
		typeMapper.setUiTypeFinder(uiTypeFinder);
		final ValueFormatTranslator valueFormatTranslater = new ValueFormatTranslatorImpl();
		typeMapper.setValueFormatTranslater(valueFormatTranslater);
		final ConfigPricingImpl configPicingFactory = new ConfigPricingImpl();
		typeMapper.setPricingFactory(configPicingFactory);
		final DummyPriceDataFactory dummyFactory = new DummyPriceDataFactory();
		configPicingFactory.setPriceDataFactory(dummyFactory);
		typeMapper.setBaseStoreService(baseStoreService);
		((ConfigurationFacadeImpl) productConfigurationFacade).setCsticTypeMapper(typeMapper);
		final BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setCatalogs(Collections.EMPTY_LIST);

		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);

	}

	@Test
	public void testAddToCart_xmlInDB() throws Exception
	{
		final ConfigurationData configData = productConfigurationFacade.getConfiguration(kbKey);
		final String cartItemKey = configCartIntegrationFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey);
		final SearchResult<Object> searchResult = flexibleSearchService
				.search("Select {pk},{externalConfiguration} from {cartentry} where {pk}='" + cartItemKey + "'");
		Assert.assertEquals(1, searchResult.getTotalCount());
		final CartEntryModel cartEntry = (CartEntryModel) searchResult.getResult().get(0);
		final String xml = cartEntry.getExternalConfiguration();
		final String externalConfigSource = Config.getString("sapproductconfigservices.externalConfiguration_fieldname",
				"externalConfiguration");
		modelService.getAttributeValue(cartEntry, externalConfigSource);
		System.out.println("ExternalConfig from DB: " + xml);

		// check that there is some parseable XML in DB as external configuration
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		final InputStream source = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		final Document doc = dBuilder.parse(source);
		assertNotNull(doc);
	}


	@Test
	public void testAddToCart_sameProductAddedTwice() throws CommerceCartModificationException
	{

		final ConfigurationData configData = productConfigurationFacade.getConfiguration(kbKey);
		final String cartItemKey1 = configCartIntegrationFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey1);
		final String cartItemKey2 = configCartIntegrationFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey2);

		Assert.assertFalse("expected new cart item, not same one!", cartItemKey1.equals(cartItemKey2));
		Assert.assertEquals("Adding same configurable product twice should lead two distinct entries in cart", 2, cartService
				.getSessionCart().getEntries().size());
	}

	@Test
	public void testAddToCart_update() throws CommerceCartModificationException
	{
		final ConfigurationData configData = productConfigurationFacade.getConfiguration(kbKey);
		final String cartItemKey1 = configCartIntegrationFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey1);
		configData.setCartItemPK(cartItemKey1);
		final String cartItemKey2 = configCartIntegrationFacade.addConfigurationToCart(configData);
		Assert.assertEquals("new cartItem created instead of updated of existing one", cartItemKey1, cartItemKey2);
		Assert.assertEquals("new cartItem created instead of updated of existing one", 1, cartService.getSessionCart().getEntries()
				.size());
	}

	@Test
	public void testAddToCart_updateRemovedProduct() throws CommerceCartModificationException
	{
		final ConfigurationData configData = productConfigurationFacade.getConfiguration(kbKey);
		final String cartItemKey1 = configCartIntegrationFacade.addConfigurationToCart(configData);
		Assert.assertNotNull(cartItemKey1);
		configData.setCartItemPK(cartItemKey1);
		final Map<Integer, Long> quantities = new HashMap();
		final AbstractOrderEntryModel cartItem1 = cartService.getSessionCart().getEntries().get(0);
		quantities.put(cartItem1.getEntryNumber(), Long.valueOf(0));
		cartService.updateQuantities(cartService.getSessionCart(), quantities);
		final String cartItemKey2 = configCartIntegrationFacade.addConfigurationToCart(configData);
		Assert.assertFalse("expected new cart item, not same one!", cartItemKey1.equals(cartItemKey2));
		Assert.assertEquals("there should be only one item in the cart", 1, cartService.getSessionCart().getEntries().size());
	}

}
