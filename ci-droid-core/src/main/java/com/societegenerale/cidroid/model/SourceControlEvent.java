package com.societegenerale.cidroid.model;

import lombok.Data;
import org.springframework.http.HttpEntity;

@Data
public abstract class SourceControlEvent {

    HttpEntity<String> rawMessage;

    Repository repository;

}
