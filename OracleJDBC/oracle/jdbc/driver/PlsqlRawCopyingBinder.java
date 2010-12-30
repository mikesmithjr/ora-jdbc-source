package oracle.jdbc.driver;

class PlsqlRawCopyingBinder extends ByteCopyingBinder
{
  PlsqlRawCopyingBinder()
  {
    PlsqlRawBinder.init(this);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.PlsqlRawCopyingBinder
 * JD-Core Version:    0.6.0
 */