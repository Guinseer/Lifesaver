package com.guinseer.lifesaver.communications;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by chou6 on 2017-06-13.
 */

public class RSS {
    private Context context;

    public RSS(Context c) {
        this.context = c;
    }

    public List<Map<String, String>> find() {
        List<Map<String, String>> temp = new ArrayList<>();
        URL url;
        try {
            Map<String, String> data = new LinkedHashMap<String, String>();

            url = new URL("http://www.kma.go.kr/wid/queryDFS.jsp?gridx=57&gridy=121");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();
            String s = "";
            //data태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
            NodeList nodeList = doc.getElementsByTagName("data");
            //data 태그를 가지는 노드를 찾음, 계층적인 노드 구조를 반환
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (i == 8)
                    break;
                //날씨 데이터를 추출
                s += "" + i + ": 날씨 정보: ";
                Node node = nodeList.item(i); //data엘리먼트 노드
                Element fstElmnt = (Element) node;

                NodeList nameList = fstElmnt.getElementsByTagName("temp");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();
                String s1 = nameList.item(0).getNodeValue() + "";
                data.put("온도", s1);
                NodeList websiteList = fstElmnt.getElementsByTagName("wfKor");
                String s2 = "" + websiteList.item(0).getChildNodes().item(0).getNodeValue();
                data.put("날씨", s2);
                NodeList low = fstElmnt.getElementsByTagName("tmn");
                String s3 = "" + low.item(0).getChildNodes().item(0).getNodeValue();
                data.put("최저", s3);
                NodeList high = fstElmnt.getElementsByTagName("tmx");
                String s4 = "" + high.item(0).getChildNodes().item(0).getNodeValue();
                data.put("최고", s4);
                Log.d("weather : ", data.toString());
                temp.add(data);
            }
        } catch (Exception e) {
            Toast.makeText(context, "기상청 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
        return temp;
    }


}
