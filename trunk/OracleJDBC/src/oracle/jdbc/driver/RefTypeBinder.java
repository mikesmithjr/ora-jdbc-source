package oracle.jdbc.driver;

class RefTypeBinder extends TypeBinder {
    Binder theRefTypeCopyingBinder = OraclePreparedStatementReadOnly.theStaticRefTypeCopyingBinder;

    RefTypeBinder() {
        init(this);
    }

    static void init(Binder x) {
        x.type = 111;
        x.bytelen = 24;
    }

    Binder copyingBinder() {
        return this.theRefTypeCopyingBinder;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.RefTypeBinder JD-Core Version: 0.6.0
 */