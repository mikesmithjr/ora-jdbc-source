// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: T4CTTIrxd.java

package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.BitSet;
import oracle.jdbc.oracore.OracleTypeADT;

// Referenced classes of package oracle.jdbc.driver:
// T4CTTIMsg, T4CMAREngine, DatabaseError, DBConversion,
// T4CRowidAccessor, Accessor, OracleLog

class T4CTTIrxd extends T4CTTIMsg {

    static final byte NO_BYTES[] = new byte[0];
    byte buffer[];
    byte bufferCHAR[];
    BitSet bvcColSent;
    int nbOfColumns;
    boolean bvcFound;
    boolean isFirstCol;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    T4CTTIrxd(T4CMAREngine _mrengine) {
        super((byte) 7);
        bvcColSent = null;
        nbOfColumns = 0;
        bvcFound = false;
        setMarshalingEngine(_mrengine);
        isFirstCol = true;
    }

    void init() {
        isFirstCol = true;
    }

    void setNumberOfColumns(int nbCol) {
        nbOfColumns = nbCol;
        bvcFound = false;
        bvcColSent = new BitSet(nbOfColumns);
    }

    void unmarshalBVC(int nbOfColumnSent) throws SQLException, IOException {
        int nbOfOnBits = 0;
        for (int i = 0; i < bvcColSent.length(); i++) {
            bvcColSent.clear(i);
        }

        int nbOfUB1 = nbOfColumns / 8 + (nbOfColumns % 8 == 0 ? 0 : 1);
        for (int ub1 = 0; ub1 < nbOfUB1; ub1++) {
            byte bvc = (byte) (meg.unmarshalUB1() & 0xff);
            for (int bit = 0; bit < 8; bit++) {
                if ((bvc & 1 << bit) != 0) {
                    bvcColSent.set(ub1 * 8 + bit);
                    nbOfOnBits++;
                }
            }

        }

        if (nbOfOnBits != nbOfColumnSent) {
            DatabaseError
                    .throwSqlException(-1,
                                       "INTERNAL ERROR: oracle.jdbc.driver.T4CTTIrxd.unmarshalBVC: bits missing in vector");
        }
        bvcFound = true;
    }

    void readBitVector(byte bitVec[]) throws SQLException, IOException {
        for (int i = 0; i < bvcColSent.length(); i++) {
            bvcColSent.clear(i);
        }

        if (bitVec == null || bitVec.length == 0) {
            bvcFound = false;
        } else {
            for (int i = 0; i < bitVec.length; i++) {
                byte bvc = bitVec[i];
                for (int bit = 0; bit < 8; bit++) {
                    if ((bvc & 1 << bit) != 0) {
                        bvcColSent.set(i * 8 + bit);
                    }
                }

            }

            bvcFound = true;
        }
    }

