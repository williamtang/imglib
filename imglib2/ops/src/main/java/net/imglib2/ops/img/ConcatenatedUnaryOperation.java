/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops.img;

import net.imglib2.ops.operation.UnaryOperation;

/**
 * @author Christian Dietz (University of Konstanz)
 */
public abstract class ConcatenatedUnaryOperation<T> implements
		UnaryOperation<T, T> {

	private UnaryOperation<T, T>[] operations;

	public ConcatenatedUnaryOperation(
			UnaryOperation<T, T>... operations) {
		this.operations = operations;
	}

	@Override
	public T compute( T input, T output )
	{
		// Check wether there exists only one solution
		if ( operations.length == 1 )
			return operations[ 0 ].compute( input, output );

		if (operations.length == 1)
			return operations[0].compute(input, output);

		T buffer = getBuffer();

		T tmpOutput = output;
		T tmpInput = buffer;
		T tmp;

		// Check needs to be done as the number of operations may be uneven and
		// the result may not be written to output
		if (operations.length % 2 == 0) {
			tmpOutput = buffer;
			tmpInput = output;
		}

		operations[0].compute(input, tmpOutput);

		for (int i = 1; i < operations.length; i++) {
			tmp = tmpInput;
			tmpInput = tmpOutput;
			tmpOutput = tmp;
			operations[i].compute(tmpInput, tmpOutput);
		}

		return output;
	}

	/**
	 * Method to retrieve the Buffer
	 */
	protected abstract T getBuffer();

	@Override
	public UnaryOperation<T, T> copy() {
		@SuppressWarnings("unchecked")
		UnaryOperation<T, T>[] operationCopy = new UnaryOperation[operations.length];

		for (int i = 0; i < operationCopy.length; i++) {
			operationCopy[i] = operations[i].copy();
		}

		return new ConcatenatedUnaryOperation<T>(operations) {

			@Override
			protected T getBuffer() {
				return this.getBuffer();
			}
		};
	}
}
