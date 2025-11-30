package org.excel_demo.excel_demo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.excel_demo.excel_demo.entity.StudentInfo;

import java.util.List;

/**
 * @author Chen和斌
 */
@Mapper
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {

    List<StudentInfo> searchList(@Param("pageSize") Integer pageSize, @Param("maxId") Long  maxId);

    Long getMaxId(@Param("sheetSize") Integer sheetSize);
}
