/*
 * The MIT License
 * Copyright © 2013 Cube Island
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
package org.cubeengine.logscribe.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.cubeengine.logscribe.Log;
import org.cubeengine.logscribe.LogLevel;

/**
 * Based on this implementation: https://blogs.oracle.com/nickstephen/entry/java_redirecting_system_out_and
 */
public class LoggingOutputStream extends ByteArrayOutputStream
{
    private final Log log;
    private final LogLevel level;
    private final String encoding;
    private final String lineSeparator;

    public LoggingOutputStream(Log log, LogLevel level)
    {
        this(log, level, "UTF-8");
    }

    public LoggingOutputStream(Log log, LogLevel level, String encoding)
    {
        super();
        this.log = log;
        this.level = level;
        this.encoding = encoding;
        this.lineSeparator = System.getProperty("line.separator");
    }

    @Override
    public void flush() throws IOException
    {
        String record;
        synchronized (this)
        {
            super.flush();
            record = this.toString(this.encoding);
            super.reset();
            if (record.length() == 0 || record.equals(this.lineSeparator))
            {
                return;
            }

            this.log.log(this.level, record);
        }
    }

    public static PrintStream hijackStandardOutput(Log log, LogLevel level) {
        PrintStream old = System.out;
        System.setOut(new PrintStream(new LoggingOutputStream(log, level), true));
        return old;
    }

    public static PrintStream hijackStandardError(Log log, LogLevel level) {
        PrintStream old = System.err;
        System.setErr(new PrintStream(new LoggingOutputStream(log, level), true));
        return old;
    }
}
