package com.bbn.protelis.networkresourcemanagement;

/**
 * Used to specify the type of information being reported for capacity or usage
 * of a {@link NetworkServer}.
 * 
 */
public enum NodeAttributeEnum implements NodeAttribute<NodeAttributeEnum> {

    /**
     * CPU information. Measured in number of cores.
     */
    CPU,
    /**
     * Amount of memory in bytes.
     */
    MEMORY,
    /**
     * Amount of disk in bytes.
     */
    DISK,
    /**
     * Measured in units of standard small containers.
     */
    TASK_CONTAINERS;

    @Override
    public NodeAttributeEnum getAttribute() {
        return this;
    }
}
