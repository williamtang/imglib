package net.imglib2;

import net.imglib2.function.BijectiveFunction;
import net.imglib2.type.numeric.real.DoubleType;

public interface Axis<T extends BijectiveFunction<DoubleType, DoubleType>> {

	T getFunction();

	double getRelativeMeasure(double absoluteMeasure);

	double getAbsoluteMeasure(double relativeMeasure);

	void setUnit(String unit);

	String getUnit();
}
