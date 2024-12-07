/**
 * 头像上传裁剪类 Author: cd0281 Date: 17-03-17
 */

layui.define(['layer', 'stools', 'webuploader', 'jcrop' ], function(exports) {
	var $ = layui.jquery
	, layer = layui.layer
	, stools = layui.stools
	, webuploader = layui.webuploader
	, jcrop = layui.jcrop;
	
	var image_area = {
		x : 0,
		y : 0,
		w : 0,
		h : 0
	};

	var imageTW = 0;
	var imageTH = 0;
	var imageVW = 0;
	var imageVH = 0;
	
	var elesObj = {}
	
	var what = function () {
        this.config = {
        	server:"",   //服务器地址
        	swfUrl : "",    //上传组件swf地址
        	container:"", //渲染容器#id
        	showlpic:false, //是否显示小图
        	publicBuk : true,  //是否是共有桶
			ratio : 1, // 裁剪比例
			img:"", //初始化图片
			success : function(data) {  //成功的回调
			}
        };
    };
	
    //初始化
    what.prototype.init = function(options){
    	var that = this;
    	this.config = $.extend({}, this.config, options);
    	var _options = this.config;
    	//渲染元素
    	this.render();
    	//渲染初始化图片
    	if (_options.img != "") {
			this.renderImage(_options.img);
		}
    	//装配上传组件
    	this.renderUpload(elesObj.uploaderBtn);
    	
    	//装配确定按钮事件
    	this.eventSubmit(elesObj.submitBtn);
    	
    }
    //元素渲染
    what.prototype.render = function(){
    	var that = this;
    	var _options = this.config;
    	var html = '<div class="crop-image">\
    					<div class="crop-opt-area-wrap">\
    						<div class="inner">\
    							<div class="crop-opt-area">\
    								<img src="" class="crop-image"/>\
    							</div>\
    						</div>\
    					</div>\
    					<div class="crop-preview-wrap">\
    						<div class="crop-preview">\
    							<div class="preview-1">\
    								<img src="" class="crop-preview-image"/>\
    							</div>\
    							<div class="preview-2">\
    								<img src="" class="crop-preview-image"/>\
    							</div>\
    						</div>\
    					</div>\
    					<div class="crop-tips error"></div>\
    					<div class="btns">\
    						<a href="javascript:;" class="btn crop-btn btn-selectImg btn-2">选择图片</a>\
    						<a href="javascript:;" class="btn crop-btn btn-cropSave btn-1"  >保存</a>\
    					</div>\
    				</div>';
    	
		var content = $(html);
		elesObj.imageContent = content.find('.crop-opt-area');
		elesObj.previewContent = content.find('.crop-preview .preview-1, .crop-preview .preview-2');
		elesObj.previewDom = elesObj.previewContent.find("img");
		elesObj.uploaderBtn = content.find('.btn-selectImg');
		elesObj.submitBtn = content.find(".btn-cropSave");
		elesObj.tipsDom = content.find(".crop-tips");
		if (!_options.showlpic) {
			content.find(".crop-preview-wrap").hide();
		}
		$(_options.container).html(content);
    }
    
    //渲染图片
    what.prototype.renderImage = function(image){
    	var that = this;
    	var _options = this.config;
    	if (image == "") {
    		elesObj.tipsDom.text("您的图片不能为空");
			return false;
		}
    	
    	var imgDom = $("<img>").attr({
			src : image
		}).css({
			maxWidth : 300,
			maxHeight : 300
		}).load(function() {
			imageVW = $(this).width();
			imageVH = $(this).height();
		});
    	
    	elesObj.imageContent.html(imgDom);
    	elesObj.previewDom.attr({
			src : image
		});
    	
    	// 获取图片真实尺寸
		var imgObj = new Image();
		var imageLoad = function() {
			imageTW = imgObj.width;
			imageTH = imgObj.height;
			var sw, sh;
			if (imageVW > imageVH) {
				sh = imageVH;
				sw = imageVH * _options.ratio;
				if (_options.ratio > 1) {
					if (sw > imageVW) {
						sw = imageVW;
						sh = imageVW / _options.ratio;
					}
				}
			} else {
				sw = imageVW;
				sh = imageVW / _options.ratio;
				if (_options.ratio < 1) {
					if (sh > imageVH) {
						sh = imageVH;
						sw = imageVH * _options.ratio;
					}
				}
			}
			imgDom.Jcrop({
				onChange : preview,
				onSelect : preview,
				keySupport : false,
				bgColor : "",
				setSelect : [ (imageVW - sw) / 2, (imageVH - sh) / 2, sw, sh ],
				aspectRatio : _options.ratio
			// 长宽比
			}, function() {
				var jcropHolder = imgDom.next(".jcrop-holder");
				jcropHolder.css({
					left : '50%',
					marginLeft : -jcropHolder.width() / 2
				});
				preview({
					w : sw,
					h : sh,
					x : (imageVW - sw) / 2,
					y : (imageVH - sh) / 2
				});
			})
		}
		
		var isLoad, t_img;
		imgObj.onload = function() {
			imgObj.onload = null;
			// 坑，解决某些时候图片未加载完便执行onload方法的问题
			if (imgObj.height === 0) {
				isLoad = false;
			}
			if (isLoad) {
				clearTimeout(t_img);
				imageLoad();
			} else {
				isLoad = true;
				t_img = setTimeout(function() {
					imageLoad();
				}, 300);
			}
		};
		imgObj.src = image;
		
    }
    var preview = function(data){
    	if (parseInt(data.w) > 0) {
			// 计算选区的真实尺寸/坐标
			var realWidth = Math.round(data.w / imageVW * imageTW);
			var realHeight = Math.round(data.h / imageVH * imageTH);
			var realLeft = Math.round(data.x / (imageVW / imageTW));
			var realTop = Math.round(data.y / (imageVH / imageTH));
			image_area = {
				x : realLeft,
				y : realTop,
				w : realWidth,
				h : realHeight
			}
			// 计算当选区预览时，预览图需要的尺寸
			elesObj.previewContent.each(function() {
				var previewWidth = Math.round($(this).width() / realWidth * imageTW);
				var previewHeight = Math.round($(this).height() / realHeight * imageTH);
				var previewLeft = Math.round(($(this).width() / realWidth) * realLeft);
				var previewTop = Math.round(($(this).height() / realHeight) * realTop);
				$(this).find("img").css({
					width : previewWidth,
					height : previewHeight,
					marginLeft : '-' + previewLeft + 'px',
					marginTop : '-' + previewTop + 'px'
				});
			})
		}
    }
    
    what.prototype.renderUpload = function(uploaderBtn){
    	var that = this;
    	var _options = this.config;
    	
    	var uploader = webuploader.create({
    	    // swf文件路径 
    	    swf: _options.swfUrl,
    	    // 文件接收服务端。
    	    server: _options.server,
    	    // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
    	    resize: false,
    	    auto : false,
    	    pick : {
				id : uploaderBtn,
				multiple : false
			},
			chunked : false,
			compress : false,
			fileNumLimit : 1,
			duplicate : false,
			accept : {
				extensions : 'jpg,jpeg,png,gif,JPG,JPEG,PNG,GIF',
				mimeTypes : 'image/jpg,image/jpeg,image/png,image/gif'
			},
			fileSizeLimit : 1024 * 1024,
			thumb : {
				type : ''
			}
    	});
    	
    	uploader.on('error', function(type) {
			if (type == 'Q_TYPE_DENIED') {
				elesObj.tipsDom.text("只支持jpg、png、gif三种图片格式");
			} else if (type == 'Q_EXCEED_SIZE_LIMIT') {
				elesObj.tipsDom.text("上传文件大小不能超过1Mb");
			} else {
				elesObj.tipsDom.text("上传文件出错");
			}
		});

		uploader.on('beforeFileQueued', function(file) {
			// 多次选择图片时，替换掉队列中的原图片
			if (uploader.getFiles().length > 0) {
				$.each(uploader.getFiles(), function(i, file) {
					uploader.removeFile(file, true);
				});
			}
			elesObj.tipsDom.text("");
		});

		uploader.on('fileQueued', function(file) {
			uploader.makeThumb(file, function(error, src) {
				if (error) {
					elesObj.tipsDom.text("该图片不能预览，请重新选择");
					return;
				}
				that.previewImageSrc(src);
			}, 1, 1);
		});
    	
		_options.uploader = uploader;
		
    }
    
    //预览图片
    what.prototype.previewImageSrc = function(src){
    	var that = this;
    	var _options = this.config;
    	if (isBase64Supported) {
    		that.renderImage(src);
		} else {
			var params = {
				mode : "preview",
				public_buk : _options["publicBuk"] ? 1 : 0
			}
			var previewUploader = webuploader.create({
				swf: _options.swfUrl,
				resize: false,
	    	    auto : false,
				server : _options["server"] + "&" + $.param(params, true)
			});
			previewUploader.addFiles(_options.uploader.getFiles());
			previewUploader.on('uploadSuccess', function(file, response) {
				if (response.code != '200') {
					stools.toastE("图片预览失败，请重试");
				} else {
					var src = response.data.fileNames;
					that.renderImage(src);
				}
			});
			previewUploader.upload();
		}
    }
    
    what.prototype.eventSubmit = function(submitBtn){
    	var that = this;
    	var _options = this.config;
    	
    	submitBtn.on("click", function(e) {
			e.preventDefault();
			var uploader = _options.uploader;
			if (_options.uploader.getFiles().length > 0) {
				var params = {
					mode : "uploadCrop",
					public_buk : _options["publicBuk"] ? 1 : 0,
					x : image_area.x,
					y : image_area.y,
					w : image_area.w,
					h : image_area.h
				};
				var loadIdx;
				uploader.option('server', _options["server"] + "&" + $.param(params, true));
				uploader.on('startUpload', function() {
					loadIdx = layer.load(2);
				});
				uploader.on('uploadSuccess', function(file, response) {
					if (response.code == "200") {
						_options.success && _options.success(response);
					} else {
						elesObj.tipsDom.text(response.msg);
					}
				});
				uploader.on('uploadFinished', function() {
					layer.close(loadIdx);
				});
				uploader.upload();
			} else {
				if (_options.img != "") {
					var img = new Image();
					img.src = _options.img;
					if (img.width > 0 || img.height > 0) {
						var params = {
							mode : "crop",
							img : _options.img,
							public_buk : _options["publicBuk"] ? 1 : 0,
							x : image_area.x,
							y : image_area.y,
							w : image_area.w,
							h : image_area.h
						};

						stools.request({
		            		url: _options["server"],
		            		data: params,
		            		scb:function(data){
		            			if (data.code == "200") {
									_options.success && _options.success(data);
								} else {
									elesObj.tipsDom.text(data.msg);
								}
		            		},
		            		fcb:function(){
		            		}
		            	});
					}
				} else {
					elesObj.tipsDom.text("请先上传一张图片");
				}
			}
		});    	
    }
    
	
    //判读是否支持base64
    var isBase64Supported = (function() {
		var data = new Image();
		var support = true;
		data.onload = data.onerror = function() {
			if (this.width != 1 || this.height != 1) {
				support = false;
			}
		}
		data.src = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";
		return support;
	})();
    
	
	exports("piccutupload", what);
});
