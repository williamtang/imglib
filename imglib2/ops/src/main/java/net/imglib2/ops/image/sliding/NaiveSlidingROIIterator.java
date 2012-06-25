package net.imglib2.ops.image.sliding;

import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.iterator.LocalizingIntervalIterator;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.roi.IterableRegionOfInterest;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

/**
 * Naive implementation of a ROI slider. Slides a ROI over a given
 * RandomAccessibleInterval. Take care: position of ROI origin corresponds to
 * (0,0) in rndAccessibleInterval
 * 
 * @author eethyo
 * 
 * @param <T>
 */
public class NaiveSlidingROIIterator< T extends Type< T >, IN extends RandomAccessibleInterval< T >> implements SlidingWindowIterator< T >
{

	private final IterableRegionOfInterest m_roi;

	private final LocalizingIntervalIterator m_cursor;

	private final ExtendedRandomAccessibleInterval< T, IN > m_rndAccessible;

	private final double[] m_currentPos;

	private final double[] m_displacement;

	private double[] m_totalDisplacement;

	public NaiveSlidingROIIterator( final OutOfBoundsFactory< T, IN > fac, IN rndAccessible, final IterableRegionOfInterest roi )
	{
		m_cursor = new LocalizingIntervalIterator( rndAccessible );
		m_rndAccessible = Views.extend( rndAccessible, fac );

		m_roi = roi;
		m_roi.move( -1, 0 );
		m_currentPos = new double[ m_roi.numDimensions() ];
		m_displacement = new double[ m_roi.numDimensions() ];
		m_totalDisplacement = new double[ m_roi.numDimensions() ];

		m_cursor.localize( m_currentPos );
	}

	@Override
	public boolean hasNext()
	{
		return m_cursor.hasNext();
	}

	@Override
	public void fwd()
	{
		m_cursor.fwd();

		for ( int d = 0; d < m_displacement.length; d++ )
		{
			final double pos = m_cursor.getDoublePosition( d );
			m_displacement[ d ] = m_cursor.getDoublePosition( d ) - m_currentPos[ d ];
			m_totalDisplacement[ d ] += m_displacement[ d ];
			m_currentPos[ d ] = pos;
		}

		m_roi.move( m_displacement );
	}

	@Override
	public Iterable< T > getIterable()
	{
		return m_roi.getIterableIntervalOverROI( m_rndAccessible );
	}

	@Override
	public Iterable< T > next()
	{
		fwd();
		return getIterable();
	}

	@Override
	public void reset()
	{
		m_cursor.reset();

		// move roi back
		for ( int d = 0; d < m_cursor.numDimensions(); d++ )
		{
			m_roi.move( -m_displacement[ d ], d );
			m_totalDisplacement[ d ] = 0;
		}
	}
}
