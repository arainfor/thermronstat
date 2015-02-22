/**
 * 
 */
package com.arainfor.util.file.io.gpio;

/**
 * @author arainfor
 *
 */
public enum Direction {

	IN() {
		public String value() {
			return "in";
		}
	},
	OUT() {
		public String value() {
			return "out";
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
