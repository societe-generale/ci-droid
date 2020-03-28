package com.societegenerale.cidroid.model;

import lombok.Data;

@Data
public abstract class PushEvent extends SourceControlEvent {

    private String ref;

}
