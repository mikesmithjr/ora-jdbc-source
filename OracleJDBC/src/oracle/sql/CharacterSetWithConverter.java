package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverterFactory;
import oracle.sql.converter.CharacterConverterFactoryJDBC;
import oracle.sql.converter.CharacterConverters;

public abstract class CharacterSetWithConverter extends CharacterSet {
    public static CharacterConverterFactory ccFactory = new CharacterConverterFactoryJDBC();
    CharacterConverters m_converter;

    CharacterSetWithConverter(int oracleId, CharacterConverters charConverter) {
        super(oracleId);

        this.m_converter = charConverter;
    }

    static CharacterSet getInstance(int oracleId) {
        CharacterConverters charConverter = ccFactory.make(oracleId);

        if (charConverter == null) {
            return null;
        }

        CharacterSet charSetInstance = null;

        if ((charSetInstance = CharacterSet1Byte.getInstance(oracleId, charConverter)) != null) {
            return charSetInstance;
        }

        if ((charSetInstance = CharacterSetSJIS.getInstance(oracleId, charConverter)) != null) {
            return charSetInstance;
        }

        if ((charSetInstance = CharacterSetShift.getInstance(oracleId, charConverter)) != null) {
            return charSetInstance;
        }

        if ((charSetInstance = CharacterSet2ByteFixed.getInstance(oracleId, charConverter)) != null) {
            return charSetInstance;
        }

        if ((charSetInstance = CharacterSetGB18030.getInstance(oracleId, charConverter)) != null) {
            return charSetInstance;
        }

        if ((charSetInstance = CharacterSet12Byte.getInstance(oracleId, charConverter)) != null) {
            return charSetInstance;
        }

        if ((charSetInstance = CharacterSetJAEUC.getInstance(oracleId, charConverter)) != null) {
            return charSetInstance;
        }

        if ((charSetInstance = CharacterSetZHTEUC.getInstance(oracleId, charConverter)) != null) {
            return charSetInstance;
        }

        return CharacterSetLCFixed.getInstance(oracleId, charConverter);
    }

    public boolean isLossyFrom(CharacterSet from) {
        return from.getOracleId() != getOracleId();
    }

    public boolean isConvertibleFrom(CharacterSet source) {
        return source.getOracleId() == getOracleId();
    }

    public String toStringWithReplacement(byte[] bytes, int offset, int count) {
        return this.m_converter.toUnicodeStringWithReplacement(bytes, offset, count);
    }

    public String toString(byte[] bytes, int offset, int count) throws SQLException {
        return this.m_converter.toUnicodeString(bytes, offset, count);
    }

    public byte[] convert(String s) throws SQLException {
        return this.m_converter.toOracleString(s);
    }

    public byte[] convertWithReplacement(String s) {
        return this.m_converter.toOracleStringWithReplacement(s);
    }

    public byte[] convert(CharacterSet from, byte[] source, int offset, int count)
            throws SQLException {
        if (from.getOracleId() == getOracleId()) {
            return useOrCopy(source, offset, count);
        }

        return convert(from.toString(source, offset, count));
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetWithConverter JD-Core Version: 0.6.0
 */