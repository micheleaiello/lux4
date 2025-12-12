/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/11/10 13:40:21 $
* $Name:  $ - $Revision: 1.1 $
*/
package com.eris4.lux4.unittest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.eris4.lux4.server.RequestHandler;
import com.eris4.lux4.server.InitializationException;
import com.eris4.lux4.server.RequestDispatcher;
import com.eris4.lux4.server.HandlerInstantiationException;
import com.eris4.lux4.client.*;

public class ReconnectTest extends TestCase {

    public static class Handler extends RequestHandler {

        private boolean prependIndex = true;
        private int index;

        public void init(String initializationString) throws InitializationException {
            if ((initializationString != null) && (!initializationString.equals(""))) {
                prependIndex = Boolean.valueOf(initializationString).booleanValue();
            }
        }

        public void close() {
        }

        public String setIndex(String indexStr) {
            index = Integer.parseInt(indexStr);
            return String.valueOf(index);
        }

        public String getIndex() {
            return String.valueOf(index);
        }

        public String concatTwo(String s1, String s2) {
            if (prependIndex) {
                return String.valueOf(index++)+s1+s2;
            }
            else {
                return s1+s2;
            }
        }

        public String concatThree(String s1, String s2, String s3) {
            if (prependIndex) {
                return String.valueOf(index++)+s1+s2+s3;
            } else {
                return s1+s2+s3;
            }
        }

        public String concatFour(String s1, String s2, String s3, String s4) {
            if (prependIndex) {
                return String.valueOf(index++)+s1+s2+s3+s4;
            } else {
                return s1+s2+s3+s4;
            }
        }
    }

    private static class Worker implements Runnable {
        private boolean shutdown = false;
        private final int op2;
        private final int op3;
        private final int op4;
        private final RequestInvoker invoker;

        public Worker(int op2, int op3, int op4, RequestInvoker invoker) {
            this.op2 = op2;
            this.op3 = op3;
            this.op4 = op4;
            this.invoker = invoker;
        }

        public void shutdown() {
            shutdown = true;
        }

        public void run() {
            while (!shutdown) {
                {
                    Request req = Lux4ClientFactory.createRequest();
                    req.set(op2, new String[] {"AAA", "BBB"});
                    try {
                        invoker.invoke(req);
                    } catch (InvokerNotConnectedException e) {
                        fail("InvokerNotConnectedException should not be raised.");
                    } catch (ConnectionException e) {
                        fail("ConnectionException should not be raised.");
                    }

                    assertTrue(req.isResponseSet());
                    assertTrue(req.isResponseValid());
                    assertEquals("AAABBB", req.getResponseResult());
                }

                {
                    Request req = Lux4ClientFactory.createRequest();
                    req.set(op2, new String[] {"AAA", "BBB"});
                    try {
                        invoker.invoke(req);
                    } catch (InvokerNotConnectedException e) {
                        fail("InvokerNotConnectedException should not be raised.");
                    } catch (ConnectionException e) {
                        fail("ConnectionException should not be raised.");
                    }

                    assertTrue(req.isResponseSet());
                    assertTrue(req.isResponseValid());
                    assertEquals("AAABBB", req.getResponseResult());
                }

                {
                    Request req = Lux4ClientFactory.createRequest();
                    req.set(op4, new String[] {"CCC", "DDD", "EEE", "FFF"});
                    try {
                        invoker.invoke(req);
                    } catch (InvokerNotConnectedException e) {
                        fail("InvokerNotConnectedException should not be raised.");
                    } catch (ConnectionException e) {
                        fail("ConnectionException should not be raised.");
                    }

                    assertTrue(req.isResponseSet());
                    assertTrue(req.isResponseValid());
                    assertEquals("CCCDDDEEEFFF", req.getResponseResult());
                }

                {
                    Request req = Lux4ClientFactory.createRequest();
                    req.set(op2, new String[] {"GGG", "HHH"});
                    try {
                        invoker.invoke(req);
                    } catch (InvokerNotConnectedException e) {
                        fail("InvokerNotConnectedException should not be raised.");
                    } catch (ConnectionException e) {
                        fail("ConnectionException should not be raised.");
                    }

                    assertTrue(req.isResponseSet());
                    assertTrue(req.isResponseValid());
                    assertEquals("GGGHHH", req.getResponseResult());
                }
            }
        }
    }

    private String hostname = "localhost";
    private int port = 2323;
    private RequestDispatcher rd;

    private int connectionCount = 1;

