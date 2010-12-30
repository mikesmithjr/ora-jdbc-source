// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: T4CTTIdcb.java

package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.jdbc.oracore.OracleTypeADT;

// Referenced classes of package oracle.jdbc.driver:
// T4CTTIMsg, T4C8TTIuds, Accessor, T4CCharAccessor,
// T4CNumberAccessor, T4CVarcharAccessor, T4CLongAccessor, T4CVarnumAccessor,
// T4CBinaryFloatAccessor, T4CBinaryDoubleAccessor, T4CRawAccessor, T4CLongRawAccessor,
// T4CRowidAccessor, T4CResultSetAccessor, T4CDateAccessor, T4CBlobAccessor,
// T4CClobAccessor, T4CBfileAccessor, T4CNamedTypeAccessor, T4CRefTypeAccessor,
// T4CTimestampAccessor, T4CTimestamptzAccessor, T4CTimestampltzAccessor, T4CIntervalymAccessor,
// T4CIntervaldsAccessor, T4CMAREngine, DBConversion, OracleStatement,
// PhysicalConnection, T4CTTIoac, OracleLog

class T4CTTIdcb extends T4CTTIMsg {

    T4C8TTIuds uds[];
    int numuds;
    String colnames[];
    int colOffset;
    byte ignoreBuff[];
    StringBuffer colNameSB;
    OracleStatement statement;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CTTIdcb(T4CMAREngine _mrengine) throws IOException, SQLException {
        super((byte) 16);
        colNameSB = null;
        statement = null;
        setMarshalingEngine(_mrengine);
        ignoreBuff = new byte[40];
    }

    void init(OracleStatement stmt, int _offset) {
        statement = stmt;
        colOffset = _offset;
    }

    Accessor[] receive(Accessor accessors[]) throws SQLException, IOException {
        int length = meg.unmarshalUB1();
        if (ignoreBuff.length < length) {
            ignoreBuff = new byte[length];
        }
        meg.unmarshalNBytes(ignoreBuff, 0, length);
        int maxSizeOfOneRow = (int) meg.unmarshalUB4();
        accessors = receiveCommon(accessors, false);
        return accessors;
    }

    Accessor[] receiveFromRefCursor(Accessor accessors[]) throws SQLException, IOException {
        int ignore1 = meg.unmarshalUB1();
        int ignore2 = (int) meg.unmarshalUB4();
        accessors = receiveCommon(accessors, false);
        return accessors;
    }

    Accessor[] receiveCommon(Accessor accessors[], boolean fromOdny) throws SQLException,
            IOException {
        if (fromOdny) {
            numuds = meg.unmarshalUB2();
        } else {
            numuds = (int) meg.unmarshalUB4();
            int ignore;
            if (numuds > 0) {
                ignore = meg.unmarshalUB1();
            }
        }
        uds = new T4C8TTIuds[numuds];
        colnames = new String[numuds];
        for (int i = 0; i < numuds; i++) {
            uds[i] = new T4C8TTIuds(meg);
            uds[i].unmarshal();
            int nbColumn;
            if (meg.versionNumber >= 10000) {
                nbColumn = meg.unmarshalUB2();
            }
            colnames[i] = meg.conv.CharBytesToString(uds[i].getColumName(), uds[i]
                    .getColumNameByteLength());
        }

        if (!fromOdny) {
            meg.unmarshalDALC();
            if (meg.versionNumber >= 10000) {
                int dcbflg = (int) meg.unmarshalUB4();
                int dcbmdbz = (int) meg.unmarshalUB4();
            }
        }
        if (statement.needToPrepareDefineBuffer) {
            if (accessors == null || accessors.length != numuds + colOffset) {
                Accessor newAccessors[] = new Accessor[numuds + colOffset];
                if (accessors != null && accessors.length == colOffset) {
                    System.arraycopy(accessors, 0, newAccessors, 0, colOffset);
                }
                accessors = newAccessors;
            }
            fillupAccessors(accessors, colOffset);
            if (!fromOdny) {
                statement.describedWithNames = true;
                statement.described = true;
                statement.numberOfDefinePositions = numuds;
                statement.accessors = accessors;
                if (statement.connection.useFetchSizeWithLongColumn) {
                    statement.prepareAccessors();
                    statement.allocateTmpByteArray();
                }
            }
        }
        return accessors;
    }

