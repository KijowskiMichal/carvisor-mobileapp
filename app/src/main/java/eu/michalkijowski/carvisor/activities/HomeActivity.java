package eu.michalkijowski.carvisor.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.NewNotificationDTO;
import eu.michalkijowski.carvisor.data_models.NotificationMessageDTO;
import eu.michalkijowski.carvisor.fragments.devices.list.DevicesListFragment;
import eu.michalkijowski.carvisor.services.AuthorizationService;
import eu.michalkijowski.carvisor.services.NotificationService;

public class HomeActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "CARVISOR_CHANEL_ID";
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private DrawerLayout drawer;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    int idPopup = 0;
    int ID_POPUP_OFFSET = 1876;
    public static ArrayList<String> listOfFindedNFC = new ArrayList<>();

    public static NfcAdapter mAdapter;
    public static PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_short)
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        switch (AuthorizationService.authorizationStatus.getRbac()) {
            case "ADMINISTRATOR":
                nav_Menu.findItem(R.id.nav_map_standard_user).setVisible(false);
                break;
            case "MODERATOR":
                nav_Menu.findItem(R.id.nav_map_standard_user).setVisible(false);
                break;
            case "STANDARD_USER":
                nav_Menu.findItem(R.id.nav_my_fleet).setVisible(false);
                nav_Menu.findItem(R.id.nav_devices).setVisible(false);
                nav_Menu.findItem(R.id.nav_map).setVisible(false);
                nav_Menu.findItem(R.id.nav_ecopoints).setVisible(false);
                nav_Menu.findItem(R.id.nav_safetypoints).setVisible(false);
                nav_Menu.findItem(R.id.nav_reports).setVisible(false);
                nav_Menu.findItem(R.id.nav_calendar).setVisible(false);
                nav_Menu.findItem(R.id.nav_zones).setVisible(false);
                ((ImageButton) findViewById(R.id.imageButton)).setVisibility(View.GONE);
                break;
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_my_fleet, R.id.nav_devices, R.id.nav_settings, R.id.nav_ecopoints, R.id.nav_safetypoints, R.id.nav_reports, R.id.nav_map, R.id.nav_map_standard_user, R.id.nav_calendar, R.id.nav_errors, R.id.nav_notification, R.id.nav_summary, R.id.nav_zones)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NotificationService.getNewNotifications();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new NewNotification().execute();
                    }
                });
            }
        }, 0, 5000);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            //nfc not support your device.
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    @Override
    protected void onPause() {
        super.onPause();
        disableNfcSearching();
    }

    public void enableNfcSearching() {
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    public void disableNfcSearching() {
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getTagInfo(intent);
    }

    private void getTagInfo(Intent intent) {
        byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        String hexdump = new String();
        for (int i = 0; i < tagId.length; i++) {
            String x = Integer.toHexString(((int) tagId[i] & 0xff));
            if (x.length() == 1) {
                x = '0' + x;
            }
            hexdump += x;
        }
        listOfFindedNFC.add(hexdump);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout(View view) {
        AuthorizationService.logout();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void settings(View view) {
        navController.navigate(R.id.nav_settings);
        drawer.close();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private class NewNotification extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<NotificationMessageDTO> listOfNewNotification = NotificationService.getNotificationMessageList(getApplicationContext());
            for (NotificationMessageDTO newNotification : listOfNewNotification) {
                builder.setContentTitle(newNotification.getTitle())
                        .setContentText(newNotification.getContent());
                notificationManager.notify(ID_POPUP_OFFSET+idPopup, builder.build());
                idPopup++;
            }
            return null;
        }
    }
}