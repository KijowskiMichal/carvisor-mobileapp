package eu.michalkijowski.carvisor.fragments.reports.list;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.ReportDTO;
import eu.michalkijowski.carvisor.services.ReportsService;

public class RowActionDialog extends DialogFragment {

    FragmentManager manager;
    String tag;
    String name;
    Fragment fragment;
    Bundle bundle;
    Context context;
    String filePath;
    private ProgressDialog mProgressDialog;

    public void show(@NonNull FragmentManager manager, @Nullable String tag, String name, Fragment fragment, Bundle bundle, Context context) {
        this.manager = manager;
        this.tag = tag;
        this.fragment = fragment;
        this.bundle = bundle;
        this.name = name;
        this.context = context;
        super.show(manager, tag);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(name)
                .setItems(R.array.reports_list_row_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0) {
                            new DownloadPdf().execute(Integer.valueOf(tag));
                        }
                        else if (which==1) {
                            new RowDeleteDialog().show(manager, tag, name, fragment, context);
                        }
                    }
                });
        return builder.create();
    }

    private class DownloadPdf extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            try {
                byte[] baos = ReportsService.downloadReport(integers[0]);
                File path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File file = File.createTempFile(name.replace(' ', '_'),".pdf", path);
                filePath = file.getAbsolutePath();
                FileOutputStream os = new FileOutputStream(file);
                os.write(baos);
                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            mProgressDialog.dismiss();
            Toast.makeText(context, "Pobrano raport do lokalizacji "+filePath, Toast.LENGTH_LONG).show();
        }
    }

}
