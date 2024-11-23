package org.atcraftmc.ofmlite;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;

public interface ProcessKiller {

    // 发送 Ctrl+C 信号的跨平台方法
    static void sendCtrlC(int pid) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            sendCtrlCWindows(pid);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            sendCtrlCUnix(pid);
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + os);
        }
    }

    // Windows 平台实现
    private static void sendCtrlCWindows(int pid) {
        // 加载 Kernel32
        Kernel32Ext kernel32 = Native.load("kernel32", Kernel32Ext.class);
        boolean result = kernel32.GenerateConsoleCtrlEvent(0, pid); // 0 = CTRL_C_EVENT
        if (!result) {
            throw new RuntimeException("Failed to send Ctrl+C on Windows. PID: " + pid);
        }
        System.out.println("Sent Ctrl+C to process " + pid + " on Windows.");
    }

    // UNIX/Linux 平台实现
    private static void sendCtrlCUnix(int pid) {
        // 加载 libc
        CLibrary cLib = Native.load("c", CLibrary.class);
        int result = cLib.kill(pid, 2); // 2 = SIGINT
        if (result != 0) {
            throw new RuntimeException("Failed to send Ctrl+C on UNIX/Linux. PID: " + pid);
        }
        System.out.println("Sent Ctrl+C to process " + pid + " on UNIX/Linux.");
    }

    // 定义 Windows 平台的接口
    interface Kernel32Ext extends Kernel32 {
        boolean GenerateConsoleCtrlEvent(int dwCtrlEvent, int dwProcessGroupId);
    }

    // 定义 UNIX/Linux 平台的接口
    interface CLibrary extends Library {
        int kill(int pid, int signal);
    }
}
