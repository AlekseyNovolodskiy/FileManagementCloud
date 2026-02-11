package learn.Cloud.service;


import learn.Cloud.model.responce.AuthenticationRequest;
import learn.Cloud.model.responce.AuthenticationResponce;
import learn.Cloud.model.responce.RegisterRequest;

public interface AuthService {
    AuthenticationResponce registrationNewUser (RegisterRequest request);
    String authUser(AuthenticationRequest request);

}