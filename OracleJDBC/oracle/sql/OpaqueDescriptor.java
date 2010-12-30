package oracle.sql;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.oracore.OracleNamedType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeOPAQUE;

public class OpaqueDescriptor extends TypeDescriptor
  implements Serializable
{
  static final boolean DEBUG = false;
  static final long serialVersionUID = 1013921343538311063L;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:47_PDT_2005";

  public static OpaqueDescriptor createDescriptor(String paramString, Connection paramConnection)
    throws SQLException
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramConnection == null))
    {
      DatabaseError.throwSqlException(60, "Invalid arguments");
    }

    SQLName localSQLName = new SQLName(paramString, (oracle.jdbc.OracleConnection)paramConnection);
    return createDescriptor(localSQLName, paramConnection);
  }

  public static OpaqueDescriptor createDescriptor(SQLName paramSQLName, Connection paramConnection)
    throws SQLException
  {
    String str = paramSQLName.getName();

    OpaqueDescriptor localOpaqueDescriptor = (OpaqueDescriptor)((oracle.jdbc.OracleConnection)paramConnection).getDescriptor(str);

    if (localOpaqueDescriptor == null)
    {
      localOpaqueDescriptor = new OpaqueDescriptor(paramSQLName, paramConnection);

      ((oracle.jdbc.OracleConnection)paramConnection).putDescriptor(str, localOpaqueDescriptor);
    }

    return localOpaqueDescriptor;
  }

  public OpaqueDescriptor(String paramString, Connection paramConnection)
    throws SQLException
  {
    super(paramString, paramConnection);

    initPickler();
  }

  public OpaqueDescriptor(SQLName paramSQLName, Connection paramConnection)
    throws SQLException
  {
    super(paramSQLName, paramConnection);

    initPickler();
  }

  public OpaqueDescriptor(SQLName paramSQLName, OracleTypeOPAQUE paramOracleTypeOPAQUE, Connection paramConnection)
    throws SQLException
  {
    super(paramSQLName, paramOracleTypeOPAQUE, paramConnection);
  }

  private void initPickler()
    throws SQLException
  {
    try
    {
      this.pickler = new OracleTypeADT(getName(), this.connection);

      ((OracleTypeADT)this.pickler).init(this.connection);

      this.pickler = ((OracleTypeOPAQUE)((OracleTypeADT)this.pickler).cleanup());

      this.pickler.setDescriptor(this);
    }
    catch (Exception localException)
    {
      if ((localException instanceof SQLException)) {
        throw ((SQLException)localException);
      }
      DatabaseError.throwSqlException(60, "Unable to resolve type \"" + getName() + "\"");
    }
  }

  public OpaqueDescriptor(OracleTypeADT paramOracleTypeADT, Connection paramConnection)
    throws SQLException
  {
    super(paramOracleTypeADT, paramConnection);
  }

  byte[] toBytes(OPAQUE paramOPAQUE, boolean paramBoolean)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    if (paramOPAQUE.shareBytes() != null)
    {
      arrayOfByte = paramOPAQUE.shareBytes();
    }
    else
    {
      try
      {
        arrayOfByte = this.pickler.linearize(paramOPAQUE);
      }
      finally
      {
        if (!paramBoolean) {
          paramOPAQUE.setShareBytes(null);
        }
      }

    }

    return arrayOfByte;
  }

  byte[] toValue(OPAQUE paramOPAQUE, boolean paramBoolean)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    if (paramOPAQUE.value != null)
    {
      arrayOfByte = paramOPAQUE.value;
    }
    else
    {
      try
      {
        this.pickler.unlinearize(paramOPAQUE.shareBytes(), 0L, paramOPAQUE, 1, null);

        arrayOfByte = paramOPAQUE.value;
      }
      finally
      {
        if (!paramBoolean) {
          paramOPAQUE.value = null;
        }
      }

    }

    return arrayOfByte;
  }

  public int getTypeCode()
  {
    return 2007;
  }

  public long getMaxLength()
    throws SQLException
  {
    long l = hasUnboundedSize() ? 0L : ((OracleTypeOPAQUE)this.pickler).getMaxLength();

    return l;
  }

  public boolean isTrustedLibrary()
    throws SQLException
  {
    return ((OracleTypeOPAQUE)this.pickler).isTrustedLibrary();
  }

  public boolean isModeledInC()
    throws SQLException
  {
    return ((OracleTypeOPAQUE)this.pickler).isModeledInC();
  }

  public boolean hasUnboundedSize()
    throws SQLException
  {
    return ((OracleTypeOPAQUE)this.pickler).isUnboundedSized();
  }

  public boolean hasFixedSize()
    throws SQLException
  {
    return ((OracleTypeOPAQUE)this.pickler).isFixedSized();
  }

  public String descType()
    throws SQLException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    String str = descType(localStringBuffer, 0);

    return str;
  }

  String descType(StringBuffer paramStringBuffer, int paramInt)
    throws SQLException
  {
    String str1 = "";

    for (int i = 0; i < paramInt; i++) {
      str1 = str1 + "  ";
    }
    String str2 = str1 + "  ";

    paramStringBuffer.append(str1);
    paramStringBuffer.append(getTypeName());
    paramStringBuffer.append(" maxLen=" + getMaxLength() + " isTrusted=" + isTrustedLibrary() + " hasUnboundedSize=" + hasUnboundedSize() + " hasFixedSize=" + hasFixedSize());

    paramStringBuffer.append("\n");

    String str3 = paramStringBuffer.toString();

    return str3;
  }

  public Class getClass(Map paramMap)
    throws SQLException
  {
    Object localObject = null;

    String str1 = getName();

    Class localClass = (Class)paramMap.get(str1);

    String str2 = getSchemaName();
    String str3 = getTypeName();

    if (localClass == null)
    {
      if (this.connection.getUserName().equals(str2)) {
        localClass = (Class)paramMap.get(str3);
      }
    }
    if (!SQLName.s_parseAllFormat)
    {
      localObject = localClass;
    }
    else
    {
      if (localClass == null)
      {
        if (this.connection.getUserName().equals(str2)) {
          localClass = (Class)paramMap.get("\"" + str3 + "\"");
        }
      }
      if (localClass == null)
      {
        localClass = (Class)paramMap.get("\"" + str2 + "\"" + "." + "\"" + str3 + "\"");
      }

      if (localClass == null)
      {
        localClass = (Class)paramMap.get("\"" + str2 + "\"" + "." + str3);
      }

      if (localClass == null)
      {
        localClass = (Class)paramMap.get(str2 + "." + "\"" + str3 + "\"");
      }

      localObject = localClass;
    }

    return localObject;
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.OpaqueDescriptor
 * JD-Core Version:    0.6.0
 */