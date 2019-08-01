package de.fhe.fhemobile.views.mensa;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.mensa.MensaChoiceAdapter;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.mensa.MensaFoodModel;

/**
 * Created by paul on 12.02.14.
 */
public class MensaChoiceView extends FrameLayout {

    public interface ViewListener {
        void onMensaChosen(Integer _Id, Integer _Position);
    }

    public MensaChoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel = MensaFoodModel.getInstance();

        mModel.addListener(MensaFoodModel.ChangeEvent.RECEIVED_CHOICE_ITEMS, mChoiceItemsListener);
    }

    public void initView(ViewListener _Listener) {
        mViewListener = _Listener;
    }

    public void destroy() {
        mModel.removeListener(MensaFoodModel.ChangeEvent.RECEIVED_CHOICE_ITEMS, mChoiceItemsListener);

        mViewListener = null;
        mChoiceItemsListener = null;
    }

    public void initContent() {
        mAdapter = new MensaChoiceAdapter(mContext, mModel.getChoiceItems());
        mChoiceListView.setAdapter(mAdapter);
        mChoiceListView.setOnItemClickListener(mMensaSelectListener);
        mChoiceListView.setItemChecked(mModel.getSelectedItemPosition(), true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mChoiceListView = (ListView) findViewById(R.id.mensaChoiceListView);

    }

    private AdapterView.OnItemClickListener mMensaSelectListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mViewListener.onMensaChosen((int) mAdapter.getItemId(position), position);
        }
    };

    private EventListener mChoiceItemsListener = new EventListener() {
        @Override
        public void onEvent(Event event) {
            initContent();
        }
    };

    private Context mContext;

    private MensaFoodModel mModel;

    private ViewListener mViewListener;
    private MensaChoiceAdapter mAdapter;

    private ListView mChoiceListView;

}
