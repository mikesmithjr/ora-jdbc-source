package oracle.jdbc.oracore;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class TDSPatch {
    static final int S_NORMAL_PATCH = 0;
    static final int S_SIMPLE_PATCH = 1;
    int typeId;
    OracleType owner;
    long position;
    int uptCode;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    public TDSPatch(int type, OracleType owner, long pos, int uptCode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "TDSPatch (type = " + type + ", owner = " + owner
                    + ", pos = " + pos + ", uptCode = " + uptCode + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.typeId = type;
        this.owner = owner;
        this.position = pos;
        this.uptCode = uptCode;
    }

    int getType() throws SQLException {
        return this.typeId;
    }

    OracleNamedType getOwner() throws SQLException {
        return (OracleNamedType) this.owner;
    }

    long getPosition() throws SQLException {
        return this.position;
    }

    byte getUptTypeCode() throws SQLException {
        return (byte) this.uptCode;
    }

    void apply(OracleType typeValue) throws SQLException {
        apply(typeValue, -1);
    }

    void apply(OracleType typeValue, int opcode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "TDSPatch.apply(typeValue = " + typeValue
                    + ", opcode = " + opcode + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.typeId == 0) {
            OracleTypeUPT patchElem = (OracleTypeUPT) this.owner;

            patchElem.realType = ((OracleTypeADT) typeValue);

            if ((typeValue instanceof OracleNamedType)) {
                OracleNamedType namedType = (OracleNamedType) typeValue;

                namedType.setParent(patchElem.getParent());
                namedType.setOrder(patchElem.getOrder());
            }
        } else if (this.typeId == 1) {
            OracleTypeCOLLECTION patchElem = (OracleTypeCOLLECTION) this.owner;

            patchElem.opcode = opcode;
            patchElem.elementType = typeValue;

            if ((typeValue instanceof OracleNamedType)) {
                OracleNamedType namedType = (OracleNamedType) typeValue;

                namedType.setParent(patchElem);
                namedType.setOrder(1);
            }
        } else {
            DatabaseError.throwSqlException(1);
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.TDSPatch"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.TDSPatch JD-Core Version: 0.6.0
 */