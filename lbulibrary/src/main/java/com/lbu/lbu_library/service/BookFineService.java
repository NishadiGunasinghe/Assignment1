package com.lbu.lbu_library.service;

public interface BookFineService {
    void checkForBookFines(String token, String authUserHref);
}
