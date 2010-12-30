package oracle.jdbc.driver;

import java.util.ResourceBundle;

public class Message11
  implements Message
{
  private static ResourceBundle bundle;
  private static final String messageFile = "oracle.jdbc.driver.Messages";
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:50_PDT_2005";

  public String msg(String paramString, Object paramObject)
  {
    if (bundle == null)
    {
      try
      {
        bundle = ResourceBundle.getBundle("oracle.jdbc.driver.Messages");
      }
      catch (Exception localException1)
      {
        return "Message file 'oracle.jdbc.driver.Messages' is missing.";
      }

    }

    try
    {
      if (paramObject != null) {
        return bundle.getString(paramString) + ": " + paramObject;
      }
      return bundle.getString(paramString);
    }
    catch (Exception localException2) {
    }
    return "Message [" + paramString + "] not found in '" + "oracle.jdbc.driver.Messages" + "'.";
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.Message11
 * JD-Core Version:    0.6.0
 */