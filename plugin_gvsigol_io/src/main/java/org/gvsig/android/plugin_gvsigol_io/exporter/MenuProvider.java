package org.gvsig.android.plugin_gvsigol_io.exporter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import org.gvsig.android.plugin_gvsigol_io.R;

import java.util.ArrayList;
import java.util.List;

import eu.geopaparazzi.core.utilities.Constants;
import eu.geopaparazzi.library.core.maps.SpatialiteMap;
import eu.geopaparazzi.library.network.NetworkUtilities;
import eu.geopaparazzi.library.plugin.PluginService;
import eu.geopaparazzi.library.plugin.types.MenuEntry;
import eu.geopaparazzi.library.plugin.types.MenuEntryList;
import eu.geopaparazzi.library.util.GPDialogs;
import eu.geopaparazzi.library.util.IActivitySupporter;
import eu.geopaparazzi.library.util.LibraryConstants;
import eu.geopaparazzi.spatialite.database.spatial.SpatialiteSourcesManager;

import static eu.geopaparazzi.library.util.LibraryConstants.DATABASE_ID;


/**
 * @author Cesar Martinez Izquierdo  (www.scolab.es)
 */
public class MenuProvider extends PluginService {
    private static final String NAME = "SpatialiteImporterMenuProvider";
    private MenuEntryList list = null;
    public MenuProvider() {
        super(NAME);
    }

    public IBinder onBind (Intent intent) {
        if (list==null) {
            list = new MenuEntryList();
            list.addEntry(new CustomMenuEntry());
        }
        return list;
    }


    public class CustomMenuEntry extends MenuEntry {
        @Override
        public String getLabel() {
            return "gvSIG Online";
        }

        @Override
        public void onClick(IActivitySupporter starter) {
            Context context = starter.getContext();

            if (!NetworkUtilities.isNetworkAvailable(context)) {
                GPDialogs.infoDialog(context, context.getString(R.string.available_only_with_network), null);
                return;
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            final String user = preferences.getString(Constants.PREF_KEY_USER, "geopaparazziuser"); //$NON-NLS-1$
            final String pwd = preferences.getString(Constants.PREF_KEY_PWD, "geopaparazzipwd"); //$NON-NLS-1$
            final String serverUrl = preferences.getString(Constants.PREF_KEY_SERVER, ""); //$NON-NLS-1$
            if (serverUrl.length() == 0) {
                GPDialogs.infoDialog(context, getString(R.string.error_set_cloud_settings_gvsigol), null);
                return;
            }

            Intent webExportIntent = new Intent(context, SpatialiteExporterActivity.class);
            webExportIntent.putExtra(LibraryConstants.PREFS_KEY_URL, serverUrl);
            webExportIntent.putExtra(LibraryConstants.PREFS_KEY_USER, user);
            webExportIntent.putExtra(LibraryConstants.PREFS_KEY_PWD, pwd);
            List<SpatialiteMap> spatialiteMaps = SpatialiteSourcesManager.INSTANCE.getSpatialiteMaps();
            List<String> databases = new ArrayList<String>();
            for (int i = 0; i < spatialiteMaps.size(); i++) {
                String dbPath = spatialiteMaps.get(i).databasePath;
                if (!databases.contains(dbPath)) {
                    databases.add(dbPath);
                }
            }
            webExportIntent.putExtra(DATABASE_ID, databases.toArray(new String[0]));
            startActivity(webExportIntent);
        }
    }

}
