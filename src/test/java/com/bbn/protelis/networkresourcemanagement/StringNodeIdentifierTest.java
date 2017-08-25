package com.bbn.protelis.networkresourcemanagement;

import org.junit.Assert;
import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Some tests for {@link StringNodeIdentifier}.
 * 
 * @author jschewe
 *
 */
public class StringNodeIdentifierTest {

    /**
     * Ensure that equality is properly defined to compare the names. This
     * checks that when using 2 string names that are equal, but not the same
     * reference produce {@link StringNodeIdentifier} objects that are equal.
     */
    @Test
    @SuppressFBWarnings(value = "DM_STRING_CTOR", justification = "Intentionally want 2 distinct String objects with the same value")
    public void testEquals() {
        final String expectedName = new String("one");
        final String compareName = new String("one");

        Assert.assertEquals(expectedName, compareName);

        // make sure that we really have 2 different string objects
        Assert.assertFalse(expectedName == compareName);

        final StringNodeIdentifier expected = new StringNodeIdentifier(expectedName);
        final StringNodeIdentifier compare = new StringNodeIdentifier(compareName);

        Assert.assertEquals(expected, compare);
    }

}
