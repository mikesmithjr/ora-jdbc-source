package oracle.jdbc.driver;

import java.sql.SQLException;

class PlsqlIbtBindInfo {
    Object[] arrayData;
    int maxLen;
    int curLen;
    int element_internal_type;
    int elemMaxLen;
    int ibtByteLength;
    int ibtCharLength;
    int ibtValueIndex;
    int ibtIndicatorIndex;
    int ibtLengthIndex;

    PlsqlIbtBindInfo(Object[] arrayData, int maxLen, int curLen, int element_internal_type,
            int elemMaxLen) throws SQLException {
        this.arrayData = arrayData;
        this.maxLen = maxLen;
        this.curLen = curLen;
        this.element_internal_type = element_internal_type;

        switch (element_internal_type) {
        case 1:
        case 96:
            this.elemMaxLen = (elemMaxLen == 0 ? 2 : elemMaxLen + 1);

            this.ibtCharLength = (this.elemMaxLen * maxLen);
            this.element_internal_type = 9;

            break;
        case 6:
            this.elemMaxLen = 22;
            this.ibtByteLength = (this.elemMaxLen * maxLen);

            break;
        default:
            DatabaseError.throwSqlException(97);
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.PlsqlIbtBindInfo JD-Core Version: 0.6.0
 */