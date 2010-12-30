package oracle.jdbc.driver;

import java.math.BigDecimal;
import java.sql.SQLException;
import oracle.sql.CHAR;
import oracle.sql.CharacterSet;
import oracle.sql.Datum;
import oracle.sql.NUMBER;

class PlsqlIndexTableAccessor extends Accessor {
    int elementInternalType;
    int maxNumberOfElements;
    int elementMaxLen;
    int ibtValueIndex;
    int ibtIndicatorIndex;
    int ibtLengthIndex;
    int ibtMetaIndex;
    int ibtByteLength;
    int ibtCharLength;

    PlsqlIndexTableAccessor(OracleStatement stmt, int elemSqlType, int elemInternalType,
            int elemMaxLen, int maxNumOfElements, short form, boolean forBind) throws SQLException {
        init(stmt, 998, 998, form, forBind);

        this.elementInternalType = elemInternalType;
        this.maxNumberOfElements = maxNumOfElements;
        this.elementMaxLen = elemMaxLen;

        initForDataAccess(elemSqlType, elemMaxLen, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        switch (this.elementInternalType) {
        case 1:
        case 96:
            this.internalTypeMaxLength = 2000;

            this.elementMaxLen = ((max_len == 0 ? this.internalTypeMaxLength : max_len) + 1);
            this.ibtCharLength = (this.elementMaxLen * this.maxNumberOfElements);
            this.elementInternalType = 9;

            break;
        case 6:
            this.internalTypeMaxLength = 21;
            this.elementMaxLen = (this.internalTypeMaxLength + 1);
            this.ibtByteLength = (this.elementMaxLen * this.maxNumberOfElements);

            break;
        default:
            DatabaseError.throwSqlException(97);
        }
    }

    Object[] getPlsqlIndexTable(int currentRow) throws SQLException {
        Object[] result = null;
        short[] ibtBindIndicators = this.statement.ibtBindIndicators;
        int actualElements = (ibtBindIndicators[(this.ibtMetaIndex + 4)] >> 16)
                + (ibtBindIndicators[(this.ibtMetaIndex + 5)] & 0xFFFF);

        int offset = this.ibtValueIndex;

        switch (this.elementInternalType) {
        case 9:
            result = new String[actualElements];
            char[] ibtBindChars = this.statement.ibtBindChars;

            for (int i = 0; i < actualElements; i++) {
                if (ibtBindIndicators[(this.ibtIndicatorIndex + i)] == -1) {
                    result[i] = null;
                } else {
                    result[i] = new String(ibtBindChars, offset + 1, ibtBindChars[offset] >> '\001');
                }

                offset += this.elementMaxLen;
            }

            break;
        case 6:
            result = new BigDecimal[actualElements];
            byte[] ibtBindBytes = this.statement.ibtBindBytes;

            for (int i = 0; i < actualElements; i++) {
                if (ibtBindIndicators[(this.ibtIndicatorIndex + i)] == -1) {
                    result[i] = null;
                } else {
                    int len = ibtBindBytes[offset];
                    byte[] val = new byte[len];

                    System.arraycopy(ibtBindBytes, offset + 1, val, 0, len);

                    result[i] = oracle.sql.NUMBER.toBigDecimal(val);
                }

                offset += this.elementMaxLen;
            }

            break;
        default:
            DatabaseError.throwSqlException(97);
        }

        return result;
    }

    Datum[] getOraclePlsqlIndexTable(int currentRow) throws SQLException {
        Datum[] result = null;
        short[] ibtBindIndicators = this.statement.ibtBindIndicators;
        int actualElements = (ibtBindIndicators[(this.ibtMetaIndex + 4)] >> 16)
                + (ibtBindIndicators[(this.ibtMetaIndex + 5)] & 0xFFFF);

        int offset = this.ibtValueIndex;

        switch (this.elementInternalType) {
        case 9:
            result = new CHAR[actualElements];

            CharacterSet charset = CharacterSet.make(2000);
            char[] ibtBindChars = this.statement.ibtBindChars;

            for (int i = 0; i < actualElements; i++) {
                if (ibtBindIndicators[(this.ibtIndicatorIndex + i)] == -1) {
                    result[i] = null;
                } else {
                    int len = ibtBindChars[offset];
                    byte[] b = new byte[len];

                    DBConversion.javaCharsToUcs2Bytes(ibtBindChars, offset + 1, b, 0, len >> 1);

                    result[i] = new CHAR(b, charset);
                }

                offset += this.elementMaxLen;
            }

            break;
        case 6:
            result = new NUMBER[actualElements];
            byte[] ibtBindBytes = this.statement.ibtBindBytes;

            for (int i = 0; i < actualElements; i++) {
                if (ibtBindIndicators[(this.ibtIndicatorIndex + i)] == -1) {
                    result[i] = null;
                } else {
                    int len = ibtBindBytes[offset];
                    byte[] val = new byte[len];

                    System.arraycopy(ibtBindBytes, offset + 1, val, 0, len);

                    result[i] = new NUMBER(val);
                }

                offset += this.elementMaxLen;
            }

            break;
        default:
            DatabaseError.throwSqlException(97);
        }

        return result;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.PlsqlIndexTableAccessor JD-Core Version: 0.6.0
 */