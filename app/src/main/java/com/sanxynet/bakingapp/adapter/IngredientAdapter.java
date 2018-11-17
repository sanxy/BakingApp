package com.sanxynet.bakingapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.sanxynet.bakingapp.R;
import com.sanxynet.bakingapp.ui.BaseActivity;
import com.sanxynet.bakingapp.db.BakingAppContract;
import timber.log.Timber;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.DetailIngredientHolder> {

    private final IngredientItemClickListener mOnIngredientItemClickListener;
    private Cursor mCursor;
    private Context mContext;
    public IngredientAdapter(IngredientItemClickListener ingredientItemClickListener) {
        mOnIngredientItemClickListener = ingredientItemClickListener;
    }

    public interface IngredientItemClickListener {
        void onIngredientItemClick(int position, int itemCount);
    }

    @NonNull
    @Override
    public DetailIngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutId = R.layout.list_ingredient;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutId, parent, false);

        return new DetailIngredientHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull DetailIngredientHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        mCursor.moveToPosition(position);

        String ingredientName = mCursor.getString(mCursor.getColumnIndex(BakingAppContract.IngredientEntry.COLUMN_NAME_INGREDIENT));
        float quantityName = mCursor.getFloat(mCursor.getColumnIndex(BakingAppContract.IngredientEntry.COLUMN_NAME_QUANTITY));
        String measureName = mCursor.getString(mCursor.getColumnIndex(BakingAppContract.IngredientEntry.COLUMN_NAME_MEASURE));

        Timber.d("position: " + position + " - " + BaseActivity.getPositionIngredient());


        StringBuilder stringBuilder = new StringBuilder(ingredientName.trim().toLowerCase(Locale.getDefault()));
        stringBuilder.setCharAt(0,Character.toUpperCase(ingredientName.charAt(0)));
        holder.mTextViewIngredientName.setText(stringBuilder);


        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        holder.mTextViewQuantityName.setText(String.valueOf(decimalFormat.format(quantityName)));

        measureName = "(" + measureName + ")";
        holder.mTextViewMeasureName.setText(measureName);

    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public class DetailIngredientHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @SuppressWarnings("unused")
        @BindView(R.id.tv_ingredient_name)
        TextView mTextViewIngredientName;

        @SuppressWarnings("unused")
        @BindView(R.id.tv_quantity_name)
        TextView mTextViewQuantityName;

        @SuppressWarnings("unused")
        @BindView(R.id.tv_measure_name)
        TextView mTextViewMeasureName;

        DetailIngredientHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mOnIngredientItemClickListener.onIngredientItemClick(getAdapterPosition(), getItemCount());
        }

    }

    @SuppressWarnings("UnusedReturnValue")
    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
}
