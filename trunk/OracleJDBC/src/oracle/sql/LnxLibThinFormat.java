// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: LnxLibThinFormat.java

package oracle.sql;

import java.sql.SQLException;
import oracle.core.lmx.CoreException;

class LnxLibThinFormat {

    boolean LNXNFFMI;
    boolean LNXNFFDS;
    boolean LNXNFFPR;
    boolean LNXNFFBL;
    boolean LNXNFFDA;
    boolean LNXNFFED;
    boolean LNXNFFSN;
    boolean LNXNFFVF;
    boolean LNXNFFSH;
    boolean LNXNFFST;
    boolean LNXNFFCH;
    boolean LNXNFFCT;
    boolean LNXNFFRC;
    boolean LNXNFFRN;
    boolean LNXNFFLC;
    boolean LNXNFFIC;
    boolean LNXNFNRD;
    boolean LNXNFRDX;
    boolean LNXNFFIL;
    boolean LNXNFFPT;
    boolean LNXNFF05;
    boolean LNXNFFHX;
    boolean LNXNFFTM;
    boolean LNXNFFUN;
    byte lnxnfgps[];
    int lnxnflhd;
    int lnxnfrhd;
    int lnxnfsiz;
    int lnxnfzld;
    int lnxnfztr;
    private final int LNXPFL_US = 1;
    private final int LNXPFL_NLS = -1;
    private final int LXM_LILCURR = 11;
    private final int LXM_LIUCURR = 11;
    private final int LXM_LIICURR = 8;
    private final int LXM_ROMOUT = 15;

    LnxLibThinFormat() {
        LNXNFFMI = false;
        LNXNFFDS = false;
        LNXNFFPR = false;
        LNXNFFBL = false;
        LNXNFFDA = false;
        LNXNFFED = false;
        LNXNFFSN = false;
        LNXNFFVF = false;
        LNXNFFSH = false;
        LNXNFFST = false;
        LNXNFFCH = false;
        LNXNFFCT = false;
        LNXNFFRC = false;
        LNXNFFRN = false;
        LNXNFFLC = false;
        LNXNFFIC = false;
        LNXNFNRD = false;
        LNXNFRDX = false;
        LNXNFFIL = false;
        LNXNFFPT = false;
        LNXNFF05 = false;
        LNXNFFHX = false;
        LNXNFFTM = false;
        LNXNFFUN = false;
        lnxnfgps = new byte[40];
        lnxnflhd = 0;
        lnxnfrhd = 0;
        lnxnfsiz = 0;
        lnxnfzld = 0;
        lnxnfztr = 0;
    }

