package com.admin.controller;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.common.result.Result;
import com.admin.entity.GenTable;
import com.admin.service.GenTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/devtools/gen")
@RequiredArgsConstructor
public class GenTableController {

    private final GenTableService genTableService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('devtools:gen:list')")
    public Result<PageResult<GenTable>> list(PageParam pageParam,
                                              @RequestParam(required = false) String tableName,
                                              @RequestParam(required = false) String tableComment) {
        return Result.success(genTableService.selectGenTableList(pageParam, tableName, tableComment));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('devtools:gen:query')")
    public Result<GenTable> getInfo(@PathVariable Long id) {
        return Result.success(genTableService.selectGenTableById(id));
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('devtools:gen:import')")
    public Result<Void> importTable(@RequestBody List<String> tableNames) {
        genTableService.importTable(tableNames);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('devtools:gen:edit')")
    public Result<Void> edit(@RequestBody GenTable genTable) {
        genTableService.updateGenTable(genTable);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('devtools:gen:remove')")
    public Result<Void> remove(@PathVariable Long id) {
        genTableService.deleteGenTable(id);
        return Result.success();
    }

    @GetMapping("/preview/{id}")
    @PreAuthorize("hasAuthority('devtools:gen:preview')")
    public Result<Map<String, String>> preview(@PathVariable Long id) {
        return Result.success(genTableService.previewCode(id));
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasAuthority('devtools:gen:download')")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        byte[] data = genTableService.generateCode(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=code.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @PostMapping("/batchDownload")
    @PreAuthorize("hasAuthority('devtools:gen:download')")
    public ResponseEntity<byte[]> batchDownload(@RequestBody Long[] tableIds) {
        byte[] data = genTableService.batchGenerateCode(tableIds);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=code.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
