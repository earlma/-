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
package com.guashu.book.ui.easyadapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.guashu.book.R;
import com.guashu.book.base.Constant;
import com.guashu.book.bean.Recommend;
import com.guashu.book.manager.SettingManager;
import com.guashu.book.utils.FileUtils;
import com.guashu.book.utils.FormatUtils;
import com.guashu.book.view.recyclerview.adapter.BaseViewHolder;
import com.guashu.book.view.recyclerview.adapter.RecyclerArrayAdapter;

import java.text.NumberFormat;

/**
 * @author yuyh.
 * @date 2016/9/7.
 */
public class RecommendAdapter extends RecyclerArrayAdapter<Recommend.RecommendBook> {

    public RecommendAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<Recommend.RecommendBook>(parent, R.layout.item_recommend_list) {
            @Override
            public void setData(final Recommend.RecommendBook item) {
                super.setData(item);
                holder.setText(R.id.tvRecommendTitle, item.title)
                        .setText(R.id.tvRecommendShort, item.lastChapter)
                        .setText(R.id.tvBookSource, item.bookSource == null ? "" : String.format(mContext.getString(R.string.book_detail_source), item.bookSource.getLink()))
                        .setVisible(R.id.ivTopLabel, item.isTop)
                        .setVisible(R.id.ckBoxSelect, item.showCheckBox)
                        .setVisible(R.id.ivUnReadDot, FormatUtils.formatZhuiShuDateString(item.updated)
                                .compareTo(item.recentReadingTime) > 0);

                if (item.path != null && item.path.endsWith(Constant.SUFFIX_PDF)) {
                    holder.setImageResource(R.id.ivRecommendCover, R.drawable.ic_shelf_pdf);
                } else if (item.path != null && item.path.endsWith(Constant.SUFFIX_EPUB)) {
                    holder.setImageResource(R.id.ivRecommendCover, R.drawable.ic_shelf_epub);
                } else if (item.path != null && item.path.endsWith(Constant.SUFFIX_CHM)) {
                    holder.setImageResource(R.id.ivRecommendCover, R.drawable.ic_shelf_chm);
                } else if (item.isFromSD) {
                    holder.setImageResource(R.id.ivRecommendCover, R.drawable.ic_shelf_txt);
                    long fileLen = FileUtils.getChapterFile(item._id, 1).length();
                    if (fileLen > 10) {
                        double progress = ((double) SettingManager.getInstance().getReadProgress(item._id)[2]) / fileLen;
                        NumberFormat fmt = NumberFormat.getPercentInstance();
                        fmt.setMaximumFractionDigits(2);
                        holder.setText(R.id.tvRecommendShort, "当前阅读进度：" + fmt.format(progress));
                    }
                } else if (!SettingManager.getInstance().isNoneCover()) {
                    holder.setRoundImageUrl(R.id.ivRecommendCover, item.cover,
                            R.drawable.no_cover);
                } else {
                    holder.setImageResource(R.id.ivRecommendCover, R.drawable.cover_default);
                }

                CheckBox ckBoxSelect = holder.getView(R.id.ckBoxSelect);
                ckBoxSelect.setChecked(item.isSeleted);
            }
        };
    }

    @Override
    public void checkItem(int position) {
        getItem(position).isSeleted = !getItem(position).isSeleted;
        notifyItemChanged(position);
    }
}
