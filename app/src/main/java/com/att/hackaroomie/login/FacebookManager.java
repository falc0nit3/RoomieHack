package com.att.hackaroomie.login;

import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import com.att.hackaroomie.model.User;

import java.util.List;

public class FacebookManager {

    private void makeMeRequest(final Session session, final User toUser) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {

                                // Set the id for the ProfilePictureView
                                // view that in turn displays the profile picture.
                                toUser.setFacebookUser(user);

                                toUser.setId(user.getId());
                                toUser.setDisplayName(user.getName());
                                toUser.setBirthDate(user.getBirthday());

                                List<String> permissions = session.getPermissions();

                                //buildUserInfoDisplay(user);

                                //setupUserBasicDetails();
                                //fetchUserCoverPic();

                                // Testing graphobject
                                Bundle params = new Bundle();
                                params.putString("fields", "cover");

                                String requestId = "154727011315956";

                                requestId = toUser.getFacebookUser().getId();

                                Request newReq = new Request(session, requestId, params, HttpMethod.GET, new Request.Callback() {
                                    public void onCompleted(Response response) {
                                        GraphObject graphObject = response.getGraphObject();
                                        FacebookRequestError error = response.getError();
                                        if(error!=null){
                                            Log.e("Error", error.getErrorMessage());
                                        }

                                    }
                                });

                                //Request.executeAndWait(newReq);
                                newReq.executeAsync();

                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                        }
                    }
                });
        request.executeAsync();


    }
}
