package com.belstu.course.service;

import com.dropbox.core.DbxException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import video.api.client.ApiVideoClient;
import video.api.client.api.ApiException;
import video.api.client.api.models.Environment;
import video.api.client.api.models.Video;
import video.api.client.api.models.VideoCreationPayload;

import java.io.*;
import java.nio.file.FileSystems;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    final String API_KEY = "4AcfL9kMWaL6eKNXcnr3uicyidzq1gs7T20iQglXrEj";
    final Environment environment = Environment.PRODUCTION;
    private final DropboxService dropboxService;

    public String saveFile(String fileName, String extension, String path, MultipartFile file) throws IOException, ApiException, DbxException {
        extension = "." + extension;
        path = "/" + path;
        saveFileLocally(fileName, extension, file);
        FileInputStream inputStream = new FileInputStream(getPath(fileName, extension));
        String dropBoxName = UUID.randomUUID().toString();
        dropboxService.upload(inputStream, path, dropBoxName + extension);

        removeFileLocally(fileName, extension);
        return dropBoxName;
    }

    public String saveVideo(String fileName, MultipartFile file) throws IOException, ApiException {
        String extension = ".mp4";
        saveFileLocally(fileName, extension, file);

        File myVideoFile = new File(getPath(fileName, extension));

        ApiVideoClient apiVideoClient = new ApiVideoClient(API_KEY, environment);
        Video video = apiVideoClient.videos().create(new VideoCreationPayload().title(fileName));
        video = apiVideoClient.videos().upload(video.getVideoId(), myVideoFile);

        removeFileLocally(fileName, extension);
        return video.getVideoId();
    }

    public void saveFileLocally(String fileName, String extension, MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        BufferedOutputStream stream =
                new BufferedOutputStream(new FileOutputStream(getPath(fileName, extension)));
        stream.write(bytes);
        stream.close();
    }

    public void removeFileLocally(String fileName, String extension) {
        File file = new File(getPath(fileName, extension));
        file.delete();
    }

    public String getPath(String fileName, String extension) {
        return FileSystems.getDefault().getPath("").toAbsolutePath() + "/materials/" + fileName + extension;
    }
}
