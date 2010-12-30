package oracle.sql;

import B;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import oracle.jdbc.OracleConnection;

public class TIMESTAMPTZ extends Datum
{
  private static int CENTURY_DEFAULT = 119;
  private static int DECADE_DEFAULT = 100;
  private static int MONTH_DEFAULT = 1;
  private static int DAY_DEFAULT = 1;

  private static int DECADE_INIT = 170;

  private static int HOUR_MILLISECOND = 3600000;

  private static int MINUTE_MILLISECOND = 60000;

  private static int JAVA_YEAR = 1970;
  private static int JAVA_MONTH = 0;
  private static int JAVA_DATE = 1;

  private static int SIZE_TIMESTAMPTZ = 13;

  private static int SIZE_TIMESTAMP = 11;

  private static int OFFSET_HOUR = 20;
  private static int OFFSET_MINUTE = 60;

  private static byte REGIONIDBIT = -128;

  public TIMESTAMPTZ()
  {
    super(initTimestamptz());
  }

  public TIMESTAMPTZ(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }

  public TIMESTAMPTZ(Connection paramConnection, java.sql.Date paramDate)
    throws SQLException
  {
    super(toBytes(paramConnection, paramDate));
  }

  public TIMESTAMPTZ(Connection paramConnection, java.sql.Date paramDate, Calendar paramCalendar)
    throws SQLException
  {
    super(toBytes(paramConnection, paramDate, paramCalendar));
  }

  public TIMESTAMPTZ(Connection paramConnection, Time paramTime)
    throws SQLException
  {
    super(toBytes(paramConnection, paramTime));
  }

  public TIMESTAMPTZ(Connection paramConnection, Time paramTime, Calendar paramCalendar)
    throws SQLException
  {
    super(toBytes(paramConnection, paramTime, paramCalendar));
  }

  public TIMESTAMPTZ(Connection paramConnection, Timestamp paramTimestamp)
    throws SQLException
  {
    super(toBytes(paramConnection, paramTimestamp));
  }

  public TIMESTAMPTZ(Connection paramConnection, Timestamp paramTimestamp, Calendar paramCalendar)
    throws SQLException
  {
    super(toBytes(paramConnection, paramTimestamp, paramCalendar));
  }

  public TIMESTAMPTZ(Connection paramConnection, DATE paramDATE)
    throws SQLException
  {
    super(toBytes(paramConnection, paramDATE));
  }

  public TIMESTAMPTZ(Connection paramConnection, String paramString)
    throws SQLException
  {
    super(toBytes(paramConnection, paramString));
  }

  public TIMESTAMPTZ(Connection paramConnection, String paramString, Calendar paramCalendar)
    throws SQLException
  {
    super(toBytes(paramConnection, paramString, paramCalendar));
  }

  public static java.sql.Date toDate(Connection paramConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    int[] arrayOfInt = new int[SIZE_TIMESTAMPTZ];

    for (int j = 0; j < SIZE_TIMESTAMPTZ; j++) {
      paramArrayOfByte[j] &= 255;
    }

    j = getJavaYear(arrayOfInt[0], arrayOfInt[1]);

    Calendar localCalendar = Calendar.getInstance();

    localCalendar.set(1, j);
    localCalendar.set(2, arrayOfInt[2] - 1);
    localCalendar.set(5, arrayOfInt[3]);
    localCalendar.set(11, arrayOfInt[4] - 1);
    localCalendar.set(12, arrayOfInt[5] - 1);
    localCalendar.set(13, arrayOfInt[6] - 1);
    localCalendar.set(14, 0);

    if ((arrayOfInt[11] & REGIONIDBIT) != 0)
    {
      int k = getHighOrderbits(arrayOfInt[11]);
      k += getLowOrderbits(arrayOfInt[12]);

      if (TIMEZONETAB.checkID(k)) {
        TIMEZONETAB.updateTable(paramConnection, k);
      }

      int i = TIMEZONETAB.getOffset(localCalendar, k);

      localCalendar.add(10, i / HOUR_MILLISECOND);
      localCalendar.add(12, i % HOUR_MILLISECOND / MINUTE_MILLISECOND);
    }
    else
    {
      localCalendar.add(10, arrayOfInt[11] - OFFSET_HOUR);
      localCalendar.add(12, arrayOfInt[12] - OFFSET_MINUTE);
    }

    long l = localCalendar.getTime().getTime();

    return new java.sql.Date(l);
  }

