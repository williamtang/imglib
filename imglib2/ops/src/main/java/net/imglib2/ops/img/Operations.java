package net.imglib2.ops.img;

import net.imglib2.IterableInterval;
import net.imglib2.ops.operation.BinaryOutputOperation;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.UnaryOutputOperation;

public class Operations {

	/*
	 * Joiner
	 */
	public static <A, B> UnaryOutputOperation<A, B> joinLeft(
			UnaryOutputOperation<A, B> op1, UnaryOutputOperation<B, B> op2) {
		return new LeftJoinedUnaryOperation<A, B>(op1, op2);
	}

	public static <A, B> UnaryOutputOperation<A, B> joinRight(
			UnaryOutputOperation<A, A> op1, UnaryOutputOperation<A, B> op2) {
		return new RightJoinedUnaryOperation<A, B>(op1, op2);
	}

	/*
	 * Concats
	 */
	public static <A, B> UnaryOutputOperation<A, B> concat(
			final LeftJoinedUnaryOperation<A, B> leftLinked,
			final UnaryOutputOperation<B, B> op) {
		return joinLeft(leftLinked.first(), joinLeft(leftLinked.follower(), op));
	}

	public static <A, B> UnaryOutputOperation<A, B> concat(
			final UnaryOutputOperation<A, A> op,
			final RightJoinedUnaryOperation<A, B> rightLinked) {
		return joinRight(joinLeft(op, rightLinked.first()),
				rightLinked.follower());
	}

	public static <A, B> UnaryOutputOperation<A, B> concat(
			final PipedUnaryOperation<A> pipe,
			final RightJoinedUnaryOperation<A, B> rightJoin) {
		return joinRight(concat(pipe.ops(), rightJoin.first()),
				rightJoin.follower());
	}

	public static <A, B> UnaryOutputOperation<A, B> concat(
			final LeftJoinedUnaryOperation<A, B> leftLink,
			final PipedUnaryOperation<B> pipe) {
		return joinLeft(leftLink.first(),
				concat(leftLink.follower(), pipe.ops()));
	}

	public static <A, B> LeftJoinedUnaryOperation<A, B> concat(
			UnaryOutputOperation<A, B> op, PipedUnaryOperation<B> piped) {
		return new LeftJoinedUnaryOperation<A, B>(op, piped);
	}

	public static <A, B> RightJoinedUnaryOperation<A, B> concat(
			PipedUnaryOperation<A> piped, UnaryOutputOperation<A, B> op) {
		return new RightJoinedUnaryOperation<A, B>(piped, op);
	}

	public static <A, B> LeftJoinedUnaryOperation<A, B> concat(
			UnaryOutputOperation<A, B> op1, UnaryOutputOperation<B, B>[] op2) {
		return new LeftJoinedUnaryOperation<A, B>(op1, concat(op2));
	}

	public static <A, B> RightJoinedUnaryOperation<A, B> concat(
			UnaryOutputOperation<A, A>[] op1, UnaryOutputOperation<A, B> op2) {
		return new RightJoinedUnaryOperation<A, B>(concat(op1), op2);
	}

	public static <A> UnaryOutputOperation<A, A> concat(
			UnaryOutputOperation<A, A>[] ops, PipedUnaryOperation<A> pipe) {
		return concat(ops, pipe.ops());
	}

	public static <A> PipedUnaryOperation<A> concat(
			PipedUnaryOperation<A> pipe1, PipedUnaryOperation<A> pipe2) {
		return concat(pipe1.ops(), pipe2.ops());
	}

	public static <A> UnaryOutputOperation<A, A> concat(
			PipedUnaryOperation<A> pipe, UnaryOutputOperation<A, A>[] ops) {
		return concat(pipe.ops(), ops);
	}

	public static <A> PipedUnaryOperation<A> concat(
			UnaryOutputOperation<A, A>[] ops1, UnaryOutputOperation<A, A>[] ops2) {
		PipedUnaryOperation<A> piped = concat(ops1);
		piped.append(ops2);
		return piped;
	}

