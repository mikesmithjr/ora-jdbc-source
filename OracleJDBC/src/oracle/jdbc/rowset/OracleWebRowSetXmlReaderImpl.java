package oracle.jdbc.rowset;

import java.io.PrintStream;
import java.io.Reader;
import java.sql.SQLException;
import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

class OracleWebRowSetXmlReaderImpl implements OracleWebRowSetXmlReader {
    private static final String JAVA_SAXPARSER_PROPERTY = "javax.xml.parsers.SAXParserFactory";
    private static final String JAVA_DOMPARSER_PROPERTY = "javax.xml.parsers.DocumentBuilderFactory";
    private static final String ORACLE_JAXP_SAXPARSER_FACTORY = "oracle.xml.jaxp.JXSAXParserFactory";
    private static final String ORACLE_JAXP_DOMPARSER_FACTORY = "oracle.xml.jaxp.JXDocumentBuilderFactory";
    private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private static final String WEBROWSET_SCHEMA = "http://java.sun.com/xml/ns/jdbc/webrowset.xsd";
    private Document document;
    private String parserStr;

    OracleWebRowSetXmlReaderImpl() {
        this.document = null;
        this.parserStr = null;
    }

    public void readXML(WebRowSet webrowset, Reader reader) throws SQLException {
        this.parserStr = getSystemProperty("javax.xml.parsers.SAXParserFactory");
        if (this.parserStr != null) {
            readXMLSax((OracleWebRowSet) webrowset, reader);
        } else {
            this.parserStr = getSystemProperty("javax.xml.parsers.DocumentBuilderFactory");
            if (this.parserStr != null) {
                readXMLDom((OracleWebRowSet) webrowset, reader);
            } else
                throw new SQLException("No valid JAXP parser property specified");
        }
    }

    public void readData(RowSetInternal internal) throws SQLException {
    }

    private void readXMLSax(OracleWebRowSet webrowset, Reader reader) throws SQLException {
        try {
            InputSource inputsource = new InputSource(reader);
            OracleWebRowSetXmlReaderContHandler contentHandler = new OracleWebRowSetXmlReaderContHandler(
                    webrowset);

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            saxParserFactory.setNamespaceAware(true);
            saxParserFactory.setValidating(true);
            SAXParser saxParser = saxParserFactory.newSAXParser();

            saxParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                                  "http://www.w3.org/2001/XMLSchema");
            saxParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource",
                                  "http://java.sun.com/xml/ns/jdbc/webrowset.xsd");

            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(contentHandler);

            xmlReader.parse(inputsource);
        } catch (SAXParseException saxparseexception) {
            System.out.println("** Parsing error, line " + saxparseexception.getLineNumber()
                    + ", uri " + saxparseexception.getSystemId());

            System.out.println("   " + saxparseexception.getMessage());
            saxparseexception.printStackTrace();
            throw new SQLException(saxparseexception.getMessage());
        } catch (SAXNotRecognizedException saxexception) {
            saxexception.printStackTrace();
            throw new SQLException("readXMLSax: SAXNotRecognizedException: "
                    + saxexception.getMessage());
        } catch (SAXException saxexception) {
            saxexception.printStackTrace();
            throw new SQLException("readXMLSax: SAXException: " + saxexception.getMessage());
        } catch (FactoryConfigurationError error) {
            error.printStackTrace();
            throw new SQLException("readXMLSax: Parser factory config: " + error.getMessage());
        } catch (ParserConfigurationException exc) {
            exc.printStackTrace();
            throw new SQLException("readXMLSax: Parser config: " + exc.getMessage());
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new SQLException("readXMLSax: " + exc.getMessage());
        }
    }

    private void readXMLDom(OracleWebRowSet webrowset, Reader reader) throws SQLException {
        try {
            InputSource inputsource = new InputSource(reader);
            OracleWebRowSetXmlReaderDomHandler domHandler = new OracleWebRowSetXmlReaderDomHandler(
                    webrowset);

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

            docBuilderFactory.setNamespaceAware(true);
            docBuilderFactory.setValidating(true);

            docBuilderFactory
                    .setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                                  "http://www.w3.org/2001/XMLSchema");
            docBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource",
                                           "http://java.sun.com/xml/ns/jdbc/webrowset.xsd");
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            this.document = docBuilder.parse(inputsource);

            domHandler.readXMLDocument(this.document);
        } catch (SAXException saxexception) {
            saxexception.printStackTrace();
            throw new SQLException("readXMLDom: SAXException: " + saxexception.getMessage());
        } catch (FactoryConfigurationError error) {
            error.printStackTrace();
            throw new SQLException("readXMLDom: Parser factory config: " + error.getMessage());
        } catch (ParserConfigurationException exc) {
            exc.printStackTrace();
            throw new SQLException("readXMLDom: Parser config: " + exc.getMessage());
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new SQLException("readXMLDom: " + exc.getMessage());
        }
    }

    private String getSystemProperty(String prop) {
        String str = null;
        try {
            str = System.getProperty(prop);
        } catch (SecurityException exc) {
            str = null;
        }

        return str;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleWebRowSetXmlReaderImpl JD-Core Version: 0.6.0
 */