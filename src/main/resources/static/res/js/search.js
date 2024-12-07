//Layui 扩展组件入口
layui.config({
    base: 'res/layui/lay/modules/extendplus/' //自定义layui组件的目录
}).extend({//设定组件别名
    stools: 'stools',
    tag: 'tag/tag'
});

layui.use(['form','stools','laytpl'],function(){
    var $ = layui.jquery,
        form = layui.form,
        stools = layui.stools,
        laytpl = layui.laytpl;

    // 取名
    form.on("submit(search)",function(data){

        if (data.field.name.length == 0) {
            stools.toastE("请输入姓名");
            return false;
        }

        if (data.field.name.length != 3) {
            stools.toastE("请输入姓名，暂时只支持单姓复名");
            return false;
        }

        stools.request({
            url:  "searchName",
            data: data.field,
            scb:function(d){
                if(d.code==200){
                    stools.toastS("查询完成",function() {
                        var getTpl = $('#tpl').html();
                        laytpl(getTpl).render(d.data, function(htm){
                            $('#view').html(htm);
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

});