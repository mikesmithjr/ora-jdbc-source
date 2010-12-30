package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CPlsqlIndexTableAccessor extends PlsqlIndexTableAccessor {
    T4CMAREngine mare;
    final int[] meta = new int[1];
    final int[] tmp = new int[1];

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CPlsqlIndexTableAccessor(OracleStatement stmt, int elemSqlType, int elemInternalType,
            int elemMaxLen, int maxNumOfElements, short form, boolean forBind, T4CMAREngine _mare)
            throws SQLException {
        super(stmt, elemSqlType, elemInternalType, elemMaxLen, maxNumOfElements, form, forBind);

        calculateSizeTmpByteArray();

        this.mare = _mare;
    }

    void processIndicator(int size) throws IOException, SQLException {
        if (((this.internalType == 1) && (this.describeType == 112))
                || ((this.internalType == 23) && (this.describeType == 113))) {
            this.mare.unmarshalUB2();
            this.mare.unmarshalUB2();
        } else if (this.mare.versionNumber < 9200) {
            this.mare.unmarshalSB2();

            if ((this.statement.sqlKind != 1) && (this.statement.sqlKind != 4)) {
                this.mare.unmarshalSB2();
            }
        } else if ((this.statement.sqlKind == 1) || (this.statement.sqlKind == 4)
                || (this.isDMLReturnedParam)) {
            this.mare.processIndicator(size <= 0, size);
        }
    }

    boolean unmarshalOneRow() throws SQLException, IOException {
        if (this.isUseLess) {
            this.lastRowProcessed += 1;

            return false;
        }

        if (this.rowSpaceIndicator == null) {
            byte[] buff = new byte[16000];

            this.mare.unmarshalCLR(buff, 0, this.meta);
            processIndicator(this.meta[0]);

            this.lastRowProcessed += 1;

            return false;
        }

        int tmpIndicatorOffset = this.indicatorIndex + this.lastRowProcessed;
        int tmpLengthOffset = this.lengthIndex + this.lastRowProcessed;
        byte[] ibtBindBytes = this.statement.ibtBindBytes;
        char[] ibtBindChars = this.statement.ibtBindChars;
        short[] ibtBindIndicators = this.statement.ibtBindIndicators;
        int ibtBindByteOffset = this.statement.ibtBindByteOffset;
        int ibtBindCharOffset = this.statement.ibtBindCharOffset;
        int ibtBindIndicatorOffset = this.statement.ibtBindIndicatorOffset;

        if (this.isNullByDescribe) {
            this.rowSpaceIndicator[tmpIndicatorOffset] = -1;
            this.rowSpaceIndicator[tmpLengthOffset] = 0;
            this.lastRowProcessed += 1;

            if (this.mare.versionNumber < 9200) {
                processIndicator(0);
            }

            return false;
        }

        int nbOfElements = (int) this.mare.unmarshalUB4();

        ibtBindIndicators[(this.ibtMetaIndex + 4)] = (short) (nbOfElements & 0xFFFFFFFF & 0xFFFF);

        ibtBindIndicators[(this.ibtMetaIndex + 5)] = (short) (nbOfElements & 0xFFFF);

        if ((this.elementInternalType == 9) || (this.elementInternalType == 96)
                || (this.elementInternalType == 1)) {
            byte[] bytesBuffer = this.statement.tmpByteArray;

            for (int i = 0; i < nbOfElements; i++) {
                int tmpSpaceCharOffset = this.ibtValueIndex + this.elementMaxLen * i;

                this.mare.unmarshalCLR(bytesBuffer, 0, this.meta);

                this.tmp[0] = this.meta[0];

                int nbOfCharsConverted = this.statement.connection.conversion
                        .CHARBytesToJavaChars(bytesBuffer, 0, ibtBindChars, tmpSpaceCharOffset + 1,
                                              this.tmp, ibtBindChars.length - tmpSpaceCharOffset
                                                      - 1);

                ibtBindChars[tmpSpaceCharOffset] = (char) (nbOfCharsConverted * 2);

                processIndicator(this.meta[0]);

                if (this.meta[0] == 0) {
                    ibtBindIndicators[(this.ibtIndicatorIndex + i)] = -1;
                    ibtBindIndicators[(this.ibtLengthIndex + i)] = 0;
                } else {
                    ibtBindIndicators[(this.ibtLengthIndex + i)] = (short) (this.meta[0] * 2);

                    ibtBindIndicators[(this.ibtIndicatorIndex + i)] = 0;
                }

            }

        } else {
            for (int i = 0; i < nbOfElements; i++) {
                int tmpSpaceByteOffset = this.ibtValueIndex + this.elementMaxLen * i;

                this.mare.unmarshalCLR(ibtBindBytes, tmpSpaceByteOffset + 1, this.meta);

                ibtBindBytes[tmpSpaceByteOffset] = (byte) this.meta[0];

                processIndicator(this.meta[0]);

                if (this.meta[0] == 0) {
                    ibtBindIndicators[(this.ibtIndicatorIndex + i)] = -1;
                    ibtBindIndicators[(this.ibtLengthIndex + i)] = 0;
                } else {
                    ibtBindIndicators[(this.ibtLengthIndex + i)] = (short) this.meta[0];
                    ibtBindIndicators[(this.ibtIndicatorIndex + i)] = 0;
                }
            }
        }

        this.lastRowProcessed += 1;

        return false;
    }

    void calculateSizeTmpByteArray() {
        if ((this.elementInternalType == 9) || (this.elementInternalType == 96)
                || (this.elementInternalType == 1)) {
            int maxNbBytes = this.ibtCharLength * this.statement.connection.conversion.cMaxCharSize
                    / this.maxNumberOfElements;

            if (this.statement.sizeTmpByteArray < maxNbBytes)
                this.statement.sizeTmpByteArray = maxNbBytes;
        }
    }

    String getString(int currentRow) throws SQLException {
        String ret = super.getString(currentRow);

        if ((ret != null) && (this.definedColumnSize > 0)
                && (ret.length() > this.definedColumnSize)) {
            ret = ret.substring(0, this.definedColumnSize);
        }
        return ret;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CPlsqlIndexTableAccessor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CPlsqlIndexTableAccessor JD-Core Version: 0.6.0
 */