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

    public static void applyNonLoadedFlags(int[][][] flags, int[][][] tileSettings) {
        int var3;
        for (int var10000 = var3 = 0; var10000 < 4; var10000 = var3) {
            int var4;
            for (var10000 = var4 = 0; var10000 < 64; var10000 = var4) {
                int var5;
                for (var10000 = var5 = 0; var10000 < 64; var10000 = var5) {
                    if ((flags[var3][var4][var5] & 1) == 1) {
                        int var6 = var3;
                        if ((flags[1][var4][var5] & 2) == 2) {
                            var6 = var3 - 1;
                        }

                        if (var6 >= 0) {
                            addFlag(tileSettings, var6, var4, var5, 2097152);
                        }
                    }

                    ++var5;
                }

                ++var4;
            }

            ++var3;
        }

    }

    public static void applyLargeObjectFlags(int[][][] map, int plane, int regionX, int regionY, int width, int height, boolean notSolid, boolean notImpenetrable) {
        int flag = 256;
        if (notSolid) {
            flag |= 131072;
        }

        if (notImpenetrable) {
            flag |= 1073741824;
        }

        int localRegionX;
        for (int index = localRegionX = regionX; index < regionX + width; index = localRegionX) {
            int localRegionY;
            if (localRegionX >= 0 && localRegionX < map[plane].length) {
                for (index = localRegionY = regionY; index < regionY + height; index = localRegionY) {
                    if (localRegionY >= 0 && localRegionY < map[plane][localRegionX].length) {
                        addFlag(map, plane, localRegionX, localRegionY, flag);
                    }

                    ++localRegionY;
                }
            }

            ++localRegionX;
        }

    }

    public static void applyObjectFlag(int[][][] flags, int plane, int regionX, int regionY) {
        addFlag(flags, plane, regionX, regionY, 262144);
    }

    public static void applyWallFlags(int[][][] flags, int plane, int regionX, int regionY, int type, int orientation, boolean notSolid, boolean notImpenetrable) {
        int orientationByte;
        if (type == 0) {
            orientationByte = (orientation << 1) - 1 & 7;
            addFlag(flags, plane, regionX, regionY, getOne(notSolid, notImpenetrable) << orientationByte);
            addFlag(flags, plane, regionX + getDirectionX(orientationByte), regionY + getDirectionY(orientationByte), getOne(notSolid, notImpenetrable) << method16863(orientationByte));
        }

        if (type == 1 || type == 3) {
            orientationByte = orientation << 1 & 7;
            addFlag(flags, plane, regionX, regionY, getOne(notSolid, notImpenetrable) << orientationByte);
            addFlag(flags, plane, regionX + getDirectionX(orientationByte), regionY + getDirectionY(orientationByte), getOne(notSolid, notImpenetrable) << method16863(orientationByte));
        }

        if (type == 2) {
            orientationByte = (orientation << 1) + 1 & 7;
            int orientationByte2 = (orientation << 1) - 1 & 7;
            addFlag(flags, plane, regionX, regionY, getOne(notSolid, notImpenetrable) << (orientationByte | orientationByte2));
            addFlag(flags, plane, regionX + getDirectionX(orientationByte), regionY + getDirectionY(orientationByte), getOne(notSolid, notImpenetrable) << method16863(orientationByte));
            addFlag(flags, plane, regionX + getDirectionX(orientationByte2), regionY + getDirectionY(orientationByte2), getOne(notSolid, notImpenetrable) << method16863(orientationByte2));
        }

        if (notSolid) {
            if (type == 0) {
                if (orientation == 0) {
                    addFlag(flags, plane, regionX, regionY, 65536);
                    addFlag(flags, plane, regionX - 1, regionY, 4096);
                }

                if (orientation == 1) {
                    addFlag(flags, plane, regionX, regionY, 1024);
                    addFlag(flags, plane, regionX, regionY + 1, 16384);
                }

                if (orientation == 2) {
                    addFlag(flags, plane, regionX, regionY, 4096);
                    addFlag(flags, plane, regionX + 1, regionY, 65536);
                }

                if (orientation == 3) {
                    addFlag(flags, plane, regionX, regionY, 16384);
                    addFlag(flags, plane, regionX, regionY - 1, 1024);
                }
            }

            if (type == 1 || type == 3) {
                if (orientation == 0) {
                    addFlag(flags, plane, regionX, regionY, 512);
                    addFlag(flags, plane, regionX - 1, regionY + 1, 8192);
                }

                if (orientation == 1) {
                    addFlag(flags, plane, regionX, regionY, 2048);
                    addFlag(flags, plane, 1 + regionX, 1 + regionY, '耀');
                }

                if (orientation == 2) {
                    addFlag(flags, plane, regionX, regionY, 8192);
                    addFlag(flags, plane, regionX + 1, regionY - 1, 512);
                }

                if (orientation == 3) {
                    addFlag(flags, plane, regionX, regionY, '耀');
                    addFlag(flags, plane, regionX - 1, regionY - 1, 2048);
                }
            }

            if (type == 2) {
                if (orientation == 0) {
                    addFlag(flags, plane, regionX, regionY, 66560);
                    addFlag(flags, plane, regionX - 1, regionY, 4096);
                    addFlag(flags, plane, regionX, 1 + regionY, 16384);
                }

                if (orientation == 1) {
                    addFlag(flags, plane, regionX, regionY, 5120);
                    addFlag(flags, plane, regionX, 1 + regionY, 16384);
                    addFlag(flags, plane, 1 + regionX, regionY, 65536);
                }

                if (orientation == 2) {
                    addFlag(flags, plane, regionX, regionY, 20480);
                    addFlag(flags, plane, regionX + 1, regionY, 65536);
                    addFlag(flags, plane, regionX, regionY - 1, 1024);
                }

                if (orientation == 3) {
                    addFlag(flags, plane, regionX, regionY, 81920);
                    addFlag(flags, plane, regionX, regionY - 1, 1024);
                    addFlag(flags, plane, regionX - 1, regionY, 4096);
                }
            }
        }

        if (notImpenetrable) {
            if (type == 0) {
                if (orientation == 0) {
                    addFlag(flags, plane, regionX, regionY, 536870912);
                    addFlag(flags, plane, regionX - 1, regionY, 33554432);
                }

                if (orientation == 1) {
                    addFlag(flags, plane, regionX, regionY, 8388608);
                    addFlag(flags, plane, regionX, regionY + 1, 134217728);
                }

                if (orientation == 2) {
                    addFlag(flags, plane, regionX, regionY, 33554432);
                    addFlag(flags, plane, regionX + 1, regionY, 536870912);
                }

                if (orientation == 3) {
                    addFlag(flags, plane, regionX, regionY, 134217728);
                    addFlag(flags, plane, regionX, regionY - 1, 8388608);
                }
            }

            if (type == 1 || type == 3) {
                if (orientation == 0) {
                    addFlag(flags, plane, regionX, regionY, 4194304);
                    addFlag(flags, plane, regionX - 1, regionY + 1, 67108864);
                }

                if (orientation == 1) {
                    addFlag(flags, plane, regionX, regionY, 16777216);
                    addFlag(flags, plane, regionX + 1, regionY + 1, 268435456);
                }

                if (orientation == 2) {
                    addFlag(flags, plane, regionX, regionY, 67108864);
                    addFlag(flags, plane, regionX + 1, regionY - 1, 4194304);
                }

                if (orientation == 3) {
                    addFlag(flags, plane, regionX, regionY, 268435456);
                    addFlag(flags, plane, regionX - 1, regionY - 1, 16777216);
                }
            }

            if (type == 2) {
                if (orientation == 0) {
                    addFlag(flags, plane, regionX, regionY, 545259520);
                    addFlag(flags, plane, regionX - 1, regionY, 33554432);
                    addFlag(flags, plane, regionX, regionY + 1, 134217728);
                }

                if (orientation == 1) {
                    addFlag(flags, plane, regionX, regionY, 41943040);
                    addFlag(flags, plane, regionX, regionY + 1, 134217728);
                    addFlag(flags, plane, regionX + 1, regionY, 536870912);
                }

                if (orientation == 2) {
                    addFlag(flags, plane, regionX, regionY, 167772160);
                    addFlag(flags, plane, regionX + 1, regionY, 536870912);
                    addFlag(flags, plane, regionX, regionY - 1, 8388608);
                }

                if (orientation == 3) {
                    addFlag(flags, plane, regionX, regionY, 671088640);
                    addFlag(flags, plane, regionX, regionY - 1, 8388608);
                    addFlag(flags, plane, regionX - 1, regionY, 33554432);
                }
            }
        }
    }

    private static void addFlag(int[][][] flags, int plane, int regionX, int regionY, int flag) {
        if (regionX >= 0 && regionX < 64 && regionY >= 0 && regionY < 64) {
            flags[plane][regionX][regionY] |= flag;
        }
    }

    private static int getOne(boolean var0, boolean var1) {
        return 1;
    }

    private static int getDirectionX(int var0) {
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

    private static int getDirectionY(int var0) {
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
}
