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
package de.hybris.platform.b2b;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BEmailService;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.testframework.SpringCustomContextLoader;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.JobPerformable;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.validation.services.ValidationService;
import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.springframework.context.support.GenericApplicationContext;


@Ignore
public abstract class B2BIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(B2BIntegrationTransactionalTest.class);

	@Resource
	public B2BCostCenterService b2bCostCenterService;
	@Resource
	public B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	@Resource
	public B2BCartService b2bCartService;
	@Resource
	public B2BOrderService b2bOrderService;
	@Resource
	public CommerceCheckoutService commerceCheckoutService;
	@Resource
	public CommonI18NService commonI18NService;
	@Resource
	public ModelService modelService;
	@Resource
	public KeyGenerator orderCodeGenerator;
	@Resource
	public UserService userService;
	@Resource
	public BusinessProcessService businessProcessService;
	@Resource
	public ProcessParameterHelper processParameterHelper;
	@Resource
	public CartFactory b2bCartFactory;
	@Resource
	public ProductService productService;
	@Resource
	public SessionService sessionService;
	@Resource
	public I18NService i18nService;
	@Resource
	public WorkflowActionService workflowActionService;
	@Resource
	public CalculationService calculationService;
	@Resource
	public SearchRestrictionService searchRestrictionService;
	@Resource
	public WorkflowProcessingService workflowProcessingService;
	@Resource
	public WorkflowService newestWorkflowService;
	@Resource
	public FlexibleSearchService flexibleSearchService;
	@Resource
	public WorkflowTemplateService workflowTemplateService;
	@Resource
	public ValidationService validationService;
	@Resource
	public BaseSiteService baseSiteService;
	@Resource
	public B2BEmailService b2bEmailService;


	protected B2BIntegrationTest()
	{
		try
		{
			new SpringCustomContextLoader(this.getClass()).loadApplicationContexts((GenericApplicationContext) Registry
					.getGlobalApplicationContext());
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public OrderModel createOrder(final int quantity) throws InvalidCartException, CalculationException
	{
		final String userId = "GC CEO";
		return this.createOrder(userId, quantity, OrderStatus.CREATED);
	}

	public OrderModel createOrder(final String uid, final int quantity, final OrderStatus status) throws InvalidCartException,
			CalculationException
	{
		final B2BCustomerModel loginUser = login(uid);
		return this.createOrder(loginUser, quantity, status, productService.getProductForCode("b2bproduct"));
	}

	public OrderModel createOrder(final UserModel user, final int quantity, final OrderStatus status, final ProductModel product)
			throws InvalidCartException, CalculationException
	{
		final CartModel cart = b2bCartService.getSessionCart();
		b2bCartService.addNewEntry(cart, product, quantity, null);
		setDefaultCostCenterOnEntries((B2BCustomerModel) user, cart);
		final AddressModel deliveryAddress = this.modelService.create(AddressModel.class);
		deliveryAddress.setOwner(user);
		deliveryAddress.setFirstname("Juergen");
		deliveryAddress.setLastname("Albertsen");
		deliveryAddress.setTown("Muenchen");

		final DebitPaymentInfoModel paymentInfo = this.modelService.create(DebitPaymentInfoModel.class);
		paymentInfo.setOwner(cart);
		paymentInfo.setBank("MeineBank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("Ich");
		paymentInfo.setCode("testPayment1");

		cart.setDeliveryAddress(deliveryAddress);
		cart.setPaymentAddress(deliveryAddress);
		cart.setPaymentInfo(paymentInfo);
		cart.setStatus(status);

		final B2BCommentModel b2BCommentModel = modelService.create(B2BCommentModel.class);
		b2BCommentModel.setCode("QuoteRequest");
		b2BCommentModel.setComment("Requesting 5% discount.");
		b2BCommentModel.setModifiedDate(new Date());
		this.modelService.save(b2BCommentModel);
		cart.setB2bcomments(Collections.singleton(b2BCommentModel));
		calculationService.calculate(cart);
		modelService.save(cart);

		LOG.info(String.format("Cart with %s products total %s%s", Integer.toString(quantity), this.commonI18NService
				.getCurrentCurrency().getSymbol(), cart.getTotalPrice()));

		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cart);
		parameter.setAddress(deliveryAddress);
		parameter.setIsDeliveryAddress(true);
		final CommerceOrderResult result = commerceCheckoutService.placeOrder(parameter);

		OrderModel order = result.getOrder();

		final AddressModel orderDeliveryAddress = order.getDeliveryAddress();
		assertNotNull("Delivery address", orderDeliveryAddress);
		assertEquals("Firstname", "Juergen", orderDeliveryAddress.getFirstname());
		assertEquals("Lastname", "Albertsen", orderDeliveryAddress.getLastname());
		assertEquals("Town", "Muenchen", orderDeliveryAddress.getTown());

		final DebitPaymentInfoModel orderPaymentInfo = (DebitPaymentInfoModel) order.getPaymentInfo();
		assertNotNull("Payment info", orderPaymentInfo);
		assertEquals("Account Number", "34434", orderPaymentInfo.getAccountNumber());
		assertEquals("Bank", "MeineBank", orderPaymentInfo.getBank());
		assertEquals("Bank ID Number", "1111112", orderPaymentInfo.getBankIDNumber());
		assertEquals("Ba Owner", "Ich", orderPaymentInfo.getBaOwner());
		return order;


	}

	protected void setDefaultCostCenterOnEntries(final B2BCustomerModel user, final CartModel cart)
	{
		// set costcenter on cart entry
		final List<B2BCostCenterModel> costCenters = b2bCostCenterService.getCostCentersForUnitBranch(user, cart.getCurrency());
		Assert.assertTrue(!costCenters.isEmpty());
		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		for (final AbstractOrderEntryModel abstractOrderEntryModel : entries)
		{
			abstractOrderEntryModel.setCostCenter(costCenters.get(0));

			this.modelService.save(abstractOrderEntryModel);
		}
	}

	public static void loadTestData() throws Exception
	{
		de.hybris.platform.servicelayer.ServicelayerTest.createCoreData();
		de.hybris.platform.servicelayer.ServicelayerTest.createDefaultCatalog();
		de.hybris.platform.catalog.jalo.CatalogManager.getInstance().createEssentialData(java.util.Collections.EMPTY_MAP, null);
		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		importCsv("/impex/essentialdata_2_b2bcommerce.impex", "UTF-8");
		importCsv("/impex/essentialdata_b2bapprovalprocess_usergroups.impex", "UTF-8");
		importCsv("/b2bapprovalprocess/test/testCatalog.csv", "UTF-8");
		importCsv("/b2bapprovalprocess/test/testdata.csv", "UTF-8");
		importCsv("/b2bapprovalprocess/test/email.impex", "UTF-8");
	}

	/**
	 * Sets the user in the session and updates the branch in session context.
	 * 
	 * @param userId
	 * @return A {@link de.hybris.platform.core.model.user.UserModel}
	 */
	public B2BCustomerModel login(final String userId)
	{
		final B2BCustomerModel user = userService.getUserForUID(userId, B2BCustomerModel.class);
		Assert.assertNotNull(userId + " user is null", user);
		login(user);
		return user;
	}

	public void login(final UserModel user)
	{
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("storetemplate"), false);
		userService.setCurrentUser(user);
		b2bUnitService.updateBranchInSession(sessionService.getCurrentSession(), user);
	}

	public void createJobPerformables()
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery("SELECT COUNT({" + ServicelayerJobModel.PK + "}) FROM {"
				+ ServicelayerJobModel._TYPECODE + "} WHERE {" + ServicelayerJobModel.SPRINGID + "}=?springid");
		fsq.setResultClassList(Arrays.asList(Integer.class));


		final Map<String, JobPerformable> beans = Registry.getApplicationContext().getBeansOfType(JobPerformable.class);
		for (final Map.Entry<String, JobPerformable> entry : beans.entrySet())
		{
			fsq.addQueryParameter("springid", entry.getKey());
			if (((Integer) flexibleSearchService.search(fsq).getResult().get(0)).intValue() == 0)
			{
				final ServicelayerJobModel servicelayerJobModel = modelService.create(ServicelayerJobModel.class);
				servicelayerJobModel.setCode(entry.getKey());
				servicelayerJobModel.setSpringId(entry.getKey());
				modelService.save(servicelayerJobModel);
			}
		}
	}
}
