package oracle.jdbc.driver;

class OracleSqlReadOnly {
    private static final int BASE = 0;
    private static final int BASE_1 = 1;
    private static final int BASE_2 = 2;
    private static final int B_STRING = 3;
    private static final int B_NAME = 4;
    private static final int B_C_COMMENT = 5;
    private static final int B_C_COMMENT_1 = 6;
    private static final int B_COMMENT = 7;
    private static final int PARAMETER = 8;
    private static final int TOKEN = 9;
    private static final int B_EGIN = 10;
    private static final int BE_GIN = 11;
    private static final int BEG_IN = 12;
    private static final int BEGI_N = 13;
    private static final int BEGIN_ = 14;
    private static final int C_ALL = 15;
    private static final int CA_LL = 16;
    private static final int CAL_L = 17;
    private static final int CALL_ = 18;
    private static final int D_Eetc = 19;
    private static final int DE_etc = 20;
    private static final int DEC_LARE = 21;
    private static final int DECL_ARE = 22;
    private static final int DECLA_RE = 23;
    private static final int DECLAR_E = 24;
    private static final int DECLARE_ = 25;
    private static final int DEL_ETE = 26;
    private static final int DELE_TE = 27;
    private static final int DELET_E = 28;
    private static final int DELETE_ = 29;
    private static final int I_NSERT = 30;
    private static final int IN_SERT = 31;
    private static final int INS_ERT = 32;
    private static final int INSE_RT = 33;
    private static final int INSER_T = 34;
    private static final int INSERT_ = 35;
    private static final int S_ELECT = 36;
    private static final int SE_LECT = 37;
    private static final int SEL_ECT = 38;
    private static final int SELE_CT = 39;
    private static final int SELEC_T = 40;
    private static final int SELECT_ = 41;
    private static final int U_PDATE = 42;
    private static final int UP_DATE = 43;
    private static final int UPD_ATE = 44;
    private static final int UPDA_TE = 45;
    private static final int UPDAT_E = 46;
    private static final int UPDATE_ = 47;
    private static final int M_ERGE = 48;
    private static final int ME_RGE = 49;
    private static final int MER_GE = 50;
    private static final int MERG_E = 51;
    private static final int MERGE_ = 52;
    private static final int W_ITH = 53;
    private static final int WI_TH = 54;
    private static final int WIT_H = 55;
    private static final int WITH_ = 56;
    private static final int KNOW_KIND = 57;
    private static final int KNOW_KIND_1 = 58;
    private static final int KNOW_KIND_2 = 59;
    private static final int K_STRING = 60;
    private static final int K_NAME = 61;
    private static final int K_C_COMMENT = 62;
    private static final int K_C_COMMENT_1 = 63;
    private static final int K_COMMENT = 64;
    private static final int K_PARAMETER = 65;
    private static final int TOKEN_KK = 66;
    private static final int W_HERE = 67;
    private static final int WH_ERE = 68;
    private static final int WHE_RE = 69;
    private static final int WHER_E = 70;
    private static final int WHERE_ = 71;
    private static final int O_RDER_BY = 72;
    private static final int OR_DER_BY = 73;
    private static final int ORD_ER_BY = 74;
    private static final int ORDE_R_BY = 75;
    private static final int ORDER__BY = 76;
    private static final int ORDER_xBY = 77;
    private static final int ORDER_B_Y = 78;
    private static final int ORDER_BY_ = 79;
    private static final int ORDER_xBY_CC_1 = 80;
    private static final int ORDER_xBY_CC_2 = 81;
    private static final int ORDER_xBY_CC_3 = 82;
    private static final int ORDER_xBY_C_1 = 83;
    private static final int ORDER_xBY_C_2 = 84;
    private static final int F_OR_UPDATE = 84;
    private static final int FO_R_UPDATE = 85;
    private static final int FOR__UPDATE = 86;
    private static final int FOR_xUPDATE = 87;
    private static final int FOR_U_PDATE = 88;
    private static final int FOR_UP_DATE = 89;
    private static final int FOR_UPD_ATE = 90;
    private static final int FOR_UPDA_TE = 91;
    private static final int FOR_UPDAT_E = 92;
    private static final int FOR_UPDATE_ = 93;
    private static final int FOR_xUPDATE_CC_1 = 94;
    private static final int FOR_xUPDATE_CC_2 = 95;
    private static final int FOR_xUPDATE_CC_3 = 96;
    private static final int FOR_xUPDATE_C_1 = 97;
    private static final int FOR_xUPDATE_C_2 = 98;
    private static final int B_N_tick = 99;
    private static final int B_NCHAR = 100;
    private static final int K_N_tick = 101;
    private static final int K_NCHAR = 102;
    private static final int LAST_STATE = 103;
    static final int[][] TRANSITION = new int[103][];
    static final int NO_ACTION = 0;
    static final int DML_ACTION = 1;
    static final int PLSQL_ACTION = 2;
    static final int CALL_ACTION = 3;
    static final int SELECT_ACTION = 4;
    static final int OTHER_ACTION = 5;
    static final int WHERE_ACTION = 6;
    static final int ORDER_ACTION = 7;
    static final int ORDER_BY_ACTION = 8;
    static final int FOR_ACTION = 9;
    static final int FOR_UPDATE_ACTION = 10;
    static final int QUESTION_ACTION = 11;
    static final int PARAMETER_ACTION = 12;
    static final int END_PARAMETER_ACTION = 13;
    static final int START_NCHAR_LITERAL_ACTION = 14;
    static final int END_NCHAR_LITERAL_ACTION = 15;
    static final int[][] ACTION = new int[103][];
    static final int cMax = 127;
    private static final int cMaxLength = 128;

