package net.imglib2.ops.operation.unary.metadata;

import net.imglib2.meta.Metadata;
import net.imglib2.ops.UnaryOperation;

public class CopyNamed implements UnaryOperation< Metadata, Metadata >
{

	@Override
	public Metadata compute( Metadata input, Metadata output )
	{
		output.setName( input.getName() );
		return output;
	}

	@Override
	public UnaryOperation< Metadata, Metadata > copy()
	{
		return new CopyNamed();
	}

}
