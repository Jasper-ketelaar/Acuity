package com.acuitybotting.path_finding.rs.utils;

public class ObjectConfigs {

    public static int compile(int type, int orientation, boolean solid) {
        orientation &= 0x3;
        type &= 0x1f;
        int base = (orientation << 6) | type;
        if (solid) {
            base |= 0x100;
        }
        return base & 0xff;
    }

    public static StubType getType(int cfg) { //22 types observed
        return StubType.get(cfg & 0x1f);
    }

    public static int getOrientation(int cfg) {
        return cfg >> 6 & 0x3; //4 possible values
    }

    //For ground items, this flag is checked to see where the object should be placed
    //relative to the items height
    public static boolean isSolid(int cfg) {
        return (cfg & Mask.SOLID) == Mask.SOLID;
    }

    public enum StubType {

        //Boundary types
        STRAIGHT_BOUNDARY,
        JOINT_BOUNDARY,
        CORNER_BOUNDARY,
        UNKNOWN_BOUNDARY, //TODO type 3

        //BoundaryDecor types
        STRAIGHT_BOUNDARY_DECOR,
        STRAIGHT_TAN_BOUNDARY_DECOR, //straight decoration tangent to the boundary
        DIAG_TAN_BOUNDARY_DECOR, //diagonal decoration tangent to the boundary
        DIAG_BOUNDARY_DECOR,
        DOUBLE_SIDED_BOUNDARY_DECOR, //this applies to decorations like windows

        //this applies to SceneElements.
        //I am unsure why some of these are treated as SceneElement when they would
        //have more meaning as TileDecors or BoundaryDecors
        DIAG_BOUNDARY, //not quite sure why this applies to SceneElements and not /actual/ boundarys
        PRIMARY_OBJECT, //a "normal" object such as a tree, rock, bank chest
        PRIMARY_TILE_DECOR, //some flowers

        //roofs
        RIDGE,
        JOINT_RIDGE,
        FLAT_JOINT_RIDGE,
        VALLEY_JOINT_RIDGE,
        HIP_JOINT_RIDGE,
        GABLE_RIDGE,

        EAVE_RIDGE,
        EAVE_JOINT_RIDGE,
        EAVE_VALLEY_JOINT_RIDGE,
        EAVE_HIP_JOINT_RIDGE,

        //TileDecor types, only 1
        TILE_DECOR; //flowers, agility obstacles, bridges

        public static StubType get(int id) {
            StubType[] values = StubType.values();
            if (id >= 0 && id < values.length) {
                return values[id];
            }
            throw new IllegalStateException("Should not happen");
        }

        public int getId() {
            return super.ordinal();
        }
    }

    public interface Mask {
        int SOLID = 0x100;
    }
}