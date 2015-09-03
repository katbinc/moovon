package by.katbinc.moovon.model;

public class NavigationItemModel {
    private int id;
    private String title;
    private NavigationContentModel content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NavigationContentModel getContent() {
        return content;
    }

    public void setContent(NavigationContentModel content) {
        this.content = content;
    }
}
