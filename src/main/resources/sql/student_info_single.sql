SET FOREIGN_KEY_CHECKS = 0;
SET NAMES utf8mb4;
-- student_info_single DDL
CREATE TABLE `student_info_single` (`id` BIGINT NOT NULL AUTO_INCREMENT Comment "id主键",
`stu_name` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
`stu_age` INT NULL,
`stu_class` VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
`stu_university` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
`stu_number` BIGINT NULL,
`stu_major` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
`stu_phone` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
`stu_score` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
INDEX `stu_number`(`stu_number` ASC) USING BTREE,
PRIMARY KEY (`id`)) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
SET FOREIGN_KEY_CHECKS = 1;
