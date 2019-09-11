package de.fhe.fhemobile.adapters.timetable;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.timetable.MyTimeTableView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class TimeTableLessonAdapter extends BaseAdapter {
	private static final String TAG = "TimeTableLessonAdapter";

	private String lessonTitle="";
	private String studygroupTitle="";

	private List<FlatDataStructure> tableLessonData;
	private Context context;
	public TimeTableLessonAdapter(Context context, List<FlatDataStructure> data) {
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
					inflate(R.layout.row_layout_events, parent, false);
		}
		final FlatDataStructure currentItem = tableLessonData.get(position);
		RelativeLayout layout = (RelativeLayout)convertView.findViewById(R.id.singleRowLayout);

		TextView lessonTitle = (TextView)convertView.findViewById(R.id.tvLessonTitle);

		if(position==0){
			lessonTitle.setText(currentItem.getEvent().getTitle());
			lessonTitle.setVisibility(View.VISIBLE);
		}
		else if(!tableLessonData.get(position).getEvent().getTitle().equals(tableLessonData.get(position-1).getEvent().getTitle())){
			lessonTitle.setText(currentItem.getEvent().getTitle());
			lessonTitle.setVisibility(View.VISIBLE);
			Log.d(TAG, "getView: currentItem: "+currentItem.getEvent().getTitle()+" prevItem: "+tableLessonData.get(position-1).getEvent().getTitle());
		}
		else{
			lessonTitle.setVisibility(View.GONE);
		}



		TextView studyGroupTitle = (TextView)convertView.findViewById(R.id.tvSetTitle);
		ImageButton btnAddLesson = (ImageButton)convertView.findViewById(R.id.ibAddLesson);
		btnAddLesson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				MyTimeTableView.addLesson(currentItem);
			}

		});



		if(position==0){
			studyGroupTitle.setText(currentItem.getStudyGroup().getTitle());
			studyGroupTitle.setVisibility(View.VISIBLE);
			btnAddLesson.setVisibility(View.VISIBLE);


		}
		else if(!currentItem.getEvent().getTitle().equals(tableLessonData.get(position-1).getEvent().getTitle())){
			studyGroupTitle.setText(currentItem.getStudyGroup().getTitle());
			studyGroupTitle.setVisibility(View.VISIBLE);
			btnAddLesson.setVisibility(View.VISIBLE);
		}
		else if(!currentItem.getStudyGroup().getTitle().equals(tableLessonData.get(position-1).getStudyGroup().getTitle())){
			studyGroupTitle.setText(currentItem.getStudyGroup().getTitle());
			studyGroupTitle.setVisibility(View.VISIBLE);
			btnAddLesson.setVisibility(View.VISIBLE);
		}
		else{
			studyGroupTitle.setVisibility(View.GONE);
			btnAddLesson.setVisibility(View.GONE);
		}

		TextView tvTime = (TextView) convertView.findViewById(R.id.tvLessonTime);
		Date df = new java.util.Date(currentItem.getEvent().getStartDate());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(df);
		tvTime.setText(date + " " + currentItem.getEvent().getStartTime() + "-" + currentItem.getEvent().getEndTime());

		TextView tvRoom = (TextView)convertView.findViewById(R.id.tvRoom);
		tvRoom.setText(currentItem.getEvent().getRoom());







		return convertView;
	}
}
