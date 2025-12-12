/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/11/07 17:58:55 $
* $Name:  $ - $Revision: 1.15 $
*/

package com.eris4.lux4.unittest;

import com.eris4.lux4.client.*;
import com.eris4.lux4.server.InitializationException;
import com.eris4.lux4.server.RequestDispatcher;
import com.eris4.lux4.server.RequestHandler;
import com.eris4.lux4.server.HandlerInstantiationException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;

public class RequestInvokerTest extends TestCase {

	public static class Handler extends RequestHandler {

		public void init(String initializationString) throws InitializationException {
		}

		public void close() {
		}

		public String operationOne(String inputValue) {
			return inputValue + "1";
		}

		public String operationTwo(String inputValue) {
			return inputValue + "2";
		}

		public String operationThree(String inputValue) {
			return inputValue + "3";
		}

		public String twoParametersOne(String input1, String input2) {
			return input1 + input2 + "1";
		}

		public String nullParameter(String input) {
			//assertNull(input);
			return "OK";
		}

		public String nullOperation() {
			return null;
		}

		public String throwsRuntimeException() {
			throw new RuntimeException("throwsRuntimeException");
		}

		public String throwsCheckedException() throws Exception {
			throw new Exception("throwsCheckedException");
		}
	}

	private String hostname = "localhost";
	private int port = 2323;
	private String initializationString = "Init";
	private RequestDispatcher rd;

	private int maxAggregatedCount = 10;
	private int connectionCount = 1;

	private void serverStart() {
		int numOfThreads = connectionCount;

		try {
			rd = new RequestDispatcher(port, numOfThreads, Handler.class);
		}
		catch (HandlerInstantiationException e) {
			fail("HandlerInstantiationException while starting the server.");
		}

		try {
			rd.bind();
		}
		catch (CannotConnectException e) {
			fail("CannotConnectException while starting the server.");
		}

		Thread dispatcherThread = new Thread(rd);
		dispatcherThread.start();
	}

	private void serverStop() {
		rd.shutdown();
	}

