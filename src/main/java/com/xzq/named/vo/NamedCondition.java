package com.xzq.named.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NamedCondition {

    private String lastName;

    private List<String> sourceTypeList;

    private boolean allowGeneral;

    private boolean checkSanCai;

    private boolean validate;

    private Integer minStroke;

    private Integer maxStroke;

    private Integer sex;

    private List<String> goodWordList;

    private List<String> badWordList;

}