  public static Time toTime(Connection paramConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    int[] arrayOfInt = new int[SIZE_TIMESTAMPTZ];

    for (int j = 0; j < SIZE_TIMESTAMPTZ; j++) {
      paramArrayOfByte[j] &= 255;
    }

    Calendar localCalendar = Calendar.getInstance();

    int k = getJavaYear(arrayOfInt[0], arrayOfInt[1]);

    localCalendar.set(1, k);
    localCalendar.set(2, arrayOfInt[2] - 1);
    localCalendar.set(5, arrayOfInt[3]);
    localCalendar.set(11, arrayOfInt[4] - 1);
    localCalendar.set(12, arrayOfInt[5] - 1);
    localCalendar.set(13, arrayOfInt[6] - 1);
    localCalendar.set(14, 0);

    if ((arrayOfInt[11] & REGIONIDBIT) != 0)
    {
      int m = getHighOrderbits(arrayOfInt[11]);
      m += getLowOrderbits(arrayOfInt[12]);

      if (TIMEZONETAB.checkID(m)) {
        TIMEZONETAB.updateTable(paramConnection, m);
      }

      int i = TIMEZONETAB.getOffset(localCalendar, m);

      localCalendar.add(10, i / HOUR_MILLISECOND);
      localCalendar.add(12, i % HOUR_MILLISECOND / MINUTE_MILLISECOND);
    }
    else
    {
      localCalendar.add(10, arrayOfInt[11] - OFFSET_HOUR);
      localCalendar.add(12, arrayOfInt[12] - OFFSET_MINUTE);
    }

    return new Time(localCalendar.get(11), localCalendar.get(12), localCalendar.get(13));
  }

  public static DATE toDATE(Connection paramConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    int[] arrayOfInt = new int[SIZE_TIMESTAMPTZ];

    for (int j = 0; j < SIZE_TIMESTAMPTZ; j++) {
      paramArrayOfByte[j] &= 255;
    }

    Calendar localCalendar = Calendar.getInstance();

    int k = getJavaYear(arrayOfInt[0], arrayOfInt[1]);

    localCalendar.set(1, k);
    localCalendar.set(2, arrayOfInt[2] - 1);
    localCalendar.set(5, arrayOfInt[3]);
    localCalendar.set(11, arrayOfInt[4] - 1);
    localCalendar.set(12, arrayOfInt[5] - 1);
    localCalendar.set(13, arrayOfInt[6] - 1);
    localCalendar.set(14, 0);

    if ((arrayOfInt[11] & REGIONIDBIT) != 0)
    {
      int m = getHighOrderbits(arrayOfInt[11]);
      m += getLowOrderbits(arrayOfInt[12]);

      if (TIMEZONETAB.checkID(m)) {
        TIMEZONETAB.updateTable(paramConnection, m);
      }
      int i = TIMEZONETAB.getOffset(localCalendar, m);

      localCalendar.add(10, i / HOUR_MILLISECOND);
      localCalendar.add(12, i % HOUR_MILLISECOND / MINUTE_MILLISECOND);
    }
    else
    {
      localCalendar.add(10, arrayOfInt[11] - OFFSET_HOUR);
      localCalendar.add(12, arrayOfInt[12] - OFFSET_MINUTE);
    }

    long l = localCalendar.getTime().getTime();

    return new DATE(new java.sql.Date(l));
  }

  public static Timestamp toTimestamp(Connection paramConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    int[] arrayOfInt = new int[SIZE_TIMESTAMPTZ];

    for (int j = 0; j < SIZE_TIMESTAMPTZ; j++) {
      paramArrayOfByte[j] &= 255;
    }

    Calendar localCalendar1 = Calendar.getInstance();

    Calendar localCalendar2 = Calendar.getInstance();

    int k = getJavaYear(arrayOfInt[0], arrayOfInt[1]);

    localCalendar1.set(1, k);
    localCalendar1.set(2, arrayOfInt[2] - 1);
    localCalendar1.set(5, arrayOfInt[3]);
    localCalendar1.set(11, arrayOfInt[4] - 1);
    localCalendar1.set(12, arrayOfInt[5] - 1);
    localCalendar1.set(13, arrayOfInt[6] - 1);
    localCalendar1.set(14, 0);

    long l = localCalendar1.getTime().getTime();

    if ((arrayOfInt[11] & REGIONIDBIT) != 0)
    {
      int m = getHighOrderbits(arrayOfInt[11]);
      m += getLowOrderbits(arrayOfInt[12]);

      if (TIMEZONETAB.checkID(m)) {
        TIMEZONETAB.updateTable(paramConnection, m);
      }
      int i = TIMEZONETAB.getOffset(localCalendar1, m);

      l += i;
      TimeZone localTimeZone;
      if ((!localCalendar1.getTimeZone().inDaylightTime(localCalendar1.getTime())) && (localCalendar2.getTimeZone().inDaylightTime(new Timestamp(l)) == true))
      {
        localTimeZone = localCalendar2.getTimeZone();
        if ((localTimeZone instanceof SimpleTimeZone))
          l -= ((SimpleTimeZone)localTimeZone).getDSTSavings();
        else {
          l -= 3600000L;
        }

      }

      if ((localCalendar1.getTimeZone().inDaylightTime(localCalendar1.getTime()) == true) && (!localCalendar2.getTimeZone().inDaylightTime(new Timestamp(l))))
      {
        localTimeZone = localCalendar2.getTimeZone();
        if ((localTimeZone instanceof SimpleTimeZone))
          l += ((SimpleTimeZone)localTimeZone).getDSTSavings();
        else
          l += 3600000L;
      }
    }
    else
    {
      localCalendar1.add(10, arrayOfInt[11] - OFFSET_HOUR);
      localCalendar1.add(12, arrayOfInt[12] - OFFSET_MINUTE);

      l = localCalendar1.getTime().getTime();
    }

    Timestamp localTimestamp = new Timestamp(l);

    int n = arrayOfInt[7] << 24;
    n |= arrayOfInt[8] << 16;
    n |= arrayOfInt[9] << 8;
    n |= arrayOfInt[10] & 0xFF;

    localTimestamp.setNanos(n);

    return localTimestamp;
  }

