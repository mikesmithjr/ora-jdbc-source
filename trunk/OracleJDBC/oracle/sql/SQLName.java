package oracle.sql;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.driver.DatabaseError;

public class SQLName
  implements Serializable
{
  static boolean DEBUG = false;
  static boolean s_parseAllFormat = false;
  static final long serialVersionUID = 2266340348729491526L;
  String name;
  String schema;
  String simple;
  int version;
  boolean synonym;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:47_PDT_2005";

  protected SQLName()
  {
  }

  public SQLName(String paramString, OracleConnection paramOracleConnection)
    throws SQLException
  {
    init(paramString, paramOracleConnection);

    this.version = 2;
    this.synonym = false;
  }

  public SQLName(String paramString1, String paramString2, OracleConnection paramOracleConnection)
    throws SQLException
  {
    this.schema = paramString1;
    this.simple = paramString2;
    this.name = (this.schema + "." + this.simple);

    this.version = 2;
    this.synonym = false;
  }

  private void init(String paramString, OracleConnection paramOracleConnection)
    throws SQLException
  {
    String[] arrayOfString1 = new String[1];
    String[] arrayOfString2 = new String[1];

    if (parse(paramString, arrayOfString1, arrayOfString2, true))
    {
      this.schema = arrayOfString1[0];
      this.simple = arrayOfString2[0];
    }
    else
    {
      this.schema = paramOracleConnection.getUserName();
      this.simple = arrayOfString2[0];
    }

    this.name = (this.schema + "." + this.simple);
  }

  public String getName()
    throws SQLException
  {
    return this.name;
  }

  public String getSchema()
    throws SQLException
  {
    return this.schema;
  }

  public String getSimpleName()
    throws SQLException
  {
    return this.simple;
  }

  public int getVersion()
    throws SQLException
  {
    return this.version;
  }

  public static boolean parse(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2)
    throws SQLException
  {
    return parse(paramString, paramArrayOfString1, paramArrayOfString2, s_parseAllFormat);
  }

  public static boolean parse(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean)
    throws SQLException
  {
    if (paramString == null) {
      return false;
    }
    if ((paramArrayOfString1 == null) || (paramArrayOfString1.length < 1) || (paramArrayOfString2 == null) || (paramArrayOfString2.length < 1))
    {
      DatabaseError.throwSqlException(68);
    }

    if (!paramBoolean)
    {
      i = paramString.indexOf(".");

      if (i < 0)
      {
        paramArrayOfString2[0] = paramString;

        return false;
      }

      paramArrayOfString1[0] = paramString.substring(0, i);
      paramArrayOfString2[0] = paramString.substring(i + 1);

      return true;
    }

    int i = paramString.length();
    int j = paramString.indexOf("\"");
    int k = paramString.indexOf("\"", j + 1);
    int m = -1;

    if (j < 0)
    {
      m = paramString.indexOf(".");

      if (m < 0)
      {
        paramArrayOfString2[0] = paramString;

        return false;
      }

      paramArrayOfString1[0] = paramString.substring(0, m);
      paramArrayOfString2[0] = paramString.substring(m + 1);

      return true;
    }

    if (j == 0)
    {
      if (k == i - 1)
      {
        paramArrayOfString2[0] = paramString.substring(j + 1, k);

        return false;
      }

      m = paramString.indexOf(".", k);
      paramArrayOfString1[0] = paramString.substring(j + 1, k);

      j = paramString.indexOf("\"", m);
      k = paramString.indexOf("\"", j + 1);

      if (j < 0)
      {
        paramArrayOfString2[0] = paramString.substring(m + 1);

        return true;
      }

      paramArrayOfString2[0] = paramString.substring(j + 1, k);

      return true;
    }

    m = paramString.indexOf(".");
    paramArrayOfString1[0] = paramString.substring(0, m);
    paramArrayOfString2[0] = paramString.substring(j + 1, k);

    return true;
  }

  public static void setHandleDoubleQuote(boolean paramBoolean)
    throws SQLException
  {
    s_parseAllFormat = paramBoolean;
  }

  public static boolean getHandleDoubleQuote()
    throws SQLException
  {
    return s_parseAllFormat;
  }

  public boolean equals(Object paramObject)
  {
    if (paramObject == this) return true;
    if (!(paramObject instanceof SQLName)) return false;
    return ((SQLName)paramObject).name.equals(this.name);
  }
  public int hashCode() {
    return this.name.hashCode();
  }
  public String toString() {
    return this.name;
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeUTF(this.name);
    paramObjectOutputStream.writeUTF(this.schema);
    paramObjectOutputStream.writeUTF(this.simple);
    paramObjectOutputStream.writeInt(this.version);
    paramObjectOutputStream.writeBoolean(this.synonym);
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.name = paramObjectInputStream.readUTF();
    this.schema = paramObjectInputStream.readUTF();
    this.simple = paramObjectInputStream.readUTF();
    this.version = paramObjectInputStream.readInt();
    this.synonym = paramObjectInputStream.readBoolean();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.SQLName
 * JD-Core Version:    0.6.0
 */