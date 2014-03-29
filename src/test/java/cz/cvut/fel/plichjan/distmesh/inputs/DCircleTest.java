package cz.cvut.fel.plichjan.distmesh.inputs;

import junit.framework.Assert;
import org.junit.Test;

/**
 */
public class DCircleTest {
    @Test
    public void testCall() throws Exception {
        DCircle circle = new DCircle(10, 20, 5);

        Assert.assertEquals(-5., circle.call(10, 20));
        Assert.assertEquals(0., circle.call(10, 25));
        Assert.assertEquals(0., circle.call(15, 20));
        Assert.assertEquals(5., circle.call(20, 20));
    }
}
