package de.fhe.fhemobile.adapters.timetable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.timetable.MyTimeTableView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class CalendarAdapter extends BaseAdapter {

	private Context context;
	public CalendarAdapter(Context context) {
		this.context=context;
	}

	@Override
	public int getCount() {
		return MyTimeTableView.getSortedLessons().size();
	}

	@Override
	public Object getItem(int position) {
		return MyTimeTableView.getSortedLessons().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).
					inflate(R.layout.row_layout_calendar, parent, false);
		}
		final FlatDataStructure currentItem = MyTimeTableView.getSortedLessons().get(position);
		RelativeLayout layout = (RelativeLayout)convertView.findViewById(R.id.singleRowLayout);


		TextView lessonDay = (TextView)convertView.findViewById(R.id.tvLessonDay);
		lessonDay.setText(currentItem.getEvent().getDayOfWeek());

		TextView lessonDate = (TextView)convertView.findViewById(R.id.tvLessonDate);
		lessonDate.setText(currentItem.getEvent().getDate());


		TextView lessonTitle = (TextView)convertView.findViewById(R.id.tvTitle);
		lessonTitle.setText(currentItem.getEvent().getTitle());

		TextView lessonTime = (TextView)convertView.findViewById(R.id.tvLessonTime);
		Date df = new Date(currentItem.getEvent().getStartDate());
		SimpleDateFormat sdf= new SimpleDateFormat("dd.MM.yyyy");
		String date = sdf.format(df);
		lessonTime.setText(currentItem.getEvent().getStartTime()+"-"+currentItem.getEvent().getEndTime());

		TextView lessonRoom = (TextView)convertView.findViewById(R.id.tvRoom);
		lessonRoom.setText(currentItem.getEvent().getRoom());

		TextView lessonLecturer = (TextView)convertView.findViewById(R.id.tvLecturer);
		lessonLecturer.setText(currentItem.getEvent().getLecturer());



		if(position==0){
			lessonDay.setVisibility(View.VISIBLE);
			lessonDate.setVisibility(View.VISIBLE);


		}
		else if(!currentItem.getEvent().getDate().equals(MyTimeTableView.getSortedLessons().get(position-1).getEvent().getDate())){
			lessonDay.setVisibility(View.VISIBLE);
			lessonDate.setVisibility(View.VISIBLE);

		}
		else{
			lessonDay.setVisibility(View.GONE);
			lessonDate.setVisibility(View.GONE);
		}
		if(sdf.format(df).compareTo(sdf.format(new Date()))==0){
			lessonDay.setText("Heute ("+currentItem.getEvent().getDayOfWeek()+")");
		}




		return convertView;
	}
}