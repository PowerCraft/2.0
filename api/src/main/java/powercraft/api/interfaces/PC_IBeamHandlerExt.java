package powercraft.api.interfaces;

import powercraft.api.PC_BeamTracer;
import powercraft.api.utils.PC_VecI;

public interface PC_IBeamHandlerExt extends PC_IBeamHandler {

	public boolean onEmptyBlockHit(PC_BeamTracer beamTracer, PC_VecI coord);

}
