package net.imglib2.ops.operationbuilder;

import static org.junit.Assert.assertTrue;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.buffer.BufferFactory;
import net.imglib2.ops.buffer.TypeBuffer;
import net.imglib2.ops.img.OperationBuilder;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.iterable.unary.Mean;
import net.imglib2.ops.operation.real.binary.RealAdd;
import net.imglib2.ops.operation.real.unary.RealAddConstant;
import net.imglib2.ops.operation.real.unary.RealMultiplyConstant;
import net.imglib2.ops.operation.real.unary.RealSubtractConstant;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Before;
import org.junit.Test;

public class OperationBuilderTest
{

	Img< DoubleType > inA = null;

	Img< DoubleType > inB = null;

	Img< DoubleType > out = null;

	BufferFactory< IterableInterval< DoubleType > > buffer = null;

	double valB = 7;

	double valA = 11;

	double constant = 31;

	@Before
	public void init()
	{
		final long[] dims = new long[] { 10, 10 };
		inA = new ArrayImgFactory< DoubleType >().create( dims, new DoubleType() );
		inB = new ArrayImgFactory< DoubleType >().create( dims, new DoubleType() );
		buffer = new BufferFactory< IterableInterval< DoubleType > >()
		{

			@Override
			public Img< DoubleType > instantiate()
			{
				return new ArrayImgFactory< DoubleType >().create( dims, new DoubleType() );
			}
		};

		out = new ArrayImgFactory< DoubleType >().create( dims, new DoubleType() );

		Cursor< DoubleType > cursorA = inA.cursor();
		Cursor< DoubleType > cursorB = inB.cursor();

		while ( cursorA.hasNext() )
		{
			cursorA.next().set( valA );
			cursorB.next().set( valB );
		}
	}

	@Test
	public void simpleMapTest()
	{

		OperationBuilder.map( OperationBuilder.concat( new TypeBuffer< DoubleType >( new DoubleType() ), new RealAdd< DoubleType, DoubleType, DoubleType >(), new RealMultiplyConstant< DoubleType, DoubleType >( constant ) ) ).compute( inA, inB, out );

		// Equation would be (valA+valB)*constant
		assertTrue( out.cursor().next().getRealDouble() == ( valA + valB ) * constant );

	}

	@Test
	public void bufferedImgConcatEvenTest()
	{

		int even = 4;

		UnaryOperation< IterableInterval< DoubleType >, IterableInterval< DoubleType >>[] ops = new UnaryOperation[ even ];

		for ( int i = 0; i < ops.length; i++ )
		{
			ops[ i ] = OperationBuilder.map( new RealAddConstant< DoubleType, DoubleType >( constant ) );
		}

		OperationBuilder.concat( buffer, OperationBuilder.map( new RealSubtractConstant< DoubleType, DoubleType >( constant ) ), ops ).compute( inA, out );

		// Equation would be (valA+valB)*constant
		assertTrue( out.cursor().next().getRealDouble() == ( valA - constant ) + ( even * constant ) );
	}

	@Test
	public void bufferedImgConcatOddTest()
	{

		int odd = 3;

		UnaryOperation< IterableInterval< DoubleType >, IterableInterval< DoubleType >>[] ops = new UnaryOperation[ odd ];

		for ( int i = 0; i < ops.length; i++ )
		{
			ops[ i ] = OperationBuilder.map( new RealAddConstant< DoubleType, DoubleType >( constant ) );
		}

		OperationBuilder.concat( buffer, OperationBuilder.map( new RealSubtractConstant< DoubleType, DoubleType >( constant ) ), ops ).compute( inA, out );

		// Equation would be (valA+valB)*constant
		assertTrue( out.cursor().next().getRealDouble() == ( valA - constant ) + ( odd * constant ) );
	}

	@Test
	public void bufferedTypeConcatEvenTest()
	{

		int even = 4;

		Mean< DoubleType, DoubleType > mean = new Mean< DoubleType, DoubleType >();

		UnaryOperation< DoubleType, DoubleType >[] ops = new UnaryOperation[ even ];

		for ( int i = 0; i < ops.length; i++ )
		{
			ops[ i ] = new RealAddConstant< DoubleType, DoubleType >( constant );
		}

		DoubleType out = OperationBuilder.concat( new TypeBuffer< DoubleType >( new DoubleType() ), mean, ops ).compute( inA.iterator(), new DoubleType() );

		// Equation would be (valA+valB)*constant
		assertTrue( out.getRealDouble() == valA + ( even * constant ) );

	}

	@Test
	public void bufferedTypeConcatOddTest()
	{

		int odd = 3;

		Mean< DoubleType, DoubleType > mean = new Mean< DoubleType, DoubleType >();

		UnaryOperation< DoubleType, DoubleType >[] ops = new UnaryOperation[ odd ];

		for ( int i = 0; i < ops.length; i++ )
		{
			ops[ i ] = new RealAddConstant< DoubleType, DoubleType >( constant );
		}

		DoubleType out = OperationBuilder.concat( new TypeBuffer< DoubleType >( new DoubleType() ), mean, ops ).compute( inA.iterator(), new DoubleType() );

		// Equation would be (valA+valB)*constant
		assertTrue( out.getRealDouble() == valA + ( odd * constant ) );

	}
}