    void marshal(byte bindBytes[], char bindChars[], short bindIndicators[],
            int bindIndicatorSubRange, byte byteBufferForCharConversion[], DBConversion conversion,
            InputStream parameterStream[], byte parameterDatum[][], OracleTypeADT parameterOtype[],
            byte ibtBindBytes[], char ibtBindChars[], short ibtBindIndicators[], byte ioVector[],
            int rowId, int oacmxlArr[], boolean isPlsql, int returnParamMeta[]) throws IOException,
            SQLException {
        marshalTTCcode();
        int number_of_bind_positions = bindIndicators[bindIndicatorSubRange + 0] & 0xffff;
        int nbPostPonedColumns = 0;
        int indexOfPostPonedColumn[] = new int[3];
        int nbOfIbt = 0;
        for (int P = 0; P < number_of_bind_positions; P++) {
            boolean marshalNullObjectForDmlReturning = false;
            int subRangeOffset = bindIndicatorSubRange + 3 + 10 * P;
            int type = bindIndicators[subRangeOffset + 0] & 0xffff;
            if (ioVector != null && (ioVector[P] & 0x20) == 0) {
                if (type == 998) {
                    nbOfIbt++;
                }
                continue;
            }
            int valueLengthOffset = ((bindIndicators[subRangeOffset + 7] & 0xffff) << 16)
                    + (bindIndicators[subRangeOffset + 8] & 0xffff) + rowId;
            int nullOffset = ((bindIndicators[subRangeOffset + 5] & 0xffff) << 16)
                    + (bindIndicators[subRangeOffset + 6] & 0xffff) + rowId;
            int length = bindIndicators[valueLengthOffset] & 0xffff;
            short nullIndicator = bindIndicators[nullOffset];
            if (type == 116) {
                meg.marshalUB1((short) 1);
                meg.marshalUB1((short) 0);
                continue;
            }
            if (type == 994) {
                nullIndicator = -1;
                int dty = returnParamMeta[3 + P * 3 + 0];
                if (dty == 109) {
                    marshalNullObjectForDmlReturning = true;
                }
            }
            if (nullIndicator == -1) {
                if (type == 109 || marshalNullObjectForDmlReturning) {
                    meg.marshalDALC(NO_BYTES);
                    meg.marshalDALC(NO_BYTES);
                    meg.marshalDALC(NO_BYTES);
                    meg.marshalUB2(0);
                    meg.marshalUB4(0L);
                    meg.marshalUB2(1);
                    continue;
                }
                if (type == 998) {
                    nbOfIbt++;
                    meg.marshalUB4(0L);
                    continue;
                }
                if (type == 112 || type == 113 || type == 114) {
                    meg.marshalUB4(0L);
                    continue;
                }
                if (type != 8 && type != 24) {
                    if (oacmxlArr[P] != 0) {
                        meg.marshalUB1((short) 0);
                    }
                    continue;
                }
            }
            if (type == 8 || type == 24) {
                if (nbPostPonedColumns >= indexOfPostPonedColumn.length) {
                    int newindexOfPostPonedColumn[] = new int[indexOfPostPonedColumn.length << 1];
                    System.arraycopy(indexOfPostPonedColumn, 0, newindexOfPostPonedColumn, 0,
                                     indexOfPostPonedColumn.length);
                    indexOfPostPonedColumn = newindexOfPostPonedColumn;
                }
                indexOfPostPonedColumn[nbPostPonedColumns++] = P;
                continue;
            }
            int nbOfChars;
            int nbOfBytes;
            if (type == 998) {
                int nbOfElem = (ibtBindIndicators[6 + nbOfIbt * 8 + 4] & 0xffff) << 16 & 0xffff000
                        | ibtBindIndicators[6 + nbOfIbt * 8 + 5] & 0xffff;
                int offsetValue = (ibtBindIndicators[6 + nbOfIbt * 8 + 6] & 0xffff) << 16
                        & 0xffff000 | ibtBindIndicators[6 + nbOfIbt * 8 + 7] & 0xffff;
                int typeElem = ibtBindIndicators[6 + nbOfIbt * 8] & 0xffff;
                int elemMaxLen = ibtBindIndicators[6 + nbOfIbt * 8 + 1] & 0xffff;
                meg.marshalUB4(nbOfElem);
                for (int i = 0; i < nbOfElem; i++) {
                    int offsetValueElem = offsetValue + i * elemMaxLen;
                    if (typeElem == 9) {
                        nbOfChars = ibtBindChars[offsetValueElem] / 2;
                        nbOfBytes = 0;
                        nbOfBytes = conversion.javaCharsToCHARBytes(ibtBindChars,
                                                                    offsetValueElem + 1,
                                                                    byteBufferForCharConversion, 0,
                                                                    nbOfChars);
                        meg.marshalCLR(byteBufferForCharConversion, nbOfBytes);
                        continue;
                    }
                    length = ibtBindBytes[offsetValueElem];
                    if (length < 1) {
                        meg.marshalUB1((short) 0);
                    } else {
                        meg.marshalCLR(ibtBindBytes, offsetValueElem + 1, length);
                    }
                }

                nbOfIbt++;
                continue;
            }
            int bytePitch = bindIndicators[subRangeOffset + 1] & 0xffff;
            if (bytePitch != 0) {
                int offsetByteData = ((bindIndicators[subRangeOffset + 3] & 0xffff) << 16)
                        + (bindIndicators[subRangeOffset + 4] & 0xffff) + bytePitch * rowId;
                if (type == 6) {
                    offsetByteData++;
                    length--;
                } else if (type == 9) {
                    offsetByteData += 2;
                    length -= 2;
                } else if (type == 114 || type == 113 || type == 112) {
                    meg.marshalUB4(length);
                }
                if (type == 109 || type == 111) {
                    if (parameterDatum == null) {
                        DatabaseError
                                .throwSqlException(-1,
                                                   "INTERNAL ERROR: oracle.jdbc.driver.T4CTTIrxd.marshal: parameterDatum is null");
                    }
                    byte bytes[] = parameterDatum[P];
                    length = bytes != null ? bytes.length : 0;
                    if (type == 109) {
                        meg.marshalDALC(NO_BYTES);
                        meg.marshalDALC(NO_BYTES);
                        meg.marshalDALC(NO_BYTES);
                        meg.marshalUB2(0);
                        meg.marshalUB4(length);
                        meg.marshalUB2(1);
                    }
                    if (length > 0) {
                        meg.marshalCLR(bytes, 0, length);
                    }
                    continue;
                }
                if (type == 104) {
                    offsetByteData += 2;
                    long rowid[] = T4CRowidAccessor.stringToRowid(bindBytes, offsetByteData, 18);
                    short len = 14;
                    long rba = rowid[0];
                    int partitionID = (int) rowid[1];
                    short tableID = 0;
                    long blockNumber = rowid[2];
                    int slotNumber = (int) rowid[3];
                    if (rba == 0L && partitionID == 0 && blockNumber == 0L && slotNumber == 0) {
                        meg.marshalUB1((short) 0);
                    } else {
                        meg.marshalUB1(len);
                        meg.marshalUB4(rba);
                        meg.marshalUB2(partitionID);
                        meg.marshalUB1(tableID);
                        meg.marshalUB4(blockNumber);
                        meg.marshalUB2(slotNumber);
                    }
                    continue;
                }
                if (length < 1) {
                    meg.marshalUB1((short) 0);
                } else {
                    meg.marshalCLR(bindBytes, offsetByteData, length);
                }
                continue;
            }
            int formOfUse = bindIndicators[subRangeOffset + 9] & 0xffff;
            if (!isPlsql && oacmxlArr != null && oacmxlArr.length > P && oacmxlArr[P] > 4000) {
                if (nbPostPonedColumns >= indexOfPostPonedColumn.length) {
                    int newindexOfPostPonedColumn[] = new int[indexOfPostPonedColumn.length << 1];
                    System.arraycopy(indexOfPostPonedColumn, 0, newindexOfPostPonedColumn, 0,
                                     indexOfPostPonedColumn.length);
                    indexOfPostPonedColumn = newindexOfPostPonedColumn;
                }
                indexOfPostPonedColumn[nbPostPonedColumns++] = P;
                continue;
            }
            int charPitch = bindIndicators[subRangeOffset + 2] & 0xffff;
            int offsetCharData = ((bindIndicators[subRangeOffset + 3] & 0xffff) << 16)
                    + (bindIndicators[subRangeOffset + 4] & 0xffff) + charPitch * rowId + 1;
            if (type == 996) {
                int nbOfCHARBytes = bindChars[offsetCharData - 1];
                if (bufferCHAR == null || bufferCHAR.length < nbOfCHARBytes) {
                    bufferCHAR = new byte[nbOfCHARBytes];
                }
                for (int i = 0; i < nbOfCHARBytes; i++) {
                    bufferCHAR[i] = (byte) ((bindChars[offsetCharData + i / 2] & 0xff00) >> 8 & 0xff);
                    if (i < nbOfCHARBytes - 1) {
                        bufferCHAR[i + 1] = (byte) (bindChars[offsetCharData + i / 2] & 0xff & 0xff);
                        i++;
                    }
                }

                meg.marshalCLR(bufferCHAR, nbOfCHARBytes);
                if (bufferCHAR.length > 4000) {
                    bufferCHAR = null;
                }
                continue;
            }
            if (type == 96) {
                nbOfChars = length / 2;
                offsetCharData--;
            } else {
                nbOfChars = (length - 2) / 2;
            }
            nbOfBytes = 0;
            if (formOfUse == 2) {
                nbOfBytes = conversion.javaCharsToNCHARBytes(bindChars, offsetCharData,
                                                             byteBufferForCharConversion, 0,
                                                             nbOfChars);
            } else {
                nbOfBytes = conversion.javaCharsToCHARBytes(bindChars, offsetCharData,
                                                            byteBufferForCharConversion, 0,
                                                            nbOfChars);
            }
            meg.marshalCLR(byteBufferForCharConversion, nbOfBytes);
        }

        if (nbPostPonedColumns > 0) {
            for (int j = 0; j < nbPostPonedColumns; j++) {
                int P = indexOfPostPonedColumn[j];
                int subRangeOffset = bindIndicatorSubRange + 3 + 10 * P;
                int type = bindIndicators[subRangeOffset + 0] & 0xffff;
                int valueLengthOffset = ((bindIndicators[subRangeOffset + 7] & 0xffff) << 16)
                        + (bindIndicators[subRangeOffset + 8] & 0xffff) + rowId;
                int nullOffset = ((bindIndicators[subRangeOffset + 5] & 0xffff) << 16)
                        + (bindIndicators[subRangeOffset + 6] & 0xffff) + rowId;
                int length = bindIndicators[valueLengthOffset] & 0xffff;
                int charPitch = bindIndicators[subRangeOffset + 2] & 0xffff;
                int offsetCharData = ((bindIndicators[subRangeOffset + 3] & 0xffff) << 16)
                        + (bindIndicators[subRangeOffset + 4] & 0xffff) + charPitch * rowId + 1;
                if (type == 996) {
                    int nbOfCHARBytes = bindChars[offsetCharData - 1];
                    if (bufferCHAR == null || bufferCHAR.length < nbOfCHARBytes) {
                        bufferCHAR = new byte[nbOfCHARBytes];
                    }
                    for (int i = 0; i < nbOfCHARBytes; i++) {
                        bufferCHAR[i] = (byte) ((bindChars[offsetCharData + i / 2] & 0xff00) >> 8 & 0xff);
                        if (i < nbOfCHARBytes - 1) {
                            bufferCHAR[i + 1] = (byte) (bindChars[offsetCharData + i / 2] & 0xff & 0xff);
                            i++;
                        }
                    }

                    meg.marshalCLR(bufferCHAR, nbOfCHARBytes);
                    if (bufferCHAR.length > 4000) {
                        bufferCHAR = null;
                    }
                    continue;
                }
                if (type != 8 && type != 24) {
                    int nbOfChars;
                    if (type == 96) {
                        nbOfChars = length / 2;
                        offsetCharData--;
                    } else {
                        nbOfChars = (length - 2) / 2;
                    }
                    int formOfUse = bindIndicators[subRangeOffset + 9] & 0xffff;
                    int nbOfBytes = 0;
                    if (formOfUse == 2) {
                        nbOfBytes = conversion.javaCharsToNCHARBytes(bindChars, offsetCharData,
                                                                     byteBufferForCharConversion,
                                                                     0, nbOfChars);
                    } else {
                        nbOfBytes = conversion.javaCharsToCHARBytes(bindChars, offsetCharData,
                                                                    byteBufferForCharConversion, 0,
                                                                    nbOfChars);
                    }
                    meg.marshalCLR(byteBufferForCharConversion, nbOfBytes);
                    continue;
                }
                int positionOfStream = P;
                if (parameterStream == null) {
                    continue;
                }
                InputStream currentStream = parameterStream[positionOfStream];
                if (currentStream == null) {
                    continue;
                }
                int bufferLength = 64;
                if (buffer == null) {
                    buffer = new byte[bufferLength];
                }
                int bytesRead = 0;
                meg.marshalUB1((short) 254);
                boolean endOfStream = false;
                do {
                    if (endOfStream) {
                        break;
                    }
                    bytesRead = currentStream.read(buffer, 0, bufferLength);
                    if (bytesRead < bufferLength) {
                        endOfStream = true;
                    }
                    if (bytesRead > 0) {
                        meg.marshalUB1((short) (bytesRead & 0xff));
                        meg.marshalB1Array(buffer, 0, bytesRead);
                    }
                } while (true);
                meg.marshalSB1((byte) 0);
            }

        }
    }

