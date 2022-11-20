package de.fhe.fhemobile.adapters.canteen;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import de.fhe.fhemobile.fragments.canteen.CanteenMenuFragment;
import de.fhe.fhemobile.utils.UserSettings;

/**
 * Created by Nadja on 05.05.2022
 */
public class CanteenPagerAdapter extends FragmentStateAdapter {

    public CanteenPagerAdapter(final FragmentManager fragmentManager,
                               final Lifecycle lifecycle){
        super(fragmentManager, lifecycle);

        final ArrayList<String> selectedCanteens = UserSettings.getInstance().getSelectedCanteenIds();
        for(final String canteenId : selectedCanteens){
            fragments.add(CanteenMenuFragment.newInstance(canteenId));
        }

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

    private final ArrayList<CanteenMenuFragment> fragments = new ArrayList<>();
}
