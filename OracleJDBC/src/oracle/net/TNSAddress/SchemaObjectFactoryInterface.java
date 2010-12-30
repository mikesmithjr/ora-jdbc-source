package oracle.net.TNSAddress;

public abstract interface SchemaObjectFactoryInterface {
    public static final int ADDR = 0;
    public static final int ADDR_LIST = 1;
    public static final int DESC = 2;
    public static final int DESC_LIST = 3;
    public static final int ALIAS = 4;
    public static final int SERVICE = 5;
    public static final int DB_SERVICE = 6;

    public abstract SchemaObject create(int paramInt);
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.net.TNSAddress.SchemaObjectFactoryInterface JD-Core Version: 0.6.0
 */