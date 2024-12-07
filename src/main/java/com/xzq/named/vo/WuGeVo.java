package com.xzq.named.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WuGeVo {

    private String wuGe;

    private String jiXiong;

    public WuGeVo(String wuGe, String jiXiong) {
        this.wuGe = wuGe;
        this.jiXiong = jiXiong;
    }
}
