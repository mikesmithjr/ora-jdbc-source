package oracle.jdbc.driver;

class OracleSqlReadOnly
{
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

  private static final int[] copy(int[] paramArrayOfInt)
  {
    int[] arrayOfInt = new int[paramArrayOfInt.length];

    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramArrayOfInt.length);

    return arrayOfInt;
  }

  private static final int[] newArray(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = new int[paramInt1];

    for (int i = 0; i < paramInt1; i++) {
      arrayOfInt[i] = paramInt2;
    }
    return arrayOfInt;
  }

  private static final int[] copyReplacing(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = new int[paramArrayOfInt.length];

    for (int i = 0; i < arrayOfInt.length; i++)
    {
      int j = paramArrayOfInt[i];

      if (j == paramInt1)
        arrayOfInt[i] = paramInt2;
      else {
        arrayOfInt[i] = j;
      }
    }
    return arrayOfInt;
  }

  static
  {
    int[] arrayOfInt1 = { 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 4, 9, 9, 57, 57, 3, 57, 57, 57, 57, 57, 2, 57, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 8, 57, 57, 57, 57, 57, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 57, 57, 57, 57, 9, 57, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 57, 9, 9, 9, 9 };

    int[] arrayOfInt2 = copyReplacing(arrayOfInt1, 57, 0);

    arrayOfInt2[66] = 10;
    arrayOfInt2[98] = 10;
    arrayOfInt2[67] = 15;
    arrayOfInt2[99] = 15;
    arrayOfInt2[68] = 19;
    arrayOfInt2[100] = 19;
    arrayOfInt2[73] = 30;
    arrayOfInt2[105] = 30;
    arrayOfInt2[78] = 99;
    arrayOfInt2[110] = 99;
    arrayOfInt2[83] = 36;
    arrayOfInt2[115] = 36;
    arrayOfInt2[85] = 42;
    arrayOfInt2[117] = 42;
    arrayOfInt2[109] = 48;
    arrayOfInt2[77] = 48;
    arrayOfInt2[87] = 53;
    arrayOfInt2[119] = 53;

    int[] arrayOfInt3 = copy(arrayOfInt1);

    arrayOfInt3[34] = 61;
    arrayOfInt3[39] = 60;
    arrayOfInt3[45] = 59;
    arrayOfInt3[47] = 58;
    arrayOfInt3[58] = 65;
    arrayOfInt3[32] = 66;

    int[] arrayOfInt4 = copyReplacing(arrayOfInt3, 9, 57);
    arrayOfInt4[32] = 66;
    arrayOfInt4[9] = 66;
    arrayOfInt4[10] = 66;
    arrayOfInt4[13] = 66;
    arrayOfInt4[61] = 66;

    TRANSITION[0] = arrayOfInt2;
    TRANSITION[1] = copy(arrayOfInt2);
    TRANSITION[1][42] = 5;
    TRANSITION[2] = copy(arrayOfInt2);
    TRANSITION[2][45] = 7;
    TRANSITION[3] = newArray(128, 3);
    TRANSITION[3][39] = 0;
    TRANSITION[99] = copy(arrayOfInt1);
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
    TRANSITION[8] = copyReplacing(arrayOfInt1, 9, 8);
    TRANSITION[9] = arrayOfInt1;
    TRANSITION[10] = copy(arrayOfInt1);
    TRANSITION[10][69] = 11;
    TRANSITION[10][101] = 11;
    TRANSITION[11] = copy(arrayOfInt1);
    TRANSITION[11][71] = 12;
    TRANSITION[11][103] = 12;
    TRANSITION[12] = copy(arrayOfInt1);
    TRANSITION[12][73] = 13;
    TRANSITION[12][105] = 13;
    TRANSITION[13] = copy(arrayOfInt1);
    TRANSITION[13][78] = 14;
    TRANSITION[13][110] = 14;
    TRANSITION[14] = arrayOfInt3;
    TRANSITION[15] = copy(arrayOfInt1);
    TRANSITION[15][65] = 16;
    TRANSITION[15][97] = 16;
    TRANSITION[16] = copy(arrayOfInt1);
    TRANSITION[16][76] = 17;
    TRANSITION[16][108] = 17;
    TRANSITION[17] = copy(arrayOfInt1);
    TRANSITION[17][76] = 18;
    TRANSITION[17][108] = 18;
    TRANSITION[18] = arrayOfInt3;
    TRANSITION[19] = copy(arrayOfInt1);
    TRANSITION[19][69] = 20;
    TRANSITION[19][101] = 20;
    TRANSITION[20] = copy(arrayOfInt1);
    TRANSITION[20][67] = 21;
    TRANSITION[20][99] = 21;
    TRANSITION[20][76] = 26;
    TRANSITION[20][108] = 26;
    TRANSITION[21] = copy(arrayOfInt1);
    TRANSITION[21][76] = 22;
    TRANSITION[21][108] = 22;
    TRANSITION[22] = copy(arrayOfInt1);
    TRANSITION[22][65] = 23;
    TRANSITION[22][97] = 23;
    TRANSITION[23] = copy(arrayOfInt1);
    TRANSITION[23][82] = 24;
    TRANSITION[23][114] = 24;
    TRANSITION[24] = copy(arrayOfInt1);
    TRANSITION[24][69] = 25;
    TRANSITION[24][101] = 25;
    TRANSITION[25] = arrayOfInt3;
    TRANSITION[26] = copy(arrayOfInt1);
    TRANSITION[26][69] = 27;
    TRANSITION[26][101] = 27;
    TRANSITION[27] = copy(arrayOfInt1);
    TRANSITION[27][84] = 28;
    TRANSITION[27][116] = 28;
    TRANSITION[28] = copy(arrayOfInt1);
    TRANSITION[28][69] = 29;
    TRANSITION[28][101] = 29;
    TRANSITION[29] = arrayOfInt3;
    TRANSITION[30] = copy(arrayOfInt1);
    TRANSITION[30][78] = 31;
    TRANSITION[30][110] = 31;
    TRANSITION[31] = copy(arrayOfInt1);
    TRANSITION[31][83] = 32;
    TRANSITION[31][115] = 32;
    TRANSITION[32] = copy(arrayOfInt1);
    TRANSITION[32][69] = 33;
    TRANSITION[32][101] = 33;
    TRANSITION[33] = copy(arrayOfInt1);
    TRANSITION[33][82] = 34;
    TRANSITION[33][114] = 34;
    TRANSITION[34] = copy(arrayOfInt1);
    TRANSITION[34][84] = 35;
    TRANSITION[34][116] = 35;
    TRANSITION[35] = arrayOfInt3;
    TRANSITION[36] = copy(arrayOfInt1);
    TRANSITION[36][69] = 37;
    TRANSITION[36][101] = 37;
    TRANSITION[37] = copy(arrayOfInt1);
    TRANSITION[37][76] = 38;
    TRANSITION[37][108] = 38;
    TRANSITION[38] = copy(arrayOfInt1);
    TRANSITION[38][69] = 39;
    TRANSITION[38][101] = 39;
    TRANSITION[39] = copy(arrayOfInt1);
    TRANSITION[39][67] = 40;
    TRANSITION[39][99] = 40;
    TRANSITION[40] = copy(arrayOfInt1);
    TRANSITION[40][84] = 41;
    TRANSITION[40][116] = 41;
    TRANSITION[41] = arrayOfInt3;
    TRANSITION[42] = copy(arrayOfInt1);
    TRANSITION[42][80] = 43;
    TRANSITION[42][112] = 43;
    TRANSITION[43] = copy(arrayOfInt1);
    TRANSITION[43][68] = 44;
    TRANSITION[43][100] = 44;
    TRANSITION[44] = copy(arrayOfInt1);
    TRANSITION[44][65] = 45;
    TRANSITION[44][97] = 45;
    TRANSITION[45] = copy(arrayOfInt1);
    TRANSITION[45][84] = 46;
    TRANSITION[45][116] = 46;
    TRANSITION[46] = copy(arrayOfInt1);
    TRANSITION[46][69] = 47;
    TRANSITION[46][101] = 47;
    TRANSITION[47] = arrayOfInt3;
    TRANSITION[48] = copy(arrayOfInt1);
    TRANSITION[48][69] = 49;
    TRANSITION[48][101] = 49;
    TRANSITION[49] = copy(arrayOfInt1);
    TRANSITION[49][82] = 50;
    TRANSITION[49][114] = 50;
    TRANSITION[50] = copy(arrayOfInt1);
    TRANSITION[50][71] = 51;
    TRANSITION[50][103] = 51;
    TRANSITION[51] = copy(arrayOfInt1);
    TRANSITION[51][69] = 52;
    TRANSITION[51][101] = 52;
    TRANSITION[52] = arrayOfInt3;
    TRANSITION[53] = copy(arrayOfInt1);
    TRANSITION[53][73] = 54;
    TRANSITION[53][105] = 54;
    TRANSITION[54] = copy(arrayOfInt1);
    TRANSITION[54][84] = 55;
    TRANSITION[54][116] = 55;
    TRANSITION[55] = copy(arrayOfInt1);
    TRANSITION[55][72] = 56;
    TRANSITION[55][104] = 56;
    TRANSITION[56] = arrayOfInt3;
    TRANSITION[57] = arrayOfInt4;
    TRANSITION[58] = copy(arrayOfInt4);
    TRANSITION[58][42] = 62;
    TRANSITION[59] = copy(arrayOfInt4);
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
    TRANSITION[65] = copyReplacing(arrayOfInt3, 9, 65);

    int[] arrayOfInt5 = copy(arrayOfInt4);
    TRANSITION[66] = arrayOfInt5;

    TRANSITION[66][32] = 66;
    TRANSITION[66][9] = 66;
    TRANSITION[66][10] = 66;
    TRANSITION[66][13] = 66;
    TRANSITION[66][61] = 66;

    TRANSITION[66][87] = 67;
    TRANSITION[66][119] = 67;
    TRANSITION[67] = copy(arrayOfInt4);
    TRANSITION[67][72] = 68;
    TRANSITION[67][104] = 68;
    TRANSITION[68] = copy(arrayOfInt4);
    TRANSITION[68][69] = 69;
    TRANSITION[68][101] = 69;
    TRANSITION[69] = copy(arrayOfInt4);
    TRANSITION[69][82] = 70;
    TRANSITION[69][114] = 70;
    TRANSITION[70] = copy(arrayOfInt4);
    TRANSITION[70][69] = 71;
    TRANSITION[70][101] = 71;
    TRANSITION[71] = arrayOfInt4;

    TRANSITION[66][79] = 72;
    TRANSITION[66][111] = 72;
    TRANSITION[72] = copy(arrayOfInt4);
    TRANSITION[72][82] = 73;
    TRANSITION[72][114] = 73;
    TRANSITION[73] = copy(arrayOfInt4);
    TRANSITION[73][68] = 74;
    TRANSITION[73][100] = 74;
    TRANSITION[74] = copy(arrayOfInt4);
    TRANSITION[74][69] = 75;
    TRANSITION[74][101] = 75;
    TRANSITION[75] = copy(arrayOfInt4);
    TRANSITION[75][82] = 76;
    TRANSITION[75][114] = 76;
    TRANSITION[76] = copyReplacing(arrayOfInt5, 66, 77);
    TRANSITION[76][47] = 80;
    TRANSITION[76][45] = 83;

    TRANSITION[77] = copyReplacing(arrayOfInt5, 66, 77);
    TRANSITION[77][47] = 80;
    TRANSITION[80] = copy(arrayOfInt4);
    TRANSITION[80][42] = 81;
    TRANSITION[81] = newArray(128, 81);
    TRANSITION[81][42] = 82;
    TRANSITION[82] = newArray(128, 81);
    TRANSITION[82][47] = 77;

    TRANSITION[77][45] = 83;
    TRANSITION[83] = copy(arrayOfInt4);
    TRANSITION[83][45] = 84;
    TRANSITION[84] = newArray(128, 84);
    TRANSITION[84][10] = 77;

    TRANSITION[77][66] = 78;
    TRANSITION[77][98] = 78;
    TRANSITION[78] = copy(arrayOfInt4);
    TRANSITION[78][89] = 79;
    TRANSITION[78][121] = 79;
    TRANSITION[79] = arrayOfInt4;

    TRANSITION[66][70] = 84;
    TRANSITION[66][102] = 84;
    TRANSITION[84] = copy(arrayOfInt4);
    TRANSITION[84][79] = 85;
    TRANSITION[84][111] = 85;
    TRANSITION[85] = copy(arrayOfInt4);
    TRANSITION[85][82] = 86;
    TRANSITION[85][114] = 86;
    TRANSITION[86] = copyReplacing(arrayOfInt5, 66, 87);
    TRANSITION[86][47] = 94;
    TRANSITION[86][45] = 97;

    TRANSITION[87] = copyReplacing(arrayOfInt5, 66, 87);
    TRANSITION[87][47] = 94;
    TRANSITION[94] = copy(arrayOfInt4);
    TRANSITION[94][42] = 95;
    TRANSITION[95] = newArray(128, 95);
    TRANSITION[95][42] = 96;
    TRANSITION[96] = newArray(128, 95);
    TRANSITION[96][47] = 87;

    TRANSITION[87][45] = 97;
    TRANSITION[97] = copy(arrayOfInt4);
    TRANSITION[97][45] = 98;
    TRANSITION[98] = newArray(128, 98);
    TRANSITION[98][10] = 87;

    TRANSITION[87][85] = 88;
    TRANSITION[87][117] = 88;
    TRANSITION[88] = copy(arrayOfInt4);
    TRANSITION[88][80] = 89;
    TRANSITION[88][112] = 89;
    TRANSITION[89] = copy(arrayOfInt4);
    TRANSITION[89][68] = 90;
    TRANSITION[89][100] = 90;
    TRANSITION[90] = copy(arrayOfInt4);
    TRANSITION[90][65] = 91;
    TRANSITION[90][97] = 91;
    TRANSITION[91] = copy(arrayOfInt4);
    TRANSITION[91][84] = 92;
    TRANSITION[91][116] = 92;
    TRANSITION[92] = copy(arrayOfInt4);
    TRANSITION[92][69] = 93;
    TRANSITION[92][101] = 93;
    TRANSITION[93] = arrayOfInt4;

    TRANSITION[66][78] = 101;
    TRANSITION[66][110] = 101;
    TRANSITION[101] = copy(arrayOfInt4);
    TRANSITION[101][39] = 102;
    TRANSITION[102] = newArray(128, 102);
    TRANSITION[102][39] = 66;

    int[] arrayOfInt6 = newArray(128, 0);

    int[] arrayOfInt7 = copy(arrayOfInt6);

    arrayOfInt7[63] = 11;

    int[] arrayOfInt8 = new int[''];

    for (int i = 0; i < arrayOfInt8.length; i++) {
      if (TRANSITION[8][i] == 8)
        arrayOfInt8[i] = 12;
      else
        arrayOfInt8[i] = 13;
    }
    int[] arrayOfInt9 = new int[''];

    for (int j = 0; j < arrayOfInt9.length; j++) {
      if (TRANSITION[65][j] == 65)
        arrayOfInt9[j] = 12;
      else
        arrayOfInt9[j] = 13;
    }
    int[] arrayOfInt10 = copy(arrayOfInt6);

    for (int k = 0; k < arrayOfInt10.length; k++) {
      if (arrayOfInt3[k] != 9)
        arrayOfInt10[k] = 2;
    }
    int[] arrayOfInt11 = copyReplacing(arrayOfInt10, 2, 3);
    int[] arrayOfInt12 = copyReplacing(arrayOfInt10, 2, 1);
    int[] arrayOfInt13 = copyReplacing(arrayOfInt10, 2, 4);
    int[] arrayOfInt14 = copyReplacing(arrayOfInt10, 2, 5);

    int[] arrayOfInt15 = copyReplacing(arrayOfInt10, 2, 7);

    for (int m = 0; m < arrayOfInt15.length; m++) {
      if (arrayOfInt15[m] == 5) {
        arrayOfInt15[m] = 0;
      }
    }
    int[] arrayOfInt16 = copyReplacing(arrayOfInt15, 7, 8);
    int[] arrayOfInt17 = copyReplacing(arrayOfInt15, 7, 6);
    int[] arrayOfInt18 = copyReplacing(arrayOfInt15, 7, 9);
    int[] arrayOfInt19 = copyReplacing(arrayOfInt15, 7, 10);

    int[] arrayOfInt20 = copy(arrayOfInt6);
    arrayOfInt20[39] = 14;

    int[] arrayOfInt21 = copy(arrayOfInt6);
    arrayOfInt21[39] = 15;

    ACTION[0] = arrayOfInt7;
    ACTION[1] = arrayOfInt7;
    ACTION[2] = arrayOfInt7;
    ACTION[3] = arrayOfInt6;
    ACTION[4] = arrayOfInt6;
    ACTION[5] = arrayOfInt6;
    ACTION[6] = arrayOfInt6;
    ACTION[7] = arrayOfInt6;
    ACTION[8] = arrayOfInt8;
    ACTION[99] = arrayOfInt20;
    ACTION[100] = arrayOfInt21;
    ACTION[9] = arrayOfInt14;
    ACTION[10] = arrayOfInt14;
    ACTION[11] = arrayOfInt14;
    ACTION[12] = arrayOfInt14;
    ACTION[13] = arrayOfInt14;
    ACTION[14] = arrayOfInt10;
    ACTION[15] = arrayOfInt14;
    ACTION[16] = arrayOfInt14;
    ACTION[17] = arrayOfInt14;
    ACTION[18] = arrayOfInt11;
    ACTION[19] = arrayOfInt14;
    ACTION[20] = arrayOfInt14;
    ACTION[21] = arrayOfInt14;
    ACTION[22] = arrayOfInt14;
    ACTION[23] = arrayOfInt14;
    ACTION[24] = arrayOfInt14;
    ACTION[25] = arrayOfInt10;
    ACTION[26] = arrayOfInt14;
    ACTION[27] = arrayOfInt14;
    ACTION[28] = arrayOfInt14;
    ACTION[29] = arrayOfInt12;
    ACTION[30] = arrayOfInt14;
    ACTION[31] = arrayOfInt14;
    ACTION[32] = arrayOfInt14;
    ACTION[33] = arrayOfInt14;
    ACTION[34] = arrayOfInt14;
    ACTION[35] = arrayOfInt12;
    ACTION[36] = arrayOfInt14;
    ACTION[37] = arrayOfInt14;
    ACTION[38] = arrayOfInt14;
    ACTION[39] = arrayOfInt14;
    ACTION[40] = arrayOfInt14;
    ACTION[41] = arrayOfInt13;
    ACTION[42] = arrayOfInt14;
    ACTION[43] = arrayOfInt14;
    ACTION[44] = arrayOfInt14;
    ACTION[45] = arrayOfInt14;
    ACTION[46] = arrayOfInt14;
    ACTION[47] = arrayOfInt12;
    ACTION[48] = arrayOfInt14;
    ACTION[49] = arrayOfInt14;
    ACTION[50] = arrayOfInt14;
    ACTION[51] = arrayOfInt14;
    ACTION[52] = arrayOfInt12;
    ACTION[53] = arrayOfInt14;
    ACTION[54] = arrayOfInt14;
    ACTION[55] = arrayOfInt14;
    ACTION[56] = arrayOfInt13;
    ACTION[57] = arrayOfInt7;
    ACTION[58] = arrayOfInt7;
    ACTION[59] = arrayOfInt7;
    ACTION[60] = arrayOfInt6;
    ACTION[61] = arrayOfInt6;
    ACTION[62] = arrayOfInt6;
    ACTION[63] = arrayOfInt6;
    ACTION[64] = arrayOfInt6;
    ACTION[65] = arrayOfInt9;
    ACTION[101] = arrayOfInt20;
    ACTION[102] = arrayOfInt21;

    ACTION[66] = arrayOfInt7;

    ACTION[67] = arrayOfInt6;
    ACTION[68] = arrayOfInt6;
    ACTION[69] = arrayOfInt6;
    ACTION[70] = arrayOfInt6;
    ACTION[71] = arrayOfInt17;

    ACTION[72] = arrayOfInt6;
    ACTION[73] = arrayOfInt6;
    ACTION[74] = arrayOfInt6;
    ACTION[75] = arrayOfInt6;
    ACTION[76] = arrayOfInt15;

    ACTION[77] = arrayOfInt6;
    ACTION[78] = arrayOfInt6;
    ACTION[79] = arrayOfInt16;

    ACTION[80] = arrayOfInt6;
    ACTION[81] = arrayOfInt6;
    ACTION[82] = arrayOfInt6;
    ACTION[83] = arrayOfInt6;
    ACTION[84] = arrayOfInt6;

    ACTION[84] = arrayOfInt6;
    ACTION[85] = arrayOfInt6;
    ACTION[86] = arrayOfInt18;

    ACTION[87] = arrayOfInt7;
    ACTION[88] = arrayOfInt6;
    ACTION[89] = arrayOfInt6;
    ACTION[90] = arrayOfInt6;
    ACTION[91] = arrayOfInt6;
    ACTION[92] = arrayOfInt6;
    ACTION[93] = arrayOfInt19;

    ACTION[94] = arrayOfInt6;
    ACTION[95] = arrayOfInt6;
    ACTION[96] = arrayOfInt6;
    ACTION[97] = arrayOfInt6;
    ACTION[98] = arrayOfInt6;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleSqlReadOnly
 * JD-Core Version:    0.6.0
 */