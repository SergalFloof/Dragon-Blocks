package com.dragonblocks.util.slope.type;

import com.dragonblocks.data.Slope;
import com.dragonblocks.util.slope.SlopeType;

public class WedgeExt implements SlopeType {

	@Override
	public Slope getNextSlopeType(Slope slope) {
	    int slopeID = slope.slopeID;
		if (slope.isPositive) {
            slopeID -= 4;
        } else {
            slopeID += 12;
        }
		return Slope.getSlopeById(slopeID);
	}

}
