package net.imglib2.ops.operation.unary.metadata;

import net.imglib2.Interval;
import net.imglib2.meta.Metadata;
import net.imglib2.ops.UnaryOperation;

public class CopyCalibratedSpace implements UnaryOperation< Metadata, Metadata >
{
	private Interval interval;

	public CopyCalibratedSpace()
	{
		interval = null;
	}

	public CopyCalibratedSpace( Interval interval )
	{
		this.interval = interval;
	}

	@Override
	public Metadata compute( Metadata input, Metadata output )
	{

		for ( int d = 0; d < input.numDimensions(); d++ )
		{
			if ( interval != null && interval.dimension( d ) == 1 )
				continue;

			output.setAxis( input.axis( d ), d );
			output.setCalibration( input.calibration( d ), d );
		}

		return output;
	}

	@Override
	public UnaryOperation< Metadata, Metadata > copy()
	{
		return new CopyCalibratedSpace();
	}

}
