package com.xzq.named.constant;

/**
 * 吉凶
 */
public enum JiXiong {

    DAJI("大吉"), XIONG("凶"), ZHONGJI("中吉");

    private final String name;

    JiXiong(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static JiXiong getByName(String name) {
        for (JiXiong jiXiong : JiXiong.values()) {
            if (name.equals(jiXiong.getName())) {
                return jiXiong;
            }
        }
        return null;
    }

}
