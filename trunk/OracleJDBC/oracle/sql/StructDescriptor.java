package oracle.sql;

import B;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.util.Map;
import java.util.Vector;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.oracore.OracleNamedType;
import oracle.jdbc.oracore.OracleType;
import oracle.jdbc.oracore.OracleTypeADT;

public class StructDescriptor extends TypeDescriptor
  implements Serializable
{
  static final boolean DEBUG = false;
  static final long serialVersionUID = 1013921343538311063L;
  transient Boolean isInstanciable = null;
  transient String supertype = null;
  transient int numLocalAttrs = -1;
  transient String[] subtypes = null;
  transient String[] attrJavaNames = null;

  final int LOCAL_TYPE = 0;
  final int LOOK_FOR_USER_SYNONYM = 1;
  final int LOOK_FOR_PUBLIC_SYNONYM = 2;
  final String[] initMetaData1_9_0_SQL = { "SELECT INSTANTIABLE, supertype_owner, supertype_name, LOCAL_ATTRIBUTES FROM all_types WHERE type_name = :1 AND owner = :2 ", "DECLARE \n bind_synonym_name user_synonyms.synonym_name%type := :1; \n the_table_owner  user_synonyms.table_owner%type; \n the_table_name   user_synonyms.table_name%type; \n the_db_link      user_synonyms.db_link%type; \n sql_string       VARCHAR2(1000); \nBEGIN \n   SELECT /*+RULE*/ TABLE_NAME, TABLE_OWNER, DB_LINK INTO  \n         the_table_name, the_table_owner, the_db_link \n         FROM USER_SYNONYMS WHERE \n         SYNONYM_NAME = bind_synonym_name; \n \n   sql_string := 'SELECT /*+RULE*/ INSTANTIABLE, SUPERTYPE_OWNER,      SUPERTYPE_NAME, LOCAL_ATTRIBUTES FROM ALL_TYPES'; \n \n   IF the_db_link IS NOT NULL  \n   THEN \n     sql_string := sql_string || '@' || the_db_link; \n   END IF; \n   sql_string := sql_string       || ' WHERE TYPE_NAME = '''       || the_table_name   || ''' AND OWNER = '''       || the_table_owner  || ''''; \n   OPEN :2 FOR sql_string; \nEND;", "DECLARE \n bind_synonym_name user_synonyms.synonym_name%type := :1; \n the_table_owner  user_synonyms.table_owner%type; \n the_table_name   user_synonyms.table_name%type; \n the_db_link      user_synonyms.db_link%type; \n sql_string       VARCHAR2(1000); \nBEGIN \n   SELECT /*+RULE*/ TABLE_NAME, TABLE_OWNER, DB_LINK INTO  \n         the_table_name, the_table_owner, the_db_link \n         FROM ALL_SYNONYMS WHERE \n         OWNER = 'PUBLIC' AND \n         SYNONYM_NAME = bind_synonym_name; \n \n   sql_string := 'SELECT /*+RULE*/ INSTANTIABLE, SUPERTYPE_OWNER,      SUPERTYPE_NAME, LOCAL_ATTRIBUTES FROM ALL_TYPES'; \n \n   IF the_db_link IS NOT NULL  \n   THEN \n     sql_string := sql_string || '@' || the_db_link; \n   END IF; \n   sql_string := sql_string       || ' WHERE TYPE_NAME = '''       || the_table_name   || ''' AND OWNER = '''       || the_table_owner  || ''''; \n   OPEN :2 FOR sql_string; \nEND;" };

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:48_PDT_2005";

  public static StructDescriptor createDescriptor(String paramString, Connection paramConnection)
    throws SQLException
  {
    return createDescriptor(paramString, paramConnection, false, false);
  }

  public static StructDescriptor createDescriptor(String paramString, Connection paramConnection, boolean paramBoolean1, boolean paramBoolean2)
    throws SQLException
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramConnection == null))
    {
      DatabaseError.throwSqlException(60, "Invalid arguments");
    }

    SQLName localSQLName = new SQLName(paramString, (oracle.jdbc.OracleConnection)paramConnection);

    return createDescriptor(localSQLName, paramConnection, paramBoolean1, paramBoolean2);
  }

  public static StructDescriptor createDescriptor(SQLName paramSQLName, Connection paramConnection, boolean paramBoolean1, boolean paramBoolean2)
    throws SQLException
  {
    String str = paramSQLName.getName();
    StructDescriptor localStructDescriptor = null;
    if (!paramBoolean2)
    {
      localStructDescriptor = (StructDescriptor)((oracle.jdbc.OracleConnection)paramConnection).getDescriptor(str);

      if (localStructDescriptor == null)
      {
        localStructDescriptor = new StructDescriptor(paramSQLName, paramConnection);
        if (paramBoolean1) localStructDescriptor.initNamesRecursively();
        ((oracle.jdbc.OracleConnection)paramConnection).putDescriptor(str, localStructDescriptor);
      }

    }

    return localStructDescriptor;
  }

  public static StructDescriptor createDescriptor(SQLName paramSQLName, Connection paramConnection)
    throws SQLException
  {
    return createDescriptor(paramSQLName, paramConnection, false, false);
  }

  public static StructDescriptor createDescriptor(OracleTypeADT paramOracleTypeADT)
    throws SQLException
  {
    String str = paramOracleTypeADT.getFullName();
    oracle.jdbc.internal.OracleConnection localOracleConnection = paramOracleTypeADT.getConnection();
    StructDescriptor localStructDescriptor = (StructDescriptor)localOracleConnection.getDescriptor(str);

    if (localStructDescriptor == null)
    {
      SQLName localSQLName = new SQLName(paramOracleTypeADT.getSchemaName(), paramOracleTypeADT.getSimpleName(), paramOracleTypeADT.getConnection());

      localStructDescriptor = new StructDescriptor(localSQLName, paramOracleTypeADT, localOracleConnection);
      localStructDescriptor.initNamesRecursively();
      localOracleConnection.putDescriptor(str, localStructDescriptor);
    }

    return localStructDescriptor;
  }

  public StructDescriptor(String paramString, Connection paramConnection)
    throws SQLException
  {
    super(paramString, paramConnection);

    initPickler();
  }

  public StructDescriptor(SQLName paramSQLName, Connection paramConnection)
    throws SQLException
  {
    super(paramSQLName, paramConnection);

    initPickler();
  }

  public StructDescriptor(SQLName paramSQLName, OracleTypeADT paramOracleTypeADT, Connection paramConnection)
    throws SQLException
  {
    super(paramSQLName, paramOracleTypeADT, paramConnection);
  }

  static StructDescriptor createDescriptor(SQLName paramSQLName, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, oracle.jdbc.internal.OracleConnection paramOracleConnection, byte[] paramArrayOfByte4)
    throws SQLException
  {
    OracleTypeADT localOracleTypeADT = new OracleTypeADT(paramSQLName, paramArrayOfByte1, paramInt, paramArrayOfByte2, paramArrayOfByte3, paramOracleConnection, paramArrayOfByte4);
    return new StructDescriptor(paramSQLName, localOracleTypeADT, paramOracleConnection);
  }

  private void initPickler()
    throws SQLException
  {
    try
    {
      this.pickler = new OracleTypeADT(getName(), this.connection);

      ((OracleTypeADT)this.pickler).init(this.connection);

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

  public StructDescriptor(OracleTypeADT paramOracleTypeADT, Connection paramConnection)
    throws SQLException
  {
    super(paramOracleTypeADT, paramConnection);
  }

  public int getTypeCode()
    throws SQLException
  {
    int i = this.pickler.getTypeCode();

    return i;
  }

  public int getTypeVersion()
    throws SQLException
  {
    int i = this.pickler.getTypeVersion();

    return i;
  }

  byte[] toBytes(STRUCT paramSTRUCT, boolean paramBoolean)
    throws SQLException
  {
    Object localObject = paramSTRUCT.shareBytes();

    if (localObject == null)
    {
      if (paramSTRUCT.datumArray != null)
      {
        localObject = this.pickler.linearize(paramSTRUCT);

        if (!paramBoolean) {
          paramSTRUCT.setShareBytes(null);
        }

      }
      else if (paramSTRUCT.objectArray != null)
      {
        paramSTRUCT.datumArray = toOracleArray(paramSTRUCT.objectArray);
        localObject = this.pickler.linearize(paramSTRUCT);

        if (!paramBoolean)
        {
          paramSTRUCT.datumArray = null;

          paramSTRUCT.setShareBytes(null);
        }

      }
      else
      {
        DatabaseError.throwSqlException(1);
      }

    }
    else if (paramSTRUCT.imageLength != 0L)
    {
      if ((paramSTRUCT.imageOffset != 0L) || (paramSTRUCT.imageLength != localObject.length))
      {
        byte[] arrayOfByte = new byte[(int)paramSTRUCT.imageLength];

        System.arraycopy(localObject, (int)paramSTRUCT.imageOffset, arrayOfByte, 0, (int)paramSTRUCT.imageLength);

        paramSTRUCT.setImage(arrayOfByte, 0L, 0L);

        localObject = arrayOfByte;
      }

    }

    return (B)localObject;
  }

  Datum[] toOracleArray(STRUCT paramSTRUCT, boolean paramBoolean)
    throws SQLException
  {
    Datum[] arrayOfDatum1 = paramSTRUCT.datumArray;
    Datum[] arrayOfDatum2 = null;

    if (arrayOfDatum1 == null)
    {
      if (paramSTRUCT.objectArray != null)
      {
        arrayOfDatum1 = toOracleArray(paramSTRUCT.objectArray);
      }
      else if (paramSTRUCT.shareBytes() != null)
      {
        if (((paramSTRUCT.shareBytes()[0] & 0x80) <= 0) && (((OracleTypeADT)this.pickler).isEmbeddedADT()))
        {
          this.pickler = OracleTypeADT.shallowClone((OracleTypeADT)this.pickler);
        }

        this.pickler.unlinearize(paramSTRUCT.shareBytes(), paramSTRUCT.imageOffset, paramSTRUCT, 1, null);

        arrayOfDatum1 = paramSTRUCT.datumArray;

        if (!paramBoolean) {
          paramSTRUCT.datumArray = null;
        }

      }
      else
      {
        DatabaseError.throwSqlException(1);
      }

    }

    if (paramBoolean)
    {
      paramSTRUCT.datumArray = arrayOfDatum1;
      arrayOfDatum2 = (Datum[])arrayOfDatum1.clone();
    }
    else
    {
      arrayOfDatum2 = arrayOfDatum1;
    }

    return arrayOfDatum2;
  }

  Object[] toArray(STRUCT paramSTRUCT, Map paramMap, boolean paramBoolean)
    throws SQLException
  {
    Object[] arrayOfObject = null;

    if (paramSTRUCT.objectArray == null)
    {
      if (paramSTRUCT.datumArray != null)
      {
        arrayOfObject = new Object[paramSTRUCT.datumArray.length];

        for (int i = 0; i < paramSTRUCT.datumArray.length; i++)
        {
          if (paramSTRUCT.datumArray[i] == null)
            continue;
          if ((paramSTRUCT.datumArray[i] instanceof STRUCT))
            arrayOfObject[i] = ((STRUCT)paramSTRUCT.datumArray[i]).toJdbc(paramMap);
          else {
            arrayOfObject[i] = paramSTRUCT.datumArray[i].toJdbc();
          }
        }
      }
      if (paramSTRUCT.shareBytes() != null)
      {
        if (((paramSTRUCT.shareBytes()[0] & 0x80) <= 0) && (((OracleTypeADT)this.pickler).isEmbeddedADT()))
        {
          this.pickler = OracleTypeADT.shallowClone((OracleTypeADT)this.pickler);
        }

        this.pickler.unlinearize(paramSTRUCT.shareBytes(), paramSTRUCT.imageOffset, paramSTRUCT, 2, paramMap);

        arrayOfObject = paramSTRUCT.objectArray;

        paramSTRUCT.objectArray = null;
      }
      else
      {
        DatabaseError.throwSqlException(1);
      }

    }
    else
    {
      arrayOfObject = (Object[])paramSTRUCT.objectArray.clone();
    }

    return arrayOfObject;
  }

  public int getLength()
    throws SQLException
  {
    int i = getFieldTypes().length;

    return i;
  }

  public OracleTypeADT getOracleTypeADT()
  {
    OracleTypeADT localOracleTypeADT = (OracleTypeADT)this.pickler;

    return localOracleTypeADT;
  }

  private OracleType[] getFieldTypes()
    throws SQLException
  {
    OracleType[] arrayOfOracleType = ((OracleTypeADT)this.pickler).getAttrTypes();

    return arrayOfOracleType;
  }

  public SQLInput toJdbc2SQLInput(STRUCT paramSTRUCT, Map paramMap)
    throws SQLException
  {
    OracleJdbc2SQLInput localOracleJdbc2SQLInput = new OracleJdbc2SQLInput(toOracleArray(paramSTRUCT, false), paramMap, this.connection);

    return localOracleJdbc2SQLInput;
  }

  public SQLOutput toJdbc2SQLOutput()
    throws SQLException
  {
    OracleSQLOutput localOracleSQLOutput = new OracleSQLOutput(this, this.connection);

    return localOracleSQLOutput;
  }

  public Datum[] toOracleArray(Object[] paramArrayOfObject)
    throws SQLException
  {
    Datum[] arrayOfDatum = null;

    if (paramArrayOfObject != null)
    {
      OracleType[] arrayOfOracleType = getFieldTypes();
      int i = arrayOfOracleType.length;

      if (paramArrayOfObject.length != i)
      {
        DatabaseError.throwSqlException(49, null);
      }

      arrayOfDatum = new Datum[i];

      oracle.jdbc.internal.OracleConnection localOracleConnection = this.connection;

      for (int j = 0; j < i; j++) {
        arrayOfDatum[j] = arrayOfOracleType[j].toDatum(paramArrayOfObject[j], localOracleConnection);
      }

    }

    return arrayOfDatum;
  }

  public Datum[] toOracleArray(Map paramMap)
    throws SQLException
  {
    Datum[] arrayOfDatum = null;
    int i = 0;

    if (paramMap != null)
    {
      OracleType[] arrayOfOracleType = getFieldTypes();
      int j = arrayOfOracleType.length;
      int k = paramMap.size();

      arrayOfDatum = new Datum[j];

      oracle.jdbc.internal.OracleConnection localOracleConnection = this.connection;

      for (int m = 0; m < j; m++)
      {
        Object localObject = paramMap.get(((OracleTypeADT)this.pickler).getAttributeName(m + 1));

        arrayOfDatum[m] = arrayOfOracleType[m].toDatum(localObject, localOracleConnection);

        if ((localObject == null) && (!paramMap.containsKey(((OracleTypeADT)this.pickler).getAttributeName(m + 1)))) {
          continue;
        }
        i++;
      }

      if (i < k)
      {
        DatabaseError.throwSqlException(68, null);
      }

    }

    return arrayOfDatum;
  }

  public ResultSetMetaData getMetaData()
    throws SQLException
  {
    ResultSetMetaData localResultSetMetaData = this.connection.newStructMetaData(this);

    return localResultSetMetaData;
  }

  public boolean isFinalType()
    throws SQLException
  {
    boolean bool = getOracleTypeADT().isFinalType();

    return bool;
  }

  public boolean isSubtype()
    throws SQLException
  {
    boolean bool = getOracleTypeADT().isSubType();

    return bool;
  }

  public boolean isInHierarchyOf(String paramString)
    throws SQLException
  {
    StructDescriptor localStructDescriptor = this;
    String str = localStructDescriptor.getName();
    int i = 0;

    if (paramString.equals(str)) {
      i = 1;
    }
    else {
      while (true)
      {
        str = localStructDescriptor.getSupertypeName();

        if (str == null)
        {
          i = 0;

          break;
        }

        if (paramString.equals(str))
        {
          i = 1;

          break;
        }

        localStructDescriptor = createDescriptor(str, this.connection);
      }

    }

    return i;
  }

  public boolean isInstantiable()
    throws SQLException
  {
    if (this.isInstanciable == null) {
      initMetaData1();
    }
    boolean bool = this.isInstanciable.booleanValue();

    return bool;
  }

  public boolean isJavaObject()
    throws SQLException
  {
    boolean bool = getOracleTypeADT().isJavaObject();

    return bool;
  }

  public String getSupertypeName()
    throws SQLException
  {
    String str = null;

    if (isSubtype())
    {
      if (this.supertype == null) {
        initMetaData1();
      }
      str = this.supertype;
    }

    return str;
  }

  public int getLocalAttributeCount()
    throws SQLException
  {
    int i;
    if (!isSubtype()) {
      i = getOracleTypeADT().getAttrTypes().length;
    }
    else {
      if (this.numLocalAttrs == -1) {
        initMetaData1();
      }
      i = this.numLocalAttrs;
    }

    return i;
  }

  public String[] getSubtypeNames()
    throws SQLException
  {
    if (this.subtypes == null) {
      initMetaData2();
    }

    return this.subtypes;
  }

  public String getJavaClassName()
    throws SQLException
  {
    String str = null;

    if (isJavaObject()) {
      str = getJavaObjectClassName(this.connection, this);
    }

    return str;
  }

  public String getAttributeJavaName(int paramInt)
    throws SQLException
  {
    String str = null;

    if (isJavaObject())
    {
      if (this.attrJavaNames == null) {
        initMetaData3();
      }
      str = this.attrJavaNames[paramInt];
    }

    return str;
  }

  public String[] getAttributeJavaNames()
    throws SQLException
  {
    String[] arrayOfString = null;

    if (isJavaObject())
    {
      if (this.attrJavaNames == null) {
        initMetaData3();
      }
      arrayOfString = this.attrJavaNames;
    }
    else
    {
      arrayOfString = new String[0];
    }

    return arrayOfString;
  }

  public String getLanguage()
    throws SQLException
  {
    String str = null;

    if (isJavaObject())
      str = "JAVA";
    else {
      str = "SQL";
    }

    return str;
  }

  public Class getClass(Map paramMap)
    throws SQLException
  {
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
    if (SQLName.s_parseAllFormat)
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

    }

    return localClass;
  }

  public static String getJavaObjectClassName(Connection paramConnection, StructDescriptor paramStructDescriptor)
    throws SQLException
  {
    String str = getJavaObjectClassName(paramConnection, paramStructDescriptor.getSchemaName(), paramStructDescriptor.getTypeName());

    return str;
  }

  public static String getJavaObjectClassName(Connection paramConnection, String paramString1, String paramString2)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;

    String str = null;
    try
    {
      localPreparedStatement = paramConnection.prepareStatement("select external_name from all_sqlj_types where owner = :1 and type_name = :2");

      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);

      localResultSet = localPreparedStatement.executeQuery();

      if (localResultSet.next()) {
        str = localResultSet.getString(1);
      }
      else
      {
        DatabaseError.throwSqlException(100);
      }

    }
    catch (SQLException localSQLException)
    {
    }
    finally
    {
      if (localResultSet != null) {
        localResultSet.close();
      }
      if (localPreparedStatement != null) {
        localPreparedStatement.close();
      }

    }

    return str;
  }

  public String descType()
    throws SQLException
  {
    StringBuffer localStringBuffer = new StringBuffer();

    return descType(localStringBuffer, 0);
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
    paramStringBuffer.append("\n");
    paramStringBuffer.append(str1);
    paramStringBuffer.append("Subtype=" + getOracleTypeADT().isSubType());
    paramStringBuffer.append(" JavaObject=" + getOracleTypeADT().isJavaObject());
    paramStringBuffer.append(" FinalType=" + getOracleTypeADT().isFinalType());
    paramStringBuffer.append("\n");

    ResultSetMetaData localResultSetMetaData = getMetaData();
    int j = localResultSetMetaData.getColumnCount();

    for (int k = 0; k < j; k++)
    {
      int m = localResultSetMetaData.getColumnType(k + 1);
      Object localObject;
      if ((m == 2002) || (m == 2008))
      {
        localObject = createDescriptor(localResultSetMetaData.getColumnTypeName(k + 1), this.connection);

        ((StructDescriptor)localObject).descType(paramStringBuffer, paramInt + 1);
      }
      else if (m == 2003)
      {
        localObject = ArrayDescriptor.createDescriptor(localResultSetMetaData.getColumnTypeName(k + 1), this.connection);

        ((ArrayDescriptor)localObject).descType(paramStringBuffer, paramInt + 1);
      }
      else if (m == 2007)
      {
        localObject = OpaqueDescriptor.createDescriptor(localResultSetMetaData.getColumnTypeName(k + 1), this.connection);

        ((OpaqueDescriptor)localObject).descType(paramStringBuffer, paramInt + 1);
      }
      else
      {
        paramStringBuffer.append(str2);
        paramStringBuffer.append(localResultSetMetaData.getColumnTypeName(k + 1));
        paramStringBuffer.append("\n");
      }
    }

    String str3 = paramStringBuffer.substring(0, paramStringBuffer.length());

    return (String)str3;
  }

  public byte[] toBytes(Object[] paramArrayOfObject)
    throws SQLException
  {
    Datum[] arrayOfDatum = toOracleArray(paramArrayOfObject);

    return toBytes(arrayOfDatum);
  }

  /** @deprecated */
  public byte[] toBytes(Datum[] paramArrayOfDatum)
    throws SQLException
  {
    STRUCT localSTRUCT = new STRUCT(this, (byte[])null, this.connection);

    localSTRUCT.setDatumArray(paramArrayOfDatum);

    return this.pickler.linearize(localSTRUCT);
  }

  public Datum[] toArray(Object[] paramArrayOfObject)
    throws SQLException
  {
    return toOracleArray(paramArrayOfObject);
  }

  public Datum[] toArray(byte[] paramArrayOfByte)
    throws SQLException
  {
    STRUCT localSTRUCT = new STRUCT(this, paramArrayOfByte, this.connection);

    return toOracleArray(localSTRUCT, false);
  }

  private void initMetaData1()
    throws SQLException
  {
    int i = this.connection.getVersionNumber();

    if (i >= 9000)
      initMetaData1_9_0();
    else
      initMetaData1_pre_9_0();
  }

  private void initMetaData1_9_0()
    throws SQLException
  {
    int i = 0;

    synchronized (this.connection)
    {
      synchronized (this)
      {
        if (this.numLocalAttrs == -1)
        {
          PreparedStatement localPreparedStatement = null;
          OracleCallableStatement localOracleCallableStatement = null;
          ResultSet localResultSet = null;
          int j = -1;
          try
          {
            while (true)
            {
              switch (i)
              {
              case 0:
                localPreparedStatement = this.connection.prepareStatement(this.initMetaData1_9_0_SQL[i]);

                localPreparedStatement.setString(1, getTypeName());
                localPreparedStatement.setString(2, getSchemaName());

                localPreparedStatement.setFetchSize(1);
                localResultSet = localPreparedStatement.executeQuery();

                break;
              case 1:
              case 2:
                try
                {
                  localOracleCallableStatement = (OracleCallableStatement)this.connection.prepareCall(this.initMetaData1_9_0_SQL[i]);

                  localOracleCallableStatement.setString(1, getTypeName());
                  localOracleCallableStatement.registerOutParameter(2, -10);

                  localOracleCallableStatement.execute();

                  localResultSet = localOracleCallableStatement.getCursor(2);
                  localResultSet.setFetchSize(1);
                }
                catch (SQLException localSQLException) {
                  if (localSQLException.getErrorCode() == 1403)
                  {
                    if (i == 1)
                    {
                      localOracleCallableStatement.close();
                      i++;
                      continue;
                    }

                    DatabaseError.throwSqlException(1, "Inconsistent catalog view");
                  }
                  else
                  {
                    throw localSQLException;
                  }

                }

              default:
                if (localResultSet.next())
                {
                  this.isInstanciable = new Boolean(localResultSet.getString(1).equals("YES"));
                  this.supertype = (localResultSet.getString(2) + "." + localResultSet.getString(3));
                  j = localResultSet.getInt(4);

                  break label362;
                }

                if (i == 2)
                {
                  DatabaseError.throwSqlException(1, "Inconsistent catalog view"); continue;
                }

                localResultSet.close();
                if (localOracleCallableStatement != null)
                  localOracleCallableStatement.close();
                i++;
              }
            }

          }
          finally
          {
            label362: if (localResultSet != null) {
              localResultSet.close();
            }
            if (localPreparedStatement != null) {
              localPreparedStatement.close();
            }
            if (localOracleCallableStatement != null) {
              localOracleCallableStatement.close();
            }
          }
          this.numLocalAttrs = j;
        }
      }
    }
  }

  private synchronized void initMetaData1_pre_9_0()
    throws SQLException
  {
    this.isInstanciable = new Boolean(true);
    this.supertype = "";
    this.numLocalAttrs = 0;
  }

  private void initMetaData2()
    throws SQLException
  {
    int i = this.connection.getVersionNumber();

    if (i >= 9000) {
      initMetaData2_9_0();
    }
    else
    {
      initMetaData2_pre_9_0();
    }
  }

  private void initMetaData2_9_0()
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        if (this.subtypes == null)
        {
          PreparedStatement localPreparedStatement = null;
          ResultSet localResultSet = null;
          String[] arrayOfString = null;
          try
          {
            localPreparedStatement = this.connection.prepareStatement("select owner, type_name from all_types where supertype_name = :1 and supertype_owner = :2");

            localPreparedStatement.setString(1, getTypeName());
            localPreparedStatement.setString(2, getSchemaName());

            localResultSet = localPreparedStatement.executeQuery();

            Vector localVector = new Vector();

            while (localResultSet.next()) {
              localVector.addElement(localResultSet.getString(1) + "." + localResultSet.getString(2));
            }
            arrayOfString = new String[localVector.size()];

            for (int i = 0; i < arrayOfString.length; i++) {
              arrayOfString[i] = ((String)localVector.elementAt(i));
            }
            localVector.removeAllElements();

            localVector = null;
          }
          finally
          {
            if (localResultSet != null) {
              localResultSet.close();
            }
            if (localPreparedStatement != null) {
              localPreparedStatement.close();
            }
          }
          this.subtypes = arrayOfString;
        }
      }
    }
  }

  private void initMetaData2_pre_9_0()
    throws SQLException
  {
    this.subtypes = new String[0];
  }

  private void initMetaData3()
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        if (this.attrJavaNames == null)
        {
          String[] arrayOfString = null;
          PreparedStatement localPreparedStatement = null;
          ResultSet localResultSet = null;
          try
          {
            localPreparedStatement = this.connection.prepareStatement("select EXTERNAL_ATTR_NAME from all_sqlj_type_attrs where owner = :1 and type_name = :2");

            localPreparedStatement.setString(1, getSchemaName());
            localPreparedStatement.setString(2, getTypeName());

            localResultSet = localPreparedStatement.executeQuery();
            arrayOfString = new String[getOracleTypeADT().getAttrTypes().length];

            for (int i = 0; localResultSet.next(); i++)
              arrayOfString[i] = localResultSet.getString(1);
          }
          finally
          {
            if (localResultSet != null) {
              localResultSet.close();
            }
            if (localPreparedStatement != null) {
              localPreparedStatement.close();
            }
          }
          this.attrJavaNames = arrayOfString;
        }
      }
    }
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
 * Qualified Name:     oracle.sql.StructDescriptor
 * JD-Core Version:    0.6.0
 */