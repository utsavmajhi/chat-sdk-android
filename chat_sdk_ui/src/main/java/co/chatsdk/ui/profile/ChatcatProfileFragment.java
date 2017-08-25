/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:27 PM
 */

package co.chatsdk.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import co.chatsdk.core.NM;

import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.User;
import co.chatsdk.ui.R;
import co.chatsdk.core.defines.Debug;

import com.braunster.chatsdk.network.FacebookManager;

import org.apache.commons.lang3.StringUtils;

import co.chatsdk.ui.helpers.UIHelper;
import co.chatsdk.ui.utils.UserAvatarHelper;
import timber.log.Timber;

/**
 * Created by itzik on 6/17/2014.
 */
@Deprecated
public class ChatcatProfileFragment extends AbstractProfileFragment {


    private static final String TAG = ChatcatProfileFragment.class.getSimpleName();
    private static boolean DEBUG = Debug.ProfileFragment;
    
    public static ChatcatProfileFragment newInstance() {
        ChatcatProfileFragment f = new ChatcatProfileFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        f.setRetainInstance(true);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Dont inflate the AbstractProfileFragment menu items.
        enableActionBarItems(false);

        initViews(inflater);

        loadData();

        return mainView;
    }

    public void initViews(LayoutInflater inflater){
        if (inflater != null)
            mainView = inflater.inflate(R.layout.chatcat_fragment_profile, null);
        else return;

        super.initViews();

        setupTouchUIToDismissKeyboard(mainView, R.id.ivAvatar);



        // Changing the weight of the view according to orientation.
        // This will make sure (hopefully) there is enough space to show the views in landscape mode.
//        int currentOrientation = getResources().getConfiguration().orientation;
//        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE){
//            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mainView.findViewById(R.id.frame_profile_image_container).getLayoutParams();
//            layoutParams.weight = 3;
//            mainView.findViewById(R.id.frame_profile_image_container).setLayoutParams(layoutParams);
//        }
//        else
//        {
//            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mainView.findViewById(R.id.frame_profile_image_container).getLayoutParams();
//            layoutParams.weight = 2;
//            mainView.findViewById(R.id.linear).setLayoutParams(layoutParams);
//        }

    }

    @Override
    public void loadData() {
        super.loadData();
        setDetails((Integer) NM.auth().getLoginInfo().get(co.chatsdk.core.types.Defines.Prefs.AccountTypeKey));
    }

    @Override
    public void clearData() {
        super.clearData();

        if (mainView != null)
        {
            ((TextView) mainView.findViewById(R.id.chat_sdk_txt_name)).setText("");
        }
    }

    @Override
    public void showSettings() {
        // Logout and return to the login activity.
        FacebookManager.logout(getActivity());

        NM.auth().logout();
        UIHelper.shared().startLoginActivity(true);
    }

    /** Fetching the user details from the user's metadata.*/
    private void setDetails(int loginType){
        if (mainView == null || getActivity() == null) {
            return;
        }

        User user = NM.currentUser();

        String name = user.getName();

        if (StringUtils.isNotEmpty(name))
            ((TextView) mainView.findViewById(R.id.chat_sdk_txt_name)).setText(name);

        String country = user.metaStringForKey(Keys.CountryCode);

        String status = user.metaStringForKey(Keys.Status);

        String location = user.metaStringForKey(Keys.Location);

        // Loading the user country icon, If not exist we will hide the icon.
        if (StringUtils.isNotEmpty(country))
        {
            ((ImageView) mainView.findViewById(R.id.chat_sdk_country_ic)).setImageResource(EditProfileActivity2.getResId(country));
            mainView.findViewById(R.id.chat_sdk_country_ic).setVisibility(View.VISIBLE);
        }
        else mainView.findViewById(R.id.chat_sdk_country_ic).setVisibility(View.INVISIBLE);

        // Loading the user status, If not exist we will hide the status line and header.
        if (StringUtils.isNotEmpty(status))
        {
            ((TextView) mainView.findViewById(R.id.chat_sdk_txt_status)).setText(status);

            mainView.findViewById(R.id.chat_sdk_txt_status).setVisibility(View.VISIBLE);
            mainView.findViewById(R.id.chat_sdk_txt_status_header).setVisibility(View.VISIBLE);
        }
        else {
            mainView.findViewById(R.id.chat_sdk_txt_status).setVisibility(View.GONE);
            mainView.findViewById(R.id.chat_sdk_txt_status_header).setVisibility(View.GONE);
        }

        if (StringUtils.isNotEmpty(location))
        {
            ((TextView) mainView.findViewById(R.id.chat_sdk_txt_location)).setText(location);
            mainView.findViewById(R.id.relative_location).setVisibility(View.VISIBLE);
        }
        else
            mainView.findViewById(R.id.relative_location).setVisibility(View.INVISIBLE);

        if (DEBUG) Timber.d("loading user details, Name: %s, Status: %s, CountryCode: %s, Location: %s", name, status, country, location);

        UserAvatarHelper.loadAvatar(user, profileCircleImageView).subscribe();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item =
                menu.add(Menu.NONE, R.id.action_chat_sdk_edit, 13, getString(R.string.action_edit));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setIcon(R.drawable.ic_edit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if (item.getItemId() == R.id.action_chat_sdk_edit)
        {
//            UIHelper.shared().startEditProfileActivity(NM.currentUser().getId());

            getActivity().overridePendingTransition(R.anim.slide_bottom_top, R.anim.dummy);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}
