# Versatile Uses of EasyExcel

#### Description
This is an open-source project based on Spring Boot 3 and EasyExcel, providing Excel data import and export solutions for various scenarios. The project supports both single-threaded and multi-threaded processing, capable of efficiently handling large amounts of data, suitable for various business scenarios.

**Key Features:**
- ✅ Support for single and multiple Sheet import/export
- ✅ Support for single-threaded and multi-threaded processing modes
- ✅ Batch processing of large data volumes to prevent memory overflow (OOM)
- ✅ Paginated query and export for performance optimization
- ✅ Configurable thread pool management
- ✅ Complete error handling and logging

#### Technology Stack

- **Framework**: Spring Boot 3.5.4
- **Java Version**: JDK 17
- **Excel Processing**: EasyExcel 4.0.3
- **ORM Framework**: MyBatis-Plus 3.5.10.1
- **Database**: MySQL 8.0.33
- **Utility Library**: Lombok
- **Build Tool**: Maven

#### Software Architecture

```
excel_demo/
├── config/                    # Configuration classes
│   ├── ExcelThreadPoolConfig.java      # Thread pool configuration
│   ├── SingleSheetListener.java        # Single sheet import listener
│   └── MoreSheetThreadListener.java    # Multiple sheet import listener
├── controller/                 # Controller layer
│   ├── ExcelToolImportController.java  # Excel import APIs
│   └── ExcelToolInputController.java   # Excel export APIs
├── entity/                     # Entity classes
│   ├── StudentInfo.java               # Student info entity
│   └── StudentResultVO.java           # Result VO
├── mapper/                     # Data access layer
│   └── StudentInfoMapper.java
├── service/                    # Service layer
│   ├── StudentInfoService.java
│   └── impl/
│       └── StudentInfoServiceImpl.java
└── resources/
    ├── application.yaml        # Configuration file
    └── mapper/                 # MyBatis mapping files
```

#### Installation

1. **Requirements**
   - JDK 17 or higher
   - Maven 3.6+
   - MySQL 8.0+

2. **Clone the project**
   ```bash
   git clone [project-url]
   cd versatile-uses-of-easyexcel
   ```

3. **Database Configuration**
   - Create database (e.g., dream_world)
   - Create table `student_info_single` (refer to entity class fields)
   - Modify database connection information in `src/main/resources/application.yaml`:
     ```yaml
     spring:
       datasource:
         url: jdbc:mysql://localhost:3306/dream_world?...
         username: root
         password: your_password
     ```

4. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access the Application**
   - Default port: 8089
   - Access APIs via Postman or browser

#### Instructions

##### Excel Import APIs

**1. Single Sheet - Single Thread Import**
- **API**: `POST /excel_import/singleSheet`
- **Parameters**: `file` (MultipartFile, required)
- **Description**: Suitable for small data volume import, single-threaded processing
- **Example**:
  ```bash
  curl -X POST http://localhost:8089/excel_import/singleSheet \
    -F "file=@student_info.xlsx"
  ```

**2. Single Sheet - Multi-threaded Import**
- **API**: `POST /excel_import/singleSheetThread`
- **Parameters**: `file` (MultipartFile, required)
- **Description**: Single sheet multi-threaded batch processing, suitable for large data volume import
- **Batch Size**: 60000 records per batch

**3. Multiple Sheets - Multi-threaded Import**
- **API**: `POST /excel_import/moreSheetThread`
- **Parameters**: `file` (MultipartFile, required)
- **Description**: Support concurrent import of multiple sheets, up to 20 sheets
- **Use Case**: Excel files containing multiple sheets that need to be imported simultaneously

##### Excel Export APIs

**1. Single Sheet - Single Thread Export**
- **API**: `GET /excel_input/singleSheet`
- **Description**: Single sheet export with paginated query (60000 records per page)
- **Example**:
  ```bash
  curl -X GET http://localhost:8089/excel_input/singleSheet \
    -o student_info.xlsx
  ```

**2. Multiple Sheets - Single Thread Export**
- **API**: `GET /excel_input/moreSheet`
- **Description**: Multiple sheets export, up to 60000 records per sheet

**3. Multiple Sheets - Multi-threaded Export**
- **API**: `GET /excel_input/moreSheetThread`
- **Description**: Multiple sheets multi-threaded concurrent export, up to 100000 records per sheet
- **Use Case**: Ultra-large data volume export to improve performance

##### Configuration

**Thread Pool Configuration** (`application.yaml`):
```yaml
thread:
  pool:
    core:
      size: 12        # Core thread count
    max:
      size: 24        # Maximum thread count
    queue:
      capacity: 100   # Queue capacity
    keep:
      alive:
        seconds: 60   # Thread keep-alive time
```

**File Upload Configuration**:
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 200MB      # Maximum file size
      max-request-size: -1      # Maximum request size (unlimited)
```

##### Excel File Format Requirements

The imported Excel file should contain the following columns (in order):
1. Name (姓名)
2. Age (年龄)
3. Class (班级)
4. University (大学)
5. Student Number (学号)
6. Major (专业)
7. Phone (联系电话)
8. Score (成绩)

#### Performance Optimization

- **Batch Processing**: Use batch insert during import to reduce database interactions
- **Memory Management**: Process data in batches to avoid loading large amounts of data into memory at once
- **Multi-threading**: Utilize thread pool for concurrent processing to improve speed
- **Paginated Query**: Use paginated query during export to avoid querying large amounts of data at once

#### Notes

1. **File Format**: Only `.xlsx` format Excel files are supported
2. **Data Volume**: It is recommended not to import more than 1 million records at a time
3. **Memory**: Pay attention to JVM memory configuration when processing large data volumes
4. **Database**: Ensure database connection pool is configured properly to avoid insufficient connections

#### FAQ

**Q: Memory overflow during import?**
A: You can adjust the batch processing size (BATCH_COUNT) or increase JVM memory configuration

**Q: Some sheets fail during multi-sheet import?**
A: Check if the number of sheets exceeds 20, or check logs for specific error messages

**Q: Export file download fails?**
A: Check response header settings and file size limit configuration

#### Contribution

1.  Fork the repository
2.  Create `Feat_xxx` branch
3.  Commit your code
4.  Create Pull Request

Issues and Pull Requests are welcome!

#### License

This project is licensed under the [LICENSE](LICENSE) license

#### Related Links

- [EasyExcel Official Documentation](https://easyexcel.opensource.alibaba.com/)
- [Spring Boot Official Documentation](https://spring.io/projects/spring-boot)
- [MyBatis-Plus Official Documentation](https://baomidou.com/)
