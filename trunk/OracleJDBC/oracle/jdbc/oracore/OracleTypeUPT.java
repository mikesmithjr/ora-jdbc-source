package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.Datum;
import oracle.sql.OPAQUE;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

public class OracleTypeUPT extends OracleTypeADT
  implements Serializable
{
  static final long serialVersionUID = -1994358478872378695L;
  static final byte KOPU_UPT_ADT = -6;
  static final byte KOPU_UPT_COLL = -5;
  static final byte KOPU_UPT_REFCUR = 102;
  static final byte KOTTCOPQ = 58;
  byte uptCode = 0;
  OracleNamedType realType = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  protected OracleTypeUPT()
  {
  }

  public OracleTypeUPT(String paramString, OracleConnection paramOracleConnection)
    throws SQLException
  {
    super(paramString, paramOracleConnection);
  }

  public OracleTypeUPT(OracleTypeADT paramOracleTypeADT, int paramInt, OracleConnection paramOracleConnection)
    throws SQLException
  {
    super(paramOracleTypeADT, paramInt, paramOracleConnection);
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    if (paramObject != null) {
      return this.realType.toDatum(paramObject, paramOracleConnection);
    }
    return null;
  }

  public Datum[] toDatumArray(Object paramObject, OracleConnection paramOracleConnection, long paramLong, int paramInt)
    throws SQLException
  {
    if (paramObject != null) {
      return this.realType.toDatumArray(paramObject, paramOracleConnection, paramLong, paramInt);
    }
    return null;
  }

  public int getTypeCode()
    throws SQLException
  {
    switch (this.uptCode)
    {
    case -6:
      return this.realType.getTypeCode();
    case -5:
      return 2003;
    case 58:
      return 2007;
    }

    DatabaseError.throwSqlException(1, "Invalid type code");

    return 0;
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

  public void parseTDSrec(TDSReader paramTDSReader)
    throws SQLException
  {
    this.nullOffset = paramTDSReader.nullOffset;
    paramTDSReader.nullOffset += 1;
    this.ldsOffset = paramTDSReader.ldsOffset;
    paramTDSReader.ldsOffset += 1;

    long l = paramTDSReader.readLong();

    this.uptCode = paramTDSReader.readByte();

    paramTDSReader.addNormalPatch(l, this.uptCode, this);
  }

  protected Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    Object localObject;
    switch (paramInt1)
    {
    case 2:
      switch (this.uptCode)
      {
      case -5:
        if ((paramUnpickleContext.readByte() & 0x1) == 1)
        {
          paramUnpickleContext.skipBytes(2);

          return null;
        }

        return ((OracleTypeCOLLECTION)this.realType).unpickle80(paramUnpickleContext, (ARRAY)null, paramInt2 == 9 ? paramInt2 : 3, paramInt2, paramMap);
      case -6:
        if ((paramUnpickleContext.readByte() & 0x1) == 1)
        {
          paramUnpickleContext.skipBytes(4);

          return null;
        }

        switch (paramInt2)
        {
        case 1:
          return ((OracleTypeADT)this.realType).unpickle80(paramUnpickleContext, (STRUCT)null, 3, paramInt2, paramMap);
        case 2:
          localObject = ((OracleTypeADT)this.realType).unpickle80(paramUnpickleContext, (STRUCT)null, 1, paramInt2, paramMap);

          return localObject == null ? localObject : ((STRUCT)localObject).toJdbc(paramMap);
        case 9:
          return ((OracleTypeADT)this.realType).unpickle80(paramUnpickleContext, (STRUCT)null, 9, 1, paramMap);
        }

        DatabaseError.throwSqlException(1);

        break;
      default:
        DatabaseError.throwSqlException(48, "upt_type");
      }

      break;
    case 3:
      switch (this.uptCode)
      {
      case -5:
        return ((OracleTypeCOLLECTION)this.realType).unpickle80(paramUnpickleContext, (ARRAY)null, paramInt2 == 9 ? paramInt2 : 3, paramInt2, paramMap);
      case -6:
        switch (paramInt2)
        {
        case 1:
          return ((OracleTypeADT)this.realType).unpickle80(paramUnpickleContext, (STRUCT)null, 3, paramInt2, paramMap);
        case 2:
          localObject = ((OracleTypeADT)this.realType).unpickle80(paramUnpickleContext, (STRUCT)null, 1, paramInt2, paramMap);

          return localObject == null ? localObject : ((STRUCT)localObject).toJdbc(paramMap);
        case 9:
          return ((OracleTypeADT)this.realType).unpickle80(paramUnpickleContext, (STRUCT)null, 9, 1, paramMap);
        }

        DatabaseError.throwSqlException(1);

        break;
      default:
        DatabaseError.throwSqlException(48, "upt_type");
      }

      break;
    case 1:
      switch (this.uptCode)
      {
      case -5:
        if (paramUnpickleContext.isNull(this.nullOffset))
        {
          paramUnpickleContext.skipBytes(4);

          return null;
        }

        paramUnpickleContext.skipTo(paramUnpickleContext.ldsOffsets[this.ldsOffset]);

        if (paramInt2 == 9)
        {
          paramUnpickleContext.skipBytes(4);

          return null;
        }

        paramUnpickleContext.markAndSkip();

        localObject = ((OracleTypeCOLLECTION)this.realType).unpickle80(paramUnpickleContext, (ARRAY)null, 3, paramInt2, paramMap);

        paramUnpickleContext.reset();

        return localObject;
      }

      DatabaseError.throwSqlException(1, "upt_type");

      break;
    default:
      DatabaseError.throwSqlException(1, "upt_type");
    }

    return null;
  }

  protected int pickle81(PickleContext paramPickleContext, Datum paramDatum)
    throws SQLException
  {
    int i = 0;

    if (paramDatum == null)
    {
      i += paramPickleContext.writeElementNull();
    }
    else
    {
      int j = paramPickleContext.offset();

      i += paramPickleContext.writeLength(PickleContext.KOPI20_LN_MAXV + 1);

      int k = 0;

      if ((this.uptCode == -6) && (!((OracleTypeADT)this.realType).isFinalType()))
      {
        if ((paramDatum instanceof STRUCT))
        {
          k = ((STRUCT)paramDatum).getDescriptor().getOracleTypeADT().pickle81(paramPickleContext, paramDatum);
        }
        else
        {
          DatabaseError.throwSqlException(1, "invalid upt state");
        }
      }
      else {
        k = this.realType.pickle81(paramPickleContext, paramDatum);
      }
      i += k;

      paramPickleContext.patchImageLen(j, k);
    }

    return i;
  }

  protected Object unpickle81rec(PickleContext paramPickleContext, int paramInt, Map paramMap)
    throws SQLException
  {
    OracleLog.print(this, 16, 8, 32, "OracleTypeUPT.unpickle81rec(" + paramPickleContext + ") " + this.sqlName);

    byte b = paramPickleContext.readByte();

    if (PickleContext.isElementNull(b))
    {
      return null;
    }
    if (paramInt == 9)
    {
      paramPickleContext.skipBytes(paramPickleContext.readRestOfLength(b));

      return null;
    }

    paramPickleContext.skipRestOfLength(b);

    return unpickle81UPT(paramPickleContext, paramInt, paramMap);
  }

  protected Object unpickle81rec(PickleContext paramPickleContext, byte paramByte, int paramInt, Map paramMap)
    throws SQLException
  {
    long l = paramPickleContext.readRestOfLength(paramByte);

    if (paramInt == 9)
    {
      paramPickleContext.skipBytes((int)l);

      return null;
    }

    return unpickle81UPT(paramPickleContext, paramInt, paramMap);
  }

  private Object unpickle81UPT(PickleContext paramPickleContext, int paramInt, Map paramMap)
    throws SQLException
  {
    Object localObject;
    switch (this.uptCode)
    {
    case -6:
      switch (paramInt)
      {
      case 1:
        return ((OracleTypeADT)this.realType).unpickle81(paramPickleContext, (STRUCT)null, 3, paramInt, paramMap);
      case 2:
        localObject = ((OracleTypeADT)this.realType).unpickle81(paramPickleContext, (STRUCT)null, 1, paramInt, paramMap);

        return localObject == null ? localObject : ((STRUCT)localObject).toJdbc(paramMap);
      case 9:
        return ((OracleTypeADT)this.realType).unpickle81(paramPickleContext, (STRUCT)null, 9, 1, paramMap);
      }

      DatabaseError.throwSqlException(1);

      break;
    case -5:
      return ((OracleTypeCOLLECTION)this.realType).unpickle81(paramPickleContext, (ARRAY)null, paramInt == 9 ? paramInt : 3, paramInt, paramMap);
    case 58:
      switch (paramInt)
      {
      case 1:
      case 9:
        return ((OracleTypeOPAQUE)this.realType).unpickle81(paramPickleContext, (OPAQUE)null, paramInt, paramMap);
      case 2:
        localObject = ((OracleTypeOPAQUE)this.realType).unpickle81(paramPickleContext, (OPAQUE)null, paramInt, paramMap);

        return localObject == null ? localObject : ((OPAQUE)localObject).toJdbc(paramMap);
      }

      DatabaseError.throwSqlException(1);
    default:
      DatabaseError.throwSqlException(1, "Unrecognized UPT code");
    }

    return null;
  }

  protected Datum unpickle81datumAsNull(PickleContext paramPickleContext, byte paramByte1, byte paramByte2)
    throws SQLException
  {
    return null;
  }

  StructDescriptor createStructDescriptor()
    throws SQLException
  {
    StructDescriptor localStructDescriptor = null;

    if (this.sqlName == null)
      localStructDescriptor = new StructDescriptor((OracleTypeADT)this.realType, this.connection);
    else {
      localStructDescriptor = StructDescriptor.createDescriptor(this.sqlName, this.connection);
    }
    return localStructDescriptor;
  }

  ArrayDescriptor createArrayDescriptor() throws SQLException
  {
    ArrayDescriptor localArrayDescriptor = null;

    if (this.sqlName == null)
      localArrayDescriptor = new ArrayDescriptor((OracleTypeCOLLECTION)this.realType, this.connection);
    else {
      localArrayDescriptor = ArrayDescriptor.createDescriptor(this.sqlName, this.connection);
    }
    return localArrayDescriptor;
  }

  public OracleType getRealType() throws SQLException
  {
    return this.realType;
  }

  public int getNumAttrs() throws SQLException
  {
    return ((OracleTypeADT)this.realType).getNumAttrs();
  }

  public OracleType getAttrTypeAt(int paramInt) throws SQLException
  {
    return ((OracleTypeADT)this.realType).getAttrTypeAt(paramInt);
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeByte(this.uptCode);
    paramObjectOutputStream.writeObject(this.realType);
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.uptCode = paramObjectInputStream.readByte();
    this.realType = ((OracleNamedType)paramObjectInputStream.readObject());
  }

  public void setConnection(OracleConnection paramOracleConnection) throws SQLException
  {
    this.connection = paramOracleConnection;

    this.realType.setConnection(paramOracleConnection);
  }

  public void initChildNamesRecursively(Map paramMap) throws SQLException
  {
    if (this.realType != null)
    {
      this.realType.setSqlName(this.sqlName);
      this.realType.initChildNamesRecursively(paramMap);
    }
  }

  public void initMetadataRecursively() throws SQLException
  {
    initMetadata(this.connection);
    if (this.realType != null) this.realType.initMetadataRecursively(); 
  }

  public void cacheDescriptor()
    throws SQLException
  {
  }

  public void printXML(PrintWriter paramPrintWriter, int paramInt)
    throws SQLException
  {
    for (int i = 0; i < paramInt; i++) paramPrintWriter.print("  ");
    paramPrintWriter.println("<OracleTypeUPT> sqlName=\"" + this.sqlName + "\" " + " toid=\"" + this.toid + "\" " + ">");

    if (this.realType != null)
      this.realType.printXML(paramPrintWriter, paramInt + 1);
    for (i = 0; i < paramInt; i++) paramPrintWriter.print("  ");
    paramPrintWriter.println("</OracleTypeUPT>");
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeUPT
 * JD-Core Version:    0.6.0
 */