package eu.michalkijowski.carvisor.fragments.myFleet.list;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.UserDTO;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.UsersService;

public class MyFleetListFragment extends Fragment {

    String regex = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UserDTO[] usersDTO = UsersService.getUsersList(regex).getListOfUsers();
        View root = inflater.inflate(R.layout.fragment_my_fleet_list, container, false);
        List<HashMap<String, String>> list = new ArrayList<>();
        for (UserDTO userDTO : usersDTO) {
            HashMap item = new HashMap<String, String>();
            item.put("name", userDTO.getName() + " " + userDTO.getSurname());
            item.put("licensePlate", userDTO.getLicensePlate().equals("------") ? "---" : userDTO.getLicensePlate());
            item.put("active", userDTO.getStatus());
            item.put("distance", userDTO.getDistance()+" km");
            item.put("time", (!userDTO.getStartTime().equals("------") ? userDTO.getStartTime()+" - "+userDTO.getFinishTime() : (!userDTO.getFinishTime().equals("------") ? userDTO.getFinishTime() : "---")));
            //image
            byte[] bytes = Base64.decode(userDTO.getImage().replace("data:image/png;base64,", ""), Base64.DEFAULT);
            Bitmap bitmap = ImageService.getCircleImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            item.put("userImage", new BitmapDrawable(getResources(), bitmap));
            list.add(item);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), list,
                R.layout.fragment_my_fleet_list_row,
                new String[]{"name", "licensePlate", "active", "distance", "time", "userImage"},
                new int[]{R.id.name, R.id.licensePlate, R.id.active, R.id.distance, R.id.time, R.id.userImage});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if (view instanceof ImageView && data instanceof Drawable) {
                    ImageView iv = (ImageView) view;
                    iv.setImageDrawable((Drawable) data);
                    return true;
                } else
                    return false;
            }
        });
        ListView listView = (ListView) root.findViewById(R.id.myFleetListView);
        listView.setAdapter(simpleAdapter);
        return root;
    }
}