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

package net.imglib2.axis;

import net.imglib2.Axis;
import net.imglib2.ScalingFunction;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.type.numeric.real.DoubleType;

// TODO - if we keep scale and offset concepts for an Axis then pull the
// various implementations of get/set offset/scale into here from implementers
// of Axis.

/**
 * 
 * @author Barry DeZonia
 *
 */
public abstract class AbstractAxis implements Axis
{

	private ScalingFunction function;
	private String unitName = null;
	private String label = null;
	private final DoubleType abs = new DoubleType();
	private final DoubleType rel = new DoubleType();
	private AxisType type = Axes.UNKNOWN;
	
	protected void setFunction(ScalingFunction func) {
		this.function = func;
	}
	
	@Override
	synchronized public double getPositionalMeasure(double calibratedMeasure) {
		abs.setReal(calibratedMeasure);
		function.computeInverse(abs, rel);
		return rel.get();
	}
	
	@Override
	synchronized public double getCalibratedMeasure(double positionalMeasure) {
		rel.setReal(positionalMeasure);
		function.compute(rel, abs);
		return abs.get();
	}
	
	@Override
	public String getUnit() {
		return unitName;
	}
	
	@Override
	public void setUnit(String unit) {
		unitName = unit;
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public void setLabel(String label) {
		this.label = label;
		this.type = Axes.get(label);
	}
	
	@Override
	public AxisType getType() {
		return type;
	}

	@Override
	public double getOrigin() {
		return getCalibratedMeasure(0);
	}

	@Override
	public double getScale(double p1, double p2) {
		return (getCalibratedMeasure(p2) - getCalibratedMeasure(p1)) / (p2 - p1);
	}

	// -- helpers --

	protected boolean same(double d1, double d2) {
		if (Double.isNaN(d1)) return Double.isNaN(d2);
		return d1 == d2;
	}

	protected boolean same(String s1, String s2) {
		if (s1 == null) return s2 == null;
		return s1.equals(s2);
	}
}
