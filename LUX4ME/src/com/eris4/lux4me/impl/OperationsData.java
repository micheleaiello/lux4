/**
 * Copyright ERIS4. All rights reserved.
 * ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Author: Stefano Antonelli
 * Date: Sep 24, 2003
 */

package com.eris4.lux4me.impl;

class OperationsData {

	String[] operationNameArray;
	int[] numberOfInputParametersArray;

	OperationsData(int operationCount) {
		operationNameArray = new String[operationCount];
		numberOfInputParametersArray =  new int[operationCount];
	}

}
