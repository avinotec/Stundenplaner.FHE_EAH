package de.fhe.fhemobile.adapters.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.navigation.RoomSearchFragment;

public class NavigationDialogPagerAdapter extends FragmentStateAdapter {

    ArrayList<FeatureFragment> fragments = new ArrayList<FeatureFragment>();

    public NavigationDialogPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle){
        super(fragmentManager, lifecycle);

        fragments.add(new RoomSearchFragment());
        //todo: person search
        //fragments.add(new PersonSearchFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return fragments.get(0);
            //todo: person search
//            case 1:
//                return fragments.get(1);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
