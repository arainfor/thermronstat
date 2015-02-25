package com.arainfor.thermostat;

/**
 * Created by arainfor on 12/27/14.
 */
public enum RelayDef {

    G("Fan"),
    Y1("Stage1"),
    Y2("Stage2"),
    W("AuxHeat"),
    O("ReversingValve"),;

    private final String name;

    RelayDef(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
