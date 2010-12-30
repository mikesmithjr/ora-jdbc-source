package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.Datum;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

public abstract class OracleType
  implements Serializable
{
  static final long serialVersionUID = -6719430495533003861L;
  public static final int STYLE_ARRAY_LENGTH = 0;
  public static final int STYLE_DATUM = 1;
  public static final int STYLE_JAVA = 2;
  public static final int STYLE_RAWBYTE = 3;
  public static final int STYLE_INT = 4;
  public static final int STYLE_DOUBLE = 5;
  public static final int STYLE_FLOAT = 6;
  public static final int STYLE_LONG = 7;
  public static final int STYLE_SHORT = 8;
  public static final int STYLE_SKIP = 9;
  static final int FORMAT_ADT_ATTR = 1;
  static final int FORMAT_COLL_ELEM = 2;
  static final int FORMAT_COLL_ELEM_NO_INDICATOR = 3;
  int nullOffset;
  int ldsOffset;
  int sizeForLds;
  int alignForLds;
  int typeCode;
  int dbTypeCode;
  static final int KOPMAP_FLOAT = 2;
  static final int KOPMAP_SB4 = 4;
  static final int KOPMAP_PTR = 5;
  static final int KOPMAP_ORLD = 11;
  static final int KOPMAP_ORLN = 12;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:48_PDT_2005";

  public OracleType()
  {
    this.nullOffset = 0;
    this.ldsOffset = 0;
    this.sizeForLds = 0;
  }

  public OracleType(int paramInt)
  {
    this();

    this.typeCode = paramInt;
  }

  public boolean isInHierarchyOf(OracleType paramOracleType)
    throws SQLException
  {
    return false;
  }

  public boolean isInHierarchyOf(StructDescriptor paramStructDescriptor)
    throws SQLException
  {
    return false;
  }

  public boolean isObjectType()
  {
    return false;
  }

  public TypeDescriptor getTypeDescriptor()
  {
    return null;
  }

  public abstract Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException;

  public Datum[] toDatumArray(Object paramObject, OracleConnection paramOracleConnection, long paramLong, int paramInt)
    throws SQLException
  {
    Datum[] arrayOfDatum = null;

    if (paramObject != null)
    {
      if ((paramObject instanceof Object[]))
      {
        Object[] arrayOfObject = (Object[])paramObject;

        int i = (int)(paramInt == -1 ? arrayOfObject.length : Math.min(arrayOfObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (int j = 0; j < i; j++) {
          arrayOfDatum[j] = toDatum(arrayOfObject[((int)paramLong + j - 1)], paramOracleConnection);
        }
      }
      DatabaseError.throwSqlException(59, paramObject);
    }

    return arrayOfDatum;
  }

  public void setTypeCode(int paramInt)
  {
    this.typeCode = paramInt;
  }

  public int getTypeCode()
    throws SQLException
  {
    return this.typeCode;
  }

  public void setDBTypeCode(int paramInt)
  {
    this.dbTypeCode = paramInt;
  }

  public int getDBTypeCode() throws SQLException
  {
    return this.dbTypeCode;
  }

  public void parseTDSrec(TDSReader paramTDSReader)
    throws SQLException
  {
    this.nullOffset = paramTDSReader.nullOffset;
    this.ldsOffset = paramTDSReader.ldsOffset;

    paramTDSReader.nullOffset += 1;
    paramTDSReader.ldsOffset += 1;
  }

  public int getSizeLDS(byte[] paramArrayOfByte)
  {
    if (this.sizeForLds == 0)
    {
      this.sizeForLds = Util.fdoGetSize(paramArrayOfByte, 5);
      this.alignForLds = Util.fdoGetAlign(paramArrayOfByte, 5);
    }

    return this.sizeForLds;
  }

  public int getAlignLDS(byte[] paramArrayOfByte)
  {
    if (this.sizeForLds == 0)
    {
      this.sizeForLds = Util.fdoGetSize(paramArrayOfByte, 5);
      this.alignForLds = Util.fdoGetAlign(paramArrayOfByte, 5);
    }

    return this.alignForLds;
  }

  protected abstract Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException;

  protected Object unpickle81rec(PickleContext paramPickleContext, int paramInt, Map paramMap)
    throws SQLException
  {
    if (paramInt == 9)
    {
      paramPickleContext.skipDataValue();

      return null;
    }

    byte[] arrayOfByte = paramPickleContext.readDataValue();

    return toObject(arrayOfByte, paramInt, paramMap);
  }

  protected Object unpickle81rec(PickleContext paramPickleContext, byte paramByte, int paramInt, Map paramMap)
    throws SQLException
  {
    if (paramInt == 9)
    {
      paramPickleContext.skipDataValue();

      return null;
    }

    byte[] arrayOfByte = paramPickleContext.readDataValue(paramByte);

    return toObject(arrayOfByte, paramInt, paramMap);
  }

  protected Datum unpickle81datumAsNull(PickleContext paramPickleContext, byte paramByte1, byte paramByte2)
    throws SQLException
  {
    DatabaseError.throwSqlException(1);
    return null;
  }

  protected Object toObject(byte[] paramArrayOfByte, int paramInt, Map paramMap)
    throws SQLException
  {
    return null;
  }

  protected int pickle81(PickleContext paramPickleContext, Datum paramDatum)
    throws SQLException
  {
    int i = paramPickleContext.writeLength((int)paramDatum.getLength());

    i += paramPickleContext.writeData(paramDatum.shareBytes());

    return i;
  }

  void writeSerializedFields(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(this.nullOffset);
    paramObjectOutputStream.writeInt(this.ldsOffset);
    paramObjectOutputStream.writeInt(this.sizeForLds);
    paramObjectOutputStream.writeInt(this.alignForLds);
    paramObjectOutputStream.writeInt(this.typeCode);
    paramObjectOutputStream.writeInt(this.dbTypeCode);
  }

  void readSerializedFields(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.nullOffset = paramObjectInputStream.readInt();
    this.ldsOffset = paramObjectInputStream.readInt();
    this.sizeForLds = paramObjectInputStream.readInt();
    this.alignForLds = paramObjectInputStream.readInt();
    this.typeCode = paramObjectInputStream.readInt();
    this.dbTypeCode = paramObjectInputStream.readInt();
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(this.nullOffset);
    paramObjectOutputStream.writeInt(this.ldsOffset);
    paramObjectOutputStream.writeInt(this.sizeForLds);
    paramObjectOutputStream.writeInt(this.alignForLds);
    paramObjectOutputStream.writeInt(this.typeCode);
    paramObjectOutputStream.writeInt(this.dbTypeCode);
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.nullOffset = paramObjectInputStream.readInt();
    this.ldsOffset = paramObjectInputStream.readInt();
    this.sizeForLds = paramObjectInputStream.readInt();
    this.alignForLds = paramObjectInputStream.readInt();
    this.typeCode = paramObjectInputStream.readInt();
    this.dbTypeCode = paramObjectInputStream.readInt();
  }

  public void setConnection(OracleConnection paramOracleConnection)
    throws SQLException
  {
  }

  public boolean isNCHAR()
    throws SQLException
  {
    return false;
  }

  public int getPrecision()
    throws SQLException
  {
    return 0;
  }

  public int getScale() throws SQLException
  {
    return 0;
  }

  public void initMetadataRecursively()
    throws SQLException
  {
  }

  public void initNamesRecursively()
    throws SQLException
  {
  }

  public void initChildNamesRecursively(Map paramMap)
    throws SQLException
  {
  }

  public void cacheDescriptor()
    throws SQLException
  {
  }

  public void setNames(String paramString1, String paramString2)
    throws SQLException
  {
  }

  public String toXMLString()
    throws SQLException
  {
    StringWriter localStringWriter = new StringWriter();
    PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
    printXMLHeader(localPrintWriter);
    printXML(localPrintWriter, 0);
    return localStringWriter.toString();
  }

  public void printXML(PrintStream paramPrintStream) throws SQLException
  {
    PrintWriter localPrintWriter = new PrintWriter(paramPrintStream, true);
    printXMLHeader(localPrintWriter);
    printXML(localPrintWriter, 0);
  }

  void printXMLHeader(PrintWriter paramPrintWriter) throws SQLException
  {
    paramPrintWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
  }

  public void printXML(PrintWriter paramPrintWriter, int paramInt) throws SQLException
  {
    for (int i = 0; i < paramInt; i++) paramPrintWriter.print("  ");
    paramPrintWriter.println("<OracleType typecode=\"" + this.typeCode + "\"" + " hashCode=\"" + hashCode() + "\" " + " ldsOffset=\"" + this.ldsOffset + "\"" + " sizeForLds=\"" + this.sizeForLds + "\"" + " alignForLds=\"" + this.alignForLds + "\"" + " />");
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleType
 * JD-Core Version:    0.6.0
 */