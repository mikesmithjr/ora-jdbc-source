package oracle.jdbc.rowset;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.SyncProvider;

class OracleWebRowSetXmlWriterImpl implements OracleWebRowSetXmlWriter {
    private Writer xmlWriter;
    private Stack xmlTagStack;
    private static final String WEBROWSET_ELEMENT = "webRowSet";
    private static final String PROPERTIES_ELEMENT = "properties";
    private static final String METADATA_ELEMENT = "metadata";
    private static final String DATA_ELEMENT = "data";
    private static final String PROPERTY_COMMAND = "command";
    private static final String PROPERTY_CONCURRENCY = "concurrency";
    private static final String PROPERTY_DATASOURCE = "datasource";
    private static final String PROPERTY_ESCAPEPROCESSING = "escape-processing";
    private static final String PROPERTY_FETCHDIRECTION = "fetch-direction";
    private static final String PROPERTY_FETCHSIZE = "fetch-size";
    private static final String PROPERTY_ISOLATIONLEVEL = "isolation-level";
    private static final String PROPERTY_KEYCOLUMNS = "key-columns";
    private static final String PROPERTY_MAP = "map";
    private static final String PROPERTY_MAXFIELDSIZE = "max-field-size";
    private static final String PROPERTY_MAXROWS = "max-rows";
    private static final String PROPERTY_QUERYTIMEOUT = "query-timeout";
    private static final String PROPERTY_READONLY = "read-only";
    private static final String PROPERTY_ROWSETTYPE = "rowset-type";
    private static final String PROPERTY_SHOWDELETED = "show-deleted";
    private static final String PROPERTY_TABLENAME = "table-name";
    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_SYNCPROVIDER = "sync-provider";
    private static final String PROPERTY_NULL = "null";
    private static final String PROPERTY_KC_COLUMN = "column";
    private static final String PROPERTY_MAP_TYPE = "type";
    private static final String PROPERTY_MAP_CLASS = "class";
    private static final String PROPERTY_S_PROVIDERNAME = "sync-provider-name";
    private static final String PROPERTY_S_PROVIDERVENDOR = "sync-provider-vendor";
    private static final String PROPERTY_S_PROVIDERVERSION = "sync-provider-version";
    private static final String PROPERTY_S_PROVIDERGRADE = "sync-provider-grade";
    private static final String PROPERTY_S_DATASOURCELOCK = "data-source-lock";
    private static final String METADATA_COLUMNCOUNT = "column-count";
    private static final String METADATA_COLUMNDEFINITION = "column-definition";
    private static final String METADATA_COLUMNINDEX = "column-index";
    private static final String METADATA_AUTOINCREMENT = "auto-increment";
    private static final String METADATA_CASESENSITIVE = "case-sensitive";
    private static final String METADATA_CURRENCY = "currency";
    private static final String METADATA_NULLABLE = "nullable";
    private static final String METADATA_SIGNED = "signed";
    private static final String METADATA_SEARCHABLE = "searchable";
    private static final String METADATA_COLUMNDISPLAYSIZE = "column-display-size";
    private static final String METADATA_COLUMNLABEL = "column-label";
    private static final String METADATA_COLUMNNAME = "column-name";
    private static final String METADATA_SCHEMANAME = "schema-name";
    private static final String METADATA_COLUMNPRECISION = "column-precision";
    private static final String METADATA_COLUMNSCALE = "column-scale";
    private static final String METADATA_TABLENAME = "table-name";
    private static final String METADATA_CATALOGNAME = "catalog-name";
    private static final String METADATA_COLUMNTYPE = "column-type";
    private static final String METADATA_COLUMNTYPENAME = "column-type-name";
    private static final String METADATA_NULL = "null";
    private static final String DATA_CURRENTROW = "currentRow";
    private static final String DATA_INSERTROW = "insertRow";
    private static final String DATA_DELETEROW = "deleteRow";
    private static final String DATA_MODIFYROW = "modifyRow";
    private static final String DATA_COLUMNVALUE = "columnValue";
    private static final String DATA_UPDATEVALUE = "updateValue";
    private static final String DATA_NULL = "null";

