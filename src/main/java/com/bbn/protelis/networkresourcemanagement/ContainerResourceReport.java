package com.bbn.protelis.networkresourcemanagement;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

/**
 * Information about a container at a particular point in time.
 * 
 * See {@link ResourceReport} for details about load, capacity and demand.
 */
public class ContainerResourceReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Timestamp for null reports.
     */
    public static final long NULL_TIMESTAMP = -1;

    /**
     * 
     * @param containerName
     *            see {@link #getContainerName()}
     * @param timestamp
     *            see {@link #getTimestamp()}
     * @param computeLoad
     *            see {@link #getComputeLoad()}
     * @param computeCapacity
     *            see {@link #getComputeCapacity()}
     * @param demandEstimationWindow
     *            see {#link {@link #getDemandEstimationWindow()}
     * @param computeDemand
     *            see {@link #getComputeDemand()}
     * @param serverAverageProcessingTime
     *            see {@link #getAverageProcessingTime()}
     * @param service
     *            see {@link #getService()}
     * @param networkCapacity
     *            see {@link #getNetworkCapacity()}
     * @param networkLoad
     *            see {@link #getNetworkLoad()}
     * @param networkDemand
     *            see {@link #getNetworkDemand()}
     * @param neighborNetworkCapacity
     *            see {@link #getNeighborNetworkCapacity()}
     * @param neighborNetworkLoad
     *            see {@link #getNeighborNetworkLoad()}
     * @param neighborNetworkDemand
     *            see {@link #getNeighborNetworkDemand()}
     */
    public ContainerResourceReport(@JsonProperty("containerName") @Nonnull final ContainerIdentifier containerName,
            @JsonProperty("timestamp") final long timestamp,
            @JsonProperty("service") final ServiceIdentifier<?> service,
            @JsonProperty("demandEstimationWindow") @Nonnull final ResourceReport.EstimationWindow demandEstimationWindow,
            @JsonProperty("computeCapacity") @Nonnull final ImmutableMap<NodeAttribute<?>, Double> computeCapacity,
            @JsonProperty("computeLoad") @Nonnull final ImmutableMap<NodeIdentifier, ImmutableMap<NodeAttribute<?>, Double>> computeLoad,
            @JsonProperty("computeDemand") @Nonnull final ImmutableMap<NodeIdentifier, ImmutableMap<NodeAttribute<?>, Double>> computeDemand,
            @JsonProperty("averageProcessingTime") final double serverAverageProcessingTime,

            @JsonProperty("networkCapacity") @Nonnull final ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkCapacity,
            @JsonProperty("networkLoad") @Nonnull final ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkLoad,
            @JsonProperty("networkDemand") @Nonnull final ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkDemand,

            @JsonProperty("neighborNetworkCapacity") @Nonnull final ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> neighborNetworkCapacity,
            @JsonProperty("neighborNetworkLoad") @Nonnull final ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> neighborNetworkLoad,
            @JsonProperty("neighborNetworkDemand") @Nonnull final ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> neighborNetworkDemand) {
        this.containerName = containerName;
        this.timestamp = timestamp;
        this.service = service;
        this.demandEstimationWindow = demandEstimationWindow;

        this.computeLoad = computeLoad;
        this.computeCapacity = computeCapacity;
        this.computeDemand = computeDemand;
        this.averageProcessingTime = serverAverageProcessingTime;

        this.networkCapacity = networkCapacity;
        this.networkLoad = networkLoad;
        this.networkDemand = networkDemand;

        this.neighborNetworkCapacity = neighborNetworkCapacity;
        this.neighborNetworkLoad = neighborNetworkLoad;
        this.neighborNetworkDemand = neighborNetworkDemand;

    }

    private final ServiceIdentifier<?> service;

    /**
     * 
     * @return the service running in the container, null if no service is
     *         currently running
     */
    public ServiceIdentifier<?> getService() {
        return service;
    }

    private final long timestamp;

    /**
     * The units of the timestamp are determined by the clock used for the
     * network. Possible examples may be milliseconds since the epoch or
     * milliseconds since the start of the application. It is not expected that
     * this time be converted to a date time for display to the user. This value
     * is used to differentiate 2 reports for the same node taken at different
     * times.
     * 
     * @return when the report was generated
     */
    public long getTimestamp() {
        return timestamp;
    }

    private final ResourceReport.EstimationWindow demandEstimationWindow;

    /**
     * @return the window over which the demand values are computed
     * @see #getComputeDemand()
     */
    @Nonnull
    public ResourceReport.EstimationWindow getDemandEstimationWindow() {
        return demandEstimationWindow;
    }

    private final NodeIdentifier containerName;

    /**
     * @return the identifier of the node that the report came from
     */
    @Nonnull
    public final NodeIdentifier getContainerName() {
        return containerName;
    }

    private final double averageProcessingTime;

    /**
     * If no requests have completed for the service in this container this
     * value will be NaN.
     * 
     * @return The average time it takes to process a request for the service.
     */
    @Nonnull
    public double getAverageProcessingTime() {
        return averageProcessingTime;
    }

    private final ImmutableMap<NodeIdentifier, ImmutableMap<NodeAttribute<?>, Double>> computeLoad;

    /**
     * Get compute load for this node. This is a measured value. node load is
     * coming from -> {@link NodeAttribute} specifying the thing being measured
     * -> value.
     * 
     * @return the load information. Not null.
     */
    @Nonnull
    public ImmutableMap<NodeIdentifier, ImmutableMap<NodeAttribute<?>, Double>> getComputeLoad() {
        return computeLoad;
    }

    private final ImmutableMap<NodeIdentifier, ImmutableMap<NodeAttribute<?>, Double>> computeDemand;

    /**
     * Get estimated compute demand for this node. The meanings of the keys and
     * values match those from {@link #getComputeLoad()}, except that this is
     * referring to estimated demand rather than measured load.
     * 
     * @return the demand information. Not null.
     */
    @Nonnull
    public ImmutableMap<NodeIdentifier, ImmutableMap<NodeAttribute<?>, Double>> getComputeDemand() {
        return computeDemand;
    }

    private final ImmutableMap<NodeAttribute<?>, Double> computeCapacity;

    /**
     * Compute capacity for each attribute of a node.
     * 
     * @return Not null.
     */
    @Nonnull
    public ImmutableMap<NodeAttribute<?>, Double> getComputeCapacity() {
        return computeCapacity;
    }

    private final ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkCapacity;

    /**
     * Link capacity for network flows. client/other node -> attribute -> value.
     * Each key in the list is the identifier of a node that is interacting with
     * this container.
     * 
     * @return Not null.
     */
    @Nonnull
    public ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> getNetworkCapacity() {
        return networkCapacity;
    }

    private final ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkLoad;

    /**
     * Network load with other nodes. See {@link #getNetworkCapacity()} for
     * details on the map definition.
     * 
     * @return Not null.
     */
    @Nonnull
    public ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> getNetworkLoad() {
        return networkLoad;
    }

    private final ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkDemand;

    /**
     * Network demand with other nodes. See {@link #getNetworkCapacity()} for
     * details on the map definition.
     * 
     * @return Not null.
     */
    @Nonnull
    public ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> getNetworkDemand() {
        return networkDemand;
    }

    private final ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> neighborNetworkCapacity;

    /**
     * Link capacity for neighboring nodes. neighbor node -> attribute -> value.
     * Each key in the list is the identifier of a neighboring node.
     * 
     * @return Not null.
     */
    @Nonnull
    public ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> getNeighborNetworkCapacity() {
        return neighborNetworkCapacity;
    }

    private final ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> neighborNetworkLoad;

    /**
     * Network load to neighboring nodes. See
     * {@link #getNeighborNetworkCapacity()} for details on the map definition.
     * 
     * @return Not null.
     */
    @Nonnull
    public ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> getNeighborNetworkLoad() {
        return neighborNetworkLoad;
    }

    private final ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> neighborNetworkDemand;

    /**
     * Network demand to neighboring nodes. See
     * {@link #getNeighborNetworkCapacity()} for details on the map definition.
     * 
     * @return Not null.
     */
    @Nonnull
    public ImmutableMap<NodeIdentifier, ImmutableMap<LinkAttribute<?>, Double>> getNeighborNetworkDemand() {
        return neighborNetworkDemand;
    }

    /**
     * Create a container resource report with no data. The timestamp is set to
     * {@link #NULL_TIMESTAMP}.
     * 
     * @param containerName
     *            the name of the node
     * @return empty report for a node
     * @param demandWindow
     *            the estimation window for this null report
     */
    public static ContainerResourceReport getNullReport(@Nonnull final ContainerIdentifier containerName,
            @Nonnull final ResourceReport.EstimationWindow demandWindow) {

        return new ContainerResourceReport(containerName, NULL_TIMESTAMP, //
                null, // service
                demandWindow, //
                ImmutableMap.of(), // serverCapacity
                ImmutableMap.of(), // serverLoad
                ImmutableMap.of(), // serverDemand
                0, // serverAverageProcessingTime
                ImmutableMap.of(), // networkCapacity
                ImmutableMap.of(), // networkLoad
                ImmutableMap.of(), // networkDemand
                ImmutableMap.of(), // neighborNetworkCapacity
                ImmutableMap.of(), // neighborNetworkLoad
                ImmutableMap.of()); // neighborNetworkDemand

    }

    @Override
    public String toString() {
        return "{" + " computeCapacity: " + getComputeCapacity() + " computeLoad: " + getComputeLoad()
                + " computeDemand: " + getComputeDemand() + " }";
    }

}
