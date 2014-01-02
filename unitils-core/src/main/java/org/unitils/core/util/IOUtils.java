/*
 * Copyright 2013,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.core.util;

import java.io.*;

/**
 * This file is a modified version of IOUtils from apache commons-io
 * The code was not modified but all unecessary methods were removed.
 * Do not use this class. Use commons-io directly.
 * <p/>
 * <p/>
 * General IO stream manipulation utilities.
 * <p/>
 * This class provides static utility methods for input/output operations.
 * <ul>
 * <li>closeQuietly - these methods close a stream ignoring nulls and exceptions
 * <li>toXxx/read - these methods read data from a stream
 * <li>write - these methods write data to a stream
 * <li>copy - these methods copy all the data from one stream to another
 * <li>contentEquals - these methods compare the content of two streams
 * </ul>
 * <p/>
 * The byte-to-char methods and char-to-byte methods involve a conversion step.
 * Two methods are provided in each case, one that uses the platform default
 * encoding and the other which allows you to specify an encoding. You are
 * encouraged to always specify an encoding because relying on the platform
 * default can lead to unexpected results, for example when moving from
 * development to production.
 * <p/>
 * All the methods in this class that read a stream are buffered internally.
 * This means that there is no cause to use a <code>BufferedInputStream</code>
 * or <code>BufferedReader</code>. The default buffer size of 4K has been shown
 * to be efficient in tests.
 * <p/>
 * Wherever possible, the methods in this class do <em>not</em> flush or close
 * the stream. This is to avoid making non-portable assumptions about the
 * streams' origin and further use. Thus the caller is still responsible for
 * closing streams after use.
 * <p/>
 * Origin of code: Excalibur.
 *
 * @author Peter Donald
 * @author Jeff Turner
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 * @author Gareth Davis
 * @author Ian Springer
 * @author Niall Pemberton
 * @author Sandy McArthur
 * @version $Id: IOUtils.java 385117 2006-03-11 18:07:02Z scolebourne $
 */
@SuppressWarnings("all")
public class IOUtils {
    // NOTE: This class is focussed on InputStream, OutputStream, Reader and
    // Writer. Each method should take at least one of these as a parameter,
    // or return one of them.

    /**
     * The system line separator string.
     */
    public static final String LINE_SEPARATOR;

    static {
        // avoid security issues
        StringWriter buf = new StringWriter(4);
        PrintWriter out = new PrintWriter(buf);
        out.println();
        LINE_SEPARATOR = buf.toString();
    }

    /**
     * The default buffer size to use.
     */
    protected static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    //-----------------------------------------------------------------------

    /**
     * Unconditionally close an <code>Reader</code>.
     * <p/>
     * Equivalent to {@link Reader#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param input the Reader to close, may be null or already closed
     */
    public static void closeQuietly(Reader input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    /**
     * Unconditionally close a <code>Writer</code>.
     * <p/>
     * Equivalent to {@link Writer#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param output the Writer to close, may be null or already closed
     */
    public static void closeQuietly(Writer output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    /**
     * Unconditionally close an <code>InputStream</code>.
     * <p/>
     * Equivalent to {@link InputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param input the InputStream to close, may be null or already closed
     */
    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    /**
     * Unconditionally close an <code>OutputStream</code>.
     * <p/>
     * Equivalent to {@link OutputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param output the OutputStream to close, may be null or already closed
     */
    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    // write String
    //-----------------------------------------------------------------------

    /**
     * Writes chars from a <code>String</code> to bytes on an
     * <code>OutputStream</code> using the default character encoding of the
     * platform.
     * <p/>
     * This method uses {@link String#getBytes()}.
     *
     * @param data   the <code>String</code> to write, null ignored
     * @param output the <code>OutputStream</code> to write to
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(String data, OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(data.getBytes());
        }
    }

    /**
     * Writes chars from a <code>String</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p/>
     * This method uses {@link String#getBytes(String)}.
     *
     * @param data     the <code>String</code> to write, null ignored
     * @param output   the <code>OutputStream</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static void write(String data, OutputStream output, String encoding)
            throws IOException {
        if (data != null) {
            if (encoding == null) {
                write(data, output);
            } else {
                output.write(data.getBytes(encoding));
            }
        }
    }

    // copy from InputStream
    //-----------------------------------------------------------------------

    /**
     * Copy bytes from an <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input  the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static int copy(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    // copy from Reader
    //-----------------------------------------------------------------------

    /**
     * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     *
     * @param input  the <code>Reader</code> to read from
     * @param output the <code>Writer</code> to write to
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static int copy(Reader input, Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
