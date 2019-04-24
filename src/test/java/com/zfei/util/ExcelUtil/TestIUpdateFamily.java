package com.zfei.util.ExcelUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * 更新家庭
 */
public class TestIUpdateFamily {


    @Test
    public void importXlsx() throws FileNotFoundException {

        int sheetNo = 2;
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

        for (Map m : importExcel) {
            System.out.println(m);


            String familyName = m.get("family").toString();
            //            String tel = m.get("tel").toString();
            //            String address = m.get("address").toString();
            String contact01name = m.get("contact01name").toString();
            String contact01relation = m.get("contact01relation").toString();
            String contact01tel = m.get("contact01tel").toString();
            String contact02name = m.get("contact02name").toString();
            String contact02relation = m.get("contact02relation").toString();
            String contact02tel = m.get("contact02tel").toString();
            String contact03name = m.get("contact03name").toString();
            String contact03relation = m.get("contact03relation").toString();
            String contact03tel = m.get("contact03tel").toString();

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name",contact01name);
            jsonObject.put("relation",contact01relation);
            jsonObject.put("tel",contact01tel);
            jsonArray.add(jsonObject);

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("name",contact02name);
            jsonObject2.put("relation",contact02relation);
            jsonObject2.put("tel",contact02tel);
            jsonArray.add(jsonObject2);

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("name",contact03name);
            jsonObject3.put("relation",contact03relation);
            jsonObject3.put("tel",contact03tel);
            jsonArray.add(jsonObject3);

            String updateSql = "update home_security_family set contact = '"+jsonArray.toJSONString()+"' where name='"+familyName+"'";
            jdbcTemplate.update(updateSql);

//            String updateSql = "update home_security_family set address ='"+address+"',mobile = '"+tel+"' where name='"+familyName+"'";
//            jdbcTemplate.update(updateSql);

        }

    }



}
