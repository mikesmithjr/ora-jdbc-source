package oracle.sql;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class TIMEZONETAB {
    private static Hashtable zonetab = new Hashtable();

    private static int OFFSET_HOUR = 20;
    private static int OFFSET_MINUTE = 60;

    private static int HOUR_MILLISECOND = 3600000;

    private static int MINUTE_MILLISECOND = 60000;

    private static int BYTE_SIZE = 10;

    public static void addTrans(byte[] paramArrayOfByte, int paramInt) {
        int[] arrayOfInt = new int[BYTE_SIZE];

        int i = paramArrayOfByte[0] & 0xFF;

        Vector localVector = new Vector(i);

        for (int j = 1; j < i * BYTE_SIZE; j += BYTE_SIZE) {
            for (int k = 0; k < BYTE_SIZE; k++) {
                arrayOfInt[k] = (paramArrayOfByte[(k + j)] & 0xFF);
            }

            int k = (arrayOfInt[0] - 100) * 100 + (arrayOfInt[1] - 100);

            Calendar localCalendar = Calendar.getInstance();
            localCalendar.set(1, k);
            localCalendar.set(2, arrayOfInt[2] - 1);
            localCalendar.set(5, arrayOfInt[3]);
            localCalendar.set(11, arrayOfInt[4] - 1);
            localCalendar.set(12, arrayOfInt[5] - 1);
            localCalendar.set(13, arrayOfInt[6] - 1);
            localCalendar.set(14, 0);

            long l = localCalendar.getTime().getTime();

            int m = (arrayOfInt[7] - OFFSET_HOUR) * HOUR_MILLISECOND
                    + (arrayOfInt[8] - OFFSET_MINUTE) * MINUTE_MILLISECOND;

            byte b = (byte) arrayOfInt[9];

            localVector.addElement(new OffsetDST(new Timestamp(l), m, b));
        }

        zonetab.put(new Integer(paramInt & 0x1FF), localVector);
    }

    public static byte getLocalOffset(Calendar paramCalendar, int paramInt, OffsetDST paramOffsetDST)
            throws NullPointerException, SQLException {
        Vector localVector = new Vector();

        int k = 0;

        byte i1 = 0;

        Calendar localCalendar1 = Calendar.getInstance();
        Calendar localCalendar2 = Calendar.getInstance();

        Calendar localCalendar3 = Calendar.getInstance();
        localCalendar3.setTime(new Timestamp(paramCalendar.getTime().getTime()));

        int i2 = 0;
        int i3 = 0;

        Calendar localCalendar4 = Calendar.getInstance();
        localCalendar4.set(1, localCalendar3.get(1));
        localCalendar4.set(2, localCalendar3.get(2));
        localCalendar4.set(5, 1);
        localCalendar4.set(11, 0);
        localCalendar4.set(12, 0);
        localCalendar4.set(13, 0);
        localCalendar4.set(14, 0);

        Timestamp localTimestamp = new Timestamp(localCalendar4.getTime().getTime());

        int i4 = 0;

        localVector = (Vector) zonetab.get(new Integer(paramInt & 0x1FF));

        Enumeration localEnumeration = localVector.elements();

        int i5 = 0;

        while (localEnumeration.hasMoreElements()) {
            if (localTimestamp.before(((OffsetDST) localEnumeration.nextElement()).getTimestamp())) {
                if (i5 == 0) {
                    throw new SQLException();
                }

                i2 = i5 - 1;
                i3 = i5;
                k = i5;

                break;
            }
            i5++;
        }

        if (i5 == localVector.size()) {
            k = i5 - 1;
        } else {
            int i = ((OffsetDST) localVector.elementAt(i2)).getOFFSET();
            int j = ((OffsetDST) localVector.elementAt(i3)).getOFFSET();

            int m = ((OffsetDST) localVector.elementAt(i2)).getDSTFLAG();
            int n = ((OffsetDST) localVector.elementAt(i3)).getDSTFLAG();

            localCalendar1.setTime(((OffsetDST) localVector.elementAt(i2)).getTimestamp());
            localCalendar2.setTime(((OffsetDST) localVector.elementAt(i3)).getTimestamp());

            localCalendar1.add(10, i / HOUR_MILLISECOND);
            localCalendar1.add(12, i % HOUR_MILLISECOND / MINUTE_MILLISECOND);

            localCalendar2.add(10, j / HOUR_MILLISECOND);
            localCalendar2.add(12, j % HOUR_MILLISECOND / MINUTE_MILLISECOND);

            boolean bool = localCalendar3.before(localCalendar1);

            if (bool) {
                k -= 2;
            } else if ((localCalendar3.before(localCalendar2))
                    && (!localCalendar3.equals(localCalendar2))) {
                k--;
            } else if (m < n) {
                localCalendar3.add(10, -1);

                if (localCalendar3.before(localCalendar1)) {
                    k--;
                }

            }

            if (m < n) {
                localCalendar2.add(10, -1);

                if (localCalendar2.after(paramCalendar)) {
                    i1 = 0;
                } else {
                    localCalendar2.add(10, 1);

                    if (localCalendar2.after(paramCalendar))
                        i1 = 1;
                    else
                        i1 = 0;
                }
            } else if (n < m) {
                localCalendar2.add(10, 1);

                if (localCalendar2.before(paramCalendar)) {
                    i1 = 0;
                } else {
                    localCalendar2.add(10, -1);

                    if (localCalendar2.after(paramCalendar))
                        i1 = 0;
                    else {
                        i1 = 1;
                    }
                }
            }
        }
        paramOffsetDST.setOFFSET(((OffsetDST) localVector.elementAt(k)).getOFFSET());
        paramOffsetDST.setDSTFLAG(((OffsetDST) localVector.elementAt(k)).getDSTFLAG());

        return i1;
    }

    public static int getOffset(Calendar paramCalendar, int paramInt) throws NullPointerException,
            SQLException {
        Vector localVector = new Vector();

        int i = 0;
        int j = 0;

        Timestamp localTimestamp = new Timestamp(paramCalendar.getTime().getTime());

        localVector = (Vector) zonetab.get(new Integer(paramInt & 0x1FF));

        Enumeration localEnumeration = localVector.elements();

        int k = 0;

        while (localEnumeration.hasMoreElements()) {
            if (localTimestamp.before(((OffsetDST) localEnumeration.nextElement()).getTimestamp())) {
                if (k != 0) {
                    break;
                }
                throw new SQLException();
            }

            k++;
        }
        i = k - 1;

        return ((OffsetDST) localVector.elementAt(i)).getOFFSET();
    }

    public static void displayTable(int paramInt) {
        Vector localVector = new Vector();

        Timestamp localTimestamp = new Timestamp(0L);

        int i = 0;

        int j = 0;

        localVector = (Vector) zonetab.get(new Integer(paramInt));

        Enumeration localEnumeration = localVector.elements();

        for (int k = 0; k < localVector.size(); k++) {
            System.out.print(((OffsetDST) localVector.elementAt(k)).getTimestamp().toString());
            System.out.print("    " + ((OffsetDST) localVector.elementAt(k)).getOFFSET());
            System.out.println("    " + ((OffsetDST) localVector.elementAt(k)).getDSTFLAG());
        }
    }

    public static boolean checkID(int paramInt) {
        int i = paramInt & 0x1FF;

        int[] arrayOfInt = new int[zonetab.size()];
        arrayOfInt = getIDs();

        int j = 0;

        while (j < zonetab.size()) {
            if (i == arrayOfInt[j])
                break;
            j++;
        }

        return j == zonetab.size();
    }

    public static void updateTable(Connection paramConnection, int paramInt) throws SQLException,
            NullPointerException {
        byte[] arrayOfByte = TRANSDUMP.getTransitions(paramConnection, paramInt);

        if (arrayOfByte == null) {
            throw new NullPointerException();
        }

        addTrans(arrayOfByte, paramInt);
    }

    private static int[] getIDs() {
        int[] arrayOfInt = new int[zonetab.size()];
        int i = 0;

        Enumeration localEnumeration = zonetab.keys();

        while (localEnumeration.hasMoreElements()) {
            arrayOfInt[(i++)] = ((Integer) localEnumeration.nextElement()).intValue();
        }
        return arrayOfInt;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.TIMEZONETAB JD-Core Version: 0.6.0
 */