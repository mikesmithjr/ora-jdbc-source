package oracle.jdbc.driver;

import java.sql.Connection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import oracle.jdbc.xa.OracleXAResource;
import oracle.jdbc.xa.client.OracleXAConnection;

public class T4CXAConnection extends OracleXAConnection {
    T4CTTIOtxen otxen;
    T4CTTIOtxse otxse;
    T4CConnection physicalConnection;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    public T4CXAConnection(Connection _physicalConnection) throws XAException {
        super(_physicalConnection);

        this.physicalConnection = ((T4CConnection) _physicalConnection);
        this.xaResource = null;
    }

    public synchronized XAResource getXAResource() {
        try {
            if (this.xaResource == null) {
                this.xaResource = new T4CXAResource(this.physicalConnection, this,
                        this.isXAResourceTransLoose);

                if (this.logicalHandle != null) {
                    ((OracleXAResource) this.xaResource).setLogicalConnection(this.logicalHandle);
                }

            }

        } catch (Exception e) {
            this.xaResource = null;
        }

        return this.xaResource;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CXAConnection"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CXAConnection JD-Core Version: 0.6.0
 */