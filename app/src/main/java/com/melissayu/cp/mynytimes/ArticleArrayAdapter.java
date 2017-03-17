package com.melissayu.cp.mynytimes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.melissayu.cp.mynytimes.R.id.ivImage;
import static com.melissayu.cp.mynytimes.R.id.tvTitle;

/**
 * Created by melissa on 3/15/17.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    private static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
    }

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Article article = this.getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.item_article_result, parent, false);

            viewHolder.ivImage = (ImageView) convertView.findViewById(ivImage);
            viewHolder.tvTitle = (TextView) convertView.findViewById(tvTitle);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivImage.setImageResource(0);
        viewHolder.tvTitle.setText(article.getHeadline());

        String thumbnail = article.getThumbnail();
        if (!TextUtils.isEmpty(thumbnail)) {
//            Picasso.with(getContext()).load("https://i.imgur.com/tGbaZCY.jpg").fit().centerCrop().into(viewHolder.ivImage);
            Picasso.with(getContext()).load(thumbnail).fit().centerCrop().into(viewHolder.ivImage);

//            Toast.makeText(getContext(), thumbnail, Toast.LENGTH_LONG).show();
        } else {
            viewHolder.ivImage.setImageResource(R.drawable.sticker_white);
            viewHolder.ivImage.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorImageBackground));
        }

        return convertView;
    }
}
