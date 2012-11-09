package net.imglib2.ops.img;

public interface BinaryObjectFactory< A, B, C >
{
	C instantiate( A inputA, B inputB );
}
