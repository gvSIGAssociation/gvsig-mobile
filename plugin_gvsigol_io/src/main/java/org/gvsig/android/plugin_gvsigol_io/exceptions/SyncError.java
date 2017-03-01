/*
 * Geopaparazzi - Digital field mapping on Android based devices
 * Copyright (C) 2017  Scolab (www.scolab.es)
 * Copyright (C) 2017  HydroloGIS (www.hydrologis.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gvsig.android.plugin_gvsigol_io.exceptions;

/**
 * @author Cesar Martinez Izquierdo (www.scolab.es)
 */
public class SyncError extends Exception {
    public SyncError(String message) {
        super(message);
    }

    public SyncError(String message, Throwable cause) {
        super(message, cause);
    }

    public SyncError(Throwable cause) {
        super(cause);
    }

    public void test() {
        this.getMessage();
    }
}
