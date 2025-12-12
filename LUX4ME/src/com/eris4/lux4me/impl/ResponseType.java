/**
 * Copyright ERIS4. All rights reserved.
 * ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Author: Stefano Antonelli
 * Date: Sep 24, 2003
 */

package com.eris4.lux4me.impl;

abstract class ResponseType {

  private ResponseType() {
  }


	public final String toString() {
		return getName();
	}

	abstract String getName();
	abstract boolean isSet();
	abstract boolean isValid();
	abstract boolean isFromServer();

	static final ResponseType notSet = new ResponseType() {
		String getName() { return "NotSet"; }
		boolean isSet() {	return false;}
		boolean isValid() {	 throw new IllegalStateException("Response not set.");}
		boolean isFromServer() { throw new IllegalStateException("Response not set.");}
	};

	static final ResponseType valid = new ResponseType() {
		String getName() { return "Valid"; }
		boolean isSet() {	return true;}
		boolean isValid() {	return true;}
		boolean isFromServer() { throw new IllegalStateException("isFromServer meaningless for valid response.");}
	};

	static final ResponseType serverException = new ResponseType() {
		String getName() { return "ServerException"; }
		boolean isSet() {	return true;}
		boolean isValid() {	return false;}
		boolean isFromServer() { return true;}
	};

	static final ResponseType clientIOException = new ResponseType() {
		String getName() { return "ClientIOException"; }
		boolean isSet() {	return true;}
		boolean isValid() {	return false;}
		boolean isFromServer() { return false;}
	};

	static final ResponseType clientInvokerNotConnectedException = new ResponseType() {
		String getName() { return "ClientInvokerNotConnectedException"; }
		boolean isSet() {	return true;}
		boolean isValid() {	return false;}
		boolean isFromServer() { return false;}
	};

	static final ResponseType clientRuntimeException = new ResponseType() {
		String getName() { return "ClientRuntimeException"; }
		boolean isSet() {	return true;}
		boolean isValid() {	return false;}
		boolean isFromServer() { return false;}
	};

}
