/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2008 - 2012
 * KNIME.com, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 * History
 *   Sep 4, 2012 (hornm): created
 */

package net.imglib2.img.file;

import java.io.File;
import java.util.UUID;

import net.imglib2.img.file.FileImgFactory.FileNameGenerator;

/**
 *
 * @author Martin Horn, University of Konstanz
 *
 */
public class RandomFileNameGenerator implements FileNameGenerator {

    private final String directory;

    public RandomFileNameGenerator(final String directory) {

        if (!directory.endsWith(File.separator)) {
            this.directory = directory + File.separator;
        } else {
            this.directory = directory;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String nextAbsoluteFileName() {
        return directory + UUID.randomUUID().toString();
    }
}
