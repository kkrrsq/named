package com.xzq.named.constant;

/**
 * 来源类型
 */
public enum SourceType {

    CHUCI("楚辞", "data/chuci.txt"), ZHOUYI("周易", "data/zhouyi.txt"), LUNYU("论语", "data/lunyu.json"), SHIJING("诗经", "data/shijing.json"), TANGSHI("唐诗", "data/tangshi"), SONGCI("宋词", "data/songci"), SONGSHI("宋诗", "data/songshi");

    private final String name;

    private final String path;

    SourceType(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return this.name;
    }

    public String getPath() {
        return this.path;
    }

    public static SourceType getByName(String name) {
        for (SourceType sourceType : SourceType.values()) {
            if (name.equals(sourceType.getName())) {
                return sourceType;
            }
        }
        return null;
    }

}
