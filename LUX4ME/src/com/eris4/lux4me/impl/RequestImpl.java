/**
 * Copyright ERIS4. All rights reserved.
 * ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * User: MK
 * Date: 24-mar-2003
 */
package com.eris4.lux4me.impl;

import java.util.Vector;
import java.util.Enumeration;

import com.eris4.lux4me.Request;
import com.eris4.lux4.LUX4;

class RequestImpl implements Request{

	private final int initialCapacity = 5;
	private final int increment = 5;

	private int operation;
	private boolean requestSet;
	private Vector inputParamVector;

	private String responseValue;
	private ResponseType responseType;
	private long timeStamp;

	RequestImpl() {
		inputParamVector = new Vector(initialCapacity, increment);
		requestSet = false;
		responseType = ResponseType.notSet;
		responseValue = null;
	}

	public synchronized void set(int operation, String[] inputParams) {
		this.operation = operation;
		inputParamVector.removeAllElements();
		if (inputParams != null){
			for (int i = 0 ; i < inputParams.length ; i++) {
				if (inputParams[i].indexOf('\n') != -1) throw new IllegalArgumentException("inputParams[" + i + "] contains '\n' inputParams[" + i + "] = " + inputParams[i]);
				inputParamVector.addElement(inputParams[i]);
			}
		}
		requestSet = true;
		responseType = ResponseType.notSet;
	}

	public void set(int operation) {
		this.operation = operation;
		inputParamVector.removeAllElements();
		requestSet = true;
		responseType = ResponseType.notSet;
	}

	public void set(int operation, String inputParam) {
		this.operation = operation;
		inputParamVector.removeAllElements();
		if (inputParam != null){
				if (inputParam.indexOf('\n') != -1) throw new IllegalArgumentException("inputParam contains '\n' inputParam = " + inputParam);
				inputParamVector.addElement(inputParam);
		}
		requestSet = true;
		responseType = ResponseType.notSet;
	}

	public void set(int operation, String inputParam1, String inputParam2) {
		this.operation = operation;
		inputParamVector.removeAllElements();
		if (inputParam1 != null){
				if (inputParam1.indexOf('\n') != -1) throw new IllegalArgumentException("inputParams1 contains '\n' inputParams1 = " + inputParam1);
				inputParamVector.addElement(inputParam1);
		}
		if (inputParam2 != null){
				if (inputParam2.indexOf('\n') != -1) throw new IllegalArgumentException("inputParam2 contains '\n' inputParams2 = " + inputParam2);
				inputParamVector.addElement(inputParam2);
		}
		requestSet = true;
		responseType = ResponseType.notSet;
	}

	public void set(int operation, String inputParam1, String inputParam2, String inputParam3) {
		this.operation = operation;
		inputParamVector.removeAllElements();
		if (inputParam1 != null){
				if (inputParam1.indexOf('\n') != -1) throw new IllegalArgumentException("inputParams1 contains '\n' inputParams1 = " + inputParam1);
				inputParamVector.addElement(inputParam1);
		}
		if (inputParam2 != null){
				if (inputParam2.indexOf('\n') != -1) throw new IllegalArgumentException("inputParam2 contains '\n' inputParams2 = " + inputParam2);
				inputParamVector.addElement(inputParam2);
		}
		if (inputParam3 != null){
				if (inputParam3.indexOf('\n') != -1) throw new IllegalArgumentException("inputParam3 contains '\n' inputParams3 = " + inputParam3);
				inputParamVector.addElement(inputParam3);
		}
		requestSet = true;
		responseType = ResponseType.notSet;
	}

	public void set(int operation, String inputParam1, String inputParam2, String inputParam3, String inputParam4) {
		this.operation = operation;
		inputParamVector.removeAllElements();
		if (inputParam1 != null){
						if (inputParam1.indexOf('\n') != -1) throw new IllegalArgumentException("inputParams1 contains '\n' inputParams1 = " + inputParam1);
						inputParamVector.addElement(inputParam1);
		}
		if (inputParam2 != null){
						if (inputParam2.indexOf('\n') != -1) throw new IllegalArgumentException("inputParam2 contains '\n' inputParams2 = " + inputParam2);
						inputParamVector.addElement(inputParam2);
		}
		if (inputParam3 != null){
						if (inputParam3.indexOf('\n') != -1) throw new IllegalArgumentException("inputParam3 contains '\n' inputParams3 = " + inputParam3);
						inputParamVector.addElement(inputParam3);
		}
		if (inputParam4 != null){
						if (inputParam4.indexOf('\n') != -1) throw new IllegalArgumentException("inputParams4 contains '\n' inputParams4 = " + inputParam4);
						inputParamVector.addElement(inputParam4);
		}
		requestSet = true;
		responseType = ResponseType.notSet;
  }

