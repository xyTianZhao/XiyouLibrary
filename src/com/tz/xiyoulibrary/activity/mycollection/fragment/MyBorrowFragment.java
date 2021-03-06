package com.tz.xiyoulibrary.activity.mycollection.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.StringRequest;
import com.tz.xiyoulibrary.R;
import com.tz.xiyoulibrary.activity.bookdetial.view.BookDetialActivity_;
import com.tz.xiyoulibrary.activity.mycollection.activity.view.MyCollectionActivity;
import com.tz.xiyoulibrary.application.Application;
import com.tz.xiyoulibrary.dialog.progressbar.MyProgressBar;
import com.tz.xiyoulibrary.dialog.progressdialog.MyAlertDialog;
import com.tz.xiyoulibrary.toastview.CustomToast;
import com.tz.xiyoulibrary.utils.Constants;
import com.tz.xiyoulibrary.utils.LogUtils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * User: shine Date: 2015-03-13 Time: 09:32 Description:
 */
@SuppressLint("ValidFragment")
public class MyBorrowFragment extends Fragment {
	private static ImageLoader imageLoader;

	public static MyBorrowFragment getInstance(Map<String, String> book,
			ImageLoader imageLoader) {
		MyBorrowFragment fragment = new MyBorrowFragment();
		if (MyBorrowFragment.imageLoader == null)
			MyBorrowFragment.imageLoader = imageLoader;
		Bundle bundle = new Bundle();
		bundle.putString("Title", book.get("Title"));
		bundle.putString("Pub", book.get("Pub"));
		bundle.putString("Sort", book.get("Sort"));
		bundle.putString("ISBN", book.get("ISBN"));
		bundle.putString("Author", book.get("Author"));
		bundle.putString("ID", book.get("ID"));
		bundle.putString("position", book.get("position"));
		bundle.putString("small", book.get("small"));
		bundle.putString("large", book.get("large"));
		bundle.putString("medium", book.get("medium"));
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final Bundle bundle = getArguments();
		View rootView = inflater.inflate(R.layout.fragment_mycollection, null);

		// 进行布局的数据添加
		TextView title = (TextView) rootView
				.findViewById(R.id.tv_book_title_mycollection);
		title.setText(bundle.getString("Title"));
		TextView author = (TextView) rootView
				.findViewById(R.id.tv_book_author_mycollection);
		author.setText("作者：" + bundle.getString("Author"));
		TextView id = (TextView) rootView
				.findViewById(R.id.tv_book_id_mycollection);
		id.setText("编号：" + bundle.getString("ID"));
		TextView isbn = (TextView) rootView
				.findViewById(R.id.tv_book_isbn_mycollection);
		isbn.setText("条形码：" + bundle.getString("ISBN"));
		TextView sort = (TextView) rootView
				.findViewById(R.id.tv_book_sort_mycollection);
		sort.setText("索书号：" + bundle.getString("Sort"));
		TextView pub = (TextView) rootView
				.findViewById(R.id.tv_book_pub_mycollection);
		pub.setText("出版社：" + bundle.getString("Pub"));

		ImageView bookImg = (ImageView) rootView
				.findViewById(R.id.iv_book_img_mycollection);
		String imgUrl = bundle.getString("medium");
		if (TextUtils.equals(imgUrl, "")) {
			bookImg.setBackgroundResource(R.drawable.img_book_no);
		} else {
			try {
				ImageListener imageListener = ImageLoader
						.getImageListener(bookImg, R.drawable.img_book_no,
								R.drawable.img_book_no);
				imageLoader.get(imgUrl, imageListener, 240, 320);
			} catch (Exception e) {
				bookImg.setBackgroundResource(R.drawable.img_book_no);
			}
		}
		rootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						BookDetialActivity_.class);
				intent.putExtra(
						"url",
						Constants.GET_BOOK_DETAIL_BY_ID
								+ bundle.getString("ID"));
				startActivity(intent);
			}
		});
		rootView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				showDialog(bundle.getString("ID"), bundle.getString("position"));
				return false;
			}
		});

		return rootView;
	}

	protected void showDialog(final String id, final String position) {
		new MyAlertDialog(getActivity(),
				new MyAlertDialog.MyAlertDialogListener() {

					@Override
					public void confirm() {
						deleteCollection(id, position);
					}
				}).show();
	}

	protected void deleteCollection(final String id, final String position) {
		final MyProgressBar mpb = new MyProgressBar(getActivity());
		mpb.show();
		// final ProgressDialog p = new ProgressDialog(getActivity());
		// p.setTitle("提示");
		// p.setMessage("正在删除,请稍候...");
		// p.show();
		StringRequest request = new StringRequest(Method.POST,
				Constants.GET_BOOK_DELETE_FAVORITE, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						LogUtils.d("deleteCollection", response);
						mpb.dismiss();
						formatDeleteDataByJson(response, position);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mpb.dismiss();
						CustomToast.showToast(getActivity(), "网络异常", 2000);
					}
				}) {
			@SuppressLint("DefaultLocale")
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("session", Application.SESSION);
				map.put("id", id);
				map.put("username", Application.USERNAME.toUpperCase());
				return map;
			}
		};
		((MyCollectionActivity) getActivity()).getQueue().add(request);
	}

	protected void formatDeleteDataByJson(String response, String position) {
		try {
			JSONObject o = new JSONObject(response);
			if (o.getBoolean("Result")) {
				String detial = o.getString("Detail");
				if (detial.equals("DELETED_SUCCEED")) {
					((MyCollectionActivity) getActivity()).upDate(position);
				} else if (detial.equals("USER_NOT_LOGIN")) {
					CustomToast.showToast(getActivity(), "用户登录失效", 2000);
				} else if (detial.equals("PARAM_ERROR")) {
					CustomToast.showToast(getActivity(), "参数错误，缺少参数", 2000);
				} else {
					CustomToast.showToast(getActivity(), "删除失败", 2000);
				}
			} else {
				CustomToast.showToast(getActivity(), "删除失败", 2000);
			}
		} catch (Exception e) {
			CustomToast.showToast(getActivity(), "删除失败", 2000);
		}
	}
}
