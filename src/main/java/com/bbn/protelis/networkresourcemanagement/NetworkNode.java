package com.bbn.protelis.networkresourcemanagement;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.bbn.protelis.utils.StringUID;

/**
 * Common functionality between {@link NetworkServer} and {@link NetworkClient}.
 * 
 */
public interface NetworkNode {

    /**
     * 
     * @return the ID of the object
     */
    @Nonnull
    StringUID getDeviceUID();

    /**
     * @return name of the object
     */
    @Nonnull
    String getName();

    /**
     * @return the name of the region that this node currently belongs to, may
     *         be null
     */
    String getRegionName();

    /**
     * Process the extra data that was found when creating the node.
     * 
     * @param extraData
     *            key/value pairs
     * @see NetworkFactory#createServer(String, java.util.Map)
     * @see NetworkFactory#createClient(String, Map)
     */
    void processExtraData(@Nonnull Map<String, Object> extraData);

    /**
     * Add a neighbor. If the neighbor node already exists, the bandwidth
     * capacity for the neighbor is replaced with the new value.
     * 
     * @param v
     *            the UID of the neighbor node
     * @param bandwidth
     *            capacity to the neighbor in bits per second. Infinity can be
     *            used for unknown.
     */
    void addNeighbor(@Nonnull StringUID v, double bandwidth);

    /**
     * 
     * @param v
     *            the neighbor to add
     * @param bandwidth
     *            to the neighbor in bits per second
     * @see #addNeighbor(StringUID, double)
     */
    void addNeighbor(@Nonnull NetworkNode v, double bandwidth);

    /**
     * The neighbors of this {@link NetworkServer}. Note that these IDs may
     * refer to either {@link NetworkServer} or {@link NetworkClient}.
     * 
     * @return unmodifiable set
     */
    @Nonnull
    Set<StringUID> getNeighbors();
    
    /**
     * 
     * @return the hardware platform for this node, may be null
     */
    String getHardware();
    
    /**
     * 
     * @param hardware the hardware platform for this node
     * @see #getHardware()
     */
    void setHardware(String hardware);

}
