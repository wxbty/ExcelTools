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
import java.util.*;

/**
 * 测试导入Excel 97/2003
 */
public class TestImportExcel {

    Map<String, Integer> type = new HashMap<>();

    Map<String, Integer> state = new HashMap<>();

    Map<String, Integer> handler = new HashMap<>();

    Map<String, Integer> level = new HashMap<>();

    {
        type.put("Entry/Exit", 201);
        type.put("SOS", 202);
        type.put("Fire", 203);
        type.put("Flood", 204);
        type.put("Stranger", 205);

        state.put("On Going", 301);
        state.put("Clear", 302);

        handler.put("Processing On Platform", 401);
        handler.put("Sent to Monitor Center", 402);
        handler.put("Resolved by User", 403);

        level.put("Level 1", 101);
        level.put("Level 2", 102);
        level.put("Level 3", 103);
        level.put("Level 4", 104);

    }

    @Test
    public void importXlsx() throws FileNotFoundException {

        int sheetNo = 0;
        int beginRow = 2;
        int beginColumn = 3;
        int skiprow = 3;
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
        Integer generatedId;

        Map<String, Set<String>> map = new HashMap<>();
        for (Map m : importExcel) {
            System.out.println(m);
            String sql1 = "select * from home_security_family where name = '" + m.get("family").toString() + "'";
            if ("Davis".equals(m.get("family").toString())) {
                System.out.println("sql1=" + sql1);
            }
            List<Map<String, Object>> list = jdbcTemplate.queryForList(
                    "select * from home_security_family where name = '" + m.get("family").toString() + "'");
            System.out.println("list.size=" + list.size() + ",family=" + m.get("family").toString());
            if (list.size() == 0) {
                String sqladd = "insert into home_security_family(company_id,name,country,city,"
                        + "county,zip_code,address,state,longitude,latitude,mobile,email,contact,security_mode,"
                        + "security_status,last_trigger_time,remark,status,gmt_create,gmt_modified)"
                        + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(new PreparedStatementCreator() {

                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(sqladd, Statement.RETURN_GENERATED_KEYS);
                        ps.setInt(1, 1);
                        ps.setString(2, m.get("family").toString());
                        ps.setString(3, "1");
                        ps.setString(4, "1");
                        ps.setString(5, "1");
                        ps.setString(6, "1");
                        ps.setString(7, "1");
                        ps.setInt(8, 1);
                        ps.setDouble(9, 1);
                        ps.setDouble(10, 1);
                        ps.setString(11, "1");
                        ps.setString(12, "1");
                        ps.setString(13, "1");
                        ps.setInt(14, 1);
                        ps.setInt(15, 1);
                        ps.setLong(16, 1L);
                        ps.setString(17, "1");
                        ps.setInt(18, 1);
                        ps.setLong(19, time);
                        ps.setLong(20, time);
                        return ps;
                    }
                }, keyHolder);

                generatedId = keyHolder.getKey().intValue();
            } else {
                generatedId = Integer.parseInt(list.get(0).get("id").toString());
            }

            String sql =
                    "insert into home_security_event(family_id,dev_id,level,title,content,type,handler,state,attachment,encrypt_key,status,gmt_create,gmt_modified)"
                            + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String levelstr = m.get("alarm level").toString().substring(6, 7);
            int handler = this.handler.get(m.get("handler").toString());
            int state = this.state.get(m.get("state").toString());
            int type = this.type.get(m.get("alarm type").toString());
            int level = this.level.get(m.get("alarm level").toString());
            System.out.println("type=" + type);

            jdbcTemplate.update(sql, new Object[] { generatedId, m.get("device id").toString(), level, "default_title",
                    "default_content", type, handler, state, "default_attach", "encrypt_key", 1, time, time });

        }

    }

    public static void main(String[] args) {
        String level = "Level 2";
        System.out.println(level.substring(6, 7));
    }

    class Family {

        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
