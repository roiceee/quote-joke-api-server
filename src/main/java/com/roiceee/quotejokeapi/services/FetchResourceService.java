package com.roiceee.quotejokeapi.services;

import com.roiceee.quotejokeapi.exceptions.WrongReqParamTypeException;
import com.roiceee.quotejokeapi.models.Phrase;
import com.roiceee.quotejokeapi.repositories.JokeRepository;
import com.roiceee.quotejokeapi.util.ReqParamTypeValues;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FetchResourceService {
    JokeRepository jokeRepository;

    public FetchResourceService(JokeRepository jokeRepository) {
        this.jokeRepository = jokeRepository;
    }

    public Phrase getRandomPhrase(String type) {
       return switch (type) {
            case ReqParamTypeValues.JOKE -> getRandomJoke();
            //this exception is not intercepted
           default -> throw new WrongReqParamTypeException(type);
        };
    }

    public Phrase getRandomJoke() {
        return jokeRepository.getRandomJoke();
    }


}
