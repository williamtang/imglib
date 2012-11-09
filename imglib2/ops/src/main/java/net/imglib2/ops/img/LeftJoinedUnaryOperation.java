package net.imglib2.ops.img;

import net.imglib2.ops.operation.UnaryOutputOperation;

public class LeftJoinedUnaryOperation<A, B> implements
		UnaryOutputOperation<A, B> {
	private UnaryOutputOperation<B, B> follower;

	private UnaryOutputOperation<A, B> first;

	@SuppressWarnings("unchecked")
	protected LeftJoinedUnaryOperation(UnaryOutputOperation<A, B> first,
			UnaryOutputOperation<B, B> follower) {
		this(first, new PipedUnaryOperation<B>(follower));
	}

	protected LeftJoinedUnaryOperation(UnaryOutputOperation<A, B> first,
			PipedUnaryOperation<B> follower) {
		this.first = first;
		this.follower = follower;
	}

	@SuppressWarnings("unchecked")
	@Override
	public B compute(A input, B output) {
		if (follower instanceof PipedUnaryOperation) {
			return Operations.compute(input, output, first,
					((PipedUnaryOperation<B>) follower).ops());
		} else {
			return Operations.compute(input, output, first, follower);
		}

	}

	@Override
	public UnaryObjectFactory<A, B> bufferFactory() {
		return first.bufferFactory();
	}

	@Override
	public UnaryOutputOperation<A, B> copy() {
		return new LeftJoinedUnaryOperation<A, B>(first, follower);
	}

	public UnaryOutputOperation<A, B> first() {
		return first;
	}

	public UnaryOutputOperation<B, B> follower() {
		return follower;
	}
}
