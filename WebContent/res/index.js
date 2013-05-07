var onlineListStore = null;
var messageStore = null;
var editor = null;

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
}

function sendMessage() {
	if (editor) {
		Chat.sendMessage(editor.getValue());
		editor.setValue('');
	}
}

Ext.tip.QuickTipManager.init();

Ext.onReady(function() {

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
			if (e.ctrlKey && Ext.EventObject.ENTER == e.getKey()) {
				sendMessage();
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

	var onlineListGrid = {
		xtype : 'grid',
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
	};

	var header = {
		xtype : 'box',
		region : 'north',
		html : '<h1>聊天室</h1>',
		cls : 'header',
		height : 50
	};

	var messageGrid = {
		xtype : 'grid',
		region : 'center',
		store : messageStore,
		columns : [ {
			text : '聊天记录',
			dataIndex : 'message',
			flex : 1,
			hideable : false,
			renderer : renderMessage
		} ]
	};

	var sendBox = {
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
			text : '发送(Ctrl + Enter)',
			handler : function() {
				sendMessage();
			}
		} ]
	};

	var viewport = Ext.create('Ext.Viewport', {
		layout : {
			type : 'border'
		},
		defaults : {
			split : false
		},
		items : [ header, onlineListGrid, messageGrid, sendBox ]
	});

	dwr.engine.setActiveReverseAjax(true);

	Chat.login('' + Math.random(), function() {
	});

	addMessage('hi', 'time', 'conetdsd');

	function renderMessage(value, p, record) {
		return Ext.String.format('<p><strong>{0}</strong> <em>{1}</em>:</p><p>{2}</p>',
				record.data.name, record.data.time, value);
	}
});
