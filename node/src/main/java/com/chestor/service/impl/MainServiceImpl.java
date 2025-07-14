package com.chestor.service.impl;

import com.chestor.dao.AppUserDAO;
import com.chestor.dao.RawDataDAO;
import com.chestor.entity.AppUser;
import com.chestor.entity.RawData;
import com.chestor.entity.enums.UserState;
import com.chestor.service.MainService;
import com.chestor.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.chestor.entity.enums.UserState.BASIC_STATE;
import static com.chestor.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static com.chestor.service.enums.ServiceCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getUserState();
        var text = update.getMessage().getText();
        var output = "";

        if (CANCEL.equals(text)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //todo добавить обработку email
        } else {
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова";
        }

        var chatId = update.getMessage().getChatId();

        sendAnswer(chatId, output);


    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }
        //todo добавить сохранение документа
        var answer = "Документ успешно загружен! Ссылка для скачивания: http//test.ru/DOC";
        sendAnswer(chatId, answer);

    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }
        //todo добавить сохранение фото
        var answer = "Фото успешно загружено! Ссылка для скачивания: http//test.ru/PHOTO";
        sendAnswer(chatId, answer);

    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getUserState();
        if (!appUser.getIsActive()) {
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузкт контента";
            sendAnswer(chatId,error);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов";
            sendAnswer(chatId,error);
            return true;
        }
        return false;
    }

    private void sendAnswer(Long chatId, String output) {
        var sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(output)
                .build();
        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        if (REGISTRATION.equals(cmd)) {
            //todo Добавить регистрацию
            return "Временно недоступно";
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "Добро пожаловать! Чтобы посмотреть список дуступных комманд введите /help";
        } else {
            return "Неизвестная комманда. Чтобы посмотреть список дуступных комманд введите /help";
        }
    }

    private String help() {
        return "Список доступных комманд:\n" +
                "/cancel - отмена выполнения текущей команды\n" +
                "/registration - регистрация пользователя";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //todo изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .userState(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }

        return persistentAppUser;

    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
