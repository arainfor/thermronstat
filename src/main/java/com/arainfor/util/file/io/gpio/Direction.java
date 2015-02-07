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
		public String get() {
			return "in";
		}
	},
	OUT() {
		public String get() {
			return "out";
		}
	}
	;
	
	public static Direction getValue(int value) {
		return Direction.values()[value];
	}

    public abstract String get();

    public String toString() {
        return new String(get());
    }

}
