package me.yirf.afk.enums;

public enum ClickType {
    FILL,
    ITEM,
    COMMAND,
    LEAVE;

    public static ClickType fromString(String type) {
        try {
            return ClickType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}