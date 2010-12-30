package oracle.jdbc.driver;

class RawBinder extends DatumBinder {
    Binder theRawCopyingBinder = OraclePreparedStatementReadOnly.theStaticRawCopyingBinder;

    static void init(Binder x) {
        x.type = 23;
        x.bytelen = 2000;
    }

    RawBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theRawCopyingBinder;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.RawBinder JD-Core Version: 0.6.0
 */