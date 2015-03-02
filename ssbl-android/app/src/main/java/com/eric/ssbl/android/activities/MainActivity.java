package com.eric.ssbl.android.activities;

import android.os.Bundle;

import com.eric.ssbl.R;
import com.eric.ssbl.android.fragments.ChartFragment;
import com.eric.ssbl.android.fragments.EventListFragment;
import com.eric.ssbl.android.fragments.InboxFragment;
import com.eric.ssbl.android.fragments.ProfileFragment;
import com.eric.ssbl.android.fragments.SearchFragment;
import com.eric.ssbl.android.fragments.SettingsFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class MainActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle bundle) {
        this.disableLearningPattern();
        MaterialAccount account = new MaterialAccount(this.getResources(),"Sanic Weedhog","gotta.go.fast@sanic.com", R.drawable.honey, R.drawable.abstract_cover);
        this.addAccount(account);

        MaterialSection map = newSection("Map", new ChartFragment());
        MaterialSection inbox = newSection("Inbox", new InboxFragment());
        MaterialSection events = newSection("Events", new EventListFragment());
        MaterialSection profile = newSection("Profile", new ProfileFragment());
        MaterialSection search = newSection("Search", new SearchFragment());
        // divisor
        MaterialSection settings = newSection("Settings", new SettingsFragment());

        this.addSection(map);
        this.addSection(profile);
        this.addSection(inbox);
        this.addSection(events);
        this.addSection(profile);
        this.addSection(search);

        this.addDivisor();

        this.addSection(settings);
    }
}