package net.imglib2.ops.img;

import net.imglib2.ops.buffer.UnaryObjectFactory;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.UnaryOutputOperation;

public class Operations
{
	/*
	 * Operation Links
	 */
	public static < A, B > UnaryOutputOperation< A, B > concat( final LeftJoinedUnaryOperation< A, B > leftLinked, final UnaryOutputOperation< B, B > op )
	{
		return concat( leftLinked.first(), concat( leftLinked.follower(), op ) );
	}

	public static < A, B > UnaryOutputOperation< A, B > concat( final UnaryOutputOperation< A, A > op, final RightJoinedUnaryOperation< A, B > rightLinked )
	{
		return concat( concat( op, rightLinked.first() ), rightLinked.follower() );
	}

	public static < A, B, C > UnaryOperationBridge< A, B, C > concat( UnaryOutputOperation< A, B > op1, UnaryOutputOperation< B, C > op2 )
	{
		return new UnaryOperationBridge< A, B, C >( op1, op2 );
	}

	public static < A, B > LeftJoinedUnaryOperation< A, B > concat( UnaryOutputOperation< A, B > op1, UnaryOutputOperation< B, B >[] op2 )
	{
		return new LeftJoinedUnaryOperation< A, B >( op1, concat( op2 ) );
	}

	public static < A, B > RightJoinedUnaryOperation< A, B > concat( UnaryOutputOperation< A, A >[] op1, UnaryOutputOperation< A, B > op2 )
	{
		return new RightJoinedUnaryOperation< A, B >( concat( op1 ), op2 );
	}

	/*
	 * Linked Pipes
	 */
	public static < A, B > UnaryOutputOperation< A, B > concat( final PipedUnaryOperation< A > pipe, final RightJoinedUnaryOperation< A, B > rightLink )
	{
		return concat( concat( pipe.ops(), rightLink.first() ), rightLink.follower() );
	}

	public static < A, B > UnaryOutputOperation< A, B > concat( final LeftJoinedUnaryOperation< A, B > leftLink, final PipedUnaryOperation< B > pipe )
	{
		return concat( leftLink.first(), concat( leftLink.follower(), pipe.ops() ) );
	}

	/*
	 * Pipes
	 */
	public static < A, B > UnaryOutputOperation< A, B > concat( final UnaryOutputOperation< A, B > op, final PipedUnaryOperation< B > pipe )
	{
		return concat( op, pipe.ops() );
	}

	public static < A, B > UnaryOperation< A, B > concat( final PipedUnaryOperation< A > pipe, final UnaryOutputOperation< A, B > op )
	{
		return concat( pipe.ops(), op );
	}

	public static < A > UnaryOutputOperation< A, A > concat( UnaryOutputOperation< A, A >[] ops, PipedUnaryOperation< A > pipe )
	{
		return concat( ops, pipe.ops() );
	}

	public static < A > PipedUnaryOperation< A > concat( PipedUnaryOperation< A > pipe1, PipedUnaryOperation< A > pipe2 )
	{
		return concat( pipe1.ops(), pipe2.ops() );
	}

	public static < A > UnaryOutputOperation< A, A > concat( PipedUnaryOperation< A > pipe, UnaryOutputOperation< A, A >[] ops )
	{
		return concat( pipe.ops(), ops );
	}

	public static < A > PipedUnaryOperation< A > concat( UnaryOutputOperation< A, A >[] ops1, UnaryOutputOperation< A, A >[] ops2 )
	{
		PipedUnaryOperation< A > piped = concat( ops1 );
		piped.append( ops2 );
		return piped;
	}

	public static < A > PipedUnaryOperation< A > concat( UnaryOutputOperation< A, A >[] ops )
	{
		// Actually this shouldn't happen if you just use the methods of this
		// class

		return new PipedUnaryOperation< A >( ops );
	}

	/*
	 * Helper methods
	 */
	public static < A, B > B compute( UnaryOutputOperation< A, B > op, A in )
	{
		return op.compute( in, op.bufferFactory().instantiate( in ) );
	}

	/*
	 * Helper to create output operation
	 */
	public static < A, B > UnaryOutputOperation< A, B > compute( final UnaryOperation< A, B > op, final UnaryObjectFactory< A, B > fac )
	{
		return new UnaryOperationWrapper< A, B >( op, fac );
	}
}
