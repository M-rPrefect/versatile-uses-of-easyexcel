package org.excel_demo.excel_demo.controller;

import jakarta.annotation.Resource;
import org.excel_demo.excel_demo.service.StudentInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Chen和斌
 */
@RestController
@RequestMapping("/excel_import")
public class ExcelToolImportController {

    @Resource
    StudentInfoService studentInfoService;

    /**
     * 单个sheet-单线程导入
     *
     * @return
     */
    @PostMapping("/singleSheet")
    public ResponseEntity<Map<String, Object>> singSheetImport(@RequestParam("file") MultipartFile file){
        Map<String, Object> result = new HashMap<>();

        // 校验文件
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "请选择要上传的文件");
            return ResponseEntity.badRequest().body(result);
        }

        // 校验文件类型
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            result.put("success", false);
            result.put("message", "仅支持Excel文件(.xlsx)");
            return ResponseEntity.badRequest().body(result);
        }

        try {
            studentInfoService.importExcelSingleSheet(file);
            result.put("success", true);
            result.put("message", "文件上传成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 单个sheet-多线程导入
     *
     * @return
     */
    @PostMapping("/singleSheetThread")
    public ResponseEntity<Map<String, Object>> singSheetThreadImport(@RequestParam("file") MultipartFile file){
        Map<String, Object> result = new HashMap<>();

        try {
            studentInfoService.importExcelSingleThreadSheet(file);
            result.put("success", true);
            result.put("message", "文件上传成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 多个sheet-多线程导入
     *
     * @return
     */
    @PostMapping("/moreSheetThread")
    public ResponseEntity<Map<String, Object>> moreSheetThreadImportAsync(@RequestParam("file") MultipartFile file){
        Map<String, Object> result = new HashMap<>();

        try {
            studentInfoService.importExcelThreadMoreSheet(file);
            result.put("success", true);
            result.put("message", "文件上传成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

}
