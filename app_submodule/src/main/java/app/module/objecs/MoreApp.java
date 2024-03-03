package app.module.objecs;

public class MoreApp {
    String app, title, des_short, icon, banner, txt_btn;
    int index;
    double rate;

    @Override
    public String toString() {
        return "MoreApp{"
                + "app='"
                + app
                + '\''
                + ", title='"
                + title
                + '\''
                + ", des_short='"
                + des_short
                + '\''
                + ", icon='"
                + icon
                + '\''
                + ", banner='"
                + banner
                + '\''
                + ", txt_btn='"
                + txt_btn
                + '\''
                + ", index="
                + index
                + ", rate="
                + rate
                + '}';
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes_short() {
        return des_short;
    }

    public void setDes_short(String des_short) {
        this.des_short = des_short;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getTxt_btn() {
        return txt_btn;
    }

    public void setTxt_btn(String txt_btn) {
        this.txt_btn = txt_btn;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
