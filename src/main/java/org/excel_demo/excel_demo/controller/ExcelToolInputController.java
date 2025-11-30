package org.excel_demo.excel_demo.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.excel_demo.excel_demo.service.StudentInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Chen和斌
 */
@RestController
@RequestMapping("/excel_input")
public class ExcelToolInputController {

    @Resource
    StudentInfoService studentInfoService;



    /**
     * 单个sheet-单线程导出
     *
     * @return
     */
    @GetMapping("/singleSheet")
    public ResponseEntity<Map<String, Object>> singSheetInput(HttpServletResponse response){
        Map<String, Object> result = new HashMap<>();

        try {
            studentInfoService.inputExcelSingleSheet(response);
            result.put("success", true);
            result.put("message", "文件下载成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 多个sheet-单线程导出
     *
     * @return
     */
    @GetMapping("/moreSheet")
    public ResponseEntity<Map<String, Object>> moreSheetInput(HttpServletResponse response){
        Map<String, Object> result = new HashMap<>();

        try {
            studentInfoService.inputExcelMoreSheet(response);
            result.put("success", true);
            result.put("message", "文件下载成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 多个sheet-多线程导出
     *
     * @return
     */
    @GetMapping("/moreSheetThread")
    public ResponseEntity<Map<String, Object>> moreSheetThreadInputAsync(HttpServletResponse response){
        Map<String, Object> result = new HashMap<>();
        try {
            studentInfoService.inputExcelMoreSheetThread(response);
            result.put("success", true);
            result.put("message", "文件下载成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

}
