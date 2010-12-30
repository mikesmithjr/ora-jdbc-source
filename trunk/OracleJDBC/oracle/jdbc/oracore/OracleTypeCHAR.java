package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.CHAR;
import oracle.sql.CharacterSet;
import oracle.sql.Datum;

public class OracleTypeCHAR extends OracleType
  implements Serializable
{
  static final long serialVersionUID = -6899444518695804629L;
  int form;
  int charset;
  int length;
  int characterSemantic;
  private transient OracleConnection connection;
  private short pickleCharaterSetId;
  private transient CharacterSet pickleCharacterSet;
  private short pickleNcharCharacterSet;
  static final int SQLCS_IMPLICIT = 1;
  static final int SQLCS_NCHAR = 2;
  static final int SQLCS_EXPLICIT = 3;
  static final int SQLCS_FLEXIBLE = 4;
  static final int SQLCS_LIT_NULL = 5;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  protected OracleTypeCHAR()
  {
  }

  public OracleTypeCHAR(OracleConnection paramOracleConnection)
  {
    this.form = 0;
    this.charset = 0;
    this.length = 0;
    this.connection = paramOracleConnection;
    this.pickleCharaterSetId = 0;
    this.pickleNcharCharacterSet = 0;
    this.pickleCharacterSet = null;
    try
    {
      this.pickleCharaterSetId = this.connection.getStructAttrCsId();
    }
    catch (SQLException localSQLException)
    {
      this.pickleCharaterSetId = -1;
    }

    this.pickleCharacterSet = CharacterSet.make(this.pickleCharaterSetId);
  }

  protected OracleTypeCHAR(OracleConnection paramOracleConnection, int paramInt)
  {
    super(paramInt);

    this.form = 0;
    this.charset = 0;
    this.length = 0;
    this.connection = paramOracleConnection;
    this.pickleCharaterSetId = 0;
    this.pickleNcharCharacterSet = 0;
    this.pickleCharacterSet = null;
    try
    {
      this.pickleCharaterSetId = this.connection.getStructAttrCsId();
    }
    catch (SQLException localSQLException)
    {
      this.pickleCharaterSetId = -1;
    }

    this.pickleCharacterSet = CharacterSet.make(this.pickleCharaterSetId);
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    if (paramObject == null) {
      return null;
    }
    CHAR localCHAR = (paramObject instanceof CHAR) ? (CHAR)paramObject : new CHAR(paramObject, this.pickleCharacterSet);

    return localCHAR;
  }

  public Datum[] toDatumArray(Object paramObject, OracleConnection paramOracleConnection, long paramLong, int paramInt)
    throws SQLException
  {
    Datum[] arrayOfDatum = null;

    if (paramObject != null)
    {
      if (((paramObject instanceof Object[])) && (!(paramObject instanceof char[][]))) {
        return super.toDatumArray(paramObject, paramOracleConnection, paramLong, paramInt);
      }
      arrayOfDatum = cArrayToDatumArray(paramObject, paramOracleConnection, paramLong, paramInt);
    }

    return arrayOfDatum;
  }

  public void parseTDSrec(TDSReader paramTDSReader)
    throws SQLException
  {
    super.parseTDSrec(paramTDSReader);
    try
    {
      this.length = paramTDSReader.readShort();
      this.form = paramTDSReader.readByte();
      this.characterSemantic = (this.form & 0x80);
      this.form &= 127;
      this.charset = paramTDSReader.readShort();
    }
    catch (SQLException localSQLException1)
    {
      DatabaseError.throwSqlException(47, "parseTDS");
    }

    if ((this.form != 2) || (this.pickleNcharCharacterSet != 0)) {
      return;
    }

    try
    {
      this.pickleNcharCharacterSet = this.connection.getStructAttrNCsId();
    }
    catch (SQLException localSQLException2)
    {
      this.pickleNcharCharacterSet = 2000;
    }

    this.pickleCharaterSetId = this.pickleNcharCharacterSet;
    this.pickleCharacterSet = CharacterSet.make(this.pickleCharaterSetId);
  }

  protected Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    switch (paramInt1)
    {
    case 1:
      if (paramUnpickleContext.isNull(this.nullOffset)) {
        return null;
      }
      paramUnpickleContext.skipTo(paramUnpickleContext.ldsOffsets[this.ldsOffset]);

      if (paramInt2 == 9)
      {
        paramUnpickleContext.skipBytes(6);

        return null;
      }

      long l = paramUnpickleContext.readLong();

      if (l == 0L)
      {
        paramUnpickleContext.skipBytes(2);
        paramUnpickleContext.mark();
      }
      else {
        paramUnpickleContext.markAndSkip(l);
      }
      byte[] arrayOfByte = paramUnpickleContext.readLengthBytes();

      paramUnpickleContext.reset();

      return toObject(arrayOfByte, paramInt2, paramMap);
    case 2:
      if ((paramUnpickleContext.readByte() & 0x1) != 1)
        break;
      paramUnpickleContext.skipBytes(4);

      return null;
    case 3:
      if (paramInt2 == 9)
      {
        paramUnpickleContext.skipLengthBytes();

        return null;
      }

      return toObject(paramUnpickleContext.readLengthBytes(), paramInt2, paramMap);
    }

    DatabaseError.throwSqlException(1, "format=" + paramInt1);

    return null;
  }

  protected int pickle81(PickleContext paramPickleContext, Datum paramDatum)
    throws SQLException
  {
    CHAR localCHAR = getDbCHAR(paramDatum);

    if ((this.characterSemantic != 0) && (this.form != 2))
    {
      if (localCHAR.getString().length() > this.length) {
        DatabaseError.throwSqlException(72, "\"" + localCHAR.getString() + "\"");
      }

    }
    else if (localCHAR.getLength() > this.length) {
      DatabaseError.throwSqlException(72, "\"" + localCHAR.getString() + "\"");
    }

    return super.pickle81(paramPickleContext, localCHAR);
  }

  protected Object toObject(byte[] paramArrayOfByte, int paramInt, Map paramMap)
    throws SQLException
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return null;
    }

    CHAR localCHAR = null;

    switch (this.form)
    {
    case 1:
    case 2:
      localCHAR = new CHAR(paramArrayOfByte, this.pickleCharacterSet);

      break;
    case 3:
    case 4:
    case 5:
      localCHAR = new CHAR(paramArrayOfByte, null);
    }

    if (paramInt == 1)
      return localCHAR;
    if (paramInt == 2)
      return localCHAR.stringValue();
    if (paramInt == 3) {
      return paramArrayOfByte;
    }
    DatabaseError.throwSqlException(59, paramArrayOfByte);

    return null;
  }

  private CHAR getDbCHAR(Datum paramDatum)
  {
    CHAR localCHAR1 = (CHAR)paramDatum;
    CHAR localCHAR2 = null;

    if (localCHAR1.getCharacterSet().getOracleId() == this.pickleCharaterSetId)
    {
      localCHAR2 = localCHAR1;
    }
    else
    {
      try
      {
        localCHAR2 = new CHAR(localCHAR1.toString(), this.pickleCharacterSet);
      }
      catch (SQLException localSQLException)
      {
        localCHAR2 = localCHAR1;
      }

    }

    return localCHAR2;
  }

  private Datum[] cArrayToDatumArray(Object paramObject, OracleConnection paramOracleConnection, long paramLong, int paramInt)
    throws SQLException
  {
    Datum[] arrayOfDatum = null;

    if (paramObject != null)
    {
      Object localObject;
      int i;
      int j;
      if ((paramObject instanceof char[][]))
      {
        localObject = (char[][])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++) {
          arrayOfDatum[j] = new CHAR(new String(localObject[((int)paramLong + j - 1)]), this.pickleCharacterSet);
        }
      }
      if ((paramObject instanceof boolean[]))
      {
        localObject = (boolean[])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++) {
          arrayOfDatum[j] = new CHAR(new Boolean(localObject[((int)paramLong + j - 1)]), this.pickleCharacterSet);
        }
      }
      if ((paramObject instanceof short[]))
      {
        localObject = (short[])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++) {
          arrayOfDatum[j] = new CHAR(new Integer(localObject[((int)paramLong + j - 1)]), this.pickleCharacterSet);
        }
      }

      if ((paramObject instanceof int[]))
      {
        localObject = (int[])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++) {
          arrayOfDatum[j] = new CHAR(new Integer(localObject[((int)paramLong + j - 1)]), this.pickleCharacterSet);
        }
      }
      if ((paramObject instanceof long[]))
      {
        localObject = (long[])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++) {
          arrayOfDatum[j] = new CHAR(new Long(localObject[((int)paramLong + j - 1)]), this.pickleCharacterSet);
        }
      }
      if ((paramObject instanceof float[]))
      {
        localObject = (float[])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++) {
          arrayOfDatum[j] = new CHAR(new Float(localObject[((int)paramLong + j - 1)]), this.pickleCharacterSet);
        }
      }
      if ((paramObject instanceof double[]))
      {
        localObject = (double[])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++) {
          arrayOfDatum[j] = new CHAR(new Double(localObject[((int)paramLong + j - 1)]), this.pickleCharacterSet);
        }

      }

      DatabaseError.throwSqlException(59, paramObject);
    }

    return (Datum)arrayOfDatum;
  }

  public int getLength()
  {
    return this.length;
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(this.form);
    paramObjectOutputStream.writeInt(this.charset);
    paramObjectOutputStream.writeInt(this.length);
    paramObjectOutputStream.writeInt(this.characterSemantic);
    paramObjectOutputStream.writeShort(this.pickleCharaterSetId);
    paramObjectOutputStream.writeShort(this.pickleNcharCharacterSet);
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.form = paramObjectInputStream.readInt();
    this.charset = paramObjectInputStream.readInt();
    this.length = paramObjectInputStream.readInt();
    this.characterSemantic = paramObjectInputStream.readInt();
    this.pickleCharaterSetId = paramObjectInputStream.readShort();
    this.pickleNcharCharacterSet = paramObjectInputStream.readShort();

    if (this.pickleNcharCharacterSet != 0)
      this.pickleCharacterSet = CharacterSet.make(this.pickleNcharCharacterSet);
    else
      this.pickleCharacterSet = CharacterSet.make(this.pickleCharaterSetId);
  }

  public void setConnection(OracleConnection paramOracleConnection) throws SQLException
  {
    this.connection = paramOracleConnection;
  }

  public boolean isNCHAR()
    throws SQLException
  {
    return this.form == 2;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeCHAR
 * JD-Core Version:    0.6.0
 */