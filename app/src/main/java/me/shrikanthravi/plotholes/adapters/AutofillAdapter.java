package me.shrikanthravi.plotholes.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import me.shrikanthravi.plotholes.R;

public class AutofillAdapter extends RecyclerView.Adapter<AutofillAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> placename;
    private ArrayList<String> placeaddress;

    public AutofillAdapter(Context context, ArrayList<String> placename, ArrayList<String> placeaddress) {
        this.context = context;
        this.placename = placename;
        this.placeaddress = placeaddress;
    }

    @Override
    public AutofillAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.autofill_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AutofillAdapter.ViewHolder viewHolder, int i) {
        viewHolder.placename.setText(placename.get(i));
        viewHolder.placeaddress.setText(placeaddress.get((i)));
    }

    @Override
    public int getItemCount() {
        return placename.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView placename;
        private TextView placeaddress;

        public ViewHolder(View view) {
            super(view);
            placename = view.findViewById(R.id.placename);
            placeaddress = view.findViewById(R.id.placeaddress);
        }
    }

}