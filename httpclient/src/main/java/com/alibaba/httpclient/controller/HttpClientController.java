package com.alibaba.httpclient.controller;

import com.alibaba.httpclient.model.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class HttpClientController {

    @RequestMapping("/doGetControllerOne")
    public String doGetControllerOne(){
        return "123";
    }

    @RequestMapping("/doGetControllerTwo")
    public String doGetControllerTwo(String name,Integer age){
        return "没想到["+ name + "]都" + age + "岁了!";
    }

    @RequestMapping(value = "/doPostControllerOne",method = RequestMethod.POST)
    public String doPostControllerOne(){
        return "这个post请求没有任何参数";
    }

    @RequestMapping(value = "/doPostControllerTwo",method = RequestMethod.POST)
    public String doPostControllerTwo(String name,Integer age){
        return "没想到["+ name + "]居然才[" + age + "]岁!!!";
    }

    @RequestMapping(value = "/doPostControllerThree",method = RequestMethod.POST)
    public String doPostControllerThree(@RequestBody User user){
        return user.toString();
    }

    @RequestMapping(value = "/doPostControllerFour",method = RequestMethod.POST)
    public String doPostControllerFour(@RequestBody User user, Integer flag, String meaning){
        return user.toString()+ "\n" + flag + ">>>" +meaning;
    }

//    @RequestMapping(value = "/form/data",method = RequestMethod.POST)
//    public String formDataControllerTest(@RequestParam("name") String name, @RequestParam("age") Integer age) {
//        return "application/x-www-form-urlencoded请求成功，name值为[" + name + "]，age值为[" + age + "]";
//    }

    @RequestMapping(value = "/form/data",method = RequestMethod.POST)
    public String formDataControllerTest(@ModelAttribute User user) {
        return user.toString();
    }


    /**
     * httpclient传文件测试
     *
     * 注: 即multipart/form-data测试。
     * 注:多文件的话，可以使用数组MultipartFile[]或集合List<MultipartFile>来接收
     * 注:单文件的话，可以直接使用MultipartFile来接收
     *
     */
    @RequestMapping(value = "/file",method = RequestMethod.POST)
    public String fileControllerTest(@RequestParam("name") String name, @RequestParam("age") Integer age, @RequestParam("files") List<MultipartFile> multipartFiles) {

        StringBuilder sb = new StringBuilder(64);
        // 防止中文乱码
        sb.append("\n");
        sb.append("name=").append(name).append("\tage=").append(age);
        String fileName;
        for (MultipartFile file : multipartFiles) {
            sb.append("\n文件信息:\n");
            fileName = file.getOriginalFilename();
            if (fileName == null) {
                continue;
            }
            // 防止中文乱码
            // 在传文件时，将文件名URLEncode，然后在这里获取文件名时，URLDecode。就能避免乱码问题。
            try {
                fileName = URLDecoder.decode(fileName, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sb.append("\t文件名: ").append(fileName);
            sb.append("\t文件大小: ").append(file.getSize() * 1.0 / 1024).append("KB");
            sb.append("\tContentType: ").append(file.getContentType());
            sb.append("\n文件内容:\n");
            FileInputStream fileInputStream = null;
            FileOutputStream fileOutputStream = null;
            File file1 = new File("/Users/wangmengyang/wmy-repositories/java-web-code/httpclient/src/main/resources/config1.properties");
            boolean newFile = true;
            if (!file1.exists()) {
                try {
                    newFile = file1.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (newFile) {
                try {
                    fileInputStream = (FileInputStream) file.getInputStream();
                    fileOutputStream = new FileOutputStream(file1);

//                byte[] buf = new byte[inputStream.available()];
//                inputStream.read(buf);
//                sb.append(new String(buf));

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = fileInputStream.read(buf))!=-1){
                        sb.append(new String(buf,0,len));
                        fileOutputStream.write(buf,0,len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return  sb.toString();
    }

    @RequestMapping(value = "/is",method = RequestMethod.POST)
    public String fileControllerTest(@RequestParam("name") String name, InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder(64);
        sb.append("\nname值为: ").append(name);
        sb.append("\n输入流数据内容为: ");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        return  sb.toString();
    }
}
