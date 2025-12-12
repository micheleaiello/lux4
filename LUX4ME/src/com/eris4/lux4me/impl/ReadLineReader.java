/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Sep 24, 2003
*/
package com.eris4.lux4me.impl;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

public class ReadLineReader extends InputStreamReader{

    public ReadLineReader(InputStream in, String charsetName) throws UnsupportedEncodingException {
        super(in,charsetName);
    }

    String readLine() throws IOException {
        StringBuffer s = new StringBuffer();
        boolean eol=false;
        char c=(char) read() ;
        while(c!=-1 && !((c == '\n') || (c == '\r'))){
            s.append(c);
            c=(char) read() ;
        }
        return s.toString();
    }
}
