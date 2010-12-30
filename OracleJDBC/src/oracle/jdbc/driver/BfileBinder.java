package oracle.jdbc.driver;

class BfileBinder extends DatumBinder {
    Binder theBfileCopyingBinder = OraclePreparedStatementReadOnly.theStaticBfileCopyingBinder;

    static void init(Binder x) {
        x.type = 114;
        x.bytelen = 530;
    }

    BfileBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theBfileCopyingBinder;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.BfileBinder JD-Core Version: 0.6.0
 */