    public void writeXML(WebRowSet webrowset, Writer writer) throws SQLException {
        if (!(webrowset instanceof OracleWebRowSet)) {
            throw new SQLException("Invalid WebRowSet argument");
        }
        this.xmlTagStack = new Stack();
        this.xmlWriter = writer;
        writeRowSet((OracleWebRowSet) webrowset);
    }

    public boolean writeData(RowSetInternal internal) throws SQLException {
        return false;
    }

    private void writeRowSet(OracleWebRowSet webrowset) throws SQLException {
        try {
            writeHeaderAndStartWebRowSetElement();
            writeProperties(webrowset);
            writeMetaData(webrowset);
            writeData(webrowset);
            endWebRowSetElement();
        } catch (IOException ioexception) {
            throw new SQLException("IOException: " + ioexception.getMessage());
        }
    }

    private void writeHeaderAndStartWebRowSetElement() throws IOException {
        this.xmlWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        this.xmlWriter.write("\n");

        setCurrentTag("webRowSet");

        this.xmlWriter.write("<webRowSet xmlns=\"http://java.sun.com/xml/ns/jdbc\"\n");
        this.xmlWriter
                .write("           xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        this.xmlWriter.write("           xsi:schemaLocation=\"http://java.sun.com/xml/ns/jdbc ");
        this.xmlWriter.write("http://java.sun.com/xml/ns/jdbc/webrowset.xsd");
        this.xmlWriter.write("\">\n");
    }

    private void endWebRowSetElement() throws IOException {
        endTag("webRowSet");
    }

    private void startElement(String elementName) throws IOException {
        startTag(elementName);
        this.xmlWriter.write("\n");
    }

    private void endElement(String elementName) throws IOException {
        writeIndent(this.xmlTagStack.size());
        endTag(elementName);
    }

    private void endElement() throws IOException {
        writeIndent(this.xmlTagStack.size());
        String currentTag = getCurrentTag();
        this.xmlWriter.write("</" + currentTag + ">\n");
        this.xmlWriter.flush();
    }

    private void startTag(String tag) throws IOException {
        setCurrentTag(tag);
        writeIndent(this.xmlTagStack.size());
        this.xmlWriter.write("<" + tag + ">");
    }

    private void endTag(String tag) throws IOException {
        String currentTag = getCurrentTag();
        if (tag.equals(currentTag))
            this.xmlWriter.write("</" + currentTag + ">\n");
        this.xmlWriter.flush();
    }

    private void setCurrentTag(String tag) {
        this.xmlTagStack.push(tag);
    }

    private String getCurrentTag() {
        return (String) this.xmlTagStack.pop();
    }

    private void writeEmptyElement(String elementName) throws IOException {
        this.xmlWriter.write("<" + elementName + "/>");
    }

    private void writeProperties(OracleWebRowSet webrowset) throws IOException {
        startElement("properties");
        try {
            writeElementString("command", webrowset.getCommand());
            writeElementInteger("concurrency", webrowset.getConcurrency());
            writeElementString("datasource", webrowset.getDataSourceName());
            writeElementBoolean("escape-processing", webrowset.getEscapeProcessing());
            writeElementInteger("fetch-direction", webrowset.getFetchDirection());
            writeElementInteger("fetch-size", webrowset.getFetchSize());
            writeElementInteger("isolation-level", webrowset.getTransactionIsolation());

            startElement("key-columns");
            int[] keyColumns = webrowset.getKeyColumns();
            for (int i = 0; (keyColumns != null) && (i < keyColumns.length); i++)
                writeElementInteger("column", keyColumns[i]);
            endElement("key-columns");

            startElement("map");
            Map map = webrowset.getTypeMap();
            Iterator iterator;
            if (map != null) {
                for (iterator = map.keySet().iterator(); iterator.hasNext();) {
                    String typeName = (String) iterator.next();
                    Class currentClass = (Class) map.get(typeName);
                    writeElementString("type", typeName);
                    writeElementString("class", currentClass.getName());
                }
            }
            endElement("map");

            writeElementInteger("max-field-size", webrowset.getMaxFieldSize());
            writeElementInteger("max-rows", webrowset.getMaxRows());
            writeElementInteger("query-timeout", webrowset.getQueryTimeout());
            writeElementBoolean("read-only", webrowset.isReadOnly());
            writeElementInteger("rowset-type", webrowset.getType());
            writeElementBoolean("show-deleted", webrowset.getShowDeleted());
            writeElementString("table-name", webrowset.getTableName());
            writeElementString("url", webrowset.getUrl());

            startElement("sync-provider");

            SyncProvider syncProvider = webrowset.getSyncProvider();
            writeElementString("sync-provider-name", syncProvider.getProviderID());
            writeElementString("sync-provider-vendor", syncProvider.getVendor());
            writeElementString("sync-provider-version", syncProvider.getVersion());
            writeElementInteger("sync-provider-grade", syncProvider.getProviderGrade());
            writeElementInteger("data-source-lock", syncProvider.getDataSourceLock());

            endElement("sync-provider");
        } catch (SQLException sqlexception) {
            throw new IOException("SQLException: " + sqlexception.getMessage());
        }

        endElement("properties");
    }

