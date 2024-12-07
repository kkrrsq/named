package com.xzq.named.vo;

import com.xzq.named.constant.SourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Source {

    private String title;

    private String author;

    private String content;

    private SourceType sourceType;

    private List<String> contentList;

    private List<Integer> strokeList;

}
