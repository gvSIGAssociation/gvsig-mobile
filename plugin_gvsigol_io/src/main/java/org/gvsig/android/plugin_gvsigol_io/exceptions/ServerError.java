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
 * Exception raised when the server response code is different from
 * 2XX (success) codes.
 *
 * @author Cesar Martinez Izquierdo (www.scolab.es)
 */
public class ServerError extends SyncError {

    private final int code;

    public ServerError(String message, int httpCode) {
        super(message);
        this.code = httpCode;
    }

    /**
     * Gets the HTTP code returned by the server
     * @return
     */
    public int getCode(){
        return this.code;
    }
}
