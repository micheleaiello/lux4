/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.3 $
*/
package com.eris4.lux4.client.impl;

class OperationsData {

	String[] operationNameArray;
	int[] numberOfInputParametersArray;

	OperationsData(int operationCount) {
		operationNameArray = new String[operationCount];
		numberOfInputParametersArray =  new int[operationCount];
	}

}
