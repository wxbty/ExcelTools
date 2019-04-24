package com.zfei.util.ExcelUtil;

import java.util.ArrayList;
import java.util.List;


public class ExcelLogs {
    private Boolean hasError;
    private List<com.zfei.util.ExcelUtil.ExcelLog> logList;

    /**
     * 
     */
    public ExcelLogs() {
        super();
        hasError = false;
    }

    /**
     * @return the hasError
     */
    public Boolean getHasError() {
        return hasError;
    }

    /**
     * @param hasError
     *            the hasError to set
     */
    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    /**
     * @return the logList
     */
    public List<com.zfei.util.ExcelUtil.ExcelLog> getLogList() {
        return logList;
    }

    public List<com.zfei.util.ExcelUtil.ExcelLog> getErrorLogList() {
        List<com.zfei.util.ExcelUtil.ExcelLog> errList = new ArrayList<>();
        for (com.zfei.util.ExcelUtil.ExcelLog log : this.logList) {
            if (log != null && ExcelUtil.isNotBlank(log.getLog())) {
                errList.add(log);
            }
        }
        return errList;
    }

    /**
     * @param logList
     *            the logList to set
     */
    public void setLogList(List<com.zfei.util.ExcelUtil.ExcelLog> logList) {
        this.logList = logList;
    }

}
