package oracle.jdbc.driver;

class TSTZBinder extends DatumBinder {
    Binder theTSTZCopyingBinder = OraclePreparedStatementReadOnly.theStaticTSTZCopyingBinder;

    static void init(Binder x) {
        x.type = 181;
        x.bytelen = 13;
    }

    TSTZBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theTSTZCopyingBinder;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.TSTZBinder JD-Core Version: 0.6.0
 */