package net.imglib2;

import net.imglib2.function.BijectiveFunction;
import net.imglib2.type.numeric.real.DoubleType;

public interface ScalingFunction extends BijectiveFunction<DoubleType,DoubleType> {
	double getOffset();
	double getScale();
	void setOffset(double offset);
	void setScale(double scale);
}
