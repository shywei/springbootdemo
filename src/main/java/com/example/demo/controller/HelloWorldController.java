package com.example.demo.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
	private static final String serverURL1 = "http://vop.baidu.com/server_api";
    private static final String serverURL2 = "http://tsn.baidu.com/text2audio";
    private static String token = "";
    private static final String testFileName = "output.wav";
    //put your own params here
    private static final String apiKey = "WNOLe4NEXY5GZFYNzEDc5WIq";
    private static final String secretKey = "Z1wHxMhg76Ig35VhSSvWF3CN7G2y7ULQ";
    private static final String cuid = "sadadaw";
    private static String text = "";
    
    private static void getToken() throws Exception {
        String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" + 
            "&client_id=" + apiKey + "&client_secret=" + secretKey;
        HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();
        token = new JSONObject(printResponse(conn)).getString("access_token");
    }
    
    private static String printResponse(HttpURLConnection conn) throws Exception {
        if (conn.getResponseCode() != 200) {
            // request error
            return "";
        }
        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        System.out.println(new JSONObject(response.toString()).toString(4));
        return response.toString();
    }
    
    @RequestMapping("/hello")
    public String index() {
        return "Hello World";
    }
    
    @RequestMapping(value="/anwser", method=RequestMethod.GET)
    public void anwser(@RequestParam(value="text", required=false) String text,HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	getToken();
    	File pcmFile = new File(testFileName);
        HttpURLConnection conn = (HttpURLConnection) new URL(serverURL2).openConnection();
        
        // add request header
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        // send request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes("tex="+URLEncoder.encode(text,"UTF-8")+"&lan="+URLEncoder.encode("zh","UTF-8")+"&ctp="+URLEncoder.encode("1","UTF-8")+"&tok="+URLEncoder.encode(token,"UTF-8")+"&cuid="+URLEncoder.encode(cuid,"UTF-8"));
        wr.flush();
        wr.close();

        System.out.println(conn.getResponseCode());
        InputStream fis=conn.getInputStream();
        response.setHeader("Content-Type","audio/mp3");
        OutputStream os = response.getOutputStream();

        byte[] bis = new byte[1024];
        while(-1 != fis.read(bis)){
        	os.write(bis);
        	}
    }
}