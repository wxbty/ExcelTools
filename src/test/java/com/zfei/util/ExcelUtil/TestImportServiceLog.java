package com.zfei.util.ExcelUtil;

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

/**
 * 测试导入Excel 97/2003
 */
public class TestImportServiceLog {


    @Test
    public void importXlsx() throws FileNotFoundException {

        int sheetNo = 5;
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

            String order_id = m.get("order id").toString();
            String state = m.get("state").toString();
            String kit = m.get("kit").toString();
            String service = m.get("service").toString();

            String user = m.get("user").toString();
            String fee = m.get("fee").toString();
            String contract = m.get("contract").toString();
            String subtime = m.get("subcription time / due time").toString();
            String region = m.get("region").toString();
            String family = m.get("family").toString();
            String address = m.get("address").toString();


            String sqladd = "insert into home_security_service_temp(order_id,state,kit,"
                    + "service,user,fee,contract,subcripton_time,region,family,address,gmt_create,gmt_modified)"
                    + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {

                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sqladd, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, order_id);
                    ps.setString(2, state);
                    ps.setString(3, kit);
                    ps.setString(4, service);
                    ps.setString(5, user);
                    ps.setString(6, fee);
                    ps.setString(7, contract);
                    ps.setString(8, subtime);
                    ps.setString(9, region);
                    ps.setString(10, family);
                    ps.setString(11, address);
                    ps.setLong(12, time);
                    ps.setLong(13, time);
                    return ps;
                }
            }, keyHolder);

        }

    }

    public static void main(String[] args) {

        LocalDateTime ldt = parseStringToDateTime("2019-03-10 10:01:01", "yyyy-MM-dd HH:mm:ss");
        System.out.println(getTimestampOfDateTime(ldt));

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
