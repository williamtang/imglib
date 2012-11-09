package net.imglib2.ops.buffer;

public interface BinaryObjectFactory< A, B, C >
{
	C instantiate( A inputA, B inputB );
}
