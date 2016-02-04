package com.example.ourstars;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SongListAdapter extends BaseAdapter {
	 ArrayList<SongDataModel> listArray;
	 protected ImageLoader loader = ImageLoader.getInstance();
     DisplayImageOptions op = new DisplayImageOptions.Builder()
     //.showStubImage(R.drawable.default_profile)
     //.showImageForEmptyUri(R.drawable.default_profile)
     //.showImageOnFail(R.drawable.default_profile)
     .cacheInMemory()
     .cacheOnDisc()
     .imageScaleType(ImageScaleType.EXACTLY)
     .displayer(new RoundedBitmapDisplayer(10))
     .build();
     
	    public SongListAdapter(ArrayList<SongDataModel> listArray, Context ctx) {
	    	this.listArray = listArray;
	    }
	 
	    @Override
	    public int getCount() {
	        return listArray.size();    // total number of elements in the list
	    }
	 
	    @Override
	    public Object getItem(int i) {
	        return listArray.get(i);    // single item in the list
	    }
	 
	    @Override
	    public long getItemId(int i) {
	        return i;                   // index number
	    }
	 
	    @Override
	    public View getView(int index, View view, final ViewGroup parent) {
	 
	        if (view == null) {
	            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
	            view = inflater.inflate(R.layout.songs_list, parent, false);
	        }

	        App.initImageLoader(parent.getContext());

	        final SongDataModel dataModel = listArray.get(index);
	        
	        TextView textView1 = (TextView) view.findViewById(R.id.textView1);
	        textView1.setText(dataModel.getName());
	        if(dataModel.getName()=="(**Unknown Name**)")
	        {
	        	textView1.setTextColor(Color.parseColor("#FF0066"));
	        }
	        else
	        {
	        	textView1.setTextColor(Color.parseColor("#FFFFFF"));
	        }
	        if(dataModel.getName().length()>25)
	        {
	        	textView1.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
	        }
	        else
	        {
	        	textView1.setTextSize(TypedValue.COMPLEX_UNIT_DIP,19);
	        }

	        TextView textView2 = (TextView) view.findViewById(R.id.textView2);
	        textView2.setText("Year Of Release: "+dataModel.getRelease());
	        
	        TextView textView3 = (TextView) view.findViewById(R.id.textView3);
	        textView3.setText("Total Views: "+dataModel.getViewcount());
	        
	        TextView textView5 = (TextView) view.findViewById(R.id.textView5);
	        textView5.setText(""+dataModel.getDuration());
	        
	        String thumbnail="http://img.youtube.com/vi/"+dataModel.getId()+"/default.jpg";
	        
	        ImageView imageView2 = (ImageView) view.findViewById(R.id.imageView2);
	        new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY);
	        loader.displayImage(thumbnail, imageView2, op, null);
	        
	        TextView textView4 = (TextView) view.findViewById(R.id.textView4);
	        textView4.setText(dataModel.getRank()+"");
	        
	        return view;
	    }
}