package com.belstu.course.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DropboxService {

    private final DbxClientV2 dropboxClient;

    public void upload(InputStream inputStream, String path, String filename) throws IOException, DbxException {
        FileMetadata metadata = dropboxClient.files().uploadBuilder(path + "/" + filename).uploadAndFinish(inputStream);

        log.info("File uploaded in dropbox in path {} and name {} with id {}", path, filename, metadata.getId());
    }

    public void delete(String path) {
        try {
            ListFolderResult result = dropboxClient.files().listFolder(path);
            for (Metadata metadata : result.getEntries()) {
                dropboxClient.files().deleteV2(metadata.getPathLower());
                log.info("File deleted in dropbox in path {}", metadata.getPathLower());
            }
        } catch (DbxException e) {
            log.warn("File not deleted in dropbox in path {}", path);
            log.debug("File not deleted in dropbox in path {}", path, e);
        }
    }

    public ByteArrayOutputStream getFile(String path) throws DbxException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        dropboxClient.files().download(path).download(outputStream);

        return outputStream;
    }

}
