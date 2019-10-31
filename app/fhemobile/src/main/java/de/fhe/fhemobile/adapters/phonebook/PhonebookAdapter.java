/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.adapters.phonebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.phonebook.EmployeeVo;

/**
 * Created by paul on 22.01.14.
 */
public class PhonebookAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<EmployeeVo> mListElements;

    static class ViewHolder {
        TextView mFullName;
        TextView mRoom;
        TextView mDivision;
    }

    public PhonebookAdapter(Context _Context, List<EmployeeVo> _Employees) {
        mContext = _Context;
        mListElements = _Employees;
    }

    @Override
    public int getCount() {
        return mListElements.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_phonebook_employee, parent, false);

            viewHolder.mFullName = (TextView) convertView.findViewById(R.id.phoneItemFullName);
            viewHolder.mRoom = (TextView) convertView.findViewById(R.id.phoneItemRoom);
            viewHolder.mDivision = (TextView) convertView.findViewById(R.id.phoneItemDivision);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final EmployeeVo employee = mListElements.get(position);
        final String title = employee.getTitle();
        if(title.length() == 0) {
            viewHolder.mFullName.setText(employee.getFirstName() + " " + employee.getLastName());
        }
        else {
            viewHolder.mFullName.setText(title + " " + employee.getFirstName() + " " + employee.getLastName());
        }
        viewHolder.mRoom.setText(employee.getRoom());
        viewHolder.mDivision.setText(employee.getDivision());

        return convertView;
    }
}
