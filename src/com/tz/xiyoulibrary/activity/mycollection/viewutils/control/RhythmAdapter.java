package com.tz.xiyoulibrary.activity.mycollection.viewutils.control;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Map;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tz.xiyoulibrary.R;

/**
 * User: shine Date: 2015-01-14 Time: 13:32 Description:
 */
public class RhythmAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;

	private ImageLoader imageLoader;

	/**
	 * item的宽度
	 */
	private float itemWidth;
	/**
	 * 数据源
	 */
	private List<Map<String, String>> mCardList;

	public RhythmAdapter(Context context, List<Map<String, String>> cardList,
			ImageLoader imageLoader) {
		this.mContext = context;
		this.mCardList = cardList;
		this.imageLoader = imageLoader;
		if (context != null)
			this.mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return this.mCardList.size();
	}

	public Object getItem(int position) {
		return this.mCardList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 设置每个item的宽度
	 */
	public void setItemWidth(float width) {
		this.itemWidth = width;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout relativeLayout = (RelativeLayout) this.mInflater
				.inflate(R.layout.adapter_rhythm_icon, null);
		// 设置item布局的大小以及Y轴的位置
		relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
				(int) itemWidth, mContext.getResources().getDimensionPixelSize(
						R.dimen.rhythm_item_height)));
		relativeLayout.setTranslationY(itemWidth);

		// 设置第二层RelativeLayout布局的宽和高
		RelativeLayout childRelativeLayout = (RelativeLayout) relativeLayout
				.getChildAt(0);
		int relativeLayoutWidth = (int) itemWidth
				- 2
				* mContext.getResources().getDimensionPixelSize(
						R.dimen.rhythm_icon_margin);
		childRelativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
				relativeLayoutWidth, mContext.getResources()
						.getDimensionPixelSize(R.dimen.rhythm_item_height)
						- 2
						* mContext.getResources().getDimensionPixelSize(
								R.dimen.rhythm_icon_margin)));

		ImageView imageIcon = (ImageView) relativeLayout
				.findViewById(R.id.image_icon);
		// 计算ImageView的大小
		int iconSize = (relativeLayoutWidth - 2 * mContext.getResources()
				.getDimensionPixelSize(R.dimen.rhythm_icon_margin));
		ViewGroup.LayoutParams iconParams = imageIcon.getLayoutParams();
		iconParams.width = iconSize;
		iconParams.height = iconSize;
		imageIcon.setLayoutParams(iconParams);
		// 设置底部按钮的背景图片
		String imgUrl = mCardList.get(position).get("medium");
		if (TextUtils.equals(imgUrl, "")) {
			imageIcon.setBackgroundResource(R.drawable.img_book_no);
		} else {
			ImageListener imageListener = ImageLoader.getImageListener(
					imageIcon, R.drawable.img_book, R.drawable.img_book_no);
			imageLoader.get(imgUrl, imageListener, iconSize, iconSize);
		}

		return relativeLayout;
	}
}