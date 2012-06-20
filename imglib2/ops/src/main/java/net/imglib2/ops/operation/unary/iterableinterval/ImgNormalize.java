package net.imglib2.ops.operation.unary.iterableinterval;

import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.image.UnaryOperationAssignment;
import net.imglib2.ops.operation.binary.real.Normalize;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;

/**
 *
 * @author dietzc, hornm, schoenenbergerf University of Konstanz
 * @param <T>
 *                The type of the {@link Img} which will be normalized. Must be
 *                {@link RealType}
 */
public class ImgNormalize<T extends RealType<T>, I extends IterableInterval<T>>
                implements UnaryOperation<I, I> {

        private double m_saturation;

        private boolean m_manuallyMinMax;

        private Pair<T, T> m_minMax;

        private MinMax<T> m_minMaxOp;

        private Normalize<T> m_normalize;

        /**
         * Normalizes an image.
         */
        public ImgNormalize() {
                this(0);
        }

        /**
         * @param saturation
         *                the percentage of pixels in the lower and upper domain
         *                to be ignored in the normalization
         */
        public ImgNormalize(double saturation) {
                m_saturation = saturation;
                m_manuallyMinMax = false;
        }

        /**
         * @param min
         * @param max
         */
        public ImgNormalize(T min, T max) {

                m_minMax = new Pair<T, T>(min, max);
                m_manuallyMinMax = true;
        }

        /**
         *
         * @param saturation
         *                the percentage of pixels in the lower and upper domain
         *                to be
         * @param numBins
         *                number of bins in the histogram to determine the
         *                saturation, if T is not {@link IntegerType}, 256 bins
         *                are chosen automatically
         */
        public ImgNormalize(ImgFactory<T> fac, double saturation) {
                this(saturation);
        }

        /**
         * {@inheritDoc}
         *
         * @return
         */
        @Override
        public I compute(I in, I res) {
                Pair<T, T> minmax;
                if (!m_manuallyMinMax) {
                        if (m_minMaxOp == null) {
                                m_minMaxOp = new MinMax<T>(m_saturation, in
                                                .firstElement()
                                                .createVariable());
                        }
                        minmax = m_minMaxOp.compute(in);
                        m_normalize = new Normalize<T>(minmax.a, minmax.b);
                } else {
                        minmax = m_minMax;
                        if (m_normalize == null)
                                m_normalize = new Normalize<T>(minmax.a,
                                                minmax.b);
                }

                // actually compute everything
                UnaryOperationAssignment<T, T> imgNormalize = new UnaryOperationAssignment<T, T>(
                                m_normalize);
                imgNormalize.compute(in, res);

                return res;
        }

        /**
         * Determines the minimum and factory for scaling according to the given
         * saturation
         *
         * @param <T>
         * @param interval
         * @param saturation
         *                the percentage of pixels in the lower and upper domain
         *                to be ignored in the normalization
         * @return with the normalization factor at position 0, minimum of the
         *         image at position 1
         */
        public double[] getNormalizationProperties(I interval, double saturation) {

                T type = interval.firstElement().createVariable();
                MinMax<T> minMax = new MinMax<T>(saturation, type);

                Pair<T, T> pair = minMax.compute(interval);
                return new double[] {
                                1
                                                / (pair.b.getRealDouble() - pair.a
                                                                .getRealDouble())
                                                * (type.getMaxValue() - type
                                                                .getMinValue()),
                                pair.a.getRealDouble() };
        }

        @Override
        public UnaryOperation<I, I> copy() {
                return new ImgNormalize<T, I>(m_saturation);
        }

}
