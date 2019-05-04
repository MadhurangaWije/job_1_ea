package com.pavithra.roadsy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CustomServiceProviderListAdapter extends ArrayAdapter<User> implements View.OnClickListener {

    private List<User> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
    }

    public CustomServiceProviderListAdapter(List<User> data, Context context){
        super(context, R.layout.service_provider_listview_item, data);
        dataSet=data;
        mContext=context;
    }



    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        User user=(User) object;

        Toast.makeText(mContext,user.getName(),Toast.LENGTH_LONG).show();

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.service_provider_listview_item, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.service_provider_list_item);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim. : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;


        viewHolder.txtName.setText(user.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
