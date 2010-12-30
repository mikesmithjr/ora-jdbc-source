package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIoscid extends T4CTTIfun {
    static final int KPDUSR_CID_RESET = 1;
    static final int KPDUSR_PROXY_RESET = 2;
    static final int KPDUSR_PROXY_TKTSENT = 4;
    static final int KPDUSR_MODULE_RESET = 8;
    static final int KPDUSR_ACTION_RESET = 16;
    static final int KPDUSR_EXECID_RESET = 32;
    static final int KPDUSR_EXECSQ_RESET = 64;
    static final int KPDUSR_COLLCT_RESET = 128;
    static final int KPDUSR_CLINFO_RESET = 256;

    T4CTTIoscid(T4CMAREngine _mrengine) throws IOException, SQLException {
        super((byte) 17, 0);

        setMarshalingEngine(_mrengine);
        setFunCode((short) 135);
    }

    void marshal(boolean[] endToEndHasChanged, String[] endToEndValues,
            int endToEndECIDSequenceNumber) throws IOException, SQLException {
        marshalFunHeader();

        int cidflag = 64;

        if (endToEndHasChanged[0] != false) {
            cidflag |= 16;
        }
        if (endToEndHasChanged[1] != false) {
            cidflag |= 1;
        }
        if (endToEndHasChanged[2] != false) {
            cidflag |= 32;
        }
        if (endToEndHasChanged[3] != false) {
            cidflag |= 8;
        }

        this.meg.marshalNULLPTR();
        this.meg.marshalNULLPTR();
        this.meg.marshalUB4(cidflag);

        byte[] cidcid = null;

        if (endToEndHasChanged[1] != false) {
            this.meg.marshalPTR();

            if (endToEndValues[1] != null) {
                cidcid = this.meg.conv.StringToCharBytes(endToEndValues[1]);

                if (cidcid != null)
                    this.meg.marshalUB4(cidcid.length);
                else
                    this.meg.marshalUB4(0L);
            } else {
                this.meg.marshalUB4(0L);
            }
        } else {
            this.meg.marshalNULLPTR();
            this.meg.marshalUB4(0L);
        }

        byte[] cidmod = null;

        if (endToEndHasChanged[3] != false) {
            this.meg.marshalPTR();

            if (endToEndValues[3] != null) {
                cidmod = this.meg.conv.StringToCharBytes(endToEndValues[3]);

                if (cidmod != null)
                    this.meg.marshalUB4(cidmod.length);
                else
                    this.meg.marshalUB4(0L);
            } else {
                this.meg.marshalUB4(0L);
            }
        } else {
            this.meg.marshalNULLPTR();
            this.meg.marshalUB4(0L);
        }

        byte[] cidact = null;

        if (endToEndHasChanged[0] != false) {
            this.meg.marshalPTR();

            if (endToEndValues[0] != null) {
                cidact = this.meg.conv.StringToCharBytes(endToEndValues[0]);

                if (cidact != null)
                    this.meg.marshalUB4(cidact.length);
                else
                    this.meg.marshalUB4(0L);
            } else {
                this.meg.marshalUB4(0L);
            }
        } else {
            this.meg.marshalNULLPTR();
            this.meg.marshalUB4(0L);
        }

        byte[] cideci = null;

        if (endToEndHasChanged[2] != false) {
            this.meg.marshalPTR();

            if (endToEndValues[2] != null) {
                cideci = this.meg.conv.StringToCharBytes(endToEndValues[2]);

                if (cideci != null)
                    this.meg.marshalUB4(cideci.length);
                else
                    this.meg.marshalUB4(0L);
            } else {
                this.meg.marshalUB4(0L);
            }
        } else {
            this.meg.marshalNULLPTR();
            this.meg.marshalUB4(0L);
        }

        this.meg.marshalUB2(0);
        this.meg.marshalUB2(endToEndECIDSequenceNumber);
        this.meg.marshalNULLPTR();
        this.meg.marshalUB4(0L);
        this.meg.marshalNULLPTR();
        this.meg.marshalUB4(0L);
        this.meg.marshalNULLPTR();
        this.meg.marshalUB4(0L);

        if (cidcid != null) {
            this.meg.marshalCHR(cidcid);
        }
        if (cidmod != null) {
            this.meg.marshalCHR(cidmod);
        }
        if (cidact != null) {
            this.meg.marshalCHR(cidact);
        }
        if (cideci != null)
            this.meg.marshalCHR(cideci);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIoscid JD-Core Version: 0.6.0
 */