package com.acuitybotting.path_finding.xtea;

/**
 * Created by Zachary Herridge on 6/25/2018.
 */
public class CollisionBuilder {

    public static void padMap(int[][][] map) {
        int var1;
        for (int var10000 = var1 = 0; var10000 < 4; var10000 = var1) {
            int var2;
            for (var10000 = var2 = 0; var10000 < 104; var10000 = var2) {
                int var3;
                for (var10000 = var3 = 0; var10000 < 104; var10000 = var3) {
                    if (var2 == 0 || var2 >= 99 || var3 == 0 || var3 >= 99) {
                        map[var1][var2][var3] = -1;
                    }

                    ++var3;
                }

                ++var2;
            }

            ++var1;
        }
    }

    public static void method16862(int[][][] var0, int[][][] var1) {
        int var3;
        for (int var10000 = var3 = 0; var10000 < 4; var10000 = var3) {
            int var4;
            for (var10000 = var4 = 0; var10000 < 64; var10000 = var4) {
                int var5;
                for (var10000 = var5 = 0; var10000 < 64; var10000 = var5) {
                    if ((var0[var3][var4][var5] & 1) == 1) {
                        int var6 = var3;
                        if ((var0[1][var4][var5] & 2) == 2) {
                            var6 = var3 - 1;
                        }

                        if (var6 >= 0) {
                            method16858(var1, var6, var4, var5, 2097152);
                        }
                    }

                    ++var5;
                }

                ++var4;
            }

            ++var3;
        }

    }

    public static void method16856(int[][][] var0, int var1, int var2, int var3, int var4, int var5, boolean var6, boolean var7) {
        int var8 = 256;
        if (var6) {
            var8 |= 131072;
        }

        if (var7) {
            var8 |= 1073741824;
        }

        int var9;
        for (int var10000 = var9 = var2; var10000 < var2 + var4; var10000 = var9) {
            int var10;
            if (var9 >= 0 && var9 < var0[var1].length) {
                for (var10000 = var10 = var3; var10000 < var3 + var5; var10000 = var10) {
                    if (var10 >= 0 && var10 < var0[var1][var9].length) {
                        method16858(var0, var1, var9, var10, var8);
                    }

                    ++var10;
                }
            }

            ++var9;
        }

    }

    public static void method16851(int[][][] var0, int var1, int var2, int var3) {
        method16858(var0, var1, var2, var3, 262144);
    }

