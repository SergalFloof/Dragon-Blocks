package com.dragonblocks.util.slope.type;

import com.dragonblocks.data.Slope;
import com.dragonblocks.util.slope.SlopeType;

public class Prism2P implements SlopeType {

	@Override
	public Slope getNextSlopeType(Slope slope) {
	    int slopeID = slope.slopeID;
		if (slope.equals(Slope.PRISM_2P_POS_NS) || slope.equals(Slope.PRISM_2P_POS_WE)) {
            slopeID = Slope.ID_PRISM_2P_POS_SE;
        } else {
            slopeID = Slope.ID_PRISM_3P_POS_NWE;
        }
		return Slope.getSlopeById(slopeID);
	}

}
