package edu.duke.ece651.riskclient;

public interface onResultListener{
    void onFailure(String error);
    void onSuccessful();
}