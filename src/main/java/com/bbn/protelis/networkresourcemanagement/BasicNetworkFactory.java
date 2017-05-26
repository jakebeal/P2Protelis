package com.bbn.protelis.networkresourcemanagement;

import javax.annotation.Nonnull;

import org.protelis.lang.ProtelisLoader;
import org.protelis.vm.ProtelisProgram;

/**
 * Create {@link Node} and {@link Link} objects.
 * 
 */
public class BasicNetworkFactory implements NetworkFactory<Node, Link> {

    private final NodeLookupService lookupService;
    private final String program;
    private final boolean anonymousProgram;

    /**
     * Create a basic factory.
     * 
     * @param lookupService
     *            how to find other nodes
     * @param program
     *            the program to put in all nodes
     * @param anonymous
     *            if true, parse as main expression; if false, treat as a module
     *            reference
     */
    public BasicNetworkFactory(final NodeLookupService lookupService, final String program, final boolean anonymous) {
        this.lookupService = lookupService;
        this.program = program;
        this.anonymousProgram = anonymous;
    }

    @Override
    @Nonnull
    public Node createNode(final String name) {
        final ProtelisProgram instance;
        if (anonymousProgram) {
            instance = ProtelisLoader.parseAnonymousModule(program);
        } else {
            instance = ProtelisLoader.parse(program);
        }
        return new Node(lookupService, instance, name);
    }

    @Override
    @Nonnull
    public Link createLink(final String name, final Node left, final Node right, final double bandwidth) {
        return new Link(name, left, right, bandwidth);
    }

}
