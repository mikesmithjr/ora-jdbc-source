package oracle.sql;

import java.sql.SQLException;
import java.util.NoSuchElementException;

public final class CharacterWalker {
    CharacterSet charSet;
    byte[] bytes;
    int next;
    int end;
    int shiftstate;

    public CharacterWalker(CharacterSet charSet, byte[] bytes, int offset, int count) {
        this.charSet = charSet;
        this.bytes = bytes;
        this.next = offset;
        this.end = (offset + count);

        if (this.next < 0) {
            this.next = 0;
        }

        if (this.end > bytes.length) {
            this.end = bytes.length;
        }
    }

    public int nextCharacter() throws NoSuchElementException {
        try {
            return this.charSet.decode(this);
        } catch (SQLException ex) {
            throw new NoSuchElementException(ex.getMessage());
        }
        
    }

    public boolean hasMoreCharacters() {
        return this.next < this.end;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterWalker JD-Core Version: 0.6.0
 */