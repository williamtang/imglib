package net.imglib2.algorithm.convolver;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.type.numeric.RealType;

/**
 * @author Christian Dietz (Universit�t Konstanz)
 */
public interface MultiKernelConvolver< T extends RealType< T >, K extends RealType< K >, O extends RealType< O >> extends BinaryOperation< RandomAccessible< T >, RandomAccessibleInterval< K >[], RandomAccessibleInterval< O >>
{

}