    boolean unmarshal(Accessor accessors[], int definesLength) throws SQLException, IOException {
        return unmarshal(accessors, 0, definesLength);
    }

    boolean unmarshal(Accessor accessors[], int from_col, int definesLength) throws SQLException,
            IOException {
        if (from_col == 0) {
            isFirstCol = true;
        }
        for (int i = from_col; i < definesLength && i < accessors.length; i++) {
            if (accessors[i] == null) {
                continue;
            }
            if (accessors[i].physicalColumnIndex < 0) {
                int physicalIndex = 0;
                for (int j = 0; j < definesLength && j < accessors.length; j++) {
                    if (accessors[j] == null) {
                        continue;
                    }
                    accessors[j].physicalColumnIndex = physicalIndex;
                    if (!accessors[j].isUseLess) {
                        physicalIndex++;
                    }
                }

            }
            if (bvcFound && !accessors[i].isUseLess) {
                if (bvcColSent.get(accessors[i].physicalColumnIndex)) {
                    if (accessors[i].unmarshalOneRow()) {
                        return true;
                    }
                    isFirstCol = false;
                } else {
                    accessors[i].copyRow();
                }
                continue;
            }
            if (accessors[i].unmarshalOneRow()) {
                return true;
            }
            isFirstCol = false;
        }

        bvcFound = false;
        return false;
    }

    boolean unmarshal(Accessor accessors[], int row_number, int from_col, int i)
            throws SQLException, IOException {
        return false;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTTIrxd"));
        } catch (Exception e) {
        }
    }
}
