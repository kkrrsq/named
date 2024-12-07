//Layui 扩展组件入口
layui.config({
    base: 'res/layui/lay/modules/extendplus/' //自定义layui组件的目录
}).extend({//设定组件别名
    stools: 'stools'
});

layui.use(['form','layer','stools'],function(){
    var $ = layui.jquery,
        form = layui.form(),
        layer = layui.layer,
        stools = layui.stools;

    //登录
    form.on("submit(name)",function(data){
        stools.request({
            url: _global_ctx + "login",
            data: data.field,
            scb:function(d){
                if(d.code==200){
                    stools.toastS("登录成功",function() {
                        location.href = "index.jsp";
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


