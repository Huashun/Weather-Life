package entity;

/**
 * User suburb preference entity
 */
public class PrefSuburb {
    private String latitudeSub;
    private String longitudeSub;

    public PrefSuburb(){}

    public PrefSuburb(String latitudeSub, String longitudeSub) {
        this.latitudeSub = latitudeSub;
        this.longitudeSub = longitudeSub;
    }

    public String getLatitudeSub() {
        return latitudeSub;
    }

    public void setLatitudeSub(String latitudeSub) {
        this.latitudeSub = latitudeSub;
    }

    public String getLongitudeSub() {
        return longitudeSub;
    }

    public void setLongitudeSub(String longitudeSub) {
        this.longitudeSub = longitudeSub;
    }
}
