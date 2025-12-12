/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/11/09 19:58:39 $
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

public class ParametersTest extends TestCase {

    public static class Handler extends RequestHandler {

        public void init(String initializationString) throws InitializationException {
        }

        public void close() {
        }

        public String concatTwo(String s1, String s2) {
            return s1+s2;
        }

        public String concatThree(String s1, String s2, String s3) {
            return s1+s2+s3;
        }

        public String concatFour(String s1, String s2, String s3, String s4) {
            return s1+s2+s3+s4;
        }
    }

    private String hostname = "localhost";
    private int port = 2323;
    private RequestDispatcher rd;

    private int connectionCount = 1;

    private void serverStart() {
        int numOfThreads = connectionCount;

        try {
            rd = new RequestDispatcher(port, numOfThreads, ParametersTest.Handler.class);
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

    public ParametersTest(String name) {
        super(name);
    }

    public void testConcatSingleRequest() {
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

        invoker.disconnect();
    }

    public void testConcatMultiRequest() {
        MultiRequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, "");
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

        {
            MultiRequest req = Lux4ClientFactory.createMultiRequest();
            req.set(new int[] {op2, op4, op2},
                    new String[][] {
                        {"AAA", "BBB"},
                        {"CCC", "DDD", "EEE", "FFF"},
                        {"GGG", "HHH"}
                    });
            try {
                invoker.invoke(req);
            } catch (InvokerNotConnectedException e) {
                fail("InvokerNotConnectedException should not be raised.");
            } catch (ConnectionException e) {
                fail("ConnectionException should not be raised.");
            }

            assertTrue(req.isSet());
            assertTrue(req.get(0).isResponseValid());
            assertTrue(req.get(1).isResponseValid());
            assertTrue(req.get(2).isResponseValid());
            assertEquals("AAABBB", req.get(0).getResponseResult());
            assertEquals("CCCDDDEEEFFF", req.get(1).getResponseResult());
            assertEquals("GGGHHH", req.get(2).getResponseResult());
        }

        {
            MultiRequest req = Lux4ClientFactory.createMultiRequest();
            req.set(new int[] {op3, op2, op4, op4, op3},
                    new String[][] {
                        {"AA", "BB", "CC"},
                        {"DD", "EE"},
                        {"FF", "GG", "HH", "II"},
                        {"JJ", "KK", "LL", "MM"},
                        {"NN", "OO", "PP"}
                    });
            try {
                invoker.invoke(req);
            } catch (InvokerNotConnectedException e) {
                fail("InvokerNotConnectedException should not be raised.");
            } catch (ConnectionException e) {
                fail("ConnectionException should not be raised.");
            }

            assertTrue(req.isSet());
            assertTrue(req.get(0).isResponseValid());
            assertTrue(req.get(1).isResponseValid());
            assertTrue(req.get(2).isResponseValid());
            assertTrue(req.get(3).isResponseValid());
            assertTrue(req.get(4).isResponseValid());
            assertEquals("AABBCC", req.get(0).getResponseResult());
            assertEquals("DDEE", req.get(1).getResponseResult());
            assertEquals("FFGGHHII", req.get(2).getResponseResult());
            assertEquals("JJKKLLMM", req.get(3).getResponseResult());
            assertEquals("NNOOPP", req.get(4).getResponseResult());
        }

        invoker.disconnect();
    }

    public void testConcatMultiRequestWrongParams() {
        MultiRequestInvoker invoker = Lux4ClientFactory.createSingleThreadRequestInvoker(hostname, port, "");
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

        {
            MultiRequest req = Lux4ClientFactory.createMultiRequest();
            req.set(new int[] {op2, op4, op2},
                    new String[][] {
                        {"AAA", "BBB", "ZZZ"},
                        {"CCC", "DDD", "EEE"},
                        {"GGG", "HHH", "ZZZ", "ZZZ"}
                    });
            try {
                invoker.invoke(req);
            } catch (InvokerNotConnectedException e) {
                fail("InvokerNotConnectedException should not be raised.");
            } catch (ConnectionException e) {
                fail("ConnectionException should not be raised.");
            }

            assertTrue(req.isSet());
            assertTrue(req.get(0).isResponseValid());
            assertTrue(req.get(1).isResponseValid());
            assertTrue(req.get(2).isResponseValid());
            assertEquals("AAABBB", req.get(0).getResponseResult());
            assertEquals("CCCDDDEEEnull", req.get(1).getResponseResult());
            assertEquals("GGGHHH", req.get(2).getResponseResult());
        }

        {
            MultiRequest req = Lux4ClientFactory.createMultiRequest();
            req.set(new int[] {op3, op2, op4, op4, op3},
                    new String[][] {
                        {"AA", "BB", "CC", "ZZ", "ZZ", "ZZ", "ZZ"},
                        {"DD", "EE", "ZZ", "ZZ", "XX", "XX"},
                        {"FF"},
                        {"JJ", "KK"},
                        {"NN", "OO", "XZ", "ZX"}
                    });
            try {
                invoker.invoke(req);
            } catch (InvokerNotConnectedException e) {
                fail("InvokerNotConnectedException should not be raised.");
            } catch (ConnectionException e) {
                fail("ConnectionException should not be raised.");
            }

            assertTrue(req.isSet());
            assertTrue(req.get(0).isResponseValid());
            assertTrue(req.get(1).isResponseValid());
            assertTrue(req.get(2).isResponseValid());
            assertTrue(req.get(3).isResponseValid());
            assertTrue(req.get(4).isResponseValid());
            assertEquals("AABBCC", req.get(0).getResponseResult());
            assertEquals("DDEE", req.get(1).getResponseResult());
            assertEquals("FFnullnullnull", req.get(2).getResponseResult());
            assertEquals("JJKKnullnull", req.get(3).getResponseResult());
            assertEquals("NNOOXZ", req.get(4).getResponseResult());
        }

        invoker.disconnect();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ParametersTest.class);
        return suite;
    }

}
