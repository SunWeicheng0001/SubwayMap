package com.sudalv.subway.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.sudalv.subway.R;
import com.sudalv.subway.fragment.LineFragment;
import com.sudalv.subway.fragment.MapFragment;
import com.sudalv.subway.fragment.NavigationDrawerFragment;
import com.sudalv.subway.fragment.NearbyFragment;
import com.sudalv.subway.fragment.SettingFragment;
import com.sudalv.subway.fragment.UserFragment;
import com.sudalv.subway.util.FileUtils;
import com.sudalv.subway.util.UncaughtExceptionHandler;

import java.io.File;

public class LauncherActivity extends Activity
        implements  NavigationDrawerFragment.NavigationDrawerCallbacks{

    public static String user_select_start = "";
    public static String user_select_end = "";
    public static String user_name = "";
    public static int user_sex = 0;
    public static PowerManager powerManager = null;
    public static PowerManager.WakeLock wakeLock = null;
    public static boolean wake = false;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Fragment currentFragment, lastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        try {
            mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
            mTitle = getTitle();
            // 设置抽屉
            mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
            //捕获uncaught异常
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(this));
            powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
            String raw_wake = FileUtils.readFromFile(this.getFilesDir(), "wake_lock");
            if (raw_wake.equals("1")) {
                wake = true;
            } else {
                wake = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void changeUserHeader(){
        mNavigationDrawerFragment.changeUserHeader();
    }

    @Override
    public void onNavigationDrawerItemSelected(String title) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        currentFragment = fragmentManager.findFragmentByTag(title);
        if(currentFragment == null && title.equals("实时")) {
            currentFragment = MapFragment.newInstance(title);
            ft.add(R.id.container, currentFragment, title);
        }else if(title.equals("出行")){
            currentFragment = LineFragment.newInstance(title);
            ft.replace(R.id.container, currentFragment, title);
        }else if(title.equals("用户")){
            currentFragment = UserFragment.newInstance(title);
            ft.replace(R.id.container, currentFragment, title);
        } else if (title.equals("设置")) {
            currentFragment = SettingFragment.newInstance(title);
            ft.replace(R.id.container, currentFragment, title);
        } else if (title.equals("周边")) {
            currentFragment = NearbyFragment.newInstance(title);
            ft.replace(R.id.container, currentFragment, title);
        }
        if(lastFragment != null) {
            ft.hide(lastFragment);
        }
        if(currentFragment.isDetached()){
            ft.attach(currentFragment);
        }
        ft.show(currentFragment);
        lastFragment = currentFragment;
        ft.commit();
        onSectionAttached(title);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            onSectionAttached(getFragmentManager().getBackStackEntryAt(0).getName());
        } else {
            super.onBackPressed();
        }
    }

    public void onSectionAttached(String title) {
        mTitle = title;
        restoreActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wake)
            wakeLock.acquire();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (wake)
            wakeLock.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "faceimage_cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode ==  Activity.RESULT_OK) {
            ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
            View view = getLayoutInflater().inflate(R.layout.fragment_user,viewGroup, false);
            ImageView faceImage = (ImageView)view.findViewById(R.id.user_face_image);
            faceImage.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
