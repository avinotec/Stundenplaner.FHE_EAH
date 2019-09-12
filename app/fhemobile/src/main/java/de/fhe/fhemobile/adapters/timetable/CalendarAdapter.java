package de.fhe.fhemobile.adapters.timetable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.timetable.MyTimeTableView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class SelectedLessonAdapter extends BaseAdapter {

	private Context context;
	public SelectedLessonAdapter(Context context) {
		this.context=context;
	}

	@Override
	public int getCount() {
		return MyTimeTableView.getLessons().size();
	}

	@Override
	public Object getItem(int position) {
		return MyTimeTableView.getLessons().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).
					inflate(R.layout.row_layout_my_scedule, parent, false);
		}
		final FlatDataStructure currentItem = MyTimeTableView.getLessons().get(position);
		RelativeLayout layout = (RelativeLayout)convertView.findViewById(R.id.singleRowLayout);

		TextView lessonTitle = (TextView)convertView.findViewById(R.id.tvLessonTitle);
		lessonTitle.setText(currentItem.getEvent().getTitle());

		TextView lessonTime = (TextView)convertView.findViewById(R.id.tvLessonTime);
		Date df = new Date(currentItem.getEvent().getStartDate());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(df);
		lessonTime.setText(date+" "+currentItem.getEvent().getStartTime()+"-"+currentItem.getEvent().getEndTime());

		TextView setTitle = (TextView)convertView.findViewById(R.id.tvSetTitle);
		setTitle.setText(currentItem.getStudyGroup().getTitle());

		ImageButton ibRemoveLesson = convertView.findViewById(R.id.ibRemoveLesson);
		ibRemoveLesson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyTimeTableView.removeLesson(currentItem);
			}
		});

		return convertView;
	}
}
