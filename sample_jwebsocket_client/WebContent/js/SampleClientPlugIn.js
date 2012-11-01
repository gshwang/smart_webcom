jws.SampleClientPlugIn = {
	// if namespace is changed update server plug-in accordingly!
	NS: "com.sample.SamplePlugIn",

	//Method is called when a token has to be progressed
	processToken: function( aToken ) {
		console.log("[processToken][ns]["+aToken.ns+"]:["+jws.SampleClientPlugIn.NS+"]");
	    // check if namespace matches
	    if( aToken.ns == jws.SampleClientPlugIn.NS ) {
	      // if it's an answer for the request "getAuthorName"
	      if( aToken.reqType == "getAuthorName" ) {
	    	  alert( "This Tutorial is done by: " + aToken.name );
	      }
	      // if it's an answer for the request "calculate"
	      else if( aToken.reqType == "calculate" ) {
	    	  alert( "calculated Number is: " + aToken.calNumber );
		  }
	      else if( aToken.reqType == "sliderHasChanged" ) {
	    	  console.log("change Slider to "+aToken.value);
	    	  document.getElementById("slider").value=aToken.value;
	      }
	    }
	  },

	  //Method is called from the button "Author"
	  //to send a request to the jwebsocketserver-> LauridsPlugIn
	  requestAuthorName: function( aOptions ) {
	    if( this.isConnected() ) {
	       //create the request token
	      var lToken = {
	         ns: jws.SampleClientPlugIn.NS,
	         type: "getAuthorName"
	      };
	      console.log("asking for Author Name...");
	      this.sendToken( lToken,  aOptions );//send it
	    }
	  },

	  calculateMyNumber: function(inputNumber, aOptions ) {
		    if( this.isConnected() ) {
		       //create the request token
		      var lToken = {
			     ns: jws.SampleClientPlugIn.NS,
			     type: "calculate",
			     myNumber: inputNumber//add the input Number to our token
		      };
		      console.log("sending calculation request for:"+inputNumber);
		      this.sendToken( lToken,  aOptions );//send it
		    }
		},
		
		sliderChanged: function(value, aOptions ) {
		    if( this.isConnected() ) {
		    	//create the request token
		      var lToken = {
			     ns: jws.SampleClientPlugIn.NS,
			     type: "sliderChanged",
			     value: value//add the slider value to the token
		      };
		      console.log("sending slider changed to:"+value);
		      this.sendToken( lToken,  aOptions );//send it
		    }
	}
};
//add the client PlugIn
jws.oop.addPlugIn( jws.jWebSocketTokenClient, jws.SampleClientPlugIn );