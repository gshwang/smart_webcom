package com.sample;

import java.util.Collection;

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.jwebsocket.api.PluginConfiguration;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.token.BaseToken;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;

public class SamplePlugIn extends TokenPlugIn{
	
	// change th Apache logger to your Classname
	private static Logger mLog = Logging.getLogger(SamplePlugIn.class);
	
	private final static String NS_SAMPLE = "com.sample.SamplePlugIn";
	
	private Collection<WebSocketConnector> mClients;

	public SamplePlugIn(PluginConfiguration aConfiguration) {
		super(aConfiguration);
		if(mLog.isDebugEnabled()){
			mLog.debug("Instantiating Sample Plugin .... ");
		}
		
		this.setNamespace(NS_SAMPLE);
		
		mClients = new FastList<WebSocketConnector>().shared();
	}
	
	@Override
	public void connectorStarted(WebSocketConnector aConnector) {
		mClients.add(aConnector);
		if(mLog.isDebugEnabled())
		{
			mLog.debug("new Client has registered: " + aConnector.getId());
		}
	}
	
	@Override
	public void connectorStopped(WebSocketConnector aConnector,
			CloseReason aCloseReason) {
		mClients.remove(aConnector);
		if(mLog.isDebugEnabled())
		{
			mLog.debug("client " + aConnector.getId() + " is gone ");
		}
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
				lResponse.setString("name", "GSHwang");
				sendToken(aConnector, aConnector, lResponse);
			}
			else if(lType.equals("calculate"))
			{
				int square = aToken.getInteger("myNumber");
				square *= square;
				Token lResponse = createResponse(aToken);
				lResponse.setInteger("calNumber", square);
				sendToken(aConnector, aConnector, lResponse);
			}
			else if (lType.equals("sliderChanged")) {//if the request is "sliderchanged"
				int value= aToken.getInteger("value");//get the Value
				mLog.debug("new Slider Value:"+value);

				//Broadcast the new Value to all other Clients
				Token lToken = TokenFactory.createToken(BaseToken.TT_EVENT);
				lToken.setString("ns", NS_SAMPLE);
				lToken.setString("reqType", "sliderHasChanged");
				lToken.setInteger("value", value);
				broadcast(lToken,aConnector);
			} 

		}
	}

	/**
	 * 
	 * @param aToken
	 */
	public void broadcastToAll(Token aToken)
	{
		for(WebSocketConnector lConnector : mClients)
		{
			getServer().sendToken(lConnector, aToken);
		}
	}
	
	/**
	 * 
	 * @param aToken
	 * @param except
	 */
	public void broadcast(Token aToken, WebSocketConnector except) {
		for (WebSocketConnector lConnector : mClients) {
			if(lConnector!=except){ // remove self
				mLog.debug("sending new Slider Value...");
				getServer().sendToken(lConnector, aToken);
			}
		}
	}
	
}
