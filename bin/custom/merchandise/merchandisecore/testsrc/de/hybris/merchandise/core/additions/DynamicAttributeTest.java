package de.hybris.merchandise.core.additions;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DynamicAttributeTest extends ServicelayerTransactionalTest
{

	@Resource
	private ModelService modelService;

	@Test
	public void testCustomerIsInternal()
	{
		final CustomerModel customerEx = new CustomerModel();
		customerEx.setUid("customer@external.com");
		customerEx.setName("ex");
		final CustomerModel customerInternalA = new CustomerModel();
		customerInternalA.setUid("customerA@hybris.com");
		customerInternalA.setName("intA");
		final CustomerModel customerInternalB = new CustomerModel();
		customerInternalB.setUid("customerB@hybris.de");
		customerInternalB.setName("intB");
		modelService.save(customerEx);
		modelService.save(customerInternalA);
		modelService.save(customerInternalB);
		assertEquals(Boolean.FALSE, customerEx.getIsInternal());
		assertEquals(Boolean.TRUE, customerInternalA.getIsInternal());
		assertEquals(Boolean.TRUE, customerInternalB.getIsInternal());
	}
}