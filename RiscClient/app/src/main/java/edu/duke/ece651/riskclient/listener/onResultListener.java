package edu.duke.ece651.riskclient.listener;

public interface onResultListener{
    void onFailure(String error);
    void onSuccessful();
}