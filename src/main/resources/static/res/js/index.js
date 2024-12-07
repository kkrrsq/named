//Layui 扩展组件入口
layui.config({
    base: 'res/layui/lay/modules/extendplus/' //自定义layui组件的目录
}).extend({//设定组件别名
    stools: 'stools',
    tag: 'tag/tag'
});

layui.use(['form','stools','table','tag'],function(){
    var $ = layui.jquery,
        form = layui.form,
        stools = layui.stools,
        table = layui.table,
        tag = layui.tag;

    var goodWords = new Array();

    var badWords = new Array();

    // 取名
    form.on("submit(named)",function(data){

        if (data.field.lastName.length == 0) {
            stools.toastE("请输入姓氏");
            return false;
        }

        if (data.field.lastName.length > 1) {
            stools.toastE("请输入单姓，暂不支持复姓");
            return false;
        }

        if ($("input:checkbox[name='sourceTypeList']:checked").length == 0) {
            stools.toastE("请至少选择一个来源");
            return false;
        }
        //获取checkbox[name='level']的值，获取所有选中的复选框，并将其值放入数组中
        var arr = new Array();
        $("input:checkbox[name='sourceTypeList']:checked").each(function(i){
            arr[i] = $(this).val();
        });
        //  替换 data.field
        data.field.sourceTypeList = arr;

        // 验证笔画
        var minStroke = data.field.minStroke;
        var maxStroke = data.field.maxStroke;
        if ((minStroke != "" && !(/^[1-9]\d*$/.test(minStroke))) || (maxStroke != "" && !(/^[1-9]\d*$/.test(maxStroke)))){
            stools.toastE("笔画只能输入数字");
            return false;
        }
        if (minStroke != "" && maxStroke != "" && parseInt(maxStroke) < parseInt(minStroke)) {
            stools.toastE("笔画范围错误");
            return false;
        }

        data.field.goodWordList = goodWords;

        data.field.badWordList = badWords;

        stools.request({
            url:  "named",
            data: data.field,
            scb:function(d){
                if(d.code==200){
                    stools.toastS("取名完成",function() {
                        table.render({
                            elem: '#table'
                            ,toolbar: true
                            ,cols: [[
                                {field:'name', title: '姓名', width:100
                                    ,templet: function(d){
                                        return d.name
                                    }
                                }
                                ,{field:'stroke', title: '笔画', width:120
                                    ,templet: function(d){
                                        return d.strokeList.join(" , ")
                                    }
                                }
                                ,{field:'strokeCount', title: '笔画总数', width:100
                                    ,templet: function(d){
                                        return d.strokeList[0] + d.strokeList[1] + d.strokeList[2]
                                    }
                                }
                                ,{field:'content', title: '来源'
                                    ,templet: function(d){
                                        return d.content
                                    }
                                }
                            ]]
                            ,data:d.data
                            ,page:true
                        });
                    });
                }else{
                    stools.toastE(d.msg);
                }
            },
            fcb:function(){
                stools.toastE("出错啦");
            }
        });
        return false;
    });

    tag.on('add(goodWord)', function (data) {
        goodWords.push(data.othis);
    });

    tag.on('delete(goodWord)', function (data) {
        goodWords.splice(data.index, 1);
    });

    tag.on('add(badWord)', function (data) {
        badWords.push(data.othis);
    });

    tag.on('delete(badWord)', function (data) {
        badWords.splice(data.index, 1);
    });

    form.on('switch(validate)', function(data) {
        if(data.elem.checked) {
            $('input[name="sex"]').prop('disabled', false);
        } else {
            $('input[name="sex"][value="0"]').prop('checked', true);
            $('input[name="sex"]').prop('disabled', true);
        }
        form.render('radio');
    });

});