package com.bbn.protelis.networkresourcemanagement;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;

import org.protelis.lang.datatype.DeviceUID;
import org.protelis.lang.datatype.Tuple;
import org.protelis.vm.ProtelisProgram;
import org.protelis.vm.ProtelisVM;
import org.protelis.vm.impl.AbstractExecutionContext;
import org.protelis.vm.impl.SimpleExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * A server in the network.
 */
public class NetworkServer extends AbstractExecutionContext
        implements NetworkStateProvider, RegionNodeStateProvider, RegionServiceStateProvider, NetworkNode {

    /**
     * Used when there is no region name.
     */
    public static final StringRegionIdentifier NULL_REGION = new StringRegionIdentifier("__null-region__");

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkServer.class);

    /** Device numerical identifier */
    private final NodeIdentifier uid;

    /** The Protelis VM to be executed by the device */
    private final ProtelisVM vm;

    /**
     * Default time to sleep between executions. Specified in milliseconds.
     */
    public static final long DEFAULT_SLEEP_TIME_MS = 2 * 1000;

    /**
     * The key into extra data passed to {@link #processExtraData(Map)} that
     * specifies the region for a node. This will create a
     * {@link StringRegionIdentifier}.
     */
    public static final String EXTRA_DATA_REGION_KEY = "region";

    /**
     * Extra data key to specify if the node is a single server or a pool of
     * servers.
     */
    public static final String EXTRA_DATA_POOL = "pool";

    private AtomicLong executionCount = new AtomicLong(0);

    private void incrementExecutionCount() {
        executionCount.incrementAndGet();
    }

    private boolean pool = false;

    private void setPool(final boolean v) {
        pool = v;
    }

    /**
     * 
     * @return true if this Node is a pool of resources
     * @see #EXTRA_DATA_POOL
     */
    public boolean isPool() {
        return pool;
    }

    /**
     * The number of times this node has executed.
     * 
     * @return the number of times that this {@link NetworkServer} has executed
     *         the program.
     */
    public final long getExecutionCount() {
        return executionCount.get();
    }

    private long sleepTime = DEFAULT_SLEEP_TIME_MS;

    /**
     * @return How many milliseconds between executions of the protelis program.
     *         Defaults to {@link #DEFAULT_SLEEP_TIME_MS}.
     */
    public final long getSleepTime() {
        return sleepTime;
    }

    /**
     * @param v
     *            Specify the sleep time
     * @see #getSleepTime()
     */
    public final void setSleepTime(final long v) {
        sleepTime = v;
    }

    /**
     * The neighboring nodes. Key is the neighbor, value is the datarate in
     * bytes per second.
     */
    private final Map<NodeIdentifier, Double> neighbors = new HashMap<>();

    @Override
    @Nonnull
    public final Set<NodeIdentifier> getNeighbors() {
        return Collections.unmodifiableSet(neighbors.keySet());
    }

    @Override
    public final void addNeighbor(@Nonnull final NodeIdentifier v, final double bandwidth) {
        neighbors.put(v, bandwidth);
    }

    @Override
    public final void addNeighbor(@Nonnull final NetworkNode v, final double bandwidth) {
        addNeighbor(v.getNodeIdentifier(), bandwidth);
    }

    /**
     * @param bandwidthLinkAttribute
     *            the {@link LinkAttribute} to associate with each bandwidth
     *            value
     * @return link capacity to neighbors
     * @see #addNeighbor(NodeIdentifier, double)
     * @see ResourceReport#getNetworkCapacity()
     */
    @Nonnull
    public ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>>
            getNeighborLinkCapacity(final LinkAttribute<?> bandwidthLinkAttribute) {
        ImmutableMap.Builder<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> builder = ImmutableMap.builder();
        neighbors.forEach((k, v) -> builder.put(k, ImmutableMap.of(bandwidthLinkAttribute, v)));
        return builder.build();
    }

    /**
     * Creates a {@link NetworkServer} with a {@link NullResourceManager}.
     * 
     * @param program
     *            the program to run on the node
     * @param name
     *            the name of the node (must be unique)
     * @param lookupService
     *            How to find other nodes
     */
    public NetworkServer(@Nonnull final NodeLookupService lookupService,
            @Nonnull final ProtelisProgram program,
            @Nonnull final String name) {
        super(new SimpleExecutionEnvironment(), new NodeNetworkManager(lookupService));
        this.uid = new StringNodeIdentifier(name);
        this.region = NULL_REGION;
        this.networkState = new NetworkState(this.region);
        this.regionNodeState = new RegionNodeState(this.region);
        this.resourceManager = new NullResourceManager(this.uid);

        // Finish making the new device and add it to our collection
        vm = new ProtelisVM(program, this);
    }

    /**
     * @return Accessor for virtual machine, to allow external execution
     *         triggering
     */
    public final ProtelisVM getVM() {
        return vm;
    }

    /**
     * Expose the network manager. This is to allow external simulation of
     * network For real devices, the NetworkManager usually runs autonomously in
     * its own thread(s).
     * 
     * @return the node specific version of the network manager
     */
    public final NodeNetworkManager accessNetworkManager() {
        return (NodeNetworkManager) super.getNetworkManager();
    }

    /**
     * 
     * @return the name of the node
     */
    public final String getName() {
        return uid.getName();
    }

    @Override
    public final NodeIdentifier getNodeIdentifier() {
        return uid;
    }

    @Override
    public final DeviceUID getDeviceUID() {
        return uid;
    }

    @Override
    public final Number getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * Child context used for {@link NetworkServer#instance()}.
     */
    public class ChildContext extends AbstractExecutionContext {
        private NetworkServer parent;

        /**
         * Create a child context.
         * 
         * @param parent
         *            the parent environment to get information from.
         */
        public ChildContext(final NetworkServer parent) {
            super(parent.getExecutionEnvironment(), parent.getNetworkManager());
            this.parent = parent;
        }

        @Override
        public Number getCurrentTime() {
            return parent.getCurrentTime();
        }

        @Override
        public NodeIdentifier getDeviceUID() {
            return parent.getNodeIdentifier();
        }

        @Override
        public double nextRandomDouble() {
            return parent.nextRandomDouble();
        }

        @Override
        protected AbstractExecutionContext instance() {
            return new ChildContext(parent);
        }

        // TODO: need to better fix the whole ChildContext architecture
        /**
         * @return the region of the parent
         */
        public RegionIdentifier getRegion() {
            return parent.getRegionIdentifier();
        }

        /**
         * 
         * @return parent network state
         */
        public NetworkState getNetworkState() {
            return parent.getNetworkState();
        }

        /**
         * 
         * @return parent region node state
         */
        public RegionNodeState getRegionNodeState() {
            return parent.getRegionNodeState();
        }
    }

    @Override
    protected AbstractExecutionContext instance() {
        return new ChildContext(this);
    }

    @Override
    public final double nextRandomDouble() {
        return Math.random();
    }

    /**
     * Executed before {@link ProtelisVM#runCycle()}.
     */
    protected void preRunCycle() {
    }

    /**
     * Executed after {@link ProtelisVM#runCycle()}.
     */
    protected void postRunCycle() {
    }

    private Exception programLoopException = null;

    /**
     * This is used for test cases. One can check this value to see if the
     * execute loop exited due to an exception.
     * 
     * @return the exception thrown in the program loop, null otherwise
     */
    public Exception getExceptionThrownInProgramLoop() {
        return programLoopException;
    }

    /**
     * Execute the protolis program.
     */
    private void run() {
        while (running) {
            try {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("{}: Executing preRunCycle", getNodeIdentifier());
                }
                preRunCycle();
                if (!running) {
                    break;
                }

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("{}: runCycle", getNodeIdentifier());
                }
                getVM().runCycle(); // execute the Protelis program
                incrementExecutionCount();
                if (!running) {
                    break;
                }

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("{}: Executing postRunCycle", getNodeIdentifier());
                }
                postRunCycle();
                if (!running) {
                    break;
                }

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("{}: sleep", getNodeIdentifier());
                }
                Thread.sleep(sleepTime);
            } catch (final InterruptedException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Node " + getName() + " got interrupted, waking up to check if it's time to exit", e);
                }
            } catch (final Exception e) {
                LOGGER.error("Exception thrown: terminating Protelis on node: " + getName(), e);
                programLoopException = e;
                break;
            }
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Exiting Protelis loop on node: " + getName());
        }
    }

    private final Object lock = new Object();

    private Thread executeThread = null;

    /**
     * 
     * @return is the node currently executing?
     */
    public final boolean isExecuting() {
        synchronized (lock) {
            return running && null != executeThread && executeThread.isAlive();
        }
    }

    private boolean running = false;

    /**
     * Start the node executing.
     */
    public final void startExecuting() {
        synchronized (lock) {
            if (running) {
                throw new IllegalStateException("Already executing, cannot start again!");
            }

            running = true;

            accessNetworkManager().start(this);

            executeThread = new Thread(() -> run());
            executeThread.setName("Node-" + getName());
            executeThread.start();
        }
    }

    /**
     * Called at the top of {@link #stopExecuting()}.
     */
    protected void preStopExecuting() {
    }

    /**
     * Stop the node executing and wait for the stop.
     */
    public final void stopExecuting() {
        if (isExecuting()) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Before lock in stopExecuting on node: {}", getNodeIdentifier());
            }

            synchronized (lock) {
                running = false;
            }

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Executing preStopExecuting on node: {}", getNodeIdentifier());
            }
            preStopExecuting();

            // stop all network communication
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Stopping network manager on node: {}", getNodeIdentifier());
            }
            accessNetworkManager().stop();

            synchronized (lock) {
                if (null != executeThread) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("Interrupting and then joining node: {}", getNodeIdentifier());
                    }
                    executeThread.interrupt();

                    try {
                        executeThread.join(); // may want to have a timeout here

                        if (LOGGER.isTraceEnabled()) {
                            LOGGER.trace("Node finished: {}", getNodeIdentifier());
                        }
                    } catch (final InterruptedException e) {
                        LOGGER.debug("Got interrupted waiting for join, probably just time to shutdown", e);
                    }
                    executeThread = null;
                } // non-null executeThread
            } // lock
        } // isExecuting
    }

    private ResourceManager resourceManager;

    /**
     * 
     * @param resourceManager
     *            the new {@link ResourceManager} for this node
     */
    public void setResourceManager(@Nonnull final ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    /**
     * 
     * @return the resource manager for this node
     */
    @Nonnull
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    /**
     * Get the latest resource report. This method should be called once per
     * cycle and then used to make decisions. This method may call directly to
     * the {@link ResourceManager} to provide gather the information.
     * 
     * @return the latest resource report
     * @param demandWindow
     *            what window size to use for estimating demand
     */
    @Nonnull
    public ResourceReport getResourceReport(@Nonnull final ResourceReport.EstimationWindow demandWindow) {
        return resourceManager.getCurrentResourceReport(demandWindow);
    }

    /**
     * Get the latest service report.
     * 
     * @return the lastest service report
     */
    @Nonnull
    public ServiceReport getServiceReport() {
        return resourceManager.getServiceReport();
    }

    private RegionIdentifier region;

    /**
     * Changing the region has the side effect of resetting the network state
     * and the regional node state.
     * 
     * @param region
     *            the new region that this node belongs to
     * @see #getNetworkState()
     */
    public void setRegion(final RegionIdentifier region) {
        this.region = region;
        this.networkState = new NetworkState(this.region);
        this.regionNodeState = new RegionNodeState(this.region);
    }

    @Override
    public RegionIdentifier getRegionIdentifier() {
        return this.region;
    }

    @Override
    public void processExtraData(@Nonnull final Map<String, Object> extraData) {
        final Object regionValue = extraData.get(EXTRA_DATA_REGION_KEY);
        if (null != regionValue) {
            final String regionName = regionValue.toString();
            final StringRegionIdentifier region = new StringRegionIdentifier(regionName);
            this.setRegion(region);
        }

        final Object pool = extraData.get(EXTRA_DATA_POOL);
        if (null != pool) {
            this.setPool(Boolean.parseBoolean(pool.toString()));
        }
    }

    private String hardware;

    @Override
    public String getHardware() {
        return hardware;
    }

    @Override
    public void setHardware(final String hardware) {
        this.hardware = hardware;
    }

    // ---- NetworkStateProvider
    private NetworkState networkState;

    @Override
    @Nonnull
    public NetworkState getNetworkState() {
        return networkState;
    }
    // ---- end NetworkStateProvider

    // ---- RegionNodeStateProvider
    private RegionNodeState regionNodeState;

    @Override
    @Nonnull
    public RegionNodeState getRegionNodeState() {
        return regionNodeState;
    }
    // ---- end RegionNodeStateProvider

    // --- RegionServiceStateProvider
    private RegionServiceState regionServiceState;

    @Override
    @Nonnull
    public RegionServiceState getRegionServiceState() {
        return regionServiceState;
    }
    // --- end RegionServiceStateProvider

    @Override
    public String toString() {
        return getName();
    }

    /**
     * This method is used by AP to call
     * {@link RegionNodeState#setResourceReports(com.google.common.collect.ImmutableSet)}.
     * 
     * @param tuple
     *            the list of reports as a tuple
     */
    public void setRegionResourceReports(final Tuple tuple) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Setting region resource reports. Region: " + getRegionIdentifier());
        }

        final ImmutableSet.Builder<ResourceReport> builder = ImmutableSet.builder();
        for (final Object entry : tuple) {
            final ResourceReport report = (ResourceReport) entry;
            builder.add(report);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Adding report for " + report.getNodeName());
            }
        }
        getRegionNodeState().setResourceReports(builder.build());

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Finished setting region resource reports.");
        }
    }

    /**
     * This method is used by AP to call
     * {@link RegionServiceState#setServiceReports(ImmutableSet)}.
     * 
     * @param tuple
     *            the list of reports as a tuple
     */
    public void setRegionServiceReports(final Tuple tuple) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Setting region service reports. Region: " + getRegionIdentifier());
        }

        final ImmutableSet.Builder<ServiceReport> builder = ImmutableSet.builder();
        for (final Object entry : tuple) {
            final ServiceReport report = (ServiceReport) entry;
            builder.add(report);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Adding report for " + report.getNodeName());
            }
        }
        getRegionServiceState().setServiceReports(builder.build());

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Finished setting region service reports.");
        }
    }

}
