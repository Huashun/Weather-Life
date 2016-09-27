package entity;

/**
 * Created by liangchenzhou on 23/05/16.
 *
 * Cloth entity is used for Cloth data operations
 */
public class Cloth {
    private int clothId;
    private String clothName;
    private String clothDesc;
    private String clothType;

    public static final String CREATECLOTH_TABLESTRING =  "CREATE TABLE CLOTHES " +
            "( CLOTH_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "CLOTHNAME TEXT NOT NULL," +
            "CLOTHDESC TEXT," +
            "CLOTHTYPE TEXT NOT NULL)";

    public Cloth() {
    }

    public Cloth(String clothName, String clothDesc, String clothType) {
        this.clothName = clothName;
        this.clothDesc = clothDesc;
        this.clothType = clothType;
    }

    public Cloth(int clothId, String clothName, String clothDesc, String clothType) {
        this.clothId = clothId;
        this.clothName = clothName;
        this.clothDesc = clothDesc;
        this.clothType = clothType;
    }

    public int getClothId() {
        return clothId;
    }

    public void setClothId(int clothId) {
        this.clothId = clothId;
    }

    public String getClothName() {
        return clothName;
    }

    public void setClothName(String clothName) {
        this.clothName = clothName;
    }

    public String getClothDesc() {
        return clothDesc;
    }

    public void setClothDesc(String clothDesc) {
        this.clothDesc = clothDesc;
    }

    public String getClothType() {
        return clothType;
    }

    public void setClothType(String clothType) {
        this.clothType = clothType;
    }
}
