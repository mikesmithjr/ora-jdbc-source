package oracle.jpub.runtime;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import oracle.jdbc.driver.OracleLog;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.BFILE;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CustomDatum;
import oracle.sql.CustomDatumFactory;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.RAW;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

public class MutableArray {
    int length;
    Object[] elements;
    Datum[] datums;
    ARRAY pickled;
    boolean pickledCorrect;
    int sqlType;
    ORADataFactory factory;
    CustomDatumFactory old_factory;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public MutableArray(int sqlType, ARRAY s, ORADataFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (sqlType = " + sqlType + ", struct = "
                    + s + ", factory = " + factory + ")");
        }

        this.length = -1;
        this.elements = null;
        this.datums = null;
        this.pickled = s;
        this.pickledCorrect = true;
        this.sqlType = sqlType;
        this.factory = factory;
    }

    public MutableArray(int sqlType, Datum[] d, ORADataFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (sqlType =" + sqlType + ", datum = "
                    + d + ", factory = " + factory + ")");
        }

        this.sqlType = sqlType;
        this.factory = factory;

        setDatumArray(d);
    }

    public MutableArray(int sqlType, Object[] a, ORADataFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (sqlType = " + sqlType + ", object = "
                    + a + ", factory = " + factory + ")");
        }

        this.sqlType = sqlType;
        this.factory = factory;

        setObjectArray(a);
    }

    public MutableArray(int sqlType, double[] a, ORADataFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (sqlType = " + sqlType + ", double = "
                    + a + ", factory = " + factory + ")");
        }

        this.sqlType = sqlType;
        this.factory = factory;

        setArray(a);
    }

    public MutableArray(int sqlType, int[] a, ORADataFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (sqlType = " + sqlType + ", int = "
                    + a + ", factory = " + factory + ")");
        }

        this.sqlType = sqlType;
        this.factory = factory;

        setArray(a);
    }

    public MutableArray(int sqlType, float[] a, ORADataFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (sqlType = " + sqlType + ", float = "
                    + a + ", factory = " + factory + ")");
        }

        this.sqlType = sqlType;
        this.factory = factory;

        setArray(a);
    }

    public MutableArray(int sqlType, short[] a, ORADataFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (sqlType = " + sqlType + ", short = "
                    + a + ", factory =" + factory + ")");
        }

        this.sqlType = sqlType;
        this.factory = factory;

        setArray(a);
    }

    public MutableArray(ARRAY s, int sqlType, CustomDatumFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (array = " + s + ", sqlType = "
                    + sqlType + ", factory = " + factory + ")");
        }

        this.length = -1;
        this.elements = null;
        this.datums = null;
        this.pickled = s;
        this.pickledCorrect = true;
        this.sqlType = sqlType;
        this.old_factory = factory;
    }

    public MutableArray(Datum[] d, int sqlType, CustomDatumFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (datum = " + d + ", sqlType = "
                    + sqlType + ", factory =" + factory + ")");
        }

        this.sqlType = sqlType;
        this.old_factory = factory;

        setDatumArray(d);
    }

    public MutableArray(Object[] a, int sqlType, CustomDatumFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (object = " + a + ", sqlType = "
                    + sqlType + ", factory = " + factory + ")");
        }

        this.sqlType = sqlType;
        this.old_factory = factory;

        setObjectArray(a);
    }

    public MutableArray(double[] a, int sqlType, CustomDatumFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (double = " + a + ", sqlType = "
                    + sqlType + ", factory = " + factory + ")");
        }

        this.sqlType = sqlType;
        this.old_factory = factory;

        setArray(a);
    }

    public MutableArray(int[] a, int sqlType, CustomDatumFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (int = " + a + ", sqlType = "
                    + sqlType + ", factory = " + factory + ")");
        }

        this.sqlType = sqlType;
        this.old_factory = factory;

        setArray(a);
    }

    public MutableArray(float[] a, int sqlType, CustomDatumFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (float = " + a + ", sqlType = "
                    + sqlType + ", factory = " + factory + ")");
        }

        this.sqlType = sqlType;
        this.old_factory = factory;

        setArray(a);
    }

    public MutableArray(short[] a, int sqlType, CustomDatumFactory factory) {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray (short = " + a + ", sqlType ="
                    + sqlType + ", factory = " + factory + ")");
        }

        this.sqlType = sqlType;
        this.old_factory = factory;

        setArray(a);
    }

    public Datum toDatum(Connection c, String sqlTypeName) throws SQLException {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray.toDatum( connection = " + c + ")");
        }

        if (!this.pickledCorrect) {
            this.pickled = new ARRAY(ArrayDescriptor.createDescriptor(sqlTypeName, c), c,
                    getDatumArray(c));

            this.pickledCorrect = true;
        }

        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray.toDatum:return " + this.pickled);
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

    public Object[] getOracleArray() throws SQLException {
        return getOracleArray(0L, 2147483647);
    }

    public Object[] getOracleArray(long index, int count) throws SQLException {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray.getOracleArray (index = " + index
                    + ", count = " + count + ")");
        }

        int sliceLen = sliceLength(index, count);

        if (sliceLen < 0)
            return null;
        Object[] a;
        switch (this.sqlType) {
        case -13:
            a = new BFILE[sliceLen];

            break;
        case 2004:
            a = new BLOB[sliceLen];

            break;
        case 1:
        case 12:
            a = new CHAR[sliceLen];

            break;
        case 2005:
            a = new CLOB[sliceLen];

            break;
        case 91:
            a = new DATE[sliceLen];

            break;
        case 93:
            a = new TIMESTAMP[sliceLen];

            break;
        case -101:
            a = new TIMESTAMPTZ[sliceLen];

            break;
        case -102:
            a = new TIMESTAMPLTZ[sliceLen];

            break;
        case -104:
            a = new INTERVALDS[sliceLen];

            break;
        case -103:
            a = new INTERVALYM[sliceLen];

            break;
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
            a = new NUMBER[sliceLen];

            break;
        case -2:
            a = new RAW[sliceLen];

            break;
        case 100:
            a = new BINARY_FLOAT[sliceLen];

            break;
        case 101:
            a = new BINARY_DOUBLE[sliceLen];

            break;
        case 0:
        case 2002:
        case 2003:
        case 2006:
        case 2007:
            if (this.old_factory == null) {
                a = new ORAData[sliceLen];
            } else {
                a = new CustomDatum[sliceLen];
            }

            break;
        default:
            throw new SQLException("Unexpected OracleTypes type code: " + this.sqlType);
        }

        return getOracleArray(index, a);
    }

    public Object[] getOracleArray(long index, Object[] a) throws SQLException {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray.getOracleArray (index = " + index
                    + ", object = " + a + ")");
        }

        if (a == null) {
            return null;
        }
        int sliceLen = sliceLength(index, a.length);
        int idx = (int) index;

        if (sliceLen != a.length) {
            return null;
        }
        if ((this.sqlType == 2002) || (this.sqlType == 2007) || (this.sqlType == 2003)
                || (this.sqlType == 2006) || (this.sqlType == 0)) {
            if (this.old_factory == null) {
                for (int i = 0; i < sliceLen; i++) {
                    a[i] = this.factory.create(getDatumElement(idx++, null), this.sqlType);
                }
            } else {
                for (int i = 0; i < sliceLen; i++) {
                    a[i] = this.old_factory.create(getDatumElement(idx++, null), this.sqlType);
                }
            }
        } else {
            for (int i = 0; i < sliceLen; i++) {
                a[i] = getDatumElement(idx++, null);
            }

        }

        if (TRACE) {
            OracleLog.print(this, 2048, 2, 32, "MutableArray.getOracleArray:return " + a);
        }

        return a;
    }

    public Object[] getOracleArray(Object[] a) throws SQLException {
        return getOracleArray(0L, a);
    }

    public Object[] getObjectArray() throws SQLException {
        return getObjectArray(0L, 2147483647);
    }

    public Object[] getObjectArray(long index, int count) throws SQLException {
        int sliceLen = sliceLength(index, count);

        if (sliceLen < 0)
            return null;
        Object[] a;
        switch (this.sqlType) {
        case 1:
        case 12:
            a = new String[sliceLen];

            break;
        case 91:
            a = new Date[sliceLen];

            break;
        case 93:
            a = new Timestamp[sliceLen];

            break;
        case 2:
        case 3:
            a = new BigDecimal[sliceLen];

            break;
        case 6:
        case 8:
            a = new Double[sliceLen];

            break;
        case 4:
        case 5:
            a = new Integer[sliceLen];

            break;
        case 7:
            a = new Float[sliceLen];

            break;
        case -2:
            a = new byte[sliceLen][];

            break;
        default:
            return getOracleArray(index, count);
        }

        return getObjectArray(index, a);
    }

    public Object[] getObjectArray(long index, Object[] a) throws SQLException {
        if (a == null) {
            return null;
        }
        int sliceLen = sliceLength(index, a.length);
        int idx = (int) index;

        if (sliceLen != a.length) {
            return null;
        }
        switch (this.sqlType) {
        case -2:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 12:
        case 91:
        case 93:
            for (int i = 0; i < sliceLen; i++) {
                a[i] = getObjectElement(idx++);
            }
            return a;
        }

        return getOracleArray(index, a);
    }

    public Object[] getObjectArray(Object[] a) throws SQLException {
        return getObjectArray(0L, a);
    }

    public Object getArray() throws SQLException {
        return getArray(0L, 2147483647);
    }

    public Object getArray(long index, int count) throws SQLException {
        int sliceLen = sliceLength(index, count);
        int idx = (int) index;

        if (sliceLen < 0) {
            return null;
        }

        switch (this.sqlType) {
        case 6:
        case 8:
        case 101: {
            double[] a = new double[sliceLen];

            for (int i = 0; i < sliceLen; i++) {
                a[i] = ((Double) getObjectElement(idx++)).doubleValue();
            }
            return a;
        }
        case 100: {
            float[] a = new float[sliceLen];

            for (int i = 0; i < sliceLen; i++) {
                a[i] = ((Float) getObjectElement(idx++)).floatValue();
            }
            return a;
        }
        case 4: {
            int[] a = new int[sliceLen];

            for (int i = 0; i < sliceLen; i++) {
                a[i] = ((Integer) getObjectElement(idx++)).intValue();
            }
            return a;
        }
        case 5: {
            short[] a = new short[sliceLen];

            for (int i = 0; i < sliceLen; i++) {
                a[i] = (short) ((Integer) getObjectElement(idx++)).intValue();
            }
            return a;
        }
        case 7: {
            float[] a = new float[sliceLen];

            for (int i = 0; i < sliceLen; i++) {
                a[i] = ((Float) getObjectElement(idx++)).floatValue();
            }
            return a;
        }
        }

        return getObjectArray(index, count);
    }

    public void setOracleArray(Object[] a) {
        if ((this.factory == null) && (this.old_factory == null))
            setDatumArray((Datum[]) a);
        else
            setObjectArray(a);
    }

    public void setOracleArray(Object[] a, long index) throws SQLException {
        if ((this.factory == null) && (this.old_factory == null))
            setDatumArray((Datum[]) a, index);
        else
            setObjectArray(a, index);
    }

    public void setObjectArray(Object[] a) {
        if (a == null) {
            setNullArray();
        } else {
            setArrayGeneric(a.length);

            this.elements = new Object[this.length];

            for (int i = 0; i < this.length; i++)
                this.elements[i] = a[i];
        }
    }

    public void setObjectArray(Object[] a, long index) throws SQLException {
        if (a == null) {
            return;
        }
        int sliceLen = sliceLength(index, a.length);
        int idx = (int) index;

        for (int i = 0; i < sliceLen; i++) {
            setObjectElement(a[i], idx++);
        }
    }

    public void setArray(double[] a) {
        if (a == null) {
            setNullArray();
        } else {
            setArrayGeneric(a.length);

            this.elements = new Object[this.length];

            for (int i = 0; i < this.length; i++)
                this.elements[i] = new Double(a[i]);
        }
    }

    public void setArray(double[] a, long index) throws SQLException {
        if (a == null) {
            return;
        }
        int sliceLen = sliceLength(index, a.length);
        int idx = (int) index;

        for (int i = 0; i < sliceLen; i++) {
            setObjectElement(new Double(a[i]), idx++);
        }
    }

    public void setArray(int[] a) {
        if (a == null) {
            setNullArray();
        } else {
            setArrayGeneric(a.length);

            this.elements = new Object[this.length];

            for (int i = 0; i < this.length; i++)
                this.elements[i] = new Integer(a[i]);
        }
    }

    public void setArray(int[] a, long index) throws SQLException {
        if (a == null) {
            return;
        }
        int sliceLen = sliceLength(index, a.length);
        int idx = (int) index;

        for (int i = 0; i < sliceLen; i++) {
            setObjectElement(new Integer(a[i]), idx++);
        }
    }

    public void setArray(float[] a) {
        if (a == null) {
            setNullArray();
        } else {
            setArrayGeneric(a.length);

            this.elements = new Object[this.length];

            for (int i = 0; i < this.length; i++)
                this.elements[i] = new Float(a[i]);
        }
    }

    public void setArray(float[] a, long index) throws SQLException {
        if (a == null) {
            return;
        }
        int sliceLen = sliceLength(index, a.length);
        int idx = (int) index;

        for (int i = 0; i < sliceLen; i++) {
            setObjectElement(new Float(a[i]), idx++);
        }
    }

    public void setArray(short[] a) {
        if (a == null) {
            setNullArray();
        } else {
            setArrayGeneric(a.length);

            this.elements = new Object[this.length];

            for (int i = 0; i < this.length; i++)
                this.elements[i] = new Integer(a[i]);
        }
    }

    public void setArray(short[] a, long index) throws SQLException {
        if (a == null) {
            return;
        }
        int sliceLen = sliceLength(index, a.length);
        int idx = (int) index;

        for (int i = 0; i < sliceLen; i++) {
            setObjectElement(new Integer(a[i]), idx++);
        }
    }

    public Object getObjectElement(long n) throws SQLException {
        Object element = getLazyArray()[(int) n];

        if (element == null) {
            if (this.old_factory == null) {
                Datum d = getLazyOracleArray()[(int) n];

                element = Util.convertToObject(d, this.sqlType, this.factory);
                this.elements[(int) n] = element;

                if (Util.isMutable(d, this.factory))
                    resetOracleElement(n);
            } else {
                Datum d = getLazyOracleArray()[(int) n];

                element = Util.convertToObject(d, this.sqlType, this.old_factory);
                this.elements[(int) n] = element;

                if (Util.isMutable(d, this.old_factory)) {
                    resetOracleElement(n);
                }

            }

        }

        return element;
    }

    public Object getOracleElement(long n) throws SQLException {
        if ((this.factory == null) && (this.old_factory == null)) {
            Datum d = getDatumElement(n, null);

            if (Util.isMutable(d, this.factory)) {
                this.pickledCorrect = false;
            }
            return d;
        }

        return getObjectElement(n);
    }

    public void setObjectElement(Object element, long n) throws SQLException {
        OracleLog.print(this, 2048, 2, 64, "MutableArray.setObjectElement( element = " + element
                + ",  n = " + n + ")");

        if (element == null) {
            getLazyOracleArray();
        }

        resetOracleElement(n);

        getLazyArray()[(int) n] = element;
    }

    public void setOracleElement(Object o, long n) throws SQLException {
        if ((this.factory == null) && (this.old_factory == null))
            setDatumElement((Datum) o, n);
        else
            setObjectElement(o, n);
    }

    public String getBaseTypeName() throws SQLException {
        return this.pickled.getBaseTypeName();
    }

    public int getBaseType() throws SQLException {
        return this.pickled.getBaseType();
    }

    public ArrayDescriptor getDescriptor() throws SQLException {
        return this.pickled.getDescriptor();
    }

    Datum[] getDatumArray(Connection c) throws SQLException {
        if (this.length < 0) {
            getLazyOracleArray();
        }
        if (this.datums == null) {
            return null;
        }
        Datum[] result = new Datum[this.length];

        for (int i = 0; i < this.length; i++) {
            result[i] = getDatumElement(i, c);
        }
        return result;
    }

    void setDatumArray(Datum[] d) {
        if (d == null) {
            setNullArray();
        } else {
            this.length = d.length;
            this.elements = null;
            this.datums = ((Datum[]) d.clone());
            this.pickled = null;
            this.pickledCorrect = false;
        }
    }

    void setDatumArray(Datum[] d, long index) throws SQLException {
        if (d == null) {
            return;
        }
        int sliceLen = sliceLength(index, d.length);
        int idx = (int) index;

        for (int i = 0; i < sliceLen; i++) {
            setDatumElement(d[i], idx++);
        }
    }

    Datum getDatumElement(long n, Connection c) throws SQLException {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 64, "MutableArray.getDatumElement( n = " + n
                    + ", connection = " + c + ")");
        }

        Datum datum = getLazyOracleArray()[(int) n];

        if (datum == null) {
            Object a = getLazyArray()[(int) n];

            datum = Util.convertToOracle(a, c);
            this.datums[(int) n] = datum;
        }

        if (TRACE) {
            OracleLog.print(this, 2048, 2, 64, "MutableArray.getDatumElement:return " + datum);
        }

        return datum;
    }

    void setDatumElement(Datum datum, long n) throws SQLException {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 64, "MutableArray.setDatumElement( datum = " + datum
                    + ", n = " + n + ")");
        }

        resetElement(n);

        getLazyOracleArray()[(int) n] = datum;
        this.pickledCorrect = false;
    }

    void resetElement(long n) throws SQLException {
        if (this.elements != null) {
            this.elements[(int) n] = null;
        }
    }

    void setNullArray() {
        this.length = -1;
        this.elements = null;
        this.datums = null;
        this.pickled = null;
        this.pickledCorrect = false;
    }

    void setArrayGeneric(int length) {
        this.length = length;
        this.datums = new Datum[length];
        this.pickled = null;
        this.pickledCorrect = false;
    }

    public int length() throws SQLException {
        if (this.length < 0) {
            getLazyOracleArray();
        }
        return this.length;
    }

    public int sliceLength(long index, int reqLength) throws SQLException {
        if (this.length < 0) {
            getLazyOracleArray();
        }
        if (index < 0L) {
            return (int) index;
        }
        return Math.min(this.length - (int) index, reqLength);
    }

    void resetOracleElement(long n) throws SQLException {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 64, "MutableArray.resetOracleElement( n = " + n + ")");
        }

        if (this.datums != null) {
            this.datums[(int) n] = null;
        }

        this.pickledCorrect = false;
    }

    Object[] getLazyArray() throws SQLException {
        if (this.length == -1) {
            getLazyOracleArray();
        }
        if (this.elements == null) {
            this.elements = new Object[this.length];
        }

        return this.elements;
    }

    Datum[] getLazyOracleArray() throws SQLException {
        if (TRACE) {
            OracleLog.print(this, 2048, 2, 64, "MutableArray.getLazyOracleArray()");
        }

        if (this.datums == null) {
            if (this.pickled != null) {
                this.datums = ((Datum[]) this.pickled.getOracleArray());
                this.length = this.datums.length;
                this.pickledCorrect = true;

                if (this.elements != null) {
                    for (int x = 0; x < this.length; x++) {
                        if (this.elements[x] == null)
                            continue;
                        this.datums[x] = null;
                        this.pickledCorrect = false;
                    }

                }

            } else if (this.length >= 0) {
                this.datums = new Datum[this.length];
            }

        }

        if (TRACE) {
            OracleLog.print(this, 2048, 2, 64, "MutableArray.getLazyOracleArray:return "
                    + this.datums);
        }

        return this.datums;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jpub.runtime.MutableArray"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jpub.runtime.MutableArray JD-Core Version: 0.6.0
 */