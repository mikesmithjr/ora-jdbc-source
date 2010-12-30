package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.oracore.OracleType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.sql.Datum;
import oracle.sql.REF;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

class RefTypeAccessor extends TypeAccessor
{
  RefTypeAccessor(OracleStatement paramOracleStatement, String paramString, short paramShort, int paramInt, boolean paramBoolean)
    throws SQLException
  {
    init(paramOracleStatement, 111, 111, paramShort, paramBoolean);
    initForDataAccess(paramInt, 0, paramString);
  }

  RefTypeAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort, String paramString)
    throws SQLException
  {
    init(paramOracleStatement, 111, 111, paramShort, false);
    initForDescribe(111, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, paramString);

    initForDataAccess(0, paramInt1, paramString);
  }

  RefTypeAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort, String paramString, OracleType paramOracleType)
    throws SQLException
  {
    init(paramOracleStatement, 111, 111, paramShort, false);

    this.describeOtype = paramOracleType;

    initForDescribe(111, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, paramString);

    this.internalOtype = paramOracleType;

    initForDataAccess(0, paramInt1, paramString);
  }

  OracleType otypeFromName(String paramString) throws SQLException
  {
    if (!this.outBind) {
      return TypeDescriptor.getTypeDescriptor(paramString, this.statement.connection).getPickler();
    }

    return StructDescriptor.createDescriptor(paramString, this.statement.connection).getOracleTypeADT();
  }

  void initForDataAccess(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    super.initForDataAccess(paramInt1, paramInt2, paramString);

    this.byteLength = this.statement.connection.refTypeAccessorByteLen;
  }

  REF getREF(int paramInt)
    throws SQLException
  {
    REF localREF = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      byte[] arrayOfByte = pickledBytes(paramInt);
      OracleTypeADT localOracleTypeADT = (OracleTypeADT)this.internalOtype;

      localREF = new REF(localOracleTypeADT.getFullName(), this.statement.connection, arrayOfByte);
    }

    return localREF;
  }

  Object getObject(int paramInt)
    throws SQLException
  {
    return getREF(paramInt);
  }

  Datum getOracleObject(int paramInt)
    throws SQLException
  {
    return getREF(paramInt);
  }

  Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    return getREF(paramInt);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.RefTypeAccessor
 * JD-Core Version:    0.6.0
 */