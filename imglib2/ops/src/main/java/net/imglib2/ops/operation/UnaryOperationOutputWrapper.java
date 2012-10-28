package net.imglib2.ops.operation;

/**
 * 
 * TODO
 * 
 * @author Christian Dietz
 * 
 */
public abstract class UnaryOperationOutputWrapper<A, B> implements
		UnaryOutputOperation<A, B> {

	private UnaryOperation<A, B> wrappedOp;

	public UnaryOperationOutputWrapper(UnaryOperation<A, B> wrappedOp) {
		this.wrappedOp = wrappedOp;
	}

	@Override
	public B compute(A input, B output) {
		return wrappedOp.compute(input, output);
	}

	@Override
	public UnaryOutputOperation<A, B> copy() {
		return new UnaryOperationOutputWrapper<A, B>(wrappedOp) {

			@Override
			public B createEmptyOutput(A input) {
				return UnaryOperationOutputWrapper.this
						.createEmptyOutput(input);
			}
		};
	}

	@Override
	public B compute(A input) {
		return wrappedOp.compute(input, createEmptyOutput(input));
	}

}
