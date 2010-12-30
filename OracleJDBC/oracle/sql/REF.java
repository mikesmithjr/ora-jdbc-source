package oracle.sql;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.oracore.OracleTypeADT;

public class REF extends DatumWithConnection
  implements Ref, Serializable, Cloneable
{
  static final boolean DEBUG = false;
  static final long serialVersionUID = 1328446996944583167L;
  String typename;
  transient StructDescriptor descriptor;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:47_PDT_2005";

  public String getBaseTypeName()
    throws SQLException
  {
    if (this.typename == null)
    {
      if (this.descriptor != null) {
        this.typename = this.descriptor.getName();
      }
      else
      {
        DatabaseError.throwSqlException(52);
      }

    }

    return this.typename;
  }

  public REF(String paramString, Connection paramConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    super(paramArrayOfByte);

    if ((paramConnection == null) || (paramString == null))
    {
      DatabaseError.throwSqlException(68);
    }

    this.typename = paramString;
    this.descriptor = null;

    setPhysicalConnectionOf(paramConnection);
  }

  public REF(StructDescriptor paramStructDescriptor, Connection paramConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    super(paramArrayOfByte);

    if ((paramConnection == null) || (paramStructDescriptor == null))
    {
      DatabaseError.throwSqlException(68);
    }

    this.descriptor = paramStructDescriptor;

    setPhysicalConnectionOf(paramConnection);
  }

  public Object getValue(Map paramMap)
    throws SQLException
  {
    STRUCT localSTRUCT = getSTRUCT();
    Object localObject = localSTRUCT != null ? localSTRUCT.toJdbc(paramMap) : null;

    return localObject;
  }

  public Object getValue()
    throws SQLException
  {
    STRUCT localSTRUCT = getSTRUCT();
    Object localObject = localSTRUCT != null ? localSTRUCT.toJdbc() : null;

    return localObject;
  }

  public synchronized STRUCT getSTRUCT()
    throws SQLException
  {
    STRUCT localSTRUCT = null;

    OraclePreparedStatement localOraclePreparedStatement = (OraclePreparedStatement)getInternalConnection().prepareStatement("select deref(:1) from dual");

    localOraclePreparedStatement.setRowPrefetch(1);
    localOraclePreparedStatement.setREF(1, this);

    OracleResultSet localOracleResultSet = (OracleResultSet)localOraclePreparedStatement.executeQuery();
    try
    {
      if (localOracleResultSet.next()) {
        localSTRUCT = localOracleResultSet.getSTRUCT(1);
      }
      else
      {
        DatabaseError.throwSqlException(52);
      }
    }
    finally
    {
      localOracleResultSet.close();

      localOracleResultSet = null;

      localOraclePreparedStatement.close();

      localOraclePreparedStatement = null;
    }

    return localSTRUCT;
  }

  public synchronized void setValue(Object paramObject)
    throws SQLException
  {
    STRUCT localSTRUCT = STRUCT.toSTRUCT(paramObject, getInternalConnection());

    if (localSTRUCT.getInternalConnection() != getInternalConnection())
    {
      DatabaseError.throwSqlException(77, "Incompatible connection object");
    }

    if (!getBaseTypeName().equals(localSTRUCT.getSQLTypeName()))
    {
      DatabaseError.throwSqlException(77, "Incompatible type");
    }

    byte[] arrayOfByte1 = localSTRUCT.toBytes();

    byte[] arrayOfByte2 = localSTRUCT.getDescriptor().getOracleTypeADT().getTOID();

    CallableStatement localCallableStatement = null;
    try
    {
      localCallableStatement = getInternalConnection().prepareCall("begin :1 := dbms_pickler.update_through_ref (:2, :3, :4, :5); end;");

      localCallableStatement.registerOutParameter(1, 2);

      localCallableStatement.setBytes(2, shareBytes());
      localCallableStatement.setInt(3, 0);
      localCallableStatement.setBytes(4, arrayOfByte2);
      localCallableStatement.setBytes(5, arrayOfByte1);

      localCallableStatement.execute();

      int i = 0;

      if ((i = localCallableStatement.getInt(1)) != 0)
      {
        DatabaseError.throwSqlException(77, "ORA-" + i);
      }

    }
    finally
    {
      if (localCallableStatement != null) {
        localCallableStatement.close();
      }
      localCallableStatement = null;
    }
  }

  public StructDescriptor getDescriptor()
    throws SQLException
  {
    if (this.descriptor == null)
    {
      this.descriptor = StructDescriptor.createDescriptor(this.typename, getInternalConnection());
    }

    return this.descriptor;
  }

  public String getSQLTypeName()
    throws SQLException
  {
    String str = getBaseTypeName();

    return str;
  }

  public Object getObject(Map paramMap)
    throws SQLException
  {
    STRUCT localSTRUCT = getSTRUCT();
    Object localObject = localSTRUCT != null ? localSTRUCT.toJdbc(paramMap) : null;

    return localObject;
  }

  public Object getObject()
    throws SQLException
  {
    STRUCT localSTRUCT = getSTRUCT();
    Object localObject = localSTRUCT != null ? localSTRUCT.toJdbc() : null;

    return localObject;
  }

  public void setObject(Object paramObject)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = getInternalConnection().prepareStatement("call sys.utl_ref.update_object( :1, :2 )");

    localPreparedStatement.setRef(1, this);
    localPreparedStatement.setObject(2, paramObject);
    localPreparedStatement.execute();
    localPreparedStatement.close();
  }

  public Object toJdbc()
    throws SQLException
  {
    return this;
  }

  public boolean isConvertibleTo(Class paramClass)
  {
    return false;
  }

  public Object makeJdbcArray(int paramInt)
  {
    return new REF[paramInt];
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    REF localREF = null;
    try
    {
      localREF = new REF(getBaseTypeName(), getInternalConnection(), getBytes());
    }
    catch (SQLException localSQLException)
    {
      throw new CloneNotSupportedException(localSQLException.getMessage());
    }

    return localREF;
  }

  public boolean equals(Object paramObject)
  {
    int i = 0;
    try
    {
      i = ((paramObject instanceof REF)) && (super.equals(paramObject)) && (getBaseTypeName().equals(((REF)paramObject).getSQLTypeName())) ? 1 : 0;
    }
    catch (Exception localException)
    {
    }

    return i;
  }

  public int hashCode()
  {
    byte[] arrayOfByte = shareBytes();
    int i = 0;
    int j;
    if ((arrayOfByte[2] & 0x5) == 5)
    {
      for (j = 0; j < 4; j++)
      {
        i *= 256;
        i += (arrayOfByte[(8 + j)] & 0xFF);
      }
    }
    if ((arrayOfByte[2] & 0x3) == 3)
    {
      for (j = 0; (j < 4) && (j < arrayOfByte.length); j++)
      {
        i *= 256;
        i += (arrayOfByte[(6 + j)] & 0xFF);
      }
    }
    if ((arrayOfByte[2] & 0x2) == 2)
    {
      for (j = 0; j < 4; j++)
      {
        i *= 256;
        i += (arrayOfByte[(8 + j)] & 0xFF);
      }

    }

    return i;
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeObject(shareBytes());
    try
    {
      paramObjectOutputStream.writeUTF(getBaseTypeName());
    }
    catch (SQLException localSQLException)
    {
      throw new IOException("SQLException ORA-" + localSQLException.getErrorCode() + " " + localSQLException.getMessage());
    }
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    setBytes((byte[])paramObjectInputStream.readObject());

    this.typename = paramObjectInputStream.readUTF();
  }

  public Connection getJavaSqlConnection()
    throws SQLException
  {
    return super.getJavaSqlConnection();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.REF
 * JD-Core Version:    0.6.0
 */