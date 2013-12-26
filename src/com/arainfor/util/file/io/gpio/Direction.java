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
			return "IN";
		}
	},
	OUT() {
		public String get() {
			return "OUT";
		}
	}
	;
	
	public static Direction getValue(int value) {
		return Direction.values()[value];
	}
	
	public abstract String get(); 
	
}
