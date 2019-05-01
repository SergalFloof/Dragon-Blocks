package com.dragonblocks.util.slope.type;

import com.dragonblocks.data.Slope;
import com.dragonblocks.util.slope.SlopeType;

public class WedgeSide implements SlopeType {

	@Override
	public Slope getNextSlopeType(Slope slope) {
	    int slopeID = slope.slopeID;
		slopeID += 8;
		return Slope.getSlopeById(slopeID);
	}
}
