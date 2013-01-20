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


package net.imglib2.function.scaling;

import net.imglib2.ScalingFunction;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class ExponentialScalingFunction extends AbstractScalingInfo
	implements ScalingFunction
{

	private double base;

	// NB - can match Math.exp() behavior by passing Math.E as base.
	
	public ExponentialScalingFunction(double offset, double scale, double base)
	{
		super(offset, scale);
		this.base = base;
	}

	@Override
	public void compute(DoubleType input, DoubleType output) {
		double value = offset + scale * Math.pow(base, input.get());
		output.setReal(value);
	}

	@Override
	public void computeInverse(DoubleType output, DoubleType input) {
		double value = log(base, ((output.get() - offset) / scale));
		output.setReal(value);
	}

	@Override
	public DoubleType createOutput() {
		return new DoubleType();
	}

	@Override
	public DoubleType createInput() {
		return new DoubleType();
	}

	@Override
	public ExponentialScalingFunction copy() {
		return new ExponentialScalingFunction(offset, scale, base);
	}

	public double getBase() {
		return base;
	}

	public void setBase(double base) {
		this.base = base;
	}

	// i.e. do a 6-based log via log(6, val);

	private double log(double logBase, double val) {
		return Math.log(val) / Math.log(logBase);
	}
}