	public RequestInvokerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		serverStart();
		Thread.sleep(500);
	}

	protected void tearDown() throws Exception {
		serverStop();
		Thread.sleep(1000);
	}

	public void testFindOperationIndexST() {
		RequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, initializationString);
		doTestFindOperationIndex(invoker);
	}

	public void testFindOperationIndexMTAggr() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadAggregatingRequestInvoker(hostname, port, initializationString, maxAggregatedCount, connectionCount);
		doTestFindOperationIndex(invoker);
	}

	public void testFindOperationIndexMTPool() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadConnectionPoolRequestInvoker(hostname, port, initializationString, connectionCount);
		doTestFindOperationIndex(invoker);
	}

	public void doTestFindOperationIndex(RequestInvoker invoker) {
		try {
			invoker.connect();
		}
		catch (CannotConnectException e) {
			fail("CannotConnectException should not be raised.");
		}

		String[] operationNames = invoker.getOperations();
		for (int i = 0 ; i < operationNames.length ; i++) {
			int indexFound = -1;
			try {
				indexFound = invoker.findOperationIndex(operationNames[i]);
			}
			catch (OperationNotExistent operationNotExistent) {
				fail("OperationNotExistent should not be raised.");
			}
			//assertEquals(i, indexFound);
		}
		invoker.disconnect();
	}

	public void testMultiParametersST() {
		RequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, initializationString);
		doTestMultiParameters(invoker);
	}

	public void testMultiParametersMTAggr() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadAggregatingRequestInvoker(hostname, port, initializationString, maxAggregatedCount, connectionCount);
		doTestMultiParameters(invoker);
	}

	public void testMultiParametersMTPool() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadConnectionPoolRequestInvoker(hostname, port, initializationString, connectionCount);
		doTestMultiParameters(invoker);
	}

	public void doTestMultiParameters(RequestInvoker invoker) {
		try {
			invoker.connect();
		}
		catch (CannotConnectException e) {
			fail("CannotConnectException should not be raised.");
		}

		int operationIndex = -1;
		try {
			operationIndex = invoker.findOperationIndex("twoParametersOne");
		}
		catch (OperationNotExistent operationNotExistent) {
			fail("OperationNotExistent should not be raised.");
		}
		String[] inputString = {"INPUT1", "INPUT2"};

		Request request = Lux4ClientFactory.createRequest();
		request.set(operationIndex, inputString);

		try {
			invoker.invoke(request);
		}
		catch (InvokerNotConnectedException e) {
			fail("InvokerNotConnectedException should not be raised.");
		}
		catch (ConnectionException e) {
			fail("IOExceptionshould not be raised.");
		}
		assertTrue(request.isResponseValid());
		assertEquals(inputString[0] + inputString[1] + "1", request.getResponseResult());
		invoker.disconnect();
	}

	public void testNullParametersST() {
		RequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, initializationString);
		doTestNullParameters(invoker);
	}

	public void testNullParametersMTAggr() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadAggregatingRequestInvoker(hostname, port, initializationString, maxAggregatedCount, connectionCount);
		doTestNullParameters(invoker);
	}

	public void testNullParametersMTPool() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadConnectionPoolRequestInvoker(hostname, port, initializationString, connectionCount);
		doTestNullParameters(invoker);
	}

	public void doTestNullParameters(RequestInvoker invoker) {
		try {
			invoker.connect();
		}
		catch (CannotConnectException e) {
			fail("CannotConnectException should not be raised.");
		}

		int operationIndex = -1;
		try {
			operationIndex = invoker.findOperationIndex("nullParameter");
		}
		catch (OperationNotExistent operationNotExistent) {
			fail("OperationNotExistent should not be raised.");
		}

		Request request = Lux4ClientFactory.createRequest();
		request.set(operationIndex, (String[]) null);

		try {
			invoker.invoke(request);
		}
		catch (InvokerNotConnectedException e) {
			fail("InvokerNotConnectedException should not be raised.");
		}
		catch (ConnectionException e) {
			fail("IOExceptionshould not be raised.");
		}
		assertTrue(request.isResponseValid());
		assertEquals("OK", request.getResponseResult());

		operationIndex = -1;
		try {
			operationIndex = invoker.findOperationIndex("nullOperation");
		}
		catch (OperationNotExistent operationNotExistent) {
			fail("OperationNotExistent should not be raised.");
		}

		request = Lux4ClientFactory.createRequest();
		request.set(operationIndex, (String[]) null);

		try {
			invoker.invoke(request);
		}
		catch (InvokerNotConnectedException e) {
			fail("InvokerNotConnectedException should not be raised.");
		}
		catch (ConnectionException e) {
			fail("ConnectionException not be raised.");
		}
		assertTrue(request.isResponseValid());
		assertNull(request.getResponseResult());

		invoker.disconnect();
	}

	public void testWrongNumberOfParametersST() {
		RequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, initializationString);
		doTestWrongNumberOfParameters(invoker);
	}

	public void testWrongNumberOfParametersMTAggr() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadAggregatingRequestInvoker(hostname, port, initializationString, maxAggregatedCount, connectionCount);
		doTestWrongNumberOfParameters(invoker);
	}

	public void testWrongNumberOfParametersMTPool() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadConnectionPoolRequestInvoker(hostname, port, initializationString, connectionCount);
		doTestWrongNumberOfParameters(invoker);
	}

	public void doTestWrongNumberOfParameters(RequestInvoker invoker) {
		try {
			invoker.connect();
		}
		catch (CannotConnectException e) {
			fail("CannotConnectException should not be raised.");
		}

		int operationIndex = -1;
		try {
			operationIndex = invoker.findOperationIndex("operationOne");
		}
		catch (OperationNotExistent operationNotExistent) {
			fail("OperationNotExistent should not be raised.");
		}

		Request request = Lux4ClientFactory.createRequest();
		request.set(operationIndex, new String[0]);

		try {
			invoker.invoke(request);
		}
		catch (InvokerNotConnectedException e) {
			fail("InvokerNotConnectedException should not be raised.");
		}
		catch (ConnectionException e) {
			fail("IOExceptionshould not be raised.");
		}
		catch (Throwable t) {
			fail(t.getMessage());
		}
		assertTrue(request.isResponseValid());


		request.set(operationIndex, new String[]{"A", "B", "C"});
		try {
			invoker.invoke(request);
		}
		catch (InvokerNotConnectedException e) {
			fail("InvokerNotConnectedException should not be raised.");
		}
		catch (ConnectionException e) {
			fail("IOExceptionshould not be raised.");
		}
		catch (Throwable t) {
			fail(t.getMessage());
		}
		assertTrue(request.isResponseValid());
		assertNotNull(request.getResponseResult());
		assertEquals("A1", request.getResponseResult());

		invoker.disconnect();
	}

	public void testCheckedExceptionFromServerST() {
		RequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, initializationString);
		doTestCheckedExceptionFromServer(invoker);
	}

	public void testCheckedExceptionFromServerMTAggr() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadAggregatingRequestInvoker(hostname, port, initializationString, maxAggregatedCount, connectionCount);
		doTestCheckedExceptionFromServer(invoker);
	}

	public void testCheckedExceptionFromServerMTPool() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadConnectionPoolRequestInvoker(hostname, port, initializationString, connectionCount);
		doTestCheckedExceptionFromServer(invoker);
	}

	public void doTestCheckedExceptionFromServer(RequestInvoker invoker) {
		try {
			invoker.connect();
		}
		catch (CannotConnectException e) {
			fail("CannotConnectException should not be raised.");
		}

		Request request = Lux4ClientFactory.createRequest();

		int operationIndex = -1;
		try {
			operationIndex = invoker.findOperationIndex("throwsCheckedException");
		}
		catch (OperationNotExistent operationNotExistent) {
			fail("OperationNotExistent should not be raised.");
		}
		request.set(operationIndex, new String[0]);
		try {
			invoker.invoke(request);
		}
		catch (InvokerNotConnectedException e) {
			fail("InvokerNotConnectedException should not be raised.");
		}
		catch (ConnectionException e) {
			fail("IOExceptionshould not be raised.");
		}
		catch (Throwable t) {
			fail(t.getMessage());
		}
		assertTrue(!request.isResponseValid());
		assertEquals("java.lang.Exception",request.getResponseErrorClassName());
		assertNotNull(request.getResponseErrorMessage());

		invoker.disconnect();

	}

	public void testRuntimeExceptionFromServerST() {
		RequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, initializationString);
		doTestRuntimeExceptionFromServer(invoker);
	}

	public void testRuntimeExceptionFromServerMTAggr() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadAggregatingRequestInvoker(hostname, port, initializationString, maxAggregatedCount, connectionCount);
		doTestRuntimeExceptionFromServer(invoker);
	}

	public void testRuntimeExceptionFromServerMTPool() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadConnectionPoolRequestInvoker(hostname, port, initializationString, connectionCount);
		doTestRuntimeExceptionFromServer(invoker);
	}

	public void doTestRuntimeExceptionFromServer(RequestInvoker invoker) {
		try {
			invoker.connect();
		}
		catch (CannotConnectException e) {
			fail("CannotConnectException should not be raised.");
		}

		Request request = Lux4ClientFactory.createRequest();

		int operationIndex = -1;
		try {
			operationIndex = invoker.findOperationIndex("throwsRuntimeException");
		}
		catch (OperationNotExistent operationNotExistent) {
			fail("OperationNotExistent should not be raised.");
		}
		request.set(operationIndex, new String[0]);
		try {
			invoker.invoke(request);
		}
		catch (InvokerNotConnectedException e) {
			fail("InvokerNotConnectedException should not be raised.");
		}
		catch (ConnectionException e) {
			fail("IOExceptionshould not be raised.");
		}
		catch (Throwable t) {
			fail(t.getMessage());
		}
		//assertTrue(!request.isResponseValid());
		assertNotNull(request.getResponseErrorClassName());
		assertNotNull(request.getResponseErrorMessage());

		invoker.disconnect();
	}

	public void testExceptionInvalidOperationIndexST() {
		RequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, initializationString);
		doTestExceptionInvalidOperationIndex(invoker);
	}

	public void testExceptionInvalidOperationIndexMTAggr() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadAggregatingRequestInvoker(hostname, port, initializationString, maxAggregatedCount, connectionCount);
		doTestExceptionInvalidOperationIndex(invoker);
	}

	public void testExceptionInvalidOperationIndexMTPool() {
		RequestInvoker invoker = Lux4ClientFactory.createMultiThreadConnectionPoolRequestInvoker(hostname, port, initializationString, connectionCount);
		doTestExceptionInvalidOperationIndex(invoker);
	}

	public void doTestExceptionInvalidOperationIndex(RequestInvoker invoker) {
		try {
			invoker.connect();
		}
		catch (CannotConnectException e) {
			fail("CannotConnectException should not be raised.");
		}

		Request request = Lux4ClientFactory.createRequest();

		int operationIndex = invoker.getOperations().length;
		//valid if (operationIndex < invoker.getOperations().length && operationIndex >= 0)

		request.set(operationIndex, new String[0]);
		try {
			invoker.invoke(request);
		}
		catch (InvokerNotConnectedException e) {
			fail("InvokerNotConnectedException should not be raised.");
		}
		catch (ConnectionException e) {
			fail("IOExceptionshould not be raised.");
		}
		catch (Throwable t) {
			fail(t.getMessage());
		}
		assertTrue(!request.isResponseValid());
		assertNotNull(request.getResponseErrorClassName());
		assertNotNull(request.getResponseErrorMessage());

		invoker.disconnect();
	}

//todo more test? (0,1,2,3 parametri di input),...

	public static Test suite() {
		TestSuite suite = new TestSuite(RequestInvokerTest.class);
		return suite;
	}

}
