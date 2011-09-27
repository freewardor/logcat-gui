package com.drsuperchamp.android.tools.logcat;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.AndroidDebugBridge.IClientChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdbWrapper implements IClientChangeListener, IDeviceChangeListener {
    private static AdbWrapper sSingletonInstance = null;
    private DeviceConnectionListener mDeviceStateListener = null;
    private boolean mIsAdbInitialized = false;
    private List<IDevice> mConnectedDevices = new ArrayList<IDevice>();

    /**
     * Interface to listen device connection states.
     *
     */
    public static interface DeviceConnectionListener {
        void deviceConnected(String serialNumber);
        void deviceDisconnected(String serialNumber);
    }

    /**
     * Interface to receive shell-command output.
     * This wrapper is to hide any interfaces from ddmlib.
     * @see {@link #executeShellCommand(String,String,ShellOutputReceiver)}.
     *
     */
    public static interface ShellOutputReceiver extends IShellOutputReceiver {
    }

    /**
     * singleton.. so..
     */
    private AdbWrapper() {
    }

    public static synchronized AdbWrapper getInstance() {
        if (sSingletonInstance == null) {
            sSingletonInstance = new AdbWrapper();
        }
        return sSingletonInstance;
    }

    private static boolean checkPath(String filePath) {
        if (filePath != null ) {
            if ((new File(filePath)).exists()) {
                return true;
            } else {
                Util.DbgLog("File not found: " + filePath);
            }
        } else {
            Util.DbgLog("filePath is null");
        }
        return false;
    }

    public boolean connect(String adbFilePath, DeviceConnectionListener listener) {
        if (!checkPath(adbFilePath)) {
            Util.DbgLog("Error occured in setting adb binary file path");
            return false;
        }

        if (mIsAdbInitialized) {
            Util.DbgLog("Already connected..");
            return false;
        }

        mDeviceStateListener = listener;

        AndroidDebugBridge.init(false /* no need to support debug*/);
        AndroidDebugBridge.addClientChangeListener(this);
        AndroidDebugBridge.addDeviceChangeListener(this);
        AndroidDebugBridge.createBridge(adbFilePath, true /* forceNewBridge */);
        mIsAdbInitialized = true;
        return true;
    }

    public void disconnect() {
        if (!mIsAdbInitialized) {
            Util.DbgLog("not connected..");
            return;
        }
        mDeviceStateListener = null;
        AndroidDebugBridge.disconnectBridge();
    }

    public String[] getConnectedDevices() {
        String[] serialNums;
        synchronized(mConnectedDevices) {
            serialNums = new String[mConnectedDevices.size()];
            int n = 0;
            for(IDevice device : mConnectedDevices) {
                serialNums[n++] = new String(device.getSerialNumber());
            }
        }
        return serialNums;
    }

    public boolean executeShellCommand(String devSerialNumber, String shellCmd, ShellOutputReceiver receiver) {
        IDevice device;
        if ((device = findDevice(devSerialNumber)) == null)
            return false;
        try {
            device.executeShellCommand(shellCmd, receiver, 0 /*timeout*/);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private IDevice findDevice(String serialNumber) {
        synchronized(mConnectedDevices) {
            for(IDevice device : mConnectedDevices) {
                if (device.getSerialNumber().equals(serialNumber))
                    return device;
            }
        }
        return null;
    }

    @Override
    public void clientChanged(Client client, int changeMask) {
        Util.DbgLog();
    }

    @Override
    public void deviceChanged(IDevice device, int changeMask) {
        Util.DbgLog();
    }

    @Override
    public void deviceConnected(IDevice device) {
        Util.DbgLog();
        synchronized(mConnectedDevices) {
            mConnectedDevices.add(device);
        }

        if (mDeviceStateListener != null) {
            mDeviceStateListener.deviceConnected(device.getSerialNumber());
        }
    }

    @Override
    public void deviceDisconnected(IDevice device) {
        Util.DbgLog();
        synchronized(mConnectedDevices) {
            mConnectedDevices.remove(device);
        }

        if (mDeviceStateListener != null) {
            mDeviceStateListener.deviceDisconnected(device.getSerialNumber());
        }
    }
}
