package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIfob extends T4CTTIMsg {
    T4CTTIfob(T4CMAREngine _mrengine) throws IOException, SQLException {
        super((byte) 19);
        setMarshalingEngine(_mrengine);
    }

    void marshal() throws IOException, SQLException {
        this.meg.marshalUB1((short) 19);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIfob JD-Core Version: 0.6.0
 */