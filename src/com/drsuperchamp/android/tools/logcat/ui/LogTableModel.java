package com.drsuperchamp.android.tools.logcat.ui;

import com.drsuperchamp.android.tools.logcat.core.LogCatWrapper.LogMessage;
import com.drsuperchamp.android.tools.logcat.core.LogCatWrapper.LogMessageInfo;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class LogTableModel extends AbstractTableModel {
    private static final String[] COLUMN_NAMES = {"Time", " ", "pid", "tag", "Message"};
    private List<LogMessage> mLogs = new LinkedList<LogMessage>();

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_NAMES[columnIndex].getClass();
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public int getRowCount() {
        return mLogs.size();
    }

    public void addLogMessages(LogMessage[] messages) {
        int firstRow = mLogs.size();
        int loop_end = messages.length;
        for(int n=0; n < loop_end; n++) {
            mLogs.add(messages[n]);
        }

        if (loop_end > 0) {
            fireTableRowsInserted(firstRow, firstRow+loop_end-1);
        }
    }

    public void removeLogMessages(int numRemovedRows) {
        for(int n=0; n < numRemovedRows; n++) {
            if (mLogs.isEmpty() == false)
                mLogs.remove(0);
        }
        if (numRemovedRows > 0) {
            fireTableRowsDeleted(0, numRemovedRows-1);
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LogMessage msg = null;
        try {
            msg = mLogs.get(rowIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        LogMessageInfo msgInfo = msg.data;
        switch(columnIndex) {
            case 0:
                return msgInfo.time;
            case 1:
                return msgInfo.logLevel.getPriorityLetter();
            case 2:
                return msgInfo.pid;
            case 3:
                return msgInfo.tag;
            case 4:
                return msg.msg;
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
