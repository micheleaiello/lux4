/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Mar 16, 2003.
*/

package com.eris4.warp4me;

import java.util.*;

public abstract class RatingEvent {
    private static final String PACKAGE="com.eris4.warp4me.events";
    private static final char SEPARATOR=',';
    private String[] fields;
    private String type;

    protected RatingEvent(){
        fields=new String[getSize()];
    }

    protected abstract int getSize();

    private void setType(String type){
        if(type==null){
            this.type="";
        }
        this.type=type;
    }

    static public RatingEvent createEvent(String type,String values[]){
        RatingEvent e=createEvent(type);
        for(int i=0;i<values.length;i++){
            e.setValue(i,values[i]);
        }
        return e;
    }

    protected final void setValue(int position, String value) {
        if(position>=0 && position<fields.length){
            fields[position]=value;
        }
    }

    static public RatingEvent createEvent(String type){
        String className=PACKAGE+"."+type;
        RatingEvent e=null;
        try {
            Class eventClass =Class.forName(className);
            e=(RatingEvent) eventClass.newInstance();
        } catch (ClassNotFoundException ex) {
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        }
        e.setType(type);
        return e;
    }

    public String toString(){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<fields.length;i++){
            if(i!=0){
                sb.append(SEPARATOR);
            }
            sb.append(fields[i]);
        }
        String ret=sb.toString();
        return ret;
    }

    public String getType(){
        return type;
    }

}
