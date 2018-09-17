package com.bbn.protelis.networkresourcemanagement;

import java.net.InetSocketAddress;

import org.protelis.lang.datatype.DeviceUID;

/**
 * Used to determine how to connect to a node for sharing information. Used by
 * {@link NodeNetworkManager}.
 */
public interface NodeLookupService {

    /**
     * Get the network connection information for a node. This is used to create
     * a socket to connect to the node for sharing information.
     * 
     * @param uid
     *            the UID of the node to find
     * @return the hostname and port. Null is returned if no node can be found with the specified uid.
     */
    InetSocketAddress getInetAddressForNode(DeviceUID uid);

}