	public synchronized int getOperation() {
		if (! requestSet) throw new IllegalStateException("Request not requestSet");
		return operation;
	}

	public synchronized String[] getInputParams() throws IllegalStateException {
		if (! requestSet) throw new IllegalStateException("Request not requestSet");
        Enumeration e=inputParamVector.elements();
        int i=0;
        while (e.hasMoreElements()) {
            Object o = (Object) e.nextElement();
            i++;
        }

        String[] ret=new String[i];
        e=inputParamVector.elements();
        int j=0;
        while (e.hasMoreElements()) {
            ret[j] = (String) e.nextElement();
            j++;
        }

		return ret;
	}

	public synchronized boolean isRequestSet() {
		return requestSet;
	}

	public synchronized boolean isResponseSet() {
		return responseType.isSet();
	}

	public synchronized boolean isResponseValid() {
		return responseType.isValid();
	}

	public synchronized String getResponseResult() {
		if (! responseType.isSet()) throw new IllegalStateException("! responseSet");
		if (! responseType.isValid()) throw new IllegalStateException("! validResponse");
		return responseValue;
	}

	public synchronized String getResponseErrorClassName() {
		if (! responseType.isSet()) throw new IllegalStateException("! responseSet");
		if (responseType.isValid()) throw new IllegalStateException("validResponse");
		return responseValue.substring(0, responseValue.indexOf(LUX4.RESPONSE_EXCEPTION_SEPARATOR));
	}

	public synchronized String getResponseErrorMessage() {
		if (! responseType.isSet()) throw new IllegalStateException("! responseSet");
		if (responseType.isValid()) throw new IllegalStateException("validResponse");
		int beginIndex = responseValue.indexOf(LUX4.RESPONSE_EXCEPTION_SEPARATOR) + 1;
		if (beginIndex < responseValue.length() && beginIndex >= 0) {
			return responseValue.substring(beginIndex);
		}
		else {
			return "";
		}
	}

	public synchronized long getTimeStamp() {
		return timeStamp;
	}

	public synchronized String toString() {
		if (! isRequestSet()) return "Request not set";
		StringBuffer out = new StringBuffer();
		out.append("getTimeStamp() = ").append(getTimeStamp()).append('\n');
        out.append("getOperation() = ").append(getOperation()).append('\n');
        out.append("getInputParams() = ").append(getInputParams()).append('\n');
		if (! isResponseSet()) return out + " Response not set";
		if (isResponseValid()) {
            out.append("getResponseResult() = ").append(getResponseResult()).append('\n');
			return out.toString();
		}
		else {
            out.append("getResponseErrorClassName() = ").append(getResponseErrorClassName()).append(" getResponseErrorMessage() = ").append(getResponseErrorMessage()).append('\n');
			return out.toString();
		}
	}

	synchronized Vector getInputParamVector() {
		return inputParamVector;
	}

	synchronized void setResponse(String value, ResponseType responseType) {
		this.responseValue = value;
		this.responseType = responseType;
		notify();
	}

	synchronized ResponseType getResponseType() {
		return responseType;
	}

	synchronized void setTimeStamp() {
		timeStamp = System.currentTimeMillis();
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RequestImpl)) return false;

		final RequestImpl request = (RequestImpl) o;

		if (operation != request.operation) return false;
		if (requestSet != request.requestSet) return false;
		if (inputParamVector != null ? !inputParamVector.equals(request.inputParamVector) : request.inputParamVector != null) return false;
		if (responseType != null ? !responseType.equals(request.responseType) : request.responseType != null) return false;
		if (responseValue != null ? !responseValue.equals(request.responseValue) : request.responseValue != null) return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = operation;
		result = 29 * result + (requestSet ? 1 : 0);
		result = 29 * result + (inputParamVector != null ? inputParamVector.hashCode() : 0);
		result = 29 * result + (responseValue != null ? responseValue.hashCode() : 0);
		result = 29 * result + (responseType != null ? responseType.hashCode() : 0);
		return result;
	}
}
