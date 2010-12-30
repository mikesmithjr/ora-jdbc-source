package oracle.jdbc.driver;

class OracleDateBinder extends DatumBinder {
    Binder theDateCopyingBinder = OraclePreparedStatementReadOnly.theStaticDateCopyingBinder;

    static void init(Binder x) {
        x.type = 12;
        x.bytelen = 7;
    }

    OracleDateBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theDateCopyingBinder;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleDateBinder JD-Core Version: 0.6.0
 */