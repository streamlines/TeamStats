package mBovin.TeamStats.UI;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import mBovin.TeamStats.R;
import mBovin.TeamStats.LiveUpdate.DownloadableLeague;
import mBovin.TeamStats.LiveUpdate.LeagueByCountry;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class LeagueListAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private ArrayList<LeagueByCountry> mGroups;
	private OnClickListener mCheckBoxOnClickListener;

	public LeagueListAdapter(Context context, ArrayList<LeagueByCountry> leagues, OnClickListener checkBoxOnClickListener) {
		super();
		this.mContext = context;
		this.mGroups = leagues;
		this.mCheckBoxOnClickListener = checkBoxOnClickListener;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		ArrayList<DownloadableLeague> leagueList = mGroups.get(groupPosition).getLeagues();
		return leagueList.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		DownloadableLeague child = (DownloadableLeague) getChild(groupPosition, childPosition);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.select_league_leagueview, null);
		}
		CheckBox cb = (CheckBox) convertView.findViewById(R.id.leagueCheckBox);
		cb.setText(child.Name.toString());
		cb.setChecked(child.Selected);
		cb.setTag(child.id);
		cb.setOnClickListener(mCheckBoxOnClickListener);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		ArrayList<DownloadableLeague> leagueList = mGroups.get(groupPosition).getLeagues();
		return leagueList.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mGroups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		LeagueByCountry group = (LeagueByCountry) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.select_league_countryview, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.countryTextView);
		ImageView iv = (ImageView) convertView.findViewById(R.id.flagImageView);
		tv.setText(group.getCountry());
		AssetManager assets = mContext.getAssets();
		InputStream iStream;
		try {
			iStream = assets.open("Flags/" + group.getFlag());
			Drawable flag = Drawable.createFromStream(iStream, group.getCountry());
			iv.setImageDrawable(flag);
		} catch (IOException e) {
			Log.e("LeagueListAdapter.java", "Unable to set flag for " + group.getCountry());
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
