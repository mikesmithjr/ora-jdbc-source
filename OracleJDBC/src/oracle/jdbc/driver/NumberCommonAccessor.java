// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: NumberCommonAccessor.java

package oracle.jdbc.driver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Map;
import oracle.sql.Datum;
import oracle.sql.NUMBER;

// Referenced classes of package oracle.jdbc.driver:
// Accessor, DatabaseError, OracleStatement, PhysicalConnection

class NumberCommonAccessor extends Accessor {

    static final boolean GET_XXX_ROUNDS = false;
    int digs[];
    static final int LNXSGNBT = 128;
    static final byte LNXDIGS = 20;
    static final byte LNXEXPBS = 64;
    static final int LNXEXPMX = 127;
    static final BigDecimal BIGDEC_ZERO = BigDecimal.valueOf(0L);
    static final byte MAX_LONG_EXPONENT = 9;
    static final byte MIN_LONG_EXPONENT = 9;
    static final byte MAX_INT_EXPONENT = 4;
    static final byte MIN_INT_EXPONENT = 4;
    static final byte MAX_SHORT_EXPONENT = 2;
    static final byte MIN_SHORT_EXPONENT = 2;
    static final byte MAX_BYTE_EXPONENT = 1;
    static final byte MIN_BYTE_EXPONENT = 1;
    static final int MAX_LONG[] = { 202, 10, 23, 34, 73, 4, 69, 55, 78, 59, 8 };
    static final int MIN_LONG[] = { 53, 92, 79, 68, 29, 98, 33, 47, 24, 43, 93, 102 };
    static final int MAX_LONG_length = 11;
    static final int MIN_LONG_length = 12;
    static final double factorTable[] = { 9.9999999999999994E+253D, 1.0000000000000001E+252D,
            9.9999999999999992E+249D, 1E+248D, 1.0000000000000001E+246D, 1.0000000000000001E+244D,
            1.0000000000000001E+242D, 1E+240D, 1E+238D, 1.0000000000000001E+236D, 1E+234D,
            1.0000000000000001E+232D, 1.0000000000000001E+230D, 9.9999999999999992E+227D,
            9.9999999999999996E+225D, 9.9999999999999997E+223D, 1E+222D, 1E+220D,
            1.0000000000000001E+218D, 1E+216D, 9.9999999999999995E+213D, 9.9999999999999991E+211D,
            9.9999999999999993E+209D, 9.9999999999999998E+207D, 1E+206D, 9.9999999999999999E+203D,
            9.999999999999999E+201D, 9.9999999999999997E+199D, 1E+198D, 9.9999999999999995E+195D,
            9.9999999999999994E+193D, 1E+192D, 1.0000000000000001E+190D, 1E+188D,
            9.9999999999999998E+185D, 1E+184D, 1.0000000000000001E+182D, 1E+180D,
            1.0000000000000001E+178D, 1E+176D, 1.0000000000000001E+174D, 1.0000000000000001E+172D,
            1E+170D, 9.9999999999999993E+167D, 9.9999999999999994E+165D, 1E+164D,
            9.9999999999999994E+161D, 1E+160D, 9.9999999999999995E+157D, 9.9999999999999998E+155D,
            1E+154D, 1E+152D, 9.9999999999999998E+149D, 1E+148D, 9.9999999999999993E+145D, 1E+144D,
            1.0000000000000001E+142D, 1.0000000000000001E+140D, 1E+138D, 1.0000000000000001E+136D,
            9.9999999999999992E+133D, 9.9999999999999999E+131D, 1.0000000000000001E+130D,
            1.0000000000000001E+128D, 9.9999999999999992E+125D, 9.9999999999999995E+123D, 1E+122D,
            9.9999999999999998E+119D, 9.9999999999999997E+117D, 1E+116D, 1E+114D,
            9.9999999999999993E+111D, 1E+110D, 1E+108D, 1.0000000000000001E+106D, 1E+104D,
            9.9999999999999998E+101D, 1E+100D, 1E+098D, 1E+096D, 1E+094D, 1E+092D,
            9.9999999999999997E+089D, 9.9999999999999996E+087D, 1E+086D, 1.0000000000000001E+084D,
            9.9999999999999996E+081D, 1E+080D, 1E+078D, 1E+076D, 9.9999999999999995E+073D,
            9.9999999999999994E+071D, 1.0000000000000001E+070D, 9.9999999999999995E+067D,
            9.9999999999999995E+065D, 1E+064D, 1E+062D, 9.9999999999999995E+059D,
            9.9999999999999994E+057D, 1.0000000000000001E+056D, 1.0000000000000001E+054D,
            9.9999999999999999E+051D, 1.0000000000000001E+050D, 1E+048D, 9.9999999999999999E+045D,
            1.0000000000000001E+044D, 1E+042D, 1E+040D, 9.9999999999999998E+037D, 1E+036D,
            9.9999999999999995E+033D, 1.0000000000000001E+032D, 1E+030D, 9.9999999999999996E+027D,
            1E+026D, 9.9999999999999998E+023D, 1E+022D, 1E+020D, 1E+018D, 10000000000000000D,
            100000000000000D, 1000000000000D, 10000000000D, 100000000D, 1000000D, 10000D, 100D,
            1.0D, 0.01D, 0.0001D, 9.9999999999999995E-007D, 1E-008D, 1E-010D,
            9.9999999999999998E-013D, 1E-014D, 9.9999999999999998E-017D, 1.0000000000000001E-018D,
            9.9999999999999995E-021D, 1E-022D, 9.9999999999999992E-025D, 1E-026D,
            9.9999999999999997E-029D, 1.0000000000000001E-030D, 1.0000000000000001E-032D,
            9.9999999999999993E-035D, 9.9999999999999994E-037D, 9.9999999999999996E-039D,
            9.9999999999999993E-041D, 1E-042D, 9.9999999999999995E-045D, 1E-046D,
            9.9999999999999997E-049D, 1E-050D, 1E-052D, 1E-054D, 1E-056D, 1E-058D,
            9.9999999999999997E-061D, 1E-062D, 9.9999999999999997E-065D, 9.9999999999999998E-067D,
            1.0000000000000001E-068D, 1E-070D, 9.9999999999999997E-073D, 9.9999999999999996E-075D,
            9.9999999999999993E-077D, 1E-078D, 9.9999999999999996E-081D, 9.9999999999999996E-083D,
            1E-084D, 1.0000000000000001E-086D, 9.9999999999999993E-089D, 9.9999999999999999E-091D,
            9.9999999999999999E-093D, 9.9999999999999996E-095D, 9.9999999999999991E-097D,
            9.9999999999999994E-099D, 1E-100D, 9.9999999999999993E-103D, 9.9999999999999993E-105D,
            9.9999999999999994E-107D, 1E-108D, 1.0000000000000001E-110D, 9.9999999999999995E-113D,
            1.0000000000000001E-114D, 9.9999999999999999E-117D, 9.9999999999999999E-119D,
            9.9999999999999998E-121D, 1.0000000000000001E-122D, 9.9999999999999993E-125D,
            9.9999999999999995E-127D, 1.0000000000000001E-128D, 1.0000000000000001E-130D,
            9.9999999999999999E-133D, 1E-134D, 1E-136D, 1.0000000000000001E-138D,
            9.9999999999999998E-141D, 1E-142D, 9.9999999999999995E-145D, 1E-146D,
            9.9999999999999994E-149D, 1E-150D, 1.0000000000000001E-152D, 9.9999999999999997E-155D,
            1E-156D, 1.0000000000000001E-158D, 9.9999999999999999E-161D, 9.9999999999999995E-163D,
            9.9999999999999996E-165D, 1E-166D, 1E-168D, 9.9999999999999998E-171D, 1E-172D, 1E-174D,
            1E-176D, 9.9999999999999995E-179D, 1E-180D, 1E-182D, 1.0000000000000001E-184D,
            9.9999999999999991E-187D, 9.9999999999999995E-189D, 1E-190D, 1.0000000000000001E-192D,
            1E-194D, 1E-196D, 9.9999999999999991E-199D, 9.9999999999999998E-201D, 1E-202D, 1E-204D,
            1E-206D, 1.0000000000000001E-208D, 1E-210D, 9.9999999999999995E-213D,
            9.9999999999999991E-215D, 1E-216D, 1E-218D, 9.9999999999999999E-221D, 1E-222D, 1E-224D,
            9.9999999999999992E-227D, 1E-228D, 1E-230D, 1E-232D, 9.9999999999999996E-235D, 1E-236D,
            9.9999999999999999E-239D, 9.9999999999999997E-241D, 9.9999999999999997E-243D,
            9.9999999999999993E-245D, 9.9999999999999996E-247D, 9.9999999999999998E-249D,
            1.0000000000000001E-250D, 9.9999999999999994E-253D, 9.9999999999999991E-255D };
    static final double small10pow[] = { 1.0D, 10D, 100D, 1000D, 10000D, 100000D, 1000000D,
            10000000D, 100000000D, 1000000000D, 10000000000D, 100000000000D, 1000000000000D,
            10000000000000D, 100000000000000D, 1000000000000000D, 10000000000000000D, 1E+017D,
            1E+018D, 1E+019D, 1E+020D, 1E+021D, 1E+022D };
    static final int tablemax;
    static final double tablemaxexponent = 127D;
    static final double tableminexponent;
    static final int MANTISSA_SIZE = 53;
    static final int expdigs0[] = { 25597, 55634, 18440, 18324, 42485, 50370, 56862, 11593, 45703,
            57341, 10255, 12549, 59579, 5 };
    static final int expdigs1[] = { 50890, 19916, 24149, 23777, 11324, 41057, 14921, 56274, 30917,
            19462, 54968, 47943, 38791, 3872 };
    static final int expdigs2[] = { 24101, 29690, 40218, 29073, 29604, 22037, 27674, 9082, 56670,
            55244, 20865, 54874, 47573, 38 };
    static final int expdigs3[] = { 22191, 40873, 1607, 45622, 23883, 24544, 32988, 43530, 61694,
            55616, 43150, 32976, 27418, 25379 };
    static final int expdigs4[] = { 55927, 44317, 6569, 54851, 238, 63160, 51447, 12231, 55667,
            25459, 5674, 40962, 52047, 253 };
    static final int expdigs5[] = { 56264, 8962, 51839, 64773, 39323, 49783, 15587, 30924, 36601,
            56615, 27581, 36454, 35254, 2 };
    static final int expdigs6[] = { 21545, 25466, 59727, 37873, 13099, 7602, 15571, 49963, 37664,
            46896, 14328, 59258, 17403, 1663 };
    static final int expdigs7[] = { 12011, 4842, 3874, 57395, 38141, 46606, 49307, 60792, 31833,
            21440, 9318, 47123, 41461, 16 };
    static final int expdigs8[] = { 52383, 25023, 56409, 43947, 51036, 17420, 62725, 5735, 53692,
            44882, 64439, 36137, 24719, 10900 };
    static final int expdigs9[] = { 65404, 27119, 57580, 26653, 42453, 19179, 26186, 42000, 1847,
            62708, 14406, 12813, 247, 109 };
    static final int expdigs10[] = { 36698, 50078, 40552, 35000, 49576, 56552, 261, 49572, 31475,
            59609, 45363, 46658, 5900, 1 };
    static final int expdigs11[] = { 33321, 54106, 42443, 60698, 47535, 24088, 45785, 18352, 47026,
            40291, 5183, 35843, 24059, 714 };
    static final int expdigs12[] = { 12129, 44450, 22706, 34030, 37175, 8760, 31915, 56544, 23407,
            52176, 7260, 41646, 9415, 7 };
    static final int expdigs13[] = { 43054, 17160, 43698, 6780, 36385, 52800, 62346, 52747, 33988,
            2855, 31979, 38083, 44325, 4681 };
    static final int expdigs14[] = { 60723, 40803, 16165, 19073, 2985, 9703, 41911, 37227, 41627,
            1994, 38986, 27250, 53527, 46 };
    static final int expdigs15[] = { 36481, 57623, 45627, 58488, 53274, 7238, 2063, 31221, 62631,
            25319, 35409, 25293, 54667, 30681 };
    static final int expdigs16[] = { 52138, 47106, 3077, 4517, 41165, 38738, 39997, 10142, 13078,
            16637, 53438, 54647, 53630, 306 };
    static final int expdigs17[] = { 25425, 24719, 55736, 8564, 12208, 3664, 51518, 17140, 61079,
            30312, 2500, 30693, 4468, 3 };
    static final int expdigs18[] = { 58368, 65134, 52675, 3178, 26300, 7986, 11833, 515, 23109,
            63525, 29138, 19030, 50114, 2010 };
    static final int expdigs19[] = { 41216, 15724, 12323, 26246, 59245, 58406, 46648, 13767, 11372,
            15053, 61895, 48686, 7054, 20 };
    static final int expdigs20[] = { 0, 29248, 62416, 1433, 14025, 43846, 39905, 44375, 137, 47955,
            62409, 33386, 48983, 13177 };
    static final int expdigs21[] = { 0, 21264, 53708, 60962, 25043, 64008, 31200, 50906, 9831,
            56185, 43877, 36378, 50952, 131 };
    static final int expdigs22[] = { 0, 50020, 25440, 60247, 44814, 39961, 6865, 26068, 34832,
            9081, 17478, 44928, 20825, 1 };
    static final int expdigs23[] = { 0, 0, 52929, 10084, 25506, 6346, 61348, 31525, 52689, 61296,
            27615, 15903, 40426, 863 };
    static final int expdigs24[] = { 0, 16384, 24122, 53840, 43508, 13170, 51076, 37670, 58198,
            31414, 57292, 61762, 41691, 8 };
    static final int expdigs25[] = { 0, 0, 4096, 29077, 42481, 30581, 10617, 59493, 46251, 1892,
            5557, 4505, 52391, 5659 };
    static final int expdigs26[] = { 0, 0, 58368, 11431, 1080, 29797, 47947, 36639, 42405, 50481,
            29546, 9875, 39190, 56 };
    static final int expdigs27[] = { 0, 0, 0, 57600, 63028, 53094, 12749, 18174, 21993, 48265,
            14922, 59933, 4030, 37092 };
    static final int expdigs28[] = { 0, 0, 0, 576, 1941, 35265, 9302, 42780, 50682, 28007, 29640,
            28124, 60333, 370 };
    static final int expdigs29[] = { 0, 0, 0, 5904, 8539, 12149, 36793, 43681, 12958, 60573, 21267,
            35015, 46478, 3 };
    static final int expdigs30[] = { 0, 0, 0, 0, 7268, 50548, 47962, 3644, 22719, 26999, 41893,
            7421, 56711, 2430 };
    static final int expdigs31[] = { 0, 0, 0, 0, 7937, 49002, 60772, 28216, 38893, 55975, 63988,
            59711, 20227, 24 };
    static final int expdigs32[] = { 0, 0, 0, 16384, 38090, 63404, 55657, 8801, 62648, 13666,
            57656, 60234, 15930 };
    static final int expdigs33[] = { 0, 0, 0, 4096, 37081, 37989, 16940, 55138, 17665, 39458, 9751,
            20263, 159 };
    static final int expdigs34[] = { 0, 0, 0, 58368, 35104, 16108, 61773, 14313, 30323, 54789,
            57113, 38868, 1 };
    static final int expdigs35[] = { 0, 0, 0, 8448, 18701, 29652, 51080, 65023, 27172, 37903, 3192,
            1044 };
    static final int expdigs36[] = { 0, 0, 0, 37440, 63101, 2917, 39177, 50457, 25830, 50186,
            28867, 10 };
    static final int expdigs37[] = { 0, 0, 0, 56080, 45850, 37384, 3668, 12301, 38269, 18196, 6842 };
    static final int expdigs38[] = { 0, 0, 0, 46436, 13565, 50181, 34770, 37478, 5625, 27707, 68 };
    static final int expdigs39[] = { 0, 0, 0, 32577, 45355, 38512, 38358, 3651, 36101, 44841 };
    static final int expdigs40[] = { 0, 0, 16384, 28506, 5696, 56746, 15456, 50499, 27230, 448 };
    static final int expdigs41[] = { 0, 0, 4096, 285, 9232, 58239, 57170, 38515, 31729, 4 };
    static final int expdigs42[] = { 0, 0, 58368, 41945, 57108, 12378, 28752, 48226, 2938 };
    static final int expdigs43[] = { 0, 0, 24832, 47605, 49067, 23716, 61891, 25385, 29 };
    static final int expdigs44[] = { 0, 0, 8768, 2442, 50298, 23174, 19624, 19259 };
    static final int expdigs45[] = { 0, 0, 40720, 45899, 1813, 31689, 38862, 192 };
    static final int expdigs46[] = { 0, 0, 36452, 14221, 34752, 48813, 60681, 1 };
    static final int expdigs47[] = { 0, 0, 61313, 34220, 16731, 11629, 1262 };
    static final int expdigs48[] = { 0, 16384, 60906, 18036, 40144, 40748, 12 };
    static final int expdigs49[] = { 0, 4096, 609, 15909, 52830, 8271 };
    static final int expdigs50[] = { 0, 58368, 3282, 56520, 47058, 82 };
    static final int expdigs51[] = { 0, 41216, 52461, 7118, 54210 };
    static final int expdigs52[] = { 0, 45632, 51642, 6624, 542 };
    static final int expdigs53[] = { 0, 25360, 24109, 27591, 5 };
    static final int expdigs54[] = { 0, 42852, 46771, 3552 };
    static final int expdigs55[] = { 0, 28609, 34546, 35 };
    static final int expdigs56[] = { 16384, 4218, 23283 };
    static final int expdigs57[] = { 4096, 54437, 232 };
    static final int expdigs58[] = { 58368, 21515, 2 };
    static final int expdigs59[] = { 57600, 1525 };
    static final int expdigs60[] = { 16960, 15 };
    static final int expdigs61[] = { 10000 };
    static final int expdigs62[] = { 100 };
    static final int expdigs63[] = { 1 };
    static final int expdigs64[] = { 36700, 62914, 23592, 49807, 10485, 36700, 62914, 23592, 49807,
            10485, 36700, 62914, 23592, 655 };
    static final int expdigs65[] = { 14784, 18979, 33659, 19503, 2726, 9542, 629, 2202, 40475,
            10590, 4299, 47815, 36280, 6 };
    static final int expdigs66[] = { 16332, 9978, 33613, 31138, 35584, 64252, 13857, 14424, 62281,
            46279, 36150, 46573, 63392, 4294 };
    static final int expdigs67[] = { 6716, 24348, 22618, 23904, 21327, 3919, 44703, 19149, 28803,
            48959, 6259, 50273, 62237, 42 };
    static final int expdigs68[] = { 8471, 23660, 38254, 26440, 33662, 38879, 9869, 11588, 41479,
            23225, 60127, 24310, 32615, 28147 };
    static final int expdigs69[] = { 13191, 6790, 63297, 30410, 12788, 42987, 23691, 28296, 32527,
            38898, 41233, 4830, 31128, 281 };
    static final int expdigs70[] = { 4064, 53152, 62236, 29139, 46658, 12881, 31694, 4870, 19986,
            24637, 9587, 28884, 53395, 2 };
    static final int expdigs71[] = { 26266, 10526, 16260, 55017, 35680, 40443, 19789, 17356, 30195,
            55905, 28426, 63010, 44197, 1844 };
    static final int expdigs72[] = { 38273, 7969, 37518, 26764, 23294, 63974, 18547, 17868, 24550,
            41191, 17323, 53714, 29277, 18 };
    static final int expdigs73[] = { 16739, 37738, 38090, 26589, 43521, 1543, 15713, 10671, 11975,
            41533, 18106, 9348, 16921, 12089 };
    static final int expdigs74[] = { 14585, 61981, 58707, 16649, 25994, 39992, 28337, 17801, 37475,
            22697, 31638, 16477, 58496, 120 };
    static final int expdigs75[] = { 58472, 2585, 40564, 27691, 44824, 27269, 58610, 54572, 35108,
            30373, 35050, 10650, 13692, 1 };
    static final int expdigs76[] = { 50392, 58911, 41968, 49557, 29112, 29939, 43526, 63500, 55595,
            27220, 25207, 38361, 18456, 792 };
    static final int expdigs77[] = { 26062, 32046, 3696, 45060, 46821, 40931, 50242, 60272, 24148,
            20588, 6150, 44948, 60477, 7 };
    static final int expdigs78[] = { 12430, 30407, 320, 41980, 58777, 41755, 41041, 13609, 45167,
            13348, 40838, 60354, 19454, 5192 };
    static final int expdigs79[] = { 30926, 26518, 13110, 43018, 54982, 48258, 24658, 15209, 63366,
            11929, 20069, 43857, 60487, 51 };
    static final int expdigs80[] = { 51263, 54048, 48761, 48627, 30576, 49046, 4414, 61195, 61755,
            48474, 19124, 55906, 15511, 34028 };
    static final int expdigs81[] = { 39834, 11681, 47018, 3107, 64531, 54229, 41331, 41899, 51735,
            42427, 59173, 13010, 18505, 340 };
    static final int expdigs82[] = { 27268, 6670, 31272, 9861, 45865, 10372, 12865, 62678, 23454,
            35158, 20252, 29621, 26399, 3 };
    static final int expdigs83[] = { 57738, 46147, 66, 48154, 11239, 21430, 55809, 46003, 15044,
            25138, 52780, 48043, 4883, 2230 };
    static final int expdigs84[] = { 20893, 62065, 64225, 52254, 59094, 55919, 60195, 5702, 48647,
            50058, 7736, 41768, 19709, 22 };
    static final int expdigs85[] = { 37714, 32321, 45840, 36031, 33290, 47121, 5146, 28127, 9887,
            25390, 52929, 2698, 1073, 14615 };
    static final int expdigs86[] = { 35111, 8187, 18153, 56721, 40309, 59453, 51824, 4868, 45974,
            3530, 43783, 8546, 9841, 146 };
    static final int expdigs87[] = { 23288, 61030, 42779, 19572, 29894, 47780, 45082, 32816, 43713,
            33458, 25341, 63655, 30244, 1 };
    static final int expdigs88[] = { 58138, 33000, 62869, 37127, 61799, 298, 46353, 5693, 63898,
            62040, 989, 23191, 53065, 957 };
    static final int expdigs89[] = { 42524, 32442, 36673, 15444, 22900, 658, 61412, 32824, 21610,
            64190, 1975, 11373, 37886, 9 };
    static final int expdigs90[] = { 26492, 4357, 32437, 10852, 34233, 53968, 55056, 34692, 64553,
            38226, 41929, 21646, 6667, 6277 };
    static final int expdigs91[] = { 61213, 698, 16053, 50571, 2963, 50347, 13657, 48188, 46520,
            19387, 33187, 25775, 50529, 62 };
    static final int expdigs92[] = { 42864, 54351, 45226, 20476, 23443, 17724, 3780, 44701, 52910,
            23402, 28374, 46862, 40234, 41137 };
    static final int expdigs93[] = { 23366, 62147, 58123, 44113, 55284, 39498, 3314, 9622, 9704,
            27759, 25187, 43722, 24650, 411 };
    static final int expdigs94[] = { 38899, 44530, 19586, 37141, 1863, 9570, 32801, 31553, 51870,
            62536, 51369, 30583, 7455, 4 };
    static final int expdigs95[] = { 10421, 4321, 43699, 3472, 65252, 17057, 13858, 29819, 14733,
            21490, 40602, 31315, 65186, 2695 };
    static final int expdigs96[] = { 6002, 54438, 29272, 34113, 17036, 25074, 36183, 953, 25051,
            12011, 20722, 4245, 62911, 26 };
    static final int expdigs97[] = { 14718, 45935, 8408, 42891, 21312, 56531, 44159, 45581, 20325,
            36295, 35509, 24455, 30844, 17668 };
    static final int expdigs98[] = { 54542, 45023, 23021, 3050, 31015, 20881, 50904, 40432, 33626,
            14125, 44264, 60537, 44872, 176 };
    static final int expdigs99[] = { 60183, 8969, 14648, 17725, 11451, 50016, 34587, 46279, 19341,
            42084, 16826, 5848, 50256, 1 };
    static final int expdigs100[] = { 64999, 53685, 60382, 19151, 25736, 5357, 31302, 23283, 14225,
            52622, 56781, 39489, 60351, 1157 };
    static final int expdigs101[] = { 1305, 4469, 39270, 18541, 63827, 59035, 54707, 16616, 32910,
            48367, 64137, 2360, 37959, 11 };
    static final int expdigs102[] = { 45449, 32125, 19705, 56098, 51958, 5225, 18285, 13654, 9341,
            25888, 50946, 26855, 36068, 7588 };
    static final int expdigs103[] = { 27324, 53405, 43450, 25464, 3796, 3329, 46058, 53220, 26307,
            53998, 33932, 23861, 58032, 75 };
    static final int expdigs104[] = { 63080, 50735, 1844, 21406, 57926, 63607, 24936, 52889, 23469,
            64488, 539, 8859, 21210, 49732 };
    static final int expdigs105[] = { 62890, 39828, 3950, 32982, 39245, 21607, 40226, 50991, 18584,
            10475, 59643, 40720, 21183, 497 };
    static final int expdigs106[] = { 37329, 64623, 11835, 985, 46923, 48712, 28582, 21481, 28366,
            41392, 13703, 49559, 63781, 4 };
    static final int expdigs107[] = { 3316, 60011, 41933, 47959, 54404, 39790, 12283, 941, 46090,
            42226, 18108, 38803, 16879, 3259 };
    static final int expdigs108[] = { 46563, 56305, 5006, 45044, 49040, 12849, 778, 6563, 46336,
            3043, 7390, 2354, 38835, 32 };
    static final int expdigs109[] = { 28653, 3742, 33331, 2671, 39772, 29981, 56489, 1973, 26280,
            26022, 56391, 56434, 57039, 21359 };
    static final int expdigs110[] = { 9461, 17732, 7542, 26241, 8917, 24548, 61513, 13126, 59245,
            41547, 1874, 41852, 39236, 213 };
    static final int expdigs111[] = { 36794, 22459, 63645, 14024, 42032, 53329, 25518, 11272,
            18287, 20076, 62933, 3039, 8912, 2 };
    static final int expdigs112[] = { 14926, 15441, 32337, 42579, 26354, 35154, 22815, 36955,
            12564, 8047, 856, 41917, 55080, 1399 };
    static final int expdigs113[] = { 8668, 50617, 10153, 17465, 1574, 28532, 15301, 58041, 38791,
            60373, 663, 29255, 65431, 13 };
    static final int expdigs114[] = { 21589, 32199, 24754, 45321, 9349, 26230, 35019, 37508, 20896,
            42986, 31405, 12458, 65173, 9173 };
    static final int expdigs115[] = { 46746, 1632, 61196, 50915, 64318, 41549, 2971, 23968, 59191,
            58756, 61917, 779, 48493, 91 };
    static final int expdigs116[] = { 1609, 63382, 15744, 15685, 51627, 56348, 33838, 52458, 44148,
            11077, 56293, 41906, 45227, 60122 };
    static final int expdigs117[] = { 19676, 45198, 6055, 38823, 8380, 49060, 17377, 58196, 43039,
            21737, 59545, 12870, 14870, 601 };
    static final int expdigs118[] = { 4128, 2418, 28241, 13495, 26298, 3767, 31631, 5169, 8950,
            27087, 56956, 4060, 804, 6 };
    static final int expdigs119[] = { 39930, 40673, 19029, 54677, 38145, 23200, 41325, 24564,
            24955, 54484, 23863, 52998, 13147, 3940 };
    static final int expdigs120[] = { 3676, 24655, 34924, 27416, 23974, 887, 10899, 4833, 21221,
            28725, 19899, 57546, 26345, 39 };
    static final int expdigs121[] = { 28904, 41324, 18596, 42292, 12070, 52013, 30810, 61057,
            55753, 32324, 38953, 6752, 32688, 25822 };
    static final int expdigs122[] = { 42232, 26627, 2807, 27948, 50583, 49016, 32420, 64180, 3178,
            3600, 21361, 52496, 14744, 258 };
    static final int expdigs123[] = { 2388, 59904, 28863, 7488, 31963, 8354, 47510, 15059, 2653,
            58363, 31670, 21496, 38158, 2 };
    static final int expdigs124[] = { 50070, 5266, 26158, 10774, 15148, 6873, 30230, 33898, 63720,
            51799, 4515, 50124, 19875, 1692 };
    static final int expdigs125[] = { 54240, 3984, 12058, 2729, 13914, 11865, 38313, 39660, 10467,
            20834, 36745, 57517, 60491, 16 };
    static final int expdigs126[] = { 5387, 58214, 9214, 13883, 14445, 34873, 21745, 13490, 23334,
            25008, 58535, 19372, 44484, 11090 };
    static final int expdigs127[] = { 27578, 64807, 12543, 794, 13907, 61297, 12013, 64360, 15961,
            20566, 24178, 15922, 59427, 110 };
    static final int expdigs128[] = { 49427, 41935, 46000, 59645, 45358, 51075, 15848, 32756,
            38170, 14623, 35631, 57175, 7147, 1 };
    static final int expdigs129[] = { 33941, 39160, 55469, 45679, 22878, 60091, 37210, 18508, 1638,
            57398, 65026, 41643, 54966, 726 };
    static final int expdigs130[] = { 60632, 24639, 41842, 62060, 20544, 59583, 52800, 1495, 48513,
            43827, 10480, 1727, 17589, 7 };
    static final int expdigs131[] = { 5590, 60244, 53985, 26632, 53049, 33628, 58267, 54922, 21641,
            62744, 58109, 2070, 26887, 4763 };
    static final int expdigs132[] = { 62970, 37957, 34618, 29757, 24123, 2302, 17622, 58876, 44780,
            6525, 33349, 36065, 41556, 47 };
    static final int expdigs133[] = { 1615, 24878, 20040, 11487, 23235, 27766, 59005, 57847, 60881,
            11588, 63635, 61281, 31817, 31217 };
    static final int expdigs134[] = { 14434, 2870, 65081, 44023, 40864, 40254, 47120, 6476, 32066,
            23053, 17020, 19618, 11459, 312 };
    static final int expdigs135[] = { 43398, 40005, 36695, 8304, 12205, 16131, 42414, 38075, 63890,
            2851, 61774, 59833, 7978, 3 };
    static final int expdigs136[] = { 56426, 22060, 15473, 31824, 19088, 38788, 64386, 12875,
            35770, 65519, 11824, 19623, 56959, 2045 };
    static final int expdigs137[] = { 16292, 32333, 10640, 47504, 29026, 30534, 23581, 6682, 10188,
            24248, 44027, 51969, 30060, 20 };
    static final int expdigs138[] = { 29432, 37518, 55373, 2727, 33243, 22572, 16689, 35625, 34145,
            15830, 59880, 32552, 52948, 13407 };
    static final int expdigs139[] = { 61898, 27244, 41841, 33450, 18682, 13988, 24415, 11497, 1652,
            34237, 34677, 325, 5117, 134 };
    static final int expdigs140[] = { 16347, 3549, 48915, 22616, 21158, 51913, 32356, 21086, 3293,
            8862, 1002, 26873, 22333, 1 };
    static final int expdigs141[] = { 25966, 63733, 28215, 31946, 40858, 58538, 11004, 6877, 6109,
            3965, 35478, 37365, 45488, 878 };
    static final int expdigs142[] = { 45479, 34060, 17321, 19980, 1719, 16314, 29601, 8588, 58388,
            22321, 14117, 63288, 51572, 8 };
    static final int expdigs143[] = { 46861, 47640, 11481, 23766, 46730, 53756, 8682, 60589, 42028,
            27453, 29714, 31598, 39954, 5758 };
    static final int expdigs144[] = { 29304, 58803, 51232, 27762, 60760, 17576, 19092, 26820,
            11561, 48771, 6850, 27841, 38410, 57 };
    static final int expdigs145[] = { 2916, 49445, 34666, 46387, 18627, 58279, 60468, 190, 3545,
            51889, 51605, 47909, 40910, 37739 };
    static final int expdigs146[] = { 19034, 62098, 15419, 33887, 38852, 53011, 28129, 37357,
            11176, 48360, 9035, 9654, 25968, 377 };
    static final int expdigs147[] = { 25094, 10451, 7363, 55389, 57404, 27399, 11422, 39695, 28947,
            12935, 61694, 26310, 50722, 3 };
    static final int expdigstable[][] = { expdigs0, expdigs1, expdigs2, expdigs3, expdigs4,
            expdigs5, expdigs6, expdigs7, expdigs8, expdigs9, expdigs10, expdigs11, expdigs12,
            expdigs13, expdigs14, expdigs15, expdigs16, expdigs17, expdigs18, expdigs19, expdigs20,
            expdigs21, expdigs22, expdigs23, expdigs24, expdigs25, expdigs26, expdigs27, expdigs28,
            expdigs29, expdigs30, expdigs31, expdigs32, expdigs33, expdigs34, expdigs35, expdigs36,
            expdigs37, expdigs38, expdigs39, expdigs40, expdigs41, expdigs42, expdigs43, expdigs44,
            expdigs45, expdigs46, expdigs47, expdigs48, expdigs49, expdigs50, expdigs51, expdigs52,
            expdigs53, expdigs54, expdigs55, expdigs56, expdigs57, expdigs58, expdigs59, expdigs60,
            expdigs61, expdigs62, expdigs63, expdigs64, expdigs65, expdigs66, expdigs67, expdigs68,
            expdigs69, expdigs70, expdigs71, expdigs72, expdigs73, expdigs74, expdigs75, expdigs76,
            expdigs77, expdigs78, expdigs79, expdigs80, expdigs81, expdigs82, expdigs83, expdigs84,
            expdigs85, expdigs86, expdigs87, expdigs88, expdigs89, expdigs90, expdigs91, expdigs92,
            expdigs93, expdigs94, expdigs95, expdigs96, expdigs97, expdigs98, expdigs99,
            expdigs100, expdigs101, expdigs102, expdigs103, expdigs104, expdigs105, expdigs106,
            expdigs107, expdigs108, expdigs109, expdigs110, expdigs111, expdigs112, expdigs113,
            expdigs114, expdigs115, expdigs116, expdigs117, expdigs118, expdigs119, expdigs120,
            expdigs121, expdigs122, expdigs123, expdigs124, expdigs125, expdigs126, expdigs127,
            expdigs128, expdigs129, expdigs130, expdigs131, expdigs132, expdigs133, expdigs134,
            expdigs135, expdigs136, expdigs137, expdigs138, expdigs139, expdigs140, expdigs141,
            expdigs142, expdigs143, expdigs144, expdigs145, expdigs146, expdigs147 };
    static final int nexpdigstable[] = { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,
            14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 13, 13, 13, 12,
            12, 11, 11, 10, 10, 10, 9, 9, 8, 8, 8, 7, 7, 6, 6, 5, 5, 5, 4, 4, 3, 3, 3, 2, 2, 1, 1,
            1, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,
            14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,
            14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,
            14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 };
    static final int binexpstable[] = { 90, 89, 89, 88, 88, 88, 87, 87, 86, 86, 86, 85, 85, 84, 84,
            83, 83, 83, 82, 82, 81, 81, 81, 80, 80, 79, 79, 78, 78, 78, 77, 77, 76, 76, 76, 75, 75,
            74, 74, 73, 73, 73, 72, 72, 71, 71, 71, 70, 70, 69, 69, 68, 68, 68, 67, 67, 66, 66, 66,
            65, 65, 64, 64, 64, 63, 63, 62, 62, 61, 61, 61, 60, 60, 59, 59, 59, 58, 58, 57, 57, 56,
            56, 56, 55, 55, 54, 54, 54, 53, 53, 52, 52, 51, 51, 51, 50, 50, 49, 49, 49, 48, 48, 47,
            47, 46, 46, 46, 45, 45, 44, 44, 44, 43, 43, 42, 42, 41, 41, 41, 40, 40, 39, 39, 39, 38,
            38, 37, 37, 37, 36, 36, 35, 35, 34, 34, 34, 33, 33, 32, 32, 32, 31, 31, 30, 30, 29, 29,
            29 };

