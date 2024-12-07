/**
 * 评论业务类 Author: cd0281 Date: 24-07-2017
 */

layui.define([ 'layer', 'stools' ], function(exports) {
	var $ = layui.jquery
	, stools = layui.stools;
	
	var commentUrl = {
		init : __global_ctx_dt + "/do?action=hd/comment&start=init",
		addComment : __global_ctx_dt + "/do?action=hd/comment&start=addComment",
		delComment : __global_ctx_dt + "/do?action=hd/comment&start=delComment",
		pageTree : __global_ctx_dt + "/do?action=hd/comment&start=pageTree",
		page : __global_ctx_dt + "/do?action=hd/comment&start=page"
	}
	
	var cfg = {
		source_id : "", //主题对应外部id
		topic_source : "",
		tree : false  //是否按照二级分布
	}
	
	function comment(config){
		var self = this;
		self.config = $.extend({},cfg,config);
		self.topicObj = {};
		self._init();
	}
	
	comment.prototype = {
		_init:function(){
			var self = this;
			stools.request({
				url: commentUrl.init,
				data:{source_id:self.config.source_id, topic_source:self.config.topic_source},
				scb:function(data, textStatus) {
					self.topicObj = data.data;
					if(typeof self.config.scb ==="function") self.config.scb(self);
				},
				fcb: function(){}
			});
		},
		getComments:function(cg, scb, fcb){
			var self = this;
			cg = $.extend({},{},cg);
			cg["source_id"]=self.topicObj.source_id;
			cg["topic_source"]=self.topicObj.topic_source;
			cg["topic_id"]=self.topicObj.topic_id;
			var tree = self.config.tree;
			var url = commentUrl.page;
			if(tree) url = commentUrl.pageTree;
			stools.request({
				url : url,
				data : cg,
				scb : function(data, textStatus) {
					if(data.code=="200"){
						scb(data);
					}
				},
				fcb : (typeof fcb ==="function")?fcb:function(){}
			});
		},
		doComment:function(r, scb, fcb){
			var self = this;
			r["source_id"]=self.topicObj.source_id;
			r["topic_id"]=self.topicObj.topic_id;
			r["topic_source"]=self.topicObj.topic_source;
			stools.request({
				url : commentUrl.addComment,
				data : r,
				scb : scb,
				fcb : (typeof fcb ==="function")?fcb:function(){}
			});
		},
		doReply:function(r, scb, fcb){
			this.doComment(r, scb, fcb);
		},
		delComment:function(r, scb, fcb){
			var self = this;
			r["source_id"]=self.topicObj.source_id;
			r["topic_id"]=self.topicObj.topic_id;
			r["topic_source"]=self.topicObj.topic_source;
			stools.request({
				url : commentUrl.addComment,
				data : r,
				scb : scb,
				fcb : (typeof fcb ==="function")?fcb:function(){}
			});
		},
		delReply:function(r, scb, fcb){
			this.delComment(r, scb, fcb);
		}
	}
	
	exports("comment", comment);
});
