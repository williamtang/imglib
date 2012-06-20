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
import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.operation.unary.img.IntervalsFromDimSelection;
import net.imglib2.type.Type;

/**
 * Applies a given Operation to each interval separately.
 * 
 * @author dietzc, hornm University of Konstanz
 */
public final class IterateUnaryOperation< T extends Type< T >, V extends Type< V >, S extends RandomAccessibleInterval< T > & IterableInterval< T >, U extends RandomAccessibleInterval< V > & IterableInterval< V >> implements UnaryOperation< S, U >
{

	private final UnaryOperation< S, U > m_op;

	private final int[] m_selectedDims;

	private final IntervalsFromDimSelection m_intervalOp;

	public IterateUnaryOperation( UnaryOperation< S, U > op, int[] selectedDims )
	{
		m_op = op;
		m_selectedDims = selectedDims;
		m_intervalOp = new IntervalsFromDimSelection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final U compute( final S in, final U out )
	{

		Interval[] inIntervals = m_intervalOp.compute( m_selectedDims, new Interval[] { in } );

		Interval[] outIntervals = m_intervalOp.compute( m_selectedDims, new Interval[] { out } );

		if ( inIntervals.length != outIntervals.length ) { throw new IllegalArgumentException( "In and out intervals do not match! Most likely an implementation error!" ); }

		for ( int i = 0; i < inIntervals.length; i++ )
		{
			m_op.compute( createSubType( inIntervals[ i ], in ), createSubType( outIntervals[ i ], out ) );
		}

		return out;
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	private synchronized < TT > TT createSubType( final Interval i, final TT in )
	{
		if ( in instanceof Labeling ) { return ( TT ) new LabelingView( SubsetViews.subsetView( ( NativeImgLabeling ) in, i, false ), ( ( NativeImgLabeling ) in ).factory() ); }
		if ( in instanceof ImgPlus ) { return ( TT ) new ImgPlusView( SubsetViews.subsetView( ( ImgPlus ) in, i, false ), ( ( ImgPlus ) in ).factory(), ( ImgPlus ) in ); }

		if ( in instanceof Img ) { return ( TT ) new ImgView( SubsetViews.subsetView( ( Img ) in, i, false ), ( ( Img ) in ).factory() ); }
		throw new IllegalArgumentException( "Not implemented yet (IntervalWiseOperation)" );
	}

	@Override
	public UnaryOperation< S, U > copy()
	{
		return new IterateUnaryOperation< T, V, S, U >( m_op.copy(), m_selectedDims );
	}
}
