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
package de.hybris.platform.processengine.definition;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.processengine.model.DynamicProcessDefinitionModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;


@IntegrationTest
public class ScriptedProcessDefinitionTest extends ServicelayerBaseTest
{
	private static final String TEST_DEFINITION_CODE = "testProcessDefinition";
	private static final String TEST_PROCESS_CODE = "testProcessCode";

	@Resource
	private ModelService modelService;
	@Resource
	private BusinessProcessService businessProcessService;
	@Resource
	private ProcessDefinitionsCache processDefinitionsCache;

	@After
	public void tearDown()
	{
		processDefinitionsCache.clear();
	}

	@Test
	public void shouldBeAbleToRunBusinessProcessWithEmbeddedScript() throws InterruptedException
	{
		givenProcessDefinition(TEST_DEFINITION_CODE, //
				"<?xml version='1.0' encoding='utf-8'?>", //
				"<process xmlns='http://www.hybris.de/xsd/processdefinition' start='action0' name='testProcessDefinition'>", //
				"	<scriptAction id='action0'>", //
				"		<script type='javascript'>(function() { return 'itworks' })()</script>",//
				"		<transition name='itworks' to='success'/>", //
				"	</scriptAction>", //
				"	<end id='success' state='SUCCEEDED'>Everything was fine</end>", //
				"</process>");

		final BusinessProcessModel process = businessProcessService.startProcess(TEST_PROCESS_CODE, TEST_DEFINITION_CODE);

		waitFor(process);
		assertThat(process.getState()).isEqualTo(ProcessState.SUCCEEDED);
	}

	@Test
	public void shouldBeAbleToSetContextParameterFromScript() throws InterruptedException
	{
		givenProcessDefinition(TEST_DEFINITION_CODE, //
				"<?xml version='1.0' encoding='utf-8'?>", //
				"<process xmlns='http://www.hybris.de/xsd/processdefinition' start='action0' name='testProcessDefinition'>", //
				"<contextParameter name='testParameter' use='required' type='java.lang.String'/>", //
				"	<scriptAction id='action0'>", //
				"		<script type='javascript'>", //
				"			var parameter = process.contextParameters.get(0);", //
				"			parameter.setValue('changedFromScript');", //
				"			modelService.save(parameter);", //
				"			'itworks'", //
				"		</script>",//
				"		<transition name='itworks' to='success'/>", //
				"	</scriptAction>", //
				"	<end id='success' state='SUCCEEDED'>Everything was fine</end>", //
				"</process>");

		final BusinessProcessModel process = businessProcessService.startProcess(TEST_PROCESS_CODE, TEST_DEFINITION_CODE,
				ImmutableMap.<String, Object> of("testParameter", "initialValue"));

		waitFor(process);
		assertThat(process.getState()).isEqualTo(ProcessState.SUCCEEDED);
		assertThat(process.getContextParameters()).hasSize(1);
		final BusinessProcessParameterModel testParameter = process.getContextParameters().iterator().next();
		assertThat(testParameter).isNotNull();
		assertThat(testParameter.getName()).isNotNull().isEqualTo("testParameter");
		assertThat(testParameter.getValue()).isNotNull().isEqualTo("changedFromScript");
	}

	@Test
	public void processShouldPickUpLatestDefinition() throws InterruptedException
	{
		final String definitionTemplate = "<?xml version='1.0' encoding='utf-8'?>" + //
				"<process xmlns='http://www.hybris.de/xsd/processdefinition' start='waitSomeTime' name='testProcessDefinition'>" + //
				"<contextParameter name='testParameter' use='required' type='java.lang.String'/>" + //
				"	<scriptAction id='waitSomeTime'>" + //
				"		<script type='javascript'>" + //
				"			java.lang.Thread.sleep(2000);" + //
				"			'OK'" + //
				"		</script>" + //
				"		<transition name='OK' to='updateParameter'/>" + //
				"	</scriptAction>" + //
				"	<scriptAction id='updateParameter'>" + //
				"		<script type='javascript'>" + //
				"			var parameter = process.contextParameters.get(0);" + //
				"			parameter.setValue('changedFromScriptVersion%d');" + //
				"			modelService.save(parameter);" + //
				"			'OK'" + //
				"		</script>" + //
				"		<transition name='OK' to='success'/>" + //
				"	</scriptAction>" + //
				"	<end id='success' state='SUCCEEDED'>Everything was fine</end>" + //
				"</process>";

		final int numberOfProcesses = 100;
		final DynamicProcessDefinitionModel processDefinition = givenProcessDefinition(TEST_DEFINITION_CODE,
				String.format(definitionTemplate, Integer.valueOf(0)));


		for (int i = 0; i < numberOfProcesses; i++)
		{
			processDefinition.setContent(String.format(definitionTemplate, Integer.valueOf(i)));
			modelService.save(processDefinition);
			businessProcessService.startProcess(TEST_PROCESS_CODE + i, TEST_DEFINITION_CODE,
					ImmutableMap.<String, Object> of("testParameter", "initialValue"));
		}

		for (int i = 0; i < numberOfProcesses; i++)
		{
			final BusinessProcessModel process = businessProcessService.getProcess(TEST_PROCESS_CODE + i);
			waitFor(process);
			assertThat(process).isNotNull();
			assertThat(process.getState()).isEqualTo(ProcessState.SUCCEEDED);
			assertThat(process.getContextParameters()).hasSize(1);
			final BusinessProcessParameterModel testParameter = process.getContextParameters().iterator().next();
			assertThat(testParameter).isNotNull();
			assertThat(testParameter.getName()).isNotNull().isEqualTo("testParameter");
			assertThat(testParameter.getValue()).isNotNull().isEqualTo(
					String.format("changedFromScriptVersion%s", Integer.valueOf(i)));
		}
	}

	private DynamicProcessDefinitionModel givenProcessDefinition(final String definitionCode, final String... lines)
	{
		final DynamicProcessDefinitionModel result = modelService.create(DynamicProcessDefinitionModel.class);
		result.setContent(Joiner.on("\n").join(lines));
		result.setCode(definitionCode);
		modelService.save(result);
		return result;
	}

	private void waitFor(final BusinessProcessModel model) throws InterruptedException
	{
		while (model.getState() == ProcessState.RUNNING)
		{
			modelService.refresh(model);
		}
	}
}
