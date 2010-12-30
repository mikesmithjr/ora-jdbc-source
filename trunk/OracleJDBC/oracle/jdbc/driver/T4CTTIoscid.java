package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIoscid extends T4CTTIfun
{
  static final int KPDUSR_CID_RESET = 1;
  static final int KPDUSR_PROXY_RESET = 2;
  static final int KPDUSR_PROXY_TKTSENT = 4;
  static final int KPDUSR_MODULE_RESET = 8;
  static final int KPDUSR_ACTION_RESET = 16;
  static final int KPDUSR_EXECID_RESET = 32;
  static final int KPDUSR_EXECSQ_RESET = 64;
  static final int KPDUSR_COLLCT_RESET = 128;
  static final int KPDUSR_CLINFO_RESET = 256;

  T4CTTIoscid(T4CMAREngine paramT4CMAREngine)
    throws IOException, SQLException
  {
    super(17, 0);

    setMarshalingEngine(paramT4CMAREngine);
    setFunCode(135);
  }

  void marshal(boolean[] paramArrayOfBoolean, String[] paramArrayOfString, int paramInt)
    throws IOException, SQLException
  {
    marshalFunHeader();

    int i = 64;

    if (paramArrayOfBoolean[0] != 0) {
      i |= 16;
    }
    if (paramArrayOfBoolean[1] != 0) {
      i |= 1;
    }
    if (paramArrayOfBoolean[2] != 0) {
      i |= 32;
    }
    if (paramArrayOfBoolean[3] != 0) {
      i |= 8;
    }

    this.meg.marshalNULLPTR();
    this.meg.marshalNULLPTR();
    this.meg.marshalUB4(i);

    byte[] arrayOfByte1 = null;

    if (paramArrayOfBoolean[1] != 0)
    {
      this.meg.marshalPTR();

      if (paramArrayOfString[1] != null)
      {
        arrayOfByte1 = this.meg.conv.StringToCharBytes(paramArrayOfString[1]);

        if (arrayOfByte1 != null)
          this.meg.marshalUB4(arrayOfByte1.length);
        else
          this.meg.marshalUB4(0L);
      }
      else {
        this.meg.marshalUB4(0L);
      }
    }
    else {
      this.meg.marshalNULLPTR();
      this.meg.marshalUB4(0L);
    }

    byte[] arrayOfByte2 = null;

    if (paramArrayOfBoolean[3] != 0)
    {
      this.meg.marshalPTR();

      if (paramArrayOfString[3] != null)
      {
        arrayOfByte2 = this.meg.conv.StringToCharBytes(paramArrayOfString[3]);

        if (arrayOfByte2 != null)
          this.meg.marshalUB4(arrayOfByte2.length);
        else
          this.meg.marshalUB4(0L);
      }
      else {
        this.meg.marshalUB4(0L);
      }
    }
    else {
      this.meg.marshalNULLPTR();
      this.meg.marshalUB4(0L);
    }

    byte[] arrayOfByte3 = null;

    if (paramArrayOfBoolean[0] != 0)
    {
      this.meg.marshalPTR();

      if (paramArrayOfString[0] != null)
      {
        arrayOfByte3 = this.meg.conv.StringToCharBytes(paramArrayOfString[0]);

        if (arrayOfByte3 != null)
          this.meg.marshalUB4(arrayOfByte3.length);
        else
          this.meg.marshalUB4(0L);
      }
      else {
        this.meg.marshalUB4(0L);
      }
    }
    else {
      this.meg.marshalNULLPTR();
      this.meg.marshalUB4(0L);
    }

    byte[] arrayOfByte4 = null;

    if (paramArrayOfBoolean[2] != 0)
    {
      this.meg.marshalPTR();

      if (paramArrayOfString[2] != null)
      {
        arrayOfByte4 = this.meg.conv.StringToCharBytes(paramArrayOfString[2]);

        if (arrayOfByte4 != null)
          this.meg.marshalUB4(arrayOfByte4.length);
        else
          this.meg.marshalUB4(0L);
      }
      else {
        this.meg.marshalUB4(0L);
      }
    }
    else {
      this.meg.marshalNULLPTR();
      this.meg.marshalUB4(0L);
    }

    this.meg.marshalUB2(0);
    this.meg.marshalUB2(paramInt);
    this.meg.marshalNULLPTR();
    this.meg.marshalUB4(0L);
    this.meg.marshalNULLPTR();
    this.meg.marshalUB4(0L);
    this.meg.marshalNULLPTR();
    this.meg.marshalUB4(0L);

    if (arrayOfByte1 != null) {
      this.meg.marshalCHR(arrayOfByte1);
    }
    if (arrayOfByte2 != null) {
      this.meg.marshalCHR(arrayOfByte2);
    }
    if (arrayOfByte3 != null) {
      this.meg.marshalCHR(arrayOfByte3);
    }
    if (arrayOfByte4 != null)
      this.meg.marshalCHR(arrayOfByte4);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIoscid
 * JD-Core Version:    0.6.0
 */