package com.xzq.named.controller;

import cn.hutool.json.JSONUtil;
import com.xzq.named.service.NameService;
import com.xzq.named.vo.NamedCondition;
import com.xzq.named.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NameController {

    @Autowired
    private NameService nameService;

    @RequestMapping("/named")
    public Result named(NamedCondition condition) {

        System.out.println(JSONUtil.toJsonPrettyStr(condition));

        return Result.success(nameService.generateName(condition));
    }

    @RequestMapping("/searchName")
    public Result searchName(String name) {

        System.out.println("名字查询：" + name);

        return nameService.searchName(name);
    }

}