    public void parseFormat(String s) throws SQLException {
        int i = 0;
        int j = 0;
        boolean flag = false;
        boolean flag1 = false;
        int k = 0;
        int l = 0;
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        char c1 = '\0';
        char c2 = '\0';
        int l1 = 0;
        byte byte0 = 39;
        int i2 = 0;
        i1 = s.length();
        char ac[] = s.toCharArray();
        LNXNFFIL = true;
        for (; i1 != 0; i1--) {
            char c = Character.toLowerCase(ac[j1]);
            switch (c) {
            case 48: // '0'
            case 53: // '5'
            case 57: // '9'
            case 120: // 'x'
                if (LNXNFFSN) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                if (l == 120 && c != 'x') {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                if (c == '5') {
                    if (i1 == 2) {
                        c1 = Character.toLowerCase(ac[j1 + 1]);
                    } else if (i1 == 3) {
                        c1 = Character.toLowerCase(ac[j1 + 1]);
                        c2 = Character.toLowerCase(ac[j1 + 2]);
                    }
                    if (!LNXNFF05
                            && (i1 == 1 || i1 == 2 && c1 == 's' || c1 == 'c' || c1 == 'l'
                                    || c1 == 'u' || i1 == 3
                                    && (c1 == 'p' && c2 == 'r' || c1 == 'p' && c2 == 't' || c1 == 'm'
                                            && c2 == 'i'))) {
                        LNXNFF05 = true;
                    } else {
                        throw new SQLException(CoreException.getMessage((byte) 5));
                    }
                }
                if (c == 'x') {
                    if (l == 0 || l == 109 || l == 48 || l == 120) {
                        LNXNFFHX = true;
                        if (ac[j1] == 'x') {
                            LNXNFFLC = true;
                        }
                    } else {
                        throw new SQLException(CoreException.getMessage((byte) 5));
                    }
                }
                i++;
                if (c == '0' && (flag || j == 0)) {
                    j = i;
                }
                break;

            case 103: // 'g'
                if (LNXNFFSN || LNXNFFHX || flag || l1 == byte0 || i2 > 0 || i == 0) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                i2 = -1;
                lnxnfgps[l1] = (byte) (0x80 | i);
                l1++;
                break;

            case 44: // ','
                if (LNXNFFSN || LNXNFFHX || flag || l1 == byte0 || i2 < 0 || i == 0) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                flag1 = true;
                lnxnfgps[l1] = (byte) i;
                l1++;
                i2 = 1;
                break;

            case 99: // 'c'
            case 108: // 'l'
            case 117: // 'u'
                if (LNXNFFCH || LNXNFFCT || LNXNFFRC || LNXNFFSN || LNXNFFDS || LNXNFFHX) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                if (c == 'c') {
                    k += 7;
                    LNXNFFIC = true;
                } else if (c == 'l') {
                    k += 10;
                } else {
                    k += 10;
                    LNXNFFUN = true;
                }
                if (j1 == k1) {
                    LNXNFFCH = true;
                    break;
                }
                if (i1 == 2) {
                    c1 = Character.toLowerCase(ac[j1 + 1]);
                } else if (i1 == 3) {
                    c1 = Character.toLowerCase(ac[j1 + 1]);
                    c2 = Character.toLowerCase(ac[j1 + 2]);
                }
                if (i1 == 1
                        || i1 == 2
                        && c1 == 's'
                        || i1 == 3
                        && (c1 == 'p' && c2 == 'r' || c1 == 'p' && c2 == 't' || c1 == 'm'
                                && c2 == 'i')) {
                    LNXNFFCT = true;
                    break;
                }
                if (LNXNFF05) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                LNXNFFRC = true;
                // fall through

            case 100: // 'd'
                if (i2 > 0 || LNXNFFHX) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                i2 = -1;
                // fall through

            case 118: // 'v'
                if (c == 'v') {
                    LNXNFNRD = true;
                }
                // fall through

            case 46: // '.'
                if (LNXNFFSN || LNXNFFHX || flag) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                flag = true;
                lnxnflhd = i;
                if (j != 0) {
                    lnxnfzld = (i - j) + 1;
                    j = 0;
                } else {
                    lnxnfzld = 0;
                }
                i = 0;
                if (c != '.' && c != 'd') {
                    break;
                }
                k++;
                if (c != '.') {
                    break;
                }
                if (i2 < 0) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                i2 = 1;
                LNXNFRDX = true;
                break;

            case 98: // 'b'
                if (LNXNFFSN || LNXNFFBL || LNXNFFHX) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                LNXNFFBL = true;
                break;

            case 101: // 'e'
                if (LNXNFFSN || LNXNFF05 || LNXNFFHX || flag1) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                LNXNFFSN = true;
                if (i1 < 4 || ac[j1] != ac[j1 + 1] || ac[j1] != ac[j1 + 2] || ac[j1] != ac[j1 + 3]) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                j1 += 3;
                i1 -= 3;
                k += 5;
                break;

            case 36: // '$'
                if (LNXNFFSN || LNXNFFDS || LNXNFFCH || LNXNFFCT || LNXNFFRC || LNXNFFHX) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                LNXNFFDS = true;
                k++;
                break;

            case 114: // 'r'
                if (j1 != k1 || i1 != 2) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                LNXNFFRN = true;
                if (ac[j1] == 'r') {
                    LNXNFFLC = true;
                }
                lnxnfsiz = 15;
                LNXNFFVF = true;
                return;

            case 102: // 'f'
                if (j1 != k1 || !LNXNFFIL) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                LNXNFFIL = false;
                j1++;
                if (Character.toLowerCase(ac[j1]) == 'm') {
                    i1--;
                    k1 = j1 + 1;
                    c = 'm';
                } else {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                break;

            case 112: // 'p'
                if (LNXNFFSH || LNXNFFST || LNXNFFHX) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                k++;
                if (--i1 > 1) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                j1++;
                if (Character.toLowerCase(ac[j1]) == 'r') {
                    LNXNFFPR = true;
                    break;
                }
                if (Character.toLowerCase(ac[j1]) == 't') {
                    LNXNFFPT = true;
                } else {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                break;

            case 109: // 'm'
                if (LNXNFFSH || LNXNFFST || LNXNFFHX) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                LNXNFFMI = true;
                j1++;
                if (Character.toLowerCase(ac[j1]) == 'i') {
                    if (--i1 > 1) {
                        throw new SQLException(CoreException.getMessage((byte) 5));
                    }
                } else {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                break;

            case 115: // 's'
                if (LNXNFFSH || LNXNFFHX) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                if (j1 == k1) {
                    LNXNFFSH = true;
                    k1++;
                    break;
                }
                if (i1 == 1) {
                    LNXNFFST = true;
                } else {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                break;

            case 116: // 't'
                if (j1 != k1 || i1 < 2 || i1 > 3) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                if (Character.toLowerCase(ac[j1 + 1]) != 'm') {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
                LNXNFFTM = true;
                LNXNFFIL = false;
                switch (i1 != 3 ? 57 : Character.toLowerCase(ac[j1 + 2])) {
                default:
                    throw new SQLException(CoreException.getMessage((byte) 5));

                case 101: // 'e'
                    LNXNFFSN = true;
                    // fall through

                case 57: // '9'
                    lnxnflhd = 0;
                    break;
                }
                lnxnfrhd = 0;
                lnxnfsiz = 64;
                lnxnfzld = 0;
                lnxnfztr = 0;
                LNXNFFVF = true;
                return;

            case 37: // '%'
            case 38: // '&'
            case 39: // '\''
            case 40: // '('
            case 41: // ')'
            case 42: // '*'
            case 43: // '+'
            case 45: // '-'
            case 47: // '/'
            case 49: // '1'
            case 50: // '2'
            case 51: // '3'
            case 52: // '4'
            case 54: // '6'
            case 55: // '7'
            case 56: // '8'
            case 58: // ':'
            case 59: // ';'
            case 60: // '<'
            case 61: // '='
            case 62: // '>'
            case 63: // '?'
            case 64: // '@'
            case 65: // 'A'
            case 66: // 'B'
            case 67: // 'C'
            case 68: // 'D'
            case 69: // 'E'
            case 70: // 'F'
            case 71: // 'G'
            case 72: // 'H'
            case 73: // 'I'
            case 74: // 'J'
            case 75: // 'K'
            case 76: // 'L'
            case 77: // 'M'
            case 78: // 'N'
            case 79: // 'O'
            case 80: // 'P'
            case 81: // 'Q'
            case 82: // 'R'
            case 83: // 'S'
            case 84: // 'T'
            case 85: // 'U'
            case 86: // 'V'
            case 87: // 'W'
            case 88: // 'X'
            case 89: // 'Y'
            case 90: // 'Z'
            case 91: // '['
            case 92: // '\\'
            case 93: // ']'
            case 94: // '^'
            case 95: // '_'
            case 96: // '`'
            case 97: // 'a'
            case 104: // 'h'
            case 105: // 'i'
            case 106: // 'j'
            case 107: // 'k'
            case 110: // 'n'
            case 111: // 'o'
            case 113: // 'q'
            case 119: // 'w'
            default:
                throw new SQLException(CoreException.getMessage((byte) 5));
            }
            l = c;
            j1++;
        }

        if (flag) {
            lnxnfrhd = i;
            lnxnfztr = !LNXNFFIL && !LNXNFNRD ? j : i;
        } else {
            lnxnflhd = i;
            lnxnfzld = j == 0 ? 0 : (i - j) + 1;
            lnxnfrhd = 0;
            lnxnfztr = 0;
            LNXNFNRD = true;
        }
        if (LNXNFFSN) {
            if (lnxnflhd <= 1) {
                if (lnxnflhd == 0) {
                    throw new SQLException(CoreException.getMessage((byte) 5));
                }
            } else {
                lnxnflhd = 1;
            }
            if (lnxnfzld > 1) {
                lnxnfzld = 1;
            }
        }
        k += lnxnflhd;
        k += lnxnfrhd;
        k += l1 + 1;
        if (k > 64) {
            throw new SQLException(CoreException.getMessage((byte) 5));
        } else {
            lnxnfsiz = k;
            return;
        }
    }
}
