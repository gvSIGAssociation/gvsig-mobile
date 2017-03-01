package org.gvsig.android.plugin_gvsigol_io.importer;

import android.content.Intent;
import android.os.IBinder;


import eu.geopaparazzi.library.plugin.PluginService;
import eu.geopaparazzi.library.plugin.types.MenuEntryList;


/**
 * @author Cesar Martinez Izquierdo  (www.scolab.es)
 */
public class SpatialiteImporterMenuProvider extends PluginService {
    private static final String NAME = "SpatialiteImporterMenuProvider";
    private MenuEntryList list = null;
    public SpatialiteImporterMenuProvider() {
        super(NAME);
    }

    public IBinder onBind (Intent intent) {
        if (list==null) {
            list = new MenuEntryList();
            list.addEntry(new SpatialiteImportMenuEntry(this));
        }
        return list;
    }


}
