package net.imglib2.ops.img;

import net.imglib2.ops.buffer.UnaryObjectFactory;

public class SingletonizedFactory< A, B > implements UnaryObjectFactory< A, B >
{

	private UnaryObjectFactory< A, B > fac;

	private B buf;

	public SingletonizedFactory( UnaryObjectFactory< A, B > fac )
	{
		this.fac = fac;
	}

	@Override
	public B instantiate( A a )
	{
		if ( buf == null )
			buf = fac.instantiate( a );

		return buf;
	}

	@Override
	public boolean equals( Object obj )
	{
		return obj.equals( fac );
	}

}
