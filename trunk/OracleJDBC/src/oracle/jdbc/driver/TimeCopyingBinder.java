package oracle.jdbc.driver;

class TimeCopyingBinder extends ByteCopyingBinder {
    TimeCopyingBinder() {
        TimeBinder.init(this);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.TimeCopyingBinder JD-Core Version: 0.6.0
 */