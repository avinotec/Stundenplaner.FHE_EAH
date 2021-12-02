package de.fhe.fhemobile.adapters.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.fhe.fhemobile.fragments.navigation.PersonSearchFragment;
import de.fhe.fhemobile.fragments.navigation.RoomSearchFragment;

public class NavigationDialogPagerAdapter extends FragmentStateAdapter {

    public NavigationDialogPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle){
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new RoomSearchFragment();
            case 2:
                return new PersonSearchFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
