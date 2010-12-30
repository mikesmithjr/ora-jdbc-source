package oracle.net.nl.mesg;

import java.util.ListResourceBundle;

public class NLSR_th extends ListResourceBundle {
    static final Object[][] contents = {
            { "NoFile-04600", "TNS-04600: ไม่พบไฟลไ1�7 {0}" },
            { "FileReadError-04601",
                    "TNS-04601: ไ1�7กิดข้อผิดพลาดขณะอ่านไฟล์พารามิไ1�7ตอร์: {0} ข้อผิดพลาด: {1}" },
            { "SyntaxError-04602",
                    "TNS-04602: รูปแบบคำสั่งไม่ถูกต้อง: ต้องการค่า \"{0}\" ก่อนหรือทีไ1�7 {1}" },
            {
                    "UnexpectedChar-04603",
                    "TNS-04603: รูปแบบคำสั่งไม่ถูกต้อง: มีอักขระ \"{0}\" ที่ไม่คาดหมายขณะพาร์ค1�7 {1}" },
            { "ParseError-04604", "TNS-04604: ยังไม่ได้เริ่มต้นการพาร์ซออบไ1�7จกต์" },
            {
                    "UnexpectedChar-04605",
                    "TNS-04605: รูปแบบคำสั่งไม่ถูกต้อง: มีอักขระหรือลิไ1�7ทอรัค1�7 \"{0}\" ที่ไม่คาดหมายก่อนหรือที่ {1}" },
            { "NoLiterals-04610",
                    "TNS-04610: ไม่มีลิเทอรัลเหลืออยู่ ถึงจุดสิ้นสุดของคูไ1�7 NV" },
            { "InvalidChar-04611",
                    "TNS-04611: อักขระการทำงานต่อซึ่งอยู่หลังความเห็นไม่ถูกต้อค1�7" },
            { "NullRHS-04612", "TNS-04612: RHS ขอค1�7 \"{0}\" ไ1�7ป็นนัล" },
            { "Internal-04613", "TNS-04613: ข้อผิดพลาดภายใค1�7 {0}" },
            { "NoNVPair-04614", "TNS-04614: ไม่พบคู่ NV {0}" },
            { "InvalidRHS-04615", "TNS-04615: RHS ไม่ถูกต้องสำหรับ {0}" } };

    public Object[][] getContents() {
        return contents;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.net.nl.mesg.NLSR_th JD-Core Version: 0.6.0
 */