    NumberCommonAccessor() {
        digs = new int[27];
    }

    void init(OracleStatement stmt, int max_len, short form, int external_type, boolean forBind)
            throws SQLException {
        init(stmt, 6, 6, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    void init(OracleStatement stmt, int internal_type, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form)
            throws SQLException {
        init(stmt, 6, 6, form, false);
        initForDescribe(internal_type, max_len, nullable, flags, precision, scale, contflag,
                        total_elems, form, null);
        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            externalType = external_type;
        }
        internalTypeMaxLength = 21;
        if (max_len > 0 && max_len < internalTypeMaxLength) {
            internalTypeMaxLength = max_len;
        }
        byteLength = internalTypeMaxLength + 1;
    }

    int getInt(int currentRow) throws SQLException {
        int result = 0;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            byte bytes[] = rowSpaceByte;
            int offset = columnIndex + byteLength * currentRow + 1;
            int len = bytes[offset - 1];
            byte expbyte = bytes[offset];
            int intNum = 0;
            if ((expbyte & 0xffffff80) != 0) {
                byte exponent = (byte) ((expbyte & 0xffffff7f) - 65);
                byte mantlen = (byte) (len - 1);
                int convDigs = mantlen <= exponent + 1 ? mantlen + 1 : exponent + 2;
                int convDigsOffset = convDigs + offset;
                if (exponent >= 4) {
                    if (exponent > 4) {
                        throwOverflow();
                    }
                    long longNum = 0L;
                    if (convDigs > 1) {
                        longNum = bytes[offset + 1] - 1;
                        for (int i = 2 + offset; i < convDigsOffset; i++) {
                            longNum = longNum * 100L + (long) (bytes[i] - 1);
                        }

                    }
                    for (int i = exponent - mantlen; i >= 0; i--) {
                        longNum *= 100L;
                    }

                    if (longNum > 0x7fffffffL) {
                        throwOverflow();
                    }
                    intNum = (int) longNum;
                } else {
                    if (convDigs > 1) {
                        intNum = bytes[offset + 1] - 1;
                        for (int i = 2 + offset; i < convDigsOffset; i++) {
                            intNum = intNum * 100 + (bytes[i] - 1);
                        }

                    }
                    for (int i = exponent - mantlen; i >= 0; i--) {
                        intNum *= 100;
                    }

                }
            } else {
                byte exponent = (byte) ((~expbyte & 0xffffff7f) - 65);
                byte mantlen = (byte) (len - 1);
                if (mantlen != 20 || bytes[offset + mantlen] == 102) {
                    mantlen--;
                }
                int convDigs = mantlen <= exponent + 1 ? mantlen + 1 : exponent + 2;
                int convDigsOffset = convDigs + offset;
                if (exponent >= 4) {
                    if (exponent > 4) {
                        throwOverflow();
                    }
                    long longNum = 0L;
                    if (convDigs > 1) {
                        longNum = 101 - bytes[offset + 1];
                        for (int i = 2 + offset; i < convDigsOffset; i++) {
                            longNum = longNum * 100L + (long) (101 - bytes[i]);
                        }

                    }
                    for (int i = exponent - mantlen; i >= 0; i--) {
                        longNum *= 100L;
                    }

                    longNum = -longNum;
                    if (longNum < 0xffffffff80000000L) {
                        throwOverflow();
                    }
                    intNum = (int) longNum;
                } else {
                    if (convDigs > 1) {
                        intNum = 101 - bytes[offset + 1];
                        for (int i = 2 + offset; i < convDigsOffset; i++) {
                            intNum = intNum * 100 + (101 - bytes[i]);
                        }

                    }
                    for (int i = exponent - mantlen; i >= 0; i--) {
                        intNum *= 100;
                    }

                    intNum = -intNum;
                }
            }
            result = intNum;
        }
        return result;
    }

