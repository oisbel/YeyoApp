package com.yeyolotto.www.yeyo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeyolotto.www.yeyo.data.Tiro;

import java.util.List;

/**
 * El adaptador que relaciona los datos con la vista mediante TiroViewHolder
 */
public class TirosAdapter extends RecyclerView.Adapter<TirosAdapter.TiroViewHolder> {

    private static final int VIEW_TYPE_TODAY = 0; // Para establecer el primer item del recycler view
    private static final int VIEW_TYPE_PAST_DAY = 1;

    //Para establecer si se usa un diferente layout para el primer item del recyvler view
    private boolean mUseTodayLayout = true;

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;

    private static final String TAG = TirosAdapter.class.getSimpleName();

    List<Tiro> mTirosData;

    public TirosAdapter(@NonNull Context context){
        mContext = context;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    @NonNull
    @Override
    public TiroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        // el layout que representa cada item del recyclerView
        int layoutIdForListItem = 0;
        switch (viewType){
            case VIEW_TYPE_TODAY: {
                layoutIdForListItem = R.layout.tiro_list_item_today;
                break;
            }
            case VIEW_TYPE_PAST_DAY: {
                layoutIdForListItem = R.layout.tiro_list_item;
                break;
            }
            default:{
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
            }
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        // view: tiro_list_item
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        TiroViewHolder viewHolder = new TiroViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TiroViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        Tiro current = mTirosData.get(position);
        holder.Bind(position, current);
    }

    @Override
    public int getItemCount() {
        if(mTirosData == null) return 0;
        return mTirosData.size();
    }

    /**
     * Devuelvo 0 para el primer item a resaltar, 1 para los restantes
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if(mUseTodayLayout && position == 0)
            return VIEW_TYPE_TODAY;
        else return VIEW_TYPE_PAST_DAY;
    }

    /**
     * This method is used to set the tiros data on a TirosAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new TirosAdapter to display it.
     * @param data
     */
    public void setTirosData(List<Tiro> data){
        mTirosData = data;
        notifyDataSetChanged();
    }

    /**
     * Representa a tiro_list_item.xml
     */
    class TiroViewHolder extends RecyclerView.ViewHolder
    {
        ImageView occasionIV;
        TextView numberTV;
        TextView dateTV;

        public TiroViewHolder(View itemView)
        {
            super(itemView);
            occasionIV = itemView.findViewById(R.id.occasionIV);
            numberTV = itemView.findViewById(R.id.numberTV);
            dateTV = itemView.findViewById(R.id.dateTV);

        }

        void Bind(int listIndex, Tiro tiro)
        {
            if(tiro == null) return;
            if(listIndex == 0){
                if(tiro.getHora().equals("N"))
                    occasionIV.setImageResource(R.drawable.ic_action_night_today);
                else
                    occasionIV.setImageResource(R.drawable.ic_action_day_today);
            }else{
                if(tiro.getHora().equals("N"))
                    occasionIV.setImageResource(R.drawable.ic_action_night);
                else
                    occasionIV.setImageResource(R.drawable.ic_action_day);
            }

            numberTV.setText(String.valueOf(tiro.getTiro()));
            dateTV.setText(String.valueOf(tiro.getFecha()));
        }
    }
}
