/**
 * 分享工具类 Author: cd0281 Date: 24-07-2017
 */

layui.define([ 'layer' ], function(exports) {
	var $ = layui.jquery;
	
	var shareUrl = {
			wb : "http://service.weibo.com/share/share.php?url={url}&title={title}&appkey={appkey}&pic={pic}",
			qzone : "http://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?url={url}&pics={pic}&title={title}&summary={summary}&desc={desc}",
			qq: "http://connect.qq.com/widget/shareqq/index.html?url={url}&pics={pic}&title={title}&summary={summary}&desc={desc}"
	};
	
	var cfg ={
		url: "",  //访问的地址
		title: "德育",   //访问的标题
		pic:"",  //分享的图片地址
		summary:"德育", //总结
		desc : "德育" //描述
	};
	
	//初始化 filter 为jquery对象 如  $('#ul a')
	var share = function(config, filter){
		var self = this;
		self.config = $.extend({},cfg,config);
		if(filter){
			filter.on('click', function(){
				var type = $(this).data("share_type");
				self.shareClick(type);
			});
		}
	}
	
	//分享点击事件
	share.shareClick = share.prototype.shareClick = function(type, defconfig){
		var self = this;
		var surl = shareUrl[type];
		if(surl){
			if(defconfig){
				surl = formatmodel(surl, defconfig);
			}else{
				surl = formatmodel(surl, self.config);
			}
			
			window.open(surl);
		}
	}
	
	//私有方法
	function formatmodel(str,model){
	    for(var k in model){
	        var re = new RegExp("{"+k+"}","g");
	        if(k == "url" || k == "pic"){
	        	str = str.replace(re,encodeURIComponent(model[k]));
	        }else{
	        	str = str.replace(re,model[k]);
	        }
	    }
	    return str;
	}
	
	exports("pcshare", share);
});
