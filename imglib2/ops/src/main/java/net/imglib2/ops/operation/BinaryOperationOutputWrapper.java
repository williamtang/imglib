package net.imglib2.ops.operation;

/**
 * 
 * TODO
 * 
 * @author Christian Dietz
 *
 */
public abstract class BinaryOperationOutputWrapper<A, B, C> implements
		BinaryOutputOperation<A, B, C> {

	private BinaryOperation<A, B, C> wrappedOp;

	public BinaryOperationOutputWrapper(BinaryOperation<A, B, C> wrappedOp) {
		this.wrappedOp = wrappedOp;
	}

	@Override
	public C compute(A inputA, B inputB, C output) {
		return wrappedOp.compute(inputA, inputB, output);
	}

	@Override
	public BinaryOperation<A, B, C> copy() {
		return new BinaryOperationOutputWrapper<A, B, C>(wrappedOp) {

			@Override
			public C createEmptyOutput(A inputA, B inputB) {
				return BinaryOperationOutputWrapper.this.createEmptyOutput(
						inputA, inputB);
			}
		};
	}

	@Override
	public C compute(A inputA, B inputB) {
		return wrappedOp.compute(inputA, inputB,
				createEmptyOutput(inputA, inputB));
	}

}
