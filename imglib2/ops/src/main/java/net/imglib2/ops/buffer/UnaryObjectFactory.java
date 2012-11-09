package net.imglib2.ops.buffer;

public interface UnaryObjectFactory< A, B >
{
	B instantiate( A a );

}
