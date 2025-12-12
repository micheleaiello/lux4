/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Mar 11, 2003.
*/

package com.eris4.warp4me.authorization;

import com.eris4.warp4me.*;
import com.eris4.warp4me.rating.RatingException;
import com.eris4.lux4me.*;

import java.io.IOException;

public class SessionManager  {
    private String eventType;

    private final RequestInvoker invoker;
    private int createSessionIdx;
    private int createSessionStringIdx;
    private int endIdx;
    private int initIdx;
    private int goOnIdx;
    private int startIdx;
    private int cancelIdx;
    private int rateActualIdx;
    private int rateAdviceIdx;
    private int stopIdx;
    private int previewIdx;
    private int availableIdx;

    /**
     * Creates a <code>SessionManager<code> that will manage sessions for the given event type.
     *
     * @param host the host of the WARP4 server
     * @param port the port of the WARP4 server
     * @param eventType the type of the event to be managed
     *
     * @throws InvalidEventTypeException if the event type is invalid
     * @throws ConnectionException if the connection cannot be established
     *
     */
    public SessionManager(String host,int port,String eventType)
            throws InvalidEventTypeException, ConnectionException
    {
		this.eventType=eventType;

        invoker = Lux4ClientFactory.createRequestInvoker(host, port, eventType);

		try {
		    invoker.connect();

		    createSessionIdx=invoker.findOperationIndex("createSession");
		    createSessionStringIdx=invoker.findOperationIndex("createSession_String");
		    initIdx=invoker.findOperationIndex("initSession");
		    startIdx=invoker.findOperationIndex("start");
		    goOnIdx=invoker.findOperationIndex("goOn");
		    endIdx=invoker.findOperationIndex("end");
		    stopIdx=invoker.findOperationIndex("stop");
		    cancelIdx=invoker.findOperationIndex("cancel");
		    rateActualIdx=invoker.findOperationIndex("rateActual");
		    rateAdviceIdx=invoker.findOperationIndex("rateAdvice");
		    previewIdx=invoker.findOperationIndex("preview");
		    availableIdx=invoker.findOperationIndex("getAvailableCredit");
		} catch (CannotConnectException e) {
		    throw new ConnectionException(e.getMessage());
		} catch (OperationNotExistent operationNotExistent) {
		    throw new ConnectionException(operationNotExistent.getMessage());
		}
    }

