package de.fhe.fhemobile.utils.headerlistview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

public abstract class SectionAdapter extends BaseAdapter implements OnItemClickListener {

    private int mCount = -1;

    public abstract int numberOfSections();

    public abstract int numberOfRows(int section);

    public abstract View getRowView(int section, int row, View convertView, ViewGroup parent);

    public abstract Object getRowItem(int section, int row);

    public boolean hasSectionHeaderView(final int section) {
        return false;
    }

    public View getSectionHeaderView(final int section, final View convertView, final ViewGroup parent) {
        return null;
    }

    public Object getSectionHeaderItem(final int section) {
        return null;
    }

    public int getRowViewTypeCount() {
        return 1;
    }

    public int getSectionHeaderViewTypeCount() {
        return 1;
    }

    /**
     * Must return a value between 0 and getRowViewTypeCount() (excluded)
     */
    public int getRowItemViewType(final int section, final int row) {
        return 0;
    }

    /**
     * Must return a value between 0 and getSectionHeaderViewTypeCount() (excluded, if > 0)
     */
    public int getSectionHeaderItemViewType(final int section) {
        return 0;
    }

    /**
     * Dispatched to call onItemClick
     */
    @Override
    public final void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        onRowItemClick(parent, view, getSection(position), getRowInSection(position), id);
    }

    public void onRowItemClick(final AdapterView<?> parent, final View view, final int section, final int row, final long id) {

    }

    /**
     * Counts the amount of cells = headers + rows
     */
    @Override
    public final int getCount() {
        if (mCount < 0) {
            mCount = numberOfCellsBeforeSection(numberOfSections());
        }
        return mCount;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }

    /**
     * Dispatched to call getRowItem or getSectionHeaderItem
     */
    @Override
    public final Object getItem(final int position) {
        final int section = getSection(position);
        if (isSectionHeader(position)) {
            if (hasSectionHeaderView(section)) {
                return getSectionHeaderItem(section);
            }
            return null;
        }
        return getRowItem(section, getRowInSection(position));
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    /**
     * Dispatched to call getRowView or getSectionHeaderView
     */
    @Override
    public final View getView(final int position, final View convertView, final ViewGroup parent) {
        final int section = getSection(position);
        if (isSectionHeader(position)) {
            if (hasSectionHeaderView(section)) {
                return getSectionHeaderView(section, convertView, parent);
            }
            return null;
        }
        return getRowView(section, getRowInSection(position), convertView, parent);
    }

    /**
     * Returns the section number of the indicated cell
     */
    protected int getSection(final int position) {
        int section = 0;
        int cellCounter = 0;
        while (cellCounter <= position && section <= numberOfSections()) {
            cellCounter += numberOfCellsInSection(section);
            section++;
        }
        return section - 1;
    }

    /**
     * Returns the row index of the indicated cell Should not be call with
     * positions directing to section headers
     */
    protected int getRowInSection(final int position) {
        final int section = getSection(position);
        final int row = position - numberOfCellsBeforeSection(section);
        if (hasSectionHeaderView(section)) {
            return row - 1;
        } else {
            return row;
        }
    }

    /**
     * Returns true if the cell at this index is a section header
     */
    protected boolean isSectionHeader(final int position) {
        final int section = getSection(position);
        return hasSectionHeaderView(section) && numberOfCellsBeforeSection(section) == position;
    }

    /**
     * Returns the number of cells (= headers + rows) before the indicated
     * section
     */
    protected int numberOfCellsBeforeSection(final int section) {
        int count = 0;
        for (int i = 0; i < Math.min(numberOfSections(), section); i++) {
            count += numberOfCellsInSection(i);
        }
        return count;
    }

    private int numberOfCellsInSection(final int section) {
        return numberOfRows(section) + (hasSectionHeaderView(section) ? 1 : 0);
    }

    @Override
    public void notifyDataSetChanged() {
        mCount = numberOfCellsBeforeSection(numberOfSections());
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        mCount = numberOfCellsBeforeSection(numberOfSections());
        super.notifyDataSetInvalidated();
    }

    /**
     * Dispatched to call getRowItemViewType or getSectionHeaderItemViewType
     */
    @Override
    public final int getItemViewType(final int position) {
        final int section = getSection(position);
        if (isSectionHeader(position)) {
            return getRowViewTypeCount() + getSectionHeaderItemViewType(section);
        } else {
            return getRowItemViewType(section, getRowInSection(position));
        }
    }

    /**
     * Dispatched to call getRowViewTypeCount and getSectionHeaderViewTypeCount
     */
    @Override
    public final int getViewTypeCount() {
        return getRowViewTypeCount() + getSectionHeaderViewTypeCount();
    }

    /**
     * By default, disables section headers
     */
    @Override
    public boolean isEnabled(final int position) {
        return (!isSectionHeader(position));
    }

}
