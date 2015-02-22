package com.arainfor.util.file.io.gpio;

/**
 * Created by arainfor on 2/22/15.
 */
public enum Value {

    SET() {
        public String value() {
            return "1";
        }
    },
    UNSET() {
        public String value() {
            return "0";
        }
    }
    ;

    public static Direction getValue(int value) {
        return Direction.values()[value];
    }

    public abstract String value();

    public String toString() {
        return value();
    }


}
