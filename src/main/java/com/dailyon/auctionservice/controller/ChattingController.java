package com.dailyon.auctionservice.controller;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChattingController {
  private final List<RSocketRequester> CLIENTS = new ArrayList<>();
}
