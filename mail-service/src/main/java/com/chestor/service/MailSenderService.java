package com.chestor.service;

import com.chestor.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
