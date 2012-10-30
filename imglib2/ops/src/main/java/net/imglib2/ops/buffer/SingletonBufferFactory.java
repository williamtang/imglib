package net.imglib2.ops.buffer;

public final class SingletonBufferFactory< A > implements BufferFactory< A >
{

	private BufferFactory< A > fac;

	private A buf = null;

	public SingletonBufferFactory( BufferFactory< A > fac )
	{
		this.fac = fac;
	}

	@Override
	public A instantiate()
	{
		if ( buf == null )
			buf = fac.instantiate();

		return buf;
	}
}
