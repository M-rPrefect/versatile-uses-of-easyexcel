package org.excel_demo.excel_demo.service;


import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.excel_demo.excel_demo.entity.StudentInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StudentInfoService extends IService<StudentInfo> {
    // 导入
    void importExcelSingleSheet(MultipartFile file) throws IOException;

    void importExcelSingleThreadSheet(MultipartFile file) throws IOException;

    void importExcelThreadMoreSheet(MultipartFile file) throws IOException;

    // 导出
    void inputExcelSingleSheet(HttpServletResponse response) throws IOException;

    void inputExcelMoreSheet(HttpServletResponse response) throws IOException;

    void inputExcelMoreSheetThread(HttpServletResponse response) throws IOException;

}
