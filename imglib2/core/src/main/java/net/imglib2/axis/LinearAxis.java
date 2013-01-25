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

import net.imglib2.function.scaling.LinearScalingFunction;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class LinearAxis extends AbstractAxis {

	private final LinearScalingFunction function;

	public LinearAxis(double offset, double scale) {
		this.function = new LinearScalingFunction(offset, scale);
		setFunction(function);
	}
	
	@Override
	public LinearAxis copy() {
		double offset = function.getOffset();
		double scale = function.getScale();
		LinearAxis axis = new LinearAxis(offset, scale);
		axis.setLabel(getLabel());
		axis.setUnit(getUnit());
		return axis;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof LinearAxis) {
			LinearAxis axis = (LinearAxis) other;
			if (!same(getOffset(), axis.getOffset())) return false;
			if (!same(getScale(), axis.getScale())) return false;
			if (!same(getLabel(), axis.getLabel())) return false;
			if (!same(getUnit(), axis.getUnit())) return false;
			return true;
		}
		return false;
	}

	public double getOffset() {
		return function.getOffset();
	}

	public void setOffset(double offset) {
		function.setOffset(offset);
	}

	public double getScale() {
		return function.getScale();
	}

	public void setScale(double scale) {
		function.setScale(scale);
	}

}
