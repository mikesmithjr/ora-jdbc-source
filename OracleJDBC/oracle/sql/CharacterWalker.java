package oracle.sql;

import java.sql.SQLException;
import java.util.NoSuchElementException;

public final class CharacterWalker
{
  CharacterSet charSet;
  byte[] bytes;
  int next;
  int end;
  int shiftstate;

  public CharacterWalker(CharacterSet paramCharacterSet, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.charSet = paramCharacterSet;
    this.bytes = paramArrayOfByte;
    this.next = paramInt1;
    this.end = (paramInt1 + paramInt2);

    if (this.next < 0)
    {
      this.next = 0;
    }

    if (this.end > paramArrayOfByte.length)
    {
      this.end = paramArrayOfByte.length;
    }
  }

  public int nextCharacter()
    throws NoSuchElementException
  {
    try
    {
      return this.charSet.decode(this);
    }
    catch (SQLException localSQLException) {
    }
    throw new NoSuchElementException(localSQLException.getMessage());
  }

  public boolean hasMoreCharacters()
  {
    return this.next < this.end;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterWalker
 * JD-Core Version:    0.6.0
 */