package com.yeyolotto.www.yeyo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeyolotto.www.yeyo.data.Tiro;
import com.yeyolotto.www.yeyo.data.YeyoDbHelper;
import com.yeyolotto.www.yeyo.utilities.DataUtils;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ImageButton userBT; // boton que muestra los datos de usuario
    TextView lastDateUpdatedTV;
    TextView loadingTV; // para mostrar errores o actualizsacion satisfactoria
    ImageView occasionIV; // dia o noche del ultimo tiro
    TextView numberTV; // ultimo tiro
    TextView dateTV; // fecha del ultimo tiro

    Tiro lastTiro;

    /** Database helper that will provide us access to the database */
    private YeyoDbHelper mDbHelper;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userBT = view.findViewById(R.id.userBT);
        lastDateUpdatedTV = view.findViewById(R.id.lastDateUpdatedTV);
        loadingTV = view.findViewById(R.id.loadingTV);
        occasionIV = view.findViewById(R.id.occasionIV);
        numberTV = view.findViewById(R.id.numberTV);
        dateTV = view.findViewById(R.id.dateTV);

        userBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UserDataActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        mDbHelper = new YeyoDbHelper(getContext());

        makeTiroQuery();

        return view;
    }

    /**
     * Recupera el ultimo tiro de la base de datos
     */
    private void makeTiroQuery(){

        List<Tiro> list = DataUtils.GetLastTiros(1,mDbHelper.getReadableDatabase());
        if(list != null && list.size()>0) {
            lastTiro = list.get(0);
            if(lastTiro.getHora().equals("N"))
                occasionIV.setImageResource(R.drawable.ic_action_night_today);
            else
                occasionIV.setImageResource(R.drawable.ic_action_day_today);
            lastDateUpdatedTV.setText(lastTiro.getFecha());
            dateTV.setText(lastTiro.getFecha());
            numberTV.setText(lastTiro.getTiro());
        }else {
            showErrorMessage();
        }
    }

    private void showErrorMessage(){
        loadingTV.setText(getString(R.string.loadingError));
        loadingTV.setBackgroundResource(R.color.loadingError);
        loadingTV.setVisibility(View.VISIBLE);
    }

    private void showLoadingMessage(){
        loadingTV.setVisibility(View.VISIBLE);
        loadingTV.setText(getString(R.string.loading));
        loadingTV.setBackgroundResource(R.color.loading);
    }

}
