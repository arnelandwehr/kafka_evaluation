package controllers;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security.Authenticator;

public class Secured extends Authenticator{

    
    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.login());
    }

	@Override
	public String getUsername(Context arg0) {
		return arg0.session().get("user");
	}
    	
	
	
}
