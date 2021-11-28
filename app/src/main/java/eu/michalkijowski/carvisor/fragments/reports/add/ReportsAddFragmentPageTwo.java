package eu.michalkijowski.carvisor.fragments.reports.add;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.ReportAddDTO;
import eu.michalkijowski.carvisor.data_models.UserAddDTO;
import eu.michalkijowski.carvisor.fragments.myFleet.add.MyFleetAddFragment;
import eu.michalkijowski.carvisor.services.ReportsService;
import eu.michalkijowski.carvisor.services.UsersService;

public class ReportsAddFragmentPageTwo extends Fragment {
    ImageView imageView;
    String image;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public ReportAddDTO validate(View view) {
        try {
            String dateFrom = ((EditText)getView().findViewById(R.id.editTextDate)).getText().toString();
            String dateTo = ((EditText)getView().findViewById(R.id.editTextDate2)).getText().toString();

            if (dateFrom.equals("")) return null;
            if (dateTo.equals("")) return null;

            Bundle bundle = getArguments();

            ReportAddDTO reportAddDTO = new ReportAddDTO();
            reportAddDTO.setDescription(bundle.getString("description"));
            reportAddDTO.setName(bundle.getString("name"));
            reportAddDTO.setType(bundle.getString("type"));
            reportAddDTO.setListOfUserIds(bundle.getIntArray("selected"));
            reportAddDTO.setStart((new SimpleDateFormat("dd/MM/yyyy").parse(dateFrom)).getTime()/1000);
            reportAddDTO.setEnd(((new SimpleDateFormat("dd/MM/yyyy").parse(dateFrom)).getTime()/1000)+86399);

            return reportAddDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_reports_add_page_two, container, false);
        Button registerButton = root.findViewById(R.id.button2);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportAddDTO reportAddDTO = validate(view);
                if (reportAddDTO!=null) {
                    new PostNewReport().execute(reportAddDTO);
                }
                else {
                    Toast.makeText(getContext(), "Nie wszystkie pola wypełniono poprawnie.", Toast.LENGTH_LONG).show();
                }
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(ReportsAddFragmentPageTwo.this)
                        .navigate(R.id.action_nav_report_add_page_two_to_nav_reports_add_page_one,bundle);
            }
        });
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        ((EditText)root.findViewById(R.id.editTextDate)).setText(date);
        ((EditText)root.findViewById(R.id.editTextDate2)).setText(date);
        return root;
    }

    private class PostNewReport extends AsyncTask<ReportAddDTO, Void, Boolean> {
        @Override
        protected Boolean doInBackground(ReportAddDTO... reportAddDTOS) {
            return ReportsService.addReport(reportAddDTOS[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            mProgressDialog.dismiss();
            if (bool) {
                Toast.makeText(getContext(), "Rozpoczęto generowanie raportu.", Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(ReportsAddFragmentPageTwo.this)
                        .navigate(R.id.action_nav_report_add_page_two_to_nav_reports,bundle);
            }
            else {
                Toast.makeText(getContext(), "Coś poszło nie tak.", Toast.LENGTH_LONG).show();
            }
        }
    }
}