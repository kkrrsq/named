package com.xzq.named.constant;

/**
 * 五格类型
 */
public enum WuGeType {

    TIANGE("天格"), RENGE("人格"), DIGE("地格"), ZONGGE("总格"), WAIGE("外格");

    private final String name;

    WuGeType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static WuGeType getByName(String name) {
        for (WuGeType wuGeType : WuGeType.values()) {
            if (name.equals(wuGeType.getName())) {
                return wuGeType;
            }
        }
        return null;
    }

}
