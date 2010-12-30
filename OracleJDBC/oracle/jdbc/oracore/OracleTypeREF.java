package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.Datum;
import oracle.sql.REF;
import oracle.sql.StructDescriptor;

public class OracleTypeREF extends OracleNamedType
  implements Serializable
{
  static final long serialVersionUID = 3186448715463064573L;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  protected OracleTypeREF()
  {
  }

  public OracleTypeREF(String paramString, OracleConnection paramOracleConnection)
    throws SQLException
  {
    super(paramString, paramOracleConnection);
  }

  public OracleTypeREF(OracleTypeADT paramOracleTypeADT, int paramInt, OracleConnection paramOracleConnection)
  {
    super(paramOracleTypeADT, paramInt, paramOracleConnection);
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    REF localREF = null;

    if (paramObject != null)
    {
      if ((paramObject instanceof REF))
        localREF = (REF)paramObject;
      else {
        DatabaseError.throwSqlException(59, paramObject);
      }
    }

    return localREF;
  }

  public int getTypeCode()
  {
    return 2006;
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
        paramUnpickleContext.skipBytes(4);

        return null;
      }

      paramUnpickleContext.markAndSkip();

      byte[] arrayOfByte = paramUnpickleContext.readPtrBytes();

      paramUnpickleContext.reset();

      return toObject(arrayOfByte, paramInt2, null);
    case 2:
      if ((paramUnpickleContext.readByte() & 0x1) != 1)
        break;
      paramUnpickleContext.skipPtrBytes();

      return null;
    case 3:
      if (paramInt2 == 9)
      {
        paramUnpickleContext.skipPtrBytes();

        return null;
      }

      return toObject(paramUnpickleContext.readPtrBytes(), paramInt2, null);
    }

    DatabaseError.throwSqlException(1, "format=" + paramInt1);

    return null;
  }

  protected Object toObject(byte[] paramArrayOfByte, int paramInt, Map paramMap)
    throws SQLException
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return null;
    }
    if ((paramInt == 1) || (paramInt == 2))
    {
      StructDescriptor localStructDescriptor = createStructDescriptor();

      return new REF(localStructDescriptor, this.connection, paramArrayOfByte);
    }
    if (paramInt == 3)
    {
      return paramArrayOfByte;
    }

    DatabaseError.throwSqlException(59, paramArrayOfByte);

    return null;
  }

  StructDescriptor createStructDescriptor() throws SQLException
  {
    if (this.descriptor == null)
    {
      if ((this.sqlName == null) && (getFullName(false) == null))
      {
        OracleTypeADT localOracleTypeADT = new OracleTypeADT(getParent(), getOrder(), this.connection);

        this.descriptor = new StructDescriptor(localOracleTypeADT, this.connection);
      }
      else
      {
        this.descriptor = StructDescriptor.createDescriptor(this.sqlName, this.connection);
      }
    }
    return (StructDescriptor)this.descriptor;
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeREF
 * JD-Core Version:    0.6.0
 */