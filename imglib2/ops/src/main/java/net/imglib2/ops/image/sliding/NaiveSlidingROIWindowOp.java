package net.imglib2.ops.image.sliding;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.iterator.LocalizingIntervalIterator;
import net.imglib2.roi.IterableRegionOfInterest;
import net.imglib2.type.Type;

/**
 * Naive implementation of a ROI slider. Slides a ROI over a given
 * RandomAccessibleInterval
 * 
 * @author eethyo
 * 
 * @param <T>
 */
public class NaiveSlidingROIWindowOp< T extends Type< T >> implements SlidingWindowIterator< T >
{

	private final IterableRegionOfInterest m_roi;

	private final LocalizingIntervalIterator m_intervalIterator;

	private final double[] m_currentPos;

	private final double[] m_displacement;

	private final RandomAccessibleInterval< T > m_rndAccessible;

	public NaiveSlidingROIWindowOp( RandomAccessibleInterval< T > rndAccessible, final IterableRegionOfInterest roi )
	{
		m_intervalIterator = new LocalizingIntervalIterator( rndAccessible );
		m_rndAccessible = rndAccessible;
		m_roi = roi;
		m_roi.move( -1, 0 );
		m_currentPos = new double[ m_roi.numDimensions() ];
		m_displacement = new double[ m_roi.numDimensions() ];
		m_intervalIterator.localize( m_currentPos );
	}

	@Override
	public boolean hasNext()
	{
		return m_intervalIterator.hasNext();
	}

	@Override
	public void fwd()
	{
		m_intervalIterator.fwd();

		for ( int d = 0; d < m_displacement.length; d++ )
		{
			final double pos = m_intervalIterator.getDoublePosition( d );
			m_displacement[ d ] = m_intervalIterator.getDoublePosition( d ) - m_currentPos[ d ];
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
}
