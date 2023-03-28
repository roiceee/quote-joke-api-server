package com.roiceee.phraseapi.resourceapi.controllers;

import com.roiceee.phraseapi.apikeymanagement.services.ApiKeyService;
import com.roiceee.phraseapi.resourceapi.models.Phrase;
import com.roiceee.phraseapi.resourceapi.services.FetchResourceService;
import com.roiceee.phraseapi.resourceapi.services.RequestCountService;
import com.roiceee.phraseapi.resourceapi.util.Params;
import com.roiceee.phraseapi.resourceapi.services.ResourceControllerLimiterService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/phrase")
public class RequestResourceController {
    FetchResourceService fetchResourceService;
    ApiKeyService apiKeyService;
    RequestCountService requestCountService;

    ResourceControllerLimiterService resourceControllerLimiterService;

    public RequestResourceController(FetchResourceService fetchResourceService, ApiKeyService apiKeyService,
                                     RequestCountService requestCountService,
                                     ResourceControllerLimiterService resourceControllerLimiterService) {
        this.fetchResourceService = fetchResourceService;
        this.apiKeyService = apiKeyService;
        this.requestCountService = requestCountService;
        this.resourceControllerLimiterService = resourceControllerLimiterService;

    }

    @GetMapping(params = {Params.appID, Params.TYPE})
    public ResponseEntity<Phrase> getRandomResource(
            @RequestParam(value = Params.appID) String appid,
            @RequestParam(value = Params.TYPE) String type
    ) {
        this.beforeAction(appid);
        Phrase phrase = fetchResourceService.getRandomPhrase(type);
        this.afterAction(appid);
        return ResponseEntity
                .ok()
                .body(phrase);
    }

    @GetMapping(params = {Params.appID, Params.TYPE, Params.QTY})
    public ResponseEntity<List<? extends Phrase>> getRandomResourceList(
            @RequestParam(value = Params.appID) String appid,
            @RequestParam(value = Params.TYPE) String type,
            @RequestParam(value = Params.QTY) int qty
    ) {
        this.beforeAction(appid);
        List<? extends Phrase> phraseList = fetchResourceService.getRandomPhraseList(type, qty);
        this.afterAction(appid);
        return ResponseEntity
                .ok()
                .body(phraseList);
    }

    @GetMapping(params = {Params.appID, Params.TYPE, Params.QTY, Params.QUERY})
    public ResponseEntity<List<? extends Phrase>> getResourcesWithPagination(
            @RequestParam(value = Params.appID) String appid,
            @RequestParam(value = Params.TYPE) String type,
            @RequestParam(value = Params.QTY) int qty,
            @RequestParam(value = Params.QUERY) String query
    ) {
        this.beforeAction(appid);
        List<? extends Phrase> phraseList = fetchResourceService.getRandomPhraseListWithQuery(type, qty, query);
        this.afterAction(appid);
        return ResponseEntity
                .ok()
                .body(phraseList);
    }

    @GetMapping(params = {Params.appID, Params.TYPE, Params.PAGE, Params.QTY, Params.QUERY})
    public ResponseEntity<Page<? extends Phrase>> getResourcesByKeywordWithPagination(
            @RequestParam(value = Params.appID) String appid,
            @RequestParam(value = Params.TYPE) String type,
            @RequestParam(value = Params.PAGE) int page,
            @RequestParam(value = Params.QTY) int qty,
            @RequestParam(value = Params.QUERY) String query
    ) {
        this.beforeAction(appid);
        Page<? extends Phrase> phrases = fetchResourceService.getPhraseListWithQueryPagination(type, query, page,
                qty);
        this.afterAction(appid);
        return ResponseEntity
                .ok()
                .body(phrases);
    }

    private void beforeAction(String appid) {
        apiKeyService.checkIfApiKeyExists(appid);
        resourceControllerLimiterService.addExistingKeyIfAbsentInCache(appid);
        resourceControllerLimiterService.consumeOne(appid);
    }

    private void afterAction(String appid) {
        requestCountService.addCount(appid);
    }
}
