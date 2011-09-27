package com.drsuperchamp.android.tools.logcat;

import com.drsuperchamp.android.tools.logcat.LogCatWrapper.LogMessage;

public interface FilterOutput {
    void out(String name, LogMessage []newMessages, int numRemoved);
}
