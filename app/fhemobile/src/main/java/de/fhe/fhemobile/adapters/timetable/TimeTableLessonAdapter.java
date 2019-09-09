package de.fhe.fhemobile.adapters.timetable;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.timetable.MyTimeTableView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class TimeTableLessonAdapter extends BaseAdapter {

	private List<List<List<FlatDataStructure>>> tableLessonData;
	private Context context;
	public TimeTableLessonAdapter(Context context, List<List<List<FlatDataStructure>>> data) {
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
		final List<List<FlatDataStructure>> currentItem = tableLessonData.get(position);
		RelativeLayout layout = (RelativeLayout)convertView.findViewById(R.id.singleRowLayout);

		TextView lessonTitle = (TextView)convertView.findViewById(R.id.tvLessonTitle);
		if(currentItem.size()>=1) {
			lessonTitle.setText(currentItem.get(0).get(0).getEvent().getTitle());
		}

		LinearLayout eventList = (LinearLayout)convertView.findViewById(R.id.llEventList);

		for(final List<FlatDataStructure> studygroupList : currentItem) {
			TextView setTitle = new TextView(context);
			setTitle.setText(studygroupList.get(0).getStudyGroup().getTitle());
			ImageButton btnAddLesson = new ImageButton(context);
			btnAddLesson.setImageResource(android.R.drawable.ic_input_add);
			btnAddLesson.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					for (final FlatDataStructure event : studygroupList) {
						MyTimeTableView.addLesson(event);
					}
				}
			});
			eventList.addView(setTitle);
			eventList.addView(btnAddLesson);

			for (final FlatDataStructure event : studygroupList) {
				View setView = LayoutInflater.from(context).inflate(R.layout.row_layout_events, eventList, false);
				TextView lessonTime = (TextView) setView.findViewById(R.id.tvLessonTime);
				Date df = new java.util.Date(event.getEvent().getStartDate());
				String date = new SimpleDateFormat("dd.MM.yyyy").format(df);
				lessonTime.setText(date + " " + event.getEvent().getStartTime() + "-" + event.getEvent().getEndTime());

				TextView roomNumber = (TextView) setView.findViewById(R.id.tvRoom);
				roomNumber.setText(event.getEvent().getRoom());

				TextView eventId = (TextView)setView.findViewById(R.id.eventId);

				eventId.setText(""+event.getId());
				Log.d("test", "getView: "+event.getId());




				eventList.addView(setView);

			}
		}







		return convertView;
	}
}
