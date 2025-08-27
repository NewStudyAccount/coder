package com.example.demo;

public class Menu {
    private Long id;
    private Long parentId;
    private String menuName;
    private String path;
    private String perms;
    private String type;
    private String icon;
    private Integer orderNum;
    private Boolean visible;

    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getPerms() { return perms; }
    public void setPerms(String perms) { this.perms = perms; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }

    public Boolean getVisible() { return visible; }
    public void setVisible(Boolean visible) { this.visible = visible; }
}