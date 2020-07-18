package ru.sergeiandreev.tvseriesinformer.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import java.util.ArrayList;

import ru.sergeiandreev.tvseriesinformer.AddSitesToTheList;
import ru.sergeiandreev.tvseriesinformer.R;
import ru.sergeiandreev.tvseriesinformer.adapters.MainAdapter;
import ru.sergeiandreev.tvseriesinformer.database.Utils;
import ru.sergeiandreev.tvseriesinformer.database.writeResult;
import ru.sergeiandreev.tvseriesinformer.dialogs.DialogFragmentList;
import ru.sergeiandreev.tvseriesinformer.recievers.BootReciever;
import ru.sergeiandreev.tvseriesinformer.searchclasses.EpscapeSearch;
import ru.sergeiandreev.tvseriesinformer.searchclasses.Result;
import ru.sergeiandreev.tvseriesinformer.searchclasses.Search;
import ru.sergeiandreev.tvseriesinformer.searchclasses.SerialDataSearch;
import ru.sergeiandreev.tvseriesinformer.serialclasses.Serial;
import ru.sergeiandreev.tvseriesinformer.services.UpdateService;


public class MainActivity extends AppCompatActivity implements DialogFragmentList.EditNameDialogListener {
    //theme variables
    private int mResultTheme;
    //components activity
    private ListView lv;
    private ProgressBar progressBar;
    private EditText searchField;
    AppCompatButton apButton;
    //classes
    private DialogFragment dlgList;
    private MainAdapter mainAdapterView;
    //other variables
    private Result taskResult;
    private PublisherInterstitialAd mInterstitialAd;
    private final String APP_ID = "ca-app-pub-8802461501886979/7570869031";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getRandomTheme();
        setContentView(R.layout.activity_main);
        initAll();
        mInterstitialAd = new PublisherInterstitialAd(this);
        mInterstitialAd.setAdUnitId(APP_ID);
        mInterstitialAd.loadAd(new PublisherAdRequest.Builder().build());

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }

        Intent intent = new Intent(this, BootReciever.class);
        sendBroadcast(intent);
        //Intent intent1 = new Intent(this, UpdateService.class);
        //startService(intent1);
    }

    //theme methods
    private void getRandomTheme() {
        int min = 1; // Минимальное число для диапазона
        int max = 4; // Максимальное число для диапазона
        max -= min;
        mResultTheme = (int) (Math.random() * ++max) + min;
        switch (mResultTheme) {
            case 1:
                setTheme(R.style.AppThemeBlue);
                break;
            case 2:
                setTheme(R.style.AppThemeRed);
                break;
            case 3:
                setTheme(R.style.AppThemeGreen);
                break;
            case 4:
                setTheme(R.style.AppThemeYellow);
                break;
            default:
                break;
        }
    }

    private int getRandomImage() {
        int min = 1; // Минимальное число для диапазона
        int max = 2; // Максимальное число для диапазона
        max -= min;
        return (int) (Math.random() * ++max) + min;
    }

    //init all components MainAcivity include theme, list. permission
    private void initAll() {
        //components activity
        apButton = findViewById(R.id.button_search);
        apButton.setOnClickListener(OnClickListenerSearch);
        ImageView mainImageOne = findViewById(R.id.main_image);
        ImageView mainImageTwo = findViewById(R.id.main_image2);
        ImageView mainImageThree = findViewById(R.id.main_image3);
        lv = findViewById(R.id.series_list);
        lv.setOnItemClickListener(OnItemClickListener);
        lv.setOnCreateContextMenuListener(OnCreateContextMenuListener);
        searchField = findViewById(R.id.edit_search);
        progressBar = findViewById(R.id.progressBar_cyclic);
        dlgList = new DialogFragmentList();
        dlgList.setCancelable(false);
        switch (mResultTheme) {
            case 1:
                int mResultImage = getRandomImage();
                switch (mResultImage) {
                    case 1:
                        mainImageOne.setImageDrawable(getDrawable(R.drawable.blue_1));
                        mainImageTwo.setImageDrawable(getDrawable(R.drawable.blue_3));
                        mainImageThree.setImageDrawable(getDrawable(R.drawable.blue_5));
                        break;
                    case 2:
                        mainImageOne.setImageDrawable(getDrawable(R.drawable.blue_2));
                        mainImageTwo.setImageDrawable(getDrawable(R.drawable.blue_4));
                        mainImageThree.setImageDrawable(getDrawable(R.drawable.blue_6));
                        break;
                }
                apButton.setTextAppearance(this, R.style.AppTheme_Button_Blue);
                break;
            case 2:
                mResultImage = getRandomImage();
                switch (mResultImage) {
                    case 1:
                        mainImageOne.setImageDrawable(getDrawable(R.drawable.red_1));
                        mainImageTwo.setImageDrawable(getDrawable(R.drawable.red_3));
                        mainImageThree.setImageDrawable(getDrawable(R.drawable.red_5));
                        break;
                    case 2:
                        mainImageOne.setImageDrawable(getDrawable(R.drawable.red_2));
                        mainImageTwo.setImageDrawable(getDrawable(R.drawable.red_4));
                        mainImageThree.setImageDrawable(getDrawable(R.drawable.red_6));
                        break;
                }
                apButton.setTextAppearance(this, R.style.AppTheme_Button_Red);
                break;
            case 3:
                mResultImage = getRandomImage();
                switch (mResultImage) {
                    case 1:
                        mainImageOne.setImageDrawable(getDrawable(R.drawable.green_1));
                        mainImageTwo.setImageDrawable(getDrawable(R.drawable.green_3));
                        mainImageThree.setImageDrawable(getDrawable(R.drawable.green_5));
                        break;
                    case 2:
                        mainImageOne.setImageDrawable(getDrawable(R.drawable.green_2));
                        mainImageTwo.setImageDrawable(getDrawable(R.drawable.green_4));
                        mainImageThree.setImageDrawable(getDrawable(R.drawable.green_6));
                        break;
                }
                apButton.setTextAppearance(this, R.style.AppTheme_Button_Green);
                break;
            case 4:
                mResultImage = getRandomImage();
                switch (mResultImage) {
                    case 1:
                        mainImageOne.setImageDrawable(getDrawable(R.drawable.yellow_1));
                        mainImageTwo.setImageDrawable(getDrawable(R.drawable.yellow_3));
                        mainImageThree.setImageDrawable(getDrawable(R.drawable.yellow_5));
                        break;
                    case 2:
                        mainImageOne.setImageDrawable(getDrawable(R.drawable.yellow_2));
                        mainImageTwo.setImageDrawable(getDrawable(R.drawable.yellow_4));
                        mainImageThree.setImageDrawable(getDrawable(R.drawable.yellow_6));
                        break;
                }
                apButton.setTextAppearance(this, R.style.AppTheme_Button_Yellow);
                break;
            default:
                break;
        }
        displayDataBase();
        requestPermission();

        startService(new Intent(this, UpdateService.class)); //ДЛЯ проверки
        //startService(new Intent(this, NotificationService.class));
    }

    private void displayDataBase() {
        if (mainAdapterView != null) mainAdapterView.clear();
        mainAdapterView = new MainAdapter(getApplicationContext(), R.layout.activity_main, new Utils(this).displayDataBase(this));
        lv.setAdapter(mainAdapterView);
    }

    private void requestPermission() {
        ArrayList<String> arrPerm = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        }
        if (!arrPerm.isEmpty()) {
            String[] permissions = new String[arrPerm.size()];
            permissions = arrPerm.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 999);
        }
    }

    //OnClick methods
    private AppCompatButton.OnClickListener OnClickListenerSearch = new AppCompatButton.OnClickListener() {

        @Override
        public void onClick(View view) {
            progressBar.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm.isAcceptingText()) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            progressBar.setVisibility(View.VISIBLE);
            AsyncSearch searchTask = new AsyncSearch();
            searchTask.execute(searchField.getText().toString());
            searchField.setText("");
            searchField.setEnabled(false);
            apButton.setEnabled(false);
        }
    };

    private ListView.OnCreateContextMenuListener OnCreateContextMenuListener = new ListView.OnCreateContextMenuListener() {

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(getString(R.string.delete_element)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
                    if (!new Utils(getApplicationContext()).deleteSerial(getApplicationContext(), mainAdapterView.getItem(info.position).getName())) {
                        Toast toast = Toast.makeText(MainActivity.this, getString(R.string.message_deleted_image), Toast.LENGTH_LONG);
                        toast.show();
                    }
                    displayDataBase();
                    return true;
                }
            });
        }
    };

    private ListView.OnItemClickListener OnItemClickListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            TextView nextEpisodeInfo = view.findViewById(R.id.text_date);
            //if (!nextEpisodeInfo.getText().toString().contains("Сериал")) {
                Intent intent = new Intent(MainActivity.this, EpisodesList.class);
                intent.putExtra("serial", adapterView.getItemAtPosition(position).toString());
                startActivity(intent);
            //}
        }
    };

    //search class
    public class AsyncSearch extends AsyncTask<String, Integer, Result> {
        private Search epscapeSearch;
        private Search serialDataSearch;
        private Result epscapeResult;
        private Result serialDataResult;
        private ArrayList<String> links = new ArrayList<>();

        @Override
        protected Result doInBackground(String... strings) {
            Result result = new Result();
            if(links.size()==0) {
                links.addAll(new AddSitesToTheList().addSites());
            }
            epscapeSearch = new EpscapeSearch();
            serialDataSearch = new SerialDataSearch();
            if (strings[0].length() > 0) {
                try {
                    epscapeResult = epscapeSearch.mainSearch(links.get(0) + strings[0]);
                    serialDataResult = serialDataSearch.mainSearch(links.get(1) + strings[0]);
                    result = checkResult(epscapeResult, serialDataResult);
                    result.setWorkResult(true);
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                    result.setWorkResult(false);
                    return result;
                }
            }else{
                result.setWorkResult(false);
                return result;
            }
        }

        private Result checkResult(Result result1, Result result2) {
            ArrayList<Serial> list = new ArrayList<>();
            list.addAll(result1.getSerialList());
            list.addAll(result2.getSerialList());
            Result result = new Result();
            result.setSerialList(list);
            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            onFinishAsyncTask(result);
        }
    }

    //processing results
    public void onFinishAsyncTask(Result result) {
        if (result.isWorkResult()) {
            taskResult = result;
            if (result.getSerialList().size() == 1) {
                writeResult wr = new Utils(this).writeToDatabase(result.getSerialList().get(0), getFilesDir());
                switch (wr) {
                    case SERIAL_ALLREADY_EXIST:
                        Toast.makeText(this, getString(R.string.serial_already_have), Toast.LENGTH_LONG).show();
                        break;
                    case SERIAL_ADDED:
                        Toast.makeText(this, getString(R.string.serial_added), Toast.LENGTH_LONG).show();
                        displayDataBase();
                        break;
                }
            } else {
                if (result.getSerialList().size() > 1) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("arraylist", result.getSerialList());
                    bundle.putInt("theme", mResultTheme);
                    dlgList.setArguments(bundle);
                    dlgList.show(getSupportFragmentManager(), dlgList.getClass().getName());
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.error_search), Toast.LENGTH_LONG).show();
            taskResult = null;
        }
        progressBar.setVisibility(View.INVISIBLE);
        searchField.setEnabled(true);
        apButton.setEnabled(true);
    }

    @Override
    public void onFinishEditDialog(int position) {
        if (position != -1) {
            Utils utils = new Utils(this);
            writeResult wr = utils.writeToDatabase(taskResult.getSerialList().get(position), getFilesDir());
            switch (wr) {
                case SERIAL_ALLREADY_EXIST:
                    Toast.makeText(this, getString(R.string.serial_already_have), Toast.LENGTH_LONG).show();
                    break;
                case SERIAL_ADDED:
                    Toast.makeText(this, getString(R.string.serial_added), Toast.LENGTH_LONG).show();
                    displayDataBase();
                    break;
            }
        }
    }

}
