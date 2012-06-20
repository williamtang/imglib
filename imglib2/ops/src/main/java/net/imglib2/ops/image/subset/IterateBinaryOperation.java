package net.imglib2.ops.image.subset;

import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.subset.ImgPlusView;
import net.imglib2.img.subset.ImgView;
import net.imglib2.img.subset.LabelingView;
import net.imglib2.img.subset.SubsetViews;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.operation.unary.img.IntervalsFromDimSelection;
import net.imglib2.type.Type;

/**
 * Applies a given Operation to each interval separately.
 * 
 * @author dietzc, hornm University of Konstanz
 */
public final class IterateBinaryOperation< T extends Type< T >, V extends Type< V >, O extends Type< O >, S extends RandomAccessibleInterval< T > & IterableInterval< T >, U extends RandomAccessibleInterval< V > & IterableInterval< V >, R extends RandomAccessibleInterval< O > & IterableInterval< O >> implements BinaryOperation< S, U, R >
{

	private final BinaryOperation< S, U, R > m_op;

	private final int[] m_selectedDims;

	private final IntervalsFromDimSelection m_intervalOp;

	public IterateBinaryOperation( BinaryOperation< S, U, R > op, int[] selectedDims )
	{
		m_op = op;
		m_selectedDims = selectedDims;
		m_intervalOp = new IntervalsFromDimSelection();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final R compute( final S in0, U in1, R out )
	{

		Interval[] inIntervals0 = m_intervalOp.compute( m_selectedDims, new Interval[] { in0 } );

		Interval[] inIntervals1 = m_intervalOp.compute( m_selectedDims, new Interval[] { in1 } );

		Interval[] outIntervals = m_intervalOp.compute( m_selectedDims, new Interval[] { out } );

		if ( inIntervals0.length != outIntervals.length || inIntervals0.length != inIntervals1.length ) { throw new IllegalArgumentException( "In and out intervals do not match! Most likely an implementation error!" ); }

		for ( int i = 0; i < inIntervals0.length; i++ )
		{
			m_op.compute( createSubType( in0, inIntervals0[ i ] ), createSubType( in1, inIntervals1[ i ] ), createSubType( out, outIntervals[ i ] ) );
		}

		return out;
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	private static synchronized < K extends Interval > K createSubType( final K in, final Interval i )
	{
		if ( in instanceof Labeling ) { return ( K ) new LabelingView( SubsetViews.subsetView( ( NativeImgLabeling ) in, i, false ), ( ( NativeImgLabeling ) in ).factory() ); }
		if ( in instanceof ImgPlus ) { return ( K ) new ImgPlusView( SubsetViews.subsetView( ( ImgPlus ) in, i, false ), ( ( ImgPlus ) in ).factory(), ( ImgPlus ) in ); }

		if ( in instanceof Img ) { return ( K ) new ImgView( SubsetViews.subsetView( ( Img ) in, i, false ), ( ( Img ) in ).factory() ); }
		throw new IllegalArgumentException( "Not implemented yet (IntervalWiseOperation)" );
	}

	@Override
	public BinaryOperation< S, U, R > copy()
	{
		return new IterateBinaryOperation< T, V, O, S, U, R >( m_op.copy(), m_selectedDims );
	}
}
