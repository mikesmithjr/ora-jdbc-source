package oracle.jdbc.oracore;

import java.sql.SQLException;
import java.util.Vector;
import oracle.jdbc.driver.DatabaseError;

public class TDSReader
{
  static final int KOPT_NONE_FINAL_TYPE = 1;
  static final int KOPT_JAVA_OBJECT = 2;
  int nullOffset;
  int ldsOffset;
  long fixedDataSize;
  Vector patches;
  byte[] tds;
  int beginIndex;
  int index;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  TDSReader(byte[] paramArrayOfByte, long paramLong)
  {
    this.nullOffset = 0;
    this.ldsOffset = 0;
    this.fixedDataSize = 0L;
    this.patches = null;

    this.tds = paramArrayOfByte;
    this.beginIndex = (int)paramLong;
    this.index = (int)paramLong;
  }

  void skipBytes(int paramInt)
    throws SQLException
  {
    this.index += paramInt;
  }

  void checkNextByte(byte paramByte)
    throws SQLException
  {
    try
    {
      if (paramByte != this.tds[this.index]) {
        DatabaseError.throwSqlException(47, "parseTDS");
      }

    }
    finally
    {
      this.index += 1;
    }
  }

  byte readByte()
    throws SQLException
  {
    try
    {
      int i = this.tds[this.index];
      return i; } finally { this.index += 1; } throw localObject;
  }

  int readUnsignedByte()
    throws SQLException
  {
    try
    {
      int i = this.tds[this.index] & 0xFF;
      return i; } finally { this.index += 1; } throw localObject;
  }

  short readShort()
    throws SQLException
  {
    try
    {
      int i = (short)((this.tds[this.index] & 0xFF) * 256 + (this.tds[(this.index + 1)] & 0xFF));
      return i; } finally { this.index += 2; } throw localObject;
  }

  long readLong()
    throws SQLException
  {
    try
    {
      long l = (((this.tds[this.index] & 0xFF) * 256 + (this.tds[(this.index + 1)] & 0xFF)) * 256 + (this.tds[(this.index + 2)] & 0xFF)) * 256 + (this.tds[(this.index + 3)] & 0xFF);
      return l; } finally { this.index += 4; } throw localObject;
  }

  void addNormalPatch(long paramLong, byte paramByte, OracleType paramOracleType)
    throws SQLException
  {
    addPatch(new TDSPatch(0, paramOracleType, paramLong, paramByte));
  }

  void addSimplePatch(long paramLong, OracleType paramOracleType)
    throws SQLException
  {
    addPatch(new TDSPatch(1, paramOracleType, paramLong, 0));
  }

  void addPatch(TDSPatch paramTDSPatch) throws SQLException
  {
    if (this.patches == null) {
      this.patches = new Vector(5);
    }
    this.patches.addElement(paramTDSPatch);
  }

  long moveToPatchPos(TDSPatch paramTDSPatch) throws SQLException
  {
    long l = paramTDSPatch.getPosition();

    if (this.beginIndex + l > this.tds.length) {
      DatabaseError.throwSqlException(47, "parseTDS");
    }

    skip_to(l);

    return l;
  }

  TDSPatch getNextPatch()
    throws SQLException
  {
    TDSPatch localTDSPatch = null;

    if (this.patches != null)
    {
      if (this.patches.size() > 0)
      {
        localTDSPatch = (TDSPatch)this.patches.firstElement();

        this.patches.removeElementAt(0);
      }
    }

    return localTDSPatch;
  }

  void skip_to(long paramLong)
  {
    this.index = (this.beginIndex + (int)paramLong);
  }

  long offset()
    throws SQLException
  {
    return this.index - this.beginIndex;
  }

  long absoluteOffset() throws SQLException
  {
    return this.index;
  }

  byte[] tds() throws SQLException
  {
    return this.tds;
  }

  boolean isJavaObject(int paramInt, byte paramByte)
  {
    return (paramInt >= 3) && ((paramByte & 0x2) != 0);
  }

  boolean isFinalType(int paramInt, byte paramByte)
  {
    return (paramInt >= 3) && ((paramByte & 0x1) == 0);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.TDSReader
 * JD-Core Version:    0.6.0
 */