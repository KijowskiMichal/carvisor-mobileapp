package eu.michalkijowski.carvisor.fragments.myFleet.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.michalkijowski.carvisor.R;

public class MyFleetAddFragment extends Fragment {

    private String[][] States_Capitals_Population =
            {{"Alabama", "Montgomery", "4,833,722"},
                    {"Alaska", "Juneau", "735,132"},
                    {"Arizona", "Phoenix", "6,626,624"},
                    {"Arkansas", "Little Rock", "2,959,373"},
                    {"California", "Sacramento", "38,332,521"},
                    {"Colorado", "Denver", "5,268,367"},
                    {"Connecticut", "Hartford", "3,596,080"},
                    {"Delaware", "Dover", "925,749"},
                    {"Florida", "Tallahassee", "19,552,860"},
                    {"Georgia", "Atlanta", "9,992,167"},
                    {"Hawaii", "Honolulu", "1,404,054"},
                    {"Idaho", "Boise", "1,612,136"},
                    {"Illinois", "Springfield", "12,882,135"},
                    {"Indiana", "Indianapolis", "6,570,902"},
                    {"Iowa", "Des Moines", "3,090,416"},
                    {"Kansas", "Topeka", "2,893,957"},
                    {"Kentucky", "Frankfort", "4,395,295"},
                    {"Louisiana", "Baton Rouge", "4,625,470"},
                    {"Maine", "Augusta", "1,328,302"}};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        List<HashMap<String, String>> list = new ArrayList<>();
        for (int i = 0; i < States_Capitals_Population.length; i++) {
            HashMap item = new HashMap<String, String>();
            item.put("line1", States_Capitals_Population[i][0]);
            item.put("line2", States_Capitals_Population[i][1]);
            item.put("line3", States_Capitals_Population[i][2]);
            list.add(item);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), list,
                R.layout.fragment_my_fleet_list_row,
                new String[]{"line1", "line2", "line3"},
                new int[]{R.id.name, R.id.active, R.id.licensePlate});
        ListView listView = (ListView) getView().findViewById(R.id.myFleetListView);
        listView.setAdapter(simpleAdapter);
        View root = inflater.inflate(R.layout.fragment_my_fleet_add, container, false);
        return root;
    }
}