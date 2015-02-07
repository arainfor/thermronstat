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
//		if (_value != 17 && _value != 21 && _value != 22 && _value != 23
//				&& _value != 14 && _value != 15 && _value != 18
//				&& _value != 24 && _value != 25)
//			throw new NumberFormatException("GPIO pin " + _value + " not supported!");
	}
	
	public int getValue() {
		return _value;
	}
	
	public String getName() {
		return Integer.toString(_value);
	}
	
	public String toString() {
        return "Pin: " + getName();
    }
}
