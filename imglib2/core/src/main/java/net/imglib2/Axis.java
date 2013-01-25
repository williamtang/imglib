/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2;

import net.imglib2.meta.AxisType;

/**
 * 
 * @author Barry DeZonia
 *
 */
public interface Axis {

	// TODO - this next method is nice but is it necessary? Again do we just
	// support one direction and eliminate the reliance on BijectiveFunction or
	// not?

	double getPositionalMeasure(double calibratedMeasure);

	double getCalibratedMeasure(double positionalMeasure);

	void setUnit(String unit);

	String getUnit();
	
	void setLabel(String label);
	
	String getLabel();
	
	AxisType getType();
	
	Axis copy();

	// NOTE: as currently implemented most or all Axes have a scale and an offset.
	// However I don't think that belongs in this interface because it makes
	// impossible the definition of nonlinear axes.

	// TODO eliminate these methods before finalization. What should
	// happen is api user can estimate linearity over a range.
	// offset = axis.calVal(rStart);
	// scale = (axis.calVal(rEnd) - axis.calVal(rStart)) / (rEnd - rStart);
	// This would work for all axis types with varying amounts of error. For a
	// LinearAxis there would be no error.
	// Note there are such methods in AxisUtils

	double getOffset();

	void setOffset(double val);

	double getScale();

	void setScale(double val);
}
