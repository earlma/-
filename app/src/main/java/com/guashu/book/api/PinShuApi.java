package com.guashu.book.api;

import com.guashu.book.base.IBookApi;
import com.guashu.book.bean.BookDetail;
import com.guashu.book.bean.BookMixAToc;
import com.guashu.book.bean.BookSource;
import com.guashu.book.bean.ChapterRead;
import com.guashu.book.bean.Recommend;
import com.guashu.book.bean.SearchDetail;
import com.sinovoice.hcicloudsdk.common.utils.Md5Util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import rx.Observable;
import rx.Subscriber;

/**
 * 品书网
 *
 * @author user
 */
public class PinShuApi implements IBookApi {
    private static final BookSource BOOK_SOURCE = BookSource.PINSHU;

    @Override
    public List<Recommend.RecommendBook> getRecommend(String bookType) {
        return null;
    }

    @Override
    public List<SearchDetail.SearchBooks> searchBooKFuzzy(String name) {
        List<SearchDetail.SearchBooks> list = new ArrayList<SearchDetail.SearchBooks>();
        String url = "http://zhannei.baidu.com/cse/search?s=1101330821780029220&entry=1&q="
                + name + "&isNeedCheckDomain=1&jump=1";
        try {
            Document document = Jsoup.connect(url).get();
            // 封面
            Elements select = document
                    .select(".result-game-item-pic a img[src]");
            // 书名和链接
            Elements select2 = document
                    .select(".result-item-title.result-game-item-title a");
            // 作者 类型 更新时间
            Elements select3 = document
                    .select(".result-game-item-info-tag span");
            for (int i = 1; i < select.size(); i++) {
                SearchDetail.SearchBooks books = new SearchDetail.SearchBooks();
                books.url = select2.get(i - 1).attr("href");
                books.title = select2.get(i - 1).attr("title");
                books.author = select3.get(8 * (i - 1) + 1).text();
                books.bookType = select3.get(8 * (i - 1) + 3).text();
                books.updateTime = select3.get(8 * (i - 1) + 7).text();
                books.cover = select.get(i - 1).attr("src");
                books.bookSource = BOOK_SOURCE;
                books._id = Md5Util.MD5(books.url);
                list.add(books);
            }
            return list;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SearchDetail.SearchBooks searchBooKExact(String name) {
        String url = "http://zhannei.baidu.com/cse/search?s=1101330821780029220&entry=1&q="
                + name + "&isNeedCheckDomain=1&jump=1";
        try {
            Document document = Jsoup.connect(url).get();
            // 封面
            Elements select = document
                    .select(".result-game-item-pic a img[src]");
            // 书名和链接
            Elements select2 = document
                    .select(".result-item-title.result-game-item-title a");
            // 作者 类型 更新时间
            Elements select3 = document
                    .select(".result-game-item-info-tag span");
            for (int i = 1; i < select.size(); i++) {
                SearchDetail.SearchBooks books = new SearchDetail.SearchBooks();
                books.title = select2.get(i - 1).attr("title");
                if (!name.equals(books.title)) {
                    continue;
                }
                books.url = select2.get(i - 1).attr("href");
                books.author = select3.get(8 * (i - 1) + 1).text();
                books.bookType = select3.get(8 * (i - 1) + 3).text();
                books.updateTime = select3.get(8 * (i - 1) + 7).text();
                books.cover = select.get(i - 1).attr("src");
                books.bookSource = BOOK_SOURCE;
                books._id = Md5Util.MD5(books.url);
                return books;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BookMixAToc.mixToc.Chapters> getChapterList(String bookUrl) {
        String[] split = bookUrl.split("index.html");
        String url = split[0];
        List<BookMixAToc.mixToc.Chapters> list = new ArrayList<BookMixAToc.mixToc.Chapters>();
        try {
            Document document = Jsoup.connect(bookUrl).get();
            Elements select = document.select("li a");
            for (int i = 22; i < select.size(); i++) {
                BookMixAToc.mixToc.Chapters chapter = new BookMixAToc.mixToc.Chapters(i + 1);
                chapter.link = url + select.get(i).attr("href");
                chapter.title = select.get(i).text();
                chapter.id = Md5Util.MD5(chapter.link);
                list.add(chapter);
            }
            return list;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Observable<ChapterRead.Chapter> getChapter(final String chapterUrl) {
        Observable<ChapterRead.Chapter> observable = Observable.create(new Observable.OnSubscribe<ChapterRead.Chapter>() {
            @Override
            public void call(Subscriber<? super ChapterRead.Chapter> subscriber) {
                subscriber.onNext(getChapterRead(chapterUrl));
                subscriber.onCompleted();
            }
        });
        return observable;
    }


    @Override
    public BookDetail getBookDetail(String bookUrl) {
        try {
            Document document = Jsoup.connect(bookUrl).get();

            Elements select = document.select("meta");
            BookDetail book = new BookDetail();
            // 书名
            Element element = select.get(2);
            book.title = (element.attr("content").toString());
            // 简介
            Element element2 = select.get(3);
            book.longIntro = (element2.attr("content").toString());
            // 封面链接
            Element element3 = select.get(4);
            book.cover = (element3.attr("content").toString());
            // 分类
            Element element4 = select.get(5);
            book.type = (element4.attr("content").toString());
            // 作者
            Element element5 = select.get(6);
            book.author = (element5.attr("content").toString());
            // 书籍链接
            Element element6 = select.get(7);
            book.url = (element6.attr("content").toString());
            // 状态
            Element element7 = select.get(10);
            book.status = (element7.attr("content").toString());
            // 更新时间
            Element element8 = select.get(11);
            book.updated = (element8.attr("content").toString());
            // 最后一章
            Element element9 = select.get(12);
            book.lastChapter = (element9.attr("content").toString());
            // System.err.println(book.toString());

            book.bookSource = BOOK_SOURCE;
            book._id = Md5Util.MD5(book.url);
            return book;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Recommend.RecommendBook> getRecommendBooks() {
        return null;
    }

    @Override
    public ChapterRead.Chapter getChapterRead(String chapterUrl) {
        try {
            Document document = Jsoup.connect(chapterUrl).get();
            String text = document.select(".wrapper_main").text();
            String[] split1 = text.split("手机阅读");
            String body = split1[1];
            String title = document.select("h1").text();
            String[] split = body.split("     ");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                String string = "     " + split[i] + "\n";
                sb.append(string);
            }
            return new ChapterRead.Chapter(title, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
