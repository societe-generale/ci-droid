package com.societegenerale.cidroid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix="repositories-to-process")
public class CiDroidProperties {

    private List<String> excluded;

    private List<String> included;

    @PostConstruct
    public void validate() {

        if(excluded==null){
            excluded=new ArrayList<>();
        }

        if(included==null){
            included=new ArrayList<>();
        }

        if(!excluded.isEmpty() && !included.isEmpty()){
            throw new IllegalStateException("You can't define both included AND excluded repositories - please configure only one type");
        }

    }
}
