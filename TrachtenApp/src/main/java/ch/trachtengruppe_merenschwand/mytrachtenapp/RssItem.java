package ch.trachtengruppe_merenschwand.mytrachtenapp;

/**
 * Created by ahaen on 31.03.2015.
 */
class RssItem {

    private final String title;
    private final String link;
    private final String description;
    private final String lastBuildDate;
    private final String pubDate;

    public RssItem(String title, String link, String description, String lastBuildDate, String pubDate) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.lastBuildDate = lastBuildDate;
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public String getPubDate() {
        return pubDate;
    }


}