    private void writeMetaData(OracleWebRowSet webrowset) throws IOException {
        startElement("metadata");
        try {
            ResultSetMetaData rsmd = webrowset.getMetaData();

            int columnCount = rsmd.getColumnCount();
            writeElementInteger("column-count", columnCount);

            for (int j = 1; j <= columnCount; j++) {
                startElement("column-definition");

                writeElementInteger("column-index", j);
                writeElementBoolean("auto-increment", rsmd.isAutoIncrement(j));
                writeElementBoolean("case-sensitive", rsmd.isCaseSensitive(j));
                writeElementBoolean("currency", rsmd.isCurrency(j));
                writeElementInteger("nullable", rsmd.isNullable(j));
                writeElementBoolean("signed", rsmd.isSigned(j));
                writeElementBoolean("searchable", rsmd.isSearchable(j));
                writeElementInteger("column-display-size", rsmd.getColumnDisplaySize(j));
                writeElementString("column-label", rsmd.getColumnLabel(j));
                writeElementString("column-name", rsmd.getColumnName(j));
                writeElementString("schema-name", rsmd.getSchemaName(j));
                writeElementInteger("column-precision", rsmd.getPrecision(j));
                writeElementInteger("column-scale", rsmd.getScale(j));
                writeElementString("table-name", rsmd.getTableName(j));
                writeElementString("catalog-name", rsmd.getCatalogName(j));
                writeElementInteger("column-type", rsmd.getColumnType(j));
                writeElementString("column-type-name", rsmd.getColumnTypeName(j));

                endElement("column-definition");
            }
        } catch (SQLException sqlexception) {
            throw new IOException("SQLException: " + sqlexception.getMessage());
        }

        endElement("metadata");
    }

    private void writeElementBoolean(String elementName, boolean flag) throws IOException {
        startTag(elementName);
        writeBoolean(flag);
        endTag(elementName);
    }

    private void writeElementInteger(String elementName, int i) throws IOException {
        startTag(elementName);
        writeInteger(i);
        endTag(elementName);
    }

    private void writeElementString(String elementName, String str) throws IOException {
        startTag(elementName);
        writeString(str);
        endTag(elementName);
    }

    private void writeData(OracleWebRowSet webrowset) throws IOException {
        try {
            ResultSetMetaData rsmd = webrowset.getMetaData();
            int columnCount = rsmd.getColumnCount();

            startElement("data");

            webrowset.beforeFirst();
            webrowset.setShowDeleted(true);
            for (; webrowset.next(); endElement()) {
                if ((webrowset.rowDeleted()) && (webrowset.rowInserted()))
                    startElement("modifyRow");
                else if (webrowset.rowDeleted())
                    startElement("deleteRow");
                else if (webrowset.rowInserted())
                    startElement("insertRow");
                else
                    startElement("currentRow");
                for (int j = 1; j <= columnCount; j++) {
                    if (webrowset.columnUpdated(j)) {
                        ResultSet resultset = webrowset.getOriginalRow();
                        resultset.next();

                        startTag("columnValue");
                        writeValue(j, (RowSet) resultset);
                        endTag("columnValue");

                        startTag("updateValue");
                        writeValue(j, webrowset);
                        endTag("updateValue");
                    } else {
                        startTag("columnValue");
                        writeValue(j, webrowset);
                        endTag("columnValue");
                    }
                }
            }

            endElement("data");
        } catch (SQLException sqlexception) {
            throw new IOException("SQLException: " + sqlexception.getMessage());
        }
    }

