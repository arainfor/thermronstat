/**
 * Copyright 2014-2015
 * Alan Rainford arainfor@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        return getName();
    }
}
