/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.cubeisland.engine.logscribe;

import de.cubeisland.engine.logscribe.target.PrintTarget;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class DefaultLogFactoryTest
{
    private DefaultLogFactory factory;

    @Before
    public void setUp() throws Exception
    {
        this.factory = new DefaultLogFactory();
    }

    @Test
    public void testFactory()
    {
        Log log = this.factory.getLog(DefaultLogFactoryTest.class);
        assertSame(log, this.factory.getLog(DefaultLogFactoryTest.class));
        // unregister Log from Factory
        log.shutdown();
        assertNotSame(log, this.factory.getLog(DefaultLogFactoryTest.class));
        log = this.factory.getLog(DefaultLogFactoryTest.class);
        log.addTarget(PrintTarget.STDOUT);
        // Shutting down all the loggers and their targets
        this.factory.shutdown();
        assertTrue(log.isShutdown());
        assertTrue(log.getTargets().get(0).isShutdown());
    }
}
