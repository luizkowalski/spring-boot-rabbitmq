package com.inkdrop.application.services;

import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MixpanelAPIService {

  public void sendEvent(JSONObject event) {
    try {
      ClientDelivery delivery = new ClientDelivery();
      delivery.addMessage(event);

      new MixpanelAPI().deliver(delivery);
    } catch (Exception e) {
      log.error("Error while delivering message to Mixpanel", e.getLocalizedMessage());
    }
  }
}
