package net.imglib2.ops.img;

import net.imglib2.ops.buffer.UnaryObjectFactory;
import net.imglib2.ops.operation.UnaryOutputOperation;

public class LeftJoinedUnaryOperation< A, B > implements UnaryOutputOperation< A, B >
{
	private UnaryOutputOperation< B, B > follower;

	private UnaryOutputOperation< A, B > first;

	protected LeftJoinedUnaryOperation( UnaryOutputOperation< A, B > first, UnaryOutputOperation< B, B > follower )
	{
		this.first = first;
		this.follower = follower;
	}

	protected LeftJoinedUnaryOperation( UnaryOutputOperation< A, B > first, PipedUnaryOperation< B > follower )
	{
		// TODO handle pipes
		this.first = first;
		this.follower = follower;
	}

	@Override
	public B compute( A input, B output )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnaryObjectFactory< A, B > bufferFactory()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnaryOutputOperation< A, B > copy()
	{
		return null;
	}

	public UnaryOutputOperation< A, B > first()
	{
		return first;
	}

	public UnaryOutputOperation< B, B > follower()
	{
		return follower;
	}
}
