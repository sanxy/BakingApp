package com.sanxynet.bakingapp.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.sanxynet.bakingapp.R;
import com.sanxynet.bakingapp.utils.Utility;

public class DetailStepAdapter extends RecyclerView.Adapter<DetailStepAdapter.DetailStepHolder> {

    private final String mDescription;
    private final String mShortDescription;
    private String mThumbnailUrl;
    private final String mVideoUri;
    private Context mContext;

    public DetailStepAdapter(String description, String shortDescription, String thumbnailUrl, String videoUri) {
        mDescription = description;
        mShortDescription = shortDescription;
        mThumbnailUrl = thumbnailUrl;
        mVideoUri = videoUri;
    }

    @NonNull
    @Override
    public DetailStepHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutId = R.layout.list_detail_step;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutId, parent, false);
        return new DetailStepHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final DetailStepHolder holder, int position) {

        if (TextUtils.isEmpty(mVideoUri)) {

            if ((!Utility.isTablet(mContext)) && mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                holder.mTextViewDetailDescription.setVisibility(View.GONE);
                holder.mTextViewShortDetailDescription.setVisibility(View.GONE);
                holder.mImageViewDetailStep.setBackgroundResource(R.color.colorBackgroundPlayer);
            } else {
                holder.mImageViewDetailStep.setBackgroundResource(R.color.colorBackgroundCardSecondary);
            }

            final RequestOptions requestOptions;
            if (TextUtils.isEmpty(mThumbnailUrl)) {
                mThumbnailUrl = null;
                requestOptions = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .fallback(R.drawable.no_media)
                        .fitCenter()
                        .placeholder(R.drawable.loading);

            } else {
                requestOptions = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fallback(R.drawable.no_media)
                        .error(R.drawable.no_media)
                        .placeholder(R.drawable.loading);
            }
            Glide.with(holder.itemView.getContext().getApplicationContext())
                    .load(mThumbnailUrl)
                    .apply(requestOptions)
                    .into(holder.mImageViewDetailStep);

            holder.mImageViewDetailStep.setVisibility(View.VISIBLE);

        }

        holder.mTextViewShortDetailDescription.setText(mShortDescription);

        holder.mTextViewDetailDescription.setText(mDescription);

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class DetailStepHolder extends RecyclerView.ViewHolder {

        @SuppressWarnings("unused")
        @BindView(R.id.tv_short_detail_step_description)
        TextView mTextViewShortDetailDescription;

        @SuppressWarnings("unused")
        @BindView(R.id.tv_detail_step_description)
        TextView mTextViewDetailDescription;

        @SuppressWarnings("unused")
        @BindView(R.id.image_detail_step_thumbnail)
        ImageView mImageViewDetailStep;


        DetailStepHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