    private static final int[] copy(int[] a) {
        int[] r = new int[a.length];

        System.arraycopy(a, 0, r, 0, a.length);

        return r;
    }

    private static final int[] newArray(int length, int value) {
        int[] r = new int[length];

        for (int i = 0; i < length; i++) {
            r[i] = value;
        }
        return r;
    }

    private static final int[] copyReplacing(int[] a, int source, int target) {
        int[] r = new int[a.length];

        for (int i = 0; i < r.length; i++) {
            int t = a[i];

            if (t == source)
                r[i] = target;
            else {
                r[i] = t;
            }
        }
        return r;
    }

    static {
        int[] token = { 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57,
                57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 4, 9, 9, 57, 57, 3, 57,
                57, 57, 57, 57, 2, 57, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 8, 57, 57, 57, 57, 57, 9,
                9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 57,
                57, 57, 57, 9, 57, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
                9, 9, 9, 9, 9, 57, 9, 9, 9, 9 };

        int[] base = copyReplacing(token, 57, 0);

        base[66] = 10;
        base[98] = 10;
        base[67] = 15;
        base[99] = 15;
        base[68] = 19;
        base[100] = 19;
        base[73] = 30;
        base[105] = 30;
        base[78] = 99;
        base[110] = 99;
        base[83] = 36;
        base[115] = 36;
        base[85] = 42;
        base[117] = 42;
        base[109] = 48;
        base[77] = 48;
        base[87] = 53;
        base[119] = 53;

        int[] found = copy(token);

        found[34] = 61;
        found[39] = 60;
        found[45] = 59;
        found[47] = 58;
        found[58] = 65;
        found[32] = 66;

        int[] known = copyReplacing(found, 9, 57);
        known[32] = 66;
        known[9] = 66;
        known[10] = 66;
        known[13] = 66;
        known[61] = 66;

        TRANSITION[0] = base;
        TRANSITION[1] = copy(base);
        TRANSITION[1][42] = 5;
        TRANSITION[2] = copy(base);
        TRANSITION[2][45] = 7;
        TRANSITION[3] = newArray(128, 3);
        TRANSITION[3][39] = 0;
        TRANSITION[99] = copy(token);
        TRANSITION[99][39] = 100;
        TRANSITION[100] = newArray(128, 100);
        TRANSITION[100][39] = 0;
        TRANSITION[4] = newArray(128, 4);
        TRANSITION[4][34] = 0;
        TRANSITION[5] = newArray(128, 5);
        TRANSITION[5][42] = 6;
        TRANSITION[6] = newArray(128, 5);
        TRANSITION[6][42] = 6;
        TRANSITION[6][47] = 0;
        TRANSITION[7] = newArray(128, 7);
        TRANSITION[7][10] = 0;
        TRANSITION[8] = copyReplacing(token, 9, 8);
        TRANSITION[9] = token;
        TRANSITION[10] = copy(token);
        TRANSITION[10][69] = 11;
        TRANSITION[10][101] = 11;
        TRANSITION[11] = copy(token);
        TRANSITION[11][71] = 12;
        TRANSITION[11][103] = 12;
        TRANSITION[12] = copy(token);
        TRANSITION[12][73] = 13;
        TRANSITION[12][105] = 13;
        TRANSITION[13] = copy(token);
        TRANSITION[13][78] = 14;
        TRANSITION[13][110] = 14;
        TRANSITION[14] = found;
        TRANSITION[15] = copy(token);
        TRANSITION[15][65] = 16;
        TRANSITION[15][97] = 16;
        TRANSITION[16] = copy(token);
        TRANSITION[16][76] = 17;
        TRANSITION[16][108] = 17;
        TRANSITION[17] = copy(token);
        TRANSITION[17][76] = 18;
        TRANSITION[17][108] = 18;
        TRANSITION[18] = found;
        TRANSITION[19] = copy(token);
        TRANSITION[19][69] = 20;
        TRANSITION[19][101] = 20;
        TRANSITION[20] = copy(token);
        TRANSITION[20][67] = 21;
        TRANSITION[20][99] = 21;
        TRANSITION[20][76] = 26;
        TRANSITION[20][108] = 26;
        TRANSITION[21] = copy(token);
        TRANSITION[21][76] = 22;
        TRANSITION[21][108] = 22;
        TRANSITION[22] = copy(token);
        TRANSITION[22][65] = 23;
        TRANSITION[22][97] = 23;
        TRANSITION[23] = copy(token);
        TRANSITION[23][82] = 24;
        TRANSITION[23][114] = 24;
        TRANSITION[24] = copy(token);
        TRANSITION[24][69] = 25;
        TRANSITION[24][101] = 25;
        TRANSITION[25] = found;
        TRANSITION[26] = copy(token);
        TRANSITION[26][69] = 27;
        TRANSITION[26][101] = 27;
        TRANSITION[27] = copy(token);
        TRANSITION[27][84] = 28;
        TRANSITION[27][116] = 28;
        TRANSITION[28] = copy(token);
        TRANSITION[28][69] = 29;
        TRANSITION[28][101] = 29;
        TRANSITION[29] = found;
        TRANSITION[30] = copy(token);
        TRANSITION[30][78] = 31;
        TRANSITION[30][110] = 31;
        TRANSITION[31] = copy(token);
        TRANSITION[31][83] = 32;
        TRANSITION[31][115] = 32;
        TRANSITION[32] = copy(token);
        TRANSITION[32][69] = 33;
        TRANSITION[32][101] = 33;
        TRANSITION[33] = copy(token);
        TRANSITION[33][82] = 34;
        TRANSITION[33][114] = 34;
        TRANSITION[34] = copy(token);
        TRANSITION[34][84] = 35;
        TRANSITION[34][116] = 35;
        TRANSITION[35] = found;
        TRANSITION[36] = copy(token);
        TRANSITION[36][69] = 37;
        TRANSITION[36][101] = 37;
        TRANSITION[37] = copy(token);
        TRANSITION[37][76] = 38;
        TRANSITION[37][108] = 38;
        TRANSITION[38] = copy(token);
        TRANSITION[38][69] = 39;
        TRANSITION[38][101] = 39;
        TRANSITION[39] = copy(token);
        TRANSITION[39][67] = 40;
        TRANSITION[39][99] = 40;
        TRANSITION[40] = copy(token);
        TRANSITION[40][84] = 41;
        TRANSITION[40][116] = 41;
        TRANSITION[41] = found;
        TRANSITION[42] = copy(token);
        TRANSITION[42][80] = 43;
        TRANSITION[42][112] = 43;
        TRANSITION[43] = copy(token);
        TRANSITION[43][68] = 44;
        TRANSITION[43][100] = 44;
        TRANSITION[44] = copy(token);
        TRANSITION[44][65] = 45;
        TRANSITION[44][97] = 45;
        TRANSITION[45] = copy(token);
        TRANSITION[45][84] = 46;
        TRANSITION[45][116] = 46;
        TRANSITION[46] = copy(token);
        TRANSITION[46][69] = 47;
        TRANSITION[46][101] = 47;
        TRANSITION[47] = found;
        TRANSITION[48] = copy(token);
        TRANSITION[48][69] = 49;
        TRANSITION[48][101] = 49;
        TRANSITION[49] = copy(token);
        TRANSITION[49][82] = 50;
        TRANSITION[49][114] = 50;
        TRANSITION[50] = copy(token);
        TRANSITION[50][71] = 51;
        TRANSITION[50][103] = 51;
        TRANSITION[51] = copy(token);
        TRANSITION[51][69] = 52;
        TRANSITION[51][101] = 52;
        TRANSITION[52] = found;
        TRANSITION[53] = copy(token);
        TRANSITION[53][73] = 54;
        TRANSITION[53][105] = 54;
        TRANSITION[54] = copy(token);
        TRANSITION[54][84] = 55;
        TRANSITION[54][116] = 55;
        TRANSITION[55] = copy(token);
        TRANSITION[55][72] = 56;
        TRANSITION[55][104] = 56;
        TRANSITION[56] = found;
        TRANSITION[57] = known;
        TRANSITION[58] = copy(known);
        TRANSITION[58][42] = 62;
        TRANSITION[59] = copy(known);
        TRANSITION[59][45] = 64;
        TRANSITION[62] = newArray(128, 62);
        TRANSITION[62][42] = 63;
        TRANSITION[63] = newArray(128, 62);
        TRANSITION[63][42] = 63;
        TRANSITION[63][47] = 66;
        TRANSITION[64] = newArray(128, 64);
        TRANSITION[64][10] = 66;
        TRANSITION[61] = newArray(128, 61);
        TRANSITION[61][34] = 57;
        TRANSITION[60] = newArray(128, 60);
        TRANSITION[60][39] = 57;
        TRANSITION[65] = copyReplacing(found, 9, 65);

        int[] token_kk = copy(known);
        TRANSITION[66] = token_kk;

        TRANSITION[66][32] = 66;
        TRANSITION[66][9] = 66;
        TRANSITION[66][10] = 66;
        TRANSITION[66][13] = 66;
        TRANSITION[66][61] = 66;

        TRANSITION[66][87] = 67;
        TRANSITION[66][119] = 67;
        TRANSITION[67] = copy(known);
        TRANSITION[67][72] = 68;
        TRANSITION[67][104] = 68;
        TRANSITION[68] = copy(known);
        TRANSITION[68][69] = 69;
        TRANSITION[68][101] = 69;
        TRANSITION[69] = copy(known);
        TRANSITION[69][82] = 70;
        TRANSITION[69][114] = 70;
        TRANSITION[70] = copy(known);
        TRANSITION[70][69] = 71;
        TRANSITION[70][101] = 71;
        TRANSITION[71] = known;

        TRANSITION[66][79] = 72;
        TRANSITION[66][111] = 72;
        TRANSITION[72] = copy(known);
        TRANSITION[72][82] = 73;
        TRANSITION[72][114] = 73;
        TRANSITION[73] = copy(known);
        TRANSITION[73][68] = 74;
        TRANSITION[73][100] = 74;
        TRANSITION[74] = copy(known);
        TRANSITION[74][69] = 75;
        TRANSITION[74][101] = 75;
        TRANSITION[75] = copy(known);
        TRANSITION[75][82] = 76;
        TRANSITION[75][114] = 76;
        TRANSITION[76] = copyReplacing(token_kk, 66, 77);
        TRANSITION[76][47] = 80;
        TRANSITION[76][45] = 83;

        TRANSITION[77] = copyReplacing(token_kk, 66, 77);
        TRANSITION[77][47] = 80;
        TRANSITION[80] = copy(known);
        TRANSITION[80][42] = 81;
        TRANSITION[81] = newArray(128, 81);
        TRANSITION[81][42] = 82;
        TRANSITION[82] = newArray(128, 81);
        TRANSITION[82][47] = 77;

        TRANSITION[77][45] = 83;
        TRANSITION[83] = copy(known);
        TRANSITION[83][45] = 84;
        TRANSITION[84] = newArray(128, 84);
        TRANSITION[84][10] = 77;

        TRANSITION[77][66] = 78;
        TRANSITION[77][98] = 78;
        TRANSITION[78] = copy(known);
        TRANSITION[78][89] = 79;
        TRANSITION[78][121] = 79;
        TRANSITION[79] = known;

        TRANSITION[66][70] = 84;
        TRANSITION[66][102] = 84;
        TRANSITION[84] = copy(known);
        TRANSITION[84][79] = 85;
        TRANSITION[84][111] = 85;
        TRANSITION[85] = copy(known);
        TRANSITION[85][82] = 86;
        TRANSITION[85][114] = 86;
        TRANSITION[86] = copyReplacing(token_kk, 66, 87);
        TRANSITION[86][47] = 94;
        TRANSITION[86][45] = 97;

        TRANSITION[87] = copyReplacing(token_kk, 66, 87);
        TRANSITION[87][47] = 94;
        TRANSITION[94] = copy(known);
        TRANSITION[94][42] = 95;
        TRANSITION[95] = newArray(128, 95);
        TRANSITION[95][42] = 96;
        TRANSITION[96] = newArray(128, 95);
        TRANSITION[96][47] = 87;

        TRANSITION[87][45] = 97;
        TRANSITION[97] = copy(known);
        TRANSITION[97][45] = 98;
        TRANSITION[98] = newArray(128, 98);
        TRANSITION[98][10] = 87;

        TRANSITION[87][85] = 88;
        TRANSITION[87][117] = 88;
        TRANSITION[88] = copy(known);
        TRANSITION[88][80] = 89;
        TRANSITION[88][112] = 89;
        TRANSITION[89] = copy(known);
        TRANSITION[89][68] = 90;
        TRANSITION[89][100] = 90;
        TRANSITION[90] = copy(known);
        TRANSITION[90][65] = 91;
        TRANSITION[90][97] = 91;
        TRANSITION[91] = copy(known);
        TRANSITION[91][84] = 92;
        TRANSITION[91][116] = 92;
        TRANSITION[92] = copy(known);
        TRANSITION[92][69] = 93;
        TRANSITION[92][101] = 93;
        TRANSITION[93] = known;

        TRANSITION[66][78] = 101;
        TRANSITION[66][110] = 101;
        TRANSITION[101] = copy(known);
        TRANSITION[101][39] = 102;
        TRANSITION[102] = newArray(128, 102);
        TRANSITION[102][39] = 66;

        int[] none = newArray(128, 0);

        int[] baseAction = copy(none);

        baseAction[63] = 11;

        int[] parameterAction = new int[''];

        for (int i = 0; i < parameterAction.length; i++) {
            if (TRANSITION[8][i] == 8)
                parameterAction[i] = 12;
            else
                parameterAction[i] = 13;
        }
        int[] kParameterAction = new int[''];

        for (int i = 0; i < kParameterAction.length; i++) {
            if (TRANSITION[65][i] == 65)
                kParameterAction[i] = 12;
            else
                kParameterAction[i] = 13;
        }
        int[] plsql = copy(none);

        for (int i = 0; i < plsql.length; i++) {
            if (found[i] != 9)
                plsql[i] = 2;
        }
        int[] call = copyReplacing(plsql, 2, 3);
        int[] dml = copyReplacing(plsql, 2, 1);
        int[] select = copyReplacing(plsql, 2, 4);
        int[] other = copyReplacing(plsql, 2, 5);

        int[] order_found = copyReplacing(plsql, 2, 7);

        for (int i = 0; i < order_found.length; i++) {
            if (order_found[i] == 5) {
                order_found[i] = 0;
            }
        }
        int[] order_by = copyReplacing(order_found, 7, 8);
        int[] where = copyReplacing(order_found, 7, 6);
        int[] for_found = copyReplacing(order_found, 7, 9);
        int[] for_update = copyReplacing(order_found, 7, 10);

        int[] startNchar = copy(none);
        startNchar[39] = 14;

        int[] endNchar = copy(none);
        endNchar[39] = 15;

        ACTION[0] = baseAction;
        ACTION[1] = baseAction;
        ACTION[2] = baseAction;
        ACTION[3] = none;
        ACTION[4] = none;
        ACTION[5] = none;
        ACTION[6] = none;
        ACTION[7] = none;
        ACTION[8] = parameterAction;
        ACTION[99] = startNchar;
        ACTION[100] = endNchar;
        ACTION[9] = other;
        ACTION[10] = other;
        ACTION[11] = other;
        ACTION[12] = other;
        ACTION[13] = other;
        ACTION[14] = plsql;
        ACTION[15] = other;
        ACTION[16] = other;
        ACTION[17] = other;
        ACTION[18] = call;
        ACTION[19] = other;
        ACTION[20] = other;
        ACTION[21] = other;
        ACTION[22] = other;
        ACTION[23] = other;
        ACTION[24] = other;
        ACTION[25] = plsql;
        ACTION[26] = other;
        ACTION[27] = other;
        ACTION[28] = other;
        ACTION[29] = dml;
        ACTION[30] = other;
        ACTION[31] = other;
        ACTION[32] = other;
        ACTION[33] = other;
        ACTION[34] = other;
        ACTION[35] = dml;
        ACTION[36] = other;
        ACTION[37] = other;
        ACTION[38] = other;
        ACTION[39] = other;
        ACTION[40] = other;
        ACTION[41] = select;
        ACTION[42] = other;
        ACTION[43] = other;
        ACTION[44] = other;
        ACTION[45] = other;
        ACTION[46] = other;
        ACTION[47] = dml;
        ACTION[48] = other;
        ACTION[49] = other;
        ACTION[50] = other;
        ACTION[51] = other;
        ACTION[52] = dml;
        ACTION[53] = other;
        ACTION[54] = other;
        ACTION[55] = other;
        ACTION[56] = select;
        ACTION[57] = baseAction;
        ACTION[58] = baseAction;
        ACTION[59] = baseAction;
        ACTION[60] = none;
        ACTION[61] = none;
        ACTION[62] = none;
        ACTION[63] = none;
        ACTION[64] = none;
        ACTION[65] = kParameterAction;
        ACTION[101] = startNchar;
        ACTION[102] = endNchar;

        ACTION[66] = baseAction;

        ACTION[67] = none;
        ACTION[68] = none;
        ACTION[69] = none;
        ACTION[70] = none;
        ACTION[71] = where;

        ACTION[72] = none;
        ACTION[73] = none;
        ACTION[74] = none;
        ACTION[75] = none;
        ACTION[76] = order_found;

        ACTION[77] = none;
        ACTION[78] = none;
        ACTION[79] = order_by;

        ACTION[80] = none;
        ACTION[81] = none;
        ACTION[82] = none;
        ACTION[83] = none;
        ACTION[84] = none;

        ACTION[84] = none;
        ACTION[85] = none;
        ACTION[86] = for_found;

        ACTION[87] = baseAction;
        ACTION[88] = none;
        ACTION[89] = none;
        ACTION[90] = none;
        ACTION[91] = none;
        ACTION[92] = none;
        ACTION[93] = for_update;

        ACTION[94] = none;
        ACTION[95] = none;
        ACTION[96] = none;
        ACTION[97] = none;
        ACTION[98] = none;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleSqlReadOnly JD-Core Version: 0.6.0
 */