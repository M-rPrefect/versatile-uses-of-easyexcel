package org.excel_demo.excel_demo.service.impl;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.excel_demo.excel_demo.config.SingleSheetListener;
import org.excel_demo.excel_demo.config.MoreSheetThreadListener;
import org.excel_demo.excel_demo.entity.StudentInfo;
import org.excel_demo.excel_demo.entity.StudentResultVO;
import org.excel_demo.excel_demo.mapper.StudentInfoMapper;
import org.excel_demo.excel_demo.service.StudentInfoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Chen和斌
 */
@Service
@Slf4j
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, StudentInfo> implements StudentInfoService {

    @Resource
    ThreadPoolExecutor excelThreadPoolExecutor;

    private static final int PAGE_SIZE = 60000;

    private static final int SHEET_SIZE = 100000;


    @Override
    public void importExcelSingleSheet(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), StudentInfo.class, new SingleSheetListener(this)).sheet().doRead();
    }

    @Override
    public void importExcelSingleThreadSheet(MultipartFile file) throws IOException {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        StudentInfoServiceImpl infoService = this;
        EasyExcel.read(file.getInputStream(), StudentInfo.class, new AnalysisEventListener<StudentInfo>() {
            private static final int BATCH_COUNT = 60000;


            /**
             * 缓存的数据
             */
            private final List<StudentInfo> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

            @Override
            public void invoke(StudentInfo studentInfo, AnalysisContext analysisContext) {
                cachedDataList.add(studentInfo);
                if (cachedDataList.size() >= BATCH_COUNT) {
                    saveData();
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                saveData();
            }

            public void saveData() {
                // 创建当前批次数据的副本
                List<StudentInfo> batchData = new ArrayList<>(cachedDataList);
                // 清空原缓存列表
                cachedDataList.clear();
                log.info("当前线程副本集合: {}", batchData.size());
                log.info("当前线程名称: {}", Thread.currentThread().getName());
                log.info("当前线程原本集合: {}", cachedDataList.size());

                futures.add(CompletableFuture.runAsync(() -> {
                    try {
                        log.info("当前线程集合: {}", batchData.size());
                        log.info("当前线程名称: {}", Thread.currentThread().getName());
                        infoService.saveBatch(batchData);
                    } catch (Exception e) {
                        log.error("导入数据出错: {}", e.getMessage());
                    } finally {
                        batchData.clear();
                    }
                }, excelThreadPoolExecutor));
            }
        }).sheet().doRead();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Override
    public void importExcelThreadMoreSheet(MultipartFile file) throws IOException {
        //获取sheet个数
        int size = EasyExcel.read(file.getInputStream()).build().excelExecutor().sheetList().size();
        log.info("sheet个数: {}", size);
        if (size > 0 && size <= 20) {
            ExecutorService executor = Executors.newFixedThreadPool(size);
            try {
                for (int i = 0; i < size; i++) {
                    int finalI = i;
                    executor.execute(() -> {
                        try {
                            EasyExcel.read(file.getInputStream(), StudentInfo.class, new MoreSheetThreadListener(this)).sheet(finalI).doRead();
                        } catch (Exception e) {
                            log.error("导入数据出错: {}", e.getMessage());
                        }
                    });
                }
                // 关闭线程池，不再接受新任务
                executor.shutdown();
                // 等待所有任务完成，最多等待30分钟
                if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
                    log.warn("线程池任务未在30分钟内完成，强制关闭");
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.error("等待线程池任务完成时被中断: {}", e.getMessage());
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                throw new IOException("导入任务被中断", e);
            } catch (Exception e) {
                log.error("导入过程中发生异常: {}", e.getMessage());
                executor.shutdownNow();
                throw new IOException("导入失败", e);
            }
        }

    }

    @Override
    public void inputExcelSingleSheet(HttpServletResponse response) throws IOException {
        long count = this.count();
        if (count <= 0) {
            return;
        }
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = java.net.URLEncoder.encode("学生信息表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        long maxId = 0;
        int pageNumTotal = ((int)count + PAGE_SIZE - 1) / PAGE_SIZE;
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), StudentInfo.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("sheet1").build();
            for (int i = 0; i < pageNumTotal; i++) {
                // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
                List<StudentInfo> infos = this.baseMapper.searchList(PAGE_SIZE, maxId);
                maxId = infos.get(infos.size() - 1).getId();
                excelWriter.write(infos, writeSheet);
                log.info("当前导出成功，页大小为：{}，最大id为：{}", infos.size(), maxId);
            }
        }
    }

    @Override
    public void inputExcelMoreSheet(HttpServletResponse response) throws IOException {
        long count = this.count();
        if (count <= 0) {
            return;
        }
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = java.net.URLEncoder.encode("学生信息表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        long maxId = 0;
        int pageNumTotal = ((int)count + PAGE_SIZE -1 ) / PAGE_SIZE;

        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), StudentInfo.class).build()) {
            for (int i = 0; i < pageNumTotal; i++) {
            WriteSheet writeSheet = EasyExcel.writerSheet("sheet" + i).build();
                List<StudentInfo> infos = this.baseMapper.searchList(PAGE_SIZE, maxId);
                maxId = infos.get(infos.size() - 1).getId();
                excelWriter.write(infos, writeSheet);
                log.info("当前导出成功，页大小为：{}，最大id为：{}", infos.size(), maxId);
            }
        }
    }

    @Override
    public void inputExcelMoreSheetThread(HttpServletResponse response) throws IOException {
        long count = this.count();
        int needSheetNum = ((int)count + SHEET_SIZE - 1 ) / SHEET_SIZE;
        if (count <= 0) {
            return;
        }
        int totalNum = ((int)count + PAGE_SIZE - 1 ) / PAGE_SIZE;

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = java.net.URLEncoder.encode("学生信息表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExecutorService executor = Executors.newFixedThreadPool(needSheetNum);
        try  {


//            CountDownLatch countDownLatch = new CountDownLatch(needSheetNum);
            //创建一个阻塞队列
            BlockingQueue<StudentResultVO> blockingQueue = new LinkedBlockingQueue<>();
            // 根据数据库分页的总的页数来
            for (int i = 0; i < needSheetNum; i++) {

                final int sheetIndex = i;
                executor.submit(() -> {
                    log.info("当前线程名称: {}", Thread.currentThread().getName());
                    long maxId = 0;
                    int pageNumTotal = (SHEET_SIZE + PAGE_SIZE - 1) / PAGE_SIZE;
                    if (sheetIndex > 0) {
                        maxId = this.baseMapper.getMaxId(SHEET_SIZE * sheetIndex - 1);
                    }
                    log.info("最大id：{}", maxId);
                    for (int j = 0; j < pageNumTotal; j++) {
                        List<StudentInfo> infos = this.baseMapper.searchList(PAGE_SIZE, maxId);
                        if (infos.isEmpty()) {
                            break;
                        }
                        maxId = infos.get(infos.size() - 1).getId();
                        StudentResultVO resultVO = new StudentResultVO();
                        resultVO.setIndex(sheetIndex);
                        resultVO.setStudentInfoList(infos);
                        blockingQueue.add(resultVO);
                        log.info("当前导出成功，页大小为：{}，最大id为：{}", infos.size(), maxId);
                    }

//                    countDownLatch.countDown();

                });

            }
            //异步任务遍历队列
            //消费队列
            try(ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), StudentInfo.class).build()) {
                int processedCount = 0;
                while (processedCount < totalNum) {
                    StudentResultVO take = blockingQueue.poll(10, TimeUnit.MINUTES);
                    WriteSheet writeSheet = EasyExcel.writerSheet("sheet" + take.getIndex()).build();
                    excelWriter.write(take.getStudentInfoList(), writeSheet);
                    log.info("消费--当前下标：{}",processedCount );
                    log.info("消费--当前数据大小：{}",take.getStudentInfoList().size());
                    processedCount++;
//                    countDownLatch.countDown();
                }
            } catch (Exception e) {
//                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
//            countDownLatch.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }

    }

}
