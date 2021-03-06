/**
 * Copyright 2016 JustWayward Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.guashu.book.manager;

import android.content.Context;
import android.text.TextUtils;

import com.guashu.book.ReaderApplication;
import com.guashu.book.base.Constant;
import com.guashu.book.bean.BookLists;
import com.guashu.book.bean.BookMixAToc;
import com.guashu.book.bean.ChapterRead;
import com.guashu.book.utils.ACache;
import com.guashu.book.utils.AppUtils;
import com.guashu.book.utils.FileUtils;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.utils.SharedPreferencesUtil;
import com.guashu.book.utils.StringUtils;
import com.guashu.book.utils.ToastUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuyh.
 * @date 2016/9/28.
 */
public class CacheManager {

    private static CacheManager manager;

    public static CacheManager getInstance() {
        return manager == null ? (manager = new CacheManager()) : manager;
    }

    public List<String> getSearchHistory() {
        return SharedPreferencesUtil.getInstance().getObject(getSearchHistoryKey(), List.class);
    }

    public void saveSearchHistory(Object obj) {
        SharedPreferencesUtil.getInstance().putObject(getSearchHistoryKey(), obj);
    }

    private String getSearchHistoryKey() {
        return "searchHistory";
    }

    /**
     * 获取我收藏的书单列表
     *
     * @return
     */
    public List<BookLists.BookListsBean> getCollectionList() {
        List<BookLists.BookListsBean> list = (ArrayList<BookLists.BookListsBean>) ACache.get(
                ReaderApplication.getsInstance()).getAsObject(getCollectionKey());
        return list == null ? null : list;
    }

    public void removeCollection(String bookListId) {
        List<BookLists.BookListsBean> list = getCollectionList();
        if (list == null) {
            return;
        }
        for (BookLists.BookListsBean bean : list) {
            if (bean != null) {
                if (TextUtils.equals(bean._id, bookListId)) {
                    list.remove(bean);
                    ACache.get(ReaderApplication.getsInstance()).put(getCollectionKey(), (Serializable) list);
                    break;
                }
            }
        }
    }

    public void addCollection(BookLists.BookListsBean bean) {
        List<BookLists.BookListsBean> list = getCollectionList();
        if (list == null) {
            list = new ArrayList<>();
        }
        for (BookLists.BookListsBean data : list) {
            if (data != null) {
                if (TextUtils.equals(data._id, bean._id)) {
                    ToastUtils.showToast("已经收藏过啦");
                    return;
                }
            }
        }
        list.add(bean);
        ACache.get(ReaderApplication.getsInstance()).put(getCollectionKey(), (Serializable) list);
        ToastUtils.showToast("收藏成功");
    }

    private String getCollectionKey() {
        return "my_book_lists";
    }

    /**
     * 获取目录缓存
     *
     * @param mContext
     * @param bookId
     * @return
     */
    public List<BookMixAToc.mixToc.Chapters> getTocList(Context mContext, String bookId) {
        Object obj = ACache.get(mContext).getAsObject(getTocListKey(bookId));
        if (obj != null) {
            try {
                BookMixAToc data = (BookMixAToc) obj;
                List<BookMixAToc.mixToc.Chapters> list = data.mixToc.chapters;
                if (list != null && !list.isEmpty()) {
                    return list;
                }
            } catch (Exception e) {
                ACache.get(mContext).remove(getTocListKey(bookId));
            }
        }
        return null;
    }

    public void saveTocList(Context mContext, String bookId, BookMixAToc data) {
        ACache.get(mContext).put(getTocListKey(bookId), data);
    }

    public void removeTocList(Context mContext, String bookId) {
        ACache.get(mContext).remove(getTocListKey(bookId));
    }

    private String getTocListKey(String bookId) {
        return bookId + "-bookToc";
    }

    public File getChapterFile(String bookId, int chapter) {
        File file = FileUtils.getChapterFile(bookId, chapter);
        if (file != null && file.length() > 50)
            return file;
        return null;
    }

    public void saveChapterFile(String bookId, int chapter, ChapterRead.Chapter data) {
        File file = FileUtils.getChapterFile(bookId, chapter);
        FileUtils.writeFile(file.getAbsolutePath(), StringUtils.formatContent(data.body), false);
    }

    /**
     * 获取缓存大小
     *
     * @return
     */
    public synchronized String getCacheSize() {
        long cacheSize = 0;

        try {
            String cacheDir = Constant.BASE_PATH;
            cacheSize += FileUtils.getFolderSize(cacheDir);
            if (FileUtils.isSdCardAvailable()) {
                String extCacheDir = AppUtils.getAppContext().getExternalCacheDir().getPath();
                cacheSize += FileUtils.getFolderSize(extCacheDir);
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }

        return FileUtils.formatFileSizeToString(cacheSize);
    }

    /**
     * 清除缓存
     *
     * @param
     */
//    public synchronized void clearCache(boolean clearReadPos, boolean clearCollect, boolean clearElse) {
    public synchronized void clearCache(boolean clearCollect, boolean clearElse) {
        try {

//            // 删除阅读记录（SharePreference）
//            if (clearReadPos) {
//                //防止再次弹出性别选择框，sp要重写入保存的性别
//                String chooseSex = SettingManager.getInstance().getUserChooseSex();
//
//                SettingManager.getInstance().saveUserChooseSex(chooseSex);
//            }
            // 清空书架
            if (clearCollect) {
                CollectionsManager.getInstance().clear();
                EventManager.refreshCollectionList();
            }
            // 清除其他缓存
            if (clearElse) {
                // 删除内存缓存
                String cacheDir = AppUtils.getAppContext().getCacheDir().getPath();
                FileUtils.deleteFileOrDirectory(new File(cacheDir));
                if (FileUtils.isSdCardAvailable()) {
                    // 删除SD书籍缓存
                    FileUtils.deleteFileOrDirectory(new File(Constant.PATH_DATA));
                }
                SharedPreferencesUtil.getInstance().removeAll();
                SettingManager.getInstance().saveRecommend();
                SettingManager.getInstance().saveUserAgreeDis();
                ACache.get(AppUtils.getAppContext()).clear();
            }

        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

}
