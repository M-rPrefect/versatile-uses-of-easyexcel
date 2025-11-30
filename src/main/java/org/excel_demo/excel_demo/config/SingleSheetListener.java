package org.excel_demo.excel_demo.config;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import org.excel_demo.excel_demo.entity.StudentInfo;
import org.excel_demo.excel_demo.service.StudentInfoService;

import java.util.List;

/**
 * @author Chen和斌
 */ //@Slf4j
public class SingleSheetListener extends AnalysisEventListener<StudentInfo> {

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 2000;

    private final StudentInfoService studentInfoService;


    /**
     * 缓存的数据
     */
    private List<StudentInfo> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    public SingleSheetListener(StudentInfoService studentInfoService) {
        this.studentInfoService = studentInfoService;
    }


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     */
    @Override
    public void invoke(StudentInfo data, AnalysisContext context) {
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
//        log.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
//        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        studentInfoService.saveBatch(cachedDataList);
//        log.info("存储数据库成功！");
    }
}
