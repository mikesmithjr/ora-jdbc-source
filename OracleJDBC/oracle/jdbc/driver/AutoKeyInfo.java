package oracle.jdbc.driver;

import java.sql.SQLException;

class AutoKeyInfo extends OracleResultSetMetaData
{
  String originalSql;
  String newSql;
  String tableName;
  String[] columnNames;
  int[] columnIndexes;
  int numColumns;
  String[] tableColumnNames;
  int[] tableColumnTypes;
  int[] tableMaxLengths;
  boolean[] tableNullables;
  short[] tableFormOfUses;
  int[] tablePrecisions;
  int[] tableScales;
  String[] tableTypeNames;
  int autoKeyType;
  static final int KEYFLAG = 0;
  static final int COLUMNAME = 1;
  static final int COLUMNINDEX = 2;
  int[] returnTypes;
  Accessor[] returnAccessors;

  AutoKeyInfo(String paramString)
  {
    this.originalSql = paramString;
    this.autoKeyType = 0;
  }

  AutoKeyInfo(String paramString, String[] paramArrayOfString)
  {
    this.originalSql = paramString;
    this.columnNames = paramArrayOfString;
    this.autoKeyType = 1;
  }

  AutoKeyInfo(String paramString, int[] paramArrayOfInt)
  {
    this.originalSql = paramString;
    this.columnIndexes = paramArrayOfInt;
    this.autoKeyType = 2;
  }

  String getNewSql()
    throws SQLException
  {
    if (this.newSql != null) return this.newSql;

    switch (this.autoKeyType)
    {
    case 0:
      this.newSql = (this.originalSql + " RETURNING ROWID INTO ?");
      this.returnTypes = new int[1];
      this.returnTypes[0] = 104;
      return this.newSql;
    case 1:
      return getNewSqlByColumnName();
    case 2:
      return getNewSqlByColumnIndexes();
    }

    DatabaseError.throwSqlException(89);
    return null;
  }

  private String getNewSqlByColumnName()
    throws SQLException
  {
    this.returnTypes = new int[this.columnNames.length];

    this.columnIndexes = new int[this.columnNames.length];

    StringBuffer localStringBuffer = new StringBuffer(this.originalSql);
    localStringBuffer.append(" RETURNING ");

    for (int j = 0; j < this.columnNames.length; j++)
    {
      int i = getReturnParamTypeCode(j, this.columnNames[j], this.columnIndexes);
      this.returnTypes[j] = i;

      localStringBuffer.append(this.columnNames[j]);

      if (j >= this.columnNames.length - 1) continue; localStringBuffer.append(", ");
    }

    localStringBuffer.append(" INTO ");

    for (j = 0; j < this.columnNames.length - 1; j++)
    {
      localStringBuffer.append("?, ");
    }

    localStringBuffer.append("?");

    this.newSql = new String(localStringBuffer);
    return this.newSql;
  }

  private String getNewSqlByColumnIndexes() throws SQLException
  {
    this.returnTypes = new int[this.columnIndexes.length];

    StringBuffer localStringBuffer = new StringBuffer(this.originalSql);
    localStringBuffer.append(" RETURNING ");

    for (int j = 0; j < this.columnIndexes.length; j++)
    {
      int k = this.columnIndexes[j] - 1;
      if ((k < 0) || (k > this.tableColumnNames.length))
      {
        DatabaseError.throwSqlException(68);
      }

      int i = this.tableColumnTypes[k];
      String str = this.tableColumnNames[k];
      this.returnTypes[j] = i;

      localStringBuffer.append(str);

      if (j >= this.columnIndexes.length - 1) continue; localStringBuffer.append(", ");
    }

    localStringBuffer.append(" INTO ");

    for (j = 0; j < this.columnIndexes.length - 1; j++)
    {
      localStringBuffer.append("?, ");
    }

    localStringBuffer.append("?");

    this.newSql = new String(localStringBuffer);
    return this.newSql;
  }

  private final int getReturnParamTypeCode(int paramInt, String paramString, int[] paramArrayOfInt)
    throws SQLException
  {
    for (int i = 0; i < this.tableColumnNames.length; i++)
    {
      if (!paramString.equalsIgnoreCase(this.tableColumnNames[i]))
        continue;
      paramArrayOfInt[paramInt] = (i + 1);
      return this.tableColumnTypes[i];
    }

    DatabaseError.throwSqlException(68);

    return -1;
  }

