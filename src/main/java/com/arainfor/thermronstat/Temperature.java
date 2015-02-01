package com.arainfor.thermronstat;

import java.text.NumberFormat;

/**
 * Created by arainfor on 1/10/15.
 */
public class Temperature {

    private final int index;
    private final String name;
    private Double value = Double.MIN_VALUE;

    public Temperature(int index, String name) {
        this.index = index;
        this.name = name;
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

    public String toString() {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);

        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(": ");
        String temperature = Integer.toString(Integer.MIN_VALUE);

        temperature = nf.format(getValue());

        return new String(name + ": " + temperature);
    }

}
