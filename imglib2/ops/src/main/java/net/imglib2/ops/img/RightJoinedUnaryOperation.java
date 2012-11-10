package net.imglib2.ops.img;

import net.imglib2.ops.operation.UnaryOutputOperation;

public class RightJoinedUnaryOperation<A, B> implements
		UnaryOutputOperation<A, B> {
	private UnaryOutputOperation<A, A> first;

	private UnaryOutputOperation<A, B> follower;

	protected RightJoinedUnaryOperation(UnaryOutputOperation<A, A> first,
			UnaryOutputOperation<A, B> follower) {
		this.first = first;
		this.follower = follower;
	}

	protected RightJoinedUnaryOperation(PipedUnaryOperation<A> first,
			UnaryOutputOperation<A, B> follower) {
		this.first = first;
		this.follower = follower;
	}

	@SuppressWarnings("unchecked")
	@Override
	public B compute(A input, B output) {

		if (first.bufferFactory().equals(follower.bufferFactory())) {
			return Operations.concat((UnaryOutputOperation<B, B>) first,
					(UnaryOutputOperation<B, B>) follower).compute((B) input,
					output);
		} else {
			return follower.compute(
					first.compute(input,
							first.bufferFactory().instantiate(input)), output);
		}

	}

	@Override
	public UnaryObjectFactory<A, B> bufferFactory() {
		return follower.bufferFactory();
	}

	@Override
	public UnaryOutputOperation<A, B> copy() {
		return new RightJoinedUnaryOperation<A, B>(first, follower);
	}

	public UnaryOutputOperation<A, A> first() {
		return first;
	}

	public UnaryOutputOperation<A, B> follower() {
		return follower;
	}
}
