package powercraft.api.utils;

import java.io.Serializable;

import powercraft.api.interfaces.PC_INBT;

public interface PC_Vec<t extends Number, ret extends PC_Vec> extends PC_INBT, Serializable {

	public t getX();

	public t getY();

	public t getZ();

	public ret setX(Number x);

	public ret setY(Number y);

	public ret setZ(Number z);

	public ret setTo(PC_Vec vec);

	public ret setTo(Number x, Number y, Number z);

	public ret add(PC_Vec vec);

	public ret add(Number n);

	public ret add(Number x, Number y, Number z);

	public ret offset(PC_Vec vec);

	public ret offset(Number n);

	public ret offset(Number x, Number y, Number z);

	public ret sub(PC_Vec vec);

	public ret sub(Number n);

	public ret sub(Number x, Number y, Number z);

	public ret mul(PC_Vec vec);

	public ret mul(Number n);

	public ret mul(Number x, Number y, Number z);

	public ret div(PC_Vec vec);

	public ret div(Number n);

	public ret div(Number x, Number y, Number z);

	public double length();

	public double distanceTo(PC_Vec vec);

	public double distanceTo(Number x, Number y, Number z);

	public ret normalize();

	public ret clamp(PC_Vec min, PC_Vec max);

	public ret clamp(PC_Vec min, t max);

	public ret clamp(t min, PC_Vec max);

	public ret clamp(t min, t max);

	public ret max(PC_Vec max);

	public ret max(t max);

	public ret min(PC_Vec min);

	public ret min(t min);

	public ret copy();

}
