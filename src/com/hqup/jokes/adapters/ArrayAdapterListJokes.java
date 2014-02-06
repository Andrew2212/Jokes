package com.hqup.jokes.adapters;

import java.util.List;

import com.hqup.jokes.R;
import com.hqup.jokes.entity.Joke;
import com.hqup.jokes.preferencers.Preferencer;
import com.hqup.jokes.utils.Logger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/*
 * http://www.vogella.com/tutorials/AndroidListView/article.html#adapterperformance_hoder
 */

public class ArrayAdapterListJokes extends ArrayAdapter<Joke> {

	private int resourceLayout;
	private Context context;
	private List<Joke> listJokes;

	private float fontSize;
	private int fontColor;

	private static class ViewHolder {
		public TextView tvNumber;
		public TextView tvText;
	}

	/**
	 * 
	 * @param fontSize
	 *            Sets text size for ListView::tvText
	 */
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * 
	 * @param fontColor
	 *            Sets text color for ListView::tvText
	 */
	public void setFontColor(int fontColor) {
		this.fontColor = fontColor;
	}

	/**
	 * 
	 * @param context
	 *            The current context
	 * @param resource
	 *            The resource ID for a layout file containing a View to use
	 *            when instantiating views.
	 */
	public ArrayAdapterListJokes(Context context, int resourceLayout,
			List<Joke> listJokes) {
		super(context, resourceLayout, listJokes);

		this.context = context;
		this.resourceLayout = resourceLayout;// R.layout.list_item
		this.listJokes = listJokes;

		/*
		 * Initialize default 'fontSize' by <string
		 * name="pref_font_size_value">18</string>
		 */
		fontSize = Preferencer.getFontSize(context);

		/*
		 * Initialize default 'fontColor'
		 */
		fontColor = Preferencer.getFontColor(context);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		if (rowView == null) {

			LayoutInflater inflater = LayoutInflater.from(context);

			rowView = inflater.inflate(resourceLayout, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.tvNumber = (TextView) rowView
					.findViewById(R.id.tv_item_number);
			viewHolder.tvText = (TextView) rowView
					.findViewById(R.id.tv_item_text);

			// Try to set font size for TextView 'text'
			viewHolder.tvText.setTextSize(fontSize);

			// Try to set font color for TextView 'text'
			viewHolder.tvText.setTextColor(fontColor);

			rowView.setTag(viewHolder);

		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		String number = listJokes.get(position).getNumber();
		String text = listJokes.get(position).getText();
		Logger.v(number);
		holder.tvNumber.setText(number);
		holder.tvText.setText(text);

		return rowView;
	}

}
