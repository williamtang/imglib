package net.imglib2.ops.operation.unary.metadata;

import net.imglib2.meta.Metadata;
import net.imglib2.ops.UnaryOperation;

public class CopySourced implements UnaryOperation< Metadata, Metadata >
{

	@Override
	public Metadata compute( Metadata input, Metadata output )
	{
		output.setSource( input.getSource() );
		return output;
	}

	@Override
	public UnaryOperation< Metadata, Metadata > copy()
	{
		return new CopySourced();
	}

}
