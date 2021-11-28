package eu.michalkijowski.carvisor.fragments.reports.add;

import android.app.ProgressDialog;
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

import eu.michalkijowski.carvisor.R;

public class ReportsAddFragmentPageZero extends Fragment {
    ImageView imageView;
    String image;
    private ProgressDialog mProgressDialog;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public Bundle validate(View view) {
        try {
            String type = ((Spinner)getView().findViewById(R.id.spinner)).getSelectedItem().toString();
            String name = ((EditText)getView().findViewById(R.id.editTextTextPersonName6)).getText().toString();
            String description = ((EditText)getView().findViewById(R.id.editTextTextPersonName11)).getText().toString();

            if (type.equals("")) return null;
            if (name.equals("")) return null;
            if (description.equals("")) return null;

            switch (type) {
                case "Raport tras":
                    type = "TRACK";
                    break;
                case "Raport ekojazdy":
                    type = "ECO";
                    break;
                case "Raport bezpieczeństwa":
                    type = "SAFETY";
                    break;
            }

            Bundle bundle = new Bundle();
            bundle.putString("type", type);
            bundle.putString("name", name);
            bundle.putString("description", description);

            return bundle;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_reports_add_page_zero, container, false);
        /********************************
         * Image selector
         *******************************/
        Button registerButton = root.findViewById(R.id.button2);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = validate(view);
                if (bundle!=null) {
                    NavHostFragment.findNavController(ReportsAddFragmentPageZero.this)
                        .navigate(R.id.action_nav_report_add_page_zero_to_nav_reports_add_page_one,bundle);
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
                NavHostFragment.findNavController(ReportsAddFragmentPageZero.this)
                        .navigate(R.id.action_nav_report_add_page_zero_to_nav_report,bundle);
            }
        });
        /********************************
         * Select (Spinners)
         *******************************/
        spinner = (Spinner) root.findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.raports_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return root;
    }
}