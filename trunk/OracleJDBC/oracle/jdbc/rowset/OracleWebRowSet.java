package oracle.jdbc.rowset;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.rowset.WebRowSet;

public class OracleWebRowSet extends OracleCachedRowSet
  implements WebRowSet
{
  private transient OracleWebRowSetXmlReader xmlReader;
  private transient OracleWebRowSetXmlWriter xmlWriter;

  public OracleWebRowSet()
    throws SQLException
  {
    this.xmlReader = new OracleWebRowSetXmlReaderImpl();
    this.xmlWriter = new OracleWebRowSetXmlWriterImpl();

    setReadOnly(false);
  }

  public void readXml(Reader paramReader)
    throws SQLException
  {
    if (this.xmlReader != null)
    {
      this.xmlReader.readXML(this, paramReader);
    }
    else
    {
      throw new SQLException("Invalid reader");
    }
  }

  public void writeXml(Writer paramWriter)
    throws SQLException
  {
    if (this.xmlWriter != null)
    {
      this.xmlWriter.writeXML(this, paramWriter);
    }
    else
    {
      throw new SQLException("Invalid writer");
    }
  }

  public void writeXml(ResultSet paramResultSet, Writer paramWriter)
    throws SQLException
  {
    populate(paramResultSet);
    writeXml(paramWriter);
  }

  public void readXml(InputStream paramInputStream)
    throws SQLException
  {
    readXml(new InputStreamReader(paramInputStream));
  }

  public void writeXml(OutputStream paramOutputStream)
    throws SQLException
  {
    writeXml(new OutputStreamWriter(paramOutputStream));
  }

  public void writeXml(ResultSet paramResultSet, OutputStream paramOutputStream)
    throws SQLException
  {
    writeXml(paramResultSet, new OutputStreamWriter(paramOutputStream));
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.rowset.OracleWebRowSet
 * JD-Core Version:    0.6.0
 */