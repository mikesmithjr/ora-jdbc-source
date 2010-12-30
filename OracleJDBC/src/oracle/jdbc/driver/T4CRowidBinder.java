package oracle.jdbc.driver;

class T4CRowidBinder extends RowidBinder {
    static void init(Binder x) {
        x.type = 104;
        x.bytelen = 130;
    }

    T4CRowidBinder() {
        this.theRowidCopyingBinder = OraclePreparedStatementReadOnly.theStaticT4CRowidCopyingBinder;

        init(this);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CRowidBinder JD-Core Version: 0.6.0
 */