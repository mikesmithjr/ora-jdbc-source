package oracle.jdbc.xa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import oracle.jdbc.driver.OracleLog;

public class OracleMultiPhaseArgs
{
  int action = 0;
  int nsites = 0;
  Vector dbLinks = null;
  Vector xids = null;

  public OracleMultiPhaseArgs()
  {
  }

  public OracleMultiPhaseArgs(int paramInt1, int paramInt2, Vector paramVector1, Vector paramVector2)
  {
    if (paramInt2 <= 1)
    {
      this.action = 0;
      this.nsites = 0;
      this.dbLinks = null;
      this.xids = null;
    }
    else if ((!paramVector1.isEmpty()) && (!paramVector2.isEmpty()) && (paramVector2.size() == paramInt2) && (paramVector1.size() == 3 * paramInt2))
    {
      this.action = paramInt1;
      this.nsites = paramInt2;
      this.xids = paramVector1;
      this.dbLinks = paramVector2;
    }
  }

  public OracleMultiPhaseArgs(byte[] paramArrayOfByte)
  {
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    DataInputStream localDataInputStream = new DataInputStream(localByteArrayInputStream);

    this.xids = new Vector();
    this.dbLinks = new Vector();
    try
    {
      this.action = localDataInputStream.readInt();
      this.nsites = localDataInputStream.readInt();

      int i = localDataInputStream.readInt();
      int j = localDataInputStream.readInt();
      byte[] arrayOfByte1 = new byte[j];
      int k = localDataInputStream.read(arrayOfByte1, 0, j);

      for (int m = 0; m < this.nsites; m++)
      {
        int n = localDataInputStream.readInt();
        byte[] arrayOfByte2 = new byte[n];
        int i1 = localDataInputStream.read(arrayOfByte2, 0, n);

        this.xids.addElement(new Integer(i));
        this.xids.addElement(arrayOfByte1);
        this.xids.addElement(arrayOfByte2);

        String str = localDataInputStream.readUTF();

        this.dbLinks.addElement(str);
      }
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public byte[] toByteArray()
  {
    return toByteArrayOS().toByteArray();
  }

  public ByteArrayOutputStream toByteArrayOS()
  {
    Object localObject = null;
    int i = 0;

    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    try
    {
      localDataOutputStream.writeInt(this.action);
      localDataOutputStream.writeInt(this.nsites);

      for (int j = 0; j < this.nsites; j++)
      {
        String str = (String)this.dbLinks.elementAt(j);
        int k = ((Integer)this.xids.elementAt(j * 3)).intValue();
        byte[] arrayOfByte1 = (byte[])this.xids.elementAt(j * 3 + 1);
        byte[] arrayOfByte2 = (byte[])this.xids.elementAt(j * 3 + 2);

        if (j == 0)
        {
          i = k;
          localObject = arrayOfByte1;

          localDataOutputStream.writeInt(k);
          localDataOutputStream.writeInt(arrayOfByte1.length);
          localDataOutputStream.write(arrayOfByte1, 0, arrayOfByte1.length);
        }

        localDataOutputStream.writeInt(arrayOfByte2.length);
        localDataOutputStream.write(arrayOfByte2, 0, arrayOfByte2.length);
        localDataOutputStream.writeUTF(str);
      }
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }

    return localByteArrayOutputStream;
  }

  public int getAction()
  {
    return this.action;
  }

  public int getnsite()
  {
    return this.nsites;
  }

  public Vector getdbLinks()
  {
    return this.dbLinks;
  }

  public Vector getXids()
  {
    return this.xids;
  }

  public void printMPArgs()
  {
    OracleLog.print(this, 256, 16, 64, "-------printMPArgs entry-------");

    OracleLog.print(this, 256, 16, 64, "  action = " + this.action);

    OracleLog.print(this, 256, 16, 64, "  nsites = " + this.nsites);

    for (int i = 0; i < this.nsites; i++)
    {
      String str = (String)this.dbLinks.elementAt(i);
      int j = ((Integer)this.xids.elementAt(i * 3)).intValue();
      byte[] arrayOfByte1 = (byte[])this.xids.elementAt(i * 3 + 1);
      byte[] arrayOfByte2 = (byte[])this.xids.elementAt(i * 3 + 2);

      OracleLog.print(this, 256, 16, 64, "  fmtid  [" + i + "] = " + j);

      OracleLog.print(this, 256, 16, 64, "  gtrid  [" + i + "] = ");

      printByteArray(arrayOfByte1);
      OracleLog.print(this, 256, 16, 64, "  bqual  [" + i + "] = ");

      printByteArray(arrayOfByte2);
      OracleLog.print(this, 256, 16, 64, "  dblink [" + i + "] = " + str);
    }

    OracleLog.print(this, 256, 16, 64, "-------printMPArgs return-------");
  }

  private void printByteArray(byte[] paramArrayOfByte)
  {
    StringBuffer localStringBuffer = new StringBuffer();

    for (int i = 0; i < paramArrayOfByte.length; i++) {
      localStringBuffer.append(paramArrayOfByte[i] + " ");
    }
    OracleLog.print(this, 256, 16, 64, "         " + localStringBuffer.toString());
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.xa.OracleMultiPhaseArgs
 * JD-Core Version:    0.6.0
 */