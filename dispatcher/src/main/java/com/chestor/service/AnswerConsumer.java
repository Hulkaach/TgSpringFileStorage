package com.chestor.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AnswerConsumer {
    void comsume(SendMessage sendMessage);
}
