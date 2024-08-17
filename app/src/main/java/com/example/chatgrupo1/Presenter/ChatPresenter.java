package com.example.chatgrupo1.Presenter;


import com.example.chatgrupo1.Model.UserModel;

// Esta interfaz define las operaciones que puede realizar el presentador del chat
public interface ChatPresenter {

    void loadConversations(UserModel user1, UserModel user2);

    void sendMessage(String messageText, UserModel user1, UserModel user2);
}
