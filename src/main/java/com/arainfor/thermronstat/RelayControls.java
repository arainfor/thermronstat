package com.arainfor.thermronstat;

import java.util.ArrayList;

/**
 * Created by arainfor on 1/10/15.
 */
public class RelayControls {

    public ArrayList<RelayDef> heatStage1() {
        ArrayList<RelayDef> relaysEnergized = new ArrayList<RelayDef>();
        relaysEnergized.add(RelayDef.G);
        relaysEnergized.add(RelayDef.Y1);
        return relaysEnergized;
    }

    public ArrayList<RelayDef> heatStage2() {
        ArrayList<RelayDef> relaysEnergized = new ArrayList<RelayDef>();
        relaysEnergized.add(RelayDef.G);
        relaysEnergized.add(RelayDef.Y1);
        relaysEnergized.add(RelayDef.Y2);
        return relaysEnergized;
    }

    public ArrayList<RelayDef> coolStage1() {
        ArrayList<RelayDef> relaysEnergized = new ArrayList<RelayDef>();
        relaysEnergized.add(RelayDef.G);
        relaysEnergized.add(RelayDef.Y1);
        relaysEnergized.add(RelayDef.O);
        return relaysEnergized;
    }

}
