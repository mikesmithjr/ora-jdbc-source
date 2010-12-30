package oracle.jdbc.driver;

abstract class VarcharBinder extends Binder
{
  Binder theVarcharCopyingBinder = OraclePreparedStatementReadOnly.theStaticVarcharCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 9;
    paramBinder.bytelen = 0;
  }

  VarcharBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theVarcharCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.VarcharBinder
 * JD-Core Version:    0.6.0
 */