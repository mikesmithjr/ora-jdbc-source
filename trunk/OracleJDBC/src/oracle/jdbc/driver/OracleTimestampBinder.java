package oracle.jdbc.driver;

class OracleTimestampBinder extends DatumBinder {
    Binder theTimestampCopyingBinder = OraclePreparedStatementReadOnly.theStaticTimestampCopyingBinder;

    static void init(Binder x) {
        x.type = 180;
        x.bytelen = 11;
    }

    OracleTimestampBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theTimestampCopyingBinder;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleTimestampBinder JD-Core Version: 0.6.0
 */