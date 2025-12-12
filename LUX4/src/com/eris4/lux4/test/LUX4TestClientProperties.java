/*
* Copyright (c) 2002-2006 ERIS4. All Rights Reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*
* $Author: aiello $ - $Date: 2005/10/26 10:49:32 $
* $Name:  $ - $Revision: 1.3 $
*/
package com.eris4.lux4.test;

import java.util.PropertyResourceBundle;

import org.apache.log4j.Logger;

class LUX4TestClientProperties {
	static private Logger logger = Logger.getLogger(LUX4TestClientProperties.class);

	static private String propertiesName = "lux4testclient";
	static private PropertyResourceBundle properties =
	        (PropertyResourceBundle) PropertyResourceBundle.getBundle(propertiesName);

	private static final String getProperty(String key) {
		try {
  		return properties.getString(key);
		}
		catch (Exception e) {
			return null;
		}
	}

	static final String getString(String propertyName, String defaultValue) {
		String out = defaultValue;
		String propertyStr = getProperty(propertyName);
		if (propertyStr != null) {
		    out = propertyStr;
		}
    logger.debug(propertyName + " = " + out);
		return out;
	}

	static final int getInt(String propertyName, int defaultValue) {
		int out = defaultValue;
		String propertyStr = getProperty(propertyName);
		if (propertyStr != null) {
		    out = Integer.parseInt(propertyStr);
		}
    logger.debug(propertyName + " = " + out);
		return out;
	}

	static final long getLong(String propertyName, long defaultValue) {
		long out = defaultValue;
		String propertyStr = getProperty(propertyName);
		if (propertyStr != null) {
		    out = Long.parseLong(propertyStr);
		}
    logger.debug(propertyName + " = " + out);
		return out;
	}

	static final double getDouble(String propertyName, double defaultValue) {
		double out = defaultValue;
		String propertyStr = getProperty(propertyName);
		if (propertyStr != null) {
		    out = Double.parseDouble(propertyStr);
		}
    logger.debug(propertyName + " = " + out);
		return out;
	}

	static final boolean getBoolean(String propertyName, boolean defaultValue) {
		boolean out = defaultValue;
		String propertyStr = getProperty(propertyName);
		if (propertyStr != null) {
		    out = propertyStr.equalsIgnoreCase("true");
		}
    logger.debug(propertyName + " = " + out);
		return out;
	}

}
