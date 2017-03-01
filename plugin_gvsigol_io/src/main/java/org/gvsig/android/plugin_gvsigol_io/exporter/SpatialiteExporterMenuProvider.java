package org.gvsig.android.plugin_gvsigol_io.exporter;

import android.content.Intent;
import android.os.IBinder;

import eu.geopaparazzi.library.plugin.PluginService;
import eu.geopaparazzi.library.plugin.types.MenuEntryList;


/**
 * @author Cesar Martinez Izquierdo  (www.scolab.es)
 */
public class SpatialiteExporterMenuProvider extends PluginService {
    private static final String NAME = "SpatialiteImporterMenuProvider";
    private MenuEntryList list = null;
    public SpatialiteExporterMenuProvider() {
        super(NAME);
    }

    public IBinder onBind (Intent intent) {
        if (list==null) {
            list = new MenuEntryList();
            list.addEntry(new SpatialiteExporterMenuEntry(this));
        }
        return list;
    }


}
