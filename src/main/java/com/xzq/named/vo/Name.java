package com.xzq.named.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Name {

    private String firstName;

    private String lastName;

    /**
     * 名字
     */
    private String name;

    /**
     * 繁体名字
     */
    private String traditionalName;

    private Source source;

    private String content;

    private List<Integer> strokeList;

    private List<WuGeVo> wuGeList;

    private SanCai sanCai;

}
