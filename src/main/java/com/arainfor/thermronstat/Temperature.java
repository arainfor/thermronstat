package com.arainfor.thermronstat;

import java.text.NumberFormat;

/**
 * Created by arainfor on 1/10/15.
 *
 * 17-feb-15 akr - Add getValueString methods.
 *
 */
public class Temperature {

    private final int index;
    private final String name;
    private Double value = Double.POSITIVE_INFINITY;

    public Temperature(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public static void main(String[] args) throws Exception {
        Temperature temp = new Temperature(0, "Alan");
        System.out.println(temp);

    }

    public static String getValueString(Double value) {
        NumberFormat df = NumberFormat.getInstance();
        df.setMaximumFractionDigits(1);
        df.setParseIntegerOnly(false);
        return df.format(value);
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getValueString() {
        return getValueString(value);
    }

    public String toString() {
        return name + StringConstants.KeyValueDelimiter + getValueString();
    }
}
