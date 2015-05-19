
<%@page import="java.util.*,
						de.hybris.platform.core.*,
						de.hybris.platform.jalo.security.*,
						de.hybris.platform.jalo.*,
						de.hybris.platform.util.*,
						de.hybris.platform.jalo.media.*,
						de.hybris.platform.jalo.type.*,
						de.hybris.platform.util.Config,
						de.hybris.platform.hmc.webchips.*,
						de.hybris.platform.hmc.webchips.event.*,
						de.hybris.platform.hmc.*,
						de.hybris.platform.hmc.administration.*,
						de.hybris.platform.hmc.attribute.*,
						de.hybris.platform.hmc.enumeration.*,
						de.hybris.platform.hmc.generic.*,
						de.hybris.platform.hmc.generic.organizer.*,
						de.hybris.platform.hmc.media.*,
						de.hybris.platform.hmc.security.*,
						java.io.*,
						org.apache.log4j.*,
						de.hybris.platform.licence.Licence"
%>


<!-- TEST PAGE FOR SESSION SERIALIZATION AND FAILOVER -->


<%
	JaloSession jaloSession = WebSessionFunctions.getSession( request );
	DisplayState state = (DisplayState)session.getAttribute("state");
	
	//out.println( session.getId() );


	if( request.getParameter("save")!=null )
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos = new ObjectOutputStream(baos); 
		oos.writeObject( jaloSession ); 
		byte[] buf = baos.toByteArray(); 
		session.setAttribute( "savedsession", buf );
		
		out.println("saved JaloSession [bytes]: "+buf.length+"<p>");
		
		baos = new ByteArrayOutputStream(); 
		oos = new ObjectOutputStream(baos); 
		oos.writeObject( state ); 
		buf = baos.toByteArray(); 
		session.setAttribute( "savedstate", buf );
		
		out.println("saved DisplayState [bytes]: "+buf.length+"<p>");
	}
	
	if( request.getParameter("restore")!=null )
	{

		ByteArrayInputStream bi = new ByteArrayInputStream( (byte[])session.getAttribute("savedsession") );
		ObjectInputStream ois = new ObjectInputStream(bi);
		JaloSession sess = (JaloSession)ois.readObject();
		session.setAttribute("jalosession", sess );
		sess.activate();
		
		bi = new ByteArrayInputStream( (byte[])session.getAttribute("savedstate") );
		ois = new ObjectInputStream(bi);
		DisplayState ds = (DisplayState)ois.readObject();
		session.setAttribute("state", ds );
		
		out.println("restored JaloSession and displaystate");	
	}

	
%>
<a href="st.jsp?save=true">save current session</a><p>
<a href="st.jsp?restore=true">Restore current session</a><p>

	



