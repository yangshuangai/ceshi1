package com.novel.boot.param;

import com.pcdd.sonovel.model.SearchResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
//@AllArgsConstructor
public class SearchResult_  {
    private Integer sourceId;
    private String url;
    private String bookName;
    private String author;
    private String intro;
    private String category;
    private String latestChapter;
    private String lastUpdateTime;
    private String status;
    private String wordCount;
}
