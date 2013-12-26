/**
 * 
 */
package com.arainfor.util.file.io.gpio;

/**
 * @author arainfor
 *
 */
public class Pin {
	
	private int _value = 0;
	
	public Pin(String value) throws NumberFormatException {
		
		this(Integer.parseInt(value));
	}
	
	public Pin(int value)  throws NumberFormatException {
		_value = value;
		if (_value != 17)
			throw new NullPointerException("GPIO 17 is only supported right now!");
	}
	
	public int getValue() {
		return _value;
	}
	
	public String getName() {
		return Integer.toString(_value);
	}
	
	public String toString() {
		return getName();
	}
}
