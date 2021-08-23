package com.chame.kaizoyu.search;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.chame.kaizolib.common.model.Result;
import com.chame.kaizoyu.R;

public class SearchItem {
    public static View inflateSearchItem(LayoutInflater inflater, Result searchResult){
        View item = inflater.inflate(R.layout.item_result, null);

        TextView title = (TextView) item.findViewById(R.id.text_title);
        title.setText(searchResult.getFilename());

        TextView text_bot = (TextView) item.findViewById(R.id.text_bot);
        text_bot.setText(searchResult.getBot());

        TextView quality = (TextView) item.findViewById(R.id.text_quality);
        quality.setText(searchResult.getQuality());

        TextView format = (TextView) item.findViewById(R.id.text_format);
        format.setText(searchResult.getFormat());

        TextView size = (TextView) item.findViewById(R.id.text_size);
        size.setText(searchResult.getSize());

        return item;
    }
}