    void fillupAccessors(Accessor accessors[], int column_offset) throws SQLException {
        int definedColumnTypes[] = statement.definedColumnType;
        int definedColumnSizes[] = statement.definedColumnSize;
        int definedColumnFormOfUses[] = statement.definedColumnFormOfUse;
        for (int i = 0; i < numuds; i++) {
            int definedColumnType = 0;
            int definedColumnSize = 0;
            int definedColumnFormOfUse = 0;
            if (definedColumnTypes != null && definedColumnTypes.length > column_offset + i
                    && definedColumnTypes[column_offset + i] != 0) {
                definedColumnType = definedColumnTypes[column_offset + i];
            }
            if (definedColumnSizes != null && definedColumnSizes.length > column_offset + i
                    && definedColumnSizes[column_offset + i] > 0) {
                definedColumnSize = definedColumnSizes[column_offset + i];
            }
            if (definedColumnFormOfUses != null
                    && definedColumnFormOfUses.length > column_offset + i
                    && definedColumnFormOfUses[column_offset + i] > 0) {
                definedColumnFormOfUse = definedColumnFormOfUses[column_offset + i];
            }
            T4C8TTIuds ud = uds[i];
            String sql_name = meg.conv.CharBytesToString(uds[i].getTypeName(), uds[i]
                    .getTypeCharLength());
            String schema_name = meg.conv.CharBytesToString(uds[i].getSchemaName(), uds[i]
                    .getSchemaCharLength());
            String type_name = schema_name + "." + sql_name;
            int max_len = ud.udsoac.oacmxl;
            switch (ud.udsoac.oacdty) {
            case 96: // '`'
            {
                if (ud.udsoac.oacmxlc != 0 && ud.udsoac.oacmxlc < max_len) {
                    max_len = 2 * ud.udsoac.oacmxlc;
                }
                int nbOfCharToAllocate = max_len;
                if ((definedColumnType == 1 || definedColumnType == 12) && definedColumnSize > 0
                        && definedColumnSize < max_len) {
                    nbOfCharToAllocate = definedColumnSize;
                }
                accessors[column_offset + i] = new T4CCharAccessor(statement, nbOfCharToAllocate,
                        ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                        ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse, max_len,
                        definedColumnType, definedColumnSize, meg);
                if ((ud.udsoac.oacfl2 & 0x1000) == 4096 || ud.udsoac.oacmxlc != 0) {
                    accessors[colOffset + i].setDisplaySize(ud.udsoac.oacmxlc);
                }
                break;
            }

            case 2: // '\002'
            {
                accessors[column_offset + i] = new T4CNumberAccessor(statement, max_len,
                        ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                        ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            case 1: // '\001'
            {
                if (ud.udsoac.oacmxlc != 0 && ud.udsoac.oacmxlc < max_len) {
                    max_len = 2 * ud.udsoac.oacmxlc;
                }
                int nbOfCharToAllocate = max_len;
                if ((definedColumnType == 1 || definedColumnType == 12) && definedColumnSize > 0
                        && definedColumnSize < max_len) {
                    nbOfCharToAllocate = definedColumnSize;
                }
                accessors[column_offset + i] = new T4CVarcharAccessor(statement,
                        nbOfCharToAllocate, ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre,
                        ud.udsoac.oacscl, ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse,
                        max_len, definedColumnType, definedColumnSize, meg);
                if ((ud.udsoac.oacfl2 & 0x1000) == 4096 || ud.udsoac.oacmxlc != 0) {
                    accessors[colOffset + i].setDisplaySize(ud.udsoac.oacmxlc);
                }
                break;
            }

            case 8: // '\b'
            {
                if ((definedColumnType == 1 || definedColumnType == 12)
                        && meg.versionNumber >= 9000 && definedColumnSize < 4001) {
                    int nbOfCharToAllocate;
                    if (definedColumnSize > 0) {
                        nbOfCharToAllocate = definedColumnSize;
                    } else {
                        nbOfCharToAllocate = 4000;
                    }
                    max_len = -1;
                    accessors[column_offset + i] = new T4CVarcharAccessor(statement,
                            nbOfCharToAllocate, ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre,
                            ud.udsoac.oacscl, ud.udsoac.oacfl2, ud.udsoac.oacmal,
                            ud.udsoac.formOfUse, max_len, definedColumnType, definedColumnSize, meg);
                    accessors[column_offset + i].describeType = 8;
                } else {
                    max_len = 0;
                    accessors[column_offset + i] = new T4CLongAccessor(statement, column_offset + i
                            + 1, max_len, ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre,
                            ud.udsoac.oacscl, ud.udsoac.oacfl2, ud.udsoac.oacmal,
                            ud.udsoac.formOfUse, definedColumnType, definedColumnSize, meg);
                }
                break;
            }

            case 6: // '\006'
            {
                accessors[column_offset + i] = new T4CVarnumAccessor(statement, max_len,
                        ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                        ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            case 100: // 'd'
            {
                accessors[column_offset + i] = new T4CBinaryFloatAccessor(statement, 4, ud.udsnull,
                        ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl, ud.udsoac.oacfl2,
                        ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            case 101: // 'e'
            {
                accessors[column_offset + i] = new T4CBinaryDoubleAccessor(statement, 8,
                        ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                        ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            case 23: // '\027'
            {
                accessors[column_offset + i] = new T4CRawAccessor(statement, max_len, ud.udsnull,
                        ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl, ud.udsoac.oacfl2,
                        ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            case 24: // '\030'
            {
                if (definedColumnType == -2 && definedColumnSize < 2001
                        && meg.versionNumber >= 9000) {
                    max_len = -1;
                    accessors[column_offset + i] = new T4CRawAccessor(statement, max_len,
                            ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                            ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse,
                            definedColumnType, definedColumnSize, meg);
                    accessors[column_offset + i].describeType = 24;
                } else {
                    accessors[column_offset + i] = new T4CLongRawAccessor(statement, column_offset
                            + i + 1, max_len, ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre,
                            ud.udsoac.oacscl, ud.udsoac.oacfl2, ud.udsoac.oacmal,
                            ud.udsoac.formOfUse, definedColumnType, definedColumnSize, meg);
                }
                break;
            }

            case 104: // 'h'
            case 208: {
                accessors[column_offset + i] = new T4CRowidAccessor(statement, max_len, ud.udsnull,
                        ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl, ud.udsoac.oacfl2,
                        ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                if (ud.udsoac.oacdty == 208) {
                    accessors[i].describeType = ud.udsoac.oacdty;
                }
                break;
            }

            case 102: // 'f'
            {
                accessors[column_offset + i] = new T4CResultSetAccessor(statement, max_len,
                        ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                        ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            case 12: // '\f'
            {
                accessors[column_offset + i] = new T4CDateAccessor(statement, max_len, ud.udsnull,
                        ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl, ud.udsoac.oacfl2,
                        ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            case 113: // 'q'
            {
                if (definedColumnType == -4 && meg.versionNumber >= 9000) {
                    accessors[column_offset + i] = new T4CLongRawAccessor(statement, column_offset
                            + i + 1, 0x7fffffff, ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre,
                            ud.udsoac.oacscl, ud.udsoac.oacfl2, ud.udsoac.oacmal,
                            ud.udsoac.formOfUse, definedColumnType, definedColumnSize, meg);
                    accessors[column_offset + i].describeType = 113;
                } else if (definedColumnType == -3 && meg.versionNumber >= 9000) {
                    accessors[column_offset + i] = new T4CRawAccessor(statement, 4000, ud.udsnull,
                            ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl, ud.udsoac.oacfl2,
                            ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                            definedColumnSize, meg);
                    accessors[column_offset + i].describeType = 113;
                } else {
                    accessors[column_offset + i] = new T4CBlobAccessor(statement, max_len,
                            ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                            ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse,
                            definedColumnType, definedColumnSize, meg);
                }
                break;
            }

            case 112: // 'p'
            {
                short formOfUse = 1;
                if (definedColumnFormOfUse != 0) {
                    formOfUse = (short) definedColumnFormOfUse;
                }
                if (definedColumnType == -1 && meg.versionNumber >= 9000) {
                    max_len = 0;
                    accessors[column_offset + i] = new T4CLongAccessor(statement, column_offset + i
                            + 1, 0x7fffffff, ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre,
                            ud.udsoac.oacscl, ud.udsoac.oacfl2, ud.udsoac.oacmal, formOfUse,
                            definedColumnType, definedColumnSize, meg);
                    accessors[column_offset + i].describeType = 112;
                } else if ((definedColumnType == 12 || definedColumnType == 1)
                        && meg.versionNumber >= 9000) {
                    int nbOfCharToAllocate = 4000;
                    if (definedColumnSize > 0 && definedColumnSize < nbOfCharToAllocate) {
                        nbOfCharToAllocate = definedColumnSize;
                    }
                    accessors[column_offset + i] = new T4CVarcharAccessor(statement,
                            nbOfCharToAllocate, ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre,
                            ud.udsoac.oacscl, ud.udsoac.oacfl2, ud.udsoac.oacmal, formOfUse, 4000,
                            definedColumnType, definedColumnSize, meg);
                    accessors[column_offset + i].describeType = 112;
                } else {
                    accessors[column_offset + i] = new T4CClobAccessor(statement, max_len,
                            ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                            ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse,
                            definedColumnType, definedColumnSize, meg);
                }
                break;
            }

            case 114: // 'r'
            {
                accessors[column_offset + i] = new T4CBfileAccessor(statement, max_len, ud.udsnull,
                        ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl, ud.udsoac.oacfl2,
                        ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            case 109: // 'm'
            {
                accessors[column_offset + i] = new T4CNamedTypeAccessor(statement, max_len,
                        ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                        ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse, type_name,
                        definedColumnType, definedColumnSize, meg);
                break;
            }

            case 111: // 'o'
            {
                accessors[column_offset + i] = new T4CRefTypeAccessor(statement, max_len,
                        ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                        ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse, type_name,
                        definedColumnType, definedColumnSize, meg);
                break;
            }

            case 180: {
                accessors[column_offset + i] = new T4CTimestampAccessor(statement, max_len,
                        ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                        ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            case 181: {
                accessors[column_offset + i] = new T4CTimestamptzAccessor(statement, max_len,
                        ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                        ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            case 231: {
                accessors[column_offset + i] = new T4CTimestampltzAccessor(statement, max_len,
                        ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                        ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            case 182: {
                accessors[column_offset + i] = new T4CIntervalymAccessor(statement, max_len,
                        ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                        ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            case 183: {
                accessors[column_offset + i] = new T4CIntervaldsAccessor(statement, max_len,
                        ud.udsnull, ud.udsoac.oacflg, ud.udsoac.oacpre, ud.udsoac.oacscl,
                        ud.udsoac.oacfl2, ud.udsoac.oacmal, ud.udsoac.formOfUse, definedColumnType,
                        definedColumnSize, meg);
                break;
            }

            default: {
                accessors[column_offset + i] = null;
                break;
            }
            }
            if (ud.udsoac.oactoid.length > 0) {
                accessors[column_offset + i].internalOtype = new OracleTypeADT(ud.udsoac.oactoid,
                        ud.udsoac.oacvsn, ud.udsoac.ncs, ud.udsoac.formOfUse, schema_name + "."
                                + sql_name);
            } else {
                accessors[column_offset + i].internalOtype = null;
            }
            accessors[column_offset + i].columnName = colnames[i];
            if (uds[i].udsoac.oacmxl == 0) {
                accessors[i].isNullByDescribe = true;
            }
        }

        colNameSB = null;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTTIdcb"));
        } catch (Exception e) {
        }
    }
}
