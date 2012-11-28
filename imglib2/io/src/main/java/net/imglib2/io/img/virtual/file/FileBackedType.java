package net.imglib2.io.img.virtual.file;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ComplexType;

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
