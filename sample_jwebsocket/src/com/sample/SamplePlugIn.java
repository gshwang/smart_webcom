package com.sample;

import org.apache.log4j.Logger;
import org.jwebsocket.api.PluginConfiguration;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.token.Token;

public class SamplePlugIn extends TokenPlugIn{
	
	// change th Apache logger to your Classname
	private static Logger mLog = Logging.getLogger(SamplePlugIn.class);
	
	private final static String NS_SAMPLE = "com.sample.SamplePlugIn";

	public SamplePlugIn(PluginConfiguration aConfiguration) {
		super(aConfiguration);
		if(mLog.isDebugEnabled()){
			mLog.debug("Instantiating Sample Plugin .... ");
		}
		
		this.setNamespace(NS_SAMPLE);
	}
	 
	@Override
	public void processToken(PlugInResponse aResponse,
			WebSocketConnector aConnector, Token aToken) {
		// get the type of the token
		// the type can be associated with a 'command'
		String lType = aToken.getType();
		
		// get the namesapce of the token
		// each plug-in should have its own unique namespace
		String lNS = aToken.getNS();
		
		// check if token has a type and a matching namespace
		if(lType != null && lNS != null && lNS.equals(getNamespace()))
		{
			if(lType.equals("getAuthorName"))
			{
				mLog.debug("Authorname was requested");
				Token lResponse = createResponse(aToken);
				lResponse.setString("name", "Sample");
				sendToken(aConnector, aConnector, aToken);
			}
			else if(lType.equals("myNumber"))
			{
				int square = aToken.getInteger("myNumber");
				square *= square;
				Token lResponse = createResponse(aToken);
				lResponse.setInteger("calNumber", square);
				sendToken(aConnector, aConnector, aToken);
			}
		}
	}

}