    public static void method16860(int[][][] var0, int var1, int var2, int var3, int var4, int direction, boolean var6, boolean var7) {
        int var8 = direction;
        int var9;
        if (var4 == 0) {
            var9 = (var8 << 1) - 1 & 7;
            method16858(var0, var1, var2, var3, method16859(var6, var7) << var9);
            method16858(var0, var1, var2 + method16855(var9), var3 + method16857(var9), method16859(var6, var7) << method16863(var9));
        }

        if (var4 == 1 || var4 == 3) {
            var9 = var8 << 1 & 7;
            method16858(var0, var1, var2, var3, method16859(var6, var7) << var9);
            method16858(var0, var1, var2 + method16855(var9), var3 + method16857(var9), method16859(var6, var7) << method16863(var9));
        }

        if (var4 == 2) {
            var9 = (var8 << 1) + 1 & 7;
            int var10 = (var8 << 1) - 1 & 7;
            method16858(var0, var1, var2, var3, method16859(var6, var7) << (var9 | var10));
            method16858(var0, var1, var2 + method16855(var9), var3 + method16857(var9), method16859(var6, var7) << method16863(var9));
            method16858(var0, var1, var2 + method16855(var10), var3 + method16857(var10), method16859(var6, var7) << method16863(var10));
        }

        if (var6) {
            if (var4 == 0) {
                if (var8 == 0) {
                    method16858(var0, var1, var2, var3, 65536);
                    method16858(var0, var1, var2 - 1, var3, 4096);
                }

                if (var8 == 1) {
                    method16858(var0, var1, var2, var3, 1024);
                    method16858(var0, var1, var2, var3 + 1, 16384);
                }

                if (var8 == 2) {
                    method16858(var0, var1, var2, var3, 4096);
                    method16858(var0, var1, var2 + 1, var3, 65536);
                }

                if (var8 == 3) {
                    method16858(var0, var1, var2, var3, 16384);
                    method16858(var0, var1, var2, var3 - 1, 1024);
                }
            }

            if (var4 == 1 || var4 == 3) {
                if (var8 == 0) {
                    method16858(var0, var1, var2, var3, 512);
                    method16858(var0, var1, var2 - 1, var3 + 1, 8192);
                }

                if (var8 == 1) {
                    method16858(var0, var1, var2, var3, 2048);
                    method16858(var0, var1, 1 + var2, 1 + var3, '耀');
                }

                if (var8 == 2) {
                    method16858(var0, var1, var2, var3, 8192);
                    method16858(var0, var1, var2 + 1, var3 - 1, 512);
                }

                if (var8 == 3) {
                    method16858(var0, var1, var2, var3, '耀');
                    method16858(var0, var1, var2 - 1, var3 - 1, 2048);
                }
            }

            if (var4 == 2) {
                if (var8 == 0) {
                    method16858(var0, var1, var2, var3, 66560);
                    method16858(var0, var1, var2 - 1, var3, 4096);
                    method16858(var0, var1, var2, 1 + var3, 16384);
                }

                if (var8 == 1) {
                    method16858(var0, var1, var2, var3, 5120);
                    method16858(var0, var1, var2, 1 + var3, 16384);
                    method16858(var0, var1, 1 + var2, var3, 65536);
                }

                if (var8 == 2) {
                    method16858(var0, var1, var2, var3, 20480);
                    method16858(var0, var1, var2 + 1, var3, 65536);
                    method16858(var0, var1, var2, var3 - 1, 1024);
                }

                if (var8 == 3) {
                    method16858(var0, var1, var2, var3, 81920);
                    method16858(var0, var1, var2, var3 - 1, 1024);
                    method16858(var0, var1, var2 - 1, var3, 4096);
                }
            }
        }

        if (var7) {
            if (var4 == 0) {
                if (var8 == 0) {
                    method16858(var0, var1, var2, var3, 536870912);
                    method16858(var0, var1, var2 - 1, var3, 33554432);
                }

                if (var8 == 1) {
                    method16858(var0, var1, var2, var3, 8388608);
                    method16858(var0, var1, var2, var3 + 1, 134217728);
                }

                if (var8 == 2) {
                    method16858(var0, var1, var2, var3, 33554432);
                    method16858(var0, var1, var2 + 1, var3, 536870912);
                }

                if (var8 == 3) {
                    method16858(var0, var1, var2, var3, 134217728);
                    method16858(var0, var1, var2, var3 - 1, 8388608);
                }
            }

            if (var4 == 1 || var4 == 3) {
                if (var8 == 0) {
                    method16858(var0, var1, var2, var3, 4194304);
                    method16858(var0, var1, var2 - 1, var3 + 1, 67108864);
                }

                if (var8 == 1) {
                    method16858(var0, var1, var2, var3, 16777216);
                    method16858(var0, var1, var2 + 1, var3 + 1, 268435456);
                }

                if (var8 == 2) {
                    method16858(var0, var1, var2, var3, 67108864);
                    method16858(var0, var1, var2 + 1, var3 - 1, 4194304);
                }

                if (var8 == 3) {
                    method16858(var0, var1, var2, var3, 268435456);
                    method16858(var0, var1, var2 - 1, var3 - 1, 16777216);
                }
            }

            if (var4 == 2) {
                if (var8 == 0) {
                    method16858(var0, var1, var2, var3, 545259520);
                    method16858(var0, var1, var2 - 1, var3, 33554432);
                    method16858(var0, var1, var2, var3 + 1, 134217728);
                }

                if (var8 == 1) {
                    method16858(var0, var1, var2, var3, 41943040);
                    method16858(var0, var1, var2, var3 + 1, 134217728);
                    method16858(var0, var1, var2 + 1, var3, 536870912);
                }

                if (var8 == 2) {
                    method16858(var0, var1, var2, var3, 167772160);
                    method16858(var0, var1, var2 + 1, var3, 536870912);
                    method16858(var0, var1, var2, var3 - 1, 8388608);
                }

                if (var8 == 3) {
                    method16858(var0, var1, var2, var3, 671088640);
                    method16858(var0, var1, var2, var3 - 1, 8388608);
                    method16858(var0, var1, var2 - 1, var3, 33554432);
                }
            }
        }
    }

    private static void method16858(int[][][] var0, int var1, int var2, int var3, int var4) {
        if (var2 >= 0 && var2 < 104 && var3 >= 0 && var3 < 104) {
            var0[var1][var2][var3] |= var4;
        }
    }

    private static int method16859(boolean var0, boolean var1) {
        return 1;
    }

    private static int method16855(int var0) {
        switch (var0) {
            case 0:
            case 6:
            case 7:
                return -1;
            case 1:
            case 5:
                return 0;
            case 2:
            case 3:
            case 4:
                return 1;
            default:
                return 0;
        }
    }

    private static int method16857(int var0) {
        switch (var0) {
            case 0:
            case 1:
            case 2:
                return 1;
            case 3:
            case 7:
                return 0;
            case 4:
            case 5:
            case 6:
                return -1;
            default:
                return 0;
        }
    }

    private static int method16863(int var0) {
        return var0 + 4 & 7;
    }

    private static boolean blocked(int var0, int... flagOffsets) {
        int[] var2 = flagOffsets;
        int var3 = flagOffsets.length;

        int var4;
        for (int var10000 = var4 = 0; var10000 < var3; var10000 = var4) {
            int $ = var2[var4];
            if ((var0 >> $ & 1) == 1) {
                return true;
            }

            ++var4;
        }

        return false;
    }

    private interface Offsets {
        /**
         * All north boundary flag offsets
         */
        int[] N = {1, 10, 23};

        /**
         * All east boundary flag offsets
         */
        int[] E = {3, 12, 25};

        /**
         * All south boundary flag offsets
         */
        int[] S = {5, 14, 27};

        /**
         * All west boundary flag offsets
         */
        int[] W = {7, 16, 29};

        /**
         * All north-east boundary flag offsets
         */
        int[] NE = {2, 11, 24};

        /**
         * All north-west boundary flag offsets
         */
        int[] NW = {0, 9, 22};

        /**
         * All south-east boundary flag offsets
         */
        int[] SE = {4, 13, 26};

        /**
         * All south-west boundary flag offsets
         */
        int[] SW = {6, 15, 28};

        /**
         * All object boundary flag offsets
         */
        int[] O = {8, 17, 18, 21};
    }

}
