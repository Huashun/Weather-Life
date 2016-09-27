package entity;

/**
 * Created by liangchenzhou on 23/05/16.
 *
 * Recommendation Entity uses for the Recommendation data operation
 */
public class Recommendation {
    private int recom_id;
    private String title;
    private String description;

    public static final String CREATERECOM_TABLESTRING = "CREATE TABLE RECOMMENDATIONS " +
            "( RECOMID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "RECOMTITLE TEXT NOT NULL," +
            "RECOMDESC TEXT)";

    public Recommendation(){
    }

    public Recommendation(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Recommendation(int recom_id, String title, String description) {
        this.recom_id = recom_id;
        this.title = title;
        this.description = description;
    }

    public int getRecom_id() {
        return recom_id;
    }

    public void setRecom_id(int recom_id) {
        this.recom_id = recom_id;
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
}
