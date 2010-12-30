package oracle.jdbc.oracore;

import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;

public class TDSPatch
{
  static final int S_NORMAL_PATCH = 0;
  static final int S_SIMPLE_PATCH = 1;
  int typeId;
  OracleType owner;
  long position;
  int uptCode;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  public TDSPatch(int paramInt1, OracleType paramOracleType, long paramLong, int paramInt2)
    throws SQLException
  {
    this.typeId = paramInt1;
    this.owner = paramOracleType;
    this.position = paramLong;
    this.uptCode = paramInt2;
  }

  int getType() throws SQLException
  {
    return this.typeId;
  }

  OracleNamedType getOwner() throws SQLException
  {
    return (OracleNamedType)this.owner;
  }

  long getPosition() throws SQLException
  {
    return this.position;
  }

  byte getUptTypeCode() throws SQLException
  {
    return (byte)this.uptCode;
  }

  void apply(OracleType paramOracleType) throws SQLException
  {
    apply(paramOracleType, -1);
  }

  void apply(OracleType paramOracleType, int paramInt)
    throws SQLException
  {
    Object localObject;
    OracleNamedType localOracleNamedType;
    if (this.typeId == 0)
    {
      localObject = (OracleTypeUPT)this.owner;

      ((OracleTypeUPT)localObject).realType = ((OracleTypeADT)paramOracleType);

      if ((paramOracleType instanceof OracleNamedType))
      {
        localOracleNamedType = (OracleNamedType)paramOracleType;

        localOracleNamedType.setParent(((OracleTypeUPT)localObject).getParent());
        localOracleNamedType.setOrder(((OracleTypeUPT)localObject).getOrder());
      }
    }
    else if (this.typeId == 1)
    {
      localObject = (OracleTypeCOLLECTION)this.owner;

      ((OracleTypeCOLLECTION)localObject).opcode = paramInt;
      ((OracleTypeCOLLECTION)localObject).elementType = paramOracleType;

      if ((paramOracleType instanceof OracleNamedType))
      {
        localOracleNamedType = (OracleNamedType)paramOracleType;

        localOracleNamedType.setParent((OracleTypeADT)localObject);
        localOracleNamedType.setOrder(1);
      }
    }
    else {
      DatabaseError.throwSqlException(1);
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.TDSPatch
 * JD-Core Version:    0.6.0
 */