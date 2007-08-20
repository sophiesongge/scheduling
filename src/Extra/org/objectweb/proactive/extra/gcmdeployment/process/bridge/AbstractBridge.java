package org.objectweb.proactive.extra.gcmdeployment.process.bridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.proactive.extra.gcmdeployment.Helpers;
import org.objectweb.proactive.extra.gcmdeployment.PathElement;
import org.objectweb.proactive.extra.gcmdeployment.process.Bridge;
import org.objectweb.proactive.extra.gcmdeployment.process.CommandBuilder;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;


public abstract class AbstractBridge implements Bridge {
    private String commandPath;
    private String env;
    private String hostname;
    private String username;
    private String id;

    public void setCommandPath(String commandPath) {
        this.commandPath = commandPath;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEnvironment(String env) {
        this.env = env;
    }

    protected String getCommandPath() {
        return commandPath;
    }

    protected String getHostname() {
        return hostname;
    }

    protected String getUsername() {
        return username;
    }

    protected String getEnv() {
        return env;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /* ------
     * Infrastructure tree operations & data
     */
    private List<Bridge> bridges = Collections.synchronizedList(new ArrayList<Bridge>());
    private List<Group> groups = Collections.synchronizedList(new ArrayList<Group>());
    private HostInfo hostInfo = null;

    public void addBridge(Bridge bridge) {
        bridges.add(bridge);
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public List<Bridge> getBridges() {
        return bridges;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(HostInfo hostInfo) {
        assert (hostInfo == null);
        this.hostInfo = hostInfo;
    }

    public void check() throws IllegalStateException {
        if (hostname == null) {
            throw new IllegalStateException("hostname is not set in " + this);
        }

        if (id == null) {
            throw new IllegalStateException("id is not set in " + this);
        }

        for (Bridge bridge : bridges)
            bridge.check();

        for (Group group : groups)
            group.check();

        if (hostInfo == null) {
            throw new IllegalStateException("hostInfo is not set in " + this);
        }
        hostInfo.check();
    }

    public List<String> buildCommands(CommandBuilder commandBuilder) {
        List<String> commands = new ArrayList<String>();

        if (hostInfo != null) {
            commands.add(commandBuilder.buildCommand(hostInfo));
        }

        for (Group group : groups) {
            commands.addAll(group.buildCommands(commandBuilder));
        }

        for (Bridge bridge : bridges) {
            commands.addAll(bridge.buildCommands(commandBuilder));
        }

        // Prefix each command with this bridge
        List<String> ret = new ArrayList<String>();
        for (String command : commands) {
            ret.add(internalBuildCommand() + " " +
                Helpers.escapeCommand(command));
        }

        return ret;
    }

    /**
     * Returns the command corresponding to this bridge
     *
     * This method is called by the generic AbstractBridge.buildCommand
     * method to prepend this bridge to retrieved command for children nodes
     * of the tree
     *
     * @return
     */
    abstract public String internalBuildCommand();
}
