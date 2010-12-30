package oracle.jdbc.internal;

public abstract interface ClientDataSupport {
    public abstract Object getClientData(Object paramObject);

    public abstract Object setClientData(Object paramObject1, Object paramObject2);

    public abstract Object removeClientData(Object paramObject);
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.internal.ClientDataSupport JD-Core Version: 0.6.0
 */