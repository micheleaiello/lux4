/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.2 $
*/
package com.eris4.lux4.client.impl;

abstract class ResponseType {
	private static final java.util.List all = new java.util.LinkedList();
	private static final ResponseType[] allArray;

  /**
   * Returns all defined ResponseType.
   */
  static ResponseType[] getAll(){
    return allArray;
  }

	static int getCount(){
		return allArray.length;
	}

  private ResponseType() {
    for (int i = 0; i < all.size(); i++) {
	    if (((ResponseType) all.get(i)).getName().equals(getName())) {
	      throw new IllegalArgumentException("Name duplicated " + getName());
	    }
    }
    all.add(this);
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

	static {
    allArray = (ResponseType[]) all.toArray(new ResponseType[0]);
  }}
