package oracle.jdbc.xa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import oracle.jdbc.driver.OracleLog;

public class OracleMultiPhaseArgs {
    int action = 0;
    int nsites = 0;
    Vector dbLinks = null;
    Vector xids = null;

    public OracleMultiPhaseArgs() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 256, 16, 16, "OracleMultiPhaseArgs()");
        }
    }

    public OracleMultiPhaseArgs(int _action, int _nsites, Vector _xids, Vector _dbLinks) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 256, 16, 16, "OracleMultiPhaseArgs(_action = " + _action
                    + ", _nsites = " + _nsites + ", _xids = " + _xids + ", _dbLinks = " + _dbLinks
                    + ")");
        }

        if (_nsites <= 1) {
            this.action = 0;
            this.nsites = 0;
            this.dbLinks = null;
            this.xids = null;
        } else if ((!_xids.isEmpty()) && (!_dbLinks.isEmpty()) && (_dbLinks.size() == _nsites)
                && (_xids.size() == 3 * _nsites)) {
            this.action = _action;
            this.nsites = _nsites;
            this.xids = _xids;
            this.dbLinks = _dbLinks;
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 256, 16, 16,
                            "OracleMultiPhaseArgs(_action, _nsites, _xids, _dbLinks) return");
        }
    }

    public OracleMultiPhaseArgs(byte[] inBytes) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 256, 16, 16, "OracleMultiPhaseArgs(inBytes = " + inBytes + ")");

            OracleLog.print(this, 256, 16, 64, "OracleMultiPhaseArgs(inBytes.length = "
                    + inBytes.length + ")");
        }

        ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(inBytes);
        DataInputStream dataIS = new DataInputStream(byteArrayIS);

        this.xids = new Vector();
        this.dbLinks = new Vector();
        try {
            this.action = dataIS.readInt();
            this.nsites = dataIS.readInt();

            int fmtid = dataIS.readInt();
            int gtridLen = dataIS.readInt();
            byte[] gtrid = new byte[gtridLen];
            int gtridRead = dataIS.read(gtrid, 0, gtridLen);

            for (int i = 0; i < this.nsites; i++) {
                int bqualLen = dataIS.readInt();
                byte[] bqual = new byte[bqualLen];
                int bqualRead = dataIS.read(bqual, 0, bqualLen);

                this.xids.addElement(new Integer(fmtid));
                this.xids.addElement(gtrid);
                this.xids.addElement(bqual);

                String dblink = dataIS.readUTF();

                this.dbLinks.addElement(dblink);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 256, 16, 16, "OracleMultiPhaseArgs(inBytes) return");
        }
    }

    public byte[] toByteArray() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 256, 16, 16, "toByteArray()");
        }

        return toByteArrayOS().toByteArray();
    }

    public ByteArrayOutputStream toByteArrayOS() {
        byte[] gtridCommon = null;
        int fmtidCommon = 0;

        if (OracleLog.TRACE) {
            OracleLog.print(this, 256, 16, 16, "toByteArrayOS()");
        }

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        DataOutputStream dataOS = new DataOutputStream(byteArrayOS);
        try {
            dataOS.writeInt(this.action);
            dataOS.writeInt(this.nsites);

            for (int i = 0; i < this.nsites; i++) {
                String dblink = (String) this.dbLinks.elementAt(i);
                int fmtid = ((Integer) this.xids.elementAt(i * 3)).intValue();
                byte[] gtrid = (byte[]) this.xids.elementAt(i * 3 + 1);
                byte[] bqual = (byte[]) this.xids.elementAt(i * 3 + 2);

                if (i == 0) {
                    fmtidCommon = fmtid;
                    gtridCommon = gtrid;

                    dataOS.writeInt(fmtid);
                    dataOS.writeInt(gtrid.length);
                    dataOS.write(gtrid, 0, gtrid.length);
                }

                dataOS.writeInt(bqual.length);
                dataOS.write(bqual, 0, bqual.length);
                dataOS.writeUTF(dblink);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 256, 16, 16, "toByteArrayOS() return: " + byteArrayOS);
        }

        return byteArrayOS;
    }

    public int getAction() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 256, 16, 16, "getAction() return: " + this.action);
        }

        return this.action;
    }

    public int getnsite() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 256, 16, 16, "getnsite() return: " + this.nsites);
        }

        return this.nsites;
    }

    public Vector getdbLinks() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 256, 16, 16, "getdbLinks() return: " + this.dbLinks);
        }

        return this.dbLinks;
    }

    public Vector getXids() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 256, 16, 16, "getXids() return: " + this.xids);
        }

        return this.xids;
    }

    public void printMPArgs() {
        OracleLog.print(this, 256, 16, 64, "-------printMPArgs entry-------");

        OracleLog.print(this, 256, 16, 64, "  action = " + this.action);

        OracleLog.print(this, 256, 16, 64, "  nsites = " + this.nsites);

        for (int i = 0; i < this.nsites; i++) {
            String dblink = (String) this.dbLinks.elementAt(i);
            int fmtid = ((Integer) this.xids.elementAt(i * 3)).intValue();
            byte[] gtrid = (byte[]) this.xids.elementAt(i * 3 + 1);
            byte[] bqual = (byte[]) this.xids.elementAt(i * 3 + 2);

            OracleLog.print(this, 256, 16, 64, "  fmtid  [" + i + "] = " + fmtid);

            OracleLog.print(this, 256, 16, 64, "  gtrid  [" + i + "] = ");

            printByteArray(gtrid);
            OracleLog.print(this, 256, 16, 64, "  bqual  [" + i + "] = ");

            printByteArray(bqual);
            OracleLog.print(this, 256, 16, 64, "  dblink [" + i + "] = " + dblink);
        }

        OracleLog.print(this, 256, 16, 64, "-------printMPArgs return-------");
    }

    private void printByteArray(byte[] bArray) {
        StringBuffer _buf = new StringBuffer();

        for (int i = 0; i < bArray.length; i++) {
            _buf.append(bArray[i] + " ");
        }
        OracleLog.print(this, 256, 16, 64, "         " + _buf.toString());
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.xa.OracleMultiPhaseArgs JD-Core Version: 0.6.0
 */