    boolean getBoolean(int currentRow) throws SQLException {
        boolean result = false;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            byte bytes[] = rowSpaceByte;
            int offset = columnIndex + byteLength * currentRow + 1;
            int len = bytes[offset - 1];
            result = len != 1 || bytes[offset] != -128;
        }
        return result;
    }

    short getShort(int currentRow) throws SQLException {
        short result = 0;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            byte bytes[] = rowSpaceByte;
            int offset = columnIndex + byteLength * currentRow + 1;
            int len = bytes[offset - 1];
            byte expbyte = bytes[offset];
            int intNum = 0;
            if ((expbyte & 0xffffff80) != 0) {
                byte exponent = (byte) ((expbyte & 0xffffff7f) - 65);
                if (exponent > 2) {
                    throwOverflow();
                }
                byte mantlen = (byte) (len - 1);
                int convDigs = mantlen <= exponent + 1 ? mantlen + 1 : exponent + 2;
                int convDigsOffset = convDigs + offset;
                if (convDigs > 1) {
                    intNum = bytes[offset + 1] - 1;
                    for (int i = 2 + offset; i < convDigsOffset; i++) {
                        intNum = intNum * 100 + (bytes[i] - 1);
                    }

                }
                for (int i = exponent - mantlen; i >= 0; i--) {
                    intNum *= 100;
                }

                if (exponent == 2 && intNum > 32767) {
                    throwOverflow();
                }
            } else {
                byte exponent = (byte) ((~expbyte & 0xffffff7f) - 65);
                if (exponent > 2) {
                    throwOverflow();
                }
                byte mantlen = (byte) (len - 1);
                if (mantlen != 20 || bytes[offset + mantlen] == 102) {
                    mantlen--;
                }
                int convDigs = mantlen <= exponent + 1 ? mantlen + 1 : exponent + 2;
                int convDigsOffset = convDigs + offset;
                if (convDigs > 1) {
                    intNum = 101 - bytes[offset + 1];
                    for (int i = 2 + offset; i < convDigsOffset; i++) {
                        intNum = intNum * 100 + (101 - bytes[i]);
                    }

                }
                for (int i = exponent - mantlen; i >= 0; i--) {
                    intNum *= 100;
                }

                intNum = -intNum;
                if (exponent == 2 && intNum < -32768) {
                    throwOverflow();
                }
            }
            result = (short) intNum;
        }
        return result;
    }

    byte getByte(int currentRow) throws SQLException {
        byte result = 0;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            byte bytes[] = rowSpaceByte;
            int offset = columnIndex + byteLength * currentRow + 1;
            int len = bytes[offset - 1];
            byte expbyte = bytes[offset];
            int intNum = 0;
            if ((expbyte & 0xffffff80) != 0) {
                byte exponent = (byte) ((expbyte & 0xffffff7f) - 65);
                if (exponent > 1) {
                    throwOverflow();
                }
                byte mantlen = (byte) (len - 1);
                if (mantlen > exponent + 1) {
                    switch (exponent) {
                    case 0: // '\0'
                        intNum = bytes[offset + 1] - 1;
                        break;

                    case 1: // '\001'
                        intNum = (bytes[offset + 1] - 1) * 100 + (bytes[offset + 2] - 1);
                        if (intNum > 127) {
                            throwOverflow();
                        }
                        break;
                    }
                } else if (mantlen == 1) {
                    if (exponent == 1) {
                        intNum = (bytes[offset + 1] - 1) * 100;
                        if (intNum > 127) {
                            throwOverflow();
                        }
                    } else {
                        intNum = bytes[offset + 1] - 1;
                    }
                } else if (mantlen == 2) {
                    intNum = (bytes[offset + 1] - 1) * 100 + (bytes[offset + 2] - 1);
                    if (intNum > 127) {
                        throwOverflow();
                    }
                }
            } else {
                byte exponent = (byte) ((~expbyte & 0xffffff7f) - 65);
                if (exponent > 1) {
                    throwOverflow();
                }
                byte mantlen = (byte) (len - 1);
                if (mantlen != 20 || bytes[offset + mantlen] == 102) {
                    mantlen--;
                }
                if (mantlen > exponent + 1) {
                    switch (exponent) {
                    case 0: // '\0'
                        intNum = -(101 - bytes[offset + 1]);
                        break;

                    case 1: // '\001'
                        intNum = -((101 - bytes[offset + 1]) * 100 + (101 - bytes[offset + 2]));
                        if (intNum < -128) {
                            throwOverflow();
                        }
                        break;
                    }
                } else if (mantlen == 1) {
                    if (exponent == 1) {
                        intNum = -(101 - bytes[offset + 1]) * 100;
                        if (intNum < -128) {
                            throwOverflow();
                        }
                    } else {
                        intNum = -(101 - bytes[offset + 1]);
                    }
                } else if (mantlen == 2) {
                    intNum = -((101 - bytes[offset + 1]) * 100 + (101 - bytes[offset + 2]));
                    if (intNum < -128) {
                        throwOverflow();
                    }
                }
            }
            result = (byte) intNum;
        }
        return result;
    }

    long getLong(int currentRow) throws SQLException {
        long result = 0L;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            byte bytes[] = rowSpaceByte;
            int offset = columnIndex + byteLength * currentRow + 1;
            int len = bytes[offset - 1];
            byte expbyte = bytes[offset];
            long longNum = 0L;
            if ((expbyte & 0xffffff80) != 0) {
                if (expbyte == -128 && len == 1) {
                    return 0L;
                }
                byte exponent = (byte) ((expbyte & 0xffffff7f) - 65);
                if (exponent > 9) {
                    throwOverflow();
                }
                if (exponent == 9) {
                    int ci = 1;
                    int cl = len;
                    if (len > 11) {
                        cl = 11;
                    }
                    for (; ci < cl; ci++) {
                        int ubytes = bytes[offset + ci] & 0xff;
                        int umax = MAX_LONG[ci];
                        if (ubytes == umax) {
                            continue;
                        }
                        if (ubytes < umax) {
                            break;
                        }
                        throwOverflow();
                    }

                    if (ci == cl && len > 11) {
                        throwOverflow();
                    }
                }
                byte mantlen = (byte) (len - 1);
                int convDigs = mantlen <= exponent + 1 ? mantlen + 1 : exponent + 2;
                int convDigsOffset = convDigs + offset;
                if (convDigs > 1) {
                    longNum = bytes[offset + 1] - 1;
                    for (int i = 2 + offset; i < convDigsOffset; i++) {
                        longNum = longNum * 100L + (long) (bytes[i] - 1);
                    }

                }
                for (int i = exponent - mantlen; i >= 0; i--) {
                    longNum *= 100L;
                }

            } else {
                byte exponent = (byte) ((~expbyte & 0xffffff7f) - 65);
                if (exponent > 9) {
                    throwOverflow();
                }
                if (exponent == 9) {
                    int ci = 1;
                    int cl = len;
                    if (len > 12) {
                        cl = 12;
                    }
                    for (; ci < cl; ci++) {
                        int ubytes = bytes[offset + ci] & 0xff;
                        int umin = MIN_LONG[ci];
                        if (ubytes == umin) {
                            continue;
                        }
                        if (ubytes > umin) {
                            break;
                        }
                        throwOverflow();
                    }

                    if (ci == cl && len < 12) {
                        throwOverflow();
                    }
                }
                byte mantlen = (byte) (len - 1);
                if (mantlen != 20 || bytes[offset + mantlen] == 102) {
                    mantlen--;
                }
                int convDigs = mantlen <= exponent + 1 ? mantlen + 1 : exponent + 2;
                int convDigsOffset = convDigs + offset;
                if (convDigs > 1) {
                    longNum = 101 - bytes[offset + 1];
                    for (int i = 2 + offset; i < convDigsOffset; i++) {
                        longNum = longNum * 100L + (long) (101 - bytes[i]);
                    }

                }
                for (int i = exponent - mantlen; i >= 0; i--) {
                    longNum *= 100L;
                }

                longNum = -longNum;
            }
            result = longNum;
        }
        return result;
    }

    float getFloat(int currentRow) throws SQLException {
        float result = 0.0F;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            byte bytes[] = rowSpaceByte;
            int offset = columnIndex + byteLength * currentRow + 1;
            int len = bytes[offset - 1];
            byte expbyte = bytes[offset];
            double number = 0.0D;
            int digidx = offset + 1;
            if ((expbyte & 0xffffff80) != 0) {
                if (expbyte == -128 && len == 1) {
                    return 0.0F;
                }
                if (len == 2 && expbyte == -1 && bytes[offset + 1] == 101) {
                    return (1.0F / 0.0F);
                }
                byte exponent = (byte) ((expbyte & 0xffffff7f) - 65);
                int ndigits;
                for (ndigits = len - 1; bytes[digidx] == 1 && ndigits > 0;) {
                    digidx++;
                    ndigits--;
                    exponent--;
                }

                int factidx = (int) (127D - (double) exponent);
                switch (ndigits) {
                case 1: // '\001'
                    number = (double) (bytes[digidx] - 1) * factorTable[factidx];
                    break;

                case 2: // '\002'
                    number = (double) ((bytes[digidx] - 1) * 100 + (bytes[digidx + 1] - 1))
                            * factorTable[factidx + 1];
                    break;

                case 3: // '\003'
                    number = (double) ((bytes[digidx] - 1) * 10000 + (bytes[digidx + 1] - 1) * 100 + (bytes[digidx + 2] - 1))
                            * factorTable[factidx + 2];
                    break;

                case 4: // '\004'
                    number = (double) ((bytes[digidx] - 1) * 0xf4240 + (bytes[digidx + 1] - 1)
                            * 10000 + (bytes[digidx + 2] - 1) * 100 + (bytes[digidx + 3] - 1))
                            * factorTable[factidx + 3];
                    break;

                case 5: // '\005'
                    number = (double) ((bytes[digidx + 1] - 1) * 0xf4240 + (bytes[digidx + 2] - 1)
                            * 10000 + (bytes[digidx + 3] - 1) * 100 + (bytes[digidx + 4] - 1))
                            * factorTable[factidx + 4]
                            + (double) (bytes[digidx] - 1)
                            * factorTable[factidx];
                    break;

                case 6: // '\006'
                    number = (double) ((bytes[digidx + 2] - 1) * 0xf4240 + (bytes[digidx + 3] - 1)
                            * 10000 + (bytes[digidx + 4] - 1) * 100 + (bytes[digidx + 5] - 1))
                            * factorTable[factidx + 5]
                            + (double) ((bytes[digidx] - 1) * 100 + (bytes[digidx + 1] - 1))
                            * factorTable[factidx + 1];
                    break;

                default:
                    number = (double) ((bytes[digidx + 3] - 1) * 0xf4240 + (bytes[digidx + 4] - 1)
                            * 10000 + (bytes[digidx + 5] - 1) * 100 + (bytes[digidx + 6] - 1))
                            * factorTable[factidx + 6]
                            + (double) ((bytes[digidx] - 1) * 10000 + (bytes[digidx + 1] - 1) * 100 + (bytes[digidx + 2] - 1))
                            * factorTable[factidx + 2];
                    break;
                }
            } else {
                if (expbyte == 0 && len == 1) {
                    return (-1.0F / 0.0F);
                }
                byte exponent = (byte) ((~expbyte & 0xffffff7f) - 65);
                int ndigits = len - 1;
                if (ndigits != 20 || bytes[offset + ndigits] == 102) {
                    ndigits--;
                }
                while (bytes[digidx] == 1 && ndigits > 0) {
                    digidx++;
                    ndigits--;
                    exponent--;
                }
                int factidx = (int) (127D - (double) exponent);
                switch (ndigits) {
                case 1: // '\001'
                    number = (double) (-(101 - bytes[digidx])) * factorTable[factidx];
                    break;

                case 2: // '\002'
                    number = (double) (-((101 - bytes[digidx]) * 100 + (101 - bytes[digidx + 1])))
                            * factorTable[factidx + 1];
                    break;

                case 3: // '\003'
                    number = (double) (-((101 - bytes[digidx]) * 10000 + (101 - bytes[digidx + 1])
                            * 100 + (101 - bytes[digidx + 2])))
                            * factorTable[factidx + 2];
                    break;

                case 4: // '\004'
                    number = (double) (-((101 - bytes[digidx]) * 0xf4240
                            + (101 - bytes[digidx + 1]) * 10000 + (101 - bytes[digidx + 2]) * 100 + (101 - bytes[digidx + 3])))
                            * factorTable[factidx + 3];
                    break;

                case 5: // '\005'
                    number = -((double) ((101 - bytes[digidx + 1]) * 0xf4240
                            + (101 - bytes[digidx + 2]) * 10000 + (101 - bytes[digidx + 3]) * 100 + (101 - bytes[digidx + 4]))
                            * factorTable[factidx + 4] + (double) (101 - bytes[digidx])
                            * factorTable[factidx]);
                    break;

                case 6: // '\006'
                    number = -((double) ((101 - bytes[digidx + 2]) * 0xf4240
                            + (101 - bytes[digidx + 3]) * 10000 + (101 - bytes[digidx + 4]) * 100 + (101 - bytes[digidx + 5]))
                            * factorTable[factidx + 5] + (double) ((101 - bytes[digidx]) * 100 + (101 - bytes[digidx + 1]))
                            * factorTable[factidx + 1]);
                    break;

                default:
                    number = -((double) ((101 - bytes[digidx + 3]) * 0xf4240
                            + (101 - bytes[digidx + 4]) * 10000 + (101 - bytes[digidx + 5]) * 100 + (101 - bytes[digidx + 6]))
                            * factorTable[factidx + 6] + (double) ((101 - bytes[digidx]) * 10000
                            + (101 - bytes[digidx + 1]) * 100 + (101 - bytes[digidx + 2]))
                            * factorTable[factidx + 2]);
                    break;
                }
            }
            result = (float) number;
        }
        return result;
    }

    double getDouble(int currentRow) throws SQLException {
        double result = 0.0D;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            byte bytes[] = rowSpaceByte;
            int offset = columnIndex + byteLength * currentRow + 1;
            int len = bytes[offset - 1];
            byte expbyte = bytes[offset];
            int digidx = offset + 1;
            int ndigits = len - 1;
            boolean positive = true;
            byte exponent;
            int odig0;
            boolean mult10;
            if ((expbyte & 0xffffff80) != 0) {
                if (expbyte == -128 && len == 1) {
                    return 0.0D;
                }
                if (len == 2 && expbyte == -1 && bytes[offset + 1] == 101) {
                    return (1.0D / 0.0D);
                }
                exponent = (byte) ((expbyte & 0xffffff7f) - 65);
                mult10 = (bytes[(digidx + ndigits) - 1] - 1) % 10 == 0;
                odig0 = bytes[digidx] - 1;
            } else {
                positive = false;
                if (expbyte == 0 && len == 1) {
                    return (-1.0D / 0.0D);
                }
                exponent = (byte) ((~expbyte & 0xffffff7f) - 65);
                if (ndigits != 20 || bytes[offset + ndigits] == 102) {
                    ndigits--;
                }
                mult10 = (101 - bytes[(digidx + ndigits) - 1]) % 10 == 0;
                odig0 = 101 - bytes[digidx];
            }
            int n10digits = ndigits << 1;
            if (mult10) {
                n10digits--;
            }
            int exp = (exponent + 1 << 1) - n10digits;
            if (odig0 < 10) {
                n10digits--;
            }
            double number;
            if (n10digits <= 15 && (exp >= 0 && exp <= 37 - n10digits || exp < 0 && exp >= -22)) {
                int odig1 = 0;
                int odig2 = 0;
                int odig3 = 0;
                int odig4 = 0;
                int odig5 = 0;
                int odig6 = 0;
                int odig7 = 0;
                if (positive) {
                    switch (ndigits) {
                    default:
                        odig7 = bytes[digidx + 7] - 1;
                        // fall through

                    case 7: // '\007'
                        odig6 = bytes[digidx + 6] - 1;
                        // fall through

                    case 6: // '\006'
                        odig5 = bytes[digidx + 5] - 1;
                        // fall through

                    case 5: // '\005'
                        odig4 = bytes[digidx + 4] - 1;
                        // fall through

                    case 4: // '\004'
                        odig3 = bytes[digidx + 3] - 1;
                        // fall through

                    case 3: // '\003'
                        odig2 = bytes[digidx + 2] - 1;
                        // fall through

                    case 2: // '\002'
                        odig1 = bytes[digidx + 1] - 1;
                        break;

                    case 1: // '\001'
                        break;
                    }
                } else {
                    switch (ndigits) {
                    default:
                        odig7 = 101 - bytes[digidx + 7];
                        // fall through

                    case 7: // '\007'
                        odig6 = 101 - bytes[digidx + 6];
                        // fall through

                    case 6: // '\006'
                        odig5 = 101 - bytes[digidx + 5];
                        // fall through

                    case 5: // '\005'
                        odig4 = 101 - bytes[digidx + 4];
                        // fall through

                    case 4: // '\004'
                        odig3 = 101 - bytes[digidx + 3];
                        // fall through

                    case 3: // '\003'
                        odig2 = 101 - bytes[digidx + 2];
                        // fall through

                    case 2: // '\002'
                        odig1 = 101 - bytes[digidx + 1];
                        break;

                    case 1: // '\001'
                        break;
                    }
                }
                double dValue;
                if (mult10) {
                    switch (ndigits) {
                    default: {
                        dValue = odig0 / 10;
                        break;
                    }

                    case 2: // '\002'
                    {
                        dValue = odig0 * 10 + odig1 / 10;
                        break;
                    }

                    case 3: // '\003'
                    {
                        dValue = odig0 * 1000 + odig1 * 10 + odig2 / 10;
                        break;
                    }

                    case 4: // '\004'
                    {
                        dValue = odig0 * 0x186a0 + odig1 * 1000 + odig2 * 10 + odig3 / 10;
                        break;
                    }

                    case 5: // '\005'
                    {
                        dValue = odig0 * 0x989680 + odig1 * 0x186a0 + odig2 * 1000 + odig3 * 10
                                + odig4 / 10;
                        break;
                    }

                    case 6: // '\006'
                    {
                        int iValue = odig1 * 0x989680 + odig2 * 0x186a0 + odig3 * 1000 + odig4 * 10
                                + odig5 / 10;
                        dValue = (long) odig0 * 0x3b9aca00L + (long) iValue;
                        break;
                    }

                    case 7: // '\007'
                    {
                        int iValue = odig2 * 0x989680 + odig3 * 0x186a0 + odig4 * 1000 + odig5 * 10
                                + odig6 / 10;
                        int iValue1 = odig0 * 100 + odig1;
                        dValue = (long) iValue1 * 0x3b9aca00L + (long) iValue;
                        break;
                    }

                    case 8: // '\b'
                    {
                        int iValue = odig3 * 0x989680 + odig4 * 0x186a0 + odig5 * 1000 + odig6 * 10
                                + odig7 / 10;
                        int iValue1 = odig0 * 10000 + odig1 * 100 + odig2;
                        dValue = (long) iValue1 * 0x3b9aca00L + (long) iValue;
                        break;
                    }
                    }
                } else {
                    switch (ndigits) {
                    default: {
                        dValue = odig0;
                        break;
                    }

                    case 2: // '\002'
                    {
                        dValue = odig0 * 100 + odig1;
                        break;
                    }

                    case 3: // '\003'
                    {
                        dValue = odig0 * 10000 + odig1 * 100 + odig2;
                        break;
                    }

                    case 4: // '\004'
                    {
                        dValue = odig0 * 0xf4240 + odig1 * 10000 + odig2 * 100 + odig3;
                        break;
                    }

                    case 5: // '\005'
                    {
                        int iValue = odig1 * 0xf4240 + odig2 * 10000 + odig3 * 100 + odig4;
                        dValue = (long) odig0 * 0x5f5e100L + (long) iValue;
                        break;
                    }

                    case 6: // '\006'
                    {
                        int iValue = odig2 * 0xf4240 + odig3 * 10000 + odig4 * 100 + odig5;
                        int iValue1 = odig0 * 100 + odig1;
                        dValue = (long) iValue1 * 0x5f5e100L + (long) iValue;
                        break;
                    }

                    case 7: // '\007'
                    {
                        int iValue = odig3 * 0xf4240 + odig4 * 10000 + odig5 * 100 + odig6;
                        int iValue1 = odig0 * 10000 + odig1 * 100 + odig2;
                        dValue = (long) iValue1 * 0x5f5e100L + (long) iValue;
                        break;
                    }

                    case 8: // '\b'
                    {
                        int iValue = odig4 * 0xf4240 + odig5 * 10000 + odig6 * 100 + odig7;
                        int iValue1 = odig0 * 0xf4240 + odig1 * 10000 + odig2 * 100 + odig3;
                        dValue = (long) iValue1 * 0x5f5e100L + (long) iValue;
                        break;
                    }
                    }
                }
                if (exp == 0 || dValue == 0.0D) {
                    number = dValue;
                } else if (exp >= 0) {
                    if (exp <= 22) {
                        number = dValue * small10pow[exp];
                    } else {
                        int slop = 15 - n10digits;
                        dValue *= small10pow[slop];
                        number = dValue * small10pow[exp - slop];
                    }
                } else {
                    number = dValue / small10pow[-exp];
                }
            } else {
                int mantdig0 = 0;
                int mantdig1 = 0;
                int mantdig2 = 0;
                int mantdig3 = 0;
                int mantdig4 = 0;
                int mantdig5 = 0;
                int mantdig6 = 0;
                int mantdig7 = 0;
                int mantdig8 = 0;
                int nmantdigs = 0;
                int newdig0 = 0;
                int newdig1 = 0;
                int newdig2 = 0;
                int newdig3 = 0;
                int newdig4 = 0;
                boolean sticky = false;
                int needexpdigs = 0;
                if (positive) {
                    int i;
                    if ((ndigits & 1) != 0) {
                        i = 2;
                        mantdig0 = odig0;
                    } else {
                        i = 3;
                        mantdig0 = odig0 * 100 + (bytes[digidx + 1] - 1);
                    }
                    for (; i < ndigits; i += 2) {
                        int val = (bytes[(digidx + i) - 1] - 1) * 100 + (bytes[digidx + i] - 1)
                                + mantdig0 * 10000;
                        switch (nmantdigs) {
                        default:
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig3 * 10000;
                            mantdig3 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig4 * 10000;
                            mantdig4 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig5 * 10000;
                            mantdig5 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig6 * 10000;
                            mantdig6 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig7 * 10000;
                            mantdig7 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig8 * 10000;
                            mantdig8 = val & 0xffff;
                            break;

                        case 7: // '\007'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig3 * 10000;
                            mantdig3 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig4 * 10000;
                            mantdig4 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig5 * 10000;
                            mantdig5 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig6 * 10000;
                            mantdig6 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig7 * 10000;
                            mantdig7 = val & 0xffff;
                            break;

                        case 6: // '\006'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig3 * 10000;
                            mantdig3 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig4 * 10000;
                            mantdig4 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig5 * 10000;
                            mantdig5 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig6 * 10000;
                            mantdig6 = val & 0xffff;
                            break;

                        case 5: // '\005'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig3 * 10000;
                            mantdig3 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig4 * 10000;
                            mantdig4 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig5 * 10000;
                            mantdig5 = val & 0xffff;
                            break;

                        case 4: // '\004'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig3 * 10000;
                            mantdig3 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig4 * 10000;
                            mantdig4 = val & 0xffff;
                            break;

                        case 3: // '\003'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig3 * 10000;
                            mantdig3 = val & 0xffff;
                            break;

                        case 2: // '\002'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            break;

                        case 1: // '\001'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            break;

                        case 0: // '\0'
                            mantdig0 = val & 0xffff;
                            break;
                        }
                        val = val >> 16 & 0xffff;
                        if (val != 0) {
                            switch (++nmantdigs) {
                            case 8: // '\b'
                                mantdig8 = val;
                                break;

                            case 7: // '\007'
                                mantdig7 = val;
                                break;

                            case 6: // '\006'
                                mantdig6 = val;
                                break;

                            case 5: // '\005'
                                mantdig5 = val;
                                break;

                            case 4: // '\004'
                                mantdig4 = val;
                                break;

                            case 3: // '\003'
                                mantdig3 = val;
                                break;

                            case 2: // '\002'
                                mantdig2 = val;
                                break;

                            case 1: // '\001'
                                mantdig1 = val;
                                break;
                            }
                        }
                    }

                } else {
                    int i;
                    if ((ndigits & 1) != 0) {
                        i = 2;
                        mantdig0 = odig0;
                    } else {
                        i = 3;
                        mantdig0 = odig0 * 100 + (101 - bytes[digidx + 1]);
                    }
                    for (; i < ndigits; i += 2) {
                        int val = (101 - bytes[(digidx + i) - 1]) * 100 + (101 - bytes[digidx + i])
                                + mantdig0 * 10000;
                        switch (nmantdigs) {
                        default:
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig3 * 10000;
                            mantdig3 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig4 * 10000;
                            mantdig4 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig5 * 10000;
                            mantdig5 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig6 * 10000;
                            mantdig6 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig7 * 10000;
                            mantdig7 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig8 * 10000;
                            mantdig8 = val & 0xffff;
                            break;

                        case 7: // '\007'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig3 * 10000;
                            mantdig3 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig4 * 10000;
                            mantdig4 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig5 * 10000;
                            mantdig5 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig6 * 10000;
                            mantdig6 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig7 * 10000;
                            mantdig7 = val & 0xffff;
                            break;

                        case 6: // '\006'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig3 * 10000;
                            mantdig3 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig4 * 10000;
                            mantdig4 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig5 * 10000;
                            mantdig5 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig6 * 10000;
                            mantdig6 = val & 0xffff;
                            break;

                        case 5: // '\005'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig3 * 10000;
                            mantdig3 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig4 * 10000;
                            mantdig4 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig5 * 10000;
                            mantdig5 = val & 0xffff;
                            break;

                        case 4: // '\004'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig3 * 10000;
                            mantdig3 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig4 * 10000;
                            mantdig4 = val & 0xffff;
                            break;

                        case 3: // '\003'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig3 * 10000;
                            mantdig3 = val & 0xffff;
                            break;

                        case 2: // '\002'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig2 * 10000;
                            mantdig2 = val & 0xffff;
                            break;

                        case 1: // '\001'
                            mantdig0 = val & 0xffff;
                            val = (val >> 16 & 0xffff) + mantdig1 * 10000;
                            mantdig1 = val & 0xffff;
                            break;

                        case 0: // '\0'
                            mantdig0 = val & 0xffff;
                            break;
                        }
                        val = val >> 16 & 0xffff;
                        if (val != 0) {
                            switch (++nmantdigs) {
                            case 8: // '\b'
                                mantdig8 = val;
                                break;

                            case 7: // '\007'
                                mantdig7 = val;
                                break;

                            case 6: // '\006'
                                mantdig6 = val;
                                break;

                            case 5: // '\005'
                                mantdig5 = val;
                                break;

                            case 4: // '\004'
                                mantdig4 = val;
                                break;

                            case 3: // '\003'
                                mantdig3 = val;
                                break;

                            case 2: // '\002'
                                mantdig2 = val;
                                break;

                            case 1: // '\001'
                                mantdig1 = val;
                                break;
                            }
                        }
                    }

                }
                int digshift = nmantdigs;
                nmantdigs++;
                int expdigsidx = (62 - exponent) + ndigits;
                int nexpdigs = nexpdigstable[expdigsidx];
                int expdigs[] = expdigstable[expdigsidx];
                needexpdigs = nmantdigs + 5;
                int expdigsoffset = 0;
                if (nexpdigs > needexpdigs) {
                    expdigsoffset = nexpdigs - needexpdigs;
                    nexpdigs = needexpdigs;
                }
                int newdigidx = 0;
                int carry = 0;
                int lim = ((nexpdigs - 1) + (nmantdigs - 1)) - 4;
                int k;
                for (k = 0; k < lim; k++) {
                    int lo = carry & 0xffff;
                    carry = carry >> 16 & 0xffff;
                    int lim2 = nmantdigs >= k + 1 ? k + 1 : nmantdigs;
                    for (int j = (k - nexpdigs) + 1 <= 0 ? 0 : (k - nexpdigs) + 1; j < lim2; j++) {
                        int didx = (expdigsoffset + k) - j;
                        int term;
                        switch (j) {
                        case 8: // '\b'
                            term = mantdig8 * expdigs[didx];
                            break;

                        case 7: // '\007'
                            term = mantdig7 * expdigs[didx];
                            break;

                        case 6: // '\006'
                            term = mantdig6 * expdigs[didx];
                            break;

                        case 5: // '\005'
                            term = mantdig5 * expdigs[didx];
                            break;

                        case 4: // '\004'
                            term = mantdig4 * expdigs[didx];
                            break;

                        case 3: // '\003'
                            term = mantdig3 * expdigs[didx];
                            break;

                        case 2: // '\002'
                            term = mantdig2 * expdigs[didx];
                            break;

                        case 1: // '\001'
                            term = mantdig1 * expdigs[didx];
                            break;

                        default:
                            term = mantdig0 * expdigs[didx];
                            break;
                        }
                        lo += term & 0xffff;
                        carry += term >> 16 & 0xffff;
                    }

                    sticky = sticky || (lo & 0xffff) != 0;
                    carry += lo >> 16 & 0xffff;
                }

                for (lim += 5; k < lim; k++) {
                    int lo = carry & 0xffff;
                    carry = carry >> 16 & 0xffff;
                    int lim2 = nmantdigs >= k + 1 ? k + 1 : nmantdigs;
                    for (int j = (k - nexpdigs) + 1 <= 0 ? 0 : (k - nexpdigs) + 1; j < lim2; j++) {
                        int didx = (expdigsoffset + k) - j;
                        int term;
                        switch (j) {
                        case 8: // '\b'
                            term = mantdig8 * expdigs[didx];
                            break;

                        case 7: // '\007'
                            term = mantdig7 * expdigs[didx];
                            break;

                        case 6: // '\006'
                            term = mantdig6 * expdigs[didx];
                            break;

                        case 5: // '\005'
                            term = mantdig5 * expdigs[didx];
                            break;

                        case 4: // '\004'
                            term = mantdig4 * expdigs[didx];
                            break;

                        case 3: // '\003'
                            term = mantdig3 * expdigs[didx];
                            break;

                        case 2: // '\002'
                            term = mantdig2 * expdigs[didx];
                            break;

                        case 1: // '\001'
                            term = mantdig1 * expdigs[didx];
                            break;

                        default:
                            term = mantdig0 * expdigs[didx];
                            break;
                        }
                        lo += term & 0xffff;
                        carry += term >> 16 & 0xffff;
                    }

                    switch (newdigidx++) {
                    case 4: // '\004'
                        newdig4 = lo & 0xffff;
                        break;

                    case 3: // '\003'
                        newdig3 = lo & 0xffff;
                        break;

                    case 2: // '\002'
                        newdig2 = lo & 0xffff;
                        break;

                    case 1: // '\001'
                        newdig1 = lo & 0xffff;
                        break;

                    default:
                        newdig0 = lo & 0xffff;
                        break;
                    }
                    carry += lo >> 16 & 0xffff;
                }

                while (carry != 0) {
                    if (newdigidx < 5) {
                        switch (newdigidx++) {
                        case 4: // '\004'
                            newdig4 = carry & 0xffff;
                            break;

                        case 3: // '\003'
                            newdig3 = carry & 0xffff;
                            break;

                        case 2: // '\002'
                            newdig2 = carry & 0xffff;
                            break;

                        case 1: // '\001'
                            newdig1 = carry & 0xffff;
                            break;

                        default:
                            newdig0 = carry & 0xffff;
                            break;
                        }
                    } else {
                        sticky = sticky || newdig0 != 0;
                        newdig0 = newdig1;
                        newdig1 = newdig2;
                        newdig2 = newdig3;
                        newdig3 = newdig4;
                        newdig4 = carry & 0xffff;
                    }
                    carry = carry >> 16 & 0xffff;
                    digshift++;
                }
                int binexp = (binexpstable[expdigsidx] + digshift) * 16 - 1;
                int hinewdig;
                switch (newdigidx) {
                case 5: // '\005'
                    hinewdig = newdig4;
                    break;

                case 4: // '\004'
                    hinewdig = newdig3;
                    break;

                case 3: // '\003'
                    hinewdig = newdig2;
                    break;

                case 2: // '\002'
                    hinewdig = newdig1;
                    break;

                default:
                    hinewdig = newdig0;
                    break;
                }
                for (int dig = hinewdig >> 1; dig != 0; dig >>= 1) {
                    binexp++;
                }

                int s = 5;
                int mhi = hinewdig << 5;
                int mlo = 0;
                carry = 0;
                while ((mhi & 0x100000) == 0) {
                    mhi <<= 1;
                    s++;
                }
                switch (newdigidx) {
                case 5: // '\005'
                    if (s > 16) {
                        mhi |= newdig3 << s - 16 | newdig2 >> 32 - s;
                        mlo = newdig2 << s | newdig1 << s - 16 | newdig0 >> 32 - s;
                        carry = newdig0 & 1 << 31 - s;
                        sticky = sticky || newdig0 << s + 1 != 0;
                    } else if (s == 16) {
                        mhi |= newdig3;
                        mlo = newdig2 << 16 | newdig1;
                        carry = newdig0 & 0x8000;
                        sticky = sticky || (newdig0 & 0x7fff) != 0;
                    } else {
                        mhi |= newdig3 >> 16 - s;
                        mlo = newdig3 << 16 + s | newdig2 << s | newdig1 >> 16 - s;
                        carry = newdig1 & 1 << 15 - s;
                        if (s < 15) {
                            sticky = sticky || newdig1 << s + 17 != 0;
                        }
                        sticky = sticky || newdig0 != 0;
                    }
                    break;

                case 4: // '\004'
                    if (s > 16) {
                        mhi |= newdig2 << s - 16 | newdig1 >> 32 - s;
                        mlo = newdig1 << s | newdig0 << s - 16;
                    } else if (s == 16) {
                        mhi |= newdig2;
                        mlo = newdig1 << 16 | newdig0;
                    } else {
                        mhi |= newdig2 >> 16 - s;
                        mlo = newdig2 << 16 + s | newdig1 << s | newdig0 >> 16 - s;
                        carry = newdig0 & 1 << 15 - s;
                        if (s < 15) {
                            sticky = sticky || newdig0 << s + 17 != 0;
                        }
                    }
                    break;

                case 3: // '\003'
                    if (s > 16) {
                        mhi |= newdig1 << s - 16 | newdig0 >> 32 - s;
                        mlo = newdig0 << s;
                    } else if (s == 16) {
                        mhi |= newdig1;
                        mlo = newdig0 << 16;
                    } else {
                        mhi |= newdig1 >> 16 - s;
                        mlo = newdig1 << 16 + s;
                        mlo |= newdig0 << s;
                    }
                    break;

                case 2: // '\002'
                    if (s > 16) {
                        mhi |= newdig0 << s - 16;
                    } else if (s == 16) {
                        mhi |= newdig0;
                    } else {
                        mhi |= newdig0 >> 16 - s;
                        mlo = newdig0 << 16 + s;
                    }
                    break;
                }
                if (carry != 0 && (sticky || (mlo & 1) != 0)) {
                    if (mlo == -1) {
                        mlo = 0;
                        if ((++mhi & 0x200000) != 0) {
                            mlo = mlo >> 1 | mhi << 31;
                            mhi >>= 1;
                            binexp++;
                        }
                    } else {
                        mlo++;
                    }
                }
                long bits = (long) binexp << 52 | (long) (mhi & 0xfffff) << 32 | (long) mlo
                        & 0xffffffffL;
                number = Double.longBitsToDouble(bits);
            }
            result = positive ? number : -number;
        }
        return result;
    }

    double getDoubleImprecise(int currentRow) throws SQLException {
        double result = 0.0D;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            byte bytes[] = rowSpaceByte;
            int offset = columnIndex + byteLength * currentRow + 1;
            int len = bytes[offset - 1];
            byte expbyte = bytes[offset];
            double number = 0.0D;
            int digidx = offset + 1;
            if ((expbyte & 0xffffff80) != 0) {
                if (expbyte == -128 && len == 1) {
                    return 0.0D;
                }
                if (len == 2 && expbyte == -1 && bytes[offset + 1] == 101) {
                    return (1.0D / 0.0D);
                }
                byte exponent = (byte) ((expbyte & 0xffffff7f) - 65);
                int ndigits = len - 1;
                int factidx = (int) (127D - (double) exponent);
                int i = ndigits % 4;
                switch (i) {
                case 1: // '\001'
                    number = (double) (bytes[digidx] - 1) * factorTable[factidx];
                    break;

                case 2: // '\002'
                    number = (double) ((bytes[digidx] - 1) * 100 + (bytes[digidx + 1] - 1))
                            * factorTable[factidx + 1];
                    break;

                case 3: // '\003'
                    number = (double) ((bytes[digidx] - 1) * 10000 + (bytes[digidx + 1] - 1) * 100 + (bytes[digidx + 2] - 1))
                            * factorTable[factidx + 2];
                    break;
                }
                for (; i < ndigits; i += 4) {
                    number += (double) ((bytes[digidx + i] - 1) * 0xf4240
                            + (bytes[digidx + i + 1] - 1) * 10000 + (bytes[digidx + i + 2] - 1)
                            * 100 + (bytes[digidx + i + 3] - 1))
                            * factorTable[factidx + i + 3];
                }

            } else {
                if (expbyte == 0 && len == 1) {
                    return (-1.0D / 0.0D);
                }
                byte exponent = (byte) ((~expbyte & 0xffffff7f) - 65);
                int ndigits = len - 1;
                if (ndigits != 20 || bytes[offset + ndigits] == 102) {
                    ndigits--;
                }
                int factidx = (int) (127D - (double) exponent);
                int i = ndigits % 4;
                switch (i) {
                case 1: // '\001'
                    number = (double) (101 - bytes[digidx]) * factorTable[factidx];
                    break;

                case 2: // '\002'
                    number = (double) ((101 - bytes[digidx]) * 100 + (101 - bytes[digidx + 1]))
                            * factorTable[factidx + 1];
                    break;

                case 3: // '\003'
                    number = (double) ((101 - bytes[digidx]) * 10000 + (101 - bytes[digidx + 1])
                            * 100 + (101 - bytes[digidx + 2]))
                            * factorTable[factidx + 2];
                    break;
                }
                for (; i < ndigits; i += 4) {
                    number += (double) ((101 - bytes[digidx + i]) * 0xf4240
                            + (101 - bytes[digidx + i + 1]) * 10000 + (101 - bytes[digidx + i + 2])
                            * 100 + (101 - bytes[digidx + i + 3]))
                            * factorTable[factidx + i + 3];
                }

                number = -number;
            }
            result = number;
        }
        return result;
    }

    BigDecimal getBigDecimal(int currentRow) throws SQLException {
        BigDecimal result = null;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            byte bytes[] = rowSpaceByte;
            int offset = columnIndex + byteLength * currentRow + 1;
            int len = bytes[offset - 1];
            int digit0 = 0;
            int digit1 = 0;
            int digit2 = 0;
            int digit3 = 0;
            int digit4 = 0;
            int digit5 = 0;
            int digit6 = 0;
            int digit7 = 0;
            int digit8 = 0;
            int digit9 = 0;
            int digit10 = 0;
            int digit11 = 0;
            int digit12 = 0;
            int digit13 = 0;
            int digit14 = 0;
            int digit15 = 0;
            int digit16 = 0;
            int digit17 = 0;
            int digit18 = 0;
            int digit19 = 0;
            int digit20 = 0;
            int digit21 = 0;
            int digit22 = 0;
            int digit23 = 0;
            int digit24 = 0;
            int digit25 = 0;
            int digit26 = 0;
            byte oidx = 1;
            byte cnt = 26;
            int value = 0;
            byte expbyte = bytes[offset];
            boolean trailingZeroP = false;
            int clen;
            int mantlen;
            int signval;
            int scale;
            if ((expbyte & 0xffffff80) != 0) {
                if (expbyte == -128 && len == 1) {
                    return BIGDEC_ZERO;
                }
                if (len == 2 && expbyte == -1 && bytes[offset + 1] == 101) {
                    throwOverflow();
                }
                signval = 1;
                int exponent = (byte) ((expbyte & 0xffffff7f) - 65);
                mantlen = len - 1;
                clen = mantlen - 1;
                scale = (exponent - mantlen) + 1 << 1;
                if (scale > 0) {
                    scale = 0;
                    clen = exponent;
                } else if (scale < 0) {
                    trailingZeroP = (bytes[offset + mantlen] - 1) % 10 == 0;
                }
                digit26 = bytes[offset + oidx++] - 1;
                for (; (clen & 1) != 0; clen--) {
                    if (oidx > mantlen) {
                        digit26 *= 100;
                    } else {
                        digit26 = digit26 * 100 + (bytes[offset + oidx++] - 1);
                    }
                }

            } else {
                if (expbyte == 0 && len == 1) {
                    throwOverflow();
                }
                signval = -1;
                int exponent = (byte) ((~expbyte & 0xffffff7f) - 65);
                mantlen = len - 1;
                if (mantlen != 20 || bytes[offset + mantlen] == 102) {
                    mantlen--;
                }
                clen = mantlen - 1;
                scale = (exponent - mantlen) + 1 << 1;
                if (scale > 0) {
                    scale = 0;
                    clen = exponent;
                } else if (scale < 0) {
                    trailingZeroP = (101 - bytes[offset + mantlen]) % 10 == 0;
                }
                digit26 = 101 - bytes[offset + oidx++];
                for (; (clen & 1) != 0; clen--) {
                    if (oidx > mantlen) {
                        digit26 *= 100;
                    } else {
                        digit26 = digit26 * 100 + (101 - bytes[offset + oidx++]);
                    }
                }

            }
            if (trailingZeroP) {
                scale++;
                digit26 /= 10;
            }
            int lim = mantlen - 1;
            for (; clen != 0; clen -= 2) {
                if (signval == 1) {
                    if (trailingZeroP) {
                        value = ((bytes[(offset + oidx) - 1] - 1) % 10) * 1000
                                + (bytes[offset + oidx] - 1) * 10 + (bytes[offset + oidx + 1] - 1)
                                / 10 + digit26 * 10000;
                        oidx += 2;
                    } else if (oidx < lim) {
                        value = (bytes[offset + oidx] - 1) * 100 + (bytes[offset + oidx + 1] - 1)
                                + digit26 * 10000;
                        oidx += 2;
                    } else {
                        value = 0;
                        if (oidx <= mantlen) {
                            int i;
                            for (i = 0; oidx <= mantlen; i++) {
                                value = value * 100 + (bytes[offset + oidx++] - 1);
                            }

                            for (; i < 2; i++) {
                                value *= 100;
                            }

                        }
                        value += digit26 * 10000;
                    }
                } else if (trailingZeroP) {
                    value = ((101 - bytes[(offset + oidx) - 1]) % 10) * 1000
                            + (101 - bytes[offset + oidx]) * 10 + (101 - bytes[offset + oidx + 1])
                            / 10 + digit26 * 10000;
                    oidx += 2;
                } else if (oidx < lim) {
                    value = (101 - bytes[offset + oidx]) * 100 + (101 - bytes[offset + oidx + 1])
                            + digit26 * 10000;
                    oidx += 2;
                } else {
                    value = 0;
                    if (oidx <= mantlen) {
                        int i;
                        for (i = 0; oidx <= mantlen; i++) {
                            value = value * 100 + (101 - bytes[offset + oidx++]);
                        }

                        for (; i < 2; i++) {
                            value *= 100;
                        }

                    }
                    value += digit26 * 10000;
                }
                switch (cnt) {
                default:
                    break;

                case 26: // '\032'
                    digit26 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit25 = value;
                    }
                    break;

                case 25: // '\031'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit24 = value;
                    }
                    break;

                case 24: // '\030'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit23 = value;
                    }
                    break;

                case 23: // '\027'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit22 = value;
                    }
                    break;

                case 22: // '\026'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit21 = value;
                    }
                    break;

                case 21: // '\025'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit20 = value;
                    }
                    break;

                case 20: // '\024'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit19 = value;
                    }
                    break;

                case 19: // '\023'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit18 = value;
                    }
                    break;

                case 18: // '\022'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit17 = value;
                    }
                    break;

                case 17: // '\021'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit16 = value;
                    }
                    break;

                case 16: // '\020'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit15 = value;
                    }
                    break;

                case 15: // '\017'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit14 = value;
                    }
                    break;

                case 14: // '\016'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit13 = value;
                    }
                    break;

                case 13: // '\r'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit12 = value;
                    }
                    break;

                case 12: // '\f'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit11 = value;
                    }
                    break;

                case 11: // '\013'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value = (value >> 16) + digit11 * 10000;
                    digit11 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit10 = value;
                    }
                    break;

                case 10: // '\n'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value = (value >> 16) + digit11 * 10000;
                    digit11 = value & 0xffff;
                    value = (value >> 16) + digit10 * 10000;
                    digit10 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit9 = value;
                    }
                    break;

                case 9: // '\t'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value = (value >> 16) + digit11 * 10000;
                    digit11 = value & 0xffff;
                    value = (value >> 16) + digit10 * 10000;
                    digit10 = value & 0xffff;
                    value = (value >> 16) + digit9 * 10000;
                    digit9 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit8 = value;
                    }
                    break;

                case 8: // '\b'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value = (value >> 16) + digit11 * 10000;
                    digit11 = value & 0xffff;
                    value = (value >> 16) + digit10 * 10000;
                    digit10 = value & 0xffff;
                    value = (value >> 16) + digit9 * 10000;
                    digit9 = value & 0xffff;
                    value = (value >> 16) + digit8 * 10000;
                    digit8 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit7 = value;
                    }
                    break;

                case 7: // '\007'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value = (value >> 16) + digit11 * 10000;
                    digit11 = value & 0xffff;
                    value = (value >> 16) + digit10 * 10000;
                    digit10 = value & 0xffff;
                    value = (value >> 16) + digit9 * 10000;
                    digit9 = value & 0xffff;
                    value = (value >> 16) + digit8 * 10000;
                    digit8 = value & 0xffff;
                    value = (value >> 16) + digit7 * 10000;
                    digit7 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit6 = value;
                    }
                    break;

                case 6: // '\006'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value = (value >> 16) + digit11 * 10000;
                    digit11 = value & 0xffff;
                    value = (value >> 16) + digit10 * 10000;
                    digit10 = value & 0xffff;
                    value = (value >> 16) + digit9 * 10000;
                    digit9 = value & 0xffff;
                    value = (value >> 16) + digit8 * 10000;
                    digit8 = value & 0xffff;
                    value = (value >> 16) + digit7 * 10000;
                    digit7 = value & 0xffff;
                    value = (value >> 16) + digit6 * 10000;
                    digit6 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit5 = value;
                    }
                    break;

                case 5: // '\005'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value = (value >> 16) + digit11 * 10000;
                    digit11 = value & 0xffff;
                    value = (value >> 16) + digit10 * 10000;
                    digit10 = value & 0xffff;
                    value = (value >> 16) + digit9 * 10000;
                    digit9 = value & 0xffff;
                    value = (value >> 16) + digit8 * 10000;
                    digit8 = value & 0xffff;
                    value = (value >> 16) + digit7 * 10000;
                    digit7 = value & 0xffff;
                    value = (value >> 16) + digit6 * 10000;
                    digit6 = value & 0xffff;
                    value = (value >> 16) + digit5 * 10000;
                    digit5 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit4 = value;
                    }
                    break;

                case 4: // '\004'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value = (value >> 16) + digit11 * 10000;
                    digit11 = value & 0xffff;
                    value = (value >> 16) + digit10 * 10000;
                    digit10 = value & 0xffff;
                    value = (value >> 16) + digit9 * 10000;
                    digit9 = value & 0xffff;
                    value = (value >> 16) + digit8 * 10000;
                    digit8 = value & 0xffff;
                    value = (value >> 16) + digit7 * 10000;
                    digit7 = value & 0xffff;
                    value = (value >> 16) + digit6 * 10000;
                    digit6 = value & 0xffff;
                    value = (value >> 16) + digit5 * 10000;
                    digit5 = value & 0xffff;
                    value = (value >> 16) + digit4 * 10000;
                    digit4 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit3 = value;
                    }
                    break;

                case 3: // '\003'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value = (value >> 16) + digit11 * 10000;
                    digit11 = value & 0xffff;
                    value = (value >> 16) + digit10 * 10000;
                    digit10 = value & 0xffff;
                    value = (value >> 16) + digit9 * 10000;
                    digit9 = value & 0xffff;
                    value = (value >> 16) + digit8 * 10000;
                    digit8 = value & 0xffff;
                    value = (value >> 16) + digit7 * 10000;
                    digit7 = value & 0xffff;
                    value = (value >> 16) + digit6 * 10000;
                    digit6 = value & 0xffff;
                    value = (value >> 16) + digit5 * 10000;
                    digit5 = value & 0xffff;
                    value = (value >> 16) + digit4 * 10000;
                    digit4 = value & 0xffff;
                    value = (value >> 16) + digit3 * 10000;
                    digit3 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit2 = value;
                    }
                    break;

                case 2: // '\002'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value = (value >> 16) + digit11 * 10000;
                    digit11 = value & 0xffff;
                    value = (value >> 16) + digit10 * 10000;
                    digit10 = value & 0xffff;
                    value = (value >> 16) + digit9 * 10000;
                    digit9 = value & 0xffff;
                    value = (value >> 16) + digit8 * 10000;
                    digit8 = value & 0xffff;
                    value = (value >> 16) + digit7 * 10000;
                    digit7 = value & 0xffff;
                    value = (value >> 16) + digit6 * 10000;
                    digit6 = value & 0xffff;
                    value = (value >> 16) + digit5 * 10000;
                    digit5 = value & 0xffff;
                    value = (value >> 16) + digit4 * 10000;
                    digit4 = value & 0xffff;
                    value = (value >> 16) + digit3 * 10000;
                    digit3 = value & 0xffff;
                    value = (value >> 16) + digit2 * 10000;
                    digit2 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit1 = value;
                    }
                    break;

                case 1: // '\001'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value = (value >> 16) + digit11 * 10000;
                    digit11 = value & 0xffff;
                    value = (value >> 16) + digit10 * 10000;
                    digit10 = value & 0xffff;
                    value = (value >> 16) + digit9 * 10000;
                    digit9 = value & 0xffff;
                    value = (value >> 16) + digit8 * 10000;
                    digit8 = value & 0xffff;
                    value = (value >> 16) + digit7 * 10000;
                    digit7 = value & 0xffff;
                    value = (value >> 16) + digit6 * 10000;
                    digit6 = value & 0xffff;
                    value = (value >> 16) + digit5 * 10000;
                    digit5 = value & 0xffff;
                    value = (value >> 16) + digit4 * 10000;
                    digit4 = value & 0xffff;
                    value = (value >> 16) + digit3 * 10000;
                    digit3 = value & 0xffff;
                    value = (value >> 16) + digit2 * 10000;
                    digit2 = value & 0xffff;
                    value = (value >> 16) + digit1 * 10000;
                    digit1 = value & 0xffff;
                    value >>= 16;
                    if (value != 0) {
                        cnt--;
                        digit0 = value;
                    }
                    break;

                case 0: // '\0'
                    digit26 = value & 0xffff;
                    value = (value >> 16) + digit25 * 10000;
                    digit25 = value & 0xffff;
                    value = (value >> 16) + digit24 * 10000;
                    digit24 = value & 0xffff;
                    value = (value >> 16) + digit23 * 10000;
                    digit23 = value & 0xffff;
                    value = (value >> 16) + digit22 * 10000;
                    digit22 = value & 0xffff;
                    value = (value >> 16) + digit21 * 10000;
                    digit21 = value & 0xffff;
                    value = (value >> 16) + digit20 * 10000;
                    digit20 = value & 0xffff;
                    value = (value >> 16) + digit19 * 10000;
                    digit19 = value & 0xffff;
                    value = (value >> 16) + digit18 * 10000;
                    digit18 = value & 0xffff;
                    value = (value >> 16) + digit17 * 10000;
                    digit17 = value & 0xffff;
                    value = (value >> 16) + digit16 * 10000;
                    digit16 = value & 0xffff;
                    value = (value >> 16) + digit15 * 10000;
                    digit15 = value & 0xffff;
                    value = (value >> 16) + digit14 * 10000;
                    digit14 = value & 0xffff;
                    value = (value >> 16) + digit13 * 10000;
                    digit13 = value & 0xffff;
                    value = (value >> 16) + digit12 * 10000;
                    digit12 = value & 0xffff;
                    value = (value >> 16) + digit11 * 10000;
                    digit11 = value & 0xffff;
                    value = (value >> 16) + digit10 * 10000;
                    digit10 = value & 0xffff;
                    value = (value >> 16) + digit9 * 10000;
                    digit9 = value & 0xffff;
                    value = (value >> 16) + digit8 * 10000;
                    digit8 = value & 0xffff;
                    value = (value >> 16) + digit7 * 10000;
                    digit7 = value & 0xffff;
                    value = (value >> 16) + digit6 * 10000;
                    digit6 = value & 0xffff;
                    value = (value >> 16) + digit5 * 10000;
                    digit5 = value & 0xffff;
                    value = (value >> 16) + digit4 * 10000;
                    digit4 = value & 0xffff;
                    value = (value >> 16) + digit3 * 10000;
                    digit3 = value & 0xffff;
                    value = (value >> 16) + digit2 * 10000;
                    digit2 = value & 0xffff;
                    value = (value >> 16) + digit1 * 10000;
                    digit1 = value & 0xffff;
                    value = (value >> 16) + digit0 * 10000;
                    digit0 = value & 0xffff;
                    break;
                }
            }

            byte barray[];
            switch (cnt) {
            default: {
                byte x = (byte) (digit0 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 53;
                    barray = new byte[numbytes];
                    barray[51] = (byte) (digit26 >> 8 & 0xff);
                    barray[52] = (byte) (digit26 & 0xff);
                    barray[49] = (byte) (digit25 >> 8 & 0xff);
                    barray[50] = (byte) (digit25 & 0xff);
                    barray[47] = (byte) (digit24 >> 8 & 0xff);
                    barray[48] = (byte) (digit24 & 0xff);
                    barray[45] = (byte) (digit23 >> 8 & 0xff);
                    barray[46] = (byte) (digit23 & 0xff);
                    barray[43] = (byte) (digit22 >> 8 & 0xff);
                    barray[44] = (byte) (digit22 & 0xff);
                    barray[41] = (byte) (digit21 >> 8 & 0xff);
                    barray[42] = (byte) (digit21 & 0xff);
                    barray[39] = (byte) (digit20 >> 8 & 0xff);
                    barray[40] = (byte) (digit20 & 0xff);
                    barray[37] = (byte) (digit19 >> 8 & 0xff);
                    barray[38] = (byte) (digit19 & 0xff);
                    barray[35] = (byte) (digit18 >> 8 & 0xff);
                    barray[36] = (byte) (digit18 & 0xff);
                    barray[33] = (byte) (digit17 >> 8 & 0xff);
                    barray[34] = (byte) (digit17 & 0xff);
                    barray[31] = (byte) (digit16 >> 8 & 0xff);
                    barray[32] = (byte) (digit16 & 0xff);
                    barray[29] = (byte) (digit15 >> 8 & 0xff);
                    barray[30] = (byte) (digit15 & 0xff);
                    barray[27] = (byte) (digit14 >> 8 & 0xff);
                    barray[28] = (byte) (digit14 & 0xff);
                    barray[25] = (byte) (digit13 >> 8 & 0xff);
                    barray[26] = (byte) (digit13 & 0xff);
                    barray[23] = (byte) (digit12 >> 8 & 0xff);
                    barray[24] = (byte) (digit12 & 0xff);
                    barray[21] = (byte) (digit11 >> 8 & 0xff);
                    barray[22] = (byte) (digit11 & 0xff);
                    barray[19] = (byte) (digit10 >> 8 & 0xff);
                    barray[20] = (byte) (digit10 & 0xff);
                    barray[17] = (byte) (digit9 >> 8 & 0xff);
                    barray[18] = (byte) (digit9 & 0xff);
                    barray[15] = (byte) (digit8 >> 8 & 0xff);
                    barray[16] = (byte) (digit8 & 0xff);
                    barray[13] = (byte) (digit7 >> 8 & 0xff);
                    barray[14] = (byte) (digit7 & 0xff);
                    barray[11] = (byte) (digit6 >> 8 & 0xff);
                    barray[12] = (byte) (digit6 & 0xff);
                    barray[9] = (byte) (digit5 >> 8 & 0xff);
                    barray[10] = (byte) (digit5 & 0xff);
                    barray[7] = (byte) (digit4 >> 8 & 0xff);
                    barray[8] = (byte) (digit4 & 0xff);
                    barray[5] = (byte) (digit3 >> 8 & 0xff);
                    barray[6] = (byte) (digit3 & 0xff);
                    barray[3] = (byte) (digit2 >> 8 & 0xff);
                    barray[4] = (byte) (digit2 & 0xff);
                    barray[1] = (byte) (digit1 >> 8 & 0xff);
                    barray[2] = (byte) (digit1 & 0xff);
                    barray[0] = (byte) (digit0 & 0xff);
                } else {
                    int numbytes = 54;
                    barray = new byte[numbytes];
                    barray[52] = (byte) (digit26 >> 8 & 0xff);
                    barray[53] = (byte) (digit26 & 0xff);
                    barray[50] = (byte) (digit25 >> 8 & 0xff);
                    barray[51] = (byte) (digit25 & 0xff);
                    barray[48] = (byte) (digit24 >> 8 & 0xff);
                    barray[49] = (byte) (digit24 & 0xff);
                    barray[46] = (byte) (digit23 >> 8 & 0xff);
                    barray[47] = (byte) (digit23 & 0xff);
                    barray[44] = (byte) (digit22 >> 8 & 0xff);
                    barray[45] = (byte) (digit22 & 0xff);
                    barray[42] = (byte) (digit21 >> 8 & 0xff);
                    barray[43] = (byte) (digit21 & 0xff);
                    barray[40] = (byte) (digit20 >> 8 & 0xff);
                    barray[41] = (byte) (digit20 & 0xff);
                    barray[38] = (byte) (digit19 >> 8 & 0xff);
                    barray[39] = (byte) (digit19 & 0xff);
                    barray[36] = (byte) (digit18 >> 8 & 0xff);
                    barray[37] = (byte) (digit18 & 0xff);
                    barray[34] = (byte) (digit17 >> 8 & 0xff);
                    barray[35] = (byte) (digit17 & 0xff);
                    barray[32] = (byte) (digit16 >> 8 & 0xff);
                    barray[33] = (byte) (digit16 & 0xff);
                    barray[30] = (byte) (digit15 >> 8 & 0xff);
                    barray[31] = (byte) (digit15 & 0xff);
                    barray[28] = (byte) (digit14 >> 8 & 0xff);
                    barray[29] = (byte) (digit14 & 0xff);
                    barray[26] = (byte) (digit13 >> 8 & 0xff);
                    barray[27] = (byte) (digit13 & 0xff);
                    barray[24] = (byte) (digit12 >> 8 & 0xff);
                    barray[25] = (byte) (digit12 & 0xff);
                    barray[22] = (byte) (digit11 >> 8 & 0xff);
                    barray[23] = (byte) (digit11 & 0xff);
                    barray[20] = (byte) (digit10 >> 8 & 0xff);
                    barray[21] = (byte) (digit10 & 0xff);
                    barray[18] = (byte) (digit9 >> 8 & 0xff);
                    barray[19] = (byte) (digit9 & 0xff);
                    barray[16] = (byte) (digit8 >> 8 & 0xff);
                    barray[17] = (byte) (digit8 & 0xff);
                    barray[14] = (byte) (digit7 >> 8 & 0xff);
                    barray[15] = (byte) (digit7 & 0xff);
                    barray[12] = (byte) (digit6 >> 8 & 0xff);
                    barray[13] = (byte) (digit6 & 0xff);
                    barray[10] = (byte) (digit5 >> 8 & 0xff);
                    barray[11] = (byte) (digit5 & 0xff);
                    barray[8] = (byte) (digit4 >> 8 & 0xff);
                    barray[9] = (byte) (digit4 & 0xff);
                    barray[6] = (byte) (digit3 >> 8 & 0xff);
                    barray[7] = (byte) (digit3 & 0xff);
                    barray[4] = (byte) (digit2 >> 8 & 0xff);
                    barray[5] = (byte) (digit2 & 0xff);
                    barray[2] = (byte) (digit1 >> 8 & 0xff);
                    barray[3] = (byte) (digit1 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit0 & 0xff);
                }
                break;
            }

            case 1: // '\001'
            {
                byte x = (byte) (digit1 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 51;
                    barray = new byte[numbytes];
                    barray[49] = (byte) (digit26 >> 8 & 0xff);
                    barray[50] = (byte) (digit26 & 0xff);
                    barray[47] = (byte) (digit25 >> 8 & 0xff);
                    barray[48] = (byte) (digit25 & 0xff);
                    barray[45] = (byte) (digit24 >> 8 & 0xff);
                    barray[46] = (byte) (digit24 & 0xff);
                    barray[43] = (byte) (digit23 >> 8 & 0xff);
                    barray[44] = (byte) (digit23 & 0xff);
                    barray[41] = (byte) (digit22 >> 8 & 0xff);
                    barray[42] = (byte) (digit22 & 0xff);
                    barray[39] = (byte) (digit21 >> 8 & 0xff);
                    barray[40] = (byte) (digit21 & 0xff);
                    barray[37] = (byte) (digit20 >> 8 & 0xff);
                    barray[38] = (byte) (digit20 & 0xff);
                    barray[35] = (byte) (digit19 >> 8 & 0xff);
                    barray[36] = (byte) (digit19 & 0xff);
                    barray[33] = (byte) (digit18 >> 8 & 0xff);
                    barray[34] = (byte) (digit18 & 0xff);
                    barray[31] = (byte) (digit17 >> 8 & 0xff);
                    barray[32] = (byte) (digit17 & 0xff);
                    barray[29] = (byte) (digit16 >> 8 & 0xff);
                    barray[30] = (byte) (digit16 & 0xff);
                    barray[27] = (byte) (digit15 >> 8 & 0xff);
                    barray[28] = (byte) (digit15 & 0xff);
                    barray[25] = (byte) (digit14 >> 8 & 0xff);
                    barray[26] = (byte) (digit14 & 0xff);
                    barray[23] = (byte) (digit13 >> 8 & 0xff);
                    barray[24] = (byte) (digit13 & 0xff);
                    barray[21] = (byte) (digit12 >> 8 & 0xff);
                    barray[22] = (byte) (digit12 & 0xff);
                    barray[19] = (byte) (digit11 >> 8 & 0xff);
                    barray[20] = (byte) (digit11 & 0xff);
                    barray[17] = (byte) (digit10 >> 8 & 0xff);
                    barray[18] = (byte) (digit10 & 0xff);
                    barray[15] = (byte) (digit9 >> 8 & 0xff);
                    barray[16] = (byte) (digit9 & 0xff);
                    barray[13] = (byte) (digit8 >> 8 & 0xff);
                    barray[14] = (byte) (digit8 & 0xff);
                    barray[11] = (byte) (digit7 >> 8 & 0xff);
                    barray[12] = (byte) (digit7 & 0xff);
                    barray[9] = (byte) (digit6 >> 8 & 0xff);
                    barray[10] = (byte) (digit6 & 0xff);
                    barray[7] = (byte) (digit5 >> 8 & 0xff);
                    barray[8] = (byte) (digit5 & 0xff);
                    barray[5] = (byte) (digit4 >> 8 & 0xff);
                    barray[6] = (byte) (digit4 & 0xff);
                    barray[3] = (byte) (digit3 >> 8 & 0xff);
                    barray[4] = (byte) (digit3 & 0xff);
                    barray[1] = (byte) (digit2 >> 8 & 0xff);
                    barray[2] = (byte) (digit2 & 0xff);
                    barray[0] = (byte) (digit1 & 0xff);
                } else {
                    int numbytes = 52;
                    barray = new byte[numbytes];
                    barray[50] = (byte) (digit26 >> 8 & 0xff);
                    barray[51] = (byte) (digit26 & 0xff);
                    barray[48] = (byte) (digit25 >> 8 & 0xff);
                    barray[49] = (byte) (digit25 & 0xff);
                    barray[46] = (byte) (digit24 >> 8 & 0xff);
                    barray[47] = (byte) (digit24 & 0xff);
                    barray[44] = (byte) (digit23 >> 8 & 0xff);
                    barray[45] = (byte) (digit23 & 0xff);
                    barray[42] = (byte) (digit22 >> 8 & 0xff);
                    barray[43] = (byte) (digit22 & 0xff);
                    barray[40] = (byte) (digit21 >> 8 & 0xff);
                    barray[41] = (byte) (digit21 & 0xff);
                    barray[38] = (byte) (digit20 >> 8 & 0xff);
                    barray[39] = (byte) (digit20 & 0xff);
                    barray[36] = (byte) (digit19 >> 8 & 0xff);
                    barray[37] = (byte) (digit19 & 0xff);
                    barray[34] = (byte) (digit18 >> 8 & 0xff);
                    barray[35] = (byte) (digit18 & 0xff);
                    barray[32] = (byte) (digit17 >> 8 & 0xff);
                    barray[33] = (byte) (digit17 & 0xff);
                    barray[30] = (byte) (digit16 >> 8 & 0xff);
                    barray[31] = (byte) (digit16 & 0xff);
                    barray[28] = (byte) (digit15 >> 8 & 0xff);
                    barray[29] = (byte) (digit15 & 0xff);
                    barray[26] = (byte) (digit14 >> 8 & 0xff);
                    barray[27] = (byte) (digit14 & 0xff);
                    barray[24] = (byte) (digit13 >> 8 & 0xff);
                    barray[25] = (byte) (digit13 & 0xff);
                    barray[22] = (byte) (digit12 >> 8 & 0xff);
                    barray[23] = (byte) (digit12 & 0xff);
                    barray[20] = (byte) (digit11 >> 8 & 0xff);
                    barray[21] = (byte) (digit11 & 0xff);
                    barray[18] = (byte) (digit10 >> 8 & 0xff);
                    barray[19] = (byte) (digit10 & 0xff);
                    barray[16] = (byte) (digit9 >> 8 & 0xff);
                    barray[17] = (byte) (digit9 & 0xff);
                    barray[14] = (byte) (digit8 >> 8 & 0xff);
                    barray[15] = (byte) (digit8 & 0xff);
                    barray[12] = (byte) (digit7 >> 8 & 0xff);
                    barray[13] = (byte) (digit7 & 0xff);
                    barray[10] = (byte) (digit6 >> 8 & 0xff);
                    barray[11] = (byte) (digit6 & 0xff);
                    barray[8] = (byte) (digit5 >> 8 & 0xff);
                    barray[9] = (byte) (digit5 & 0xff);
                    barray[6] = (byte) (digit4 >> 8 & 0xff);
                    barray[7] = (byte) (digit4 & 0xff);
                    barray[4] = (byte) (digit3 >> 8 & 0xff);
                    barray[5] = (byte) (digit3 & 0xff);
                    barray[2] = (byte) (digit2 >> 8 & 0xff);
                    barray[3] = (byte) (digit2 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit1 & 0xff);
                }
                break;
            }

            case 2: // '\002'
            {
                byte x = (byte) (digit2 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 49;
                    barray = new byte[numbytes];
                    barray[47] = (byte) (digit26 >> 8 & 0xff);
                    barray[48] = (byte) (digit26 & 0xff);
                    barray[45] = (byte) (digit25 >> 8 & 0xff);
                    barray[46] = (byte) (digit25 & 0xff);
                    barray[43] = (byte) (digit24 >> 8 & 0xff);
                    barray[44] = (byte) (digit24 & 0xff);
                    barray[41] = (byte) (digit23 >> 8 & 0xff);
                    barray[42] = (byte) (digit23 & 0xff);
                    barray[39] = (byte) (digit22 >> 8 & 0xff);
                    barray[40] = (byte) (digit22 & 0xff);
                    barray[37] = (byte) (digit21 >> 8 & 0xff);
                    barray[38] = (byte) (digit21 & 0xff);
                    barray[35] = (byte) (digit20 >> 8 & 0xff);
                    barray[36] = (byte) (digit20 & 0xff);
                    barray[33] = (byte) (digit19 >> 8 & 0xff);
                    barray[34] = (byte) (digit19 & 0xff);
                    barray[31] = (byte) (digit18 >> 8 & 0xff);
                    barray[32] = (byte) (digit18 & 0xff);
                    barray[29] = (byte) (digit17 >> 8 & 0xff);
                    barray[30] = (byte) (digit17 & 0xff);
                    barray[27] = (byte) (digit16 >> 8 & 0xff);
                    barray[28] = (byte) (digit16 & 0xff);
                    barray[25] = (byte) (digit15 >> 8 & 0xff);
                    barray[26] = (byte) (digit15 & 0xff);
                    barray[23] = (byte) (digit14 >> 8 & 0xff);
                    barray[24] = (byte) (digit14 & 0xff);
                    barray[21] = (byte) (digit13 >> 8 & 0xff);
                    barray[22] = (byte) (digit13 & 0xff);
                    barray[19] = (byte) (digit12 >> 8 & 0xff);
                    barray[20] = (byte) (digit12 & 0xff);
                    barray[17] = (byte) (digit11 >> 8 & 0xff);
                    barray[18] = (byte) (digit11 & 0xff);
                    barray[15] = (byte) (digit10 >> 8 & 0xff);
                    barray[16] = (byte) (digit10 & 0xff);
                    barray[13] = (byte) (digit9 >> 8 & 0xff);
                    barray[14] = (byte) (digit9 & 0xff);
                    barray[11] = (byte) (digit8 >> 8 & 0xff);
                    barray[12] = (byte) (digit8 & 0xff);
                    barray[9] = (byte) (digit7 >> 8 & 0xff);
                    barray[10] = (byte) (digit7 & 0xff);
                    barray[7] = (byte) (digit6 >> 8 & 0xff);
                    barray[8] = (byte) (digit6 & 0xff);
                    barray[5] = (byte) (digit5 >> 8 & 0xff);
                    barray[6] = (byte) (digit5 & 0xff);
                    barray[3] = (byte) (digit4 >> 8 & 0xff);
                    barray[4] = (byte) (digit4 & 0xff);
                    barray[1] = (byte) (digit3 >> 8 & 0xff);
                    barray[2] = (byte) (digit3 & 0xff);
                    barray[0] = (byte) (digit2 & 0xff);
                } else {
                    int numbytes = 50;
                    barray = new byte[numbytes];
                    barray[48] = (byte) (digit26 >> 8 & 0xff);
                    barray[49] = (byte) (digit26 & 0xff);
                    barray[46] = (byte) (digit25 >> 8 & 0xff);
                    barray[47] = (byte) (digit25 & 0xff);
                    barray[44] = (byte) (digit24 >> 8 & 0xff);
                    barray[45] = (byte) (digit24 & 0xff);
                    barray[42] = (byte) (digit23 >> 8 & 0xff);
                    barray[43] = (byte) (digit23 & 0xff);
                    barray[40] = (byte) (digit22 >> 8 & 0xff);
                    barray[41] = (byte) (digit22 & 0xff);
                    barray[38] = (byte) (digit21 >> 8 & 0xff);
                    barray[39] = (byte) (digit21 & 0xff);
                    barray[36] = (byte) (digit20 >> 8 & 0xff);
                    barray[37] = (byte) (digit20 & 0xff);
                    barray[34] = (byte) (digit19 >> 8 & 0xff);
                    barray[35] = (byte) (digit19 & 0xff);
                    barray[32] = (byte) (digit18 >> 8 & 0xff);
                    barray[33] = (byte) (digit18 & 0xff);
                    barray[30] = (byte) (digit17 >> 8 & 0xff);
                    barray[31] = (byte) (digit17 & 0xff);
                    barray[28] = (byte) (digit16 >> 8 & 0xff);
                    barray[29] = (byte) (digit16 & 0xff);
                    barray[26] = (byte) (digit15 >> 8 & 0xff);
                    barray[27] = (byte) (digit15 & 0xff);
                    barray[24] = (byte) (digit14 >> 8 & 0xff);
                    barray[25] = (byte) (digit14 & 0xff);
                    barray[22] = (byte) (digit13 >> 8 & 0xff);
                    barray[23] = (byte) (digit13 & 0xff);
                    barray[20] = (byte) (digit12 >> 8 & 0xff);
                    barray[21] = (byte) (digit12 & 0xff);
                    barray[18] = (byte) (digit11 >> 8 & 0xff);
                    barray[19] = (byte) (digit11 & 0xff);
                    barray[16] = (byte) (digit10 >> 8 & 0xff);
                    barray[17] = (byte) (digit10 & 0xff);
                    barray[14] = (byte) (digit9 >> 8 & 0xff);
                    barray[15] = (byte) (digit9 & 0xff);
                    barray[12] = (byte) (digit8 >> 8 & 0xff);
                    barray[13] = (byte) (digit8 & 0xff);
                    barray[10] = (byte) (digit7 >> 8 & 0xff);
                    barray[11] = (byte) (digit7 & 0xff);
                    barray[8] = (byte) (digit6 >> 8 & 0xff);
                    barray[9] = (byte) (digit6 & 0xff);
                    barray[6] = (byte) (digit5 >> 8 & 0xff);
                    barray[7] = (byte) (digit5 & 0xff);
                    barray[4] = (byte) (digit4 >> 8 & 0xff);
                    barray[5] = (byte) (digit4 & 0xff);
                    barray[2] = (byte) (digit3 >> 8 & 0xff);
                    barray[3] = (byte) (digit3 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit2 & 0xff);
                }
                break;
            }

            case 3: // '\003'
            {
                byte x = (byte) (digit3 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 47;
                    barray = new byte[numbytes];
                    barray[45] = (byte) (digit26 >> 8 & 0xff);
                    barray[46] = (byte) (digit26 & 0xff);
                    barray[43] = (byte) (digit25 >> 8 & 0xff);
                    barray[44] = (byte) (digit25 & 0xff);
                    barray[41] = (byte) (digit24 >> 8 & 0xff);
                    barray[42] = (byte) (digit24 & 0xff);
                    barray[39] = (byte) (digit23 >> 8 & 0xff);
                    barray[40] = (byte) (digit23 & 0xff);
                    barray[37] = (byte) (digit22 >> 8 & 0xff);
                    barray[38] = (byte) (digit22 & 0xff);
                    barray[35] = (byte) (digit21 >> 8 & 0xff);
                    barray[36] = (byte) (digit21 & 0xff);
                    barray[33] = (byte) (digit20 >> 8 & 0xff);
                    barray[34] = (byte) (digit20 & 0xff);
                    barray[31] = (byte) (digit19 >> 8 & 0xff);
                    barray[32] = (byte) (digit19 & 0xff);
                    barray[29] = (byte) (digit18 >> 8 & 0xff);
                    barray[30] = (byte) (digit18 & 0xff);
                    barray[27] = (byte) (digit17 >> 8 & 0xff);
                    barray[28] = (byte) (digit17 & 0xff);
                    barray[25] = (byte) (digit16 >> 8 & 0xff);
                    barray[26] = (byte) (digit16 & 0xff);
                    barray[23] = (byte) (digit15 >> 8 & 0xff);
                    barray[24] = (byte) (digit15 & 0xff);
                    barray[21] = (byte) (digit14 >> 8 & 0xff);
                    barray[22] = (byte) (digit14 & 0xff);
                    barray[19] = (byte) (digit13 >> 8 & 0xff);
                    barray[20] = (byte) (digit13 & 0xff);
                    barray[17] = (byte) (digit12 >> 8 & 0xff);
                    barray[18] = (byte) (digit12 & 0xff);
                    barray[15] = (byte) (digit11 >> 8 & 0xff);
                    barray[16] = (byte) (digit11 & 0xff);
                    barray[13] = (byte) (digit10 >> 8 & 0xff);
                    barray[14] = (byte) (digit10 & 0xff);
                    barray[11] = (byte) (digit9 >> 8 & 0xff);
                    barray[12] = (byte) (digit9 & 0xff);
                    barray[9] = (byte) (digit8 >> 8 & 0xff);
                    barray[10] = (byte) (digit8 & 0xff);
                    barray[7] = (byte) (digit7 >> 8 & 0xff);
                    barray[8] = (byte) (digit7 & 0xff);
                    barray[5] = (byte) (digit6 >> 8 & 0xff);
                    barray[6] = (byte) (digit6 & 0xff);
                    barray[3] = (byte) (digit5 >> 8 & 0xff);
                    barray[4] = (byte) (digit5 & 0xff);
                    barray[1] = (byte) (digit4 >> 8 & 0xff);
                    barray[2] = (byte) (digit4 & 0xff);
                    barray[0] = (byte) (digit3 & 0xff);
                } else {
                    int numbytes = 48;
                    barray = new byte[numbytes];
                    barray[46] = (byte) (digit26 >> 8 & 0xff);
                    barray[47] = (byte) (digit26 & 0xff);
                    barray[44] = (byte) (digit25 >> 8 & 0xff);
                    barray[45] = (byte) (digit25 & 0xff);
                    barray[42] = (byte) (digit24 >> 8 & 0xff);
                    barray[43] = (byte) (digit24 & 0xff);
                    barray[40] = (byte) (digit23 >> 8 & 0xff);
                    barray[41] = (byte) (digit23 & 0xff);
                    barray[38] = (byte) (digit22 >> 8 & 0xff);
                    barray[39] = (byte) (digit22 & 0xff);
                    barray[36] = (byte) (digit21 >> 8 & 0xff);
                    barray[37] = (byte) (digit21 & 0xff);
                    barray[34] = (byte) (digit20 >> 8 & 0xff);
                    barray[35] = (byte) (digit20 & 0xff);
                    barray[32] = (byte) (digit19 >> 8 & 0xff);
                    barray[33] = (byte) (digit19 & 0xff);
                    barray[30] = (byte) (digit18 >> 8 & 0xff);
                    barray[31] = (byte) (digit18 & 0xff);
                    barray[28] = (byte) (digit17 >> 8 & 0xff);
                    barray[29] = (byte) (digit17 & 0xff);
                    barray[26] = (byte) (digit16 >> 8 & 0xff);
                    barray[27] = (byte) (digit16 & 0xff);
                    barray[24] = (byte) (digit15 >> 8 & 0xff);
                    barray[25] = (byte) (digit15 & 0xff);
                    barray[22] = (byte) (digit14 >> 8 & 0xff);
                    barray[23] = (byte) (digit14 & 0xff);
                    barray[20] = (byte) (digit13 >> 8 & 0xff);
                    barray[21] = (byte) (digit13 & 0xff);
                    barray[18] = (byte) (digit12 >> 8 & 0xff);
                    barray[19] = (byte) (digit12 & 0xff);
                    barray[16] = (byte) (digit11 >> 8 & 0xff);
                    barray[17] = (byte) (digit11 & 0xff);
                    barray[14] = (byte) (digit10 >> 8 & 0xff);
                    barray[15] = (byte) (digit10 & 0xff);
                    barray[12] = (byte) (digit9 >> 8 & 0xff);
                    barray[13] = (byte) (digit9 & 0xff);
                    barray[10] = (byte) (digit8 >> 8 & 0xff);
                    barray[11] = (byte) (digit8 & 0xff);
                    barray[8] = (byte) (digit7 >> 8 & 0xff);
                    barray[9] = (byte) (digit7 & 0xff);
                    barray[6] = (byte) (digit6 >> 8 & 0xff);
                    barray[7] = (byte) (digit6 & 0xff);
                    barray[4] = (byte) (digit5 >> 8 & 0xff);
                    barray[5] = (byte) (digit5 & 0xff);
                    barray[2] = (byte) (digit4 >> 8 & 0xff);
                    barray[3] = (byte) (digit4 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit3 & 0xff);
                }
                break;
            }

            case 4: // '\004'
            {
                byte x = (byte) (digit4 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 45;
                    barray = new byte[numbytes];
                    barray[43] = (byte) (digit26 >> 8 & 0xff);
                    barray[44] = (byte) (digit26 & 0xff);
                    barray[41] = (byte) (digit25 >> 8 & 0xff);
                    barray[42] = (byte) (digit25 & 0xff);
                    barray[39] = (byte) (digit24 >> 8 & 0xff);
                    barray[40] = (byte) (digit24 & 0xff);
                    barray[37] = (byte) (digit23 >> 8 & 0xff);
                    barray[38] = (byte) (digit23 & 0xff);
                    barray[35] = (byte) (digit22 >> 8 & 0xff);
                    barray[36] = (byte) (digit22 & 0xff);
                    barray[33] = (byte) (digit21 >> 8 & 0xff);
                    barray[34] = (byte) (digit21 & 0xff);
                    barray[31] = (byte) (digit20 >> 8 & 0xff);
                    barray[32] = (byte) (digit20 & 0xff);
                    barray[29] = (byte) (digit19 >> 8 & 0xff);
                    barray[30] = (byte) (digit19 & 0xff);
                    barray[27] = (byte) (digit18 >> 8 & 0xff);
                    barray[28] = (byte) (digit18 & 0xff);
                    barray[25] = (byte) (digit17 >> 8 & 0xff);
                    barray[26] = (byte) (digit17 & 0xff);
                    barray[23] = (byte) (digit16 >> 8 & 0xff);
                    barray[24] = (byte) (digit16 & 0xff);
                    barray[21] = (byte) (digit15 >> 8 & 0xff);
                    barray[22] = (byte) (digit15 & 0xff);
                    barray[19] = (byte) (digit14 >> 8 & 0xff);
                    barray[20] = (byte) (digit14 & 0xff);
                    barray[17] = (byte) (digit13 >> 8 & 0xff);
                    barray[18] = (byte) (digit13 & 0xff);
                    barray[15] = (byte) (digit12 >> 8 & 0xff);
                    barray[16] = (byte) (digit12 & 0xff);
                    barray[13] = (byte) (digit11 >> 8 & 0xff);
                    barray[14] = (byte) (digit11 & 0xff);
                    barray[11] = (byte) (digit10 >> 8 & 0xff);
                    barray[12] = (byte) (digit10 & 0xff);
                    barray[9] = (byte) (digit9 >> 8 & 0xff);
                    barray[10] = (byte) (digit9 & 0xff);
                    barray[7] = (byte) (digit8 >> 8 & 0xff);
                    barray[8] = (byte) (digit8 & 0xff);
                    barray[5] = (byte) (digit7 >> 8 & 0xff);
                    barray[6] = (byte) (digit7 & 0xff);
                    barray[3] = (byte) (digit6 >> 8 & 0xff);
                    barray[4] = (byte) (digit6 & 0xff);
                    barray[1] = (byte) (digit5 >> 8 & 0xff);
                    barray[2] = (byte) (digit5 & 0xff);
                    barray[0] = (byte) (digit4 & 0xff);
                } else {
                    int numbytes = 46;
                    barray = new byte[numbytes];
                    barray[44] = (byte) (digit26 >> 8 & 0xff);
                    barray[45] = (byte) (digit26 & 0xff);
                    barray[42] = (byte) (digit25 >> 8 & 0xff);
                    barray[43] = (byte) (digit25 & 0xff);
                    barray[40] = (byte) (digit24 >> 8 & 0xff);
                    barray[41] = (byte) (digit24 & 0xff);
                    barray[38] = (byte) (digit23 >> 8 & 0xff);
                    barray[39] = (byte) (digit23 & 0xff);
                    barray[36] = (byte) (digit22 >> 8 & 0xff);
                    barray[37] = (byte) (digit22 & 0xff);
                    barray[34] = (byte) (digit21 >> 8 & 0xff);
                    barray[35] = (byte) (digit21 & 0xff);
                    barray[32] = (byte) (digit20 >> 8 & 0xff);
                    barray[33] = (byte) (digit20 & 0xff);
                    barray[30] = (byte) (digit19 >> 8 & 0xff);
                    barray[31] = (byte) (digit19 & 0xff);
                    barray[28] = (byte) (digit18 >> 8 & 0xff);
                    barray[29] = (byte) (digit18 & 0xff);
                    barray[26] = (byte) (digit17 >> 8 & 0xff);
                    barray[27] = (byte) (digit17 & 0xff);
                    barray[24] = (byte) (digit16 >> 8 & 0xff);
                    barray[25] = (byte) (digit16 & 0xff);
                    barray[22] = (byte) (digit15 >> 8 & 0xff);
                    barray[23] = (byte) (digit15 & 0xff);
                    barray[20] = (byte) (digit14 >> 8 & 0xff);
                    barray[21] = (byte) (digit14 & 0xff);
                    barray[18] = (byte) (digit13 >> 8 & 0xff);
                    barray[19] = (byte) (digit13 & 0xff);
                    barray[16] = (byte) (digit12 >> 8 & 0xff);
                    barray[17] = (byte) (digit12 & 0xff);
                    barray[14] = (byte) (digit11 >> 8 & 0xff);
                    barray[15] = (byte) (digit11 & 0xff);
                    barray[12] = (byte) (digit10 >> 8 & 0xff);
                    barray[13] = (byte) (digit10 & 0xff);
                    barray[10] = (byte) (digit9 >> 8 & 0xff);
                    barray[11] = (byte) (digit9 & 0xff);
                    barray[8] = (byte) (digit8 >> 8 & 0xff);
                    barray[9] = (byte) (digit8 & 0xff);
                    barray[6] = (byte) (digit7 >> 8 & 0xff);
                    barray[7] = (byte) (digit7 & 0xff);
                    barray[4] = (byte) (digit6 >> 8 & 0xff);
                    barray[5] = (byte) (digit6 & 0xff);
                    barray[2] = (byte) (digit5 >> 8 & 0xff);
                    barray[3] = (byte) (digit5 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit4 & 0xff);
                }
                break;
            }

            case 5: // '\005'
            {
                byte x = (byte) (digit5 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 43;
                    barray = new byte[numbytes];
                    barray[41] = (byte) (digit26 >> 8 & 0xff);
                    barray[42] = (byte) (digit26 & 0xff);
                    barray[39] = (byte) (digit25 >> 8 & 0xff);
                    barray[40] = (byte) (digit25 & 0xff);
                    barray[37] = (byte) (digit24 >> 8 & 0xff);
                    barray[38] = (byte) (digit24 & 0xff);
                    barray[35] = (byte) (digit23 >> 8 & 0xff);
                    barray[36] = (byte) (digit23 & 0xff);
                    barray[33] = (byte) (digit22 >> 8 & 0xff);
                    barray[34] = (byte) (digit22 & 0xff);
                    barray[31] = (byte) (digit21 >> 8 & 0xff);
                    barray[32] = (byte) (digit21 & 0xff);
                    barray[29] = (byte) (digit20 >> 8 & 0xff);
                    barray[30] = (byte) (digit20 & 0xff);
                    barray[27] = (byte) (digit19 >> 8 & 0xff);
                    barray[28] = (byte) (digit19 & 0xff);
                    barray[25] = (byte) (digit18 >> 8 & 0xff);
                    barray[26] = (byte) (digit18 & 0xff);
                    barray[23] = (byte) (digit17 >> 8 & 0xff);
                    barray[24] = (byte) (digit17 & 0xff);
                    barray[21] = (byte) (digit16 >> 8 & 0xff);
                    barray[22] = (byte) (digit16 & 0xff);
                    barray[19] = (byte) (digit15 >> 8 & 0xff);
                    barray[20] = (byte) (digit15 & 0xff);
                    barray[17] = (byte) (digit14 >> 8 & 0xff);
                    barray[18] = (byte) (digit14 & 0xff);
                    barray[15] = (byte) (digit13 >> 8 & 0xff);
                    barray[16] = (byte) (digit13 & 0xff);
                    barray[13] = (byte) (digit12 >> 8 & 0xff);
                    barray[14] = (byte) (digit12 & 0xff);
                    barray[11] = (byte) (digit11 >> 8 & 0xff);
                    barray[12] = (byte) (digit11 & 0xff);
                    barray[9] = (byte) (digit10 >> 8 & 0xff);
                    barray[10] = (byte) (digit10 & 0xff);
                    barray[7] = (byte) (digit9 >> 8 & 0xff);
                    barray[8] = (byte) (digit9 & 0xff);
                    barray[5] = (byte) (digit8 >> 8 & 0xff);
                    barray[6] = (byte) (digit8 & 0xff);
                    barray[3] = (byte) (digit7 >> 8 & 0xff);
                    barray[4] = (byte) (digit7 & 0xff);
                    barray[1] = (byte) (digit6 >> 8 & 0xff);
                    barray[2] = (byte) (digit6 & 0xff);
                    barray[0] = (byte) (digit5 & 0xff);
                } else {
                    int numbytes = 44;
                    barray = new byte[numbytes];
                    barray[42] = (byte) (digit26 >> 8 & 0xff);
                    barray[43] = (byte) (digit26 & 0xff);
                    barray[40] = (byte) (digit25 >> 8 & 0xff);
                    barray[41] = (byte) (digit25 & 0xff);
                    barray[38] = (byte) (digit24 >> 8 & 0xff);
                    barray[39] = (byte) (digit24 & 0xff);
                    barray[36] = (byte) (digit23 >> 8 & 0xff);
                    barray[37] = (byte) (digit23 & 0xff);
                    barray[34] = (byte) (digit22 >> 8 & 0xff);
                    barray[35] = (byte) (digit22 & 0xff);
                    barray[32] = (byte) (digit21 >> 8 & 0xff);
                    barray[33] = (byte) (digit21 & 0xff);
                    barray[30] = (byte) (digit20 >> 8 & 0xff);
                    barray[31] = (byte) (digit20 & 0xff);
                    barray[28] = (byte) (digit19 >> 8 & 0xff);
                    barray[29] = (byte) (digit19 & 0xff);
                    barray[26] = (byte) (digit18 >> 8 & 0xff);
                    barray[27] = (byte) (digit18 & 0xff);
                    barray[24] = (byte) (digit17 >> 8 & 0xff);
                    barray[25] = (byte) (digit17 & 0xff);
                    barray[22] = (byte) (digit16 >> 8 & 0xff);
                    barray[23] = (byte) (digit16 & 0xff);
                    barray[20] = (byte) (digit15 >> 8 & 0xff);
                    barray[21] = (byte) (digit15 & 0xff);
                    barray[18] = (byte) (digit14 >> 8 & 0xff);
                    barray[19] = (byte) (digit14 & 0xff);
                    barray[16] = (byte) (digit13 >> 8 & 0xff);
                    barray[17] = (byte) (digit13 & 0xff);
                    barray[14] = (byte) (digit12 >> 8 & 0xff);
                    barray[15] = (byte) (digit12 & 0xff);
                    barray[12] = (byte) (digit11 >> 8 & 0xff);
                    barray[13] = (byte) (digit11 & 0xff);
                    barray[10] = (byte) (digit10 >> 8 & 0xff);
                    barray[11] = (byte) (digit10 & 0xff);
                    barray[8] = (byte) (digit9 >> 8 & 0xff);
                    barray[9] = (byte) (digit9 & 0xff);
                    barray[6] = (byte) (digit8 >> 8 & 0xff);
                    barray[7] = (byte) (digit8 & 0xff);
                    barray[4] = (byte) (digit7 >> 8 & 0xff);
                    barray[5] = (byte) (digit7 & 0xff);
                    barray[2] = (byte) (digit6 >> 8 & 0xff);
                    barray[3] = (byte) (digit6 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit5 & 0xff);
                }
                break;
            }

            case 6: // '\006'
            {
                byte x = (byte) (digit6 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 41;
                    barray = new byte[numbytes];
                    barray[39] = (byte) (digit26 >> 8 & 0xff);
                    barray[40] = (byte) (digit26 & 0xff);
                    barray[37] = (byte) (digit25 >> 8 & 0xff);
                    barray[38] = (byte) (digit25 & 0xff);
                    barray[35] = (byte) (digit24 >> 8 & 0xff);
                    barray[36] = (byte) (digit24 & 0xff);
                    barray[33] = (byte) (digit23 >> 8 & 0xff);
                    barray[34] = (byte) (digit23 & 0xff);
                    barray[31] = (byte) (digit22 >> 8 & 0xff);
                    barray[32] = (byte) (digit22 & 0xff);
                    barray[29] = (byte) (digit21 >> 8 & 0xff);
                    barray[30] = (byte) (digit21 & 0xff);
                    barray[27] = (byte) (digit20 >> 8 & 0xff);
                    barray[28] = (byte) (digit20 & 0xff);
                    barray[25] = (byte) (digit19 >> 8 & 0xff);
                    barray[26] = (byte) (digit19 & 0xff);
                    barray[23] = (byte) (digit18 >> 8 & 0xff);
                    barray[24] = (byte) (digit18 & 0xff);
                    barray[21] = (byte) (digit17 >> 8 & 0xff);
                    barray[22] = (byte) (digit17 & 0xff);
                    barray[19] = (byte) (digit16 >> 8 & 0xff);
                    barray[20] = (byte) (digit16 & 0xff);
                    barray[17] = (byte) (digit15 >> 8 & 0xff);
                    barray[18] = (byte) (digit15 & 0xff);
                    barray[15] = (byte) (digit14 >> 8 & 0xff);
                    barray[16] = (byte) (digit14 & 0xff);
                    barray[13] = (byte) (digit13 >> 8 & 0xff);
                    barray[14] = (byte) (digit13 & 0xff);
                    barray[11] = (byte) (digit12 >> 8 & 0xff);
                    barray[12] = (byte) (digit12 & 0xff);
                    barray[9] = (byte) (digit11 >> 8 & 0xff);
                    barray[10] = (byte) (digit11 & 0xff);
                    barray[7] = (byte) (digit10 >> 8 & 0xff);
                    barray[8] = (byte) (digit10 & 0xff);
                    barray[5] = (byte) (digit9 >> 8 & 0xff);
                    barray[6] = (byte) (digit9 & 0xff);
                    barray[3] = (byte) (digit8 >> 8 & 0xff);
                    barray[4] = (byte) (digit8 & 0xff);
                    barray[1] = (byte) (digit7 >> 8 & 0xff);
                    barray[2] = (byte) (digit7 & 0xff);
                    barray[0] = (byte) (digit6 & 0xff);
                } else {
                    int numbytes = 42;
                    barray = new byte[numbytes];
                    barray[40] = (byte) (digit26 >> 8 & 0xff);
                    barray[41] = (byte) (digit26 & 0xff);
                    barray[38] = (byte) (digit25 >> 8 & 0xff);
                    barray[39] = (byte) (digit25 & 0xff);
                    barray[36] = (byte) (digit24 >> 8 & 0xff);
                    barray[37] = (byte) (digit24 & 0xff);
                    barray[34] = (byte) (digit23 >> 8 & 0xff);
                    barray[35] = (byte) (digit23 & 0xff);
                    barray[32] = (byte) (digit22 >> 8 & 0xff);
                    barray[33] = (byte) (digit22 & 0xff);
                    barray[30] = (byte) (digit21 >> 8 & 0xff);
                    barray[31] = (byte) (digit21 & 0xff);
                    barray[28] = (byte) (digit20 >> 8 & 0xff);
                    barray[29] = (byte) (digit20 & 0xff);
                    barray[26] = (byte) (digit19 >> 8 & 0xff);
                    barray[27] = (byte) (digit19 & 0xff);
                    barray[24] = (byte) (digit18 >> 8 & 0xff);
                    barray[25] = (byte) (digit18 & 0xff);
                    barray[22] = (byte) (digit17 >> 8 & 0xff);
                    barray[23] = (byte) (digit17 & 0xff);
                    barray[20] = (byte) (digit16 >> 8 & 0xff);
                    barray[21] = (byte) (digit16 & 0xff);
                    barray[18] = (byte) (digit15 >> 8 & 0xff);
                    barray[19] = (byte) (digit15 & 0xff);
                    barray[16] = (byte) (digit14 >> 8 & 0xff);
                    barray[17] = (byte) (digit14 & 0xff);
                    barray[14] = (byte) (digit13 >> 8 & 0xff);
                    barray[15] = (byte) (digit13 & 0xff);
                    barray[12] = (byte) (digit12 >> 8 & 0xff);
                    barray[13] = (byte) (digit12 & 0xff);
                    barray[10] = (byte) (digit11 >> 8 & 0xff);
                    barray[11] = (byte) (digit11 & 0xff);
                    barray[8] = (byte) (digit10 >> 8 & 0xff);
                    barray[9] = (byte) (digit10 & 0xff);
                    barray[6] = (byte) (digit9 >> 8 & 0xff);
                    barray[7] = (byte) (digit9 & 0xff);
                    barray[4] = (byte) (digit8 >> 8 & 0xff);
                    barray[5] = (byte) (digit8 & 0xff);
                    barray[2] = (byte) (digit7 >> 8 & 0xff);
                    barray[3] = (byte) (digit7 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit6 & 0xff);
                }
                break;
            }

            case 7: // '\007'
            {
                byte x = (byte) (digit7 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 39;
                    barray = new byte[numbytes];
                    barray[37] = (byte) (digit26 >> 8 & 0xff);
                    barray[38] = (byte) (digit26 & 0xff);
                    barray[35] = (byte) (digit25 >> 8 & 0xff);
                    barray[36] = (byte) (digit25 & 0xff);
                    barray[33] = (byte) (digit24 >> 8 & 0xff);
                    barray[34] = (byte) (digit24 & 0xff);
                    barray[31] = (byte) (digit23 >> 8 & 0xff);
                    barray[32] = (byte) (digit23 & 0xff);
                    barray[29] = (byte) (digit22 >> 8 & 0xff);
                    barray[30] = (byte) (digit22 & 0xff);
                    barray[27] = (byte) (digit21 >> 8 & 0xff);
                    barray[28] = (byte) (digit21 & 0xff);
                    barray[25] = (byte) (digit20 >> 8 & 0xff);
                    barray[26] = (byte) (digit20 & 0xff);
                    barray[23] = (byte) (digit19 >> 8 & 0xff);
                    barray[24] = (byte) (digit19 & 0xff);
                    barray[21] = (byte) (digit18 >> 8 & 0xff);
                    barray[22] = (byte) (digit18 & 0xff);
                    barray[19] = (byte) (digit17 >> 8 & 0xff);
                    barray[20] = (byte) (digit17 & 0xff);
                    barray[17] = (byte) (digit16 >> 8 & 0xff);
                    barray[18] = (byte) (digit16 & 0xff);
                    barray[15] = (byte) (digit15 >> 8 & 0xff);
                    barray[16] = (byte) (digit15 & 0xff);
                    barray[13] = (byte) (digit14 >> 8 & 0xff);
                    barray[14] = (byte) (digit14 & 0xff);
                    barray[11] = (byte) (digit13 >> 8 & 0xff);
                    barray[12] = (byte) (digit13 & 0xff);
                    barray[9] = (byte) (digit12 >> 8 & 0xff);
                    barray[10] = (byte) (digit12 & 0xff);
                    barray[7] = (byte) (digit11 >> 8 & 0xff);
                    barray[8] = (byte) (digit11 & 0xff);
                    barray[5] = (byte) (digit10 >> 8 & 0xff);
                    barray[6] = (byte) (digit10 & 0xff);
                    barray[3] = (byte) (digit9 >> 8 & 0xff);
                    barray[4] = (byte) (digit9 & 0xff);
                    barray[1] = (byte) (digit8 >> 8 & 0xff);
                    barray[2] = (byte) (digit8 & 0xff);
                    barray[0] = (byte) (digit7 & 0xff);
                } else {
                    int numbytes = 40;
                    barray = new byte[numbytes];
                    barray[38] = (byte) (digit26 >> 8 & 0xff);
                    barray[39] = (byte) (digit26 & 0xff);
                    barray[36] = (byte) (digit25 >> 8 & 0xff);
                    barray[37] = (byte) (digit25 & 0xff);
                    barray[34] = (byte) (digit24 >> 8 & 0xff);
                    barray[35] = (byte) (digit24 & 0xff);
                    barray[32] = (byte) (digit23 >> 8 & 0xff);
                    barray[33] = (byte) (digit23 & 0xff);
                    barray[30] = (byte) (digit22 >> 8 & 0xff);
                    barray[31] = (byte) (digit22 & 0xff);
                    barray[28] = (byte) (digit21 >> 8 & 0xff);
                    barray[29] = (byte) (digit21 & 0xff);
                    barray[26] = (byte) (digit20 >> 8 & 0xff);
                    barray[27] = (byte) (digit20 & 0xff);
                    barray[24] = (byte) (digit19 >> 8 & 0xff);
                    barray[25] = (byte) (digit19 & 0xff);
                    barray[22] = (byte) (digit18 >> 8 & 0xff);
                    barray[23] = (byte) (digit18 & 0xff);
                    barray[20] = (byte) (digit17 >> 8 & 0xff);
                    barray[21] = (byte) (digit17 & 0xff);
                    barray[18] = (byte) (digit16 >> 8 & 0xff);
                    barray[19] = (byte) (digit16 & 0xff);
                    barray[16] = (byte) (digit15 >> 8 & 0xff);
                    barray[17] = (byte) (digit15 & 0xff);
                    barray[14] = (byte) (digit14 >> 8 & 0xff);
                    barray[15] = (byte) (digit14 & 0xff);
                    barray[12] = (byte) (digit13 >> 8 & 0xff);
                    barray[13] = (byte) (digit13 & 0xff);
                    barray[10] = (byte) (digit12 >> 8 & 0xff);
                    barray[11] = (byte) (digit12 & 0xff);
                    barray[8] = (byte) (digit11 >> 8 & 0xff);
                    barray[9] = (byte) (digit11 & 0xff);
                    barray[6] = (byte) (digit10 >> 8 & 0xff);
                    barray[7] = (byte) (digit10 & 0xff);
                    barray[4] = (byte) (digit9 >> 8 & 0xff);
                    barray[5] = (byte) (digit9 & 0xff);
                    barray[2] = (byte) (digit8 >> 8 & 0xff);
                    barray[3] = (byte) (digit8 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit7 & 0xff);
                }
                break;
            }

            case 8: // '\b'
            {
                byte x = (byte) (digit8 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 37;
                    barray = new byte[numbytes];
                    barray[35] = (byte) (digit26 >> 8 & 0xff);
                    barray[36] = (byte) (digit26 & 0xff);
                    barray[33] = (byte) (digit25 >> 8 & 0xff);
                    barray[34] = (byte) (digit25 & 0xff);
                    barray[31] = (byte) (digit24 >> 8 & 0xff);
                    barray[32] = (byte) (digit24 & 0xff);
                    barray[29] = (byte) (digit23 >> 8 & 0xff);
                    barray[30] = (byte) (digit23 & 0xff);
                    barray[27] = (byte) (digit22 >> 8 & 0xff);
                    barray[28] = (byte) (digit22 & 0xff);
                    barray[25] = (byte) (digit21 >> 8 & 0xff);
                    barray[26] = (byte) (digit21 & 0xff);
                    barray[23] = (byte) (digit20 >> 8 & 0xff);
                    barray[24] = (byte) (digit20 & 0xff);
                    barray[21] = (byte) (digit19 >> 8 & 0xff);
                    barray[22] = (byte) (digit19 & 0xff);
                    barray[19] = (byte) (digit18 >> 8 & 0xff);
                    barray[20] = (byte) (digit18 & 0xff);
                    barray[17] = (byte) (digit17 >> 8 & 0xff);
                    barray[18] = (byte) (digit17 & 0xff);
                    barray[15] = (byte) (digit16 >> 8 & 0xff);
                    barray[16] = (byte) (digit16 & 0xff);
                    barray[13] = (byte) (digit15 >> 8 & 0xff);
                    barray[14] = (byte) (digit15 & 0xff);
                    barray[11] = (byte) (digit14 >> 8 & 0xff);
                    barray[12] = (byte) (digit14 & 0xff);
                    barray[9] = (byte) (digit13 >> 8 & 0xff);
                    barray[10] = (byte) (digit13 & 0xff);
                    barray[7] = (byte) (digit12 >> 8 & 0xff);
                    barray[8] = (byte) (digit12 & 0xff);
                    barray[5] = (byte) (digit11 >> 8 & 0xff);
                    barray[6] = (byte) (digit11 & 0xff);
                    barray[3] = (byte) (digit10 >> 8 & 0xff);
                    barray[4] = (byte) (digit10 & 0xff);
                    barray[1] = (byte) (digit9 >> 8 & 0xff);
                    barray[2] = (byte) (digit9 & 0xff);
                    barray[0] = (byte) (digit8 & 0xff);
                } else {
                    int numbytes = 38;
                    barray = new byte[numbytes];
                    barray[36] = (byte) (digit26 >> 8 & 0xff);
                    barray[37] = (byte) (digit26 & 0xff);
                    barray[34] = (byte) (digit25 >> 8 & 0xff);
                    barray[35] = (byte) (digit25 & 0xff);
                    barray[32] = (byte) (digit24 >> 8 & 0xff);
                    barray[33] = (byte) (digit24 & 0xff);
                    barray[30] = (byte) (digit23 >> 8 & 0xff);
                    barray[31] = (byte) (digit23 & 0xff);
                    barray[28] = (byte) (digit22 >> 8 & 0xff);
                    barray[29] = (byte) (digit22 & 0xff);
                    barray[26] = (byte) (digit21 >> 8 & 0xff);
                    barray[27] = (byte) (digit21 & 0xff);
                    barray[24] = (byte) (digit20 >> 8 & 0xff);
                    barray[25] = (byte) (digit20 & 0xff);
                    barray[22] = (byte) (digit19 >> 8 & 0xff);
                    barray[23] = (byte) (digit19 & 0xff);
                    barray[20] = (byte) (digit18 >> 8 & 0xff);
                    barray[21] = (byte) (digit18 & 0xff);
                    barray[18] = (byte) (digit17 >> 8 & 0xff);
                    barray[19] = (byte) (digit17 & 0xff);
                    barray[16] = (byte) (digit16 >> 8 & 0xff);
                    barray[17] = (byte) (digit16 & 0xff);
                    barray[14] = (byte) (digit15 >> 8 & 0xff);
                    barray[15] = (byte) (digit15 & 0xff);
                    barray[12] = (byte) (digit14 >> 8 & 0xff);
                    barray[13] = (byte) (digit14 & 0xff);
                    barray[10] = (byte) (digit13 >> 8 & 0xff);
                    barray[11] = (byte) (digit13 & 0xff);
                    barray[8] = (byte) (digit12 >> 8 & 0xff);
                    barray[9] = (byte) (digit12 & 0xff);
                    barray[6] = (byte) (digit11 >> 8 & 0xff);
                    barray[7] = (byte) (digit11 & 0xff);
                    barray[4] = (byte) (digit10 >> 8 & 0xff);
                    barray[5] = (byte) (digit10 & 0xff);
                    barray[2] = (byte) (digit9 >> 8 & 0xff);
                    barray[3] = (byte) (digit9 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit8 & 0xff);
                }
                break;
            }

            case 9: // '\t'
            {
                byte x = (byte) (digit9 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 35;
                    barray = new byte[numbytes];
                    barray[33] = (byte) (digit26 >> 8 & 0xff);
                    barray[34] = (byte) (digit26 & 0xff);
                    barray[31] = (byte) (digit25 >> 8 & 0xff);
                    barray[32] = (byte) (digit25 & 0xff);
                    barray[29] = (byte) (digit24 >> 8 & 0xff);
                    barray[30] = (byte) (digit24 & 0xff);
                    barray[27] = (byte) (digit23 >> 8 & 0xff);
                    barray[28] = (byte) (digit23 & 0xff);
                    barray[25] = (byte) (digit22 >> 8 & 0xff);
                    barray[26] = (byte) (digit22 & 0xff);
                    barray[23] = (byte) (digit21 >> 8 & 0xff);
                    barray[24] = (byte) (digit21 & 0xff);
                    barray[21] = (byte) (digit20 >> 8 & 0xff);
                    barray[22] = (byte) (digit20 & 0xff);
                    barray[19] = (byte) (digit19 >> 8 & 0xff);
                    barray[20] = (byte) (digit19 & 0xff);
                    barray[17] = (byte) (digit18 >> 8 & 0xff);
                    barray[18] = (byte) (digit18 & 0xff);
                    barray[15] = (byte) (digit17 >> 8 & 0xff);
                    barray[16] = (byte) (digit17 & 0xff);
                    barray[13] = (byte) (digit16 >> 8 & 0xff);
                    barray[14] = (byte) (digit16 & 0xff);
                    barray[11] = (byte) (digit15 >> 8 & 0xff);
                    barray[12] = (byte) (digit15 & 0xff);
                    barray[9] = (byte) (digit14 >> 8 & 0xff);
                    barray[10] = (byte) (digit14 & 0xff);
                    barray[7] = (byte) (digit13 >> 8 & 0xff);
                    barray[8] = (byte) (digit13 & 0xff);
                    barray[5] = (byte) (digit12 >> 8 & 0xff);
                    barray[6] = (byte) (digit12 & 0xff);
                    barray[3] = (byte) (digit11 >> 8 & 0xff);
                    barray[4] = (byte) (digit11 & 0xff);
                    barray[1] = (byte) (digit10 >> 8 & 0xff);
                    barray[2] = (byte) (digit10 & 0xff);
                    barray[0] = (byte) (digit9 & 0xff);
                } else {
                    int numbytes = 36;
                    barray = new byte[numbytes];
                    barray[34] = (byte) (digit26 >> 8 & 0xff);
                    barray[35] = (byte) (digit26 & 0xff);
                    barray[32] = (byte) (digit25 >> 8 & 0xff);
                    barray[33] = (byte) (digit25 & 0xff);
                    barray[30] = (byte) (digit24 >> 8 & 0xff);
                    barray[31] = (byte) (digit24 & 0xff);
                    barray[28] = (byte) (digit23 >> 8 & 0xff);
                    barray[29] = (byte) (digit23 & 0xff);
                    barray[26] = (byte) (digit22 >> 8 & 0xff);
                    barray[27] = (byte) (digit22 & 0xff);
                    barray[24] = (byte) (digit21 >> 8 & 0xff);
                    barray[25] = (byte) (digit21 & 0xff);
                    barray[22] = (byte) (digit20 >> 8 & 0xff);
                    barray[23] = (byte) (digit20 & 0xff);
                    barray[20] = (byte) (digit19 >> 8 & 0xff);
                    barray[21] = (byte) (digit19 & 0xff);
                    barray[18] = (byte) (digit18 >> 8 & 0xff);
                    barray[19] = (byte) (digit18 & 0xff);
                    barray[16] = (byte) (digit17 >> 8 & 0xff);
                    barray[17] = (byte) (digit17 & 0xff);
                    barray[14] = (byte) (digit16 >> 8 & 0xff);
                    barray[15] = (byte) (digit16 & 0xff);
                    barray[12] = (byte) (digit15 >> 8 & 0xff);
                    barray[13] = (byte) (digit15 & 0xff);
                    barray[10] = (byte) (digit14 >> 8 & 0xff);
                    barray[11] = (byte) (digit14 & 0xff);
                    barray[8] = (byte) (digit13 >> 8 & 0xff);
                    barray[9] = (byte) (digit13 & 0xff);
                    barray[6] = (byte) (digit12 >> 8 & 0xff);
                    barray[7] = (byte) (digit12 & 0xff);
                    barray[4] = (byte) (digit11 >> 8 & 0xff);
                    barray[5] = (byte) (digit11 & 0xff);
                    barray[2] = (byte) (digit10 >> 8 & 0xff);
                    barray[3] = (byte) (digit10 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit9 & 0xff);
                }
                break;
            }

            case 10: // '\n'
            {
                byte x = (byte) (digit10 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 33;
                    barray = new byte[numbytes];
                    barray[31] = (byte) (digit26 >> 8 & 0xff);
                    barray[32] = (byte) (digit26 & 0xff);
                    barray[29] = (byte) (digit25 >> 8 & 0xff);
                    barray[30] = (byte) (digit25 & 0xff);
                    barray[27] = (byte) (digit24 >> 8 & 0xff);
                    barray[28] = (byte) (digit24 & 0xff);
                    barray[25] = (byte) (digit23 >> 8 & 0xff);
                    barray[26] = (byte) (digit23 & 0xff);
                    barray[23] = (byte) (digit22 >> 8 & 0xff);
                    barray[24] = (byte) (digit22 & 0xff);
                    barray[21] = (byte) (digit21 >> 8 & 0xff);
                    barray[22] = (byte) (digit21 & 0xff);
                    barray[19] = (byte) (digit20 >> 8 & 0xff);
                    barray[20] = (byte) (digit20 & 0xff);
                    barray[17] = (byte) (digit19 >> 8 & 0xff);
                    barray[18] = (byte) (digit19 & 0xff);
                    barray[15] = (byte) (digit18 >> 8 & 0xff);
                    barray[16] = (byte) (digit18 & 0xff);
                    barray[13] = (byte) (digit17 >> 8 & 0xff);
                    barray[14] = (byte) (digit17 & 0xff);
                    barray[11] = (byte) (digit16 >> 8 & 0xff);
                    barray[12] = (byte) (digit16 & 0xff);
                    barray[9] = (byte) (digit15 >> 8 & 0xff);
                    barray[10] = (byte) (digit15 & 0xff);
                    barray[7] = (byte) (digit14 >> 8 & 0xff);
                    barray[8] = (byte) (digit14 & 0xff);
                    barray[5] = (byte) (digit13 >> 8 & 0xff);
                    barray[6] = (byte) (digit13 & 0xff);
                    barray[3] = (byte) (digit12 >> 8 & 0xff);
                    barray[4] = (byte) (digit12 & 0xff);
                    barray[1] = (byte) (digit11 >> 8 & 0xff);
                    barray[2] = (byte) (digit11 & 0xff);
                    barray[0] = (byte) (digit10 & 0xff);
                } else {
                    int numbytes = 34;
                    barray = new byte[numbytes];
                    barray[32] = (byte) (digit26 >> 8 & 0xff);
                    barray[33] = (byte) (digit26 & 0xff);
                    barray[30] = (byte) (digit25 >> 8 & 0xff);
                    barray[31] = (byte) (digit25 & 0xff);
                    barray[28] = (byte) (digit24 >> 8 & 0xff);
                    barray[29] = (byte) (digit24 & 0xff);
                    barray[26] = (byte) (digit23 >> 8 & 0xff);
                    barray[27] = (byte) (digit23 & 0xff);
                    barray[24] = (byte) (digit22 >> 8 & 0xff);
                    barray[25] = (byte) (digit22 & 0xff);
                    barray[22] = (byte) (digit21 >> 8 & 0xff);
                    barray[23] = (byte) (digit21 & 0xff);
                    barray[20] = (byte) (digit20 >> 8 & 0xff);
                    barray[21] = (byte) (digit20 & 0xff);
                    barray[18] = (byte) (digit19 >> 8 & 0xff);
                    barray[19] = (byte) (digit19 & 0xff);
                    barray[16] = (byte) (digit18 >> 8 & 0xff);
                    barray[17] = (byte) (digit18 & 0xff);
                    barray[14] = (byte) (digit17 >> 8 & 0xff);
                    barray[15] = (byte) (digit17 & 0xff);
                    barray[12] = (byte) (digit16 >> 8 & 0xff);
                    barray[13] = (byte) (digit16 & 0xff);
                    barray[10] = (byte) (digit15 >> 8 & 0xff);
                    barray[11] = (byte) (digit15 & 0xff);
                    barray[8] = (byte) (digit14 >> 8 & 0xff);
                    barray[9] = (byte) (digit14 & 0xff);
                    barray[6] = (byte) (digit13 >> 8 & 0xff);
                    barray[7] = (byte) (digit13 & 0xff);
                    barray[4] = (byte) (digit12 >> 8 & 0xff);
                    barray[5] = (byte) (digit12 & 0xff);
                    barray[2] = (byte) (digit11 >> 8 & 0xff);
                    barray[3] = (byte) (digit11 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit10 & 0xff);
                }
                break;
            }

            case 11: // '\013'
            {
                byte x = (byte) (digit11 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 31;
                    barray = new byte[numbytes];
                    barray[29] = (byte) (digit26 >> 8 & 0xff);
                    barray[30] = (byte) (digit26 & 0xff);
                    barray[27] = (byte) (digit25 >> 8 & 0xff);
                    barray[28] = (byte) (digit25 & 0xff);
                    barray[25] = (byte) (digit24 >> 8 & 0xff);
                    barray[26] = (byte) (digit24 & 0xff);
                    barray[23] = (byte) (digit23 >> 8 & 0xff);
                    barray[24] = (byte) (digit23 & 0xff);
                    barray[21] = (byte) (digit22 >> 8 & 0xff);
                    barray[22] = (byte) (digit22 & 0xff);
                    barray[19] = (byte) (digit21 >> 8 & 0xff);
                    barray[20] = (byte) (digit21 & 0xff);
                    barray[17] = (byte) (digit20 >> 8 & 0xff);
                    barray[18] = (byte) (digit20 & 0xff);
                    barray[15] = (byte) (digit19 >> 8 & 0xff);
                    barray[16] = (byte) (digit19 & 0xff);
                    barray[13] = (byte) (digit18 >> 8 & 0xff);
                    barray[14] = (byte) (digit18 & 0xff);
                    barray[11] = (byte) (digit17 >> 8 & 0xff);
                    barray[12] = (byte) (digit17 & 0xff);
                    barray[9] = (byte) (digit16 >> 8 & 0xff);
                    barray[10] = (byte) (digit16 & 0xff);
                    barray[7] = (byte) (digit15 >> 8 & 0xff);
                    barray[8] = (byte) (digit15 & 0xff);
                    barray[5] = (byte) (digit14 >> 8 & 0xff);
                    barray[6] = (byte) (digit14 & 0xff);
                    barray[3] = (byte) (digit13 >> 8 & 0xff);
                    barray[4] = (byte) (digit13 & 0xff);
                    barray[1] = (byte) (digit12 >> 8 & 0xff);
                    barray[2] = (byte) (digit12 & 0xff);
                    barray[0] = (byte) (digit11 & 0xff);
                } else {
                    int numbytes = 32;
                    barray = new byte[numbytes];
                    barray[30] = (byte) (digit26 >> 8 & 0xff);
                    barray[31] = (byte) (digit26 & 0xff);
                    barray[28] = (byte) (digit25 >> 8 & 0xff);
                    barray[29] = (byte) (digit25 & 0xff);
                    barray[26] = (byte) (digit24 >> 8 & 0xff);
                    barray[27] = (byte) (digit24 & 0xff);
                    barray[24] = (byte) (digit23 >> 8 & 0xff);
                    barray[25] = (byte) (digit23 & 0xff);
                    barray[22] = (byte) (digit22 >> 8 & 0xff);
                    barray[23] = (byte) (digit22 & 0xff);
                    barray[20] = (byte) (digit21 >> 8 & 0xff);
                    barray[21] = (byte) (digit21 & 0xff);
                    barray[18] = (byte) (digit20 >> 8 & 0xff);
                    barray[19] = (byte) (digit20 & 0xff);
                    barray[16] = (byte) (digit19 >> 8 & 0xff);
                    barray[17] = (byte) (digit19 & 0xff);
                    barray[14] = (byte) (digit18 >> 8 & 0xff);
                    barray[15] = (byte) (digit18 & 0xff);
                    barray[12] = (byte) (digit17 >> 8 & 0xff);
                    barray[13] = (byte) (digit17 & 0xff);
                    barray[10] = (byte) (digit16 >> 8 & 0xff);
                    barray[11] = (byte) (digit16 & 0xff);
                    barray[8] = (byte) (digit15 >> 8 & 0xff);
                    barray[9] = (byte) (digit15 & 0xff);
                    barray[6] = (byte) (digit14 >> 8 & 0xff);
                    barray[7] = (byte) (digit14 & 0xff);
                    barray[4] = (byte) (digit13 >> 8 & 0xff);
                    barray[5] = (byte) (digit13 & 0xff);
                    barray[2] = (byte) (digit12 >> 8 & 0xff);
                    barray[3] = (byte) (digit12 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit11 & 0xff);
                }
                break;
            }

            case 12: // '\f'
            {
                byte x = (byte) (digit12 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 29;
                    barray = new byte[numbytes];
                    barray[27] = (byte) (digit26 >> 8 & 0xff);
                    barray[28] = (byte) (digit26 & 0xff);
                    barray[25] = (byte) (digit25 >> 8 & 0xff);
                    barray[26] = (byte) (digit25 & 0xff);
                    barray[23] = (byte) (digit24 >> 8 & 0xff);
                    barray[24] = (byte) (digit24 & 0xff);
                    barray[21] = (byte) (digit23 >> 8 & 0xff);
                    barray[22] = (byte) (digit23 & 0xff);
                    barray[19] = (byte) (digit22 >> 8 & 0xff);
                    barray[20] = (byte) (digit22 & 0xff);
                    barray[17] = (byte) (digit21 >> 8 & 0xff);
                    barray[18] = (byte) (digit21 & 0xff);
                    barray[15] = (byte) (digit20 >> 8 & 0xff);
                    barray[16] = (byte) (digit20 & 0xff);
                    barray[13] = (byte) (digit19 >> 8 & 0xff);
                    barray[14] = (byte) (digit19 & 0xff);
                    barray[11] = (byte) (digit18 >> 8 & 0xff);
                    barray[12] = (byte) (digit18 & 0xff);
                    barray[9] = (byte) (digit17 >> 8 & 0xff);
                    barray[10] = (byte) (digit17 & 0xff);
                    barray[7] = (byte) (digit16 >> 8 & 0xff);
                    barray[8] = (byte) (digit16 & 0xff);
                    barray[5] = (byte) (digit15 >> 8 & 0xff);
                    barray[6] = (byte) (digit15 & 0xff);
                    barray[3] = (byte) (digit14 >> 8 & 0xff);
                    barray[4] = (byte) (digit14 & 0xff);
                    barray[1] = (byte) (digit13 >> 8 & 0xff);
                    barray[2] = (byte) (digit13 & 0xff);
                    barray[0] = (byte) (digit12 & 0xff);
                } else {
                    int numbytes = 30;
                    barray = new byte[numbytes];
                    barray[28] = (byte) (digit26 >> 8 & 0xff);
                    barray[29] = (byte) (digit26 & 0xff);
                    barray[26] = (byte) (digit25 >> 8 & 0xff);
                    barray[27] = (byte) (digit25 & 0xff);
                    barray[24] = (byte) (digit24 >> 8 & 0xff);
                    barray[25] = (byte) (digit24 & 0xff);
                    barray[22] = (byte) (digit23 >> 8 & 0xff);
                    barray[23] = (byte) (digit23 & 0xff);
                    barray[20] = (byte) (digit22 >> 8 & 0xff);
                    barray[21] = (byte) (digit22 & 0xff);
                    barray[18] = (byte) (digit21 >> 8 & 0xff);
                    barray[19] = (byte) (digit21 & 0xff);
                    barray[16] = (byte) (digit20 >> 8 & 0xff);
                    barray[17] = (byte) (digit20 & 0xff);
                    barray[14] = (byte) (digit19 >> 8 & 0xff);
                    barray[15] = (byte) (digit19 & 0xff);
                    barray[12] = (byte) (digit18 >> 8 & 0xff);
                    barray[13] = (byte) (digit18 & 0xff);
                    barray[10] = (byte) (digit17 >> 8 & 0xff);
                    barray[11] = (byte) (digit17 & 0xff);
                    barray[8] = (byte) (digit16 >> 8 & 0xff);
                    barray[9] = (byte) (digit16 & 0xff);
                    barray[6] = (byte) (digit15 >> 8 & 0xff);
                    barray[7] = (byte) (digit15 & 0xff);
                    barray[4] = (byte) (digit14 >> 8 & 0xff);
                    barray[5] = (byte) (digit14 & 0xff);
                    barray[2] = (byte) (digit13 >> 8 & 0xff);
                    barray[3] = (byte) (digit13 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit12 & 0xff);
                }
                break;
            }

            case 13: // '\r'
            {
                byte x = (byte) (digit13 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 27;
                    barray = new byte[numbytes];
                    barray[25] = (byte) (digit26 >> 8 & 0xff);
                    barray[26] = (byte) (digit26 & 0xff);
                    barray[23] = (byte) (digit25 >> 8 & 0xff);
                    barray[24] = (byte) (digit25 & 0xff);
                    barray[21] = (byte) (digit24 >> 8 & 0xff);
                    barray[22] = (byte) (digit24 & 0xff);
                    barray[19] = (byte) (digit23 >> 8 & 0xff);
                    barray[20] = (byte) (digit23 & 0xff);
                    barray[17] = (byte) (digit22 >> 8 & 0xff);
                    barray[18] = (byte) (digit22 & 0xff);
                    barray[15] = (byte) (digit21 >> 8 & 0xff);
                    barray[16] = (byte) (digit21 & 0xff);
                    barray[13] = (byte) (digit20 >> 8 & 0xff);
                    barray[14] = (byte) (digit20 & 0xff);
                    barray[11] = (byte) (digit19 >> 8 & 0xff);
                    barray[12] = (byte) (digit19 & 0xff);
                    barray[9] = (byte) (digit18 >> 8 & 0xff);
                    barray[10] = (byte) (digit18 & 0xff);
                    barray[7] = (byte) (digit17 >> 8 & 0xff);
                    barray[8] = (byte) (digit17 & 0xff);
                    barray[5] = (byte) (digit16 >> 8 & 0xff);
                    barray[6] = (byte) (digit16 & 0xff);
                    barray[3] = (byte) (digit15 >> 8 & 0xff);
                    barray[4] = (byte) (digit15 & 0xff);
                    barray[1] = (byte) (digit14 >> 8 & 0xff);
                    barray[2] = (byte) (digit14 & 0xff);
                    barray[0] = (byte) (digit13 & 0xff);
                } else {
                    int numbytes = 28;
                    barray = new byte[numbytes];
                    barray[26] = (byte) (digit26 >> 8 & 0xff);
                    barray[27] = (byte) (digit26 & 0xff);
                    barray[24] = (byte) (digit25 >> 8 & 0xff);
                    barray[25] = (byte) (digit25 & 0xff);
                    barray[22] = (byte) (digit24 >> 8 & 0xff);
                    barray[23] = (byte) (digit24 & 0xff);
                    barray[20] = (byte) (digit23 >> 8 & 0xff);
                    barray[21] = (byte) (digit23 & 0xff);
                    barray[18] = (byte) (digit22 >> 8 & 0xff);
                    barray[19] = (byte) (digit22 & 0xff);
                    barray[16] = (byte) (digit21 >> 8 & 0xff);
                    barray[17] = (byte) (digit21 & 0xff);
                    barray[14] = (byte) (digit20 >> 8 & 0xff);
                    barray[15] = (byte) (digit20 & 0xff);
                    barray[12] = (byte) (digit19 >> 8 & 0xff);
                    barray[13] = (byte) (digit19 & 0xff);
                    barray[10] = (byte) (digit18 >> 8 & 0xff);
                    barray[11] = (byte) (digit18 & 0xff);
                    barray[8] = (byte) (digit17 >> 8 & 0xff);
                    barray[9] = (byte) (digit17 & 0xff);
                    barray[6] = (byte) (digit16 >> 8 & 0xff);
                    barray[7] = (byte) (digit16 & 0xff);
                    barray[4] = (byte) (digit15 >> 8 & 0xff);
                    barray[5] = (byte) (digit15 & 0xff);
                    barray[2] = (byte) (digit14 >> 8 & 0xff);
                    barray[3] = (byte) (digit14 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit13 & 0xff);
                }
                break;
            }

            case 14: // '\016'
            {
                byte x = (byte) (digit14 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 25;
                    barray = new byte[numbytes];
                    barray[23] = (byte) (digit26 >> 8 & 0xff);
                    barray[24] = (byte) (digit26 & 0xff);
                    barray[21] = (byte) (digit25 >> 8 & 0xff);
                    barray[22] = (byte) (digit25 & 0xff);
                    barray[19] = (byte) (digit24 >> 8 & 0xff);
                    barray[20] = (byte) (digit24 & 0xff);
                    barray[17] = (byte) (digit23 >> 8 & 0xff);
                    barray[18] = (byte) (digit23 & 0xff);
                    barray[15] = (byte) (digit22 >> 8 & 0xff);
                    barray[16] = (byte) (digit22 & 0xff);
                    barray[13] = (byte) (digit21 >> 8 & 0xff);
                    barray[14] = (byte) (digit21 & 0xff);
                    barray[11] = (byte) (digit20 >> 8 & 0xff);
                    barray[12] = (byte) (digit20 & 0xff);
                    barray[9] = (byte) (digit19 >> 8 & 0xff);
                    barray[10] = (byte) (digit19 & 0xff);
                    barray[7] = (byte) (digit18 >> 8 & 0xff);
                    barray[8] = (byte) (digit18 & 0xff);
                    barray[5] = (byte) (digit17 >> 8 & 0xff);
                    barray[6] = (byte) (digit17 & 0xff);
                    barray[3] = (byte) (digit16 >> 8 & 0xff);
                    barray[4] = (byte) (digit16 & 0xff);
                    barray[1] = (byte) (digit15 >> 8 & 0xff);
                    barray[2] = (byte) (digit15 & 0xff);
                    barray[0] = (byte) (digit14 & 0xff);
                } else {
                    int numbytes = 26;
                    barray = new byte[numbytes];
                    barray[24] = (byte) (digit26 >> 8 & 0xff);
                    barray[25] = (byte) (digit26 & 0xff);
                    barray[22] = (byte) (digit25 >> 8 & 0xff);
                    barray[23] = (byte) (digit25 & 0xff);
                    barray[20] = (byte) (digit24 >> 8 & 0xff);
                    barray[21] = (byte) (digit24 & 0xff);
                    barray[18] = (byte) (digit23 >> 8 & 0xff);
                    barray[19] = (byte) (digit23 & 0xff);
                    barray[16] = (byte) (digit22 >> 8 & 0xff);
                    barray[17] = (byte) (digit22 & 0xff);
                    barray[14] = (byte) (digit21 >> 8 & 0xff);
                    barray[15] = (byte) (digit21 & 0xff);
                    barray[12] = (byte) (digit20 >> 8 & 0xff);
                    barray[13] = (byte) (digit20 & 0xff);
                    barray[10] = (byte) (digit19 >> 8 & 0xff);
                    barray[11] = (byte) (digit19 & 0xff);
                    barray[8] = (byte) (digit18 >> 8 & 0xff);
                    barray[9] = (byte) (digit18 & 0xff);
                    barray[6] = (byte) (digit17 >> 8 & 0xff);
                    barray[7] = (byte) (digit17 & 0xff);
                    barray[4] = (byte) (digit16 >> 8 & 0xff);
                    barray[5] = (byte) (digit16 & 0xff);
                    barray[2] = (byte) (digit15 >> 8 & 0xff);
                    barray[3] = (byte) (digit15 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit14 & 0xff);
                }
                break;
            }

            case 15: // '\017'
            {
                byte x = (byte) (digit15 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 23;
                    barray = new byte[numbytes];
                    barray[21] = (byte) (digit26 >> 8 & 0xff);
                    barray[22] = (byte) (digit26 & 0xff);
                    barray[19] = (byte) (digit25 >> 8 & 0xff);
                    barray[20] = (byte) (digit25 & 0xff);
                    barray[17] = (byte) (digit24 >> 8 & 0xff);
                    barray[18] = (byte) (digit24 & 0xff);
                    barray[15] = (byte) (digit23 >> 8 & 0xff);
                    barray[16] = (byte) (digit23 & 0xff);
                    barray[13] = (byte) (digit22 >> 8 & 0xff);
                    barray[14] = (byte) (digit22 & 0xff);
                    barray[11] = (byte) (digit21 >> 8 & 0xff);
                    barray[12] = (byte) (digit21 & 0xff);
                    barray[9] = (byte) (digit20 >> 8 & 0xff);
                    barray[10] = (byte) (digit20 & 0xff);
                    barray[7] = (byte) (digit19 >> 8 & 0xff);
                    barray[8] = (byte) (digit19 & 0xff);
                    barray[5] = (byte) (digit18 >> 8 & 0xff);
                    barray[6] = (byte) (digit18 & 0xff);
                    barray[3] = (byte) (digit17 >> 8 & 0xff);
                    barray[4] = (byte) (digit17 & 0xff);
                    barray[1] = (byte) (digit16 >> 8 & 0xff);
                    barray[2] = (byte) (digit16 & 0xff);
                    barray[0] = (byte) (digit15 & 0xff);
                } else {
                    int numbytes = 24;
                    barray = new byte[numbytes];
                    barray[22] = (byte) (digit26 >> 8 & 0xff);
                    barray[23] = (byte) (digit26 & 0xff);
                    barray[20] = (byte) (digit25 >> 8 & 0xff);
                    barray[21] = (byte) (digit25 & 0xff);
                    barray[18] = (byte) (digit24 >> 8 & 0xff);
                    barray[19] = (byte) (digit24 & 0xff);
                    barray[16] = (byte) (digit23 >> 8 & 0xff);
                    barray[17] = (byte) (digit23 & 0xff);
                    barray[14] = (byte) (digit22 >> 8 & 0xff);
                    barray[15] = (byte) (digit22 & 0xff);
                    barray[12] = (byte) (digit21 >> 8 & 0xff);
                    barray[13] = (byte) (digit21 & 0xff);
                    barray[10] = (byte) (digit20 >> 8 & 0xff);
                    barray[11] = (byte) (digit20 & 0xff);
                    barray[8] = (byte) (digit19 >> 8 & 0xff);
                    barray[9] = (byte) (digit19 & 0xff);
                    barray[6] = (byte) (digit18 >> 8 & 0xff);
                    barray[7] = (byte) (digit18 & 0xff);
                    barray[4] = (byte) (digit17 >> 8 & 0xff);
                    barray[5] = (byte) (digit17 & 0xff);
                    barray[2] = (byte) (digit16 >> 8 & 0xff);
                    barray[3] = (byte) (digit16 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit15 & 0xff);
                }
                break;
            }

            case 16: // '\020'
            {
                byte x = (byte) (digit16 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 21;
                    barray = new byte[numbytes];
                    barray[19] = (byte) (digit26 >> 8 & 0xff);
                    barray[20] = (byte) (digit26 & 0xff);
                    barray[17] = (byte) (digit25 >> 8 & 0xff);
                    barray[18] = (byte) (digit25 & 0xff);
                    barray[15] = (byte) (digit24 >> 8 & 0xff);
                    barray[16] = (byte) (digit24 & 0xff);
                    barray[13] = (byte) (digit23 >> 8 & 0xff);
                    barray[14] = (byte) (digit23 & 0xff);
                    barray[11] = (byte) (digit22 >> 8 & 0xff);
                    barray[12] = (byte) (digit22 & 0xff);
                    barray[9] = (byte) (digit21 >> 8 & 0xff);
                    barray[10] = (byte) (digit21 & 0xff);
                    barray[7] = (byte) (digit20 >> 8 & 0xff);
                    barray[8] = (byte) (digit20 & 0xff);
                    barray[5] = (byte) (digit19 >> 8 & 0xff);
                    barray[6] = (byte) (digit19 & 0xff);
                    barray[3] = (byte) (digit18 >> 8 & 0xff);
                    barray[4] = (byte) (digit18 & 0xff);
                    barray[1] = (byte) (digit17 >> 8 & 0xff);
                    barray[2] = (byte) (digit17 & 0xff);
                    barray[0] = (byte) (digit16 & 0xff);
                } else {
                    int numbytes = 22;
                    barray = new byte[numbytes];
                    barray[20] = (byte) (digit26 >> 8 & 0xff);
                    barray[21] = (byte) (digit26 & 0xff);
                    barray[18] = (byte) (digit25 >> 8 & 0xff);
                    barray[19] = (byte) (digit25 & 0xff);
                    barray[16] = (byte) (digit24 >> 8 & 0xff);
                    barray[17] = (byte) (digit24 & 0xff);
                    barray[14] = (byte) (digit23 >> 8 & 0xff);
                    barray[15] = (byte) (digit23 & 0xff);
                    barray[12] = (byte) (digit22 >> 8 & 0xff);
                    barray[13] = (byte) (digit22 & 0xff);
                    barray[10] = (byte) (digit21 >> 8 & 0xff);
                    barray[11] = (byte) (digit21 & 0xff);
                    barray[8] = (byte) (digit20 >> 8 & 0xff);
                    barray[9] = (byte) (digit20 & 0xff);
                    barray[6] = (byte) (digit19 >> 8 & 0xff);
                    barray[7] = (byte) (digit19 & 0xff);
                    barray[4] = (byte) (digit18 >> 8 & 0xff);
                    barray[5] = (byte) (digit18 & 0xff);
                    barray[2] = (byte) (digit17 >> 8 & 0xff);
                    barray[3] = (byte) (digit17 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit16 & 0xff);
                }
                break;
            }

            case 17: // '\021'
            {
                byte x = (byte) (digit17 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 19;
                    barray = new byte[numbytes];
                    barray[17] = (byte) (digit26 >> 8 & 0xff);
                    barray[18] = (byte) (digit26 & 0xff);
                    barray[15] = (byte) (digit25 >> 8 & 0xff);
                    barray[16] = (byte) (digit25 & 0xff);
                    barray[13] = (byte) (digit24 >> 8 & 0xff);
                    barray[14] = (byte) (digit24 & 0xff);
                    barray[11] = (byte) (digit23 >> 8 & 0xff);
                    barray[12] = (byte) (digit23 & 0xff);
                    barray[9] = (byte) (digit22 >> 8 & 0xff);
                    barray[10] = (byte) (digit22 & 0xff);
                    barray[7] = (byte) (digit21 >> 8 & 0xff);
                    barray[8] = (byte) (digit21 & 0xff);
                    barray[5] = (byte) (digit20 >> 8 & 0xff);
                    barray[6] = (byte) (digit20 & 0xff);
                    barray[3] = (byte) (digit19 >> 8 & 0xff);
                    barray[4] = (byte) (digit19 & 0xff);
                    barray[1] = (byte) (digit18 >> 8 & 0xff);
                    barray[2] = (byte) (digit18 & 0xff);
                    barray[0] = (byte) (digit17 & 0xff);
                } else {
                    int numbytes = 20;
                    barray = new byte[numbytes];
                    barray[18] = (byte) (digit26 >> 8 & 0xff);
                    barray[19] = (byte) (digit26 & 0xff);
                    barray[16] = (byte) (digit25 >> 8 & 0xff);
                    barray[17] = (byte) (digit25 & 0xff);
                    barray[14] = (byte) (digit24 >> 8 & 0xff);
                    barray[15] = (byte) (digit24 & 0xff);
                    barray[12] = (byte) (digit23 >> 8 & 0xff);
                    barray[13] = (byte) (digit23 & 0xff);
                    barray[10] = (byte) (digit22 >> 8 & 0xff);
                    barray[11] = (byte) (digit22 & 0xff);
                    barray[8] = (byte) (digit21 >> 8 & 0xff);
                    barray[9] = (byte) (digit21 & 0xff);
                    barray[6] = (byte) (digit20 >> 8 & 0xff);
                    barray[7] = (byte) (digit20 & 0xff);
                    barray[4] = (byte) (digit19 >> 8 & 0xff);
                    barray[5] = (byte) (digit19 & 0xff);
                    barray[2] = (byte) (digit18 >> 8 & 0xff);
                    barray[3] = (byte) (digit18 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit17 & 0xff);
                }
                break;
            }

            case 18: // '\022'
            {
                byte x = (byte) (digit18 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 17;
                    barray = new byte[numbytes];
                    barray[15] = (byte) (digit26 >> 8 & 0xff);
                    barray[16] = (byte) (digit26 & 0xff);
                    barray[13] = (byte) (digit25 >> 8 & 0xff);
                    barray[14] = (byte) (digit25 & 0xff);
                    barray[11] = (byte) (digit24 >> 8 & 0xff);
                    barray[12] = (byte) (digit24 & 0xff);
                    barray[9] = (byte) (digit23 >> 8 & 0xff);
                    barray[10] = (byte) (digit23 & 0xff);
                    barray[7] = (byte) (digit22 >> 8 & 0xff);
                    barray[8] = (byte) (digit22 & 0xff);
                    barray[5] = (byte) (digit21 >> 8 & 0xff);
                    barray[6] = (byte) (digit21 & 0xff);
                    barray[3] = (byte) (digit20 >> 8 & 0xff);
                    barray[4] = (byte) (digit20 & 0xff);
                    barray[1] = (byte) (digit19 >> 8 & 0xff);
                    barray[2] = (byte) (digit19 & 0xff);
                    barray[0] = (byte) (digit18 & 0xff);
                } else {
                    int numbytes = 18;
                    barray = new byte[numbytes];
                    barray[16] = (byte) (digit26 >> 8 & 0xff);
                    barray[17] = (byte) (digit26 & 0xff);
                    barray[14] = (byte) (digit25 >> 8 & 0xff);
                    barray[15] = (byte) (digit25 & 0xff);
                    barray[12] = (byte) (digit24 >> 8 & 0xff);
                    barray[13] = (byte) (digit24 & 0xff);
                    barray[10] = (byte) (digit23 >> 8 & 0xff);
                    barray[11] = (byte) (digit23 & 0xff);
                    barray[8] = (byte) (digit22 >> 8 & 0xff);
                    barray[9] = (byte) (digit22 & 0xff);
                    barray[6] = (byte) (digit21 >> 8 & 0xff);
                    barray[7] = (byte) (digit21 & 0xff);
                    barray[4] = (byte) (digit20 >> 8 & 0xff);
                    barray[5] = (byte) (digit20 & 0xff);
                    barray[2] = (byte) (digit19 >> 8 & 0xff);
                    barray[3] = (byte) (digit19 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit18 & 0xff);
                }
                break;
            }

            case 19: // '\023'
            {
                byte x = (byte) (digit19 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 15;
                    barray = new byte[numbytes];
                    barray[13] = (byte) (digit26 >> 8 & 0xff);
                    barray[14] = (byte) (digit26 & 0xff);
                    barray[11] = (byte) (digit25 >> 8 & 0xff);
                    barray[12] = (byte) (digit25 & 0xff);
                    barray[9] = (byte) (digit24 >> 8 & 0xff);
                    barray[10] = (byte) (digit24 & 0xff);
                    barray[7] = (byte) (digit23 >> 8 & 0xff);
                    barray[8] = (byte) (digit23 & 0xff);
                    barray[5] = (byte) (digit22 >> 8 & 0xff);
                    barray[6] = (byte) (digit22 & 0xff);
                    barray[3] = (byte) (digit21 >> 8 & 0xff);
                    barray[4] = (byte) (digit21 & 0xff);
                    barray[1] = (byte) (digit20 >> 8 & 0xff);
                    barray[2] = (byte) (digit20 & 0xff);
                    barray[0] = (byte) (digit19 & 0xff);
                } else {
                    int numbytes = 16;
                    barray = new byte[numbytes];
                    barray[14] = (byte) (digit26 >> 8 & 0xff);
                    barray[15] = (byte) (digit26 & 0xff);
                    barray[12] = (byte) (digit25 >> 8 & 0xff);
                    barray[13] = (byte) (digit25 & 0xff);
                    barray[10] = (byte) (digit24 >> 8 & 0xff);
                    barray[11] = (byte) (digit24 & 0xff);
                    barray[8] = (byte) (digit23 >> 8 & 0xff);
                    barray[9] = (byte) (digit23 & 0xff);
                    barray[6] = (byte) (digit22 >> 8 & 0xff);
                    barray[7] = (byte) (digit22 & 0xff);
                    barray[4] = (byte) (digit21 >> 8 & 0xff);
                    barray[5] = (byte) (digit21 & 0xff);
                    barray[2] = (byte) (digit20 >> 8 & 0xff);
                    barray[3] = (byte) (digit20 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit19 & 0xff);
                }
                break;
            }

            case 20: // '\024'
            {
                byte x = (byte) (digit20 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 13;
                    barray = new byte[numbytes];
                    barray[11] = (byte) (digit26 >> 8 & 0xff);
                    barray[12] = (byte) (digit26 & 0xff);
                    barray[9] = (byte) (digit25 >> 8 & 0xff);
                    barray[10] = (byte) (digit25 & 0xff);
                    barray[7] = (byte) (digit24 >> 8 & 0xff);
                    barray[8] = (byte) (digit24 & 0xff);
                    barray[5] = (byte) (digit23 >> 8 & 0xff);
                    barray[6] = (byte) (digit23 & 0xff);
                    barray[3] = (byte) (digit22 >> 8 & 0xff);
                    barray[4] = (byte) (digit22 & 0xff);
                    barray[1] = (byte) (digit21 >> 8 & 0xff);
                    barray[2] = (byte) (digit21 & 0xff);
                    barray[0] = (byte) (digit20 & 0xff);
                } else {
                    int numbytes = 14;
                    barray = new byte[numbytes];
                    barray[12] = (byte) (digit26 >> 8 & 0xff);
                    barray[13] = (byte) (digit26 & 0xff);
                    barray[10] = (byte) (digit25 >> 8 & 0xff);
                    barray[11] = (byte) (digit25 & 0xff);
                    barray[8] = (byte) (digit24 >> 8 & 0xff);
                    barray[9] = (byte) (digit24 & 0xff);
                    barray[6] = (byte) (digit23 >> 8 & 0xff);
                    barray[7] = (byte) (digit23 & 0xff);
                    barray[4] = (byte) (digit22 >> 8 & 0xff);
                    barray[5] = (byte) (digit22 & 0xff);
                    barray[2] = (byte) (digit21 >> 8 & 0xff);
                    barray[3] = (byte) (digit21 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit20 & 0xff);
                }
                break;
            }

            case 21: // '\025'
            {
                byte x = (byte) (digit21 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 11;
                    barray = new byte[numbytes];
                    barray[9] = (byte) (digit26 >> 8 & 0xff);
                    barray[10] = (byte) (digit26 & 0xff);
                    barray[7] = (byte) (digit25 >> 8 & 0xff);
                    barray[8] = (byte) (digit25 & 0xff);
                    barray[5] = (byte) (digit24 >> 8 & 0xff);
                    barray[6] = (byte) (digit24 & 0xff);
                    barray[3] = (byte) (digit23 >> 8 & 0xff);
                    barray[4] = (byte) (digit23 & 0xff);
                    barray[1] = (byte) (digit22 >> 8 & 0xff);
                    barray[2] = (byte) (digit22 & 0xff);
                    barray[0] = (byte) (digit21 & 0xff);
                } else {
                    int numbytes = 12;
                    barray = new byte[numbytes];
                    barray[10] = (byte) (digit26 >> 8 & 0xff);
                    barray[11] = (byte) (digit26 & 0xff);
                    barray[8] = (byte) (digit25 >> 8 & 0xff);
                    barray[9] = (byte) (digit25 & 0xff);
                    barray[6] = (byte) (digit24 >> 8 & 0xff);
                    barray[7] = (byte) (digit24 & 0xff);
                    barray[4] = (byte) (digit23 >> 8 & 0xff);
                    barray[5] = (byte) (digit23 & 0xff);
                    barray[2] = (byte) (digit22 >> 8 & 0xff);
                    barray[3] = (byte) (digit22 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit21 & 0xff);
                }
                break;
            }

            case 22: // '\026'
            {
                byte x = (byte) (digit22 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 9;
                    barray = new byte[numbytes];
                    barray[7] = (byte) (digit26 >> 8 & 0xff);
                    barray[8] = (byte) (digit26 & 0xff);
                    barray[5] = (byte) (digit25 >> 8 & 0xff);
                    barray[6] = (byte) (digit25 & 0xff);
                    barray[3] = (byte) (digit24 >> 8 & 0xff);
                    barray[4] = (byte) (digit24 & 0xff);
                    barray[1] = (byte) (digit23 >> 8 & 0xff);
                    barray[2] = (byte) (digit23 & 0xff);
                    barray[0] = (byte) (digit22 & 0xff);
                } else {
                    int numbytes = 10;
                    barray = new byte[numbytes];
                    barray[8] = (byte) (digit26 >> 8 & 0xff);
                    barray[9] = (byte) (digit26 & 0xff);
                    barray[6] = (byte) (digit25 >> 8 & 0xff);
                    barray[7] = (byte) (digit25 & 0xff);
                    barray[4] = (byte) (digit24 >> 8 & 0xff);
                    barray[5] = (byte) (digit24 & 0xff);
                    barray[2] = (byte) (digit23 >> 8 & 0xff);
                    barray[3] = (byte) (digit23 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit22 & 0xff);
                }
                break;
            }

            case 23: // '\027'
            {
                byte x = (byte) (digit23 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 7;
                    barray = new byte[numbytes];
                    barray[5] = (byte) (digit26 >> 8 & 0xff);
                    barray[6] = (byte) (digit26 & 0xff);
                    barray[3] = (byte) (digit25 >> 8 & 0xff);
                    barray[4] = (byte) (digit25 & 0xff);
                    barray[1] = (byte) (digit24 >> 8 & 0xff);
                    barray[2] = (byte) (digit24 & 0xff);
                    barray[0] = (byte) (digit23 & 0xff);
                } else {
                    int numbytes = 8;
                    barray = new byte[numbytes];
                    barray[6] = (byte) (digit26 >> 8 & 0xff);
                    barray[7] = (byte) (digit26 & 0xff);
                    barray[4] = (byte) (digit25 >> 8 & 0xff);
                    barray[5] = (byte) (digit25 & 0xff);
                    barray[2] = (byte) (digit24 >> 8 & 0xff);
                    barray[3] = (byte) (digit24 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit23 & 0xff);
                }
                break;
            }

            case 24: // '\030'
            {
                byte x = (byte) (digit24 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 5;
                    barray = new byte[numbytes];
                    barray[3] = (byte) (digit26 >> 8 & 0xff);
                    barray[4] = (byte) (digit26 & 0xff);
                    barray[1] = (byte) (digit25 >> 8 & 0xff);
                    barray[2] = (byte) (digit25 & 0xff);
                    barray[0] = (byte) (digit24 & 0xff);
                } else {
                    int numbytes = 6;
                    barray = new byte[numbytes];
                    barray[4] = (byte) (digit26 >> 8 & 0xff);
                    barray[5] = (byte) (digit26 & 0xff);
                    barray[2] = (byte) (digit25 >> 8 & 0xff);
                    barray[3] = (byte) (digit25 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit24 & 0xff);
                }
                break;
            }

            case 25: // '\031'
            {
                byte x = (byte) (digit25 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 3;
                    barray = new byte[numbytes];
                    barray[1] = (byte) (digit26 >> 8 & 0xff);
                    barray[2] = (byte) (digit26 & 0xff);
                    barray[0] = (byte) (digit25 & 0xff);
                } else {
                    int numbytes = 4;
                    barray = new byte[numbytes];
                    barray[2] = (byte) (digit26 >> 8 & 0xff);
                    barray[3] = (byte) (digit26 & 0xff);
                    barray[0] = x;
                    barray[1] = (byte) (digit25 & 0xff);
                }
                break;
            }

            case 26: // '\032'
            {
                byte x = (byte) (digit26 >> 8 & 0xff);
                if (x == 0) {
                    int numbytes = 1;
                    barray = new byte[numbytes];
                    barray[0] = (byte) (digit26 & 0xff);
                } else {
                    int numbytes = 2;
                    barray = new byte[numbytes];
                    barray[0] = x;
                    barray[1] = (byte) (digit26 & 0xff);
                }
                break;
            }
            }
            BigInteger bigtemp = new BigInteger(signval, barray);
            result = new BigDecimal(bigtemp, -scale);
        }
        return result;
    }

    BigDecimal getBigDecimaln(int currentRow) throws SQLException {
        BigDecimal result = null;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            byte bytes[] = rowSpaceByte;
            int offset = columnIndex + byteLength * currentRow + 1;
            int len = bytes[offset - 1];
            for (int i = 0; i < 27; i++) {
                digs[i] = 0;
            }

            int dig26 = 0;
            byte oidx = 1;
            byte cnt = 26;
            int value = 0;
            byte expbyte = bytes[offset];
            boolean trailingZeroP = false;
            int clen;
            int mantlen;
            int signval;
            int scale;
            if ((expbyte & 0xffffff80) != 0) {
                if (expbyte == -128 && len == 1) {
                    return BIGDEC_ZERO;
                }
                if (len == 2 && expbyte == -1 && bytes[offset + 1] == 101) {
                    throwOverflow();
                }
                signval = 1;
                int exponent = (byte) ((expbyte & 0xffffff7f) - 65);
                mantlen = len - 1;
                clen = mantlen - 1;
                scale = (exponent - mantlen) + 1 << 1;
                if (scale > 0) {
                    scale = 0;
                    clen = exponent;
                } else if (scale < 0) {
                    trailingZeroP = (bytes[offset + mantlen] - 1) % 10 == 0;
                }
                dig26 = bytes[offset + oidx++] - 1;
                for (; (clen & 1) != 0; clen--) {
                    if (oidx > mantlen) {
                        dig26 *= 100;
                    } else {
                        dig26 = dig26 * 100 + (bytes[offset + oidx++] - 1);
                    }
                }

            } else {
                if (expbyte == 0 && len == 1) {
                    throwOverflow();
                }
                signval = -1;
                int exponent = (byte) ((~expbyte & 0xffffff7f) - 65);
                mantlen = len - 1;
                if (mantlen != 20 || bytes[offset + mantlen] == 102) {
                    mantlen--;
                }
                clen = mantlen - 1;
                scale = (exponent - mantlen) + 1 << 1;
                if (scale > 0) {
                    scale = 0;
                    clen = exponent;
                } else if (scale < 0) {
                    trailingZeroP = (101 - bytes[offset + mantlen]) % 10 == 0;
                }
                dig26 = 101 - bytes[offset + oidx++];
                for (; (clen & 1) != 0; clen--) {
                    if (oidx > mantlen) {
                        dig26 *= 100;
                    } else {
                        dig26 = dig26 * 100 + (101 - bytes[offset + oidx++]);
                    }
                }

            }
            if (trailingZeroP) {
                scale++;
                dig26 /= 10;
            }
            int lim = mantlen - 1;
            for (; clen != 0; clen -= 2) {
                if (signval == 1) {
                    if (trailingZeroP) {
                        value = ((bytes[(offset + oidx) - 1] - 1) % 10) * 1000
                                + (bytes[offset + oidx] - 1) * 10 + (bytes[offset + oidx + 1] - 1)
                                / 10 + dig26 * 10000;
                        oidx += 2;
                    } else if (oidx < lim) {
                        value = (bytes[offset + oidx] - 1) * 100 + (bytes[offset + oidx + 1] - 1)
                                + dig26 * 10000;
                        oidx += 2;
                    } else {
                        value = 0;
                        if (oidx <= mantlen) {
                            int i;
                            for (i = 0; oidx <= mantlen; i++) {
                                value = value * 100 + (bytes[offset + oidx++] - 1);
                            }

                            for (; i < 2; i++) {
                                value *= 100;
                            }

                        }
                        value += dig26 * 10000;
                    }
                } else if (trailingZeroP) {
                    value = ((101 - bytes[(offset + oidx) - 1]) % 10) * 1000
                            + (101 - bytes[offset + oidx]) * 10 + (101 - bytes[offset + oidx + 1])
                            / 10 + dig26 * 10000;
                    oidx += 2;
                } else if (oidx < lim) {
                    value = (101 - bytes[offset + oidx]) * 100 + (101 - bytes[offset + oidx + 1])
                            + dig26 * 10000;
                    oidx += 2;
                } else {
                    value = 0;
                    if (oidx <= mantlen) {
                        int i;
                        for (i = 0; oidx <= mantlen; i++) {
                            value = value * 100 + (101 - bytes[offset + oidx++]);
                        }

                        for (; i < 2; i++) {
                            value *= 100;
                        }

                    }
                    value += dig26 * 10000;
                }
                dig26 = value & 0xffff;
                for (int i = 25; i >= cnt; i--) {
                    value = (value >> 16) + digs[i] * 10000;
                    digs[i] = value & 0xffff;
                }

                if (value != 0) {
                    digs[cnt--] = value;
                }
            }

            digs[26] = dig26;
            byte x = (byte) (digs[cnt] >> 8 & 0xff);
            byte barray[];
            if (x == 0) {
                int numbytes = 53 - (cnt << 1);
                barray = new byte[numbytes];
                for (int i = 26; i > cnt; i--) {
                    int j = i - cnt << 1;
                    barray[j - 1] = (byte) (digs[i] >> 8 & 0xff);
                    barray[j] = (byte) (digs[i] & 0xff);
                }

                barray[0] = (byte) (digs[cnt] & 0xff);
            } else {
                int numbytes = 54 - (cnt << 1);
                barray = new byte[numbytes];
                for (int i = 26; i > cnt; i--) {
                    int j = i - cnt << 1;
                    barray[j] = (byte) (digs[i] >> 8 & 0xff);
                    barray[j + 1] = (byte) (digs[i] & 0xff);
                }

                barray[0] = x;
                barray[1] = (byte) (digs[cnt] & 0xff);
            }
            BigInteger bigtemp = new BigInteger(signval, barray);
            result = new BigDecimal(bigtemp, -scale);
        }
        return result;
    }

    BigDecimal getBigDecimal(int currentRow, int scale) throws SQLException {
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] == -1) {
            return null;
        } else {
            return getBigDecimal(currentRow).setScale(scale, 6);
        }
    }

    String getString(int currentRow) throws SQLException {
        String result = null;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            byte bytes[] = rowSpaceByte;
            int offset = columnIndex + byteLength * currentRow + 1;
            int len = bytes[offset - 1];
            byte array[] = new byte[len];
            System.arraycopy(bytes, offset, array, 0, len);
            NUMBER oranum = new NUMBER(array);
            String s = oracle.sql.NUMBER.toString(array);
            int numlength = s.length();
            if (s.startsWith("0.") || s.startsWith("-0.")) {
                numlength--;
            }
            if (numlength > 38) {
                s = oranum.toText(-44, null);
                int idxe = s.indexOf('E');
                int idxpls = s.indexOf('+');
                if (idxe == -1) {
                    idxe = s.indexOf('e');
                }
                int idx;
                for (idx = idxe - 1; s.charAt(idx) == '0'; idx--) {
                }
                String left = s.substring(0, idx + 1);
                String right = null;
                if (idxpls > 0) {
                    right = s.substring(idxpls + 1);
                } else {
                    right = s.substring(idxe + 1);
                }
                return (left + "E" + right).trim();
            } else {
                return oranum.toText(38, null).trim();
            }
        } else {
            return result;
        }
    }

    NUMBER getNUMBER(int currentRow) throws SQLException {
        NUMBER result = null;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            int off = columnIndex + byteLength * currentRow + 1;
            int len = rowSpaceByte[off - 1];
            byte data[] = new byte[len];
            System.arraycopy(rowSpaceByte, off, data, 0, len);
            result = new NUMBER(data);
        }
        return result;
    }

    Object getObject(int currentRow) throws SQLException {
        Object result = null;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            if (externalType == 0) {
                if (statement.connection.j2ee13Compliant && precision != 0 && scale == -127) {
                    result = new Double(getDouble(currentRow));
                } else {
                    result = getBigDecimal(currentRow);
                }
            } else {
                switch (externalType) {
                case -7:
                    return new Boolean(getBoolean(currentRow));

                case -6:
                    return new Byte(getByte(currentRow));

                case 5: // '\005'
                    return new Short(getShort(currentRow));

                case 4: // '\004'
                    return new Integer(getInt(currentRow));

                case -5:
                    return new Long(getLong(currentRow));

                case 6: // '\006'
                case 8: // '\b'
                    return new Double(getDouble(currentRow));

                case 7: // '\007'
                    return new Float(getFloat(currentRow));

                case 2: // '\002'
                case 3: // '\003'
                    return getBigDecimal(currentRow);

                case -4:
                case -3:
                case -2:
                case -1:
                case 0: // '\0'
                case 1: // '\001'
                default:
                    DatabaseError.throwSqlException(4);
                    break;
                }
                return null;
            }
        }
        return result;
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        return getObject(currentRow);
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getNUMBER(currentRow);
    }

    byte[] getBytes(int currentRow) throws SQLException {
        byte result[] = null;
        if (rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (rowSpaceIndicator[indicatorIndex + currentRow] != -1) {
            int off = columnIndex + byteLength * currentRow + 1;
            int len = rowSpaceByte[off - 1];
            result = new byte[len];
            System.arraycopy(rowSpaceByte, off, result, 0, len);
        }
        return result;
    }

    void throwOverflow() throws SQLException {
        DatabaseError.throwSqlException(26);
    }

    static {
        tablemax = factorTable.length;
        tableminexponent = 127D - (double) (tablemax - 20);
    }
}
