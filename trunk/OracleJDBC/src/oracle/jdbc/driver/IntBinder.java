package oracle.jdbc.driver;

class IntBinder extends VarnumBinder {
    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        byte[] b = bindBytes;
        int offset = byteoffset + 1;
        int val = stmt.parameterInt[rank][bindPosition];
        int len = 0;

        if (val == 0) {
            b[offset] = -128;
            len = 1;
        } else if (val < 0) {
            if (val == -2147483648) {
                b[offset] = 58;
                b[(offset + 1)] = 80;
                b[(offset + 2)] = 54;

                b[(offset + 3)] = 53;
                b[(offset + 4)] = 65;
                b[(offset + 5)] = 53;
                b[(offset + 6)] = 102;
                len = 7;
            } else if (-val < 100) {
                b[offset] = 62;
                b[(offset + 1)] = (byte) (101 + val);
                b[(offset + 2)] = 102;
                len = 3;
            } else if (-val < 10000) {
                b[offset] = 61;
                b[(offset + 1)] = (byte) (101 - -val / 100);
                int x = -val % 100;

                if (x != 0) {
                    b[(offset + 2)] = (byte) (101 - x);
                    b[(offset + 3)] = 102;
                    len = 4;
                } else {
                    b[(offset + 2)] = 102;
                    len = 3;
                }
            } else if (-val < 1000000) {
                b[offset] = 60;
                b[(offset + 1)] = (byte) (101 - -val / 10000);
                int x = -val % 100;

                if (x != 0) {
                    b[(offset + 2)] = (byte) (101 - -val % 10000 / 100);
                    b[(offset + 3)] = (byte) (101 - x);
                    b[(offset + 4)] = 102;
                    len = 5;
                } else {
                    x = -val % 10000 / 100;

                    if (x != 0) {
                        b[(offset + 2)] = (byte) (101 - x);
                        b[(offset + 3)] = 102;
                        len = 4;
                    } else {
                        b[(offset + 2)] = 102;
                        len = 3;
                    }
                }
            } else if (-val < 100000000) {
                b[offset] = 59;
                b[(offset + 1)] = (byte) (101 - -val / 1000000);
                int x = -val % 100;

                if (x != 0) {
                    b[(offset + 2)] = (byte) (101 - -val % 1000000 / 10000);
                    b[(offset + 3)] = (byte) (101 - -val % 10000 / 100);
                    b[(offset + 4)] = (byte) (101 - x);
                    b[(offset + 5)] = 102;
                    len = 6;
                } else {
                    x = -val % 10000 / 100;

                    if (x != 0) {
                        b[(offset + 2)] = (byte) (101 - -val % 1000000 / 10000);
                        b[(offset + 3)] = (byte) (101 - x);
                        b[(offset + 4)] = 102;
                        len = 5;
                    } else {
                        x = -val % 1000000 / 10000;

                        if (x != 0) {
                            b[(offset + 2)] = (byte) (101 - x);
                            b[(offset + 3)] = 102;
                            len = 4;
                        } else {
                            b[(offset + 2)] = 102;
                            len = 3;
                        }
                    }
                }
            } else {
                b[offset] = 58;
                b[(offset + 1)] = (byte) (101 - -val / 100000000);
                int x = -val % 100;

                if (x != 0) {
                    b[(offset + 2)] = (byte) (101 - -val % 100000000 / 1000000);
                    b[(offset + 3)] = (byte) (101 - -val % 1000000 / 10000);
                    b[(offset + 4)] = (byte) (101 - -val % 10000 / 100);
                    b[(offset + 5)] = (byte) (101 - x);
                    b[(offset + 6)] = 102;
                    len = 7;
                } else {
                    x = -val % 10000 / 100;

                    if (x != 0) {
                        b[(offset + 2)] = (byte) (101 - -val % 100000000 / 1000000);
                        b[(offset + 3)] = (byte) (101 - -val % 1000000 / 10000);
                        b[(offset + 4)] = (byte) (101 - x);
                        b[(offset + 5)] = 102;
                        len = 6;
                    } else {
                        x = -val % 1000000 / 10000;

                        if (x != 0) {
                            b[(offset + 2)] = (byte) (101 - -val % 100000000 / 1000000);
                            b[(offset + 3)] = (byte) (101 - x);
                            b[(offset + 4)] = 102;
                            len = 5;
                        } else {
                            x = -val % 100000000 / 1000000;

                            if (x != 0) {
                                b[(offset + 2)] = (byte) (101 - x);
                                b[(offset + 3)] = 102;
                                len = 4;
                            } else {
                                b[(offset + 2)] = 102;
                                len = 3;
                            }
                        }
                    }
                }

            }

        } else if (val < 100) {
            b[offset] = -63;
            b[(offset + 1)] = (byte) (val + 1);
            len = 2;
        } else if (val < 10000) {
            b[offset] = -62;
            b[(offset + 1)] = (byte) (val / 100 + 1);
            int x = val % 100;

            if (x != 0) {
                b[(offset + 2)] = (byte) (x + 1);
                len = 3;
            } else {
                len = 2;
            }
        } else if (val < 1000000) {
            b[offset] = -61;
            b[(offset + 1)] = (byte) (val / 10000 + 1);
            int x = val % 100;

            if (x != 0) {
                b[(offset + 2)] = (byte) (val % 10000 / 100 + 1);
                b[(offset + 3)] = (byte) (x + 1);
                len = 4;
            } else {
                x = val % 10000 / 100;

                if (x != 0) {
                    b[(offset + 2)] = (byte) (x + 1);
                    len = 3;
                } else {
                    len = 2;
                }
            }
        } else if (val < 100000000) {
            b[offset] = -60;
            b[(offset + 1)] = (byte) (val / 1000000 + 1);
            int x = val % 100;

            if (x != 0) {
                b[(offset + 2)] = (byte) (val % 1000000 / 10000 + 1);
                b[(offset + 3)] = (byte) (val % 10000 / 100 + 1);
                b[(offset + 4)] = (byte) (x + 1);
                len = 5;
            } else {
                x = val % 10000 / 100;

                if (x != 0) {
                    b[(offset + 2)] = (byte) (val % 1000000 / 10000 + 1);
                    b[(offset + 3)] = (byte) (x + 1);
                    len = 4;
                } else {
                    x = val % 1000000 / 10000;

                    if (x != 0) {
                        b[(offset + 2)] = (byte) (x + 1);
                        len = 3;
                    } else {
                        len = 2;
                    }
                }
            }
        } else {
            b[offset] = -59;
            b[(offset + 1)] = (byte) (val / 100000000 + 1);
            int x = val % 100;

            if (x != 0) {
                b[(offset + 2)] = (byte) (val % 100000000 / 1000000 + 1);
                b[(offset + 3)] = (byte) (val % 1000000 / 10000 + 1);
                b[(offset + 4)] = (byte) (val % 10000 / 100 + 1);
                b[(offset + 5)] = (byte) (x + 1);
                len = 6;
            } else {
                x = val % 10000 / 100;

                if (x != 0) {
                    b[(offset + 2)] = (byte) (val % 100000000 / 1000000 + 1);
                    b[(offset + 3)] = (byte) (val % 1000000 / 10000 + 1);
                    b[(offset + 4)] = (byte) (x + 1);
                    len = 5;
                } else {
                    x = val % 1000000 / 10000;

                    if (x != 0) {
                        b[(offset + 2)] = (byte) (val % 100000000 / 1000000 + 1);
                        b[(offset + 3)] = (byte) (x + 1);
                        len = 4;
                    } else {
                        x = val % 100000000 / 1000000;

                        if (x != 0) {
                            b[(offset + 2)] = (byte) (x + 1);
                            len = 3;
                        } else {
                            len = 2;
                        }
                    }
                }
            }

        }

        b[byteoffset] = (byte) len;
        bindIndicators[indoffset] = 0;
        bindIndicators[lenoffset] = (short) (len + 1);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.IntBinder JD-Core Version: 0.6.0
 */