	public static <A> PipedUnaryOperation<A> concat(
			UnaryOutputOperation<A, A>[] ops) {
		return new PipedUnaryOperation<A>(ops);
	}

	@SuppressWarnings("unchecked")
	public static <A> PipedUnaryOperation<A> concat(
			UnaryOutputOperation<A, A> op1, UnaryOutputOperation<A, A> op2) {
		return concat(new UnaryOutputOperation[] { op1, op2 });
	}

	/*
	 * Bridges
	 */
	public static <A, B, C> UnaryOperationBridge<A, B, C> bridge(
			UnaryOutputOperation<A, B> op1, UnaryOutputOperation<B, C> op2) {
		return new UnaryOperationBridge<A, B, C>(op1, op2);
	}

	/*
	 * Pipes
	 */
	public static <A> PipedUnaryOperation<A> concatSameType(
			final PipedUnaryOperation<A> pipe,
			final UnaryOutputOperation<A, A> op) {
		PipedUnaryOperation<A> res = concat(pipe.ops());
		res.append(op);
		return res;
	}

	public static <A> PipedUnaryOperation<A> concatSameType(
			final UnaryOutputOperation<A, A> op,
			final PipedUnaryOperation<A> pipe) {
		@SuppressWarnings("unchecked")
		PipedUnaryOperation<A> res = new PipedUnaryOperation<A>(op);
		res.append(pipe.ops());
		return res;
	}

	/*
	 * Iterative Operation
	 */
	public static <A> PipedUnaryOperation<A> iterate(
			UnaryOutputOperation<A, A> op, int numIterations) {

		@SuppressWarnings("unchecked")
		UnaryOutputOperation<A, A>[] ops = new UnaryOutputOperation[numIterations];

		for (int i = 0; i < numIterations; i++)
			ops[i] = op;

		return concat(ops);
	}

	/*
	 * Helper to create output operation
	 */
	public static <A, B> UnaryOutputOperation<A, B> wrap(
			final UnaryOperation<A, B> op, final UnaryObjectFactory<A, B> fac) {
		return new UnaryOperationWrapper<A, B>(op, fac);
	}

	/*
	 * Mapper
	 */
	public static <A, B> UnaryOperation<IterableInterval<A>, IterableInterval<B>> map(
			UnaryOperation<A, B> op) {

		// TODO mapper can be optimized

		return new UnaryOperationAssignment<A, B>(op);
	}

	/*
	 * Compute
	 */

	/**
	 * Compute several ops of the same type at once (using only one buffer)
	 */
	public static <A, B> B compute(B input, B output,
			UnaryOutputOperation<B, B>[] ops) {
		@SuppressWarnings("unchecked")
		UnaryOutputOperation<B, B>[] follower = new UnaryOutputOperation[ops.length - 1];
		System.arraycopy(ops, 1, follower, 0, follower.length);

		return compute(input, output, ops[0], follower);
	}

	/**
	 * First computes op1 and then all following. only one buffer is used
	 * 
	 * TODO: What happens if ops is a piped op? we need to unpack!
	 */
	public static <A, B> B compute(A input, B output,
			UnaryOutputOperation<A, B> op1, UnaryOutputOperation<B, B>... ops) {

		B buffer = op1.bufferFactory().instantiate(input);

		B tmpOutput = output;
		B tmpInput = buffer;
		B tmp;

		if (ops.length % 2 == 1) {
			tmpOutput = buffer;
			tmpInput = output;
		}

		op1.compute(input, tmpOutput);

		for (int i = 0; i < ops.length; i++) {
			tmp = tmpInput;
			tmpInput = tmpOutput;
			tmpOutput = tmp;
			ops[i].compute(tmpInput, tmpOutput);
		}

		return output;
	}

	/**
	 * Use op with in as input. output will be generated.
	 */
	public static <A, B> B compute(UnaryOutputOperation<A, B> op, A in) {
		return op.compute(in, op.bufferFactory().instantiate(in));
	}

	public static <A, B, C> C compute(BinaryOutputOperation<A, B, C> op, A in1,
			B in2) {
		return op.compute(in1, in2, op.bufferFactory().instantiate(in1, in2));
	}

}
