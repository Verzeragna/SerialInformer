package ru.sergeiandreev.tvseriesinformer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.sergeiandreev.tvseriesinformer.R;
import ru.sergeiandreev.tvseriesinformer.serialclasses.Serial;

public class FragmentAdapter extends ArrayAdapter<Serial> {
    private ArrayList<Serial> mSubject;
    private LayoutInflater mInflater;

    public FragmentAdapter(Context context, int resource, ArrayList<Serial> subjectData) {
        super(context, resource, subjectData);
        this.mSubject = subjectData;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = mInflater.inflate(R.layout.fragment_item, viewGroup, false);

        Serial subject = mSubject.get(i);

        // заполняем View в пункте списка данными

        Picasso.get().load(subject.getSrcImage()).into((ImageView) view.findViewById(R.id.image_view));

        ((TextView) view.findViewById(R.id.text_info)).setText(getSource(subject.getLink()));

        ((TextView) view.findViewById(R.id.text_main)).setText(subject.getName());

        return view;
    }

    private String getSource(String link){
        if(link.contains("epscape")){
            return "Результат ресурс 1";
        }else return "Результат ресурс 2";
    }

    @Nullable
    @Override
    public Serial getItem(int position) {
        return mSubject.get(position);
    }
}
