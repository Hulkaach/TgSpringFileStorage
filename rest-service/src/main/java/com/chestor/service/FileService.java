package com.chestor.service;

import com.chestor.entity.AppDocument;
import com.chestor.entity.AppPhoto;
import com.chestor.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument(String id);

    AppPhoto getPhoto(String id);

    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
