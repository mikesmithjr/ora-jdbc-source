package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIokeyval extends T4CTTIfun
{
  static final byte KVASET_KPDUSR = 1;
  static final byte KVACLA_KPDUSR = 2;
  private byte[] namespaceByteArr;
  private char[] charArr;
  private byte[][] attrArr;
  private int[] attrArrSize;
  private byte[][] valueArr;
  private int[] valueArrSize;
  private byte[] kvalflg;

  T4CTTIokeyval(T4CMAREngine paramT4CMAREngine)
    throws IOException, SQLException
  {
    super(17, 0);

    setMarshalingEngine(paramT4CMAREngine);
    setFunCode(154);

    this.namespaceByteArr = new byte[100];
    this.charArr = new char[100];

    this.attrArr = new byte[10][];
    this.attrArrSize = new int[10];
    this.valueArr = new byte[10][];
    this.valueArrSize = new int[10];

    this.kvalflg = new byte[10];
  }

  void marshal(Namespace paramNamespace) throws IOException, SQLException
  {
    String str1 = paramNamespace.name;
    String[] arrayOfString1 = paramNamespace.keys;
    String[] arrayOfString2 = paramNamespace.values;
    boolean bool = paramNamespace.clear;
    int i = paramNamespace.nbPairs;

    int j = str1.length() * this.meg.conv.cMaxCharSize;
    if (j > this.namespaceByteArr.length)
      this.namespaceByteArr = new byte[j];
    if (str1.length() > this.charArr.length)
      this.charArr = new char[str1.length()];
    str1.getChars(0, str1.length(), this.charArr, 0);
    int k = this.meg.conv.javaCharsToCHARBytes(this.charArr, 0, this.namespaceByteArr, 0, str1.length());

    if (i > 0)
    {
      if (i > this.attrArr.length)
      {
        this.attrArr = new byte[i][];
        this.attrArrSize = new int[i];
        this.valueArr = new byte[i][];
        this.valueArrSize = new int[i];
        this.kvalflg = new byte[i];
      }

      for (int m = 0; m < i; m++)
      {
        String str2 = arrayOfString1[m];
        String str3 = arrayOfString2[m];

        int i1 = str2.length() * this.meg.conv.cMaxCharSize;
        if ((this.attrArr[m] == null) || (this.attrArr[m].length < i1)) {
          this.attrArr[m] = new byte[i1];
        }
        int i2 = str3.length() * this.meg.conv.cMaxCharSize;
        if ((this.valueArr[m] == null) || (this.valueArr[m].length < i2)) {
          this.valueArr[m] = new byte[i2];
        }
        if (str2.length() > this.charArr.length)
          this.charArr = new char[str2.length()];
        str2.getChars(0, str2.length(), this.charArr, 0);
        this.attrArrSize[m] = this.meg.conv.javaCharsToCHARBytes(this.charArr, 0, this.attrArr[m], 0, str2.length());

        if (str3.length() > this.charArr.length)
          this.charArr = new char[str3.length()];
        str3.getChars(0, str3.length(), this.charArr, 0);
        this.valueArrSize[m] = this.meg.conv.javaCharsToCHARBytes(this.charArr, 0, this.valueArr[m], 0, str3.length());
      }

    }

    marshalFunHeader();

    this.meg.marshalPTR();
    this.meg.marshalUB4(k);
    if (i > 0)
      this.meg.marshalPTR();
    else
      this.meg.marshalNULLPTR();
    this.meg.marshalUB4(i);
    int n = 0;
    if (i > 0)
      n = 1;
    if (bool)
      n |= 2;
    this.meg.marshalUB2(n);
    this.meg.marshalNULLPTR();

    this.meg.marshalCHR(this.namespaceByteArr, 0, k);
    if (i > 0)
      this.meg.marshalKEYVAL(this.attrArr, this.attrArrSize, this.valueArr, this.valueArrSize, this.kvalflg, i);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIokeyval
 * JD-Core Version:    0.6.0
 */