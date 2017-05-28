package th.ac.bu.mcop.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jeeraphan on 5/27/17.
 */

public class Scan {

    @SerializedName("engien")
    String engien;

    @SerializedName("found")
    String found;

    public String getEngien() {
        return engien;
    }

    public void setEngien(String engien) {
        this.engien = engien;
    }

    public String getFound() {
        return found;
    }

    public void setFound(String found) {
        this.found = found;
    }
}
