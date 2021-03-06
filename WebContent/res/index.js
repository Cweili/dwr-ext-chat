var SUCCESS = 'success';

var onlineListStore = null;
var messageStore = null;
var editor = null;
var onlineListGrid = null;
var messageGrid = null;
var sendBox = null;
var viewport = null;
var loginWindow = null;

function updateOnlineList(onlineList) {
	if (onlineList && onlineList.length > 0) {
		onlineListStore.loadData(onlineList);
	}
};

function addMessage(name, time, message) {
	if (messageStore) {
		messageStore.add({
			name : name,
			time : time,
			message : message
		});
	}

	// 滚动到底部
	if (messageGrid) {
		messageGrid.getView().focusRow(messageGrid.getStore().getCount() - 1);
	}
}

function showErrorBox(title, message, target) {
	if (target) {
		Ext.MessageBox.show({
			title : title,
			msg : message,
			buttons : Ext.MessageBox.OK,
			icon : Ext.MessageBox.ERROR,
			animateTarget : target
		});
		setTimeout(function() {
			Ext.MessageBox.hide();
		}, 1500);
	} else {
		Ext.MessageBox.show({
			title : title,
			msg : msg,
			width : 400,
			icon : Ext.MessageBox.ERROR
		});
	}
}

var page = {
	username : null,
	sendMessage : function() {
		if (editor) {
			editor.setLoading(true);
			Chat.sendMessage(editor.getValue(), function(data) {
				editor.setLoading(false);
				if (SUCCESS == data) {
					editor.setValue('');
				} else {
					showErrorBox('发送失败', '服务器出现错误', editor);
				}
			});
		}
	},

	openLoginWindow : function() {
		if (loginWindow && viewport) {
			loginWindow.show();
			this.toggleDisabled();
		}
	},

	login : function(form) {
		loginWindow.setLoading(true);
		form = form.up('form').getForm();
		var name;
		if (form.isValid()) {
			name = form.getValues().username;
		} else {
			return;
		}
		Chat.login(name, function(data) {
			loginWindow.setLoading(false);
			if (SUCCESS == data) {
				page.username = name;
				page.toggleDisabled();
				loginWindow.hide();
			} else {
				showErrorBox('登录失败', '你的用户名已经被占用', loginWindow);
			}
		});
	},

	disabled : false,

	toggleDisabled : function() {
		if (this.disabled) {
			this.disabled = false;
			onlineListGrid.enable();
			messageGrid.enable();
			sendBox.enable();
		} else {
			this.disabled = true;
			onlineListGrid.disable();
			messageGrid.disable();
			sendBox.disable();
		}
	}
};

Ext.tip.QuickTipManager.init();

Ext.onReady(function() {

	// 重写 HtmlEditor，添加 Ctrl + Enter 监听
	Ext.form.HtmlEditor.override({
		frame : true,
		initComponent : function() {
			this.callOverridden();
			this.addEvents('submit');
		},

		initEditor : function() {
			this.callOverridden();

			var me = this;
			var doc = me.getDoc();

			if (Ext.isGecko) {
				Ext.EventManager.on(doc, 'keypress', me.fireSubmit, me);
			}

			if (Ext.isIE || Ext.isWebKit || Ext.isOpera) {
				Ext.EventManager.on(doc, 'keydown', me.fireSubmit, me);
			}
		},

		fireSubmit : function(e) {
			if (e.ctrlKey && e.ENTER == e.getKey()) {
				page.sendMessage();
			}
		}
	});

	onlineListStore = Ext.create('Ext.data.Store', {
		autoLoad : false,
		fields : [ {
			name : 'name',
			type : 'string'
		}, {
			name : 'time',
			type : 'string'
		} ],
		proxy : {
			type : 'memory',
			reader : {
				type : 'array'
			}
		}
	});

	messageStore = Ext.create('Ext.data.Store', {
		fields : [ {
			name : 'name',
			type : 'string'
		}, {
			name : 'time',
			type : 'string'
		}, {
			name : 'message',
			type : 'string'
		} ],
		proxy : {
			type : 'memory',
			reader : {
				type : 'array'
			}
		}
	});

	editor = Ext.create('Ext.form.HtmlEditor', {
		fontFamilies : [ "微软雅黑", "宋体", "黑体", "Arial", "Courier New", "Tahoma", "Times New Roman",
				"Verdana" ],
		listeners : {
			render : function() {
				this.textareaEl.on('keydown', function() {
					alert();
				}, this);
			}
		}
	});

	onlineListGrid = Ext.create('Ext.grid.Panel', {
		region : 'east',
		collapsible : true,
		split : true,
		width : 250,
		title : '在线列表',
		store : onlineListStore,
		columns : [ {
			text : '用户名',
			dataIndex : 'name',
			flex : 1,
			hideable : false
		}, {
			text : '上线时间',
			dataIndex : 'time',
			width : 138,
			hidden : true
		} ]
	});

	messageGrid = Ext.create('Ext.grid.Panel', {
		region : 'center',
		rowLines : false,
		store : messageStore,
		columns : [ {
			text : '聊天记录',
			dataIndex : 'message',
			flex : 1,
			hideable : false,
			renderer : function(value, p, record) {
				return Ext.String.format(
						'<p class="message-meta"><strong>{0}</strong> <em>{1}</em></p><p>{2}</p>',
						record.data.name, record.data.time, value);
			}
		} ]
	});

	sendBox = Ext.create('Ext.panel.Panel', {
		region : 'south',
		collapsible : true,
		split : true,
		height : 200,
		weight : -100,
		layout : 'fit',
		title : '发送框',
		items : [ editor ],
		bbar : [ '->', {
			width : 160,
			text : '发送 (Ctrl + Enter)',
			handler : function() {
				sendMessage();
			}
		} ]
	});

	var header = {
		xtype : 'box',
		region : 'north',
		html : '<h1>聊天室</h1>',
		cls : 'header',
		height : 50
	};

	viewport = Ext.create('Ext.Viewport', {
		layout : {
			type : 'border'
		},
		defaults : {
			split : false
		},
		items : [ header, onlineListGrid, messageGrid, sendBox ]
	});

	var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';

	loginWindow = Ext.create('Ext.Window', {
		title : '登录',
		width : 300,
		height : 94,
		closable : false,
		border : false,
		items : [ {
			xtype : 'form',
			layout : 'form',
			id : 'simpleForm',
			url : 'save-form.php',
			frame : true,
			fieldDefaults : {
				msgTarget : 'side',
				labelWidth : 75
			},
			defaultType : 'textfield',
			items : [ {
				fieldLabel : '用户名',
				name : 'username',
				afterLabelTextTpl : required,
				allowBlank : false,
				listeners : {
					specialkey : function(field, e) {
						// e.HOME, e.END, e.PAGE_UP, e.PAGE_DOWN,
						// e.TAB, e.ESC, arrow keys: e.LEFT, e.RIGHT, e.UP,
						// e.DOWN
						if (e.getKey() == e.ENTER) {
							page.login(this);
						}
					}
				}
			} ],
			buttons : [ {
				text : '重置',
				handler : function() {
					this.up('form').getForm().reset();
				}
			}, {
				text : '登录',
				handler : function() {
					page.login(this);
				}
			} ]
		} ]
	});

	(function() {
		if (!page.username) {
			page.openLoginWindow();
		}
	})();

	dwr.engine.setActiveReverseAjax(true);

	dwr.engine.setNotifyServerOnPageUnload(true);

});
