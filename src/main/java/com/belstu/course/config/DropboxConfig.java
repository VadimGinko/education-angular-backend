package com.belstu.course.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DropboxConfig {

    @Value("${dropbox.access.token}")
    private String dropboxAccessToken;

    @Value("${dropbox.refresh.token}")
    private String dropboxRefreshToken;

    @Value("${dropbox.app.key}")
    private String dropboxAppKey;

    @Value("${dropbox.app.secret}")
    private String dropboxAppSecret;

    @Bean
    public DbxClientV2 DropboxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("course_project").build();

        DbxCredential credentials = new DbxCredential(dropboxAccessToken, -1L, dropboxRefreshToken, dropboxAppKey, dropboxAppSecret);

        return new DbxClientV2(config, credentials);
    }

}
