package oracle.jdbc.driver;

class DateCopyingBinder extends ByteCopyingBinder {
    DateCopyingBinder() {
        DateBinder.init(this);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.DateCopyingBinder JD-Core Version: 0.6.0
 */