  public static String toString(Connection paramConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    Timestamp localTimestamp = toTimestamp(paramConnection, paramArrayOfByte);

    Calendar localCalendar = Calendar.getInstance();

    localCalendar.setTime(localTimestamp);

    int i = localCalendar.get(1);
    int j = localCalendar.get(2) + 1;
    int k = localCalendar.get(5);
    int m = localCalendar.get(11);
    int n = localCalendar.get(12);
    int i1 = localCalendar.get(13);
    int i2 = 0;
    i2 = (paramArrayOfByte[7] & 0xFF) << 24;
    i2 |= (paramArrayOfByte[8] & 0xFF) << 16;
    i2 |= (paramArrayOfByte[9] & 0xFF) << 8;
    i2 |= paramArrayOfByte[10] & 0xFF & 0xFF;
    int i3;
    String str;
    if ((paramArrayOfByte[11] & REGIONIDBIT) != 0)
    {
      i3 = getHighOrderbits(paramArrayOfByte[11]);
      i3 += getLowOrderbits(paramArrayOfByte[12]);
      str = new String(ZONEIDMAP.getRegion(i3));
    }
    else
    {
      i3 = paramArrayOfByte[11] - OFFSET_HOUR;
      int i4 = paramArrayOfByte[12] - OFFSET_MINUTE;
      str = new String(i3 + ":" + i4);
    }

    return i + "-" + j + "-" + k + " " + m + "." + n + "." + i1 + "." + i2 + " " + str;
  }

  public Timestamp timestampValue(Connection paramConnection)
    throws SQLException
  {
    return toTimestamp(paramConnection, getBytes());
  }

  public byte[] toBytes()
  {
    return getBytes();
  }

