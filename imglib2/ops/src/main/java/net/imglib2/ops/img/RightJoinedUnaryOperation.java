package net.imglib2.ops.img;

import net.imglib2.ops.buffer.UnaryObjectFactory;
import net.imglib2.ops.operation.UnaryOutputOperation;

public class RightJoinedUnaryOperation< A, B > implements UnaryOutputOperation< A, B >
{
	private UnaryOutputOperation< A, A > first;

	private UnaryOutputOperation< A, B > follower;

	protected RightJoinedUnaryOperation( UnaryOutputOperation< A, A > first, UnaryOutputOperation< A, B > follower )
	{
		this.first = first;
		this.follower = follower;
	}

	protected RightJoinedUnaryOperation( PipedUnaryOperation< A > first, UnaryOutputOperation< A, B > follower )
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

	public UnaryOutputOperation< A, A > first()
	{
		return first;
	}

	public UnaryOutputOperation< A, B > follower()
	{
		return follower;
	}
}
