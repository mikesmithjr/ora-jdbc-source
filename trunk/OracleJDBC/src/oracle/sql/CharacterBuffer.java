package oracle.sql;

import java.sql.SQLException;

public final class CharacterBuffer {
    CharacterSet charSet;
    byte[] bytes;
    int next;

    public CharacterBuffer(CharacterSet charSet) {
        this.charSet = charSet;
        this.next = 0;

        this.bytes = new byte[32];
    }

    public void append(int c) throws SQLException {
        this.charSet.encode(this, c);
    }

    public byte[] getBytes() {
        return CharacterSet.useOrCopy(this.bytes, 0, this.next);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterBuffer JD-Core Version: 0.6.0
 */