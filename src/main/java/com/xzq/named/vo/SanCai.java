package com.xzq.named.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 三才
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SanCai {

    private String sanCaiName;

    private String jiXiong;
}
