/**
 * User: Stefano Antonelli
 * Date: Nov 7, 2005
 * Time: 11:32:45 AM
 */

package com.eris4.lux4.client;

public class ConnectionException extends Exception {
    public static final int COMPLETED_MAYBE=0;
    public static final int COMPLETED_NO=1;

    private int completion;

    public ConnectionException(int completion) {
        super();
        this.completion = completion;
    }

    public ConnectionException(String message, int completion) {
        super(message);
        this.completion = completion;
    }

    public ConnectionException(Throwable cause, int completion) {
        super(cause);
        this.completion = completion;
    }

    public ConnectionException(String message, Throwable cause, int completion) {
        super(message, cause);
        this.completion = completion;
    }
}
