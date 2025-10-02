package com.boolint.photogallery;

/**
 * 메뉴 아이템 데이터 클래스
 * MenuVo를 기반으로 한 MenuItem
 */
public class MenuItem {
    public String id;
    public String actType; // MAP, SUB, IMG, IMGS
    public String title;
    public String kind;
    public String data0;
    public String area0;
    public String data1;
    public String area1;
    public String data2;
    public String area2;
    public int icon;
    public String iconUrl = "";
    public String apiOption;
    public String apiOption2;

    public MenuItem() {
    }

    public MenuItem(String actType) {
        if ("MAP".equals(actType)) {
            this.actType = "MAP";
        } else if ("SUB".equals(actType)) {
            this.actType = "SUB";
        } else if ("IMG".equals(actType)) {
            this.actType = "IMG";
        } else if ("IMGS".equals(actType)) {
            this.actType = "IMGS";
        }
    }

    // Getter 메서드들
    public String getId() { return id; }
    public String getActType() { return actType; }
    public String getTitle() { return title; }
    public String getKind() { return kind; }
    public String getData0() { return data0; }
    public String getArea0() { return area0; }
    public String getData1() { return data1; }
    public String getArea1() { return area1; }
    public String getData2() { return data2; }
    public String getArea2() { return area2; }
    public int getIcon() { return icon; }
    public String getIconUrl() { return iconUrl; }
    public String getApiOption() { return apiOption; }
    public String getApiOption2() { return apiOption2; }

    // Setter 메서드들
    public void setId(String id) { this.id = id; }
    public void setActType(String actType) { this.actType = actType; }
    public void setTitle(String title) { this.title = title; }
    public void setKind(String kind) { this.kind = kind; }
    public void setData0(String data0) { this.data0 = data0; }
    public void setArea0(String area0) { this.area0 = area0; }
    public void setData1(String data1) { this.data1 = data1; }
    public void setArea1(String area1) { this.area1 = area1; }
    public void setData2(String data2) { this.data2 = data2; }
    public void setArea2(String area2) { this.area2 = area2; }
    public void setIcon(int icon) { this.icon = icon; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public void setApiOption(String apiOption) { this.apiOption = apiOption; }
    public void setApiOption2(String apiOption2) { this.apiOption2 = apiOption2; }
}