package com.chestor.service;

import com.chestor.entity.AppDocument;
import com.chestor.entity.AppPhoto;
import com.chestor.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);

    AppPhoto processPhoto(Message telegramMessage);

    String generateLink(Long docId, LinkType linkType);
}
