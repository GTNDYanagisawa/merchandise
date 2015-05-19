tinymce.PluginManager.requireLangPack('hybris');

(function() {
	tinymce.create('tinymce.plugins.Hybris', {
	
		/**
		 * Returns information about the plugin as a name/value array.
		 * The current keys are longname, author, authorurl, infourl and version.
		 *
		 * @return {Object} Name/value array containing information about the plugin.
		 */
		getInfo : function() 
		{
			return {
				longname : 'hybris plugin',
				author : 'mr',
				authorurl : 'http://www.hybris.de',
				version : "1.1"
			};
		},
	
		/**
		 * Initializes the plugin, this will be executed after the plugin has been created.
		 * This call is done before the editor instance has finished it's initialization so use the onInit event
		 * of the editor instance to intercept that event.
		 *
		 * @param {tinymce.Editor} ed Editor instance that the plugin is initialized in.
		 * @param {string} url Absolute URL to where the plugin is located.
		 */
		init : function(ed, url)
		{
			var _pagecontentlink = tinyMCE.activeEditor.getParam("hybris_pagecontentlink", "");
			var _pagecontentlink_enabled = tinyMCE.activeEditor.getParam("hybris_pagecontentlink_enabled", false);
		
			var _anyitemlink = tinyMCE.activeEditor.getParam("hybris_anyitemlink", "");
			var _anyitemlink_enabled = tinyMCE.activeEditor.getParam("hybris_anyitemlink_enabled", false);
		
			var _medialink = tinyMCE.activeEditor.getParam("hybris_medialink", "");
			var _medialink_enabled = tinyMCE.activeEditor.getParam("hybris_medialink_enabled", false);
			
			ed.addCommand('mcepagecontentlink', function() {
				if( _pagecontentlink_enabled )
				{
					var selectedText = tinyMCE.activeEditor.selection.getContent();
					if( selectedText && selectedText.length > 0 )
					{
						var link = "<a href='component://" + _pagecontentlink + "'>" + selectedText + "</a>";						
						tinyMCE.activeEditor.execCommand('mceInsertContent', false, link, false);
						
					}
				}
			});
			
			ed.addCommand('mceanyitemlink', function() {
				if( _anyitemlink_enabled )
				{
					var selectedText = tinyMCE.activeEditor.selection.getContent();
					if( selectedText && selectedText.length > 0 )
					{
						var link = "<a href='item://" + _anyitemlink + "'>" + selectedText + "</a>";
						tinyMCE.activeEditor.execCommand('mceInsertContent', false, link, false);
						
					}
				}
			});
			
			ed.addCommand('mcemedialink', function() {
				if( _medialink_enabled )
				{
					var image = "<img src='" + _medialink + "'/>";
					tinyMCE.activeEditor.execCommand('mceInsertContent', false, image, false);
					
				}
			});
	
			// Register pagecontentlink button
			ed.addButton('pagecontentlink', {
				title : 'hybris.pagecontentlink_title',
				cmd : 'mcepagecontentlink',
				image : url+'/images/pagecontentlink.gif'
			});
			
			// Register anyitemlink button
			ed.addButton('anyitemlink', {
				title : 'hybris.anyitemlink_title',
				cmd : 'mceanyitemlink',
				image : url+'/images/anyitemlink.gif'
			});
			
			// Register medialink button
			ed.addButton('medialink', {
				title : 'hybris.medialink_title',
				cmd : 'mcemedialink',
				image : url+'/images/medialink.gif'
			});
	
			// Add a node change handler, selects the button in the UI when a image is selected
			ed.onNodeChange.add(function(ed, cm, n, co) {
				if( co || !_pagecontentlink_enabled )
				{
					cm.setDisabled('pagecontentlink',true);
				}
				else
				{
					cm.setDisabled('pagecontentlink',false);
				}
			});
			
			ed.onNodeChange.add(function(ed, cm, n, co) {
				if( co || !_anyitemlink_enabled )
				{
					cm.setDisabled('anyitemlink',true);
				}
				else
				{
					cm.setDisabled('anyitemlink',false);
				}
			});
			
			ed.onNodeChange.add(function(ed, cm, n, co) {
				if( !_medialink_enabled )
				{
					cm.setDisabled('medialink',true);
				}
				else
				{
					cm.setDisabled('medialink',false);
				}
			});
		}
	});
	// Adds the plugin class to the list of available TinyMCE plugins
	tinymce.PluginManager.add('hybris', tinymce.plugins.Hybris);
})();
