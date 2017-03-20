package com.melissayu.cp.mynytimes.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.melissayu.cp.mynytimes.R;
import com.melissayu.cp.mynytimes.models.Article;

import java.util.List;

/**
 * Created by melissa on 3/18/17.
 */

public class ArticleRVAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int IMAGE = 0, NOIMAGE = 1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
        TextView tvSnippet;
        TextView tvNewsDesk;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvSnippet = (TextView) itemView.findViewById(R.id.tvSnippet);
            tvNewsDesk = (TextView) itemView.findViewById(R.id.tvNewsDesk);
        }
    }
    public static class ViewHolderNoImage extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSnippet;
        TextView tvNewsDesk;

        public ViewHolderNoImage(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvSnippet = (TextView) itemView.findViewById(R.id.tvSnippet);
            tvNewsDesk = (TextView) itemView.findViewById(R.id.tvNewsDesk);
        }
    }

    private List<Article> mArticles;
    private Context mContext;

    public ArticleRVAdapter(Context context, List<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (mArticles.get(position).getThumbnail() != null && mArticles.get(position).getThumbnail() != "") {
            return IMAGE;
        } else {
            return NOIMAGE;
        }
//        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case IMAGE:
                View v1 = inflater.inflate(R.layout.item_article_result, parent, false);
                viewHolder = new ViewHolder(v1);
                break;
            case NOIMAGE:
                View v2 = inflater.inflate(R.layout.item_article_result_noimage, parent, false);
                viewHolder = new ViewHolderNoImage(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.item_article_result, parent, false);
                viewHolder = new ViewHolder(v);
                break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case IMAGE:
                ViewHolder vh1 = (ViewHolder) viewHolder;
                configureViewHolderImage(vh1, position);
                break;
            case NOIMAGE:
                ViewHolderNoImage vh2 = (ViewHolderNoImage) viewHolder;
                configureViewHolderNoImage(vh2, position);
                break;
            default:
                ViewHolder vh = (ViewHolder) viewHolder;
                configureViewHolderImage(vh, position);
                break;
        }
    }

    private void configureViewHolderImage(ViewHolder vh1, int position) {
        Article article = (Article) mArticles.get(position);
        if (article != null) {
            vh1.tvTitle.setText(article.getHeadline());
            vh1.tvSnippet.setText(article.getSnippet());

            if (!article.getNewsDesk().equals("None")) {
                vh1.tvNewsDesk.setText(article.getNewsDesk());
            } else {
                vh1.tvNewsDesk.setVisibility(View.GONE);
            }
            String thumbnail = article.getThumbnail();
            if (!TextUtils.isEmpty(thumbnail)) {
                Glide.with(getContext()).load(thumbnail).fitCenter().placeholder(R.drawable.loading_animation).into(vh1.ivImage);
            } else {
                vh1.ivImage.setImageResource(R.drawable.sticker_white);
                vh1.ivImage.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorImageBackground));
            }
        }
    }

    private void configureViewHolderNoImage(ViewHolderNoImage vh1, int position) {
        Article article = (Article) mArticles.get(position);
        if (article != null) {
            vh1.tvTitle.setText(article.getHeadline());
            vh1.tvTitle.setTextSize(18);
            vh1.tvSnippet.setText(article.getSnippet());
            if (!article.getNewsDesk().equals("None")) {
                vh1.tvNewsDesk.setText(article.getNewsDesk());
            } else {
                vh1.tvNewsDesk.setVisibility(View.GONE);
            }
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArticles.size();
    }

}