    /**
     * Creates a new session identified by <code>sessionID</code>.
     *
     * @param sessionID the session identifier
     *
     * @return the session created
     *
     * @throws InvalidSessionIDException if the session identifier already exists
     * @throws SessionManagerException if there are not enought session resources
     */
    public String createSession(String sessionID)
            throws InvalidSessionIDException, SessionManagerException, ConnectionException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(createSessionStringIdx, sessionID);
        try {
            invoker.invoke(request);

            if (request.isResponseValid()) {
                return request.getResponseResult();
            }
            else {
                String exceptionClass = request.getResponseErrorClassName();
                if (exceptionClass.endsWith("SessionManagerException")) {
                    throw new SessionManagerException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("InvalidSessionIDException")) {
                    throw new SessionManagerException(request.getResponseErrorMessage());
                }
                else {
                    throw new SessionManagerException(request.getResponseErrorMessage());
                }
            }
        } catch (InvokerNotConnectedException e) {
            throw new ConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    /**
     * Creates a new session.
     *
     * @return the session created
     *
     * @throws SessionManagerException if there are not enought session resources
     * @throws ConnectionException if the connection to WARP4 failed
     */
    public String createSession()
            throws SessionManagerException, ConnectionException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(createSessionIdx);
        try {
            invoker.invoke(request);

            if (request.isResponseValid()) {
                return request.getResponseResult();
            }
            else {
                String exceptionClass = request.getResponseErrorClassName();
                if (exceptionClass.endsWith("SessionManagerException")) {
                    throw new SessionManagerException(request.getResponseErrorMessage());
                }
                else {
                    throw new SessionManagerException(request.getResponseErrorMessage());
                }
            }
        } catch (InvokerNotConnectedException e) {
            throw new ConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    /**
     * Inits the session.
     * Returns <code>true</code> if the init is authorized and <code>false</code> otherwise.
     * If the init is authorized the state of the session is changed to <code>INITIALIZED</code>.
     *
     * @param sid the <code>SessionIdentifier</code>
     * @param initEvent the init event
     *
     * @return the result of the init

     * @throws InvalidSessionStateException if the session is not in the <code>NEW</code> state.
     * @throws InvalidSessionIDException if doesn't exists a session with the given session id.
     * @throws ConnectionException if the connection to WARP4 failed
     * @throws AuthorizationException if there is some generic authorization problem
     */
    public  boolean init(String sid, RatingEvent initEvent)
            throws InvalidSessionStateException, InvalidSessionIDException,
            ConnectionException, AuthorizationException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(initIdx, sid, initEvent.toString());

        try {
            invoker.invoke(request);

            if (request.isResponseValid()) {
                String responseResult= request.getResponseResult();
                return Util.parseBoolean(responseResult);
            } else {
                String exceptionClass = request.getResponseErrorClassName();
                if (exceptionClass.endsWith("InvalidSessionIDException")) {
                    throw new InvalidSessionIDException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("InvalidSessionStateException")) {
                    throw new InvalidSessionStateException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("RatingContextNotFoundException")) {
                    throw new InvalidSessionStateException(request.getResponseErrorMessage());
                }
                else {
                    throw new AuthorizationException(request.getResponseErrorMessage());
                }
            }
        } catch (InvokerNotConnectedException e) {
            throw new ConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException();
        }
    };

    /**
     * Starts the session.
     * Returns the authorized chunk if the start is authorized <code>null</code> otherwise.
     * If the start is authorized the state of the session is changed to <code>STARTED</code>.
     *
     * @param sid the session identifier
     * @param startEvent
     *
     * @return the authorized chunk.
     *
     * @throws InvalidSessionStateException if the session is in the <code>NEW</code> state or if the type of the event is not compatible
     * @throws InvalidSessionIDException if doesn't exists a session with the given session id.
     * @throws ConnectionException if the connection to WARP4 failed
     * @throws AuthorizationException if there is some generic authorization problem

     */
    public Response start(String sid, RatingEvent startEvent)
            throws InvalidSessionStateException, InvalidSessionIDException,
            ConnectionException, AuthorizationException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(startIdx, sid, startEvent.toString());

        try {
            invoker.invoke(request);

            if (request.isResponseValid()) {
                return new Response(request.getResponseResult());
            }
            else {
                String exceptionClass = request.getResponseErrorClassName();
                if (exceptionClass.endsWith("InvalidSessionIDException")) {
                    throw new InvalidSessionIDException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("InvalidSessionStateException")) {
                    throw new InvalidSessionStateException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("RatingContextNotFoundException")) {
                    throw new InvalidSessionStateException(request.getResponseErrorMessage());
                }
                else {
                    throw new AuthorizationException(request.getResponseErrorMessage());
                }
            }
        } catch (InvokerNotConnectedException e) {
            throw new ConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    /**
     * Go on the session.
     * Returns the charged amount and the authorized chunk if the go on is authorized, <code>null</code> otherwise.
     *
     * @param sid the session identifier.
     * @param goOnEvent
     *
     * @return the response containing the charged value and the authorized chunk.

     * @throws InvalidSessionStateException if the session is in the <code>NEW</code> state or if the type of the event is not compatible
     * @throws InvalidSessionIDException if doesn't exists a session with the given session id.
     * @throws ConnectionException if the connection to WARP4 failed
     * @throws AuthorizationException if there is some generic authorization problem
     */
    public  Response goOn(String sid, RatingEvent goOnEvent)
            throws InvalidSessionStateException, InvalidSessionIDException,
            ConnectionException, AuthorizationException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(goOnIdx, sid, goOnEvent.toString());

        try {
            invoker.invoke(request);

            if (request.isResponseValid()) {
                return new Response(request.getResponseResult());
            }
            else {
                String exceptionClass = request.getResponseErrorClassName();
                if (exceptionClass.endsWith("InvalidSessionIDException")) {
                    throw new InvalidSessionIDException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("InvalidSessionStateException")) {
                    throw new InvalidSessionStateException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("RatingContextNotFoundException")) {
                    throw new InvalidSessionStateException(request.getResponseErrorMessage());
                }
                else {
                    throw new AuthorizationException(request.getResponseErrorMessage());
                }
            }
        } catch (InvokerNotConnectedException e) {
            throw new ConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    /**
     * Ends the session. Returns the charged amount.
     *
     * @param sid the session
     * @param endEvent
     *
     * @return the charged value
     *
     * @throws InvalidSessionStateException if the session is in the <code>NEW</code> state or if the type of the event is not compatible
     * @throws ConnectionException if the connection to WARP4 failed
     * @throws AuthorizationException if there is some generic authorization problem
     */
    public  RatingValue end(String sid, RatingEvent endEvent)
            throws InvalidSessionStateException, InvalidSessionIDException,
            ConnectionException, AuthorizationException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(endIdx, sid, endEvent.toString());

        try {
            invoker.invoke(request);

            if (request.isResponseValid()) {
                return Util.parseRatingValue(request.getResponseResult());
            }
            else {
                String exceptionClass = request.getResponseErrorClassName();
                if (exceptionClass.endsWith("InvalidSessionIDException")) {
                    throw new InvalidSessionIDException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("InvalidSessionStateException")) {
                    throw new InvalidSessionStateException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("RatingContextNotFoundException")) {
                    throw new InvalidSessionStateException(request.getResponseErrorMessage());
                }
                else {
                    throw new AuthorizationException(request.getResponseErrorMessage());
                }
            }
        } catch (InvokerNotConnectedException e) {
            throw new ConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    /**
     * Stops the session. Returns the charged amount.
     *
     * @param sid the session
     * @param stopEvent
     *
     * @return the charged value
     *
     * @throws InvalidSessionStateException if the session is in the <code>NEW</code> state or if the type of the event is not compatible
     * @throws ConnectionException if the connection to WARP4 failed
     * @throws AuthorizationException if there is some generic authorization problem
     */
    public  RatingValue stop(String sid, RatingEvent stopEvent)
            throws InvalidSessionStateException, InvalidSessionIDException,
            ConnectionException, AuthorizationException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(stopIdx, sid, stopEvent.toString());

        try {
            invoker.invoke(request);

            if (request.isResponseValid()) {
                return Util.parseRatingValue(request.getResponseResult());
            }
            else {
                String exceptionClass = request.getResponseErrorClassName();
                if (exceptionClass.endsWith("InvalidSessionIDException")) {
                    throw new InvalidSessionIDException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("InvalidSessionStateException")) {
                    throw new InvalidSessionStateException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("RatingContextNotFoundException")) {
                    throw new InvalidSessionStateException(request.getResponseErrorMessage());
                }
                else {
                    throw new AuthorizationException(request.getResponseErrorMessage());
                }
            }
        } catch (InvokerNotConnectedException e) {
            throw new ConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    /**
     * Cancel the session.
     *
     * @param sid the session
     *
     * @throws ConnectionException if the connection to WARP4 failed
     */
    public void cancel(String sid)
            throws ConnectionException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(cancelIdx, sid);
        try {
            invoker.invoke(request);
        } catch (InvokerNotConnectedException e) {
            throw new ConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage());
        }
        return;
    }

    /**
     * Performs a rating operation using the given event. It return the result of the rate.
     * The state of the rating account is not changed.
     *
     * @param sid the session
     * @param event the event to be rated
     *
     * @return the result of the rating
     *
     * @throws RatingException if any invalid operation is performed during the rating
     * @throws RatingAccountNotFoundException if the rating account is not found
     * @throws ConnectionException if the connection to WARP4 failed
     */
    public RatingValue rateAdvice(String sid, RatingEvent event)
            throws RatingException, RatingAccountNotFoundException, ConnectionException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(rateAdviceIdx, sid, event.toString());

        try {
            invoker.invoke(request);

            if (request.isResponseValid()) {
                return Util.parseRatingValue(request.getResponseResult());
            }
            else {
                String exceptionClass = request.getResponseErrorClassName();
                if (exceptionClass.endsWith("RatingException")) {
                    throw new RatingException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("RatingContextNotFoundException")) {
                    throw new RatingAccountNotFoundException(request.getResponseErrorMessage());
                }
                else {
                    throw new RatingException(request.getResponseErrorMessage());
                }
            }
        } catch (InvokerNotConnectedException e) {
            throw new ConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    /**
     * Performs a rating operation using the given event. It return the result of the rate.
     *
     * @param sid the session
     * @param event the event to be rated
     *
     * @return the result of the rating
     *
     * @throws RatingException if any invalid operation is performed during the rating
     * @throws RatingAccountNotFoundException if the rating account is not found
     * @throws ConnectionException if the connection to WARP4 failed
     */
    public RatingValue rateActual(String sid, RatingEvent event)
            throws RatingException, RatingAccountNotFoundException, ConnectionException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(rateActualIdx, sid, event.toString());

        try {
            invoker.invoke(request);

            if (request.isResponseValid()) {
                return Util.parseRatingValue(request.getResponseResult());
            }
            else {
                String exceptionClass = request.getResponseErrorClassName();
                if (exceptionClass.endsWith("RatingException")) {
                    throw new RatingException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("RatingContextNotFoundException")) {
                    throw new RatingAccountNotFoundException(request.getResponseErrorMessage());
                }
                else {
                    throw new RatingException(request.getResponseErrorMessage());
                }
            }
        } catch (InvokerNotConnectedException e) {
            throw new ConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    public RatingValue preview(String sid, RatingEvent event, RatingValue credit, String toPreview)
            throws RatingException, RatingAccountNotFoundException, ConnectionException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(previewIdx, sid, event.toString(), credit.toString(), toPreview);
        try {
            invoker.invoke(request);

            if (request.isResponseValid()) {
                return Util.parseRatingValue(request.getResponseResult());
            }
            else {
                String exceptionClass = request.getResponseErrorClassName();
                if (exceptionClass.endsWith("RatingException")) {
                    throw new RatingException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("RatingContextNotFoundException")) {
                    throw new RatingAccountNotFoundException(request.getResponseErrorMessage());
                }
                else {
                    throw new RatingException(request.getResponseErrorMessage());
                }
            }
        } catch (InvokerNotConnectedException e) {
            throw new ConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    public RatingValue getAvailableCredit(String sid)
            throws RatingException, RatingAccountNotFoundException, ConnectionException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(availableIdx, sid);
        try {
            invoker.invoke(request);

            if (request.isResponseValid()) {
                return Util.parseRatingValue(request.getResponseResult());
            }
            else {
                String exceptionClass = request.getResponseErrorClassName();
                if (exceptionClass.endsWith("RatingException")) {
                    throw new RatingException(request.getResponseErrorMessage());
                }
                else if (exceptionClass.endsWith("RatingContextNotFoundException")) {
                    throw new RatingAccountNotFoundException(request.getResponseErrorMessage());
                }
                else {
                    throw new RatingException(request.getResponseErrorMessage());
                }
            }
        } catch (InvokerNotConnectedException e) {
            throw new ConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

}
