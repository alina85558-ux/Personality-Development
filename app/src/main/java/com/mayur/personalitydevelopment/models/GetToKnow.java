package com.mayur.personalitydevelopment.models;

import java.util.List;

/**
 * Created by Admin on 10/8/2017.
 */

public class GetToKnow {

    private List<PdMainBean> pd_main;

    public List<PdMainBean> getPd_main() {
        return pd_main;
    }

    public void setPd_main(List<PdMainBean> pd_main) {
        this.pd_main = pd_main;
    }

    public static class PdMainBean {

        private String id;
        private String title;
        private String description;
        private long time_stamp;
        private String cat;
        private String sub_cat;
        private String image;
        private String icon;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public long getTime_stamp() {
            return time_stamp;
        }

        public void setTime_stamp(long time_stamp) {
            this.time_stamp = time_stamp;
        }

        public String getCat() {
            return cat;
        }

        public void setCat(String cat) {
            this.cat = cat;
        }

        public String getSub_cat() {
            return sub_cat;
        }

        public void setSub_cat(String sub_cat) {
            this.sub_cat = sub_cat;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
