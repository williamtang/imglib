package net.imglib2.ops.operation.unary.metadata;

import net.imglib2.meta.Metadata;
import net.imglib2.ops.UnaryOperation;

public class CopyImageMetadata implements UnaryOperation< Metadata, Metadata >
{

	@Override
	public Metadata compute( Metadata input, Metadata output )
	{
		output.setValidBits( input.getValidBits() );
		output.setCompositeChannelCount( input.getCompositeChannelCount() );

		for ( int c = 0; c < output.getCompositeChannelCount(); c++ )
		{
			output.setChannelMinimum( c, input.getChannelMinimum( c ) );
			output.setChannelMaximum( c, input.getChannelMaximum( c ) );
		}

		output.initializeColorTables( input.getColorTableCount() );
		for ( int n = 0; n < input.getColorTableCount(); n++ )
		{
			output.setColorTable( input.getColorTable16( n ), n );
			output.setColorTable( input.getColorTable8( n ), n );
		}

		return output;
	}

	@Override
	public UnaryOperation< Metadata, Metadata > copy()
	{
		return new CopyImageMetadata();
	}

}
