/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.guashu.book.ui.contract;

import com.guashu.book.base.BaseContract;
import com.guashu.book.bean.BookDetail;
import com.guashu.book.bean.BookSource;

/**
 * @author lfh.
 * @date 2016/8/6.
 */
public interface BookDetailContract {

    interface View extends BaseContract.BaseView {
        void showBookDetail(BookDetail data);

        /**
         * 显示加载画面
         */
        void showLoading();

        /**
         * 隐藏加载画面
         */
        void hideLoading();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getBookDetail(BookSource bookSource, String bookId);
    }
}
