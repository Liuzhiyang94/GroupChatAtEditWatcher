package com.example.lzy.inputdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MemberAdapter extends BaseAdapter {

    private List<MemberInfo> data;
    private Context context;

    public MemberAdapter(List<MemberInfo> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public MemberInfo getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_member_list, null);
            //通过上面layout得到的view来获取里面的具体控件
            holder.textView = (TextView) convertView.findViewById(R.id.item_tv);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(data.get(position).nickName);
        return convertView;
    }


    class ViewHolder{
        TextView textView;
    }
}
