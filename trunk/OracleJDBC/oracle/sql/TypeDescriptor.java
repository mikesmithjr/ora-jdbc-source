package oracle.sql;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.oracore.OracleNamedType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCOLLECTION;
import oracle.jdbc.oracore.OracleTypeOPAQUE;

public abstract class TypeDescriptor
  implements Serializable
{
  public static boolean DEBUG_SERIALIZATION = false;
  static final long serialVersionUID = 2022598722047823723L;
  SQLName sqlName;
  OracleNamedType pickler;
  transient oracle.jdbc.internal.OracleConnection connection;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:48_PDT_2005";

  protected TypeDescriptor()
  {
  }

  protected TypeDescriptor(String paramString, Connection paramConnection)
    throws SQLException
  {
    if ((paramString == null) || (paramConnection == null))
    {
      DatabaseError.throwSqlException(60, "Invalid arguments");
    }

    setPhysicalConnectionOf(paramConnection);

    this.sqlName = new SQLName(paramString, getInternalConnection());
  }

  protected TypeDescriptor(SQLName paramSQLName, Connection paramConnection)
    throws SQLException
  {
    if ((paramSQLName == null) || (paramConnection == null))
    {
      DatabaseError.throwSqlException(60, "Invalid arguments");
    }

    this.sqlName = paramSQLName;

    setPhysicalConnectionOf(paramConnection);
  }

  protected TypeDescriptor(SQLName paramSQLName, OracleTypeADT paramOracleTypeADT, Connection paramConnection)
    throws SQLException
  {
    if ((paramSQLName == null) || (paramOracleTypeADT == null) || (paramConnection == null))
    {
      DatabaseError.throwSqlException(60, "Invalid arguments");
    }

    this.sqlName = paramSQLName;

    setPhysicalConnectionOf(paramConnection);

    this.pickler = paramOracleTypeADT;

    this.pickler.setDescriptor(this);
  }

  protected TypeDescriptor(OracleTypeADT paramOracleTypeADT, Connection paramConnection)
    throws SQLException
  {
    if ((paramOracleTypeADT == null) || (paramConnection == null))
    {
      DatabaseError.throwSqlException(60, "Invalid arguments");
    }

    setPhysicalConnectionOf(paramConnection);

    this.sqlName = null;
    this.pickler = paramOracleTypeADT;

    this.pickler.setDescriptor(this);
  }

  public synchronized String getName()
    throws SQLException
  {
    if (this.sqlName == null) {
      initSQLName();
    }
    String str = this.sqlName.getName();

    return str;
  }

  public synchronized SQLName getSQLName()
    throws SQLException
  {
    if (this.sqlName == null) {
      initSQLName();
    }

    return this.sqlName;
  }

  void initSQLName()
    throws SQLException
  {
    if ((this.pickler == null) || (getInternalConnection() == null))
    {
      DatabaseError.throwSqlException(1);
    }

    this.sqlName = new SQLName(this.pickler.getFullName(), getInternalConnection());
  }

  public String getSchemaName()
    throws SQLException
  {
    String str = getSQLName().getSchema();

    return str;
  }

  public String getTypeName()
    throws SQLException
  {
    String str = getSQLName().getSimpleName();

    return str;
  }

  public OracleNamedType getPickler()
  {
    return this.pickler;
  }

  public oracle.jdbc.internal.OracleConnection getInternalConnection()
  {
    return this.connection;
  }

  public void setPhysicalConnectionOf(Connection paramConnection)
  {
    this.connection = ((oracle.jdbc.OracleConnection)paramConnection).physicalConnectionWithin();
  }

  public abstract int getTypeCode()
    throws SQLException;

  public static TypeDescriptor getTypeDescriptor(String paramString, oracle.jdbc.OracleConnection paramOracleConnection)
    throws SQLException
  {
    Object localObject = null;
    try
    {
      SQLName localSQLName = new SQLName(paramString, paramOracleConnection);
      String str = localSQLName.getName();

      localObject = (TypeDescriptor)paramOracleConnection.getDescriptor(str);

      if (localObject == null)
      {
        OracleTypeADT localOracleTypeADT = new OracleTypeADT(str, paramOracleConnection);
        oracle.jdbc.internal.OracleConnection localOracleConnection = (oracle.jdbc.internal.OracleConnection)paramOracleConnection;

        localOracleTypeADT.init(localOracleConnection);

        OracleNamedType localOracleNamedType = localOracleTypeADT.cleanup();

        switch (localOracleNamedType.getTypeCode())
        {
        case 2002:
        case 2008:
          localObject = new StructDescriptor(localSQLName, (OracleTypeADT)localOracleNamedType, paramOracleConnection);

          break;
        case 2003:
          localObject = new ArrayDescriptor(localSQLName, (OracleTypeCOLLECTION)localOracleNamedType, paramOracleConnection);

          break;
        case 2007:
          localObject = new OpaqueDescriptor(localSQLName, (OracleTypeOPAQUE)localOracleNamedType, paramOracleConnection);

          break;
        case 2004:
        case 2005:
        case 2006:
        default:
          DatabaseError.throwSqlException(1);
        }

        paramOracleConnection.putDescriptor(str, localObject);

        localOracleNamedType.setDescriptor((TypeDescriptor)localObject);
      }

    }
    catch (Exception localException)
    {
      if ((localException instanceof SQLException)) {
        DatabaseError.throwSqlException((SQLException)localException, 60, "Unable to resolve type \"" + paramString + "\"");
      }
      else
      {
        DatabaseError.throwSqlException(60, "Unable to resolve type \"" + paramString + "\"");
      }

    }

    return (TypeDescriptor)localObject;
  }

  public static TypeDescriptor getTypeDescriptor(String paramString, oracle.jdbc.OracleConnection paramOracleConnection, byte[] paramArrayOfByte, long paramLong)
    throws SQLException
  {
    Object localObject = null;
    byte[][] arrayOfByte = new byte[1][];

    String str = getSubtypeName(paramOracleConnection, paramArrayOfByte, paramLong);

    if (str == null) {
      str = paramString;
    }

    localObject = (TypeDescriptor)paramOracleConnection.getDescriptor(str);

    if (localObject == null)
    {
      SQLName localSQLName = new SQLName(str, paramOracleConnection);

      OracleTypeADT localOracleTypeADT = new OracleTypeADT(str, paramOracleConnection);
      oracle.jdbc.internal.OracleConnection localOracleConnection = (oracle.jdbc.internal.OracleConnection)paramOracleConnection;

      localOracleTypeADT.init(localOracleConnection);

      OracleNamedType localOracleNamedType = localOracleTypeADT.cleanup();

      switch (localOracleNamedType.getTypeCode())
      {
      case 2002:
      case 2008:
        localObject = new StructDescriptor(localSQLName, (OracleTypeADT)localOracleNamedType, paramOracleConnection);

        break;
      case 2003:
        localObject = new ArrayDescriptor(localSQLName, (OracleTypeCOLLECTION)localOracleNamedType, paramOracleConnection);

        break;
      case 2007:
        localObject = new OpaqueDescriptor(localSQLName, (OracleTypeOPAQUE)localOracleNamedType, paramOracleConnection);

        break;
      case 2004:
      case 2005:
      case 2006:
      default:
        DatabaseError.throwSqlException(1);
      }

      paramOracleConnection.putDescriptor(str, localObject);
    }

    return (TypeDescriptor)localObject;
  }

  public boolean isInHierarchyOf(String paramString)
    throws SQLException
  {
    return false;
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    try
    {
      if (this.sqlName == null) {
        initSQLName();
      }

    }
    catch (SQLException localSQLException)
    {
      DatabaseError.SQLToIOException(localSQLException);
    }

    paramObjectOutputStream.writeObject(this.sqlName);
    paramObjectOutputStream.writeObject(this.pickler);
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.sqlName = ((SQLName)paramObjectInputStream.readObject());
    this.pickler = ((OracleNamedType)paramObjectInputStream.readObject());
  }

  public void setConnection(Connection paramConnection)
    throws SQLException
  {
    setPhysicalConnectionOf(paramConnection);
    this.pickler.setConnection(getInternalConnection());
  }

  public static String getSubtypeName(oracle.jdbc.OracleConnection paramOracleConnection, byte[] paramArrayOfByte, long paramLong)
    throws SQLException
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0) || (paramOracleConnection == null))
    {
      DatabaseError.throwSqlException(68);
    }

    String str = OracleTypeADT.getSubtypeName(paramOracleConnection, paramArrayOfByte, paramLong);

    return str;
  }

  public void initMetadataRecursively()
    throws SQLException
  {
    if (this.pickler != null)
      this.pickler.initMetadataRecursively();
  }

  public void initNamesRecursively()
    throws SQLException
  {
    if (this.pickler != null)
      this.pickler.initNamesRecursively();
  }

  public void fixupConnection(oracle.jdbc.internal.OracleConnection paramOracleConnection)
    throws SQLException
  {
    if (this.connection == null) this.connection = paramOracleConnection;
    if (this.pickler != null) this.pickler.fixupConnection(paramOracleConnection);
  }

  public String toXMLString()
    throws SQLException
  {
    StringWriter localStringWriter = new StringWriter();
    PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
    printXMLHeader(localPrintWriter);
    printXML(localPrintWriter, 0);
    return localStringWriter.toString();
  }

  public void printXML(PrintStream paramPrintStream)
    throws SQLException
  {
    PrintWriter localPrintWriter = new PrintWriter(paramPrintStream, true);
    printXMLHeader(localPrintWriter);
    printXML(localPrintWriter, 0);
  }

  void printXML(PrintWriter paramPrintWriter, int paramInt) throws SQLException
  {
    String str = getClass().getName();
    int i = hashCode();
    paramPrintWriter.println("<" + str + " hashCode=\"" + i + "\" >");
    if (this.pickler != null) this.pickler.printXML(paramPrintWriter, paramInt + 1);
    paramPrintWriter.println("</" + str + ">");
  }

  void printXMLHeader(PrintWriter paramPrintWriter) throws SQLException
  {
    paramPrintWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.TypeDescriptor
 * JD-Core Version:    0.6.0
 */