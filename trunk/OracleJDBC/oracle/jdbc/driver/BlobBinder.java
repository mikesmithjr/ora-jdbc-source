package oracle.jdbc.driver;

class BlobBinder extends DatumBinder
{
  Binder theBlobCopyingBinder = OraclePreparedStatementReadOnly.theStaticBlobCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 113;
    paramBinder.bytelen = 4000;
  }

  BlobBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theBlobCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.BlobBinder
 * JD-Core Version:    0.6.0
 */