    private void writeBigDecimal(BigDecimal bigdecimal) throws IOException {
        if (bigdecimal != null)
            this.xmlWriter.write(bigdecimal.toString());
        else
            writeEmptyElement("null");
    }

    private void writeBoolean(boolean flag) throws IOException {
        this.xmlWriter.write(new Boolean(flag).toString());
    }

    private void writeDouble(double d) throws IOException {
        this.xmlWriter.write(Double.toString(d));
    }

    private void writeFloat(float f) throws IOException {
        this.xmlWriter.write(Float.toString(f));
    }

    private void writeInteger(int i) throws IOException {
        this.xmlWriter.write(Integer.toString(i));
    }

    private void writeLong(long l) throws IOException {
        this.xmlWriter.write(Long.toString(l));
    }

    private void writeNull() throws IOException {
        writeEmptyElement("null");
    }

    private void writeShort(short shrt) throws IOException {
        this.xmlWriter.write(Short.toString(shrt));
    }

    private void writeBytes(byte[] b) throws IOException {
        this.xmlWriter.write(new String(b));
    }

    private void writeString(String str) throws IOException {
        if (str != null)
            this.xmlWriter.write(str);
        else
            this.xmlWriter.write("");
    }

    private void writeIndent(int indent) throws IOException {
        for (int j = 1; j < indent; j++)
            this.xmlWriter.write("  ");
    }

    private void writeValue(int columnIndex, RowSet rowset) throws IOException {
        try {
            int columnType = rowset.getMetaData().getColumnType(columnIndex);
            switch (columnType) {
            case -7:
            case 5:
                short shrt = rowset.getShort(columnIndex);
                if (rowset.wasNull())
                    writeNull();
                else
                    writeShort(shrt);
                break;
            case 4:
                int val = rowset.getInt(columnIndex);
                if (rowset.wasNull())
                    writeNull();
                else
                    writeInteger(val);
                break;
            case -5:
                long l = rowset.getLong(columnIndex);
                if (rowset.wasNull())
                    writeNull();
                else
                    writeLong(l);
                break;
            case 6:
            case 7:
                float f = rowset.getFloat(columnIndex);
                if (rowset.wasNull())
                    writeNull();
                else
                    writeFloat(f);
                break;
            case 8:
                double d = rowset.getDouble(columnIndex);
                if (rowset.wasNull())
                    writeNull();
                else
                    writeDouble(d);
                break;
            case 2:
            case 3:
                BigDecimal bigdec = rowset.getBigDecimal(columnIndex);
                if (rowset.wasNull())
                    writeNull();
                else
                    writeBigDecimal(bigdec);
                break;
            case 91:
                Date date = rowset.getDate(columnIndex);
                if (rowset.wasNull())
                    writeNull();
                else
                    writeLong(date.getTime());
                break;
            case 92:
                Time time = rowset.getTime(columnIndex);
                if (rowset.wasNull())
                    writeNull();
                else
                    writeLong(time.getTime());
                break;
            case 93:
                Timestamp timestamp = rowset.getTimestamp(columnIndex);
                if (rowset.wasNull())
                    writeNull();
                else
                    writeLong(timestamp.getTime());
                break;
            case -4:
            case -3:
            case -2:
            case 2004:
                byte[] bytes = rowset.getBytes(columnIndex);
                if (rowset.wasNull())
                    writeNull();
                else
                    writeBytes(bytes);
                break;
            case -1:
            case 1:
            case 12:
            case 2005:
                String str = rowset.getString(columnIndex);
                if (rowset.wasNull())
                    writeNull();
                else
                    writeString(str);
                break;
            default:
                throw new SQLException("The type " + columnType + " is not supported currently.");
            }
        } catch (SQLException sqlexception) {
            throw new IOException("Failed to writeValue: " + sqlexception.getMessage());
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleWebRowSetXmlWriterImpl JD-Core Version: 0.6.0
 */