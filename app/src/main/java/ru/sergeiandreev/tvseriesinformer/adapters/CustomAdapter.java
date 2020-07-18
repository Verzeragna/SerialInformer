package ru.sergeiandreev.tvseriesinformer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Pattern;

import ru.sergeiandreev.tvseriesinformer.R;

public class CustomAdapter extends BaseAdapter {

    private ArrayList<String> mLine;
    private LayoutInflater mInflater;
    private String mSeasonNumber;
    private Context ctx;
    private final Pattern pattern = Pattern.compile("[^\\d]");

    public CustomAdapter(Context context, ArrayList<String> line, String seasonNumber) {
        ctx = context;
        mLine = line;
        mSeasonNumber = seasonNumber;
        mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mLine.size();
    }

    @Override
    public Object getItem(int i) {
        return mLine.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // используем созданные, но не используемые view
        if (view == null) {
            view = mInflater.inflate(R.layout.item, viewGroup, false);
        }

        if (mLine.size() > 0) {
            String[] line = mLine.get(i).split(",");
            // заполняем View в пункте списка данными
            StringBuilder builder = new StringBuilder();
            for (int j = 1; j < line.length - 1; j++) {
                builder.append(line[j]);
            }

            StringBuilder number = new StringBuilder();
            number.append("Сезон: ").append(mSeasonNumber).append(", ").append("серия: ").append(line[0]);

            ((TextView) view.findViewById(R.id.textview_name)).setText(number);
            ((TextView) view.findViewById(R.id.textview_number)).setText(builder);
            ((TextView) view.findViewById(R.id.textview_date)).setText("Дата выхода: " + line[line.length - 1].replace(".", " "));
        }

        return view;
    }

}
