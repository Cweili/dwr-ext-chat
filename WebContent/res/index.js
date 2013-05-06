Ext.tip.QuickTipManager.init();

Ext.onReady(function() {
	
	var onlineListStore =  Ext.create('Ext.data.Store', {
		proxy: {
	        type: 'memory'
		}
	});
	
	var onlineListGrid = {
			xtype: 'grid',
			store: onlineListStore
	};
	
	var header = {
		xtype: 'box',
		region: 'north',
		html: '<h1>聊天室</h1>',
		cls: 'header',
		height: 50
	};
	
	var onlineListPanel = {
		region: 'east',
		collapsible: true,
		split: true,
		width: 200,
		layout: 'fit',
		title: '在线列表',
		item: [onlineListGrid]
	};
	
	var messageBox = {
		region: 'center',
		html: 'messagebox'
	};
	
	var sendBox = {
		region: 'south',
		collapsible: true,
		split: true,
		height: 200,
		weight: -100,
		layout: 'fit',
		title: '发送框',
		items: [{
	        xtype: 'htmleditor',
	        region: 'center',
	        fontFamilies: ["微软雅黑", "宋体", "黑体", "Arial", "Courier New", "Tahoma", "Times New Roman", "Verdana"]
	    }],
	    bbar: ['->',{
	    	width: 80,
	    	text: '发送'
	    }]
	};
	
	var viewport = Ext.create('Ext.Viewport', {
		layout: {
			type: 'border'
		},
		defaults: {
			split: false
		},
		items: [
		        header, onlineListPanel, messageBox, sendBox
		]
	});
	
	var updateOnlineList = function(onlineList) {
		if (onlineList && onlineList.length > 0) {
			onlineListStore.loadData(onlineList);
		}
	};
	
	dwr.engine.setActiveReverseAjax(true);
	
	Chat.login("" + Math.random(), function() {});
});
