package oracle.jdbc.driver;

class TimestampCopyingBinder extends ByteCopyingBinder
{
  TimestampCopyingBinder()
  {
    TimestampBinder.init(this);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.TimestampCopyingBinder
 * JD-Core Version:    0.6.0
 */