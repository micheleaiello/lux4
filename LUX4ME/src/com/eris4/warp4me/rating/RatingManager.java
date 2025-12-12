/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Mar 11, 2003.
*/

package com.eris4.warp4me.rating;

import com.eris4.warp4me.*;
import com.eris4.warp4me.authorization.*;
import com.eris4.warp4me.rating.RatingException;
import com.eris4.lux4me.*;

import java.io.IOException;

public class RatingManager  {
    private String eventType;

    private final RequestInvoker invoker;
    private int rateActualIdx;
    private int rateAdviceIdx;
    private int previewIdx;

    /**
     * Creates a <code>RatingManager<code> that will manage rating reqeusts for the given event type.
     *
     * @param host the host of the WARP4 server
     * @param port the port of the WARP4 server
     * @param eventType the type of the event to be managed
     *
     * @throws InvalidEventTypeException if the event type is invalid
     * @throws ConnectionException if the connection cannot be established
     *
     */
    public RatingManager(String host,int port,String eventType)
            throws InvalidEventTypeException, ConnectionException
    {
		this.eventType=eventType;

        invoker = Lux4ClientFactory.createRequestInvoker(host, port, eventType);

		try {
		    invoker.connect();

		    rateActualIdx=invoker.findOperationIndex("rateActual");
		    rateAdviceIdx=invoker.findOperationIndex("rateAdvice");
		    //previewIdx=invoker.findOperationIndex("preview");
		} catch (CannotConnectException e) {
		    throw new ConnectionException(e.getMessage());
		} catch (OperationNotExistent operationNotExistent) {
		    throw new ConnectionException(operationNotExistent.getMessage());
		}
    }



    /**
     * Performs a rating operation using the given event. It return the result of the rate.
     * The state of the rating account is not changed.
     *
     * @param event the event to be rated
     *
     * @return the result of the rating
     *
     * @throws RatingException if any invalid operation is performed during the rating
     * @throws RatingAccountNotFoundException if the rating account is not found
     * @throws ConnectionException if the connection to WARP4 failed
     */
    public RatingValue rateAdvice(RatingEvent event)
            throws RatingException, RatingAccountNotFoundException, ConnectionException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(rateAdviceIdx, event.toString());

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
     * @param event the event to be rated
     *
     * @return the result of the rating
     *
     * @throws RatingException if any invalid operation is performed during the rating
     * @throws RatingAccountNotFoundException if the rating account is not found
     * @throws ConnectionException if the connection to WARP4 failed
     */
    public RatingValue rateActual( RatingEvent event)
            throws RatingException, RatingAccountNotFoundException, ConnectionException
    {
        Request request = Lux4ClientFactory.createRequest();
        request.set(rateActualIdx, event.toString());

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
