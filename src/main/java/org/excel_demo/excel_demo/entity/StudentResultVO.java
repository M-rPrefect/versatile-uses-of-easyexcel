package org.excel_demo.excel_demo.entity;

import lombok.Data;

import java.util.List;

@Data
public class StudentResultVO {

    private Integer index;

    private List<StudentInfo> studentInfoList;
}
