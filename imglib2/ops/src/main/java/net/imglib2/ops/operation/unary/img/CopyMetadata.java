package net.imglib2.ops.operation.unary.img;

import net.imglib2.Interval;
import net.imglib2.meta.Axes;
import net.imglib2.meta.Metadata;
import net.imglib2.ops.UnaryOperation;

/**
 * 
 * @author dietzc
 * 
 */
public class CopyMetadata implements UnaryOperation< Metadata, Metadata >
{

	private final boolean keepOneSizedDims;

	private boolean copyCalibratedSpace;

	private Interval interval;

	public CopyMetadata( boolean copyCalibratedSpace )
	{
		this( null, true, copyCalibratedSpace );
	}

	public CopyMetadata()
	{
		this( null, true, true );
	}

	public CopyMetadata( Interval interval, boolean keepOneSizedDims )
	{

		this( interval, keepOneSizedDims, true );
	}

	public CopyMetadata( Interval interval, boolean keepOneSizedDims, boolean copyCalibratedSpace )
	{
		this.interval = interval;
		this.keepOneSizedDims = keepOneSizedDims;
		this.copyCalibratedSpace = copyCalibratedSpace;
	}

	@Override
	public Metadata compute( Metadata inMetadata, Metadata outMetadata )
	{

		outMetadata.setName( inMetadata.getName() );
		outMetadata.setSource( inMetadata.getSource() );
		outMetadata.setValidBits( inMetadata.getValidBits() );

		if ( copyCalibratedSpace )
		{
			int innerCtr = 0;
			for ( int d = 0; d < inMetadata.numDimensions(); d++ )
			{
				if ( interval == null || interval.dimension( d ) > 1 || keepOneSizedDims )
				{
					outMetadata.setAxis( inMetadata.axis( d ), innerCtr );
					outMetadata.setCalibration( inMetadata.calibration( d ), innerCtr );

					innerCtr++;
				}

			}
		}

		if ( !keepOneSizedDims )
		{
			outMetadata.setCompositeChannelCount( ( int ) interval.dimension( inMetadata.getAxisIndex( Axes.CHANNEL ) ) );
		}
		else
		{
			outMetadata.setCompositeChannelCount( inMetadata.getCompositeChannelCount() );

			for ( int c = 0; c < outMetadata.getCompositeChannelCount(); c++ )
			{
				outMetadata.setChannelMinimum( c, inMetadata.getChannelMinimum( c ) );
				outMetadata.setChannelMaximum( c, inMetadata.getChannelMaximum( c ) );
			}
		}

		outMetadata.initializeColorTables( inMetadata.getColorTableCount() );
		for ( int n = 0; n < inMetadata.getColorTableCount(); n++ )
		{
			outMetadata.setColorTable( inMetadata.getColorTable16( n ), n );
			outMetadata.setColorTable( inMetadata.getColorTable8( n ), n );
		}

		return outMetadata;
	}

	@Override
	public UnaryOperation< Metadata, Metadata > copy()
	{
		return new CopyMetadata( interval, keepOneSizedDims );
	}

}
