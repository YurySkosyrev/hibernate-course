package com.dmdev.listener;

import com.dmdev.entity.Chat;
import com.dmdev.entity.UserChat;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;

public class UserChatListener {

    @PostPersist
    public void postPersist(UserChat userChat) {
        Chat chat = userChat.getChat();
        chat.setCount(chat.getCount() + 1);
    }

    @PostRemove
    public void postRemove(UserChat userChat) {
        Chat chat = userChat.getChat();
        chat.setCount(chat.getCount() - 1);
    }

}
