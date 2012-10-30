package net.imglib2.ops.buffer;

import net.imglib2.Interval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class ImgBuffer< T extends NativeType< T >> implements BufferFactory< Img< T > >
{

	private Img< T > img;

	public ImgBuffer( Img< T > img )
	{
		this.img = img;
	}

	@Override
	public Img< T > instantiate()
	{
		return img.factory().create( img, img.firstElement().createVariable() );
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !( obj instanceof ImgBuffer ) ) { return false; }

		if ( !( ( ImgBuffer ) obj ).img.iterationOrder().equals( img ) )
			return false;

		if ( ( ( ImgBuffer ) obj ).img.firstElement().getClass() != img.firstElement().getClass() )
			return false;

		return true;
	}
}