    private void serverStart() {
        int numOfThreads = connectionCount;

        try {
            rd = new RequestDispatcher(port, numOfThreads, ReconnectTest.Handler.class);
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

    protected void setUp() throws Exception {
        serverStart();
        Thread.sleep(500);
    }

    protected void tearDown() throws Exception {
        serverStop();
        Thread.sleep(1000);
    }

    public ReconnectTest(String name) {
        super(name);
    }

    public void testClientSocketTimeout() {
        RequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, "");
        try {
            invoker.connect();
        }
        catch (CannotConnectException e) {
            fail("CannotConnectException should not be raised.");
        }

        int op2 = -1;
        try {
            op2 = invoker.findOperationIndex("concatTwo");
        } catch (OperationNotExistent operationNotExistent) {
            fail("OperationNotExistent should not be raised.");
        }

        int op3 = -1;
        try {
            op3 = invoker.findOperationIndex("concatThree");
        } catch (OperationNotExistent operationNotExistent) {
            fail("OperationNotExistent should not be raised.");
        }

        int op4 = -1;
        try {
            op4 = invoker.findOperationIndex("concatFour");
        } catch (OperationNotExistent operationNotExistent) {
            fail("OperationNotExistent should not be raised.");
        }

        int setIndexOp = -1;
        try {
            setIndexOp = invoker.findOperationIndex("setIndex");
        } catch (OperationNotExistent operationNotExistent) {
            fail("OperationNotExistent should not be raised.");
        }

        {
            Request req = Lux4ClientFactory.createRequest();
            req.set(setIndexOp, new String[] {"1"});
            try {
                invoker.invoke(req);
            } catch (InvokerNotConnectedException e) {
                fail("InvokerNotConnectedException should not be raised.");
            } catch (ConnectionException e) {
                fail("ConnectionException should not be raised.");
            }

            assertTrue(req.isResponseSet());
            assertTrue(req.isResponseValid());
        }

        {
            Request req = Lux4ClientFactory.createRequest();
            req.set(op2, new String[] {"AAA", "BBB"});
            try {
                invoker.invoke(req);
            } catch (InvokerNotConnectedException e) {
                fail("InvokerNotConnectedException should not be raised.");
            } catch (ConnectionException e) {
                fail("ConnectionException should not be raised.");
            }

            assertTrue(req.isResponseSet());
            assertTrue(req.isResponseValid());
            assertEquals("1AAABBB", req.getResponseResult());
        }

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
        }

        {
            Request req = Lux4ClientFactory.createRequest();
            req.set(op2, new String[] {"AAA", "BBB"});
            try {
                invoker.invoke(req);
            } catch (InvokerNotConnectedException e) {
                fail("InvokerNotConnectedException should not be raised.");
            } catch (ConnectionException e) {
                fail("ConnectionException should not be raised.");
            }

            assertTrue(req.isResponseSet());
            assertTrue(req.isResponseValid());
            assertEquals("2AAABBB", req.getResponseResult());
        }

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
        }

        {
            Request req = Lux4ClientFactory.createRequest();
            req.set(op4, new String[] {"CCC", "DDD", "EEE", "FFF"});
            try {
                invoker.invoke(req);
            } catch (InvokerNotConnectedException e) {
                fail("InvokerNotConnectedException should not be raised.");
            } catch (ConnectionException e) {
                fail("ConnectionException should not be raised.");
            }

            assertTrue(req.isResponseSet());
            assertTrue(req.isResponseValid());
            assertEquals("3CCCDDDEEEFFF", req.getResponseResult());
        }

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
        }

        {
            Request req = Lux4ClientFactory.createRequest();
            req.set(op2, new String[] {"GGG", "HHH"});
            try {
                invoker.invoke(req);
            } catch (InvokerNotConnectedException e) {
                fail("InvokerNotConnectedException should not be raised.");
            } catch (ConnectionException e) {
                fail("ConnectionException should not be raised.");
            }

            assertTrue(req.isResponseSet());
            assertTrue(req.isResponseValid());
            assertEquals("4GGGHHH", req.getResponseResult());
        }

        invoker.disconnect();
    }

    public void testServerShutdown() throws OperationNotExistent {
        final RequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, "false");
        try {
            invoker.connect();
        }
        catch (CannotConnectException e) {
            fail("CannotConnectException should not be raised.");
        }

        final int op2 = invoker.findOperationIndex("concatTwo");
        final int op3 = invoker.findOperationIndex("concatThree");
        final int op4 = invoker.findOperationIndex("concatFour");

        int setIndexOp = invoker.findOperationIndex("setIndex");

        {
            Request req = Lux4ClientFactory.createRequest();
            req.set(setIndexOp, new String[] {"1"});
            try {
                invoker.invoke(req);
            } catch (InvokerNotConnectedException e) {
                fail("InvokerNotConnectedException should not be raised.");
            } catch (ConnectionException e) {
                fail("ConnectionException should not be raised.");
            }

            assertTrue(req.isResponseSet());
            assertTrue(req.isResponseValid());
        }


        Worker worker = new Worker(op2, op3, op4, invoker);
        Thread workingThread = new Thread(worker);

        workingThread.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }

        System.err.println("Stopping the server...");
        serverStop();

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
        }

        System.err.println("Starting the server...");
        serverStart();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }

        System.err.println("Stopping the server...");
        serverStop();

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
        }

        System.err.println("Starting the server...");
        serverStart();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }

        worker.shutdown();

        try {
            workingThread.join();
        } catch (InterruptedException e) {
        }

        invoker.disconnect();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ReconnectTest.class);
        return suite;
    }

}
