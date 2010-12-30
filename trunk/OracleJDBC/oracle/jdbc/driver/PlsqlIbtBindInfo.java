package oracle.jdbc.driver;

import java.sql.SQLException;

class PlsqlIbtBindInfo
{
  Object[] arrayData;
  int maxLen;
  int curLen;
  int element_internal_type;
  int elemMaxLen;
  int ibtByteLength;
  int ibtCharLength;
  int ibtValueIndex;
  int ibtIndicatorIndex;
  int ibtLengthIndex;

  PlsqlIbtBindInfo(Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws SQLException
  {
    this.arrayData = paramArrayOfObject;
    this.maxLen = paramInt1;
    this.curLen = paramInt2;
    this.element_internal_type = paramInt3;

    switch (paramInt3)
    {
    case 1:
    case 96:
      this.elemMaxLen = (paramInt4 == 0 ? 2 : paramInt4 + 1);

      this.ibtCharLength = (this.elemMaxLen * paramInt1);
      this.element_internal_type = 9;

      break;
    case 6:
      this.elemMaxLen = 22;
      this.ibtByteLength = (this.elemMaxLen * paramInt1);

      break;
    default:
      DatabaseError.throwSqlException(97);
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.PlsqlIbtBindInfo
 * JD-Core Version:    0.6.0
 */