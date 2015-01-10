package com.arainfor.thermronstat;

import java.util.ArrayList;

/**
 * Created by arainfor on 1/10/15.
 */
public class RelayControls {

    public ArrayList<RelayOutputs> heatStage1() {
        ArrayList<RelayOutputs> relaysEnergized = new ArrayList<RelayOutputs>();
        relaysEnergized.add(RelayOutputs.G);
        relaysEnergized.add(RelayOutputs.Y1);
        return relaysEnergized;
    }

    public ArrayList<RelayOutputs> coolStage1() {
        ArrayList<RelayOutputs> relaysEnergized = new ArrayList<RelayOutputs>();
        relaysEnergized.add(com.arainfor.thermronstat.RelayOutputs.G);
        relaysEnergized.add(com.arainfor.thermronstat.RelayOutputs.Y1);
        relaysEnergized.add(com.arainfor.thermronstat.RelayOutputs.O);
        return relaysEnergized;
    }

}
