package org.atcraftmc.ofmlite;

import com.formdev.flatlaf.FlatDarkLaf;
import org.atcraftmc.ofmlite.ui.MainWindowUI;
import org.atcraftmc.ofmlite.ui.SelectFrpcUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Objects;

public final class Launch {
    public static void main(String[] args) {
        SharedContext.setTheme(FlatDarkLaf.class);
        SharedContext.loadConfig();

        var icon = new ImageIcon(Objects.requireNonNull(Launch.class.getClassLoader().getResource("icon.png")));
        var trayLogo = new ImageIcon(Objects.requireNonNull(Launch.class.getClassLoader().getResource("tray.png")));

        var frame = new JFrame("OFManagerLite - 1.0");
         // 确保有一个合适的图标文件

        if (!SystemTray.isSupported()) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            var systemTray = SystemTray.getSystemTray();
            var popupMenu = new PopupMenu();
            var trayIcon = new TrayIcon(trayLogo.getImage(), "OFMLite-托盘", popupMenu);
            var showUI = new MenuItem("Display");
            var quit = new MenuItem("Quit");

            try {
                // 将托盘图标添加到系统托盘
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }

            showUI.addActionListener(e -> frame.setVisible(true));
            quit.addActionListener(e -> System.exit(0));
            trayIcon.addActionListener(e -> frame.setVisible(true));

            popupMenu.add(showUI);
            popupMenu.add(quit);
            //Runtime.getRuntime().addShutdownHook(new Thread(() -> systemTray.remove(trayIcon)));
        }

        frame.setContentPane(new MainWindowUI().$$$getRootComponent$$$());
        frame.setSize(1280, 720);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setIconImage(icon.getImage());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (var i : SharedContext.INSTANCES.values()) {
                if (!i.isRunning()) {
                    continue;
                }
                var process = i.getProcess();

                if (process == null) {
                    continue;
                }

                process.destroy();
                process.destroyForcibly();
            }
        }));

        var file = new File(SharedContext.CONFIG.getProperty("frpc").replace("{runtime}", SharedContext.RUNTIME_PATH));

        if (!file.exists() || file.length() == 0) {
            SelectFrpcUI.open(null);
        }
    }


    // 弹出系统通知
    public static void showNotification(String title, String message) {
        if (!SystemTray.isSupported()) {
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon trayIcon = tray.getTrayIcons()[0];
        trayIcon.displayMessage("OFMLite-" + title, message, TrayIcon.MessageType.INFO);
    }
}
