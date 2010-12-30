package oracle.sql;

import java.io.PrintStream;
import java.sql.SQLException;

abstract class CharacterSetFactory
{
  public static final short DEFAULT_CHARSET = -1;
  public static final short ASCII_CHARSET = 1;
  public static final short ISO_LATIN_1_CHARSET = 31;
  public static final short UNICODE_1_CHARSET = 870;
  public static final short UNICODE_2_CHARSET = 871;

  public abstract CharacterSet make(int paramInt);

  public static void main(String[] paramArrayOfString)
  {
    CharacterSet localCharacterSet1 = CharacterSet.make(871);
    int[] arrayOfInt = { 1, 31, 870, 871 };

    for (int i = 0; i < arrayOfInt.length; i++)
    {
      CharacterSet localCharacterSet2 = CharacterSet.make(arrayOfInt[i]);

      String str1 = "longlonglonglong";
      str1 = str1 + str1 + str1 + str1;
      str1 = str1 + str1 + str1 + str1;
      str1 = str1 + str1 + str1 + str1;
      str1 = str1 + str1 + str1 + str1;

      String[] arrayOfString = { "abc", "ab?c", "XYZ", str1 };

      for (int j = 0; j < arrayOfString.length; j++)
      {
        String str2 = arrayOfString[j];
        String str3 = str2;

        if (str2.length() > 16)
        {
          str3 = str3.substring(0, 16) + "...";
        }

        System.out.println("testing " + localCharacterSet2 + " against <" + str3 + ">");

        int k = 1;
        try
        {
          byte[] arrayOfByte1 = localCharacterSet2.convertWithReplacement(str2);
          String str4 = localCharacterSet2.toStringWithReplacement(arrayOfByte1, 0, arrayOfByte1.length);

          arrayOfByte1 = localCharacterSet2.convert(str4);

          String str5 = localCharacterSet2.toString(arrayOfByte1, 0, arrayOfByte1.length);

          if (!str4.equals(str5))
          {
            System.out.println("    FAILED roundTrip " + str5);

            k = 0;
          }
          Object localObject;
          if (localCharacterSet2.isLossyFrom(localCharacterSet1))
          {
            try
            {
              byte[] arrayOfByte2 = localCharacterSet2.convert(str2);
              localObject = localCharacterSet2.toString(arrayOfByte2, 0, arrayOfByte2.length);

              if (!((String)localObject).equals(str5))
              {
                System.out.println("    FAILED roundtrip, no throw");
              }
            }
            catch (SQLException localSQLException) {
            }
          }
          else {
            if (!str5.equals(str2))
            {
              System.out.println("    FAILED roundTrip " + str5);

              k = 0;
            }

            byte[] arrayOfByte3 = localCharacterSet1.convert(str2);
            localObject = localCharacterSet2.convert(localCharacterSet1, arrayOfByte3, 0, arrayOfByte3.length);

            String str6 = localCharacterSet2.toString(localObject, 0, localObject.length);

            if (!str6.equals(str2))
            {
              System.out.println("    FAILED withoutReplacement " + str6);

              k = 0;
            }
          }
        }
        catch (Exception localException)
        {
          System.out.println("    FAILED with Exception " + localException);
        }

        if (k == 0)
          continue;
        System.out.println("    PASSED " + (localCharacterSet2.isLossyFrom(localCharacterSet1) ? "LOSSY" : ""));
      }
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSetFactory
 * JD-Core Version:    0.6.0
 */