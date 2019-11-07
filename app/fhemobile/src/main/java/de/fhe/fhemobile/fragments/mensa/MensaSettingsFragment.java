package de.fhe.fhemobile.fragments.mensa;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.models.mensa.MensaFoodModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.views.mensa.MensaChoiceView;


public class MensaSettingsFragment extends Fragment {

    public MensaSettingsFragment() {
        // Required empty public constructor
    }

    public static MensaSettingsFragment newInstance() {
        MensaSettingsFragment fragment = new MensaSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (MensaChoiceView) inflater.inflate(R.layout.fragment_mensa_choice, container, false);
        mView.initView(mViewListener);

        if(MensaFoodModel.getInstance().getChoiceItems() == null) {
            NetworkHandler.getInstance().fetchAvailableMensas();
        }
        else {
            mView.initContent();
        }

        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            mView.destroy();
        }
    }


    private final MensaChoiceView.ViewListener mViewListener = new MensaChoiceView.ViewListener() {
        @Override
        public void onMensaChosen(Integer _Id, Integer _Position) {

            String mensaName = MensaFoodModel.getInstance().getChoiceItems()[_Position].getName();

            UserSettings.getInstance().setChosenMensa( String.valueOf(_Id), mensaName );
            MensaFoodModel.getInstance().setSelectedItemPosition(_Position);
        }
    };

    private MensaChoiceView mView;

}
