package de.fhe.fhemobile.adapters.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.navigation.PersonNavigationSearchFragment;
import de.fhe.fhemobile.fragments.navigation.RoomNavigationSearchFragment;

/**
 * Created by Nadja on 12/2021
 */
public class NavigationDialogPagerAdapter extends FragmentStateAdapter {

    public NavigationDialogPagerAdapter(@NonNull final FragmentManager fragmentManager,
                                        @NonNull final Lifecycle lifecycle){
        super(fragmentManager, lifecycle);

        fragments.add(new RoomNavigationSearchFragment());
        fragments.add(new PersonNavigationSearchFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(final int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    private final ArrayList<FeatureFragment> fragments = new ArrayList<>();
}
