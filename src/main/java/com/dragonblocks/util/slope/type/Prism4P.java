package com.dragonblocks.util.slope.type;

import com.dragonblocks.data.Slope;
import com.dragonblocks.util.slope.SlopeType;

public class Prism4P implements SlopeType {

	@Override
	public Slope getNextSlopeType(Slope slope) {
	    int slopeID = slope.slopeID;
		slopeID = Slope.ID_PRISM_WEDGE_POS_N;
		return Slope.getSlopeById(slopeID);
	}

}
