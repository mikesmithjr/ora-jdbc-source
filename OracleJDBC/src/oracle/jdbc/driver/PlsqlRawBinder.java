package oracle.jdbc.driver;

class PlsqlRawBinder extends DatumBinder {
    Binder thePlsqlRawCopyingBinder = OraclePreparedStatementReadOnly.theStaticPlsqlRawCopyingBinder;

    static void init(Binder x) {
        x.type = 23;
        x.bytelen = 32767;
    }

    PlsqlRawBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.thePlsqlRawCopyingBinder;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.PlsqlRawBinder JD-Core Version: 0.6.0
 */