package com.drsuperchamp.android.tools.logcat.ui;

import com.drsuperchamp.android.tools.logcat.core.AdbWrapper;
import com.drsuperchamp.android.tools.logcat.core.AdbWrapper.DeviceConnectionListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class MainFrame extends JFrame implements DeviceConnectionListener {
    private final String ADB_BIN_PATH;
    private AdbWrapper mAdb = null;
    private String mConnectedDevSerialNum = null;
    private List<JTable> mTables = new ArrayList<JTable>();
    private LogTableModel mLogModel;
    private JButton mBtnConnect;

    public MainFrame(String adb_bin_path) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        JMenuItem mntmLoad = new JMenuItem("Load");
        mnFile.add(mntmLoad);

        JToolBar toolBar = new JToolBar();
        getContentPane().add(toolBar, BorderLayout.NORTH);

        mBtnConnect = new JButton("No device");
        mBtnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                LogTableUpdater updater = new LogTableUpdater(mLogModel);
            }
        });
        mBtnConnect.setEnabled(false);
        toolBar.add(mBtnConnect);

        JButton btnAddAFilter = new JButton("Add a filter");
        toolBar.add(btnAddAFilter);

        JButton btnRemoveAFilter = new JButton("Remove a filter");
        toolBar.add(btnRemoveAFilter);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane();
        tabbedPane.addTab("Log", null, scrollPane, null);

        mLogModel = new LogTableModel();
        JTable table = new JTable(mLogModel);
        table.setName("log_table");
        scrollPane.setViewportView(table);
        mTables.add(table);

        ADB_BIN_PATH = adb_bin_path;
        mAdb = AdbWrapper.getInstance();
        mAdb.connect(ADB_BIN_PATH, this);
    }

    @Override
    public void deviceConnected(String devSerialNumber) {
        mConnectedDevSerialNum = new String(devSerialNumber);
        runInEventThread(new Runnable() {
            @Override
            public void run() {
                mBtnConnect.setText("Connect: "+mConnectedDevSerialNum);
                mBtnConnect.setEnabled(true);
            }
        }, true);
    }

    private void runInEventThread(Runnable r, boolean isSynchronous) {
        if (isSynchronous) {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InterruptedException e) {
            } catch (InvocationTargetException e) {
            }
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    @Override
    public void deviceDisconnected(String devSerialNumber) {
        mConnectedDevSerialNum = null;
        runInEventThread(new Runnable() {
            @Override
            public void run() {
                mBtnConnect.setText("No device");
                mBtnConnect.setEnabled(false);
            }
        }, true);
    }

    public static void main(String[] args) {
        final String ADB_PATH = "/home/wpark/android/sdk/android-sdk-linux_x86/platform-tools/adb";
        MainFrame frame = new MainFrame(ADB_PATH);
        frame.setVisible(true);
    }
}
