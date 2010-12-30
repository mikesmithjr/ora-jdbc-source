package oracle.net.TNSAddress;

public class SOException extends Throwable {
    public String error = null;

    public SOException() {
    }

    public SOException(String paramString) {
        super(paramString);
        this.error = paramString;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.net.TNSAddress.SOException JD-Core Version: 0.6.0
 */