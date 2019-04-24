package com.zfei.util.ExcelUtil;

import com.zfei.util.ExcelUtil.ExcelLogs;
import com.zfei.util.ExcelUtil.ExcelUtil;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * 测试导入Excel 97/2003
 */
public class TestImportDeviceLog {

    @Test
    public void importXlsx() throws FileNotFoundException {

        int sheetNo = 4;
        int beginRow = 2;
        int beginColumn = 2;
        int skiprow = 0;
        importXlsx(sheetNo, beginRow, beginColumn, skiprow);
    }

    public void importXlsx(int sheetNo, int beginRow, int beginColumn, int skiprow) throws FileNotFoundException {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "spring-service.xml" });
        final JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");

        File f = new File("src/test/resources/HS-Platform-FakeData_20190422.xlsx");
        InputStream inputStream = new FileInputStream(f);

        ExcelLogs logs = new ExcelLogs();
        Collection<Map> importExcel = ExcelUtil
                .importExcel(Map.class, inputStream, "yyyy/MM/dd HH:mm:ss", logs, sheetNo, beginRow, beginColumn,
                        skiprow, 0);
        long time = System.currentTimeMillis();

        for (Map m : importExcel) {
            System.out.println(m);

            String device = m.get("device").toString();
            String deviceName = m.get("device name").toString();
            String deviceId = m.get("device id").toString();
            String state = m.get("state").toString();

            String event_type = m.get("event type").toString();
            String event = m.get("event").toString();
            String event_time = m.get("event time").toString();


            String sqladd = "insert into home_security_device_log_temp(device,device_name,dev_id,"
                    + "state,event_type,event,event_time,status,gmt_create,gmt_modified)"
                    + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {

                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sqladd, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, device);
                    ps.setString(2, deviceName);
                    ps.setString(3, deviceId);
                    ps.setString(4, state);
                    ps.setString(5, event_type);
                    ps.setString(6, event);
                    ps.setString(7, event_time);
                    ps.setInt(8, 1);
                    ps.setLong(9, time);
                    ps.setLong(10, time);
                    return ps;
                }
            }, keyHolder);

        }

    }

    public static void main(String[] args) {
        //        String level = "Level 2";
        //        System.out.println(level.substring(6, 7));

        LocalDateTime ldt = parseStringToDateTime("2019-03-10 10:01:01", "yyyy-MM-dd HH:mm:ss");
        System.out.println(getTimestampOfDateTime(ldt));

    }

    public String get20UUID() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[2] + idd[3];
    }

    public static LocalDateTime parseStringToDateTime(String time, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, df);
    }

    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();

    }

}
