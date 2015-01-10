package com.arainfor.thermronstat;

/**
 * Created by arainfor on 12/27/14.
 */
public enum RelayOutputs {

    G("Fan"),
    Y1("Stage1"),
    Y2("Stage2"),
    W("AuxHeat"),
    O("ReversingValve");

    private String name;

    RelayOutputs(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
