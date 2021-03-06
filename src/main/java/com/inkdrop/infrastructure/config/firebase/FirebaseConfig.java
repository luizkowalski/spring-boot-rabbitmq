package com.inkdrop.infrastructure.config.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FirebaseConfig {

  @Autowired
  private ResourceLoader resourceLoader;

  @Bean
  public FirebaseApp firebase() {
    try {
      FirebaseOptions options = new FirebaseOptions.Builder()
          .setServiceAccount(
              resourceLoader.getResource("classpath:credentials.json").getInputStream())
          .setDatabaseUrl("https://chathub-48840.firebaseio.com/")
          .build();
      log.info("Firebase application loaded");
      return FirebaseApp.initializeApp(options);
    } catch (IOException exception) {
      log.error("Error during setting up firebase", exception);
      return null;
    }
  }
}
