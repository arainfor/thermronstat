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
