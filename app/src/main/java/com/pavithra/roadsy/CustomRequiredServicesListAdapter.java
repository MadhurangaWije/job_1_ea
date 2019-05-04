package com.pavithra.roadsy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.pavithra.roadsy.request_service.RequiredService;

import java.util.List;

public class CustomRequiredServicesListAdapter extends ArrayAdapter<RequiredService> implements View.OnClickListener {

    private List<RequiredService> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
//        TextView txtName;
        CheckBox checkBox;
    }

    public CustomRequiredServicesListAdapter(List<RequiredService> data, Context context){
        super(context, R.layout.required_service_list_item, data);
        dataSet=data;
        mContext=context;

    }



    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        RequiredService requiredService =(RequiredService) object;
        requiredService.setRequired( (!Boolean.getBoolean(requiredService.isRequired()))+"" );
        Toast.makeText(mContext,requiredService.getName(),Toast.LENGTH_LONG).show();

    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RequiredService requiredService = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.required_service_list_item, parent, false);
//            viewHolder.txtName = convertView.findViewById(R.id.requiredServiceTextview);
            viewHolder.checkBox = convertView.findViewById(R.id.requiredServiceCheckbox);

//            viewHolder.checkBox.setRequired(!requiredService.isRequired());

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim. : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object object= getItem(position);
                RequiredService requiredService =(RequiredService) object;
                requiredService.setRequired( (!Boolean.getBoolean(requiredService.isRequired()))+"" );
//                v.findViewById(R.id.requiredServiceCheckbox).setSelected(true);
////                m(viewHolder,!requiredService.isRequired());
                Toast.makeText(mContext,requiredService.getName(),Toast.LENGTH_LONG).show();
            }
        });

//        viewHolder.txtName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Object object= getItem(position);
//                RequiredService requiredService =(RequiredService) object;
//                requiredService.setRequired(!requiredService.isRequired());
//                m(viewHolder,!requiredService.isRequired());
//                Toast.makeText(mContext,requiredService.getName(),Toast.LENGTH_LONG).show();
//            }
//        });
        viewHolder.checkBox.setOnClickListener(this);

//        viewHolder.txtName.setTag(position);
        viewHolder.checkBox.setText(requiredService.getName());
        viewHolder.checkBox.setTag(position);


//        viewHolder.txtName.setText(requiredService.getName());
        viewHolder.checkBox.setSelected(Boolean.getBoolean(requiredService.isRequired()));
        // Return the completed view to render on screen
        return convertView;
    }

    void m(ViewHolder viewHolder,boolean result){
        viewHolder.checkBox.setSelected(result);
    }
}
