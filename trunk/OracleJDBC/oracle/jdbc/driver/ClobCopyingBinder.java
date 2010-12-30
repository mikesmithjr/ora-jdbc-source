package oracle.jdbc.driver;

class ClobCopyingBinder extends ByteCopyingBinder
{
  ClobCopyingBinder()
  {
    ClobBinder.init(this);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.ClobCopyingBinder
 * JD-Core Version:    0.6.0
 */