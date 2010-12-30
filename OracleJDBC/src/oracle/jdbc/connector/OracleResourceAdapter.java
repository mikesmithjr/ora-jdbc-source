package oracle.jdbc.connector;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;
import oracle.jdbc.driver.OracleLog;

public class OracleResourceAdapter implements ResourceAdapter {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:57_PDT_2005";

    public OracleResourceAdapter() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResourceAdapter.OracleResourceAdapter()",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void start(BootstrapContext bootstrapContext) throws ResourceAdapterInternalException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResourceAdapter.start(bootstrapConext = "
                    + bootstrapContext + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void stop() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResourceAdapter.stop()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void endpointActivation(MessageEndpointFactory mesgFactory, ActivationSpec activationSpec)
            throws NotSupportedException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleResourceAdapter.endpointActivation(mesgFactory = "
                                               + mesgFactory + ", activationSpec = "
                                               + activationSpec + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void endpointDeactivation(MessageEndpointFactory mesgFactory,
            ActivationSpec activationSpec) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleResourceAdapter.endpointDeactivation(mesgFactory = "
                                               + mesgFactory + ", activationSpec = "
                                               + activationSpec + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public XAResource[] getXAResources(ActivationSpec[] activationSpecs) throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResourceAdapter.getXAResources()", this);

            OracleLog.recursiveTrace = false;
        }

        return new XAResource[0];
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.connector.OracleResourceAdapter"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.connector.OracleResourceAdapter JD-Core Version: 0.6.0
 */