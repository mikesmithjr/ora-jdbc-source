package oracle.jdbc.driver;

class BINARY_FLOATBinder extends DatumBinder {
    Binder theBINARY_FLOATCopyingBinder = OraclePreparedStatementReadOnly.theStaticBINARY_FLOATCopyingBinder;

    static void init(Binder x) {
        x.type = 100;
        x.bytelen = 4;
    }

    BINARY_FLOATBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theBINARY_FLOATCopyingBinder;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.BINARY_FLOATBinder JD-Core Version: 0.6.0
 */