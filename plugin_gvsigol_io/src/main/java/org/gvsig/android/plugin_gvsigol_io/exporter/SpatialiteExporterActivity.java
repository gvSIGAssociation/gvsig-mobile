/*
 * Geopaparazzi - Digital field mapping on Android based devices
 * Copyright (C) 2016  HydroloGIS (www.hydrologis.com)
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
package org.gvsig.android.plugin_gvsigol_io.exporter;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.gvsig.android.plugin_gvsigol_io.R;
import org.gvsig.android.plugin_gvsigol_io.WebDataManager;
import org.gvsig.android.plugin_gvsigol_io.exceptions.ServerError;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.geopaparazzi.library.core.maps.SpatialiteMap;
import eu.geopaparazzi.library.database.GPLog;
import eu.geopaparazzi.library.util.GPDialogs;
import eu.geopaparazzi.library.util.StringAsyncTask;
import eu.geopaparazzi.spatialite.database.spatial.SpatialiteSourcesManager;

import static eu.geopaparazzi.library.util.LibraryConstants.DATABASE_ID;
import static eu.geopaparazzi.library.util.LibraryConstants.PREFS_KEY_PWD;
import static eu.geopaparazzi.library.util.LibraryConstants.PREFS_KEY_URL;
import static eu.geopaparazzi.library.util.LibraryConstants.PREFS_KEY_USER;

/**
 * Web projects listing activity.
 *
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class SpatialiteExporterActivity extends ListActivity {
    public static final int DOWNLOADDATA_RETURN_CODE = 667;

    protected static final String ASYNC_ERROR = "OK"; //$NON-NLS-1$
    protected static final String ASYNC_OK = "OK"; //$NON-NLS-1$

    private List<File> dataListToLoad = new ArrayList<>();

    private String user;
    private String pwd;
    private String url;

    private ProgressDialog downloadDataListDialog;
    private ProgressDialog cloudProgressDialog;
    private String[] databases;
    private ArrayAdapter<File> arrayAdapter;
    private StringAsyncTask uploadTask;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.webdatauploadlist);

        Bundle extras = getIntent().getExtras();
        user = extras.getString(PREFS_KEY_USER);
        pwd = extras.getString(PREFS_KEY_PWD);
        url = extras.getString(PREFS_KEY_URL);
        databases = extras.getStringArray(DATABASE_ID);

        refreshList();
    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        refreshList();
//    }

//    @Override
//    protected void onPause() {
//        GPDialogs.dismissProgressDialog(downloadDataListDialog);
//        GPDialogs.dismissProgressDialog(cloudProgressDialog);
//        super.onPause();
//    }

//    protected void onDestroy() {
//        super.onDestroy();
//    }


    private void refreshList() {
        if (GPLog.LOG)
            GPLog.addLogEntry(this, "refreshing projects list"); //$NON-NLS-1$
        for (String dbPath : databases) {
            File f = new File(dbPath);
            if (f.exists())
                dataListToLoad.add(f);
        }
        arrayAdapter = new ArrayAdapter<File>(this, R.layout.webdatauploadrow, dataListToLoad) {
            @Override
            public View getView(final int position, View cView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.webdatauploadrow, null);
                TextView titleText = (TextView) rowView.findViewById(R.id.titletext);
                titleText.setText(dataListToLoad.get(position).getName());
                TextView descrText = (TextView) rowView.findViewById(R.id.descriptiontext);
                descrText.setText(dataListToLoad.get(position).getParentFile().getAbsolutePath());

                Button uploadButton = (Button) rowView.findViewById(R.id.uploadFinishButton);
                uploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upload(position, true);
                    }
                });

                uploadButton = (Button) rowView.findViewById(R.id.uploadContinueButton);
                uploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upload(position, false);
                    }
                });
                return rowView;
            }
        };

        setListAdapter(arrayAdapter);
    }

    private void upload(final int position, final boolean finish) {
        //FIXME: this will probably fail if the phone is rotated while uploading/downloading
        //See the proper way to do it in:
        // https://developer.android.com/training/basics/network-ops/connecting.html#config-changes
        uploadTask = new StringAsyncTask(this) {
            protected String doBackgroundWork() {
                try {
                    String result;
                    File db = dataListToLoad.get(position);
                    if (finish) {
                        result = WebDataManager.INSTANCE.uploadData(SpatialiteExporterActivity.this, db, url, user, pwd);
                        dataListToLoad.get(position).delete();
                        List<SpatialiteMap> maps = SpatialiteSourcesManager.INSTANCE.getSpatialiteMaps();
                        for (SpatialiteMap map: maps) {
                            if (map.databasePath.equals(db.getPath())) {
                                SpatialiteSourcesManager.INSTANCE.removeSpatialiteMap(map);
                            }
                        }
                    }
                    else {
                        result = WebDataManager.INSTANCE.uploadData(SpatialiteExporterActivity.this, db, url, user, pwd, WebDataManager.UPLOAD_AND_CONTINUE_DATA);
                    }
                    return ASYNC_OK;
                } catch (ServerError e) {
                    return e.getMessage();
                } catch (Exception e) {
                    return "ERROR: " + e.getMessage();
                }
            }

            protected void doUiPostWork(String response) {
                if (ASYNC_OK.equals(response)) {
                    String message = getResources().getString(eu.geopaparazzi.library.R.string.file_upload_completed_properly);

                    GPDialogs.infoDialog(SpatialiteExporterActivity.this, message, new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
                else {
                    GPDialogs.warningDialog(SpatialiteExporterActivity.this, getErrorMessage(response), null);
                }

            }
        };
        String progressMessage = getResources().getString(R.string.uploading_data_to_cloud);
        uploadTask.setProgressDialog(null, progressMessage, false, null);
        uploadTask.execute();
    }

    @Override
    protected void onDestroy() {
        if (uploadTask!= null) uploadTask.dispose();
        super.onDestroy();
    }

    protected String getErrorMessage(String response) {
        String message;
        if (response == null) {
            message = getResources().getString(R.string.error_uploading_the_data);
        }
        else if (response.startsWith("ERROR_GOL_")) {
            response = response.substring(10);
            if (response.startsWith("ERROR_GOL_201")) {
                String[] responseParts = response.split(":", 2);
                String layerName;
                if (responseParts.length==2) {
                    layerName = responseParts[1];
                }
                else {
                    layerName = "";
                }
                message = String.format(getResources().getString(R.string.layer_is_not_locked), layerName);
            }
            else {
                message = getResources().getString(R.string.error_uploading_the_data);
            }
        }
        else {
            message = "ERROR: "+response;
        }
        return message;

    }
}
