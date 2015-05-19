tinymce.PluginManager.requireLangPack('charcount');

(function(){
	tinymce.create('tinymce.plugins.CharCount', {
		getInfo : function() {
			return {
				longname : 'Count Words/Characters',
				author : 'Ryan Demmer',
				authorurl : 'http://www.cellardoor.za.net/mosce/',
				infourl : '',
				version : '1.0'
			};
		},
		init : function(ed, url)
		{
			ed.addButton('charcount', {
				title : 'charcount.desc',
				cmd : 'mceCount',
				image : url+'/images/charcount.gif'
			});
			
			ed.addCommand('mceCount', function() {
				var content = tinyMCE.activeEditor.getContent();
                var words = _countWords( content );
                var chars = _countChars( content, false );
                var chars_spaces = _countChars( content, true );
                
                var count_text = ed.getLang('charcount.desc', '', true)+'\n'+'\n';
                    count_text += ed.getLang('charcount.words', '', true)+': '+words+'\n';
                    count_text += ed.getLang('charcount.chars', '', true)+': '+chars+'\n';
                    count_text += ed.getLang('charcount.chars_spaces', '', true)+': '+chars_spaces;
                alert( count_text );
			});
		}
	});
	tinymce.PluginManager.add('charcount', tinymce.plugins.CharCount);
})()
		
function _clean( content )
{
    content = content.replace(/<(.+?)>/g, '');//remove html
    content = content.replace('&nbsp;', ' ', 'g');//replace &nbsp; with space
    content = content.replace(/&(.*);/g, '1');//convert entities to single character
    return content;
}
function _countWords( content )
{
    content = _clean( content );
    var arr = content.split(' ');
    
    var total = 0;
    for(var i=0; i<arr.length; i++)
    {
        if( arr[i].match(/\w/g)){
            total ++;
        }
    }
    return total;
}
function _countChars( content, spaces )
{
    content = _clean( content );

    var total = 0;
    if(!spaces)
    {
        content = content.replace( ' ', '', 'g' );
    }
    total = content.length;

    return total;
}

