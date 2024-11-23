package org.atcraftmc.ofmlite;

public interface InstanceListener {
    void acceptCommandLine(Instance instance, String commandLine);

    void onInstanceStateChange(Instance instance, boolean newState, int code);
}
