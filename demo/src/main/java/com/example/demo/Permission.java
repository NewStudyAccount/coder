package com.example.demo;

public class Permission {
    private Long id;
    private String permissionName;
    private String description;

    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPermissionName() { return permissionName; }
    public void setPermissionName(String permissionName) { this.permissionName = permissionName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}