/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.dao;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.dataset.xml.FlatXmlWriter;
import org.xml.sax.InputSource;

import java.io.*;
import java.net.URL;

/**
 * @author Filip Neven
 */
public class GroupableFlatXmlDataSet extends GroupableCachedDataSet {

    /**
     * Creates an FlatXmlDataSet object with the specifed InputSource.
     */
    public GroupableFlatXmlDataSet(InputSource source) throws DataSetException {
        super(new FlatXmlProducer(source));
    }

    /**
     * Creates an GroupableFlatXmlDataSet object with the specifed xml file.
     * Relative DOCTYPE uri are resolved from the xml file path.
     *
     * @param xmlFile the xml file
     */
    public GroupableFlatXmlDataSet(File xmlFile) throws IOException, DataSetException {
        this(xmlFile, true);
    }

    /**
     * Creates an GroupableFlatXmlDataSet object with the specifed xml file.
     * Relative DOCTYPE uri are resolved from the xml file path.
     *
     * @param xmlFile     the xml file
     * @param dtdMetadata if <code>false</code> do not use DTD as metadata
     */
    public GroupableFlatXmlDataSet(File xmlFile, boolean dtdMetadata)
            throws IOException, DataSetException {
        this(xmlFile.toURL(), dtdMetadata);
    }

    /**
     * Creates an GroupableFlatXmlDataSet object with the specifed xml URL.
     * Relative DOCTYPE uri are resolved from the xml file path.
     *
     * @param xmlUrl the xml URL
     */
    public GroupableFlatXmlDataSet(URL xmlUrl) throws DataSetException {
        this(xmlUrl, true);
    }

    /**
     * Creates an GroupableFlatXmlDataSet object with the specifed xml URL.
     * Relative DOCTYPE uri are resolved from the xml file path.
     *
     * @param xmlUrl      the xml URL
     * @param dtdMetadata if <code>false</code> do not use DTD as metadata
     */
    public GroupableFlatXmlDataSet(URL xmlUrl, boolean dtdMetadata)
            throws DataSetException {
        super(new FlatXmlProducer(new InputSource(xmlUrl.toString()), dtdMetadata));
    }

    /**
     * Creates an GroupableFlatXmlDataSet object with the specifed xml reader.
     * Relative DOCTYPE uri are resolved from the current working dicrectory.
     *
     * @param xmlReader the xml reader
     */
    public GroupableFlatXmlDataSet(Reader xmlReader) throws DataSetException {
        this(xmlReader, true);
    }

    /**
     * Creates an GroupableFlatXmlDataSet object with the specifed xml reader.
     * Relative DOCTYPE uri are resolved from the current working dicrectory.
     *
     * @param xmlReader   the xml reader
     * @param dtdMetadata if <code>false</code> do not use DTD as metadata
     */
    public GroupableFlatXmlDataSet(Reader xmlReader, boolean dtdMetadata)
            throws DataSetException {
        super(new FlatXmlProducer(new InputSource(xmlReader), dtdMetadata));
    }

    /**
     * Creates an GroupableFlatXmlDataSet object with the specifed xml and dtd readers.
     *
     * @param xmlReader the xml reader
     * @param dtdReader the dtd reader
     */
    public GroupableFlatXmlDataSet(Reader xmlReader, Reader dtdReader)
            throws IOException, DataSetException {
        this(xmlReader, new FlatDtdDataSet(dtdReader));
    }

    /**
     * Creates an GroupableFlatXmlDataSet object with the specifed xml reader.
     *
     * @param xmlReader   the xml reader
     * @param metaDataSet the dataset used as metadata source.
     */
    public GroupableFlatXmlDataSet(Reader xmlReader, IDataSet metaDataSet)
            throws DataSetException {
        super(new FlatXmlProducer(new InputSource(xmlReader), metaDataSet));
    }

    /**
     * Creates an GroupableFlatXmlDataSet object with the specifed xml input stream.
     * Relative DOCTYPE uri are resolved from the current working dicrectory.
     *
     * @param xmlStream the xml input stream
     */
    public GroupableFlatXmlDataSet(InputStream xmlStream) throws DataSetException {
        this(xmlStream, true);
    }

    /**
     * Creates an GroupableFlatXmlDataSet object with the specifed xml input stream.
     * Relative DOCTYPE uri are resolved from the current working dicrectory.
     *
     * @param xmlStream   the xml input stream
     * @param dtdMetadata if <code>false</code> do not use DTD as metadata
     */
    public GroupableFlatXmlDataSet(InputStream xmlStream, boolean dtdMetadata)
            throws DataSetException {
        super(new FlatXmlProducer(new InputSource(xmlStream), dtdMetadata));
    }

    /**
     * Creates an GroupableFlatXmlDataSet object with the specifed xml and dtd input
     * stream.
     *
     * @param xmlStream the xml input stream
     * @param dtdStream the dtd input stream
     */
    public GroupableFlatXmlDataSet(InputStream xmlStream, InputStream dtdStream)
            throws IOException, DataSetException {
        this(xmlStream, new FlatDtdDataSet(dtdStream));
    }

    /**
     * Creates an GroupableFlatXmlDataSet object with the specifed xml input stream.
     *
     * @param xmlStream   the xml input stream
     * @param metaDataSet the dataset used as metadata source.
     */
    public GroupableFlatXmlDataSet(InputStream xmlStream, IDataSet metaDataSet)
            throws DataSetException {
        super(new FlatXmlProducer(new InputSource(xmlStream), metaDataSet));
    }

    /**
     * Write the specified dataset to the specified output stream as xml.
     */
    public static void write(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException {
        FlatXmlWriter datasetWriter = new FlatXmlWriter(out);
        datasetWriter.setIncludeEmptyTable(true);
        datasetWriter.write(dataSet);
    }

    /**
     * Write the specified dataset to the specified writer as xml.
     */
    public static void write(IDataSet dataSet, Writer writer)
            throws DataSetException {
        write(dataSet, writer, null);
    }

    /**
     * Write the specified dataset to the specified writer as xml.
     */
    public static void write(IDataSet dataSet, Writer writer, String encoding)
            throws DataSetException {
        FlatXmlWriter datasetWriter = new FlatXmlWriter(writer, encoding);
        datasetWriter.setIncludeEmptyTable(true);
        datasetWriter.write(dataSet);
    }

    /**
     * Write a DTD for the specified dataset to the specified output.
     *
     * @deprecated use {@link FlatDtdDataSet#write}
     */
    public static void writeDtd(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException {
        FlatDtdDataSet.write(dataSet, out);
    }
}
