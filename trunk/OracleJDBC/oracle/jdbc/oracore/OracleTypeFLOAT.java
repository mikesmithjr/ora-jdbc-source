package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.Datum;

public class OracleTypeFLOAT extends OracleType
  implements Serializable
{
  static final long serialVersionUID = 4088841548269771109L;
  int precision;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    return OracleTypeNUMBER.toNUMBER(paramObject, paramOracleConnection);
  }

  public Datum[] toDatumArray(Object paramObject, OracleConnection paramOracleConnection, long paramLong, int paramInt)
    throws SQLException
  {
    return OracleTypeNUMBER.toNUMBERArray(paramObject, paramOracleConnection, paramLong, paramInt);
  }

  public int getTypeCode()
  {
    return 6;
  }

  public void parseTDSrec(TDSReader paramTDSReader)
    throws SQLException
  {
    this.nullOffset = paramTDSReader.nullOffset;
    this.ldsOffset = paramTDSReader.ldsOffset;

    paramTDSReader.nullOffset += 1;
    paramTDSReader.ldsOffset += 1;

    this.precision = paramTDSReader.readUnsignedByte();
  }

  public int getScale()
  {
    return -127;
  }

  public int getPrecision()
  {
    return this.precision;
  }

  public int getSizeLDS(byte[] paramArrayOfByte)
  {
    if (this.sizeForLds == 0)
    {
      this.sizeForLds = Util.fdoGetSize(paramArrayOfByte, 12);
      this.alignForLds = Util.fdoGetAlign(paramArrayOfByte, 12);
    }

    return this.sizeForLds;
  }

  public int getAlignLDS(byte[] paramArrayOfByte)
  {
    if (this.sizeForLds == 0)
    {
      this.sizeForLds = Util.fdoGetSize(paramArrayOfByte, 12);
      this.alignForLds = Util.fdoGetAlign(paramArrayOfByte, 12);
    }

    return this.alignForLds;
  }

  protected Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    return OracleTypeNUMBER.numericUnpickle80rec(this.ldsOffset, this.nullOffset, paramUnpickleContext, paramInt1, paramInt2, paramMap);
  }

  protected static Object unpickle80NativeArray(UnpickleContext paramUnpickleContext, long paramLong, int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    return OracleTypeNUMBER.unpickle80NativeArray(paramUnpickleContext, paramLong, paramInt1, paramInt2, paramInt3);
  }

  protected static Object unpickle81NativeArray(PickleContext paramPickleContext, long paramLong, int paramInt1, int paramInt2)
    throws SQLException
  {
    return OracleTypeNUMBER.unpickle81NativeArray(paramPickleContext, paramLong, paramInt1, paramInt2);
  }

  protected Object toObject(byte[] paramArrayOfByte, int paramInt, Map paramMap)
    throws SQLException
  {
    return OracleTypeNUMBER.toNumericObject(paramArrayOfByte, paramInt, paramMap);
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(this.precision);
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.precision = paramObjectInputStream.readInt();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeFLOAT
 * JD-Core Version:    0.6.0
 */