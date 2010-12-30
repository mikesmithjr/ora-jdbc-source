// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: OraclePreparedStatement.java

package oracle.jdbc.driver;

class FDBigInt {

    int nWords;
    int data[];

    FDBigInt(int v) {
        nWords = 1;
        data = new int[1];
        data[0] = v;
    }

    FDBigInt(long v) {
        data = new int[2];
        data[0] = (int) v;
        data[1] = (int) (v >>> 32);
        nWords = data[1] != 0 ? 2 : 1;
    }

    FDBigInt(FDBigInt other) {
        data = new int[nWords = other.nWords];
        System.arraycopy(other.data, 0, data, 0, nWords);
    }

    FDBigInt(int d[], int n) {
        data = d;
        nWords = n;
    }

    void lshiftMe(int c) throws IllegalArgumentException {
        if (c <= 0) {
            if (c == 0) {
                return;
            } else {
                throw new IllegalArgumentException("negative shift count");
            }
        }
        int wordcount = c >> 5;
        int bitcount = c & 0x1f;
        int anticount = 32 - bitcount;
        int t[] = data;
        int s[] = data;
        if (nWords + wordcount + 1 > t.length) {
            t = new int[nWords + wordcount + 1];
        }
        int target = nWords + wordcount;
        int src = nWords - 1;
        if (bitcount == 0) {
            System.arraycopy(s, 0, t, wordcount, nWords);
            target = wordcount - 1;
        } else {
            for (t[target--] = s[src] >>> anticount; src >= 1; t[target--] = s[src] << bitcount
                    | s[--src] >>> anticount) {
            }
            t[target--] = s[src] << bitcount;
        }
        while (target >= 0) {
            t[target--] = 0;
        }
        data = t;
        for (nWords += wordcount + 1; nWords > 1 && data[nWords - 1] == 0; nWords--) {
        }
    }

    int normalizeMe() throws IllegalArgumentException {
        int wordcount = 0;
        int bitcount = 0;
        int v = 0;
        int src;
        for (src = nWords - 1; src >= 0 && (v = data[src]) == 0; src--) {
            wordcount++;
        }

        if (src < 0) {
            throw new IllegalArgumentException("zero value");
        }
        nWords -= wordcount;
        if ((v & 0xf0000000) != 0) {
            for (bitcount = 32; (v & 0xf0000000) != 0; bitcount--) {
                v >>>= 1;
            }

        } else {
            while (v <= 0xfffff) {
                v <<= 8;
                bitcount += 8;
            }
            while (v <= 0x7ffffff) {
                v <<= 1;
                bitcount++;
            }
        }
        if (bitcount != 0) {
            lshiftMe(bitcount);
        }
        return bitcount;
    }

    FDBigInt mult(int iv) {
        long v = iv;
        int r[] = new int[v * ((long) data[nWords - 1] & 0xffffffffL) <= 0xfffffffL ? nWords
                : nWords + 1];
        long p = 0L;
        for (int i = 0; i < nWords; i++) {
            p += v * ((long) data[i] & 0xffffffffL);
            r[i] = (int) p;
            p >>>= 32;
        }

        if (p == 0L) {
            return new FDBigInt(r, nWords);
        } else {
            r[nWords] = (int) p;
            return new FDBigInt(r, nWords + 1);
        }
    }

    FDBigInt mult(FDBigInt other) {
        int r[] = new int[nWords + other.nWords];
        int i;
        for (i = 0; i < nWords; i++) {
            long v = (long) data[i] & 0xffffffffL;
            long p = 0L;
            int j;
            for (j = 0; j < other.nWords; j++) {
                p += ((long) r[i + j] & 0xffffffffL) + v * ((long) other.data[j] & 0xffffffffL);
                r[i + j] = (int) p;
                p >>>= 32;
            }

            r[i + j] = (int) p;
        }

        for (i = r.length - 1; i > 0 && r[i] == 0; i--) {
        }
        return new FDBigInt(r, i + 1);
    }

    FDBigInt add(FDBigInt other) {
        long c = 0L;
        int a[];
        int b[];
        int n;
        int m;
        if (nWords >= other.nWords) {
            a = data;
            n = nWords;
            b = other.data;
            m = other.nWords;
        } else {
            a = other.data;
            n = other.nWords;
            b = data;
            m = nWords;
        }
        int r[] = new int[n];
        int i;
        for (i = 0; i < n; i++) {
            c += (long) a[i] & 0xffffffffL;
            if (i < m) {
                c += (long) b[i] & 0xffffffffL;
            }
            r[i] = (int) c;
            c >>= 32;
        }

        if (c != 0L) {
            int s[] = new int[r.length + 1];
            System.arraycopy(r, 0, s, 0, r.length);
            s[i++] = (int) c;
            return new FDBigInt(s, i);
        } else {
            return new FDBigInt(r, i);
        }
    }

    int cmp(FDBigInt other) {
        int i;
        if (nWords > other.nWords) {
            int j = other.nWords - 1;
            for (i = nWords - 1; i > j; i--) {
                if (data[i] != 0) {
                    return 1;
                }
            }

        } else if (nWords < other.nWords) {
            int j = nWords - 1;
            for (i = other.nWords - 1; i > j; i--) {
                if (other.data[i] != 0) {
                    return -1;
                }
            }

        } else {
            i = nWords - 1;
        }
        for (; i > 0 && data[i] == other.data[i]; i--) {
        }
        int a = data[i];
        int b = other.data[i];
        if (a < 0) {
            if (b < 0) {
                return a - b;
            } else {
                return 1;
            }
        }
        if (b < 0) {
            return -1;
        } else {
            return a - b;
        }
    }

    int quoRemIteration(FDBigInt S) throws IllegalArgumentException {
        if (nWords != S.nWords) {
            throw new IllegalArgumentException("disparate values");
        }
        int n = nWords - 1;
        long q = ((long) data[n] & 0xffffffffL) / (long) S.data[n];
        long diff = 0L;
        for (int i = 0; i <= n; i++) {
            diff += ((long) data[i] & 0xffffffffL) - q * ((long) S.data[i] & 0xffffffffL);
            data[i] = (int) diff;
            diff >>= 32;
        }

        if (diff != 0L) {
            for (long sum = 0L; sum == 0L;) {
                sum = 0L;
                for (int i = 0; i <= n; i++) {
                    sum += ((long) data[i] & 0xffffffffL) + ((long) S.data[i] & 0xffffffffL);
                    data[i] = (int) sum;
                    sum >>= 32;
                }

                if (sum != 0L && sum != 1L) {
                    throw new RuntimeException("Assertion botch: " + sum
                            + " carry out of division correction");
                }
                q--;
            }

        }
        long p = 0L;
        for (int i = 0; i <= n; i++) {
            p += 10L * ((long) data[i] & 0xffffffffL);
            data[i] = (int) p;
            p >>= 32;
        }

        if (p != 0L) {
            throw new RuntimeException("Assertion botch: carry out of *10");
        } else {
            return (int) q;
        }
    }
}
