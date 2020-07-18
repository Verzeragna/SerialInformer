package ru.sergeiandreev.tvseriesinformer.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import ru.sergeiandreev.tvseriesinformer.R;
import ru.sergeiandreev.tvseriesinformer.adapters.FragmentAdapter;

public class DialogFragmentList extends DialogFragment {
    private View mView;
    private ListView mListView;
    private AppCompatButton mButton;
    private FragmentAdapter fragmentAdapter;
    private String mSerial;
    private String mLink;

    public interface EditNameDialogListener {
        void onFinishEditDialog(int position);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dialog_fragment, null);
        mListView = mView.findViewById(R.id.list_fragment);
        mButton = mView.findViewById(R.id.button_cancel);
        mButton.setOnClickListener(onClickListener);
        mListView.setOnItemClickListener(onItemClickListener);
        savedInstanceState = getArguments();
        int mResultTheme = savedInstanceState.getInt("theme");
        switch (mResultTheme){
            case 1:
                mButton.setTextAppearance(getActivity(),R.style.AppTheme_Button_Blue);
                break;
            case 2:
                mButton.setTextAppearance(getActivity(),R.style.AppTheme_Button_Red);
                break;
            case 3:
                mButton.setTextAppearance(getActivity(),R.style.AppTheme_Button_Green);
                break;
            case 4:
                mButton.setTextAppearance(getActivity(),R.style.AppTheme_Button_Yellow);
                break;
            default:
                break;
        }
        fragmentAdapter = new FragmentAdapter(getActivity(), R.layout.dialog_fragment, savedInstanceState.getParcelableArrayList("arraylist"));
        mListView.setAdapter(fragmentAdapter);
        return mView;
    }


    private ListView.OnItemClickListener onItemClickListener =
            (adapterView, view, i, l) -> {
                EditNameDialogListener activity = (EditNameDialogListener) getActivity();
                activity.onFinishEditDialog(i);
                this.dismiss();
            };

    private AppCompatButton.OnClickListener onClickListener =
            view -> {
                EditNameDialogListener activity = (EditNameDialogListener) getActivity();
                activity.onFinishEditDialog(-1);
                this.dismiss();
            };
}
