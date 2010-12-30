package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.oracore.OracleNamedType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

public class OracleResultSetMetaData
  implements oracle.jdbc.internal.OracleResultSetMetaData
{
  PhysicalConnection connection;
  OracleStatement statement;
  int m_beginColumnIndex;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  public OracleResultSetMetaData()
  {
  }

  public OracleResultSetMetaData(PhysicalConnection paramPhysicalConnection, OracleStatement paramOracleStatement)
    throws SQLException
  {
    this.connection = paramPhysicalConnection;
    this.statement = paramOracleStatement;

    paramOracleStatement.describe();

    this.m_beginColumnIndex = 0;
  }

  OracleResultSetMetaData(PhysicalConnection paramPhysicalConnection, OracleStatement paramOracleStatement, int paramInt)
    throws SQLException
  {
    this.connection = paramPhysicalConnection;
    this.statement = paramOracleStatement;

    paramOracleStatement.describe();

    this.m_beginColumnIndex = paramInt;
  }

  public OracleResultSetMetaData(OracleResultSet paramOracleResultSet)
    throws SQLException
  {
    this.statement = ((OracleStatement)paramOracleResultSet.getStatement());
    this.connection = ((PhysicalConnection)this.statement.getConnection());

    this.statement.describe();

    this.m_beginColumnIndex = paramOracleResultSet.getFirstUserColumnIndex();
  }

  public int getColumnCount()
    throws SQLException
  {
    return this.statement.getNumberOfColumns() - this.m_beginColumnIndex;
  }

  public boolean isAutoIncrement(int paramInt)
    throws SQLException
  {
    return false;
  }

  int getValidColumnIndex(int paramInt)
    throws SQLException
  {
    int i = paramInt + this.m_beginColumnIndex - 1;

    if ((i < 0) || (i >= this.statement.getNumberOfColumns())) {
      DatabaseError.throwSqlException(3, "getValidColumnIndex");
    }

    return i;
  }

  public boolean isCaseSensitive(int paramInt)
    throws SQLException
  {
    int i = getColumnType(paramInt);

    return (i == 1) || (i == 12) || (i == -1);
  }

  public boolean isSearchable(int paramInt)
    throws SQLException
  {
    int i = getColumnType(paramInt);

    return (i != -4) && (i != -1) && (i != 2004) && (i != 2005) && (i != -13) && (i != 2002) && (i != 2008) && (i != 2007) && (i != 2003) && (i != 2006) && (i != -10);
  }

  public boolean isCurrency(int paramInt)
    throws SQLException
  {
    int i = getColumnType(paramInt);

    return (i == 2) || (i == 6);
  }

  public int isNullable(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    return getDescription()[i].nullable ? 1 : 0;
  }

  public boolean isSigned(int paramInt)
    throws SQLException
  {
    return true;
  }

  public int getColumnDisplaySize(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    return getDescription()[i].describeMaxLength;
  }

  public String getColumnLabel(int paramInt)
    throws SQLException
  {
    return getColumnName(paramInt);
  }

  public String getColumnName(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    return this.statement.getDescriptionWithNames()[i].columnName;
  }

  public String getSchemaName(int paramInt)
    throws SQLException
  {
    return "";
  }

  public int getPrecision(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    int j = getDescription()[i].describeType;

    switch (j)
    {
    case 112:
    case 113:
      return -1;
    case 8:
    case 24:
      return 2147483647;
    case 1:
    case 96:
      return getDescription()[i].describeMaxLength;
    }

    return getDescription()[i].precision;
  }

  public int getScale(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);
    int j = getDescription()[i].scale;

    return (j == -127) && (this.statement.connection.j2ee13Compliant) ? 0 : j;
  }

  public String getTableName(int paramInt)
    throws SQLException
  {
    return "";
  }

  public String getCatalogName(int paramInt)
    throws SQLException
  {
    return "";
  }

  public int getColumnType(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    int j = getDescription()[i].describeType;

    switch (j)
    {
    case 96:
      return 1;
    case 1:
      return 12;
    case 8:
      return -1;
    case 2:
    case 6:
      if ((this.statement.connection.j2ee13Compliant) && (getDescription()[i].precision != 0) && (getDescription()[i].scale == -127))
      {
        return 6;
      }
      return 2;
    case 100:
      return 100;
    case 101:
      return 101;
    case 23:
      return -3;
    case 24:
      return -4;
    case 104:
      return -8;
    case 102:
      return -10;
    case 12:
      return this.connection.v8Compatible ? 93 : 91;
    case 180:
      return 93;
    case 181:
      return -101;
    case 231:
      return -102;
    case 113:
      return 2004;
    case 112:
      return 2005;
    case 114:
      return -13;
    case 109:
      OracleNamedType localOracleNamedType = (OracleNamedType)getDescription()[i].describeOtype;

      TypeDescriptor localTypeDescriptor = TypeDescriptor.getTypeDescriptor(localOracleNamedType.getFullName(), this.connection);

      if (localTypeDescriptor != null) {
        return localTypeDescriptor.getTypeCode();
      }
      DatabaseError.throwSqlException(60);

      return -1;
    case 111:
      return 2006;
    case 182:
      return -103;
    case 183:
      return -104;
    }

    return 1111;
  }

  public String getColumnTypeName(int paramInt)
    throws SQLException
  {
    int i = getColumnType(paramInt);
    int j;
    OracleTypeADT localOracleTypeADT;
    switch (i)
    {
    case 1:
      return "CHAR";
    case 12:
      return "VARCHAR2";
    case -1:
      return "LONG";
    case -3:
      return "RAW";
    case -4:
      return "LONG RAW";
    case 2:
      return "NUMBER";
    case 6:
      return "FLOAT";
    case 100:
      return "BINARY_FLOAT";
    case 101:
      return "BINARY_DOUBLE";
    case 91:
      return "DATE";
    case 93:
      return this.connection.v8Compatible ? "DATE" : "TIMESTAMP";
    case -101:
      return "TIMESTAMPTZ";
    case -102:
      return "TIMESTAMPLTZ";
    case -8:
      return "ROWID";
    case -10:
      return "REFCURSOR";
    case 2004:
      return "BLOB";
    case 2005:
      return "CLOB";
    case -13:
      return "BFILE";
    case -103:
      return "INTERVALYM";
    case -104:
      return "INTERVALDS";
    case 2002:
    case 2003:
    case 2007:
    case 2008:
      j = getValidColumnIndex(paramInt);
      localOracleTypeADT = (OracleTypeADT)getDescription()[j].describeOtype;

      return localOracleTypeADT.getFullName();
    case 2006:
      j = getValidColumnIndex(paramInt);
      localOracleTypeADT = (OracleTypeADT)getDescription()[j].describeOtype;

      return localOracleTypeADT.getFullName();
    case 1111:
    }

    return null;
  }

  public boolean isReadOnly(int paramInt)
    throws SQLException
  {
    return false;
  }

  public boolean isWritable(int paramInt)
    throws SQLException
  {
    return true;
  }

  public boolean isDefinitelyWritable(int paramInt)
    throws SQLException
  {
    return false;
  }

  public String getColumnClassName(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    int j = getDescription()[i].describeType;

    switch (j)
    {
    case 1:
    case 8:
    case 96:
    case 999:
      return "java.lang.String";
    case 2:
    case 6:
      if ((getDescription()[i].precision != 0) && (getDescription()[i].scale == -127))
      {
        return "java.lang.Double";
      }
      return "java.math.BigDecimal";
    case 23:
    case 24:
      return "byte[]";
    case 12:
      return "java.sql.Timestamp";
    case 180:
      return "oracle.sql.TIMESTAMP";
    case 181:
      return "oracle.sql.TIMESTAMPTZ";
    case 231:
      return "oracle.sql.TIMESTAMPLTZ";
    case 182:
      return "oracle.sql.INTERVALYM";
    case 183:
      return "oracle.sql.INTERVALDS";
    case 104:
      return "oracle.sql.ROWID";
    case 113:
      return "oracle.sql.BLOB";
    case 112:
      return "oracle.sql.CLOB";
    case 114:
      return "oracle.sql.BFILE";
    case 102:
      return "OracleResultSet";
    case 109:
      Object localObject1;
      Object localObject2;
      switch (getColumnType(paramInt))
      {
      case 2003:
        return "oracle.sql.ARRAY";
      case 2007:
        return "oracle.sql.OPAQUE";
      case 2008:
        localObject1 = (OracleNamedType)getDescription()[i].describeOtype;

        localObject2 = this.connection.getJavaObjectTypeMap();

        if (localObject2 != null)
        {
          Class localClass = (Class)((Map)localObject2).get(((OracleNamedType)localObject1).getFullName());

          if (localClass != null) {
            return localClass.getName();
          }
        }
        return StructDescriptor.getJavaObjectClassName(this.connection, ((OracleNamedType)localObject1).getSchemaName(), ((OracleNamedType)localObject1).getSimpleName());
      case 2002:
        localObject1 = this.connection.getTypeMap();

        if (localObject1 != null)
        {
          localObject2 = (Class)((Map)localObject1).get(((OracleNamedType)getDescription()[i].describeOtype).getFullName());

          if (localObject2 != null) {
            return ((Class)localObject2).getName();
          }
        }
        return "oracle.sql.STRUCT";
      case 2004:
      case 2005:
      case 2006:
      }DatabaseError.throwSqlException(1);
    case 111:
      return "oracle.sql.REF";
    }

    return (String)(String)null;
  }

  public boolean isNCHAR(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    return getDescription()[i].formOfUse == 2;
  }

  Accessor[] getDescription()
    throws SQLException
  {
    return this.statement.getDescription();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleResultSetMetaData
 * JD-Core Version:    0.6.0
 */