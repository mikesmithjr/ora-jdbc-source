package oracle.jdbc.driver;

import java.sql.SQLException;
import oracle.jdbc.oracore.OracleType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCHAR;
import oracle.jdbc.oracore.OracleTypeFLOAT;
import oracle.jdbc.oracore.OracleTypeNUMBER;
import oracle.jdbc.oracore.OracleTypeRAW;
import oracle.jdbc.oracore.OracleTypeREF;
import oracle.sql.StructDescriptor;

public class StructMetaData
  implements oracle.jdbc.internal.StructMetaData
{
  StructDescriptor descriptor;
  OracleTypeADT otype;
  OracleType[] types;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  public StructMetaData(StructDescriptor paramStructDescriptor)
    throws SQLException
  {
    if (paramStructDescriptor == null) {
      DatabaseError.throwSqlException(1, "illegal operation: descriptor is null");
    }

    this.descriptor = paramStructDescriptor;
    this.otype = paramStructDescriptor.getOracleTypeADT();
    this.types = this.otype.getAttrTypes();
  }

  public int getColumnCount()
    throws SQLException
  {
    return this.types.length;
  }

  public boolean isAutoIncrement(int paramInt)
    throws SQLException
  {
    return false;
  }

  public boolean isSearchable(int paramInt)
    throws SQLException
  {
    return false;
  }

  public boolean isCurrency(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    return ((this.types[i] instanceof OracleTypeNUMBER)) || ((this.types[i] instanceof OracleTypeFLOAT));
  }

  public boolean isCaseSensitive(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    return this.types[i] instanceof OracleTypeCHAR;
  }

  public int isNullable(int paramInt)
    throws SQLException
  {
    return 1;
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

    if ((this.types[i] instanceof OracleTypeCHAR))
      return ((OracleTypeCHAR)this.types[i]).getLength();
    if ((this.types[i] instanceof OracleTypeRAW)) {
      return ((OracleTypeRAW)this.types[i]).getLength();
    }
    return 0;
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

    return this.otype.getAttributeName(paramInt);
  }

  public String getSchemaName(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    if ((this.types[i] instanceof OracleTypeADT)) {
      return ((OracleTypeADT)this.types[i]).getSchemaName();
    }
    return "";
  }

  public int getPrecision(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    return this.types[i].getPrecision();
  }

  public int getScale(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    return this.types[i].getScale();
  }

  public String getTableName(int paramInt)
    throws SQLException
  {
    return null;
  }

  public String getCatalogName(int paramInt)
    throws SQLException
  {
    return null;
  }

  public int getColumnType(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    return this.types[i].getTypeCode();
  }

  public String getColumnTypeName(int paramInt)
    throws SQLException
  {
    int i = getColumnType(paramInt);
    int j = getValidColumnIndex(paramInt);

    switch (i)
    {
    case 12:
      return "VARCHAR";
    case 1:
      return "CHAR";
    case -2:
      return "RAW";
    case 6:
      return "FLOAT";
    case 2:
      return "NUMBER";
    case 8:
      return "DOUBLE";
    case 3:
      return "DECIMAL";
    case 100:
      return "BINARY_FLOAT";
    case 101:
      return "BINARY_DOUBLE";
    case 91:
      return "DATE";
    case -104:
      return "INTERVALDS";
    case -103:
      return "INTERVALYM";
    case 93:
      return "TIMESTAMP";
    case -101:
      return "TIMESTAMPTZ";
    case -102:
      return "TIMESTAMPLTZ";
    case 2004:
      return "BLOB";
    case 2005:
      return "CLOB";
    case -13:
      return "BFILE";
    case 2002:
    case 2003:
    case 2007:
    case 2008:
      return ((OracleTypeADT)this.types[j]).getFullName();
    case 2006:
      return "REF " + ((OracleTypeREF)this.types[j]).getFullName();
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
    return false;
  }

  public boolean isDefinitelyWritable(int paramInt)
    throws SQLException
  {
    return false;
  }

  public String getColumnClassName(int paramInt)
    throws SQLException
  {
    int i = getColumnType(paramInt);

    switch (i)
    {
    case 1:
    case 12:
      return "java.lang.String";
    case -2:
      return "byte[]";
    case 2:
    case 3:
    case 6:
    case 8:
      return "java.math.BigDecimal";
    case 91:
      return "java.sql.Timestamp";
    case -103:
      return "oracle.sql.INTERVALYM";
    case -104:
      return "oracle.sql.INTERVALDS";
    case 93:
      return "oracle.sql.TIMESTAMP";
    case -101:
      return "oracle.sql.TIMESTAMPTZ";
    case -102:
      return "oracle.sql.TIMESTAMPLTZ";
    case 2004:
      return "oracle.sql.BLOB";
    case 2005:
      return "oracle.sql.CLOB";
    case -13:
      return "oracle.sql.BFILE";
    case 2002:
    case 2008:
      return "oracle.sql.STRUCT";
    case 2007:
      return "oracle.sql.OPAQUE";
    case 2003:
      return "oracle.sql.ARRAY";
    case 2006:
      return "oracle.sql.REF";
    case 1111:
    }

    return null;
  }

  public String getOracleColumnClassName(int paramInt)
    throws SQLException
  {
    int i = getColumnType(paramInt);

    switch (i)
    {
    case 1:
    case 12:
      return "CHAR";
    case -2:
      return "RAW";
    case 2:
    case 3:
    case 6:
    case 8:
      return "NUMBER";
    case 91:
      return "DATE";
    case -103:
      return "INTERVALYM";
    case -104:
      return "INTERVALDS";
    case 93:
      return "TIMESTAMP";
    case -101:
      return "TIMESTAMPTZ";
    case -102:
      return "TIMESTAMPLTZ";
    case 2004:
      return "BLOB";
    case 2005:
      return "CLOB";
    case -13:
      return "BFILE";
    case 2002:
      return "STRUCT";
    case 2008:
      return "JAVA_STRUCT";
    case 2007:
      return "OPAQUE";
    case 2003:
      return "ARRAY";
    case 2006:
      return "REF";
    case 1111:
    }

    return null;
  }

  public int getLocalColumnCount()
    throws SQLException
  {
    return this.descriptor.getLocalAttributeCount();
  }

  public boolean isInherited(int paramInt)
    throws SQLException
  {
    return paramInt <= getColumnCount() - getLocalColumnCount();
  }

  public String getAttributeJavaName(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    return this.descriptor.getAttributeJavaName(i);
  }

  private int getValidColumnIndex(int paramInt)
    throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (i >= this.types.length)) {
      DatabaseError.throwSqlException(3, "getValidColumnIndex");
    }

    return i;
  }

  public boolean isNCHAR(int paramInt)
    throws SQLException
  {
    int i = getValidColumnIndex(paramInt);

    return this.types[i].isNCHAR();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.StructMetaData
 * JD-Core Version:    0.6.0
 */