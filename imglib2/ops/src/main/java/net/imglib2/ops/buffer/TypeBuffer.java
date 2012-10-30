package net.imglib2.ops.buffer;

import net.imglib2.type.Type;

public class TypeBuffer< T extends Type< T >> implements BufferFactory< T >
{
	private final T type;

	public TypeBuffer( T type )
	{
		this.type = type;
	}

	@Override
	public T instantiate()
	{
		return type.createVariable();
	}
}
