package oracle.jdbc.connector;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.EISSystemException;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;
import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import oracle.jdbc.driver.OracleLog;

public class OracleManagedConnectionFactory implements ManagedConnectionFactory {
    private XADataSource xaDataSource = null;
    private String xaDataSourceName = null;
    private static final String RAERR_MCF_SET_XADS = "invalid xads";
    private static final String RAERR_MCF_GET_PCRED = "no password credential";
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:57_PDT_2005";

    public OracleManagedConnectionFactory() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.INFO,
                         "OracleManagedConnectionFactory.OracleManagedConnectionFactory()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleManagedConnectionFactory(XADataSource xads) throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnectionFactory.OracleManagedConnectionFactory(xads = "
                                               + xads + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.xaDataSource = xads;
        this.xaDataSourceName = "XADataSource";
    }

    public void setXADataSourceName(String xadsName) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnectionFactory.setXADataSourceName(xadsName = "
                                               + xadsName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.xaDataSourceName = xadsName;
    }

    public String getXADataSourceName() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnectionFactory.getXADataSourceName() returns: "
                                               + this.xaDataSourceName, this);

            OracleLog.recursiveTrace = false;
        }

        return this.xaDataSourceName;
    }

    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnectionFactory.createConnectionFactory(cxManager = "
                                               + cxManager + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.xaDataSource == null) {
            setupXADataSource();
        }

        return (DataSource) this.xaDataSource;
    }

    public Object createConnectionFactory() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnectionFactory.createConnectionFactory()",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        return createConnectionFactory(null);
    }

    public ManagedConnection createManagedConnection(Subject subject,
            ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnectionFactory.createManagedConnection(subject = "
                                               + subject + ", cxRequestInfo = " + cxRequestInfo
                                               + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ResourceException rexc;
        try {
            if (this.xaDataSource == null) {
                setupXADataSource();
            }

            XAConnection xaconn = null;
            PasswordCredential pcred = getPasswordCredential(subject, cxRequestInfo);

            if (pcred == null) {
                xaconn = this.xaDataSource.getXAConnection();
            } else {
                xaconn = this.xaDataSource.getXAConnection(pcred.getUserName(), new String(pcred
                        .getPassword()));
            }

            OracleManagedConnection omc = new OracleManagedConnection(xaconn);

            omc.setPasswordCredential(pcred);

            omc.setLogWriter(getLogWriter());

            return omc;
        } catch (SQLException exc) {
            rexc = new EISSystemException("SQLException: " + exc.getMessage());

            rexc.setLinkedException(exc);
        }
        throw rexc;
    }

    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject,
            ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnectionFactory.matchManagedConnection(connectionSet = "
                                               + connectionSet + ", subject = " + subject
                                               + ", cxRequestInfo = " + cxRequestInfo + ")", this);

            OracleLog.recursiveTrace = false;
        }

        PasswordCredential pcred = getPasswordCredential(subject, cxRequestInfo);
        Iterator iter = connectionSet.iterator();

        while (iter.hasNext()) {
            Object obj = iter.next();

            if ((obj instanceof OracleManagedConnection)) {
                OracleManagedConnection omc = (OracleManagedConnection) obj;

                if (omc.getPasswordCredential().equals(pcred)) {
                    return omc;
                }
            }
        }

        return null;
    }

    public void setLogWriter(PrintWriter out) throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnectionFactory.setLogWriter(out = " + out
                                               + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if (this.xaDataSource == null) {
                setupXADataSource();
            }

            this.xaDataSource.setLogWriter(out);
        } catch (SQLException exc) {
            ResourceException rexc = new EISSystemException("SQLException: " + exc.getMessage());

            rexc.setLinkedException(exc);

            throw rexc;
        }
    }

    public PrintWriter getLogWriter() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleManagedConnectionFactory.getLogWriter()",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        ResourceException rexc;
        try {
            if (this.xaDataSource == null) {
                setupXADataSource();
            }

            return this.xaDataSource.getLogWriter();
        } catch (SQLException exc) {
            rexc = new EISSystemException("SQLException: " + exc.getMessage());

            rexc.setLinkedException(exc);
        }
        throw rexc;
    }

    private void setupXADataSource() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleManagedConnectionFactory.setupXADataSource()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            Context ic = null;
            try {
                Properties props = System.getProperties();

                ic = new InitialContext(props);
            } catch (java.lang.SecurityException exc) {
            }

            if (ic == null) {
                ic = new InitialContext();
            }

            XADataSource xads = (XADataSource) ic.lookup(this.xaDataSourceName);

            if (xads == null) {
                throw new ResourceAdapterInternalException("Invalid XADataSource object");
            }

            this.xaDataSource = xads;
        } catch (NamingException exc) {
            ResourceException rexc = new ResourceException("NamingException: " + exc.getMessage());

            rexc.setLinkedException(exc);

            throw rexc;
        }
    }

    private PasswordCredential getPasswordCredential(Subject subject,
            ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleManagedConnectionFactory.getPasswordCredential(subject = "
                                               + subject + ", cxRequestInfo = " + cxRequestInfo
                                               + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (subject != null) {
            Set creds = subject.getPrivateCredentials(PasswordCredential.class);
            Iterator iter = creds.iterator();

            while (iter.hasNext()) {
                PasswordCredential pcred = (PasswordCredential) iter.next();

                if (pcred.getManagedConnectionFactory().equals(this)) {
                    return pcred;
                }
            }

            throw new javax.resource.spi.SecurityException(
                    "Can not find user/password information", "no password credential");
        }

        if (cxRequestInfo == null) {
            return null;
        }

        OracleConnectionRequestInfo info = (OracleConnectionRequestInfo) cxRequestInfo;

        PasswordCredential pcred = new PasswordCredential(info.getUser(), info.getPassword()
                .toCharArray());

        pcred.setManagedConnectionFactory(this);

        return pcred;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.connector.OracleManagedConnectionFactory"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.connector.OracleManagedConnectionFactory JD-Core Version: 0.6.0
 */