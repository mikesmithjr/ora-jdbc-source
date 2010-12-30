package oracle.jpub.runtime;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;
import oracle.sql.CustomDatumFactory;
import oracle.sql.Datum;
import oracle.sql.ORADataFactory;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

public class MutableStruct {
    int length;
    STRUCT pickled;
    Datum[] datums;
    Object[] attributes;
    CustomDatumFactory[] old_factories;
    ORADataFactory[] factories;
    int[] sqlTypes;
    boolean pickledCorrect;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public MutableStruct(STRUCT s, int[] sqlTypes, ORADataFactory[] factories) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINER, "MutableStruct (s = " + s + ", sqlTypes = "
                    + sqlTypes + ", factories = " + factories + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.length = factories.length;
        this.pickled = s;
        this.factories = factories;
        this.sqlTypes = sqlTypes;
        this.pickledCorrect = true;
    }

    public MutableStruct(Object[] attributes, int[] sqlTypes, ORADataFactory[] factories) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINER, "MutableStruct (" + attributes + ", " + sqlTypes
                    + ", " + factories + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.length = factories.length;
        this.attributes = attributes;
        this.factories = factories;
        this.sqlTypes = sqlTypes;
        this.pickledCorrect = false;
    }

    public MutableStruct(STRUCT s, int[] sqlTypes, CustomDatumFactory[] factories) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINER, "MutableStruct (" + s + ", " + sqlTypes + ", "
                    + factories + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.length = factories.length;
        this.pickled = s;
        this.old_factories = factories;
        this.sqlTypes = sqlTypes;
        this.pickledCorrect = true;
    }

    public MutableStruct(Object[] attributes, int[] sqlTypes, CustomDatumFactory[] factories) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINER, "MutableStruct (attributes = " + attributes
                    + ", sqlTypes = " + sqlTypes + ", factories =" + factories + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.length = factories.length;
        this.attributes = attributes;
        this.old_factories = factories;
        this.sqlTypes = sqlTypes;
        this.pickledCorrect = false;
    }

    public Datum toDatum(Connection c, String sqlTypeName) throws SQLException {
        if (!this.pickledCorrect) {
            this.pickled = new STRUCT(StructDescriptor.createDescriptor(sqlTypeName, c), c,
                    getDatumAttributes(c));

            this.pickledCorrect = true;
        }

        return this.pickled;
    }

    public Datum toDatum(oracle.jdbc.OracleConnection c, String sqlTypeName) throws SQLException {
        return toDatum(c, sqlTypeName);
    }

    /** @deprecated */
    public Datum toDatum(oracle.jdbc.driver.OracleConnection c, String sqlTypeName)
            throws SQLException {
        return toDatum(c, sqlTypeName);
    }

    public Object getAttribute(int n) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINEST, "MutableStruct.getAttribute (n = " + n + ")",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        Object attribute = getLazyAttributes()[n];

        if (attribute == null) {
            Datum d = getLazyDatums()[n];

            if (this.old_factories == null) {
                attribute = Util.convertToObject(d, this.sqlTypes[n], this.factories[n]);
                this.attributes[n] = attribute;

                if (Util.isMutable(d, this.factories[n]))
                    resetDatum(n);
            } else {
                attribute = Util.convertToObject(d, this.sqlTypes[n], this.old_factories[n]);
                this.attributes[n] = attribute;

                if (Util.isMutable(d, this.old_factories[n])) {
                    resetDatum(n);
                }

            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINEST, "MutableStruct.getAttribute:return attribute ="
                    + attribute, this);

            OracleLog.recursiveTrace = false;
        }

        return attribute;
    }

    public Object getOracleAttribute(int n) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINEST, "MutableStruct.getOracleAttribute (n = " + n
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }
        Object o;
        if (this.old_factories == null) {
            if (this.factories[n] == null) {
                o = getDatumAttribute(n, null);

                Datum d = getLazyDatums()[n];

                if (Util.isMutable(d, this.factories[n]))
                    this.pickledCorrect = false;
            } else {
                o = getAttribute(n);
            }

        } else if (this.old_factories[n] == null) {
            o = getDatumAttribute(n, null);

            Datum d = getLazyDatums()[n];

            if (Util.isMutable(d, this.old_factories[n]))
                this.pickledCorrect = false;
        } else {
            o = getAttribute(n);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINEST,
                                     "MutableStruct.getOracleAttribute:return object = " + o, this);

            OracleLog.recursiveTrace = false;
        }

        return o;
    }

    public Object[] getAttributes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINER, "MutableStruct.getAttributes ()", this);

            OracleLog.recursiveTrace = false;
        }

        for (int i = 0; i < this.length; i++) {
            getAttribute(i);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINEST,
                                     "MutableStruct.getAttributes:return attributes = "
                                             + this.attributes, this);

            OracleLog.recursiveTrace = false;
        }

        return this.attributes;
    }

    public Object[] getOracleAttributes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINER, "MutableStruct.getOracleAttributes ()", this);

            OracleLog.recursiveTrace = false;
        }

        Object[] result = new Object[this.length];

        for (int i = 0; i < this.length; i++) {
            result[i] = getOracleAttribute(i);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINER,
                                     "MutableStruct.getOracleAttributes:return result = " + result,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public void setAttribute(int n, Object attribute) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINER, "MutableStruct.setAttribute (n = " + n
                    + ", attribute = " + attribute + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (attribute == null) {
            getLazyDatums();
        }

        resetDatum(n);

        getLazyAttributes()[n] = attribute;
    }

    public void setDoubleAttribute(int n, double attribute) throws SQLException {
        setAttribute(n, new Double(attribute));
    }

    public void setFloatAttribute(int n, float attribute) throws SQLException {
        setAttribute(n, new Float(attribute));
    }

    public void setIntAttribute(int n, int attribute) throws SQLException {
        setAttribute(n, new Integer(attribute));
    }

    public void setOracleAttribute(int n, Object attribute) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINER, "MutableStruct.setOracleAttribute (n = " + n
                    + ", attribute = " + attribute + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.old_factories == null) {
            if (this.factories[n] == null)
                setDatumAttribute(n, (Datum) attribute);
            else {
                setAttribute(n, attribute);
            }

        } else if (this.old_factories[n] == null)
            setDatumAttribute(n, (Datum) attribute);
        else
            setAttribute(n, attribute);
    }

    Datum getDatumAttribute(int n, Connection c) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINER, "MutableStruct.getDatumAttribute (n = " + n
                    + ", connection  = " + c + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getLazyDatums()[n];

        if (datum == null) {
            Object a = getLazyAttributes()[n];

            datum = Util.convertToOracle(a, c);
            this.datums[n] = datum;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINER,
                                     "MutableStruct.getDatumAttribute:return " + datum, this);

            OracleLog.recursiveTrace = false;
        }

        return datum;
    }

    void setDatumAttribute(int n, Datum datum) throws SQLException {
        resetAttribute(n);

        getLazyDatums()[n] = datum;
        this.pickledCorrect = false;
    }

    Datum[] getDatumAttributes(Connection c) throws SQLException {
        for (int i = 0; i < this.length; i++) {
            getDatumAttribute(i, c);
        }

        return (Datum[]) this.datums.clone();
    }

    void resetAttribute(int n) throws SQLException {
        if (this.attributes != null) {
            this.attributes[n] = null;
        }
    }

    void resetDatum(int n) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINER, "MutableStruct.resetDatum (n = " + n + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.datums != null) {
            this.datums[n] = null;
        }

        this.pickledCorrect = false;
    }

    Object[] getLazyAttributes() {
        if (this.attributes == null) {
            this.attributes = new Object[this.length];
        }

        return this.attributes;
    }

    Datum[] getLazyDatums() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINEST, "MutableStruct.getLazyDatums ()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.datums == null) {
            if (this.pickled != null) {
                this.datums = this.pickled.getOracleAttributes();
                this.pickledCorrect = true;

                if (this.attributes != null) {
                    for (int x = 0; x < this.length; x++) {
                        if (this.attributes[x] == null)
                            continue;
                        this.datums[x] = null;
                        this.pickledCorrect = false;
                    }
                }

            } else {
                this.datums = new Datum[this.length];
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINEST, "MutableStruct.getLazyDatums:return "
                    + this.datums, this);

            OracleLog.recursiveTrace = false;
        }

        return this.datums;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jpub.runtime.MutableStruct"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jpub.runtime.MutableStruct JD-Core Version: 0.6.0
 */