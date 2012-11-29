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

package net.imglib2.io.img.virtual.file;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ComplexType;

/**
 * @author Barry DeZonia
 * @param <T>
 */
public class FileBackedType<T extends NativeType<T> & ComplexType<T>>
	implements ComplexType<T>
{

	private final FileBackedImg<T> img;
	private T type;
	private long pos;

	public FileBackedType(FileBackedImg<T> img) {
		this.img = img;
		this.type = img.backingType().createVariable();
		this.pos = 0;
	}

	@Override
	public void add(T c) {
		type.add(c);
		img.setValue(pos, type);
	}

	@Override
	public void sub(T c) {
		type.sub(c);
		img.setValue(pos, type);
	}

	@Override
	public void mul(T c) {
		type.mul(c);
		img.setValue(pos, type);
	}

	@Override
	public void div(T c) {
		type.div(c);
		img.setValue(pos, type);
	}

	@Override
	public void setZero() {
		type.setZero();
		img.setValue(pos, type);
	}

	@Override
	public void setOne() {
		type.setOne();
		img.setValue(pos, type);
	}

	@Override
	public void mul(float c) {
		type.mul(c);
		img.setValue(pos, type);
	}

	@Override
	public void mul(double c) {
		type.mul(c);
		img.setValue(pos, type);
	}

	@Override
	public T createVariable() {
		return type.createVariable();
	}

	@Override
	public T copy() {
		return type.copy();
	}

	@Override
	public void set(T c) {
		type.set(c);
		img.setValue(pos, type);
	}

	@Override
	public double getRealDouble() {
		return type.getRealDouble();
	}

	@Override
	public float getRealFloat() {
		return type.getRealFloat();
	}

	@Override
	public double getImaginaryDouble() {
		return type.getImaginaryDouble();
	}

	@Override
	public float getImaginaryFloat() {
		return type.getImaginaryFloat();
	}

	@Override
	public void setReal(float f) {
		type.setReal(f);
		img.setValue(pos, type);
	}

	@Override
	public void setReal(double f) {
		type.setReal(f);
		img.setValue(pos, type);
	}

	@Override
	public void setImaginary(float f) {
		type.setImaginary(f);
		img.setValue(pos, type);
	}

	@Override
	public void setImaginary(double f) {
		type.setImaginary(f);
		img.setValue(pos, type);
	}

	@Override
	public void setComplexNumber(float r, float i) {
		type.setComplexNumber(r, i);
		img.setValue(pos, type);
	}

	@Override
	public void setComplexNumber(double r, double i) {
		type.setComplexNumber(r, i);
		img.setValue(pos, type);
	}

	@Override
	public float getPowerFloat() {
		return type.getPowerFloat();
	}

	@Override
	public double getPowerDouble() {
		return type.getPowerDouble();
	}

	@Override
	public float getPhaseFloat() {
		return type.getPhaseFloat();
	}

	@Override
	public double getPhaseDouble() {
		return type.getPhaseDouble();
	}

	@Override
	public void complexConjugate() {
		type.complexConjugate();
		img.setValue(pos, type);
	}

	public void fillFromFile(long p) {
		pos = p;
		img.getValue(pos, type);
	}
}
