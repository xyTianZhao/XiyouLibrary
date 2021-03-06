package com.tz.xiyoulibrary.activity.bookdetial.presenter;

import com.android.volley.RequestQueue;
import com.tz.xiyoulibrary.activity.bookdetial.model.BookDetialModel;
import com.tz.xiyoulibrary.activity.bookdetial.model.IBookDetialModel;
import com.tz.xiyoulibrary.activity.bookdetial.view.IBookDetialView;
import com.tz.xiyoulibrary.activity.callback.CallBack;

public class BookDetialPresenter {

	private IBookDetialView mBookDetialView;
	private IBookDetialModel mBookDetialModel;

	public BookDetialPresenter(IBookDetialView view) {
		this.mBookDetialView = view;
		mBookDetialModel = new BookDetialModel();
	}

	/**
	 * 获取书籍信息
	 */
	public void getBookDetial(RequestQueue queue, String url) {
		mBookDetialModel.getBookDetial(queue, url,
				new CallBack<BookDetialModel>() {

					@Override
					public void getModel(BookDetialModel model) {
						switch (model.state) {
						case IBookDetialModel.LOADING:
							mBookDetialView.showLoadingView();
							break;
						case IBookDetialModel.LOADING_FALUIRE:
							mBookDetialView.showLoadFaluireView();
							mBookDetialView.showMsg(model.msg);
							break;
						case IBookDetialModel.NO_DATA:
							mBookDetialView.showNoDataView();
							break;
						case IBookDetialModel.LOADING_SUCCESS:
							mBookDetialView
									.showBookDetialView(model.bookDetial);
							break;
						}
					}
				});
	}

	/**
	 * 添加收藏
	 */
	public void collection(RequestQueue queue, String id) {
		mBookDetialModel.collection(queue, id, new CallBack<BookDetialModel>() {

			@Override
			public void getModel(BookDetialModel model) {
				switch (model.state) {
				case IBookDetialModel.LOADING:
					mBookDetialView.showDialog();
					break;
				case IBookDetialModel.LOADING_FALUIRE:
				case IBookDetialModel.LOADING_SUCCESS:
					mBookDetialView.hidenDialog();
					mBookDetialView.showMsg(model.msg);
					break;
				}
			}
		});
	}

}
