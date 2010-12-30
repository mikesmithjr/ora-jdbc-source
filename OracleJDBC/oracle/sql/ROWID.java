package oracle.sql;

import java.sql.SQLException;

public class ROWID extends Datum
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:47_PDT_2005";

  public ROWID()
  {
  }

  public ROWID(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }

  public Object toJdbc()
    throws SQLException
  {
    return this;
  }

  public boolean isConvertibleTo(Class paramClass)
  {
    String str = paramClass.getName();

    return str.compareTo("java.lang.String") == 0;
  }

  public String stringValue()
  {
    byte[] arrayOfByte = null;

    arrayOfByte = getBytes();

    String str = new String(arrayOfByte, 0, 0, arrayOfByte.length);

    return str;
  }

  public Object makeJdbcArray(int paramInt)
  {
    return new byte[paramInt][];
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.ROWID
 * JD-Core Version:    0.6.0
 */