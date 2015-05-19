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
package de.hybris.platform.webservices;

import static de.hybris.platform.webservices.processor.RequestProcessor.RequestType.DELETE;
import static de.hybris.platform.webservices.processor.RequestProcessor.RequestType.GET;
import static de.hybris.platform.webservices.processor.RequestProcessor.RequestType.PUT;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import de.hybris.platform.webservices.processchain.RequestProcessChain;
import de.hybris.platform.webservices.processchain.ConfigurableRequestProcessChain.RequestExecution;
import de.hybris.platform.webservices.processchain.impl.DefaultRequestProcessChain;
import de.hybris.platform.webservices.processor.RequestProcessor;
import de.hybris.platform.webservices.processor.RequestProcessor.RequestType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


public class ProcessChainResourceTest
{

	private final Object dummyDto = new Object();

	private DefaultRequestProcessChain chain;
	private RequestExecution executor;
	private AbstractResponseBuilder resourceBuilder;
	private RequestProcessor firstProcessor;
	private RequestProcessor secondProcessor;

	@Before
	public void setUp()
	{
		chain = new DefaultRequestProcessChain();
		executor = createMock(RequestExecution.class);
		resourceBuilder = createMock(AbstractResponseBuilder.class);
		firstProcessor = createMock(RequestProcessor.class);
		secondProcessor = createMock(RequestProcessor.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullProcessors() //NOPMD
	{
		chain.setProcessors(null);
	}

	@Test(expected = IllegalStateException.class)
	public void testExecutionWithEmptyProcessors()
	{
		chain.setProcessors(Collections.EMPTY_LIST);
		chain.configure(null, null, null, executor);

		executor.execute(resourceBuilder);
		expectLastCall().once();
		replay(executor, resourceBuilder);

		chain.doProcess();

		verify(executor, resourceBuilder);
	}

	@Test
	public void testExecutionWithOneProcessor()
	{
		final RequestProcessor proc1 = new ChainingRequestProcessor();
		chain.setProcessors(Collections.singletonList(proc1));
		chain.configure(GET, dummyDto, resourceBuilder, executor);

		executor.execute(resourceBuilder);
		expectLastCall().once();
		replay(executor, resourceBuilder);

		chain.doProcess();

		verify(executor, resourceBuilder);
	}

	@Test
	public void testExecutionWithManyProcessors()
	{
		final RequestProcessor proc1 = new ChainingRequestProcessor();
		final RequestProcessor proc2 = new ChainingRequestProcessor();
		final RequestProcessor proc3 = new ChainingRequestProcessor();
		chain.setProcessors(Arrays.asList(new RequestProcessor[]
		{ proc1, proc2, proc3 }));
		chain.configure(GET, dummyDto, resourceBuilder, executor);

		executor.execute(resourceBuilder);
		expectLastCall().once();
		replay(executor, resourceBuilder);

		chain.doProcess();

		verify(executor, resourceBuilder);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExceptionInExecution()
	{
		final RequestProcessor proc1 = new ChainingRequestProcessor();
		chain.setProcessors(Arrays.asList(new RequestProcessor[]
		{ proc1 }));

		executor.execute(resourceBuilder);
		expectLastCall().andThrow(new IllegalArgumentException()).once();
		chain.configure(GET, dummyDto, resourceBuilder, executor);
		replay(executor, resourceBuilder);

		chain.doProcess();

		verify(executor, resourceBuilder);
	}

	@Test
	public void testFirstProcessorCalled()
	{
		chain.setProcessors(Collections.singletonList(firstProcessor));
		chain.configure(GET, null, resourceBuilder, executor);

		firstProcessor.doProcess(GET, null, resourceBuilder, chain);
		expectLastCall();
		replay(firstProcessor, executor, resourceBuilder);

		chain.doProcess();

		verify(firstProcessor, executor, resourceBuilder);
	}

	@Test
	public void testSecondProcessorCalled()
	{
		chain.setProcessors(Arrays.asList(new ChainingRequestProcessor(), secondProcessor));
		chain.configure(GET, dummyDto, resourceBuilder, executor);

		secondProcessor.doProcess(GET, dummyDto, resourceBuilder, chain);
		expectLastCall();

		replay(secondProcessor, executor, resourceBuilder);

		chain.doProcess();

		verify(secondProcessor, executor, resourceBuilder);
	}

	@Test
	public void testExecutionDeleteOnlyOnDelete()
	{
		final RequestProcessor proc = new TestDeleteOnlyProcessor();
		chain.setProcessors(Collections.singletonList(proc));
		chain.configure(DELETE, dummyDto, resourceBuilder, executor);

		executor.execute(resourceBuilder);
		expectLastCall().once();
		replay(executor, resourceBuilder);

		chain.doProcess();

		verify(executor, resourceBuilder);
	}

	@Test
	public void testExecutionDeleteOnlyOnNonDelete()
	{
		final RequestProcessor proc = new TestDeleteOnlyProcessor();
		chain.setProcessors(Collections.singletonList(proc));
		chain.configure(PUT, dummyDto, resourceBuilder, executor);

		replay(executor, resourceBuilder);

		chain.doProcess();

		verify(executor, resourceBuilder);
	}


	/**
	 * Test checking order call of before after default execution
	 */
	@Test
	public void testFewProcssorsExecutionBeforeAfter()
	{

		final Stack<String> callStack = new Stack<String>();
		final Stack<String> expectedcallStack = new Stack<String>();

		expectedcallStack.add("before");
		expectedcallStack.add("default");
		expectedcallStack.add("after");


		final RequestProcessor proc1 = new PassThroughBeforeAfterProcessor(callStack);
		final RequestProcessor proc2 = new PassThroughProcessor();


		chain.setProcessors(Arrays.asList(new RequestProcessor[]
		{ proc1, proc2 }));
		//
		final RequestExecution executor = new RequestExecution()
		{
			@Override
			public void execute(final AbstractResponseBuilder result)
			{
				//default execution
				callStack.add("default");
			}
		};
		//
		chain.configure(RequestType.POST, new Object(), resourceBuilder, executor);
		chain.doProcess();

		Assert.assertEquals("expected call sequence should be [" + expectedcallStack + "]", expectedcallStack, callStack);

	}

	/**
	 * Test checking order call of before after default execution
	 */
	@Test
	public void testCheckBeforeAfterDefaultExecution()
	{

		final Stack<String> callStack = new Stack<String>();
		final Stack<String> expectedcallStack = new Stack<String>();

		//create expected stack
		expectedcallStack.add("before");
		expectedcallStack.add("default");
		expectedcallStack.add("after");

		final RequestProcessor proc1 = new PassThroughProcessor();
		final RequestProcessor proc2 = new PassThroughBeforeAfterProcessor(callStack);

		chain.setProcessors(Arrays.asList(new RequestProcessor[]
		{ proc1, proc2 }));
		//
		//
		final RequestExecution executor = new RequestExecution()
		{
			@Override
			public void execute(final AbstractResponseBuilder result)
			{
				//default execution
				callStack.add("default");
			}
		};
		//
		chain.configure(RequestType.POST, new Object(), resourceBuilder, executor);
		chain.doProcess();

		Assert.assertEquals("expected call sequence should be [" + expectedcallStack + "]", expectedcallStack, callStack);

	}


	/**
	 * Test checking order call of before after default execution
	 */
	@Test
	public void testCheckBeforeAfterDefaultExecutionCalledTwice()
	{

		final Stack<String> callStack = new Stack<String>();
		final Stack<String> expectedcallStack = new Stack<String>();

		//create expected stack
		expectedcallStack.add("before");
		expectedcallStack.add("before");
		expectedcallStack.add("default");
		expectedcallStack.add("after");
		expectedcallStack.add("after");

		final RequestProcessor proc1 = new PassThroughBeforeAfterProcessor(callStack);
		final RequestProcessor proc2 = new PassThroughProcessor();
		final RequestProcessor proc3 = new PassThroughBeforeAfterProcessor(callStack);


		chain.setProcessors(Arrays.asList(new RequestProcessor[]
		{ proc1, proc2, proc3 }));
		//
		final RequestExecution executor = new RequestExecution()
		{
			@Override
			public void execute(final AbstractResponseBuilder resourceBuilder)
			{
				//default execution
				callStack.add("default");
			}
		};
		//
		chain.configure(RequestType.POST, new Object(), resourceBuilder, executor);
		chain.doProcess();

		Assert.assertEquals("expected call sequence should be [" + expectedcallStack + "]", expectedcallStack, callStack);

	}

	/**
	 * Test checking order call of before after default execution
	 */
	@Test
	public void testCheckBeforeAfterWithoutDefault()
	{

		final Stack<String> callStack = new Stack<String>();
		final Stack<String> expectedcallStack = new Stack<String>();

		//create expected stack
		expectedcallStack.add("before");
		expectedcallStack.add("after");

		final RequestProcessor proc1 = new PassThroughBeforeAfterProcessor(callStack);
		final RequestProcessor proc2 = new AbortProcessor();
		final RequestProcessor proc3 = new PassThroughBeforeAfterProcessor(callStack);


		chain.setProcessors(Arrays.asList(new RequestProcessor[]
		{ proc1, proc2, proc3 }));
		//
		final RequestExecution executor = new RequestExecution()
		{
			@Override
			public void execute(final AbstractResponseBuilder resourceBuilder)
			{
				//default execution
				callStack.add("default");
			}
		};
		//
		chain.configure(RequestType.POST, new Object(), resourceBuilder, executor);
		chain.doProcess();

		Assert.assertEquals("expected call sequence should be [" + expectedcallStack + "]", expectedcallStack, callStack);

	}

	/**
	 * Test checking order call of before after default execution
	 */
	@Test
	public void testCheckBeforeAfterTwiceWithoutDefault()
	{

		final Stack<String> callStack = new Stack<String>();
		final Stack<String> expectedcallStack = new Stack<String>();

		//create expected stack
		expectedcallStack.add("before");
		expectedcallStack.add("before");
		expectedcallStack.add("after");
		expectedcallStack.add("after");

		final RequestProcessor proc1 = new PassThroughBeforeAfterProcessor(callStack);
		final RequestProcessor proc2 = new PassThroughBeforeAfterProcessor(callStack);
		final RequestProcessor proc3 = new AbortProcessor();



		chain.setProcessors(Arrays.asList(new RequestProcessor[]
		{ proc1, proc2, proc3 }));
		//
		final RequestExecution executor = new RequestExecution()
		{
			@Override
			public void execute(final AbstractResponseBuilder resourceBuilder)
			{
				//default execution
				callStack.add("default");
			}
		};
		//
		chain.configure(RequestType.POST, new Object(), resourceBuilder, executor);
		chain.doProcess();

		Assert.assertEquals("expected call sequence should be [" + expectedcallStack + "]", expectedcallStack, callStack);

	}


	private static class TestDeleteOnlyProcessor implements RequestProcessor
	{
		@Override
		public void doProcess(final RequestProcessor.RequestType type, final Object dto,
				final AbstractResponseBuilder responseBuilder, final RequestProcessChain chain)
		{
			if (type == DELETE)
			{
				chain.doProcess();
			}
		}
	}

	private class ChainingRequestProcessor implements RequestProcessor
	{
		@Override
		public void doProcess(final RequestProcessor.RequestType type, final Object dto,
				final AbstractResponseBuilder responseBuilder, final RequestProcessChain chain)
		{
			Assert.assertEquals(dummyDto, dto);
			Assert.assertEquals(ProcessChainResourceTest.this.resourceBuilder, responseBuilder);
			Assert.assertEquals(ProcessChainResourceTest.this.chain, chain);
			chain.doProcess();
		}
	}


	/**
	 * Test processor which puts its before , after logic call into some external stack
	 */
	private class PassThroughBeforeAfterProcessor implements RequestProcessor
	{
		private final Stack<String> stack;

		PassThroughBeforeAfterProcessor(final Stack stack)
		{
			super();
			this.stack = stack;
		}

		@Override
		public void doProcess(final RequestType type, final Object dto, final AbstractResponseBuilder responseBuilder,
				final RequestProcessChain chain)
		{
			doBefore();
			chain.doProcess();
			doAfter();
		}

		private void doBefore()
		{
			stack.add("before");
		}

		private void doAfter()
		{
			stack.add("after");
		}
	}


	/**
	 * Test processor which returns result from next processor.
	 */
	private class PassThroughProcessor implements RequestProcessor
	{
		protected int callOccurencies = 0;

		@Override
		public void doProcess(final RequestType type, final Object dto, final AbstractResponseBuilder responseBuilder,
				final RequestProcessChain chain)
		{
			Assert.assertEquals("Each processor should be called once ", 1, ++callOccurencies);
			chain.doProcess();
		}

	}

	/**
	 * Test processor which returns result from next processor.
	 */
	private class AbortProcessor implements RequestProcessor
	{
		protected int callOccurencies = 0;

		@Override
		public void doProcess(final RequestType type, final Object dto, final AbstractResponseBuilder responseBuilder,
				final RequestProcessChain chain)
		{
			Assert.assertEquals("Each processor should be called once ", 1, ++callOccurencies);
		}

	}
}
