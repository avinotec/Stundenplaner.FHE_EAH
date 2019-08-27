package de.fhe.fhemobile.adapters.timetable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class TimeTableLessonAdapter extends BaseAdapter {

	private List<FlatDataStructure> tableLessonData;
	private Context context;
	public TimeTableLessonAdapter(Context context,List<FlatDataStructure> data) {
		tableLessonData=data;
		this.context=context;
	}

	@Override
	public int getCount() {
		return tableLessonData.size();
	}

	@Override
	public Object getItem(int position) {
		return tableLessonData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).
					inflate(R.layout.row_layout_lessons, parent, false);
		}
		final FlatDataStructure currentItem = tableLessonData.get(position);
		RelativeLayout layout = (RelativeLayout)convertView.findViewById(R.id.singleRowLayout);
		TextView lessonTitle = (TextView)convertView.findViewById(R.id.tvLessonTitle);
		ImageButton btnAddLesson = (ImageButton) convertView.findViewById(R.id.ibAddLesson);
		lessonTitle.setText(currentItem.getEvent().getTitle());
		btnAddLesson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!currentItem.isSelected()) {
					currentItem.select();
				}
			}
		});




		return convertView;
	}
}