  public static byte[] toBytes(Connection paramConnection, java.sql.Date paramDate)
    throws SQLException
  {
    if (paramDate == null) {
      return null;
    }
    byte[] arrayOfByte = new byte[SIZE_TIMESTAMPTZ];

    String str1 = ((OracleConnection)paramConnection).getSessionTimeZone();
    Calendar localCalendar;
    if (str1 == null)
      localCalendar = Calendar.getInstance();
    else {
      localCalendar = Calendar.getInstance(TimeZone.getTimeZone(str1));
    }
    localCalendar.setTime(paramDate);
    int j;
    if (localCalendar.getTimeZone().inDaylightTime(paramDate))
      j = 1;
    else {
      j = 0;
    }
    localCalendar.set(11, 0);
    localCalendar.set(12, 0);
    localCalendar.set(13, 0);
    int i;
    if (localCalendar.getTimeZone().getID() == "Custom")
    {
      i = localCalendar.getTimeZone().getRawOffset();
      arrayOfByte[11] = (byte)(i / HOUR_MILLISECOND + OFFSET_HOUR);
      arrayOfByte[12] = (byte)(i % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
    }
    else
    {
      String str2 = new String(localCalendar.getTimeZone().getID());

      int k = ZONEIDMAP.getID(str2);

      if (k == ZONEIDMAP.INV_ZONEID)
      {
        if (localCalendar.getTimeZone().useDaylightTime()) {
          throw new SQLException("Timezone not supported");
        }

        i = localCalendar.getTimeZone().getRawOffset();
        arrayOfByte[11] = (byte)(i / HOUR_MILLISECOND + OFFSET_HOUR);
        arrayOfByte[12] = (byte)(i % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
      }
      else
      {
        if (TIMEZONETAB.checkID(k)) {
          TIMEZONETAB.updateTable(paramConnection, k);
        }

        OffsetDST localOffsetDST = new OffsetDST();

        int m = TIMEZONETAB.getLocalOffset(localCalendar, k, localOffsetDST);
        i = localOffsetDST.getOFFSET();

        if ((j != 0) && (m == 1))
        {
          if (localOffsetDST.getDSTFLAG() == 0)
            i += HOUR_MILLISECOND;
          else {
            throw new SQLException();
          }

        }

        arrayOfByte[11] = (byte)setHighOrderbits(ZONEIDMAP.getID(str2));
        byte[] tmp343_340 = arrayOfByte; tmp343_340[11] = (byte)(tmp343_340[11] | REGIONIDBIT);
        arrayOfByte[12] = (byte)setLowOrderbits(ZONEIDMAP.getID(str2));
      }

    }

    localCalendar.add(10, -(i / HOUR_MILLISECOND));
    localCalendar.add(12, -(i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);

    arrayOfByte[0] = (byte)(localCalendar.get(1) / 100 + 100);
    arrayOfByte[1] = (byte)(localCalendar.get(1) % 100 + 100);
    arrayOfByte[2] = (byte)(localCalendar.get(2) + 1);
    arrayOfByte[3] = (byte)localCalendar.get(5);
    arrayOfByte[4] = (byte)(localCalendar.get(11) + 1);
    arrayOfByte[5] = (byte)(localCalendar.get(12) + 1);
    arrayOfByte[6] = (byte)(localCalendar.get(13) + 1);
    arrayOfByte[7] = 0;
    arrayOfByte[8] = 0;
    arrayOfByte[9] = 0;
    arrayOfByte[10] = 0;

    return arrayOfByte;
  }

  public static byte[] toBytes(Connection paramConnection, java.sql.Date paramDate, Calendar paramCalendar)
    throws SQLException
  {
    if (paramDate == null) {
      return null;
    }
    byte[] arrayOfByte = new byte[SIZE_TIMESTAMPTZ];

    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime(paramDate);
    int j;
    if (paramCalendar.getTimeZone().inDaylightTime(paramDate))
      j = 1;
    else {
      j = 0;
    }
    localCalendar.set(11, 0);
    localCalendar.set(12, 0);
    localCalendar.set(13, 0);
    int i;
    if (paramCalendar.getTimeZone().getID() == "Custom")
    {
      i = paramCalendar.getTimeZone().getRawOffset();
      arrayOfByte[11] = (byte)(i / HOUR_MILLISECOND + OFFSET_HOUR);
      arrayOfByte[12] = (byte)(i % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
    }
    else
    {
      String str = new String(paramCalendar.getTimeZone().getID());

      int k = ZONEIDMAP.getID(str);

      if (k == ZONEIDMAP.INV_ZONEID)
      {
        if (paramCalendar.getTimeZone().useDaylightTime()) {
          throw new SQLException("Timezone not supported");
        }

        i = paramCalendar.getTimeZone().getRawOffset();
        arrayOfByte[11] = (byte)(i / HOUR_MILLISECOND + OFFSET_HOUR);
        arrayOfByte[12] = (byte)(i % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
      }
      else
      {
        if (TIMEZONETAB.checkID(k)) {
          TIMEZONETAB.updateTable(paramConnection, k);
        }

        OffsetDST localOffsetDST = new OffsetDST();

        int m = TIMEZONETAB.getLocalOffset(localCalendar, k, localOffsetDST);
        i = localOffsetDST.getOFFSET();

        if ((j != 0) && (m == 1))
        {
          if (localOffsetDST.getDSTFLAG() == 0)
            i += HOUR_MILLISECOND;
          else {
            throw new SQLException();
          }
        }

        arrayOfByte[11] = (byte)setHighOrderbits(ZONEIDMAP.getID(str));
        byte[] tmp315_311 = arrayOfByte; tmp315_311[11] = (byte)(tmp315_311[11] | REGIONIDBIT);
        arrayOfByte[12] = (byte)setLowOrderbits(ZONEIDMAP.getID(str));
      }

    }

    localCalendar.add(10, -(i / HOUR_MILLISECOND));
    localCalendar.add(12, -(i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);

    arrayOfByte[0] = (byte)(localCalendar.get(1) / 100 + 100);
    arrayOfByte[1] = (byte)(localCalendar.get(1) % 100 + 100);
    arrayOfByte[2] = (byte)(localCalendar.get(2) + 1);
    arrayOfByte[3] = (byte)localCalendar.get(5);
    arrayOfByte[4] = (byte)(localCalendar.get(11) + 1);
    arrayOfByte[5] = (byte)(localCalendar.get(12) + 1);
    arrayOfByte[6] = (byte)(localCalendar.get(13) + 1);
    arrayOfByte[7] = 0;
    arrayOfByte[8] = 0;
    arrayOfByte[9] = 0;
    arrayOfByte[10] = 0;

    return arrayOfByte;
  }

  public static byte[] toBytes(Connection paramConnection, Time paramTime)
    throws SQLException
  {
    if (paramTime == null) {
      return null;
    }

    byte[] arrayOfByte = new byte[SIZE_TIMESTAMPTZ];

    String str1 = ((OracleConnection)paramConnection).getSessionTimeZone();
    Calendar localCalendar;
    if (str1 == null)
      localCalendar = Calendar.getInstance();
    else
      localCalendar = Calendar.getInstance(TimeZone.getTimeZone(str1));
    localCalendar.setTime(paramTime);
    int i;
    if (localCalendar.getTimeZone().inDaylightTime(paramTime))
      i = 1;
    else {
      i = 0;
    }
    localCalendar.set(1, (CENTURY_DEFAULT - 100) * 100 + DECADE_DEFAULT % 100);
    localCalendar.set(2, MONTH_DEFAULT - 1);
    localCalendar.set(5, DAY_DEFAULT);
    int j;
    if (localCalendar.getTimeZone().getID() == "Custom")
    {
      j = localCalendar.getTimeZone().getRawOffset();
      arrayOfByte[11] = (byte)(j / HOUR_MILLISECOND + OFFSET_HOUR);
      arrayOfByte[12] = (byte)(j % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
    }
    else
    {
      String str2 = new String(localCalendar.getTimeZone().getID());

      int k = ZONEIDMAP.getID(str2);

      if (k == ZONEIDMAP.INV_ZONEID)
      {
        if (localCalendar.getTimeZone().useDaylightTime()) {
          throw new SQLException("Timezone not supported");
        }

        j = localCalendar.getTimeZone().getRawOffset();
        arrayOfByte[11] = (byte)(j / HOUR_MILLISECOND + OFFSET_HOUR);
        arrayOfByte[12] = (byte)(j % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
      }
      else
      {
        if (TIMEZONETAB.checkID(k)) {
          TIMEZONETAB.updateTable(paramConnection, k);
        }

        OffsetDST localOffsetDST = new OffsetDST();

        int m = TIMEZONETAB.getLocalOffset(localCalendar, k, localOffsetDST);

        j = localOffsetDST.getOFFSET();

        arrayOfByte[11] = (byte)setHighOrderbits(ZONEIDMAP.getID(str2));
        byte[] tmp324_320 = arrayOfByte; tmp324_320[11] = (byte)(tmp324_320[11] | REGIONIDBIT);
        arrayOfByte[12] = (byte)setLowOrderbits(ZONEIDMAP.getID(str2));
      }

    }

    localCalendar.add(10, -(j / HOUR_MILLISECOND));
    localCalendar.add(12, -(j % HOUR_MILLISECOND) / MINUTE_MILLISECOND);

    arrayOfByte[0] = (byte)(localCalendar.get(1) / 100 + 100);
    arrayOfByte[1] = (byte)(localCalendar.get(1) % 100 + 100);
    arrayOfByte[2] = (byte)(localCalendar.get(2) + 1);
    arrayOfByte[3] = (byte)localCalendar.get(5);
    arrayOfByte[4] = (byte)(localCalendar.get(11) + 1);
    arrayOfByte[5] = (byte)(localCalendar.get(12) + 1);
    arrayOfByte[6] = (byte)(localCalendar.get(13) + 1);
    arrayOfByte[7] = 0;
    arrayOfByte[8] = 0;
    arrayOfByte[9] = 0;
    arrayOfByte[10] = 0;

    return arrayOfByte;
  }

  public static byte[] toBytes(Connection paramConnection, Time paramTime, Calendar paramCalendar)
    throws SQLException
  {
    if (paramTime == null) {
      return null;
    }

    Calendar localCalendar = Calendar.getInstance();

    byte[] arrayOfByte = new byte[SIZE_TIMESTAMPTZ];

    localCalendar.setTime(paramTime);
    int j;
    if (paramCalendar.getTimeZone().inDaylightTime(paramTime))
      j = 1;
    else {
      j = 0;
    }

    localCalendar.set(1, (CENTURY_DEFAULT - 100) * 100 + DECADE_DEFAULT % 100);
    localCalendar.set(2, MONTH_DEFAULT - 1);
    localCalendar.set(5, DAY_DEFAULT);
    int i;
    if (paramCalendar.getTimeZone().getID() == "Custom")
    {
      i = paramCalendar.getTimeZone().getRawOffset();
      arrayOfByte[11] = (byte)(i / HOUR_MILLISECOND + OFFSET_HOUR);
      arrayOfByte[12] = (byte)(i % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
    }
    else
    {
      String str = new String(paramCalendar.getTimeZone().getID());

      int k = ZONEIDMAP.getID(str);

      if (k == ZONEIDMAP.INV_ZONEID)
      {
        if (paramCalendar.getTimeZone().useDaylightTime()) {
          throw new SQLException("Timezone not supported");
        }

        i = paramCalendar.getTimeZone().getRawOffset();
        arrayOfByte[11] = (byte)(i / HOUR_MILLISECOND + OFFSET_HOUR);
        arrayOfByte[12] = (byte)(i % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
      }
      else
      {
        if (TIMEZONETAB.checkID(k)) {
          TIMEZONETAB.updateTable(paramConnection, k);
        }

        OffsetDST localOffsetDST = new OffsetDST();

        int m = TIMEZONETAB.getLocalOffset(localCalendar, k, localOffsetDST);

        i = localOffsetDST.getOFFSET();

        if ((j != 0) && (m == 1))
        {
          if (localOffsetDST.getDSTFLAG() == 0)
            i += HOUR_MILLISECOND;
          else {
            throw new SQLException();
          }
        }
        arrayOfByte[11] = (byte)setHighOrderbits(ZONEIDMAP.getID(str));
        byte[] tmp336_332 = arrayOfByte; tmp336_332[11] = (byte)(tmp336_332[11] | REGIONIDBIT);
        arrayOfByte[12] = (byte)setLowOrderbits(ZONEIDMAP.getID(str));
      }

    }

    localCalendar.add(11, -(i / HOUR_MILLISECOND));
    localCalendar.add(12, -(i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);

    arrayOfByte[0] = (byte)(localCalendar.get(1) / 100 + 100);
    arrayOfByte[1] = (byte)(localCalendar.get(1) % 100 + 100);
    arrayOfByte[2] = (byte)(localCalendar.get(2) + 1);
    arrayOfByte[3] = (byte)localCalendar.get(5);
    arrayOfByte[4] = (byte)(localCalendar.get(11) + 1);
    arrayOfByte[5] = (byte)(localCalendar.get(12) + 1);
    arrayOfByte[6] = (byte)(localCalendar.get(13) + 1);
    arrayOfByte[7] = 0;
    arrayOfByte[8] = 0;
    arrayOfByte[9] = 0;
    arrayOfByte[10] = 0;

    return arrayOfByte;
  }

  public static byte[] toBytes(Connection paramConnection, Timestamp paramTimestamp)
    throws SQLException
  {
    if (paramTimestamp == null) {
      return null;
    }
    byte[] arrayOfByte = new byte[SIZE_TIMESTAMPTZ];

    long l = 0L;

    String str1 = ((OracleConnection)paramConnection).getSessionTimeZone();
    Object localObject2;
    int n;
    if (str1 == null)
    {
      str1 = TimeZone.getDefault().getID();

      l = paramTimestamp.getTime();
    }
    else
    {
      int k = paramTimestamp.getNanos();

      Calendar localCalendar2 = Calendar.getInstance();

      localObject2 = TimeZone.getTimeZone(str1);

      localCalendar2.setTimeZone((TimeZone)localObject2);
      localCalendar2.setTime(paramTimestamp);

      n = localCalendar2.get(1);
      int i1 = localCalendar2.get(2) + 1;
      int i2 = localCalendar2.get(5);
      int i3 = localCalendar2.get(11);
      int i4 = localCalendar2.get(12);
      int i5 = localCalendar2.get(13);
      double d = k / 1000000;

      String str2 = new Integer(n).toString() + "/" + new Integer(i1).toString() + "/" + new Integer(i2).toString() + " " + new Integer(i3).toString() + ":" + new Integer(i4).toString() + ":" + new Integer(i5).toString() + ":" + new Double(d).toString();

      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("y/M/d H:m:s:S");

      java.util.Date localDate = null;
      try
      {
        localDate = localSimpleDateFormat.parse(str2);
      }
      catch (ParseException localParseException)
      {
        throw new SQLException(localParseException.getMessage());
      }

      l = localDate.getTime();
    }

    Calendar localCalendar1 = Calendar.getInstance(TimeZone.getTimeZone(str1));
    int j;
    if (localCalendar1.getTimeZone().inDaylightTime(paramTimestamp))
      j = 1;
    else {
      j = 0;
    }
    localCalendar1.setTime(paramTimestamp);
    int i;
    if (localCalendar1.getTimeZone().getID() == "Custom")
    {
      i = localCalendar1.getTimeZone().getRawOffset();
      arrayOfByte[11] = (byte)(i / HOUR_MILLISECOND + OFFSET_HOUR);
      arrayOfByte[12] = (byte)(i % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
    }
    else
    {
      localObject1 = new String(localCalendar1.getTimeZone().getID());

      int m = ZONEIDMAP.getID((String)localObject1);

      if (m == ZONEIDMAP.INV_ZONEID)
      {
        if (localCalendar1.getTimeZone().useDaylightTime()) {
          throw new SQLException("Timezone not supported");
        }

        i = localCalendar1.getTimeZone().getRawOffset();
        arrayOfByte[11] = (byte)(i / HOUR_MILLISECOND + OFFSET_HOUR);
        arrayOfByte[12] = (byte)(i % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
      }
      else
      {
        if (TIMEZONETAB.checkID(m)) {
          TIMEZONETAB.updateTable(paramConnection, m);
        }

        localObject2 = new OffsetDST();

        n = TIMEZONETAB.getLocalOffset(localCalendar1, m, (OffsetDST)localObject2);

        i = ((OffsetDST)localObject2).getOFFSET();

        if ((j != 0) && (n == 1))
        {
          if (((OffsetDST)localObject2).getDSTFLAG() == 0)
            i += HOUR_MILLISECOND;
          else {
            throw new SQLException();
          }

        }

        arrayOfByte[11] = (byte)setHighOrderbits(ZONEIDMAP.getID((String)localObject1));
        byte[] tmp618_615 = arrayOfByte; tmp618_615[11] = (byte)(tmp618_615[11] | REGIONIDBIT);
        arrayOfByte[12] = (byte)setLowOrderbits(ZONEIDMAP.getID((String)localObject1));
      }

    }

    l -= i;

    Object localObject1 = new Timestamp(l);
    localCalendar1.setTime((java.util.Date)localObject1);
    localCalendar1.setTimeZone(TimeZone.getDefault());

    arrayOfByte[0] = (byte)(localCalendar1.get(1) / 100 + 100);
    arrayOfByte[1] = (byte)(localCalendar1.get(1) % 100 + 100);
    arrayOfByte[2] = (byte)(localCalendar1.get(2) + 1);
    arrayOfByte[3] = (byte)localCalendar1.get(5);
    arrayOfByte[4] = (byte)(localCalendar1.get(11) + 1);
    arrayOfByte[5] = (byte)(localCalendar1.get(12) + 1);
    arrayOfByte[6] = (byte)(localCalendar1.get(13) + 1);

    arrayOfByte[7] = (byte)(paramTimestamp.getNanos() >> 24);
    arrayOfByte[8] = (byte)(paramTimestamp.getNanos() >> 16 & 0xFF);
    arrayOfByte[9] = (byte)(paramTimestamp.getNanos() >> 8 & 0xFF);
    arrayOfByte[10] = (byte)(paramTimestamp.getNanos() & 0xFF);

    return (B)(B)arrayOfByte;
  }

  public static byte[] toBytes(Connection paramConnection, Timestamp paramTimestamp, Calendar paramCalendar)
    throws SQLException
  {
    if (paramTimestamp == null) {
      return null;
    }
    byte[] arrayOfByte = new byte[SIZE_TIMESTAMPTZ];
    Calendar localCalendar = Calendar.getInstance();

    localCalendar.setTime(paramTimestamp);
    int j;
    if (paramCalendar.getTimeZone().inDaylightTime(paramTimestamp))
      j = 1;
    else
      j = 0;
    int i;
    if (paramCalendar.getTimeZone().getID() == "Custom")
    {
      i = paramCalendar.getTimeZone().getRawOffset();
      arrayOfByte[11] = (byte)(i / HOUR_MILLISECOND + OFFSET_HOUR);
      arrayOfByte[12] = (byte)(i % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
    }
    else
    {
      String str = new String(paramCalendar.getTimeZone().getID());

      int k = ZONEIDMAP.getID(str);

      if (k == ZONEIDMAP.INV_ZONEID)
      {
        if (paramCalendar.getTimeZone().useDaylightTime()) {
          throw new SQLException("Timezone not supported");
        }

        i = paramCalendar.getTimeZone().getRawOffset();
        arrayOfByte[11] = (byte)(i / HOUR_MILLISECOND + OFFSET_HOUR);
        arrayOfByte[12] = (byte)(i % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
      }
      else
      {
        if (TIMEZONETAB.checkID(k)) {
          TIMEZONETAB.updateTable(paramConnection, k);
        }

        OffsetDST localOffsetDST = new OffsetDST();

        int m = TIMEZONETAB.getLocalOffset(localCalendar, k, localOffsetDST);

        i = localOffsetDST.getOFFSET();

        if ((j != 0) && (m == 1))
        {
          if (localOffsetDST.getDSTFLAG() == 0)
            i += HOUR_MILLISECOND;
          else {
            throw new SQLException();
          }
        }
        arrayOfByte[11] = (byte)setHighOrderbits(ZONEIDMAP.getID(str));
        byte[] tmp293_290 = arrayOfByte; tmp293_290[11] = (byte)(tmp293_290[11] | REGIONIDBIT);
        arrayOfByte[12] = (byte)setLowOrderbits(ZONEIDMAP.getID(str));
      }

    }

    localCalendar.add(10, -(i / HOUR_MILLISECOND));
    localCalendar.add(12, -(i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);

    arrayOfByte[0] = (byte)(localCalendar.get(1) / 100 + 100);
    arrayOfByte[1] = (byte)(localCalendar.get(1) % 100 + 100);
    arrayOfByte[2] = (byte)(localCalendar.get(2) + 1);
    arrayOfByte[3] = (byte)localCalendar.get(5);
    arrayOfByte[4] = (byte)(localCalendar.get(11) + 1);
    arrayOfByte[5] = (byte)(localCalendar.get(12) + 1);
    arrayOfByte[6] = (byte)(localCalendar.get(13) + 1);

    arrayOfByte[7] = (byte)(paramTimestamp.getNanos() >> 24);
    arrayOfByte[8] = (byte)(paramTimestamp.getNanos() >> 16 & 0xFF);
    arrayOfByte[9] = (byte)(paramTimestamp.getNanos() >> 8 & 0xFF);
    arrayOfByte[10] = (byte)(paramTimestamp.getNanos() & 0xFF);

    return arrayOfByte;
  }

  public static byte[] toBytes(Connection paramConnection, DATE paramDATE)
    throws SQLException
  {
    if (paramDATE == null) {
      return null;
    }
    byte[] arrayOfByte = new byte[SIZE_TIMESTAMPTZ];

    Calendar localCalendar = Calendar.getInstance();

    localCalendar.setTime(DATE.toDate(paramDATE.toBytes()));
    int j;
    if (localCalendar.getTimeZone().inDaylightTime(DATE.toDate(paramDATE.toBytes())))
      j = 1;
    else
      j = 0;
    int i;
    if (localCalendar.getTimeZone().getID() == "Custom")
    {
      i = localCalendar.getTimeZone().getRawOffset();
      arrayOfByte[11] = (byte)(i / HOUR_MILLISECOND + OFFSET_HOUR);
      arrayOfByte[12] = (byte)(i % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
    }
    else
    {
      String str = new String(localCalendar.getTimeZone().getID());

      int k = ZONEIDMAP.getID(str);

      if (k == ZONEIDMAP.INV_ZONEID)
      {
        if (localCalendar.getTimeZone().useDaylightTime()) {
          throw new SQLException("Timezone not supported");
        }

        i = localCalendar.getTimeZone().getRawOffset();
        arrayOfByte[11] = (byte)(i / HOUR_MILLISECOND + OFFSET_HOUR);
        arrayOfByte[12] = (byte)(i % HOUR_MILLISECOND / MINUTE_MILLISECOND + OFFSET_MINUTE);
      }
      else
      {
        if (TIMEZONETAB.checkID(k)) {
          TIMEZONETAB.updateTable(paramConnection, k);
        }

        OffsetDST localOffsetDST = new OffsetDST();

        int m = TIMEZONETAB.getLocalOffset(localCalendar, k, localOffsetDST);

        i = localOffsetDST.getOFFSET();

        if ((j != 0) && (m == 1))
        {
          if (localOffsetDST.getDSTFLAG() == 0)
            i += HOUR_MILLISECOND;
          else {
            throw new SQLException();
          }

        }

        arrayOfByte[11] = (byte)setHighOrderbits(ZONEIDMAP.getID(str));
        byte[] tmp302_299 = arrayOfByte; tmp302_299[11] = (byte)(tmp302_299[11] | REGIONIDBIT);
        arrayOfByte[12] = (byte)setLowOrderbits(ZONEIDMAP.getID(str));
      }

    }

    localCalendar.add(10, -(i / HOUR_MILLISECOND));
    localCalendar.add(12, -(i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);

    arrayOfByte[0] = (byte)(localCalendar.get(1) / 100 + 100);
    arrayOfByte[1] = (byte)(localCalendar.get(1) % 100 + 100);
    arrayOfByte[2] = (byte)(localCalendar.get(2) + 1);
    arrayOfByte[3] = (byte)localCalendar.get(5);
    arrayOfByte[4] = (byte)(localCalendar.get(11) + 1);
    arrayOfByte[5] = (byte)(localCalendar.get(12) + 1);
    arrayOfByte[6] = (byte)(localCalendar.get(13) + 1);
    arrayOfByte[7] = 0;
    arrayOfByte[8] = 0;
    arrayOfByte[9] = 0;
    arrayOfByte[10] = 0;

    return arrayOfByte;
  }

  public static byte[] toBytes(Connection paramConnection, String paramString)
    throws SQLException
  {
    return toBytes(paramConnection, Timestamp.valueOf(paramString));
  }

  public static byte[] toBytes(Connection paramConnection, String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return toBytes(paramConnection, Timestamp.valueOf(paramString), paramCalendar);
  }

  public String stringValue(Connection paramConnection)
    throws SQLException
  {
    return toString(paramConnection, getBytes());
  }

  public java.sql.Date dateValue(Connection paramConnection)
    throws SQLException
  {
    return toDate(paramConnection, getBytes());
  }

  public Time timeValue(Connection paramConnection)
    throws SQLException
  {
    return toTime(paramConnection, getBytes());
  }

  private static byte[] initTimestamptz()
  {
    byte[] arrayOfByte = new byte[SIZE_TIMESTAMPTZ];
    Calendar localCalendar = Calendar.getInstance();

    arrayOfByte[0] = (byte)CENTURY_DEFAULT;
    arrayOfByte[1] = (byte)DECADE_INIT;
    arrayOfByte[2] = (byte)MONTH_DEFAULT;
    arrayOfByte[3] = (byte)DAY_DEFAULT;
    arrayOfByte[4] = 1;
    arrayOfByte[5] = 1;
    arrayOfByte[6] = 1;
    arrayOfByte[7] = 0;
    arrayOfByte[8] = 0;
    arrayOfByte[9] = 0;
    arrayOfByte[10] = 0;

    String str = new String(localCalendar.getTimeZone().getID());

    arrayOfByte[11] = (byte)setHighOrderbits(ZONEIDMAP.getID(str));
    arrayOfByte[11] = (byte)(arrayOfByte[11] | REGIONIDBIT);
    arrayOfByte[12] = (byte)setLowOrderbits(ZONEIDMAP.getID(str));

    return arrayOfByte;
  }

  public Object toJdbc()
    throws SQLException
  {
    return null;
  }

  public Object makeJdbcArray(int paramInt)
  {
    Timestamp[] arrayOfTimestamp = new Timestamp[paramInt];

    return arrayOfTimestamp;
  }

  public boolean isConvertibleTo(Class paramClass)
  {
    return (paramClass.getName().compareTo("java.sql.Date") == 0) || (paramClass.getName().compareTo("java.sql.Time") == 0) || (paramClass.getName().compareTo("java.sql.Timestamp") == 0) || (paramClass.getName().compareTo("java.lang.String") == 0);
  }

  private static int setHighOrderbits(int paramInt)
  {
    return (paramInt & 0x1FC0) >> 6;
  }

  private static int setLowOrderbits(int paramInt)
  {
    return (paramInt & 0x3F) << 2;
  }

  private static int getHighOrderbits(int paramInt)
  {
    return (paramInt & 0x7F) << 6;
  }

  private static int getLowOrderbits(int paramInt)
  {
    return (paramInt & 0xFC) >> 2;
  }

  private static int getJavaYear(int paramInt1, int paramInt2)
  {
    return (paramInt1 - 100) * 100 + (paramInt2 - 100);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.TIMESTAMPTZ
 * JD-Core Version:    0.6.0
 */