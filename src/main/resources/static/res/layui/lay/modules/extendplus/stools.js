/**
 * 工具类
 */

layui.define([ 'layer' ], function(exports) {
	var $ = layui.jquery;
	var requestCfg = {
		data : {},
		url : "",
		dataType : "json",
		contentType : "application/x-www-form-urlencoded; charset=utf-8",
		type : "POST",
		async : true,
		loading : true
	}
	var time = 2000;
	var tools = {
		request : function(config) {
			config = $.extend({}, requestCfg, config);
			if(config.loading) layer.load(2);
			$.ajax({
				type : config.type,
				url : config.url,
				data : $.param(config.data, true),
				dataType : config.dataType,
				async : config.async,
				contentType : config.contentType,
				success : function(data, textStatus) {
					if(config.loading) layer.closeAll('loading');
					if (data && data.code == "200") {
						if (typeof (config.scb) === 'function')
							config.scb(data, textStatus);
					} else if (data && data.code == "401" || data && data.code == "402") {
						if(data.data && data.data.fromAction){
							var fromAction = data.data.fromAction;
							var _redirecturl = encodeURIComponent(window.location.href);
							var url = __global_ctx_dt;
							if(fromAction.indexOf("backend/")==0){
								url = url + "/do?action=backend/login&start=login&_redirecturl="+_redirecturl;
							}else if(fromAction.indexOf("h5/")==0){
								url = url + "/do?action=h5/portal&start=login&_redirecturl="+_redirecturl;
							}else if(fromAction.indexOf("pc/")==0){
								url = url + "/do?action=pc/portal&start=index&_redirecturl="+_redirecturl;
							}else if(fromAction.indexOf("platform/admin/") ==0){
								url = __global_ctx + "/do?action=platform/admin/login&start=login&_redirecturl="+_redirecturl;
							}else{
								url = url + "/do?action=pc/portal&start=index&_redirecturl="+_redirecturl;
							}
							if(window.top==window.self){
								window.location.href=url;
							}else{
								window.top.location.href=url;
							}
						}  
					} else if (data && data.code == "500") {
						console.log("500");
					} else if (data && data.code == "410") {
						console.log("410");
					} else {
						if (typeof (config.scb) === 'function')
							config.scb(data, textStatus);
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					if(config.loading) layer.closeAll('loading');
					if (typeof (config.fcb) === 'function')
						config.fcb(XMLHttpRequest, textStatus, errorThrown);
				}
			});
		},
		// 成功提示
		alertS : function(text, title) {
			layer.alert(text, {
				title : title,
				icon : 1,
				time : 5000,
				resize : false,
				zIndex : layer.zIndex,
				anim : Math.ceil(Math.random() * 6)
			});
		},
		// 错误提示
		alertE : function(text, title) {
			layer.alert(text, {
				title : title,
				icon : 2,
				time : 5000,
				resize : false,
				zIndex : layer.zIndex,
				anim : Math.ceil(Math.random() * 6)
			});
		},
		// 信息提示
		alertI : function(text) {
			layer.alert(text, {
				time : 5000,
				resize : false,
				zIndex : layer.zIndex,
				anim : Math.ceil(Math.random() * 6)
			});
			return;
		},
		toastS: function(msg,cb){
			if(typeof cb !== "function"){
				cb = function(){};
			}
			layer.msg(msg, {time: time,icon: 1}, cb);
		},
		toastE: function(msg,cb){
			if(typeof cb !== "function"){
				cb = function(){};
			}
			layer.msg(msg,{time: time,icon: 2}, cb);
		},
		toastI: function(msg,cb){
			if(typeof cb !== "function"){
				cb = function(){};
			}
			layer.msg(msg,{time:time}, cb);
		},
		toast: function(msg,cb){
			if(typeof cb !== "function"){
				cb = function(){};
			}
			layer.msg(msg,{time:time}, cb);
		},
		confirm: function(text, config, cb){
			var cfg = {title: "请确认",resize: false,btn: ['确定', '取消'],btnAlign: 'c',icon: 3};
			config = $.extend({}, cfg, config);
			layer.confirm(text, config, cb);
		}
	}

	exports("stools", tools);
});
