package com.lbu.lbulibrary.service;

public interface BookFineService {
    void checkForBookFines(String token, String authUserHref);
}
