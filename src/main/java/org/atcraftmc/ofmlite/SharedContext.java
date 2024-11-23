package org.atcraftmc.ofmlite;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public interface SharedContext {
    String RUNTIME_PATH = System.getProperty("user.dir");

    Properties CONFIG = new Properties();
    HashMap<String, Instance> INSTANCES = new HashMap<>();


    private static void setDefaults(){
        if(!CONFIG.containsKey("global_token")){
            CONFIG.setProperty("global_token", "[empty]");
        }
        if(!CONFIG.containsKey("frpc")){
            CONFIG.setProperty("frpc", "{runtime}/frpc_windows_amd64.exe");
        }
        if(!CONFIG.containsKey("theme")){
            CONFIG.setProperty("theme", "Darcula");
        }
        if(!CONFIG.containsKey("command")){
            CONFIG.setProperty("command", "-u {token} -p {tunnel}");
        }
        if(!CONFIG.containsKey("tunnel_log_count")){
            CONFIG.setProperty("tunnel_log_count", String.valueOf(1000));
        }
    }

    static void loadConfig() {
        try {
            var f = new File(RUNTIME_PATH + "/config.properties");

            if (!f.exists()) {
                f.createNewFile();
                setDefaults();
                try (var o = new FileOutputStream(f)) {
                    CONFIG.store(o, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                CONFIG.load(new FileInputStream(f));
                setDefaults();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void loadInstances() {
        for (var i : INSTANCES.values()) {
            i.terminate();
        }

        INSTANCES.clear();

        var p = new Properties();
        var f = new File(RUNTIME_PATH + "/instances.properties");

        try {
            if (!f.exists()) {
                f.createNewFile();
            }

            try (var i = new FileInputStream(f)) {
                p.load(i);
            }

            for (var s : p.keySet()) {
                var name = s.toString();

                var args = p.getProperty(name).split(":");

                var instance = new Instance(
                        args[0],
                        args[1],
                        name,
                        Boolean.parseBoolean(args[2])
                );

                INSTANCES.put(name, instance);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void saveInstances() {
        var f = new File(RUNTIME_PATH + "/instances.properties");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }

            var p = new Properties();

            for (var s : INSTANCES.keySet()) {
                var instance = INSTANCES.get(s);

                var tunnelId = instance.getId();
                var token = instance.getToken();
                var name = instance.getName();

                p.setProperty(name, "%s:%s:%s".formatted(tunnelId, token, instance.isAutoStart()));
            }

            try (var o = new FileOutputStream(f)) {
                p.store(o, "");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void saveConfig() {
        try {
            CONFIG.store(new FileOutputStream(RUNTIME_PATH + "/config.properties"), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void setTheme(Class<? extends FlatLaf> clazz) {
        try {
            clazz.getDeclaredMethod("setup").invoke(null);
            UIManager.setLookAndFeel(clazz.getDeclaredConstructor().newInstance());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
