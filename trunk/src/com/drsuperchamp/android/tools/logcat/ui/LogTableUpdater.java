package com.drsuperchamp.android.tools.logcat.ui;

import com.drsuperchamp.android.tools.logcat.core.FilterOutput;
import com.drsuperchamp.android.tools.logcat.core.LogCatWrapper.LogMessage;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class LogTableUpdater implements FilterOutput {
    private LogTableModel mTableModel;
    private MyRunnable mUpdateTableRunnable = new MyRunnable();

    public LogTableUpdater(LogTableModel model) {
        mTableModel = model;
    }

    @Override
    public void out(String filterName, LogMessage[] newMessages, int numRemoved) {
        mUpdateTableRunnable.newMessages = newMessages;
        mUpdateTableRunnable.numRemoved = numRemoved;
        try {
            SwingUtilities.invokeAndWait(mUpdateTableRunnable);
        } catch (InterruptedException e) {
        } catch (InvocationTargetException e) {
        }
    }

    private final class MyRunnable implements Runnable {
        public LogMessage[] newMessages = null;
        public int numRemoved = 0;
        @Override
        public void run() {
            if (numRemoved > 0)
                mTableModel.removeLogMessages(numRemoved);
            if (newMessages != null && newMessages.length > 0)
                mTableModel.addLogMessages(newMessages);
        }
    }
}
