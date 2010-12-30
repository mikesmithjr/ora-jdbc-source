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

public class OracleWebRowSet extends OracleCachedRowSet implements WebRowSet {
    private transient OracleWebRowSetXmlReader xmlReader;
    private transient OracleWebRowSetXmlWriter xmlWriter;

    public OracleWebRowSet() throws SQLException {
        this.xmlReader = new OracleWebRowSetXmlReaderImpl();
        this.xmlWriter = new OracleWebRowSetXmlWriterImpl();

        setReadOnly(false);
    }

    public void readXml(Reader reader) throws SQLException {
        if (this.xmlReader != null) {
            this.xmlReader.readXML(this, reader);
        } else {
            throw new SQLException("Invalid reader");
        }
    }

    public void writeXml(Writer writer) throws SQLException {
        if (this.xmlWriter != null) {
            this.xmlWriter.writeXML(this, writer);
        } else {
            throw new SQLException("Invalid writer");
        }
    }

    public void writeXml(ResultSet resultset, Writer writer) throws SQLException {
        populate(resultset);
        writeXml(writer);
    }

    public void readXml(InputStream istream) throws SQLException {
        readXml(new InputStreamReader(istream));
    }

    public void writeXml(OutputStream ostream) throws SQLException {
        writeXml(new OutputStreamWriter(ostream));
    }

    public void writeXml(ResultSet rset, OutputStream ostream) throws SQLException {
        writeXml(rset, new OutputStreamWriter(ostream));
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleWebRowSet JD-Core Version: 0.6.0
 */