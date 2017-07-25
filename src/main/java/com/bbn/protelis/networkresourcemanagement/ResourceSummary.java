package com.bbn.protelis.networkresourcemanagement;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.protelis.lang.datatype.Field;

import com.google.common.collect.ImmutableMap;

/**
 * Summary information about the resources in a region.
 * 
 */
public class ResourceSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param region
     *            the name of this region
     * @param serverLoad
     *            the load for services from this region. Key is service name,
     *            value is map of attribute to value
     * @param serverCapacity
     *            the capacity of the servers in this region
     * @param networkCapacity
     *            the capacity of the links to the neighboring regions (key is
     *            region name)
     * @param networkLoad
     *            the load on the links to the neighboring regions (key is
     *            region name)
     * @param networkDemand
     *            the estimated demand on the links to the neighboring regions
     *            (key is region name)
     * @param serverDemand
     *            the estimated demand on the server (key is region name)
     * @param minTimestamp
     *            the minimum timestamp of the reports combined to create this
     *            summary
     * @param maxTimestamp
     *            the maximum timestamp of the reports combined to create this
     *            summary
     * @param demandEstimationWindow
     *            the window size used for estimating demand
     */
    public ResourceSummary(@Nonnull final RegionIdentifier region,
            final long minTimestamp,
            final long maxTimestamp,
            @Nonnull final ResourceReport.EstimationWindow demandEstimationWindow,
            @Nonnull final ImmutableMap<NodeAttribute<?>, Double> serverCapacity,
            @Nonnull final ImmutableMap<ServiceIdentifier<?>, ImmutableMap<RegionIdentifier, ImmutableMap<NodeAttribute<?>, Double>>> serverLoad,
            @Nonnull final ImmutableMap<ServiceIdentifier<?>, ImmutableMap<RegionIdentifier, ImmutableMap<NodeAttribute<?>, Double>>> serverDemand,
            @Nonnull final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkCapacity,
            @Nonnull final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkLoad,
            @Nonnull final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkDemand) {
        this.region = region;
        this.minTimestamp = minTimestamp;
        this.maxTimestamp = maxTimestamp;
        this.demandEstimationWindow = demandEstimationWindow;
        this.serverLoad = serverLoad;
        this.serverDemand = serverDemand;
        this.serverCapacity = serverCapacity;
        this.networkCapacity = networkCapacity;
        this.networkLoad = networkLoad;
        this.networkDemand = networkDemand;
    }

    private final RegionIdentifier region;

    /**
     * @return the name of the region
     */
    @Nonnull
    public final RegionIdentifier getRegion() {
        return region;
    }

    private final long minTimestamp;

    /**
     * @return the minimum timestamp of the reports combined to create this
     *         summary
     */
    public long getMinTimestamp() {
        return minTimestamp;
    }

    private final long maxTimestamp;

    /**
     * @return the maximum timestamp of the reports combined to create this
     *         summary
     */
    public long getMaxTimestamp() {
        return maxTimestamp;
    }

    private final ResourceReport.EstimationWindow demandEstimationWindow;

    /**
     * @return the window over which the demand values are computed
     */
    @Nonnull
    public ResourceReport.EstimationWindow getDemandEstimationWindow() {
        return demandEstimationWindow;
    }

    private final ImmutableMap<ServiceIdentifier<?>, ImmutableMap<RegionIdentifier, ImmutableMap<NodeAttribute<?>, Double>>> serverLoad;

    /**
     * Get server load for this region. Key is the service name, value is the
     * load by {@link NodeAttribute}.
     * 
     * @return the summary information. Not null.
     */
    @Nonnull
    public ImmutableMap<ServiceIdentifier<?>, ImmutableMap<RegionIdentifier, ImmutableMap<NodeAttribute<?>, Double>>>
            getServerLoad() {
        return serverLoad;
    }

    private final ImmutableMap<ServiceIdentifier<?>, ImmutableMap<RegionIdentifier, ImmutableMap<NodeAttribute<?>, Double>>> serverDemand;

    /**
     * Get server demand for this region. Key is the service name, value is the
     * load by {@link NodeAttribute}.
     * 
     * @return the demand information. Not null.
     */
    @Nonnull
    public ImmutableMap<ServiceIdentifier<?>, ImmutableMap<RegionIdentifier, ImmutableMap<NodeAttribute<?>, Double>>>
            getServerDemand() {
        return serverDemand;
    }

    private final ImmutableMap<NodeAttribute<?>, Double> serverCapacity;

    /**
     * Server capacity for this region.
     * 
     * @return the summary information. Not null.
     */
    @Nonnull
    public ImmutableMap<NodeAttribute<?>, Double> getServerCapacity() {
        return serverCapacity;
    }

    private final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkCapacity;

    /**
     * Network capacity for neighboring regions. Key is region name.
     * 
     * @return the summary information. Not null.
     */
    @Nonnull
    public ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> getNetworkCapacity() {
        return networkCapacity;
    }

    private final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkLoad;

    /**
     * Network load for neighboring regions. Key is region name.
     * 
     * @return the summary information. Not null.
     */
    @Nonnull
    public ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> getNetworkLoad() {
        return networkLoad;
    }

    private final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkDemand;

    /**
     * Network demand for neighboring regions. Key is region name.
     * 
     * @return the demand information. Not null.
     */
    @Nonnull
    public ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> getNetworkDemand() {
        return networkDemand;
    }

    /**
     * Uses {@link ResourceReport#NULL_TIMESTAMP} as the minimum and maximum
     * timestamps.
     * 
     * @param region
     *            the region
     * @param estimationWindow
     *            the window over which demand is estimated
     * @return empty summary for a region
     */
    public static ResourceSummary getNullSummary(@Nonnull final RegionIdentifier region,
            @Nonnull final ResourceReport.EstimationWindow estimationWindow) {
        final ImmutableMap<ServiceIdentifier<?>, ImmutableMap<RegionIdentifier, ImmutableMap<NodeAttribute<?>, Double>>> serverLoad = ImmutableMap
                .of();
        final ImmutableMap<NodeAttribute<?>, Double> serverCapacity = ImmutableMap.of();
        final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkCapacity = ImmutableMap.of();
        final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkLoad = ImmutableMap.of();

        return new ResourceSummary(region, ResourceReport.NULL_TIMESTAMP, ResourceReport.NULL_TIMESTAMP,
                estimationWindow, serverCapacity, serverLoad, serverLoad, networkCapacity, networkLoad, networkLoad);
    }

    /**
     * Merge two summaries.
     * 
     * @param one
     *            the first summary to merge. Not null.
     * @param two
     *            the second summary to merge. Not null.
     * @return a newly created summary. Not null, but may be the result of
     *         {@link #getNullSummary(RegionIdentifier)}
     * @throws IllegalArgumentException
     *             if the 2 summaries are not for the same region or the 2
     *             summarizes have different estimation windows
     */
    @Nonnull
    public static ResourceSummary merge(@Nonnull final ResourceSummary one, @Nonnull final ResourceSummary two) {
        if (!one.getRegion().equals(two.getRegion())) {
            throw new IllegalArgumentException("Cannot merge resource summaries from different regions");
        }
        if (!one.getDemandEstimationWindow().equals(two.getDemandEstimationWindow())) {
            throw new IllegalArgumentException("Cannot merge resource summaries with different estimation windows");
        }

        final ImmutableMap<ServiceIdentifier<?>, ImmutableMap<RegionIdentifier, ImmutableMap<NodeAttribute<?>, Double>>> serverLoad = mergeMaps3(
                one.getServerLoad(), two.getServerLoad());
        final ImmutableMap<NodeAttribute<?>, Double> serverCapacity = mergeDoubleMapViaSum(one.getServerCapacity(),
                two.getServerCapacity());

        final ImmutableMap<ServiceIdentifier<?>, ImmutableMap<RegionIdentifier, ImmutableMap<NodeAttribute<?>, Double>>> serverDemand = mergeMaps3(
                one.getServerDemand(), two.getServerDemand());

        final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkCapacity = mergeMaps2(
                one.getNetworkCapacity(), two.getNetworkCapacity());
        final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkLoad = mergeMaps2(
                one.getNetworkLoad(), two.getNetworkLoad());
        final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkDemand = mergeMaps2(
                one.getNetworkDemand(), two.getNetworkDemand());

        final long minTimestamp = Math.min(one.getMinTimestamp(), two.getMinTimestamp());
        final long maxTimestamp = Math.max(one.getMaxTimestamp(), two.getMaxTimestamp());
        return new ResourceSummary(one.getRegion(), minTimestamp, maxTimestamp, one.getDemandEstimationWindow(),
                serverCapacity, serverLoad, serverDemand, networkCapacity, networkLoad, networkDemand);
    }

    /**
     * Convert a {@link ResourceReport} into a {@link ResourceSummary} to be
     * merged later.
     * 
     * @param report
     *            the report to convert
     * @param nodeToRegion
     *            the mapping of {@link NodeIdentifier} to
     *            {@link RegionIdentifier}.
     * @return a new {@link ResourceSummary} object
     * @throws ClassCastException
     *             if nodeRegions does not contain {@link RegionIdentifier}
     *             objects.
     */
    @Nonnull
    public static ResourceSummary convertToSummary(@Nonnull final ResourceReport report,
            @Nonnull final Field nodeToRegion) {
        final ImmutableMap<ServiceIdentifier<?>, ImmutableMap<RegionIdentifier, ImmutableMap<NodeAttribute<?>, Double>>> serverLoad = report
                .getServerLoad();
        final ImmutableMap<NodeAttribute<?>, Double> serverCapacity = report.getServerCapacity();
        final ImmutableMap<ServiceIdentifier<?>, ImmutableMap<RegionIdentifier, ImmutableMap<NodeAttribute<?>, Double>>> serverDemand = report
                .getServerDemand();

        final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkCapacity = convertNodeToRegion(
                nodeToRegion, report.getNetworkCapacity());

        final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkLoad = convertNodeToRegion(
                nodeToRegion, report.getNetworkLoad());

        final ImmutableMap<RegionIdentifier, ImmutableMap<LinkAttribute<?>, Double>> networkDemand = convertNodeToRegion(
                nodeToRegion, report.getNetworkDemand());

        final RegionIdentifier reportRegion = (RegionIdentifier) nodeToRegion.getSample(report.getNodeName());

        final ResourceSummary summary = new ResourceSummary(reportRegion, report.getTimestamp(), report.getTimestamp(),
                report.getDemandEstimationWindow(), serverCapacity, serverLoad, serverDemand, networkCapacity,
                networkLoad, networkDemand);
        return summary;
    }

    private static <T> ImmutableMap<RegionIdentifier, ImmutableMap<T, Double>> convertNodeToRegion(
            @Nonnull final Field nodeToRegion, final ImmutableMap<NodeIdentifier, ImmutableMap<T, Double>> source) {

        final Map<RegionIdentifier, ImmutableMap<T, Double>> dest = new HashMap<>();
        source.forEach((k, v) -> {
            final RegionIdentifier region = (RegionIdentifier) nodeToRegion.getSample(k);
            if (null != region) {
                dest.merge(region, v, ResourceSummary::mergeDoubleMapViaSum);
            }
        });

        return ImmutableMap.copyOf(dest);
    }

    @Nonnull
    private static <ID, T> ImmutableMap<ID, ImmutableMap<RegionIdentifier, ImmutableMap<T, Double>>> mergeMaps3(
            @Nonnull final ImmutableMap<ID, ImmutableMap<RegionIdentifier, ImmutableMap<T, Double>>> one,
            @Nonnull final ImmutableMap<ID, ImmutableMap<RegionIdentifier, ImmutableMap<T, Double>>> two) {

        final Map<ID, ImmutableMap<RegionIdentifier, ImmutableMap<T, Double>>> newMap = new HashMap<>();
        for (final ImmutableMap.Entry<ID, ImmutableMap<RegionIdentifier, ImmutableMap<T, Double>>> oneEntry : one
                .entrySet()) {
            final ImmutableMap<RegionIdentifier, ImmutableMap<T, Double>> twoValue = two.get(oneEntry.getKey());
            if (null != twoValue) {
                final ImmutableMap<RegionIdentifier, ImmutableMap<T, Double>> oneValue = oneEntry.getValue();

                final ImmutableMap<RegionIdentifier, ImmutableMap<T, Double>> newValue = mergeMaps2(oneValue, twoValue);

                newMap.put(oneEntry.getKey(), newValue);
            } else {
                // no conflict, just add
                newMap.put(oneEntry.getKey(), oneEntry.getValue());
            }
        }
        for (final ImmutableMap.Entry<ID, ImmutableMap<RegionIdentifier, ImmutableMap<T, Double>>> twoEntry : two
                .entrySet()) {
            if (!one.containsKey(twoEntry.getKey())) {
                newMap.put(twoEntry.getKey(), twoEntry.getValue());
            }
        }

        return ImmutableMap.copyOf(newMap);

    }

    /**
     * Create a new map that sums the values of the inner map for matching keys.
     * 
     * @param one
     *            the first map to combine
     * @param two
     *            the second map to combine
     * @return a new map
     * @param <ID>
     *            first level lookup
     * @param <T>
     *            second level lookup
     */
    @Nonnull
    public static <ID, T> ImmutableMap<ID, ImmutableMap<T, Double>> mergeMaps2(
            @Nonnull final ImmutableMap<ID, ImmutableMap<T, Double>> one,
            @Nonnull final ImmutableMap<ID, ImmutableMap<T, Double>> two) {
        final Map<ID, ImmutableMap<T, Double>> newMap = new HashMap<>();
        for (final ImmutableMap.Entry<ID, ImmutableMap<T, Double>> oneEntry : one.entrySet()) {
            final ImmutableMap<T, Double> twoValue = two.get(oneEntry.getKey());
            if (null != twoValue) {
                final ImmutableMap<T, Double> oneValue = oneEntry.getValue();

                final ImmutableMap<T, Double> newValue = mergeDoubleMapViaSum(oneValue, twoValue);

                newMap.put(oneEntry.getKey(), newValue);
            } else {
                // no conflict, just add
                newMap.put(oneEntry.getKey(), oneEntry.getValue());
            }
        }
        for (final ImmutableMap.Entry<ID, ImmutableMap<T, Double>> twoEntry : two.entrySet()) {
            if (!one.containsKey(twoEntry.getKey())) {
                newMap.put(twoEntry.getKey(), twoEntry.getValue());
            }
        }

        return ImmutableMap.copyOf(newMap);
    }

    /**
     * Create a new ImmutableMap that sums the values for matching keys.
     * 
     * @param one
     *            the first map to combine
     * @param two
     *            the second map to combine
     * @return a new map
     * @param <T>
     *            the type of the map key
     */
    @Nonnull
    public static <T> ImmutableMap<T, Double> mergeDoubleMapViaSum(@Nonnull final ImmutableMap<T, Double> one,
            @Nonnull final ImmutableMap<T, Double> two) {

        final Map<T, Double> newMap = new HashMap<>(one);
        two.forEach((k, v) -> newMap.merge(k, v, Double::sum));

        final ImmutableMap<T, Double> newImmutableMap = ImmutableMap.copyOf(newMap);

        return newImmutableMap;
    }

}
