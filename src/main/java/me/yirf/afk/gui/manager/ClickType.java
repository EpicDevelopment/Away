package me.yirf.afk.gui.manager;

public enum ClickType {
    FILL,
    ITEM,
    LEAVE;

    public static ClickType fromString(String type) {
        try {
            return ClickType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}