package by.katbinc.moovon.model;

public class PlayerStreamModel {
    private StreamCoverModel coverBig;
    private StreamCoverModel cover;
    private String streamUrl;
    private String title;
    private String description;
    private int isHighlight;

    public StreamCoverModel getCoverBig() {
        return coverBig;
    }

    public void setCoverBig(StreamCoverModel coverBig) {
        this.coverBig = coverBig;
    }

    public StreamCoverModel getCover() {
        return cover;
    }

    public void setCover(StreamCoverModel cover) {
        this.cover = cover;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsHighlight() {
        return isHighlight;
    }

    public void setIsHighlight(int isHighlight) {
        this.isHighlight = isHighlight;
    }
}
