package net.imglib2.ops.img;

import net.imglib2.IterableInterval;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.operation.UnaryOperation;

public class OperationBuilder {

	public static <A, B, C, D> UnaryOperation<A, D> concat(
			final UnaryOperation<A, B> op1, final UnaryOperation<A, C> op2,
			final BinaryOperation<B, C, D> binaryOp, final B bufferB,
			final C bufferC) {
		return new UnaryBinaryOperationAdapter<A, B, C, D>(op1, op2, binaryOp) {

			@Override
			protected B getOp1Buffer() {
				return bufferB;
			}

			@Override
			protected C getOp2Buffer() {
				return bufferC;
			}
		};
	}

	public static <A, B, C, D> BinaryOperation<A, B, D> concat(
			final BinaryOperation<A, B, C> binaryOp,
			final UnaryOperation<C, D> op1, final C buffer) {
		return new BinaryUnaryOperationAdapter<A, B, C, D>(binaryOp, op1) {
			@Override
			protected C getBinaryOpBuffer() {
				return buffer;
			}
		};
	}

	public static <A> UnaryOperation<A, A> concat(final A buffer,
			final UnaryOperation<A, A>... ops) {
		return new ConcatenatedUnaryOperation<A>(ops) {

			@Override
			protected A getBuffer() {
				return buffer;
			}
		};
	}

	public static <A, B> UnaryOperation<A, B> concat(final B buffer,
			final UnaryOperation<A, B> first,
			final UnaryOperation<B, B>... second) {

		if (second.length % 2 == 1) {
			return concat(buffer, first, concat(buffer, second));
		} else {
			return new UnaryOperation<A, B>() {

				@Override
				public B compute(A input, B output) {
					first.compute(input, output);
					return concat(buffer, second).compute(output, output);
				}

				@Override
				public UnaryOperation<A, B> copy() {
					return concat(buffer, first, second);
				}
			};
		}
	}

	public static <A, B> UnaryOperation<A, B> concat(final B buffer,
			final UnaryOperation<A, B> first, final UnaryOperation<B, B> second) {
		return new UnaryOperationBridge<A, B, B>(first, second) {

			@Override
			public B getBuffer() {
				return buffer;
			}
		};
	}

	public static <A, B, C, D> BinaryOperation<A, B, D> concat(final C buffer,
			BinaryOperation<A, B, C> binaryOp, UnaryOperation<C, D> unaryOp) {
		return new BinaryUnaryOperationAdapter<A, B, C, D>(binaryOp, unaryOp) {

			@Override
			protected C getBinaryOpBuffer() {
				return buffer;
			}
		};
	}

	public static <A, B> UnaryOperation<IterableInterval<A>, IterableInterval<B>> map(
			UnaryOperation<A, B> op) {
		return new UnaryOperationAssignment<A, B>(op);
	}

	public static <A, B, C> BinaryOperation<IterableInterval<A>, IterableInterval<B>, IterableInterval<C>> map(
			BinaryOperation<A, B, C> op) {
		return new BinaryOperationAssignment<A, B, C>(op);
	}
}
