package org.atcraftmc.ofmlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Instance {
    private final LogRecordQueue queue = new LogRecordQueue(Integer.parseInt(SharedContext.CONFIG.getProperty("tunnel_log_count")));
    private final String id;
    private final String token;
    private final String name;
    private final boolean autoStart;
    private InstanceListener listener;
    private boolean running = false;
    private Process process;

    public Instance(String id, String token, String name, boolean autoStart) {
        this.id = id;
        this.token = token;
        this.name = name;
        this.autoStart = autoStart;

        if (autoStart) {
            start();
        }
    }

    public void append(String msg) {
        this.queue.add(msg);
        if (this.listener != null) {
            this.listener.acceptCommandLine(this, msg);
        }
    }

    public void setListener(InstanceListener listener) {
        this.listener = listener;
    }

    public void terminate() {
        if (!this.running) {
            throw new IllegalStateException("Cannot terminate not-running instance!");
        }

        this.running = false;

        this.append("[OFMLite] 正在关闭...");
        try {
            this.process.getOutputStream().write(3);
            this.process.getOutputStream().flush();

            ProcessKiller.sendCtrlC((int) this.process.pid());

            this.process.destroy();
            this.process.destroyForcibly();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.waitUntilStop();

        Launch.showNotification("隧道关闭", "隧道%s(#%s)已关闭!".formatted(this.name, this.id));
    }

    private void waitUntilStop() {
        int exitCode;
        try {
            exitCode = this.process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        append("[OFMLite] 进程已退出, exitCode=" + exitCode);

        this.running = false;
        this.listener.onInstanceStateChange(this, false, exitCode);
        this.process.destroy();
    }

    public void start() {
        if (this.running) {
            throw new IllegalStateException("Cannot start running instance!");
        }
        var config = SharedContext.CONFIG;

        var exec = config.getProperty("frpc").replace("{runtime}", SharedContext.RUNTIME_PATH);
        var cmd = config.getProperty("command")
                .replace("{tunnel}", this.id)
                .replace("{token}", this.token)
                .replace("{#global_token}", SharedContext.CONFIG.getProperty("global_token")).split(" ");

        this.append("[OFMLite] 正在启动隧道...");

        var args = new String[cmd.length + 1];

        args[0] = exec;

        System.arraycopy(cmd, 0, args, 1, cmd.length);

        var builder = new ProcessBuilder(args);
        builder.redirectErrorStream(true);

        try {
            var proc = builder.start();
            this.process = proc;

            append("[OFMLite] 进程已创建, pid=" + proc.pid());

        } catch (IOException e) {
            append("[OFMLite] 出现异常: " + e.getMessage());
            return;
        }

        new Thread(() -> {
            try {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(this.process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        this.append(line);
                    }
                }

                this.process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        this.running = true;

        Launch.showNotification("隧道启动", "隧道%s(#%s)启动成功!".formatted(this.name, this.id));
    }

    public boolean isRunning() {
        return this.running;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public LogRecordQueue getQueue() {
        return queue;
    }

    @Override
    public String toString() {
        return "%s[#%s] - %s".formatted(this.name, this.id, this.running ? "运行中" : "就绪");
    }

    public Process getProcess() {
        return this.process;
    }
}
