package com.chestor.service.impl;

import com.chestor.dao.AppDocumentDAO;
import com.chestor.dao.AppPhotoDAO;
import com.chestor.entity.AppDocument;
import com.chestor.entity.AppPhoto;
import com.chestor.entity.BinaryContent;
import com.chestor.service.FileService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Log4j
public class FileServiceImpl implements FileService {
    private final AppPhotoDAO appPhotoDAO;
    private final AppDocumentDAO appDocumentDAO;

    public FileServiceImpl(AppPhotoDAO appPhotoDAO, AppDocumentDAO appDocumentDAO) {
        this.appPhotoDAO = appPhotoDAO;
        this.appDocumentDAO = appDocumentDAO;
    }

    @Override
    public AppDocument getDocument(String docId) {
        //todo добавить дешифрование хеш-строки
        var id = Long.parseLong(docId);
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        //todo добавить дешифрование хеш-строки
        var id = Long.parseLong(photoId);
        return appPhotoDAO.findById(id).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            //todo добавить генерацию имени временного файла
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error("Ошибка " + e.getMessage());
            return null;
        }
    }
}
