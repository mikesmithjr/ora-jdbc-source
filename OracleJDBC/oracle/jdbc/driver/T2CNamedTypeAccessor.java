package oracle.jdbc.driver;

import java.sql.SQLException;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.oracore.OracleNamedType;
import oracle.jdbc.oracore.OracleType;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.Datum;
import oracle.sql.JAVA_STRUCT;
import oracle.sql.OPAQUE;
import oracle.sql.OpaqueDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

class T2CNamedTypeAccessor extends NamedTypeAccessor
{
  int columnNumber = 0;

  T2CNamedTypeAccessor(OracleStatement paramOracleStatement, String paramString, short paramShort, int paramInt1, boolean paramBoolean, int paramInt2)
    throws SQLException
  {
    super(paramOracleStatement, paramString, paramShort, paramInt1, paramBoolean);

    this.columnNumber = paramInt2;
    this.isColumnNumberAware = true;
  }

  T2CNamedTypeAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort, String paramString)
    throws SQLException
  {
    super(paramOracleStatement, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, paramString);
  }

  T2CNamedTypeAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort, String paramString, OracleType paramOracleType)
    throws SQLException
  {
    super(paramOracleStatement, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, paramString, paramOracleType);
  }

  OracleType otypeFromName(String paramString)
    throws SQLException
  {
    if ("SYS.ANYDATA".equals(paramString)) {
      return StructDescriptor.createDescriptor(paramString, this.statement.connection).getPickler();
    }

    return super.otypeFromName(paramString);
  }

  Object getAnyDataEmbeddedObject(int paramInt)
    throws SQLException
  {
    byte[] arrayOfByte1 = getBytes(paramInt);

    if (arrayOfByte1 == null) {
      return null;
    }
    Object localObject1 = null;

    int[] arrayOfInt = new int[2];

    String str = getAllAnydataTypeInfo(this.statement.c_state, paramInt, this.columnNumber, arrayOfInt);

    int i = arrayOfInt[0];
    OracleConnection localOracleConnection = ((OracleNamedType)this.internalOtype).getConnection();

    switch (i)
    {
    case 2003:
      localObject1 = ArrayDescriptor.createDescriptor(str, localOracleConnection);

      break;
    case 2002:
    case 2008:
      localObject1 = StructDescriptor.createDescriptor(str, localOracleConnection);

      break;
    case 2007:
      localObject1 = OpaqueDescriptor.createDescriptor(str, localOracleConnection);

      break;
    case 2004:
    case 2005:
    case 2006:
    default:
      DatabaseError.throwSqlException(23);
    }

    Object localObject2 = null;

    int j = arrayOfInt[1];

    int k = arrayOfByte1.length - j;
    byte[] arrayOfByte2 = new byte[k];

    System.arraycopy(arrayOfByte1, j, arrayOfByte2, 0, k);

    switch (((TypeDescriptor)localObject1).getTypeCode())
    {
    case 2003:
      localObject2 = new ARRAY((ArrayDescriptor)localObject1, arrayOfByte2, this.statement.connection);

      break;
    case 2002:
      localObject2 = new STRUCT((StructDescriptor)localObject1, arrayOfByte2, this.statement.connection);

      break;
    case 2007:
      localObject2 = new OPAQUE((OpaqueDescriptor)localObject1, arrayOfByte2, this.statement.connection);

      break;
    case 2008:
      localObject2 = new JAVA_STRUCT((StructDescriptor)localObject1, arrayOfByte2, this.statement.connection);

      break;
    case 2004:
    case 2005:
    case 2006:
    default:
      DatabaseError.throwSqlException(4);
    }

    return ((Datum)localObject2).toJdbc();
  }

  native String getAllAnydataTypeInfo(long paramLong, int paramInt1, int paramInt2, int[] paramArrayOfInt);
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T2CNamedTypeAccessor
 * JD-Core Version:    0.6.0
 */