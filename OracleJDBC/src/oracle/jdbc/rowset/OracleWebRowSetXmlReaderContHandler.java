package oracle.jdbc.rowset;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetMetaData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class OracleWebRowSetXmlReaderContHandler extends DefaultHandler {
    private OracleWebRowSet wrset;
    private RowSetMetaData rsetMetaData;
    private Vector updatesToRowSet;
    private Vector keyCols;
    private String columnValue;
    private String propertyValue;
    private String metadataValue;
    private boolean isNullValue;
    private int columnIndex;
    private Hashtable propertyNameTagMap;
    private Hashtable metadataNameTagMap;
    private Hashtable dataNameTagMap;
    protected static final String WEBROWSET_ELEMENT_NAME = "webRowSet";
    protected static final String PROPERTIES_ELEMENT_NAME = "properties";
    protected static final String METADATA_ELEMENT_NAME = "metadata";
    protected static final String DATA_ELEMENT_NAME = "data";
    private int state;
    private static final int INITIAL_STATE = 0;
    private static final int PROPERTIES_STATE = 1;
    private static final int METADATA_STATE = 2;
    private static final int DATA_STATE = 3;
    private int tag;
    private static final int NO_TAG = -1;
    private String[] propertyNames = { "command", "concurrency", "datasource", "escape-processing",
            "fetch-direction", "fetch-size", "isolation-level", "key-columns", "map",
            "max-field-size", "max-rows", "query-timeout", "read-only", "rowset-type",
            "show-deleted", "table-name", "url", "sync-provider", "null", "column", "type",
            "class", "sync-provider-name", "sync-provider-vendor", "sync-provider-version",
            "sync-provider-grade", "data-source-lock" };
    private boolean readReadOnlyValue;
    private static final int PROPERTY_COMMAND_TAG = 0;
    private static final int PROPERTY_CONCURRENCY_TAG = 1;
    private static final int PROPERTY_DATASOURCETAG = 2;
    private static final int PROPERTY_ESCAPEPROCESSING_TAG = 3;
    private static final int PROPERTY_FETCHDIRECTION_TAG = 4;
    private static final int PROPERTY_FETCHSIZE_TAG = 5;
    private static final int PROPERTY_ISOLATIONLEVEL_TAG = 6;
    private static final int PROPERTY_KEYCOLUMNS_TAG = 7;
    private static final int PROPERTY_MAP_TAG = 8;
    private static final int PROPERTY_MAXFIELDSIZE_TAG = 9;
    private static final int PROPERTY_MAXROWS_TAG = 10;
    private static final int PROPERTY_QUERYTIMEOUT_TAG = 11;
    private static final int PROPERTY_READONLY_TAG = 12;
    private static final int PROPERTY_ROWSETTYPE_TAG = 13;
    private static final int PROPERTY_SHOWDELETED_TAG = 14;
    private static final int PROPERTY_TABLENAME_TAG = 15;
    private static final int PROPERTY_URL_TAG = 16;
    private static final int PROPERTY_SYNCPROVIDER_TAG = 17;
    private static final int PROPERTY_NULL_TAG = 18;
    private static final int PROPERTY_COLUMN_TAG = 19;
    private static final int PROPERTY_TYPE_TAG = 20;
    private static final int PROPERTY_CLASS_TAG = 21;
    private static final int PROPERTY_SYNCPROVIDERNAME_TAG = 22;
    private static final int PROPERTY_SYNCPROVIDERVENDOR_TAG = 23;
    private static final int PROPERTY_SYNCPROVIDERVERSION_TAG = 24;
    private static final int PROPERTY_SYNCPROVIDERGRADE_TAG = 25;
    private static final int PROPERTY_DATASOURCELOCK_TAG = 26;
    private String[] metadataNames = { "column-count", "column-definition", "column-index",
            "auto-increment", "case-sensitive", "currency", "nullable", "signed", "searchable",
            "column-display-size", "column-label", "column-name", "schema-name",
            "column-precision", "column-scale", "table-name", "catalog-name", "column-type",
            "column-type-name", "null" };
    private static final int METADATA_COLUMNCOUNT_TAG = 0;
    private static final int METADATA_COLUMNDEFINITION_TAG = 1;
    private static final int METADATA_COLUMNINDEX_TAG = 2;
    private static final int METADATA_AUTOINCREMENT_TAG = 3;
    private static final int METADATA_CASESENSITIVE_TAG = 4;
    private static final int METADATA_CURRENCY_TAG = 5;
    private static final int METADATA_NULLABLE_TAG = 6;
    private static final int METADATA_SIGNED_TAG = 7;
    private static final int METADATA_SEARCHABLE_TAG = 8;
    private static final int METADATA_COLUMNDISPLAYSIZE_TAG = 9;
    private static final int METADATA_COLUMNLABEL_TAG = 10;
    private static final int METADATA_COLUMNNAME_TAG = 11;
    private static final int METADATA_SCHEMANAME_TAG = 12;
    private static final int METADATA_COLUMNPRECISION_TAG = 13;
    private static final int METADATA_COLUMNSCALE_TAG = 14;
    private static final int METADATA_TABLENAME_TAG = 15;
    private static final int METADATA_CATALOGNAME_TAG = 16;
    private static final int METADATA_COLUMNTYPE_TAG = 17;
    private static final int METADATA_COLUMNTYPENAME_TAG = 18;
    private static final int METADATA_NULL_TAG = 19;
    private String[] dataNames = { "currentRow", "insertRow", "deleteRow", "modifyRow",
            "columnValue", "updateValue", "null" };
    private static final int DATA_CURRENTROW_TAG = 0;
    private static final int DATA_INSERTROW_TAG = 1;
    private static final int DATA_DELETEROW_TAG = 2;
    private static final int DATA_MODIFYROW_TAG = 3;
    private static final int DATA_COLUMNVALUE_TAG = 4;
    private static final int DATA_UPDATEVALUE_TAG = 5;
    private static final int DATA_NULL_TAG = 6;

    OracleWebRowSetXmlReaderContHandler(RowSet rowset) {
        this.wrset = ((OracleWebRowSet) rowset);

        initialize();
    }

    public void characters(char[] chrs, int start, int length) throws SAXException {
        String elementValue = new String(chrs, start, length);
        processElement(elementValue);
    }

    public void endDocument() throws SAXException {
        try {
            if (this.readReadOnlyValue) {
                this.wrset.setReadOnly(this.readReadOnlyValue);
            }

        } catch (SQLException sqlexc) {
            throw new SAXException(sqlexc.getMessage());
        }
    }

    public void endElement(String nsUri, String localName, String qualifiedName)
            throws SAXException {
        String elementName = (localName == null) || (localName.equals("")) ? qualifiedName
                : localName;

        switch (getState()) {
        case 1:
            if (elementName.equals("properties")) {
                this.state = 0;
            } else {
                try {
                    int pTag = ((Integer) this.propertyNameTagMap.get(elementName)).intValue();

                    switch (pTag) {
                    case 7:
                        if (this.keyCols == null)
                            break;
                        int[] keyColumns = new int[this.keyCols.size()];

                        for (int i = 0; i < keyColumns.length; i++) {
                            keyColumns[i] = Integer.parseInt((String) this.keyCols.elementAt(i));
                        }
                        this.wrset.setKeyColumns(keyColumns);
                    }

                    setPropertyValue(this.propertyValue);
                } catch (SQLException sqlexc) {
                    throw new SAXException(sqlexc.getMessage());
                }

                this.propertyValue = new String("");

                setNullValue(false);

                setTag(-1);
            }

            break;
        case 2:
            if (elementName.equals("metadata")) {
                try {
                    this.wrset.setMetaData(this.rsetMetaData);

                    this.state = 0;
                } catch (SQLException sqlexc) {
                    throw new SAXException("Error setting metadata in WebRowSet: "
                            + sqlexc.getMessage());
                }

            } else {
                try {
                    setMetaDataValue(this.metadataValue);
                } catch (SQLException sqlexc) {
                    throw new SAXException("Error setting metadata value: " + sqlexc.getMessage());
                }

                this.metadataValue = new String("");

                setNullValue(false);

                setTag(-1);
            }

            break;
        case 3:
            if (elementName.equals("data")) {
                this.state = 0;
                return;
            }

            int dTag = ((Integer) this.dataNameTagMap.get(elementName)).intValue();

            switch (dTag) {
            default:
                break;
            case 4:
                try {
                    this.columnIndex += 1;

                    insertValue(this.columnValue);

                    this.columnValue = new String("");

                    setNullValue(false);

                    setTag(-1);
                } catch (SQLException sqlexc) {
                    throw new SAXException("Error inserting column values: " + sqlexc.getMessage());
                }

            case 0:
                try {
                    this.wrset.insertRow();
                    this.wrset.moveToCurrentRow();
                    this.wrset.next();

                    OracleRow row = this.wrset.getCurrentRow();
                    row.setInsertedFlag(false);
                    applyUpdates();
                } catch (SQLException sqlexc) {
                    throw new SAXException("Error constructing current row: " + sqlexc.getMessage());
                }

            case 2:
                try {
                    this.wrset.insertRow();
                    this.wrset.moveToCurrentRow();
                    this.wrset.next();

                    OracleRow row = this.wrset.getCurrentRow();
                    row.setInsertedFlag(false);
                    row.setRowDeleted(true);
                    applyUpdates();
                } catch (SQLException sqlexc) {
                    throw new SAXException("Error constructing deleted row: " + sqlexc.getMessage());
                }

            case 1:
                try {
                    this.wrset.insertRow();
                    this.wrset.moveToCurrentRow();
                    this.wrset.next();

                    applyUpdates();
                } catch (SQLException sqlexc) {
                    throw new SAXException("Error constructing inserted row: "
                            + sqlexc.getMessage());
                }

            case 3:
                try {
                    this.wrset.insertRow();
                    this.wrset.moveToCurrentRow();
                    this.wrset.next();

                    OracleRow row = this.wrset.getCurrentRow();
                    row.setRowDeleted(true);
                    applyUpdates();
                } catch (SQLException sqlexc) {
                    throw new SAXException("Error constructing modified row: "
                            + sqlexc.getMessage());
                }
            }
        }
    }

    public void startElement(String nsUri, String localName, String qualifiedName,
            Attributes attributes) throws SAXException {
        String elementName = (localName == null) || (localName.equals("")) ? qualifiedName
                : localName;

        switch (getState()) {
        case 1:
            int pTag = ((Integer) this.propertyNameTagMap.get(elementName)).intValue();

            if (pTag == 18) {
                setNullValue(true);
                this.propertyValue = null;
            } else {
                setTag(pTag);
            }

            break;
        case 2:
            int mTag = ((Integer) this.metadataNameTagMap.get(elementName)).intValue();

            if (mTag == 19) {
                setNullValue(true);
                this.metadataValue = null;
            } else {
                setTag(mTag);
            }

            break;
        case 3:
            int dTag = ((Integer) this.dataNameTagMap.get(elementName)).intValue();

            if (dTag == 6) {
                setNullValue(true);
                this.columnValue = null;
            } else {
                setTag(dTag);

                if ((dTag != 0) && (dTag != 1) && (dTag != 2) && (dTag != 3)) {
                    break;
                }

                this.columnIndex = 0;
                try {
                    this.wrset.moveToInsertRow();
                } catch (SQLException ex) {
                }
            }
        default:
            setState(elementName);
        }
    }

    public void error(SAXParseException saxparseexception) throws SAXParseException {
        throw saxparseexception;
    }

    public void warning(SAXParseException saxparseexception) throws SAXParseException {
        System.out.println("** Warning, line " + saxparseexception.getLineNumber() + ", uri "
                + saxparseexception.getSystemId());

        System.out.println("   " + saxparseexception.getMessage());
    }

    private void initialize() {
        this.propertyNameTagMap = new Hashtable(30);
        int i = this.propertyNames.length;
        for (int j = 0; j < i; j++) {
            this.propertyNameTagMap.put(this.propertyNames[j], new Integer(j));
        }
        this.metadataNameTagMap = new Hashtable(30);
        i = this.metadataNames.length;
        for (int j = 0; j < i; j++) {
            this.metadataNameTagMap.put(this.metadataNames[j], new Integer(j));
        }
        this.dataNameTagMap = new Hashtable(10);
        i = this.dataNames.length;
        for (int j = 0; j < i; j++) {
            this.dataNameTagMap.put(this.dataNames[j], new Integer(j));
        }
        this.updatesToRowSet = new Vector();
        this.columnValue = new String("");
        this.propertyValue = new String("");
        this.metadataValue = new String("");
        this.isNullValue = false;
        this.columnIndex = 0;

        this.readReadOnlyValue = false;
    }

    protected void processElement(String elementValue) throws SAXException {
        try {
            switch (getState()) {
            case 1:
                this.propertyValue = elementValue;
                break;
            case 2:
                this.metadataValue = elementValue;
                break;
            case 3:
                setDataValue(elementValue);
            }

        } catch (SQLException sqlexc) {
            throw new SAXException("processElement: " + sqlexc.getMessage());
        }
    }

    private BigDecimal getBigDecimalValue(String colValue) {
        return new BigDecimal(colValue);
    }

    private byte[] getBinaryValue(String colValue) {
        return colValue.getBytes();
    }

    private boolean getBooleanValue(String colValue) {
        return new Boolean(colValue).booleanValue();
    }

    private byte getByteValue(String colValue) {
        return Byte.parseByte(colValue);
    }

    private Date getDateValue(String colValue) {
        return new Date(getLongValue(colValue));
    }

    private double getDoubleValue(String colValue) {
        return Double.parseDouble(colValue);
    }

    private float getFloatValue(String colValue) {
        return Float.parseFloat(colValue);
    }

    private int getIntegerValue(String colValue) {
        return Integer.parseInt(colValue);
    }

    private long getLongValue(String colValue) {
        return Long.parseLong(colValue);
    }

    private boolean getNullValue() {
        return this.isNullValue;
    }

    private short getShortValue(String colValue) {
        return Short.parseShort(colValue);
    }

    private String getStringValue(String colValue) {
        return colValue;
    }

    private Time getTimeValue(String colValue) {
        return new Time(getLongValue(colValue));
    }

    private Timestamp getTimestampValue(String colValue) {
        return new Timestamp(getLongValue(colValue));
    }

    private Blob getBlobValue(String colValue) throws SQLException {
        return new OracleSerialBlob(colValue.getBytes());
    }

    private Clob getClobValue(String colValue) throws SQLException {
        return new OracleSerialClob(colValue.toCharArray());
    }

    private void applyUpdates() throws SAXException {
        if (this.updatesToRowSet.size() > 0) {
            try {
                Iterator iterator = this.updatesToRowSet.iterator();
                while (iterator.hasNext()) {
                    Object[] update = (Object[]) iterator.next();
                    this.columnIndex = ((Integer) update[0]).intValue();
                    insertValue((String) update[1]);
                }

                this.wrset.updateRow();
            } catch (SQLException sqlexc) {
                throw new SAXException("Error updating row: " + sqlexc.getMessage());
            }

            this.updatesToRowSet.removeAllElements();
        }
    }

    private void insertValue(String colValue) throws SQLException {
        if ((getNullValue()) || (colValue == null)) {
            this.wrset.updateNull(this.columnIndex);
            return;
        }

        int colType = this.wrset.getMetaData().getColumnType(this.columnIndex);

        switch (colType) {
        case -7:
            this.wrset.updateByte(this.columnIndex, getByteValue(colValue));
            break;
        case 5:
            this.wrset.updateShort(this.columnIndex, getShortValue(colValue));
            break;
        case 4:
            this.wrset.updateInt(this.columnIndex, getIntegerValue(colValue));
            break;
        case -5:
            this.wrset.updateLong(this.columnIndex, getLongValue(colValue));
            break;
        case 6:
        case 7:
            this.wrset.updateFloat(this.columnIndex, getFloatValue(colValue));
            break;
        case 8:
            this.wrset.updateDouble(this.columnIndex, getDoubleValue(colValue));
            break;
        case 2:
        case 3:
            this.wrset.updateObject(this.columnIndex, getBigDecimalValue(colValue));
            break;
        case -4:
        case -3:
        case -2:
            this.wrset.updateBytes(this.columnIndex, getBinaryValue(colValue));
            break;
        case 91:
            this.wrset.updateDate(this.columnIndex, getDateValue(colValue));
            break;
        case 92:
            this.wrset.updateTime(this.columnIndex, getTimeValue(colValue));
            break;
        case 93:
            this.wrset.updateTimestamp(this.columnIndex, getTimestampValue(colValue));
            break;
        case -1:
        case 1:
        case 12:
            this.wrset.updateString(this.columnIndex, getStringValue(colValue));
            break;
        case 2004:
            this.wrset.updateBlob(this.columnIndex, getBlobValue(colValue));
            break;
        case 2005:
            this.wrset.updateClob(this.columnIndex, getClobValue(colValue));
            break;
        default:
            throw new SQLException("The type " + colType + " is not supported currently.");
        }
    }

    private void setPropertyValue(String propValue) throws SQLException {
        boolean flag = getNullValue();

        switch (getTag()) {
        case 7:
        case 8:
        case 17:
        case 18:
        case 20:
        case 21:
        default:
            break;
        case 0:
            if (flag) {
                this.wrset.setCommand(null);
            } else {
                this.wrset.setCommand(propValue);
            }

            break;
        case 1:
            if (flag) {
                throw new SQLException("Bad value; non-nullable property");
            }

            this.wrset.setConcurrency(getIntegerValue(propValue));
            break;
        case 2:
            if (flag) {
                this.wrset.setDataSourceName(null);
            } else {
                this.wrset.setDataSourceName(propValue);
            }

            break;
        case 3:
            if (flag) {
                throw new SQLException("Bad value; non-nullable property");
            }

            this.wrset.setEscapeProcessing(getBooleanValue(propValue));
            break;
        case 4:
            if (flag) {
                throw new SQLException("Bad value; non-nullable property");
            }

            int _rsetType = this.wrset.getType();
            if (_rsetType == 1005)
                break;
            this.wrset.setFetchDirection(getIntegerValue(propValue));
            break;
        case 5:
            if (flag) {
                throw new SQLException("Bad value; non-nullable property");
            }

            this.wrset.setFetchSize(getIntegerValue(propValue));
            break;
        case 6:
            if (flag) {
                throw new SQLException("Bad value; non-nullable property");
            }

            this.wrset.setTransactionIsolation(getIntegerValue(propValue));
            break;
        case 19:
            if (this.keyCols == null) {
                this.keyCols = new Vector();
            }

            this.keyCols.add(propValue);
            break;
        case 9:
            if (flag) {
                throw new SQLException("Bad value; non-nullable property");
            }

            this.wrset.setMaxFieldSize(getIntegerValue(propValue));
            break;
        case 10:
            if (flag) {
                throw new SQLException("Bad value; non-nullable property");
            }

            this.wrset.setMaxRows(getIntegerValue(propValue));
            break;
        case 11:
            if (flag) {
                throw new SQLException("Bad value; non-nullable property");
            }

            this.wrset.setQueryTimeout(getIntegerValue(propValue));
            break;
        case 12:
            if (flag) {
                throw new SQLException("Bad value; non-nullable property");
            }

            this.readReadOnlyValue = getBooleanValue(propValue);

            break;
        case 13:
            if (flag) {
                throw new SQLException("Bad value; non-nullable property");
            }

            this.wrset.setType(getIntegerValue(propValue));

            break;
        case 14:
            if (flag) {
                throw new SQLException("Bad value; non-nullable property");
            }

            this.wrset.setShowDeleted(getBooleanValue(propValue));
            break;
        case 15:
            if (flag) {
                this.wrset.setTableName(null);
            } else {
                this.wrset.setTableName(propValue);
            }

            break;
        case 16:
            if (flag) {
                this.wrset.setUrl(null);
            } else {
                this.wrset.setUrl(propValue);
            }

            break;
        case 22:
            if (flag) {
                this.wrset.setSyncProvider(null);
            } else {
                this.wrset.setSyncProvider(propValue);
            }
        }
    }

    private void setMetaDataValue(String metaValue) throws SQLException {
        boolean flag = getNullValue();

        switch (getTag()) {
        case 1:
        default:
            break;
        case 0:
            if (flag) {
                throw new SQLException("Bad value; non-nullable metadata");
            }

            int _columnCount = getIntegerValue(metaValue);

            this.rsetMetaData = new OracleRowSetMetaData(_columnCount);

            this.columnIndex = 0;

            break;
        case 2:
            this.columnIndex += 1;
            break;
        case 3:
            if (flag) {
                throw new SQLException("Bad value; non-nullable metadata");
            }

            this.rsetMetaData.setAutoIncrement(this.columnIndex, getBooleanValue(metaValue));
            break;
        case 4:
            if (flag) {
                throw new SQLException("Bad value; non-nullable metadata");
            }

            this.rsetMetaData.setCaseSensitive(this.columnIndex, getBooleanValue(metaValue));
            break;
        case 5:
            if (flag) {
                throw new SQLException("Bad value; non-nullable metadata");
            }

            this.rsetMetaData.setCurrency(this.columnIndex, getBooleanValue(metaValue));
            break;
        case 6:
            if (flag) {
                throw new SQLException("Bad value; non-nullable metadata");
            }

            this.rsetMetaData.setNullable(this.columnIndex, getIntegerValue(metaValue));
            break;
        case 7:
            if (flag) {
                throw new SQLException("Bad value; non-nullable metadata");
            }

            this.rsetMetaData.setSigned(this.columnIndex, getBooleanValue(metaValue));
            break;
        case 8:
            if (flag) {
                throw new SQLException("Bad value; non-nullable metadata");
            }

            this.rsetMetaData.setSearchable(this.columnIndex, getBooleanValue(metaValue));
            break;
        case 9:
            if (flag) {
                throw new SQLException("Bad value; non-nullable metadata");
            }

            this.rsetMetaData.setColumnDisplaySize(this.columnIndex, getIntegerValue(metaValue));
            break;
        case 10:
            if (flag) {
                this.rsetMetaData.setColumnLabel(this.columnIndex, null);
            } else {
                this.rsetMetaData.setColumnLabel(this.columnIndex, metaValue);
            }

            break;
        case 11:
            if (flag) {
                this.rsetMetaData.setColumnName(this.columnIndex, null);
            } else {
                this.rsetMetaData.setColumnName(this.columnIndex, metaValue);
            }

            break;
        case 12:
            if (flag) {
                this.rsetMetaData.setSchemaName(this.columnIndex, null);
            } else {
                this.rsetMetaData.setSchemaName(this.columnIndex, metaValue);
            }

            break;
        case 13:
            if (flag) {
                throw new SQLException("Bad value; non-nullable metadata");
            }

            this.rsetMetaData.setPrecision(this.columnIndex, getIntegerValue(metaValue));
            break;
        case 14:
            if (flag) {
                throw new SQLException("Bad value; non-nullable metadata");
            }

            this.rsetMetaData.setScale(this.columnIndex, getIntegerValue(metaValue));
            break;
        case 15:
            if (flag) {
                this.rsetMetaData.setTableName(this.columnIndex, null);
            } else {
                this.rsetMetaData.setTableName(this.columnIndex, metaValue);
            }

            break;
        case 16:
            if (flag) {
                this.rsetMetaData.setCatalogName(this.columnIndex, null);
            } else {
                this.rsetMetaData.setCatalogName(this.columnIndex, metaValue);
            }

            break;
        case 17:
            if (flag) {
                throw new SQLException("Bad value; non-nullable metadata");
            }

            this.rsetMetaData.setColumnType(this.columnIndex, getIntegerValue(metaValue));
            break;
        case 18:
            if (flag) {
                this.rsetMetaData.setColumnTypeName(this.columnIndex, null);
            } else {
                this.rsetMetaData.setColumnTypeName(this.columnIndex, metaValue);
            }
        }
    }

    private void setDataValue(String elementValue) throws SQLException {
        switch (getTag()) {
        case 1:
        case 2:
        case 3:
        default:
            break;
        case 4:
            this.columnValue = elementValue;
            break;
        case 5:
            Object[] update = new Object[2];
            update[1] = elementValue;
            update[0] = new Integer(this.columnIndex);
            this.updatesToRowSet.add(update);
        }
    }

    protected void setNullValue(boolean flag) {
        this.isNullValue = flag;
    }

    private int getState() {
        return this.state;
    }

    private int getTag() {
        return this.tag;
    }

    private void setState(String elementName) throws SAXException {
        if (elementName.equals("webRowSet")) {
            this.state = 0;
        } else if (elementName.equals("properties")) {
            if (this.state != 1)
                this.state = 1;
            else {
                this.state = 0;
            }
        } else if (elementName.equals("metadata")) {
            if (this.state != 2)
                this.state = 2;
            else {
                this.state = 0;
            }
        } else if (elementName.equals("data"))
            if (this.state != 3)
                this.state = 3;
            else
                this.state = 0;
    }

    private void setTag(int t) {
        this.tag = t;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleWebRowSetXmlReaderContHandler JD-Core Version: 0.6.0
 */