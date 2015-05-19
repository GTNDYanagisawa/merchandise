<%@page import="javolution.xml.*"%>
<%@page import="javolution.xml.stream.*"%>
<%@page import="javolution.util.*"%>
<%@page import="java.util.*"%>
<%@page import="java.io.*"%>


<%!
	static boolean LOGGING = true;
	static File logFile = new File("/var/log/one/masterserver/masterserver.log");
%>

	
<%	
	if( !LOGGING ) return;
	//request.setCharacterEncoding("UTF-8");
	FileWriter wr = new FileWriter( logFile, true );
	try
	{
		
		//wr.write( "START "+request.getParameter("info").replaceAll(" ","+")+" END" );

		Map p = collectInfos( request );
		wr.write( new Date().toString()+" ");
	
		for( Map.Entry entry : (Set<Map.Entry>)p.entrySet() )
		{
			wr.write( entry.getKey()+"="+entry.getValue()+",");			
			out.write( entry.getKey()+"="+entry.getValue()+",");			
		}
	}
	catch( Exception e )
	{
		wr.write( "error occured: "+getStackTraceAsString( e ) );
		out.write( getStackTraceAsString( e ) );
		wr.write( request.getQueryString() );
	}
	finally
	{
		wr.write( "\n" );
		wr.close();
	}
	
%>

<H2>written.</H2>



<%!
 	static Map collectInfos( HttpServletRequest request ) throws Exception
 	{
		Map result = new HashMap();
		result.put("servername",request.getServerName());
		result.put("serverport",request.getServerPort());
		result.put("remotehost",request.getRemoteHost());
		result.put("remoteport",request.getRemotePort());
		String info = request.getParameter("info");
		if( info==null || info.length()==0 )
		{
			result.put("infofield", "wasempty" );
			result.put("querystring", request.getQueryString() );
		}
		else
		{	
			String dd = "";
			byte[] b = decode( info );
			dd = ""+b.length;
			dd = new String( b, "UTF-8" );	
			//if(true)throw new RuntimeException( dd );
		try
		{
			XMLBinding binding = new XMLBinding();
		    	binding.setAlias(FastMap.class, "Map");
		    	InputStream in = new ByteArrayInputStream(decode( request.getParameter("info").replaceAll(" ","+") ));
		    	XMLObjectReader reader = XMLObjectReader.newInstance(in).setBinding(binding);
		    	FastMap map = reader.read("PlatformInfo", FastMap.class); 
		    	reader.close();	
			for( Iterator it = map.keySet().iterator(); it.hasNext(); )
			{
				String key = (String)it.next();
				result.put( key, map.get(key) );
			}
		}		
		catch(Exception e )
		{
                    throw e;
		}
		}
		return result;
	}



	public static final String getStackTraceAsString( final Throwable throwable )
	{
		final StringWriter stackWriter = new StringWriter();
		final PrintWriter stackPrinter = new PrintWriter( stackWriter, true );
		throwable.printStackTrace( stackPrinter );
		stackPrinter.flush();
		stackPrinter.close();
		return stackWriter.getBuffer().toString();
	}


%>


<%!

  public static final String encode(byte[] raw) 
  {
    StringBuilder encoded = new StringBuilder();
    encode(raw, encoded);
    return encoded.toString();
  }
  public static void encode(byte[] raw, StringBuilder target)
  {
    for (int i = 0; i < raw.length; i += 3) 
    {
      target.append(encodeBlock(raw, i) );
    }
  }

  private static final char[] encodeBlock(byte[] raw, int offset) 
  {
    int block = 0;
    int slack = raw.length - offset - 1;
    int end = (slack >= 2) ? 2 : slack;
    for (int i = 0; i <= end; i++) 
    {
      byte b = raw[offset + i];
      int neuter = (b < 0) ? b + 256 : b;
      block += neuter << (8 * (2 - i));
    }
    char[] base64 = new char[4];
    for (int i = 0; i < 4; i++) 
    {
      int sixbit = (block >>> (6 * (3 - i))) & 0x3f;
      base64[i] = getChar(sixbit);
    }
    if (slack < 1) base64[2] = '=';
    if (slack < 2) base64[3] = '=';
    return base64;
  }

  private static final char getChar(int sixBit) 
  {
    if (sixBit >= 0 && sixBit <= 25)
      return (char)('A' + sixBit);
    if (sixBit >= 26 && sixBit <= 51)
      return (char)('a' + (sixBit - 26));
    if (sixBit >= 52 && sixBit <= 61)
      return (char)('0' + (sixBit - 52));
    if (sixBit == 62) return '+';
    if (sixBit == 63) return '/';
    return '?';
  }

  public static final byte[] decode(String base64) 
  {
    int pad = 0;
    int s = base64.length();
    for (int i = s - 1; base64.charAt(i) == '='; i--)
      pad++;
    int length = base64.length() * 6 / 8 - pad;
    byte[] raw = new byte[length];
    int rawIndex = 0;
    for (int i = 0; i < s ; i += 4) 
    {
      // hot fix for malformed strings (where length is not correct)
      int block = 0;
      for( int j = 0 ; j < 4 && i + j < s ; j++ )
      {
         block += getValue(base64.charAt(i+j)) << ( 18 - ( j * 6 ));
      }
//      int block = (getValue(base64.charAt(i)) << 18)
//          + (getValue(base64.charAt(i + 1)) << 12)
//          + (getValue(base64.charAt(i + 2)) << 6)
//          + (getValue(base64.charAt(i + 3)));
      for (int j = 0; j < 3 && rawIndex + j < raw.length; j++)
        raw[rawIndex + j] = (byte)((block >> (8 * (2 - j))) & 0xff);
      rawIndex += 3;
    }
    return raw;
  }

  private static final int getValue(char c) 
  {
    if (c >= 'A' && c <= 'Z') return c - 'A';
    if (c >= 'a' && c <= 'z') return c - 'a' + 26;
    if (c >= '0' && c <= '9') return c - '0' + 52;
    if (c == '+') return 62;
    if (c == '/') return 63;
    if (c == '=') return 0;
    return -1;
  }  

%>


<%!

	
	public static String getInfoAddress( HttpServletRequest req )
	{
		StringBuilder b = new StringBuilder();
		//b.append( "http://localhost:19001/hmc/administration/getinfo.jsp" );
		b.append( "http://www.hybris.de/getinfo.jsp" );
		b.append( "?info="+getBase64InfoMap() );
		if( req!=null )
		{
			b.append( "&server="+req.getServerName() );
		}
		return b.toString();
	}
	
	public static String getBase64InfoMap()
	{
		try
		{
		   XMLBinding binding = new XMLBinding();
		   binding.setAlias(FastMap.class, "Map");
		   ByteArrayOutputStream out = new ByteArrayOutputStream();
		   XMLObjectWriter writer = new XMLObjectWriter().setOutput(out).setBinding(binding);
		   writer.write(getInfoMap(), "PlatformInfo");
		   writer.close();
		   return encode( out.toByteArray() );
		}
		catch( XMLStreamException e)
		{
			return "unabletowrite="+e.getMessage();
		}
	}
	
	public static Map getInfoMap()
	{
	     Map map = new FastMap();
	     map.put("availableprocessors", Runtime.getRuntime().availableProcessors() );
	     return map;
	}	
	
	
	public static void main( String args[] )
	{
		System.out.println(getInfoMap());
		System.out.println(getInfoAddress( null ));
	}
%>
