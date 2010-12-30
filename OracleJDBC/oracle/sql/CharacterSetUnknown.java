package oracle.sql;

import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;

class CharacterSetUnknown extends CharacterSet
  implements CharacterRepConstants
{
  CharacterSetUnknown(int paramInt)
  {
    super(paramInt);

    this.rep = (1024 + paramInt);
  }

  public boolean isLossyFrom(CharacterSet paramCharacterSet)
  {
    return paramCharacterSet.getOracleId() != getOracleId();
  }

  public boolean isConvertibleFrom(CharacterSet paramCharacterSet)
  {
    return paramCharacterSet.getOracleId() == getOracleId();
  }

  public String toStringWithReplacement(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return "???";
  }

  public String toString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    failCharsetUnknown(this);

    return null;
  }

  public byte[] convert(String paramString) throws SQLException
  {
    failCharsetUnknown(this);

    return null;
  }

  public byte[] convertWithReplacement(String paramString)
  {
    return new byte[0];
  }

  public byte[] convert(CharacterSet paramCharacterSet, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    if (paramCharacterSet.getOracleId() == getOracleId())
    {
      return useOrCopy(paramArrayOfByte, paramInt1, paramInt2);
    }

    failCharsetUnknown(this);

    return null;
  }

  int decode(CharacterWalker paramCharacterWalker) throws SQLException
  {
    failCharsetUnknown(this);

    return 0;
  }

  void encode(CharacterBuffer paramCharacterBuffer, int paramInt) throws SQLException
  {
    failCharsetUnknown(this);
  }

  static void failCharsetUnknown(CharacterSet paramCharacterSet)
    throws SQLException
  {
    DatabaseError.throwSqlException(56, paramCharacterSet);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSetUnknown
 * JD-Core Version:    0.6.0
 */