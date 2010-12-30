package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIokeyval extends T4CTTIfun {
    static final byte KVASET_KPDUSR = 1;
    static final byte KVACLA_KPDUSR = 2;
    private byte[] namespaceByteArr;
    private char[] charArr;
    private byte[][] attrArr;
    private int[] attrArrSize;
    private byte[][] valueArr;
    private int[] valueArrSize;
    private byte[] kvalflg;

    T4CTTIokeyval(T4CMAREngine _mrengine) throws IOException, SQLException {
        super((byte) 17, 0);

        setMarshalingEngine(_mrengine);
        setFunCode((short) 154);

        this.namespaceByteArr = new byte[100];
        this.charArr = new char[100];

        this.attrArr = new byte[10][];
        this.attrArrSize = new int[10];
        this.valueArr = new byte[10][];
        this.valueArrSize = new int[10];

        this.kvalflg = new byte[10];
    }

    void marshal(Namespace namespace) throws IOException, SQLException {
        String namespaceStr = namespace.name;
        String[] keys = namespace.keys;
        String[] values = namespace.values;
        boolean clear = namespace.clear;
        int nbKeyVal = namespace.nbPairs;

        int namespaceByteArrSize = namespaceStr.length() * this.meg.conv.cMaxCharSize;
        if (namespaceByteArrSize > this.namespaceByteArr.length)
            this.namespaceByteArr = new byte[namespaceByteArrSize];
        if (namespaceStr.length() > this.charArr.length)
            this.charArr = new char[namespaceStr.length()];
        namespaceStr.getChars(0, namespaceStr.length(), this.charArr, 0);
        int nbNamespaceBytes = this.meg.conv.javaCharsToCHARBytes(this.charArr, 0,
                                                                  this.namespaceByteArr, 0,
                                                                  namespaceStr.length());

        if (nbKeyVal > 0) {
            if (nbKeyVal > this.attrArr.length) {
                this.attrArr = new byte[nbKeyVal][];
                this.attrArrSize = new int[nbKeyVal];
                this.valueArr = new byte[nbKeyVal][];
                this.valueArrSize = new int[nbKeyVal];
                this.kvalflg = new byte[nbKeyVal];
            }

            for (int i = 0; i < nbKeyVal; i++) {
                String attr = keys[i];
                String val = values[i];

                int attrByteArrSize = attr.length() * this.meg.conv.cMaxCharSize;
                if ((this.attrArr[i] == null) || (this.attrArr[i].length < attrByteArrSize)) {
                    this.attrArr[i] = new byte[attrByteArrSize];
                }
                int valueByteArrSize = val.length() * this.meg.conv.cMaxCharSize;
                if ((this.valueArr[i] == null) || (this.valueArr[i].length < valueByteArrSize)) {
                    this.valueArr[i] = new byte[valueByteArrSize];
                }
                if (attr.length() > this.charArr.length)
                    this.charArr = new char[attr.length()];
                attr.getChars(0, attr.length(), this.charArr, 0);
                this.attrArrSize[i] = this.meg.conv.javaCharsToCHARBytes(this.charArr, 0,
                                                                         this.attrArr[i], 0, attr
                                                                                 .length());

                if (val.length() > this.charArr.length)
                    this.charArr = new char[val.length()];
                val.getChars(0, val.length(), this.charArr, 0);
                this.valueArrSize[i] = this.meg.conv.javaCharsToCHARBytes(this.charArr, 0,
                                                                          this.valueArr[i], 0, val
                                                                                  .length());
            }

        }

        marshalFunHeader();

        this.meg.marshalPTR();
        this.meg.marshalUB4(nbNamespaceBytes);
        if (nbKeyVal > 0)
            this.meg.marshalPTR();
        else
            this.meg.marshalNULLPTR();
        this.meg.marshalUB4(nbKeyVal);
        int flag = 0;
        if (nbKeyVal > 0)
            flag = 1;
        if (clear)
            flag |= 2;
        this.meg.marshalUB2(flag);
        this.meg.marshalNULLPTR();

        this.meg.marshalCHR(this.namespaceByteArr, 0, nbNamespaceBytes);
        if (nbKeyVal > 0)
            this.meg.marshalKEYVAL(this.attrArr, this.attrArrSize, this.valueArr,
                                   this.valueArrSize, this.kvalflg, nbKeyVal);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIokeyval JD-Core Version: 0.6.0
 */