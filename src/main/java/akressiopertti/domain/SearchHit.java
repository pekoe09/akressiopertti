package akressiopertti.domain;

/**
 * Haun tuloksena saatu osuma.
 */
public class SearchHit {
    
    private String title;
    private String entityType;
    private String description;
    private String viewLink;
    
    public SearchHit(String title,
                        String entityType,
                        String description,
                        String viewLink) {
        this.title = title;
        this.entityType = entityType;
        this.description = description;
        this.viewLink = viewLink;
    }

    public String getTitle() {
        return title;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getDescription() {
        return description;
    }

    public String getViewLink() {
        return viewLink;
    }

}
