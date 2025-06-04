package com.novel.boot.controller;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.novel.boot.param.SearchResult_;
import com.pcdd.sonovel.action.AggregatedSearchAction;
import com.pcdd.sonovel.core.Crawler;
import com.pcdd.sonovel.model.AppConfig;
import com.pcdd.sonovel.model.SearchResult;
import com.pcdd.sonovel.parse.TocParser;
import com.pcdd.sonovel.util.ConfigUtils;
import lombok.SneakyThrows;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author yukai
 * @date 2025/4/30
 */
@RequestMapping("/api/novel")
@RestController
public class NovelController {
    private static final Map<String, List<SearchResult>> resultCache = ExpiringMap.builder()
            .maxSize(20)
            .expiration(60*5, TimeUnit.SECONDS)
            .build();

    @GetMapping("/search")
    public List<SearchResult> search(@RequestParam String q) {
        if (resultCache.containsKey(q)) {
            return resultCache.get(q);
        }
        List<SearchResult> results = AggregatedSearchAction.getSearchResults(q);
        resultCache.put(q, results);
        return results;
    }

    @PostMapping("/download")
    @SneakyThrows
    public ResponseEntity<FileSystemResource> download(@RequestBody SearchResult_ result) {
        var config = ConfigUtils.config();
        var tocParser = new TocParser(config);
        config.setSourceId(result.getSourceId());
        var toc = tocParser.parse(result.getUrl(), 1, Integer.MAX_VALUE);
        SearchResult build = SearchResult.builder().build();
        BeanUtil.copyProperties(result, build);
        try {
            var res = new Crawler(config).crawl(build, toc);
            return tryDownload(config, build);
        } catch (Exception e) {
            return tryDownload(config, build);
        }
    }

    public ResponseEntity<FileSystemResource> tryDownload(AppConfig config, SearchResult result) {
        HttpHeaders headers = new HttpHeaders();
        Path path = Paths.get(config.getDownloadPath(), StrUtil.format("{}({}).{}", result.getBookName(),
                result.getAuthor(), config.getExtName()));
        File file = path.toFile();
        String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedFileName);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        if (file.exists()) {
            return ResponseEntity.ok().headers(headers).body(new FileSystemResource(file));
        } else return ResponseEntity.internalServerError().build();
    }
}