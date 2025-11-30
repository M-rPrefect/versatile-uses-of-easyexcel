# EasyExcel多场景使用

#### 介绍
这是一个基于Spring Boot 3和EasyExcel的开源项目，提供了多种场景下的Excel数据导入导出解决方案。项目支持单线程和多线程处理，能够高效处理大量数据，适用于各种业务场景。

**主要功能特性：**
- ✅ 支持单个Sheet和多个Sheet的导入导出
- ✅ 支持单线程和多线程处理模式
- ✅ 大数据量批量处理，防止内存溢出（OOM）
- ✅ 分页查询和导出，优化性能
- ✅ 可配置的线程池管理
- ✅ 完整的错误处理和日志记录

#### 技术栈

- **框架**: Spring Boot 3.5.4
- **Java版本**: JDK 17
- **Excel处理**: EasyExcel 4.0.3
- **ORM框架**: MyBatis-Plus 3.5.10.1
- **数据库**: MySQL 8.0.33
- **工具库**: Lombok
- **构建工具**: Maven

#### 软件架构

```
excel_demo/
├── config/                    # 配置类
│   ├── ExcelThreadPoolConfig.java      # 线程池配置
│   ├── SingleSheetListener.java        # 单Sheet导入监听器
│   └── MoreSheetThreadListener.java    # 多Sheet导入监听器
├── controller/                 # 控制器层
│   ├── ExcelToolImportController.java  # Excel导入接口
│   └── ExcelToolInputController.java  # Excel导出接口
├── entity/                     # 实体类
│   ├── StudentInfo.java               # 学生信息实体
│   └── StudentResultVO.java           # 结果VO
├── mapper/                     # 数据访问层
│   └── StudentInfoMapper.java
├── service/                    # 服务层
│   ├── StudentInfoService.java
│   └── impl/
│       └── StudentInfoServiceImpl.java
└── resources/
    ├── application.yaml        # 配置文件
    └── mapper/                 # MyBatis映射文件
```

#### 安装教程

1. **环境要求**
   - JDK 17 或更高版本
   - Maven 3.6+
   - MySQL 8.0+

2. **克隆项目**
   ```bash
   git clone [项目地址]
   cd versatile-uses-of-easyexcel
   ```

3. **数据库配置**
   - 创建数据库（如：dream_world）
   - 创建表 `student_info_single`（参考实体类字段）
   - 修改 `src/main/resources/application.yaml` 中的数据库连接信息：
     ```yaml
     spring:
       datasource:
         url: jdbc:mysql://localhost:3306/dream_world?...
         username: root
         password: your_password
     ```

4. **编译运行**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **访问应用**
   - 服务启动后默认端口：8089
   - 可通过 Postman 或浏览器访问接口

#### 使用说明

##### Excel导入接口

**1. 单个Sheet-单线程导入**
- **接口**: `POST /excel_import/singleSheet`
- **参数**: `file` (MultipartFile, 必填)
- **说明**: 适用于小数据量导入，单线程处理
- **示例**:
  ```bash
  curl -X POST http://localhost:8089/excel_import/singleSheet \
    -F "file=@student_info.xlsx"
  ```

**2. 单个Sheet-多线程导入**
- **接口**: `POST /excel_import/singleSheetThread`
- **参数**: `file` (MultipartFile, 必填)
- **说明**: 单Sheet多线程批量处理，适合大数据量导入
- **批量大小**: 60000条/批

**3. 多个Sheet-多线程导入**
- **接口**: `POST /excel_import/moreSheetThread`
- **参数**: `file` (MultipartFile, 必填)
- **说明**: 支持多Sheet并发导入，最多支持20个Sheet
- **适用场景**: Excel文件包含多个Sheet需要同时导入

##### Excel导出接口

**1. 单个Sheet-单线程导出**
- **接口**: `GET /excel_input/singleSheet`
- **说明**: 单Sheet导出，分页查询数据（每页60000条）
- **示例**:
  ```bash
  curl -X GET http://localhost:8089/excel_input/singleSheet \
    -o student_info.xlsx
  ```

**2. 多个Sheet-单线程导出**
- **接口**: `GET /excel_input/moreSheet`
- **说明**: 多Sheet导出，每个Sheet最多60000条数据

**3. 多个Sheet-多线程导出**
- **接口**: `GET /excel_input/moreSheetThread`
- **说明**: 多Sheet多线程并发导出，每个Sheet最多100000条数据
- **适用场景**: 超大数据量导出，提升性能

##### 配置说明

**线程池配置** (`application.yaml`):
```yaml
thread:
  pool:
    core:
      size: 12        # 核心线程数
    max:
      size: 24        # 最大线程数
    queue:
      capacity: 100   # 队列容量
    keep:
      alive:
        seconds: 60   # 线程存活时间
```

**文件上传配置**:
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 200MB      # 最大文件大小
      max-request-size: -1      # 最大请求大小（无限制）
```

##### Excel文件格式要求

导入的Excel文件需要包含以下列（按顺序）：
1. 姓名
2. 年龄
3. 班级
4. 大学
5. 学号
6. 专业
7. 联系电话
8. 成绩

#### 性能优化

- **批量处理**: 导入时使用批量插入，减少数据库交互
- **内存管理**: 分批处理数据，避免一次性加载大量数据到内存
- **多线程**: 利用线程池并发处理，提升处理速度
- **分页查询**: 导出时使用分页查询，避免一次性查询大量数据

#### 注意事项

1. **文件格式**: 仅支持 `.xlsx` 格式的Excel文件
2. **数据量**: 建议单次导入不超过100万条数据
3. **内存**: 大数据量处理时注意JVM内存配置
4. **数据库**: 确保数据库连接池配置合理，避免连接数不足

#### 常见问题

**Q: 导入时出现内存溢出？**
A: 可以调整批量处理大小（BATCH_COUNT）或增加JVM内存配置

**Q: 多Sheet导入时部分Sheet失败？**
A: 检查Sheet数量是否超过20个，或查看日志了解具体错误信息

**Q: 导出文件下载失败？**
A: 检查响应头设置和文件大小限制配置

#### 参与贡献

1.  Fork 本仓库
2.  新建 `Feat_xxx` 分支
3.  提交代码
4.  新建 Pull Request

欢迎提交Issue和Pull Request！

#### 许可证

本项目采用 [LICENSE](LICENSE) 许可证

#### 相关链接

- [EasyExcel官方文档](https://easyexcel.opensource.alibaba.com/)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [MyBatis-Plus官方文档](https://baomidou.com/)
