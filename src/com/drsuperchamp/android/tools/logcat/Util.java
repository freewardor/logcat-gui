package com.drsuperchamp.android.tools.logcat;

public class Util {
    private static DebugOutput mOutputInterface = null;

    public static void DbgLog(String ... msg) {
        String s = null;
        if (msg.length == 0) {
            s = getMethod();
        } else {
            s = getMethod() + ": " + msg[0];
        }

        if (mOutputInterface != null) {
            mOutputInterface.out(s);
        } else {
            // default is 'console'
            System.out.println(s);
        }
    }

    public static void setDebugOutput(DebugOutput outInterface) {
        mOutputInterface = outInterface;
    }

    private static String getMethod()
    {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        StackTraceElement currentStack = stacks[2];
        return currentStack.getMethodName();
    }
}
