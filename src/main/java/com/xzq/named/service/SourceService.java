package com.xzq.named.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xzq.named.constant.SourceType;
import com.xzq.named.vo.Source;
import org.nlpcn.commons.lang.jianfan.JianFan;
import org.nlpcn.commons.lang.standardization.SentencesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xzq.named.constant.SourceType.*;

@Service
public class SourceService {

    private static final String CHINESE_NAME_DAT = "data/Chinese_Names.dat";

    @Autowired
    private StrokeService strokeService;

    private Map<SourceType, List<Source>> sourceMap;

    /**
     * 名字库
     */
    private Map<String, String> nameMap;

    private SentencesUtil sentencesUtil;

    /**
     * 初始化
     * 加载所有资源
     */
    @PostConstruct
    public void init() {
        sourceMap = CollUtil.newHashMap();
        nameMap = CollUtil.newHashMap();
        sentencesUtil = new SentencesUtil();
        loadSource();
        loadNameDat();
    }

    public List<Source> listSourceByType(SourceType sourceType) {
        return sourceMap.get(sourceType);
    }

    public boolean validateName(String name, int sex) {
        if (sex == 0) {
            return nameMap.containsKey(name);
        }
        if (sex == 1) {
            return nameMap.containsKey(name) && nameMap.get(name).contains("男");
        }
        if (sex == 2){
            return nameMap.containsKey(name) && nameMap.get(name).contains("女");
        }
        return true;
    }

    private void loadSource() {
        String content = ResourceUtil.readUtf8Str(CHUCI.getPath());
        loadSourceListByTxt(CHUCI, content);

        content = ResourceUtil.readUtf8Str(ZHOUYI.getPath());
        loadSourceListByTxt(SourceType.ZHOUYI, content);

        content = ResourceUtil.readUtf8Str(LUNYU.getPath());
        loadSourceListByJson(SourceType.LUNYU, content);

        content = ResourceUtil.readUtf8Str(SHIJING.getPath());
        loadSourceListByJson(SourceType.SHIJING, content);

        List<String> files = getAllFilesInDirectory(TANGSHI.getPath());
        for (String file : files) {
            if (!file.startsWith("authors")) {
                content = ResourceUtil.readUtf8Str(TANGSHI.getPath() + File.separator + file);
                loadSourceListByJson(SourceType.TANGSHI, content);
            }
        }

        files = getAllFilesInDirectory(SONGCI.getPath());
        for (String file : files) {
            if (!file.startsWith("authors")) {
                content = ResourceUtil.readUtf8Str(SONGCI.getPath() + File.separator + file);
                loadSourceListByJson(SourceType.SONGCI, content);
            }
        }

        files = getAllFilesInDirectory(SONGSHI.getPath());
        for (String file : files) {
            if (!file.startsWith("authors")) {
                content = ResourceUtil.readUtf8Str(SONGSHI.getPath() + File.separator + file);
                loadSourceListByJson(SourceType.SONGSHI, content);
            }
        }

        for (SourceType sourceType : sourceMap.keySet()) {
            System.out.println(sourceType.getName() + " : " + sourceMap.get(sourceType).size());
        }

    }

    private void loadSourceListByTxt(SourceType sourceType, String content) {
        List<String> sentenceList = sentencesUtil.toSentenceList(content);
        List<Source> sourceList = CollUtil.newArrayList();
        for (String s : sentenceList) {
            Source source = new Source();

            String fs = JianFan.j2f(s);
            source.setContent(fs);

            List<String> contentList = CollUtil.toList(fs.split(""));
            source.setContentList(contentList);

            List<Integer> strokeList = CollUtil.newArrayList();
            contentList.forEach(e -> {
                strokeList.add(strokeService.getStroke(e));
            });
            source.setStrokeList(strokeList);

            source.setSourceType(sourceType);
            sourceList.add(source);
        }
        if (sourceMap.containsKey(sourceType)) {
            sourceMap.get(sourceType).addAll(sourceList);
        } else {
            sourceMap.put(sourceType, sourceList);
        }
    }

    private void loadSourceListByJson(SourceType sourceType, String content) {
        List<String> list = CollUtil.newArrayList();
        JSONArray jsonArray = JSONUtil.parseArray(content);
        List<Source> sourceList = CollUtil.newArrayList();
        for (Object o : jsonArray) {
            JSONObject obj = (JSONObject) o;
            JSONArray lines = obj.getJSONArray("paragraphs");
            if (null == lines) {
                continue;
            }
            for (int j = 0; j < lines.size(); j++) {
                String line = lines.getStr(j);
                List<String> sentenceList = sentencesUtil.toSentenceList(line);
                for (String s : sentenceList) {
                    Source source = new Source();

                    String fs = JianFan.j2f(s);
                    source.setContent(fs);

                    List<String> contentList = CollUtil.toList(fs.split(""));
                    source.setContentList(contentList);

                    List<Integer> strokeList = CollUtil.newArrayList();
                    contentList.forEach(e -> {
                        strokeList.add(strokeService.getStroke(e));
                    });
                    source.setStrokeList(strokeList);

                    source.setSourceType(sourceType);
                    sourceList.add(source);
                    list.add(s);
                }
            }
        }
        if (sourceMap.containsKey(sourceType)) {
            sourceMap.get(sourceType).addAll(sourceList);
        } else {
            sourceMap.put(sourceType, sourceList);
        }
    }

    private void loadNameDat() {
        String content = ResourceUtil.readUtf8Str(CHINESE_NAME_DAT);
        List<String> sentenceList = sentencesUtil.toSentenceList(content);
        for (String s : sentenceList) {
            String[] data = s.split(",");
            String name = data[0].substring(1);
            String gender = data[1];
            if (nameMap.containsKey(name)) {
                if (!gender.equals(nameMap.get(name)) || gender.equals("未知")) {
                    nameMap.put(name, "男女");
                }
            } else {
                nameMap.put(name, gender);
            }
        }

    }

    private List<String> getAllFilesInDirectory(String directoryPath) {
        List<String> fileList = new ArrayList<>();

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = null;
        try {
            resources = resolver.getResources("classpath:" + directoryPath + "/*");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Resource resource : resources) {
            fileList.add(resource.getFilename());
        }

        return fileList;
    }

}
