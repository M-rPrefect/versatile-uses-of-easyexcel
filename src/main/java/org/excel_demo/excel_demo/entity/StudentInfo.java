package org.excel_demo.excel_demo.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * student_info
 * @author Chen和斌
 */
@Getter
@Setter
@EqualsAndHashCode
@TableName("student_info_single")
public class StudentInfo implements Serializable {

    @ExcelIgnore
    @TableId
    private Long id;

    @ExcelProperty(value = "姓名", index = 0)
    @TableField("stu_name")
    private String stuName;

    @ExcelProperty(value = "年龄", index = 1)
    @TableField("stu_age")
    private String stuAge;

    @ExcelProperty(value = "班级", index = 2)
    @TableField("stu_class")
    private String stuClass;

    @ExcelProperty(value = "大学", index = 3)
    @TableField("stu_university")
    private String stuUniversity;

    @ExcelProperty(value = "学号", index = 4)
    @TableField("stu_number")
    private String stuNumber;

    @ExcelProperty(value = "专业", index = 5)
    @TableField("stu_major")
    private String stuMajor;

    @ExcelProperty(value = "联系电话", index = 6)
    @TableField("stu_phone")
    private String stuPhone;

    @ExcelProperty(value = "成绩", index = 7)
    @TableField("stu_score")
    private String stuScore;


}
