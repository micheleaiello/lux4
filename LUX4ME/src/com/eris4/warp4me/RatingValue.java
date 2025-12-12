/*
* Copyright ERIS4. All rights reserved.
* ERIS4 PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
* Author: Stefano Antonelli
* Date: Mar 11, 2003.
*/

package com.eris4.warp4me;

/**
 * It is the value used in all the rating and authorization methods. In is a double value with a unit identifier.
 */
public class RatingValue {

    public static final char SEPARATOR = '@';

    private String unit;
    private long amount;

    /**
     * Null constructor
     */
    public RatingValue() {

    }

    public RatingValue(RatingValue aValue) {
        this.amount = aValue.amount;
        this.unit = aValue.unit;
    }

    /**
     * Copy constructor
     *
     * @param amount the amount
     * @param unit the unit identifier
     */
    public RatingValue(long amount, String unit) {
        this.amount = amount;
        this.unit = unit;
    }

    /**
     * Returns the unit identifier
     * @return the unit identifier
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Returns the amount
     * @return the amount
     */
    public long getAmount() {
        return amount;
    }
    /**
     * Sets the unit identifier
     *
     * @param unit the unit identifier
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Sets the amount
     *
     * @param amount the amount
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }

    /**
     * Returns the String rapresentation of the RatingValue.
     *
     * @return the String rapresentation of the RatingValue
     */
    public String toString() {
        if (!(unit==null) && !unit.equals("")) {
            StringBuffer buff = new StringBuffer(25);

            buff.append(amount);
            buff.append(SEPARATOR);
            buff.append(unit);

            return buff.toString();
        }
        else {
            return String.valueOf(amount);
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof RatingValue) {
            RatingValue ratingValue = (RatingValue) obj;
            if ((ratingValue.amount == this.amount) && (this.unit == ratingValue.unit)) {
                return true;
            }
            return false;
        }
        return false;
    }

}
