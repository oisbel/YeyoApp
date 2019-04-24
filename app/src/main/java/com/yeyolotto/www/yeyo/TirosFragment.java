package com.yeyolotto.www.yeyo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yeyolotto.www.yeyo.data.Tiro;
import com.yeyolotto.www.yeyo.data.TirosData;
import com.yeyolotto.www.yeyo.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;


public class TirosFragment extends Fragment {

    private TirosAdapter mTirosAdapter;
    private RecyclerView mTiroList;
    private TextView mErrorMessageDisplay;
    // Create a ProgressBar variable to store a reference to the ProgressBar
    private ProgressBar mLoadingIndicator;

    // Lista de los tiros
    List<Tiro> mTirosData;

    public TirosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tiros, container, false);

        mErrorMessageDisplay = view.findViewById(R.id.errorMessageTV);
        mTiroList = view.findViewById(R.id.tirosRecyclerView);
        mLoadingIndicator = view.findViewById(R.id.loadingIndicatorPB);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mTiroList.setLayoutManager(layoutManager);
        mTiroList.setHasFixedSize(true);
        mTirosAdapter = new TirosAdapter(getContext());
        mTiroList.setAdapter(mTirosAdapter);

        /* Once all of our views are setup, we can load the tiros data. */
        makeTirosQuery();

        return view;
    }

    /**
     * Ejecuta el hilo para descargar los tiros
     */
    private void makeTirosQuery(){
        showTiroRecyclerView();
        new TirosQueryTask().execute();
    }

    // Helper methods
    private void showTiroRecyclerView() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the RecyclerView data is visible
        mTiroList.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        // First, hide the currently visible data
        mTiroList.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * Ejecuta el pedido de obtener los tiros
     */
    public class TirosQueryTask extends AsyncTask<Void, Void, List<Tiro>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Tiro> doInBackground(Void... voids) {
            URL tirosUrl = NetworkUtils.buildTirosUrl();
            // Para guardar la respuesta string en formato JSON
            String tirosJSONResult = null;
            try {
                tirosJSONResult = NetworkUtils.getTirosFromHttpUrl(tirosUrl);
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
            TirosData tirosData = null;
            try {
                tirosData= new TirosData(tirosJSONResult);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(tirosData!=null) {
                return tirosData.getTiros();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Tiro> tirosDataResult) {
            // As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if(tirosDataResult != null && tirosDataResult.size() > 0){
                showTiroRecyclerView();
                // Guardo la referecia de la lista de tiros
                mTirosData = tirosDataResult;
                // Mando los datos al adaptador para que los muestre en el recyclerView
                mTirosAdapter.setTirosData(mTirosData);
            }else{
                showErrorMessage();
            }
        }
    }
}