  static final boolean isInsertSqlStmt(String paramString)
    throws SQLException
  {
    if (paramString == null)
    {
      DatabaseError.throwSqlException(68);
    }

    return paramString.trim().toUpperCase().startsWith("INSERT");
  }

  String getTableName() throws SQLException
  {
    if (this.tableName != null) return this.tableName;

    String str = this.originalSql.trim().toUpperCase();

    int i = str.indexOf("INSERT");
    i = str.indexOf("INTO", i);

    if (i < 0) {
      DatabaseError.throwSqlException(68);
    }
    int j = str.length();
    int k = i + 5;

    while ((k < j) && (str.charAt(k) == ' ')) k++;

    if (k >= j) {
      DatabaseError.throwSqlException(68);
    }
    int m = k + 1;

    while ((m < j) && (str.charAt(m) != ' ') && (str.charAt(m) != '(')) m++;

    if (k == m - 1) {
      DatabaseError.throwSqlException(68);
    }
    this.tableName = str.substring(k, m);

    return this.tableName;
  }

  void allocateSpaceForDescribedData(int paramInt) throws SQLException
  {
    this.numColumns = paramInt;

    this.tableColumnNames = new String[paramInt];
    this.tableColumnTypes = new int[paramInt];
    this.tableMaxLengths = new int[paramInt];
    this.tableNullables = new boolean[paramInt];
    this.tableFormOfUses = new short[paramInt];
    this.tablePrecisions = new int[paramInt];
    this.tableScales = new int[paramInt];
    this.tableTypeNames = new String[paramInt];
  }

  void fillDescribedData(int paramInt1, String paramString1, int paramInt2, int paramInt3, boolean paramBoolean, short paramShort, int paramInt4, int paramInt5, String paramString2)
    throws SQLException
  {
    this.tableColumnNames[paramInt1] = paramString1;
    this.tableColumnTypes[paramInt1] = paramInt2;
    this.tableMaxLengths[paramInt1] = paramInt3;
    this.tableNullables[paramInt1] = paramBoolean;
    this.tableFormOfUses[paramInt1] = paramShort;
    this.tablePrecisions[paramInt1] = paramInt4;
    this.tableScales[paramInt1] = paramInt5;
    this.tableTypeNames[paramInt1] = paramString2;
  }

  void initMetaData(OracleReturnResultSet paramOracleReturnResultSet) throws SQLException
  {
    if (this.returnAccessors != null) return;

    this.returnAccessors = paramOracleReturnResultSet.returnAccessors;

    switch (this.autoKeyType)
    {
    case 0:
      initMetaDataKeyFlag();
      break;
    case 1:
    case 2:
      initMetaDataColumnIndexes();
    }
  }

  void initMetaDataKeyFlag()
    throws SQLException
  {
    this.returnAccessors[0].columnName = "ROWID";
    this.returnAccessors[0].describeType = 104;
    this.returnAccessors[0].describeMaxLength = 4;
    this.returnAccessors[0].nullable = true;
    this.returnAccessors[0].precision = 0;
    this.returnAccessors[0].scale = 0;
    this.returnAccessors[0].formOfUse = 0;
  }

  void initMetaDataColumnIndexes()
    throws SQLException
  {
    for (int j = 0; j < this.returnAccessors.length; j++)
    {
      Accessor localAccessor = this.returnAccessors[j];
      int i = this.columnIndexes[j] - 1;

      localAccessor.columnName = this.tableColumnNames[i];
      localAccessor.describeType = this.tableColumnTypes[i];
      localAccessor.describeMaxLength = this.tableMaxLengths[i];
      localAccessor.nullable = this.tableNullables[i];
      localAccessor.precision = this.tablePrecisions[i];
      localAccessor.scale = this.tablePrecisions[i];
      localAccessor.formOfUse = this.tableFormOfUses[i];
    }
  }

  int getValidColumnIndex(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.returnAccessors.length)) {
      DatabaseError.throwSqlException(3);
    }
    return paramInt - 1;
  }

  public int getColumnCount() throws SQLException
  {
    return this.returnAccessors.length;
  }

  public String getColumnName(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.returnAccessors.length)) {
      DatabaseError.throwSqlException(3);
    }
    return this.returnAccessors[(paramInt - 1)].columnName;
  }

  public String getTableName(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.returnAccessors.length)) {
      DatabaseError.throwSqlException(3);
    }
    return getTableName();
  }

  Accessor[] getDescription() throws SQLException
  {
    return this.returnAccessors;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.AutoKeyInfo
 * JD-Core Version:    0.6.0
 */