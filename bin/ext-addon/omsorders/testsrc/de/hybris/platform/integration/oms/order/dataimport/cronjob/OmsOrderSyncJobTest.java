package de.hybris.platform.integration.oms.order.dataimport.cronjob;

import static org.junit.Assert.*;

import com.hybris.oms.domain.order.UpdatedSinceList;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.integration.commons.hystrix.HystrixExecutable;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandConfiguration;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandFactory;
import de.hybris.platform.integration.commons.services.OndemandPreferenceSelectorService;
import de.hybris.platform.integration.oms.order.cronjob.model.OmsOrderSyncCronJobModel;
import de.hybris.platform.integration.oms.order.service.OmsOrderService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class OmsOrderSyncJobTest
{
	@InjectMocks
	private final OmsOrderSyncJob omsOrderSyncJob = new OmsOrderSyncJob();
	@Mock
	private OmsOrderService omsOrderService;
	@Mock
	private ModelService modelService;
	@Mock
	private TaskService taskService;
	@Mock
	private SessionService sessionService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private TenantService tenantService;
	@Mock
	private OndemandPreferenceSelectorService ondemandPreferenceSelectorService;
	@Mock
	private OndemandHystrixCommandFactory ondemandHystrixCommandFactory;
	@Mock
	private OndemandHystrixCommandConfiguration config;


	private OmsOrderSyncCronJobModel cronJobModel = null;
	private UpdatedSinceList<String> updatedSinceList = null;
	private TaskModel task = null;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		cronJobModel = new OmsOrderSyncCronJobModel();
		cronJobModel.setLastRemoteUpdateTime(new Date(0L));
		updatedSinceList = new UpdatedSinceList<String>();
		updatedSinceList.setDate(new Date(1L));
		final List<String> orderIdsList = new ArrayList<String>();
		orderIdsList.add("orderId1");
		orderIdsList.add("orderId2");
		updatedSinceList.setDelegatedList(orderIdsList);
		task = new TaskModel();

		BDDMockito.when(omsOrderService.getUpdatedOrderIds(new Date(0L))).thenReturn(updatedSinceList);
		BDDMockito.when(modelService.create(TaskModel.class)).thenReturn(task);

		omsOrderSyncJob.setHystrixCommandConfig(config);
		omsOrderSyncJob.setOndemandHystrixCommandFactory(ondemandHystrixCommandFactory);

		final OndemandHystrixCommandFactory.OndemandHystrixCommand command = Mockito
				.mock(OndemandHystrixCommandFactory.OndemandHystrixCommand.class);
		Mockito.when(
				ondemandHystrixCommandFactory.newCommand(Mockito.any(OndemandHystrixCommandConfiguration.class),
						Mockito.any(HystrixExecutable.class))).thenReturn(command);
		Mockito.when(command.execute()).thenReturn(updatedSinceList);
	}

	@Test
	public void testPerformSuccess() throws Exception
	{
		final PerformResult performResult = omsOrderSyncJob.perform(cronJobModel);
		assertEquals(performResult.getResult(), CronJobResult.SUCCESS);
		assertEquals(performResult.getStatus(), CronJobStatus.FINISHED);
	}

	@Test
	public void testAvoidProcessingDuplicatedOrders() throws Exception
	{
		updatedSinceList.clear();
		updatedSinceList.add("repeatedId1");
		updatedSinceList.add("repeatedId1");

		final PerformResult performResult = omsOrderSyncJob.perform(cronJobModel);

		BDDMockito.verify(taskService, BDDMockito.times(1)).scheduleTask(task);
		assertEquals(performResult.getResult(), CronJobResult.SUCCESS);
		assertEquals(performResult.getStatus(), CronJobStatus.FINISHED);

	}


}
