<%@include file="../head.inc"%>


<!--
 sample for a jsp which opens a creator of a given type
 put the following line into the <explorertree> area in 
 your hmc.tmpl of your extension
 
    <explorertree>
   	<staticcontent name="create product" uri="sample/create.jsp" />
	</explorertree>
-->



<%

	ComposedType type = TypeManager.getInstance().getComposedType("Product");		// choose your favorite ComposedType here
	
	// create new creator window for the given ComposedType
	
	EditorNode node = StructureLoader.getEditorNode(type);
	GenericItemCreatorChip creator = node.createGenericItemCreatorChip(theDisplayState, type);
	creator.showEditor();			
	
	// set default (empty) content to prevent infinite loop
	// ????
	Chip content = ExplorerChip.getCurrentExplorerChip(theDisplayState).getContent();
	ExplorerChip.getCurrentExplorerChip(theDisplayState).setContent(content);

%>
	<script language = "JavaScript1.2">
		setScrollAndSubmit();
	</script>
