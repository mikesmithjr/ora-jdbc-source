package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.oracore.OracleType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.Datum;
import oracle.sql.JAVA_STRUCT;
import oracle.sql.OPAQUE;
import oracle.sql.OpaqueDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

class NamedTypeAccessor extends TypeAccessor
{
  NamedTypeAccessor(OracleStatement paramOracleStatement, String paramString, short paramShort, int paramInt, boolean paramBoolean)
    throws SQLException
  {
    init(paramOracleStatement, 109, 109, paramShort, paramBoolean);
    initForDataAccess(paramInt, 0, paramString);
  }

  NamedTypeAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort, String paramString)
    throws SQLException
  {
    init(paramOracleStatement, 109, 109, paramShort, false);
    initForDescribe(109, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, paramString);

    initForDataAccess(0, paramInt1, paramString);
  }

  NamedTypeAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort, String paramString, OracleType paramOracleType)
    throws SQLException
  {
    init(paramOracleStatement, 109, 109, paramShort, false);

    this.describeOtype = paramOracleType;

    initForDescribe(109, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, paramString);

    this.internalOtype = paramOracleType;

    initForDataAccess(0, paramInt1, paramString);
  }

  OracleType otypeFromName(String paramString) throws SQLException
  {
    if (!this.outBind) {
      return TypeDescriptor.getTypeDescriptor(paramString, this.statement.connection).getPickler();
    }
    if (this.externalType == 2003) {
      return ArrayDescriptor.createDescriptor(paramString, this.statement.connection).getOracleTypeCOLLECTION();
    }
    if (this.externalType == 2007) {
      return OpaqueDescriptor.createDescriptor(paramString, this.statement.connection).getPickler();
    }

    return StructDescriptor.createDescriptor(paramString, this.statement.connection).getOracleTypeADT();
  }

  void initForDataAccess(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    super.initForDataAccess(paramInt1, paramInt2, paramString);

    this.byteLength = this.statement.connection.namedTypeAccessorByteLen;
  }

  Object getObject(int paramInt)
    throws SQLException
  {
    return getObject(paramInt, this.statement.connection.getTypeMap());
  }

  Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    if (this.rowSpaceIndicator == null) {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      Datum localDatum;
      if (this.externalType == 0)
      {
        localDatum = getOracleObject(paramInt);

        if (localDatum == null) {
          return null;
        }
        if ((localDatum instanceof STRUCT)) {
          return ((STRUCT)localDatum).toJdbc(paramMap);
        }
        if ((localDatum instanceof OPAQUE)) {
          return ((OPAQUE)localDatum).toJdbc(paramMap);
        }
        return localDatum.toJdbc();
      }

      switch (this.externalType)
      {
      case 2008:
        paramMap = null;
      case 2000:
      case 2002:
      case 2003:
      case 2007:
        localDatum = getOracleObject(paramInt);

        if (localDatum == null) {
          return null;
        }
        if ((localDatum instanceof STRUCT)) {
          return ((STRUCT)localDatum).toJdbc(paramMap);
        }
        return localDatum.toJdbc();
      case 2001:
      case 2004:
      case 2005:
      case 2006:
      }

      DatabaseError.throwSqlException(4);

      return null;
    }

    return null;
  }

  Datum getOracleObject(int paramInt)
    throws SQLException
  {
    Object localObject = null;

    if (this.rowSpaceIndicator == null) {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      byte[] arrayOfByte = pickledBytes(paramInt);

      if ((arrayOfByte == null) || (arrayOfByte.length == 0)) {
        return null;
      }
      PhysicalConnection localPhysicalConnection = this.statement.connection;
      OracleTypeADT localOracleTypeADT = (OracleTypeADT)this.internalOtype;
      TypeDescriptor localTypeDescriptor = TypeDescriptor.getTypeDescriptor(localOracleTypeADT.getFullName(), localPhysicalConnection, arrayOfByte, 0L);

      switch (localTypeDescriptor.getTypeCode())
      {
      case 2003:
        localObject = new ARRAY((ArrayDescriptor)localTypeDescriptor, arrayOfByte, localPhysicalConnection);

        break;
      case 2002:
        localObject = new STRUCT((StructDescriptor)localTypeDescriptor, arrayOfByte, localPhysicalConnection);

        break;
      case 2007:
        localObject = new OPAQUE((OpaqueDescriptor)localTypeDescriptor, arrayOfByte, localPhysicalConnection);

        break;
      case 2008:
        localObject = new JAVA_STRUCT((StructDescriptor)localTypeDescriptor, arrayOfByte, localPhysicalConnection);

        break;
      case 2004:
      case 2005:
      case 2006:
      default:
        DatabaseError.throwSqlException(1);
      }

    }

    return (Datum)localObject;
  }

  ARRAY getARRAY(int paramInt)
    throws SQLException
  {
    return (ARRAY)getOracleObject(paramInt);
  }

  STRUCT getSTRUCT(int paramInt)
    throws SQLException
  {
    return (STRUCT)getOracleObject(paramInt);
  }

  OPAQUE getOPAQUE(int paramInt)
    throws SQLException
  {
    return (OPAQUE)getOracleObject(paramInt);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.NamedTypeAccessor
 * JD-Core Version:    0.6.0
 */