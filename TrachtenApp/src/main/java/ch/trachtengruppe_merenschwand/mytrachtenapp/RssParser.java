package ch.trachtengruppe_merenschwand.mytrachtenapp;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahaen on 31.03.2015.
 */
class RssParser {

    public List<RssItem> parse(InputStream inputStream) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            inputStream.close();
        }
    }

    private List<RssItem> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "rss");

        String title = null;
        String link = null;
        String description = null;
        String lastBuildDate = null;
        String pubDate = null;

        List<RssItem> items = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name) {
                case "title":
                    title = readElement(parser, "title");
                    break;
                case "link":
                    link = readElement(parser, "link");
                    break;
                case "description":
                    description = readElement(parser, "description");
                    break;
                case "pubDate":
                    pubDate = readElement(parser, "pubDate");
                    // Log.w("::pubDate::", pubDate);
                    break;
                case "lastBuildDate":
                    lastBuildDate = readElement(parser, "lastBuildDate");
                    //  Log.w("::lastBuildDate::", lastBuildDate);
                    break;
            }

            if (name.equals("item")){//(title != null && link != null && description!= null){// || lastBuildDate!= null || pubDate!= null) {
                RssItem item = new RssItem(title, link, description, lastBuildDate, pubDate);
                items.add(item);
                title = null;
                link = null;
                description = null;
                lastBuildDate = null;
                pubDate = null;
            }
        }

        if (title != null || link != null || description!= null || lastBuildDate!= null || pubDate!= null) {
            RssItem item = new RssItem(title, link, description, lastBuildDate, pubDate);
            items.add(item);
        }
        return items;
    }

    private String readElement(XmlPullParser parser, String Element) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, Element);
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, Element);
        return description;
    }

    // For the tags title and link, extract their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}