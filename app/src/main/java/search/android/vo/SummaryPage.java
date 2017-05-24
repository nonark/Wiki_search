package search.android.vo;

/**
 * Created by nhnent on 2017. 5. 2..
 */

public class SummaryPage {

    public class Thumbnail {
        public String source;
        public int width;
        public int height;
    }

    private Thumbnail thumbnail;
    private String title;
    private String extract;

    public SummaryPage() {
        thumbnail = new Thumbnail();
    }

    public SummaryPage(Thumbnail thumbnail, String title, String extract) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.extract = extract;
    }

    public String getExtract() {
        return extract;
    }

    public void setExtract(String extract) {
        this.extract = extract;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnail.source;
    }

    public int getThumbnailWidth() {
        return thumbnail.width;
    }

    public int getThumbnailHeight() {
        return thumbnail.height;
    }
}
