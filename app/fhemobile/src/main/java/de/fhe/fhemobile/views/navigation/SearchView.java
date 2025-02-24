package de.fhe.fhemobile.views.navigation;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;

import de.fhe.fhemobile.R;

public abstract class SearchView extends LinearLayout {

    public SearchView(final Context context, final AttributeSet attrs){
        super(context, attrs); }

    public SearchView(final Context context){ super(context); }

    /**
     * Initialisation of child specific elements
     * @param _manager
     */
    public abstract void initializeView(final FragmentManager _manager);

    /**
     * Initialisation of elements of this abstract super class
     */
    protected void initializeView(){
        toggleStartInputCardVisibility(false);

        mQrButton.setOnClickListener(mQrClickListener);
        mGoButton.setOnClickListener(mGoClickListener);
        toggleGoButtonEnabled(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mStartInputCard = (CardView) findViewById(R.id.cardviewStart);

        mStartInputLayout = (TextInputLayout) findViewById(R.id.navigation_input_layout_start);
        mStartInputText = (EditText) findViewById(R.id.navigation_input_edittext_start);

        mQrButton = (ImageButton) findViewById(R.id.navigationButtonQR);
        mGoButton = (Button) findViewById(R.id.navigationDialogButton);
    }

    public void toggleStartInputCardVisibility(final boolean _Visible) {
        mStartInputCard.setVisibility(_Visible ? VISIBLE : GONE);
        mStartInputCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
    }

    public void toggleGoButtonEnabled(final boolean _Enabled) {
        mGoButton.setEnabled(_Enabled);
        if (_Enabled) {
            mGoButton.setBackgroundResource(R.drawable.buttonshape_filled);
        } else {
            mGoButton.setBackgroundResource(R.drawable.buttonshape_filled_disabled);
        }
    }

    public String getStartInputText() {
        final Editable text = mStartInputText.getText();

        //getText() returns null if no text was entered -> error message is displayed
        if (text == null) {
            return null;

        } else {
            return text.toString();
        }
    }

    /**
     * Set error message that is displayed at the start input layout
     * Message: RoomVo not found!
     */
    public void setInputErrorRoomNotFound() {
        mStartInputLayout.setError(
                getResources().getString(R.string.error_message_room_input_invalid));
    }

    /**
     * Set error message that is displayed at the start input layout
     * Message: No RoomVo entered!
     */
    public void setInputErrorNoInput() {
        mStartInputLayout.setError(
                getResources().getString(R.string.error_message_no_room_input));
    }

    public void setStartInputText(final String room) {
        mStartInputText.setText(room, TextView.BufferType.EDITABLE);
    }

    //Listener for QR-Code button
    private final View.OnClickListener mQrClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (getViewListener() != null) {
                getViewListener().onQrClicked();
            }
        }
    };

    //Listener for Go! button
    private final View.OnClickListener mGoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (getViewListener() != null) {
                getViewListener().onGoClicked();
            }
        }
    };

    public abstract SearchView.IViewListener getViewListener();

    public abstract void setViewListener(final SearchView.IViewListener _Listener);

    public interface IViewListener {
        void onQrClicked();
        void onGoClicked();
    }

    private CardView mStartInputCard;

    private TextInputLayout mStartInputLayout;
    private EditText mStartInputText;

    private ImageButton mQrButton;
    private Button mGoButton;
}

