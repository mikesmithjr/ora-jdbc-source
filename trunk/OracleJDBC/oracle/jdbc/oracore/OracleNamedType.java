package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.Datum;
import oracle.sql.SQLName;
import oracle.sql.TypeDescriptor;

public abstract class OracleNamedType extends OracleType
  implements Serializable
{
  transient OracleConnection connection;
  SQLName sqlName = null;
  transient OracleTypeADT parent = null;
  transient int idx;
  transient TypeDescriptor descriptor = null;

  static String getUserTypeTreeSql = "/*+ RULE */select level depth, parent_type, child_type, ATTR_NO, child_type_owner from  (select TYPE_NAME parent_type, ELEM_TYPE_NAME child_type, 0 ATTR_NO,       ELEM_TYPE_OWNER child_type_owner     from USER_COLL_TYPES  union   select TYPE_NAME parent_type, ATTR_TYPE_NAME child_type, ATTR_NO,       ATTR_TYPE_OWNER child_type_owner     from USER_TYPE_ATTRS  ) start with parent_type  = ?  connect by prior  child_type = parent_type";

  static String getAllTypeTreeSql = "/*+ RULE */select parent_type, parent_type_owner, child_type, ATTR_NO, child_type_owner from ( select TYPE_NAME parent_type,  OWNER parent_type_owner,     ELEM_TYPE_NAME child_type, 0 ATTR_NO,     ELEM_TYPE_OWNER child_type_owner   from ALL_COLL_TYPES union   select TYPE_NAME parent_type, OWNER parent_type_owner,     ATTR_TYPE_NAME child_type, ATTR_NO,     ATTR_TYPE_OWNER child_type_owner   from ALL_TYPE_ATTRS ) start with parent_type  = ?  and parent_type_owner = ? connect by prior child_type = parent_type   and ( child_type_owner = parent_type_owner or child_type_owner is null )";

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:48_PDT_2005";

  protected OracleNamedType()
  {
  }

  public OracleNamedType(String paramString, OracleConnection paramOracleConnection)
    throws SQLException
  {
    setConnectionInternal(paramOracleConnection);
    this.sqlName = new SQLName(paramString, paramOracleConnection);
  }

  protected OracleNamedType(OracleTypeADT paramOracleTypeADT, int paramInt, OracleConnection paramOracleConnection)
  {
    setConnectionInternal(paramOracleConnection);
    this.parent = paramOracleTypeADT;
    this.idx = paramInt;
  }

  public String getFullName()
    throws SQLException
  {
    return getFullName(false);
  }

  public String getFullName(boolean paramBoolean)
    throws SQLException
  {
    String str = null;

    if ((paramBoolean | this.sqlName == null))
    {
      if ((this.parent != null) && ((str = this.parent.getAttributeType(this.idx)) != null))
      {
        this.sqlName = new SQLName(str, this.connection);
      }
      else {
        DatabaseError.throwSqlException(1, "Unable to resolve name");
      }

    }

    return this.sqlName.getName();
  }

  public String getSchemaName() throws SQLException
  {
    if (this.sqlName == null) getFullName();

    return this.sqlName.getSchema();
  }

  public String getSimpleName() throws SQLException
  {
    if (this.sqlName == null) getFullName();

    return this.sqlName.getSimpleName();
  }
  public boolean hasName() throws SQLException {
    return this.sqlName != null;
  }
  public OracleTypeADT getParent() throws SQLException { return this.parent; }

  public void setParent(OracleTypeADT paramOracleTypeADT) throws SQLException
  {
    this.parent = paramOracleTypeADT;
  }
  public int getOrder() throws SQLException {
    return this.idx;
  }
  public void setOrder(int paramInt) throws SQLException { this.idx = paramInt;
  }

  public OracleConnection getConnection()
    throws SQLException
  {
    return this.connection;
  }

  public void setConnection(OracleConnection paramOracleConnection)
    throws SQLException
  {
    setConnectionInternal(paramOracleConnection);
  }

  public void setConnectionInternal(OracleConnection paramOracleConnection)
  {
    this.connection = paramOracleConnection;
  }

  public Datum unlinearize(byte[] paramArrayOfByte, long paramLong, Datum paramDatum, int paramInt, Map paramMap)
    throws SQLException
  {
    DatabaseError.throwSqlException(23);

    return null;
  }

  public Datum unlinearize(byte[] paramArrayOfByte, long paramLong1, Datum paramDatum, long paramLong2, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    DatabaseError.throwSqlException(23);

    return null;
  }

  public byte[] linearize(Datum paramDatum)
    throws SQLException
  {
    DatabaseError.throwSqlException(23);

    return null;
  }

  public TypeDescriptor getDescriptor() throws SQLException
  {
    return this.descriptor;
  }

  public void setDescriptor(TypeDescriptor paramTypeDescriptor) throws SQLException
  {
    this.descriptor = paramTypeDescriptor;
  }

  public int getTypeVersion()
  {
    return 1;
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    try
    {
      paramObjectOutputStream.writeUTF(getFullName());
    }
    catch (SQLException localSQLException)
    {
      DatabaseError.SQLToIOException(localSQLException);
    }
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    String str = paramObjectInputStream.readUTF();
    try { this.sqlName = new SQLName(str, null); } catch (SQLException localSQLException) {
    }this.parent = null;
    this.idx = -1;
  }

  public void fixupConnection(OracleConnection paramOracleConnection)
    throws SQLException
  {
    if (this.connection == null) setConnection(paramOracleConnection);
  }

  public void printXML(PrintWriter paramPrintWriter, int paramInt)
    throws SQLException
  {
    for (int i = 0; i < paramInt; i++) paramPrintWriter.print("  ");
    paramPrintWriter.println("<OracleNamedType/>");
  }

  public void initNamesRecursively() throws SQLException
  {
    Map localMap = createTypesTreeMap();
    if (localMap.size() > 0)
      initChildNamesRecursively(localMap);
  }

  public void setNames(String paramString1, String paramString2) throws SQLException
  {
    this.sqlName = new SQLName(paramString1, paramString2, this.connection);
  }

  public void setSqlName(SQLName paramSQLName)
  {
    this.sqlName = paramSQLName;
  }

  public Map createTypesTreeMap()
    throws SQLException
  {
    Map localMap = null;
    String str = this.connection.getUserName();
    if (this.sqlName.getSchema().equals(str)) {
      localMap = getNodeMapFromUserTypes();
    }
    if (localMap == null)
      localMap = getNodeMapFromAllTypes();
    return localMap;
  }

  Map getNodeMapFromUserTypes()
    throws SQLException
  {
    HashMap localHashMap = new HashMap();
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement = this.connection.prepareStatement(getUserTypeTreeSql);
      localPreparedStatement.setString(1, this.sqlName.getSimpleName());
      localResultSet = localPreparedStatement.executeQuery();

      while (localResultSet.next())
      {
        int i = localResultSet.getInt(1);
        String str1 = localResultSet.getString(2);
        String str2 = localResultSet.getString(3);
        int j = localResultSet.getInt(4);
        String str3 = localResultSet.getString(5);
        if ((str3 != null) && (!str3.equals(this.sqlName.getSchema())))
          throw new NodeMapException();
        if (str1.length() <= 0)
          continue;
        SQLName localSQLName = new SQLName(this.sqlName.getSchema(), str1, this.connection);
        TypeTreeElement localTypeTreeElement = null;
        if (localHashMap.containsKey(localSQLName)) {
          localTypeTreeElement = (TypeTreeElement)localHashMap.get(localSQLName);
        }
        else {
          localTypeTreeElement = new TypeTreeElement(this.sqlName.getSchema(), str1);
          localHashMap.put(localSQLName, localTypeTreeElement);
        }
        localTypeTreeElement.putChild(this.sqlName.getSchema(), str2, j);
      }
    }
    catch (NodeMapException localNodeMapException) {
      localHashMap = null; localNodeMapException.printStackTrace(System.err); } finally {
      if (localResultSet != null) localResultSet.close(); if (localPreparedStatement != null) localPreparedStatement.close(); 
    }
    return localHashMap;
  }

  Map getNodeMapFromAllTypes()
    throws SQLException
  {
    HashMap localHashMap = new HashMap();
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement = this.connection.prepareStatement(getAllTypeTreeSql);
      localPreparedStatement.setString(1, this.sqlName.getSimpleName());
      localPreparedStatement.setString(2, this.sqlName.getSchema());
      localResultSet = localPreparedStatement.executeQuery();

      while (localResultSet.next())
      {
        String str1 = localResultSet.getString(1);
        String str2 = localResultSet.getString(2);
        String str3 = localResultSet.getString(3);
        int i = localResultSet.getInt(4);
        String str4 = localResultSet.getString(5);
        if (str4 == null) str4 = "SYS";
        if (str1.length() <= 0)
          continue;
        SQLName localSQLName = new SQLName(str2, str1, this.connection);
        TypeTreeElement localTypeTreeElement = null;
        if (localHashMap.containsKey(localSQLName)) {
          localTypeTreeElement = (TypeTreeElement)localHashMap.get(localSQLName);
        }
        else {
          localTypeTreeElement = new TypeTreeElement(str2, str1);
          localHashMap.put(localSQLName, localTypeTreeElement);
        }
        localTypeTreeElement.putChild(str4, str3, i);
      }
    }
    finally {
      if (localResultSet != null) localResultSet.close(); if (localPreparedStatement != null) localPreparedStatement.close(); 
    }
    return localHashMap;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleNamedType
 * JD-Core Version:    0.6.0
 */