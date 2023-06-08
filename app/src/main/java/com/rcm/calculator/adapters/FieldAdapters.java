package com.rcm.calculator.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rcm.calculator.R;
import com.rcm.calculator.models.FieldData;

import java.util.List;

public class FieldAdapters extends RecyclerView.Adapter<FieldAdapters.ViewHolder> {

    private Context context;
    private List<FieldData> fieldDataList;
    private String[] hints;

    public FieldAdapters(Context context, List<FieldData> fieldDataList, String[] hints) {
        this.context = context;
        this.fieldDataList = fieldDataList;
        this.hints = hints;
    }

    @NonNull
    @Override
    public FieldAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_field,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldAdapters.ViewHolder holder, int position) {

        FieldData fieldData = fieldDataList.get(position);

        holder.UserTitle.setText(hints[position]+" *");
        holder.UserPV.setText(fieldData.getUserPV());
        holder.UserPV.setTag(fieldData);
        holder.UserPV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fieldData.setUserPV(s.toString());
                FieldData data = (FieldData) holder.UserPV.getTag();
                data.setUserPV(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return fieldDataList.size();
    }

    public void addNewField() {
        fieldDataList.add(new FieldData());
        notifyItemInserted(fieldDataList.size());
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView UserTitle, UserPV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            UserTitle = itemView.findViewById(R.id.item_field_title);
            UserPV = itemView.findViewById(R.id.item_field_pv);
        }
    }
}
