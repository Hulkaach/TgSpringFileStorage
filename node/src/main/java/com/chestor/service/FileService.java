package com.chestor.service;

import com.chestor.entity.AppDocument;
import com.chestor.entity.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);

    AppPhoto processPhoto(Message telegramMessage);
}
