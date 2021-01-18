package com.alibaba.httpclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.httpclient.model.User;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpClient {

    /**
     * GET无参
     */
    @Test
    public void doGetTestOne() {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet("http://localhost:12345/doGetControllerOne");

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            System.out.println("响应状态码为:" + response.getStatusLine().getStatusCode());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
                // 设置解码方式,解决乱码情况的发生
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * GET有参(方式一：直接拼接URL)
     */
    @Test
    public void doGetTestWayOne() {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 参数
        StringBuilder params = new StringBuilder();
        try {
            // 字符数据最好encoding以下;这样一来，某些特殊字符才能传过去(如:某人的名字就是“&”,不encoding的话,传不过去)
            params.append("name=").append(URLEncoder.encode("&", "utf-8"));
            params.append("&");
            params.append("age=24");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        // 创建Get请求
        HttpGet httpGet = new HttpGet("http://localhost:12345/doGetControllerTwo" + "?" + params);
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 配置信息
            RequestConfig requestConfig = RequestConfig.custom()
                    // 设置连接超时时间(单位毫秒)
                    .setConnectTimeout(5000)
                    // 设置请求超时时间(单位毫秒)
                    .setConnectionRequestTimeout(5000)
                    // socket读写超时时间(单位毫秒)
                    .setSocketTimeout(5000)
                    // 设置是否允许重定向(默认为true)
                    .setRedirectsEnabled(true).build();

            // 将上面的配置信息 运用到这个Get请求里
            httpGet.setConfig(requestConfig);

            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);

            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * GET有参(方式二：使用URI获得HttpGet)
     */
    @Test
    public void doGetTestWayTwo() {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 参数
        URI uri = null;
        try {
            // 将参数放入键值对类NameValuePair中,再放入集合中
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", "&"));
            params.add(new BasicNameValuePair("age", "18"));

            // 设置uri信息,并将参数集合放入uri;
            // 注:这里也支持"一个键值对一个键值"对地往里面放setParameter(String key, String value)
            uri = new URIBuilder().setScheme("http").setHost("localhost")
                    .setPort(12345).setPath("/doGetControllerTwo")
                    .setParameters(params).build();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        // 创建Get请求
        HttpGet httpGet = new HttpGet(uri);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 配置信息
            RequestConfig requestConfig = RequestConfig.custom()
                    // 设置连接超时时间(单位毫秒)
                    .setConnectTimeout(5000)
                    // 设置请求超时时间(单位毫秒)
                    .setConnectionRequestTimeout(5000)
                    // socket读写超时时间(单位毫秒)
                    .setSocketTimeout(5000)
                    // 设置是否允许重定向(默认为true)
                    .setRedirectsEnabled(true).build();

            // 将上面的配置信息 运用到这个Get请求里
            httpGet.setConfig(requestConfig);

            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);

            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * POST无参
     */
    @Test
    public void doPostTestOne() {

        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 创建Post请求
        HttpPost httpPost = new HttpPost("http://localhost:12345/doPostControllerOne");
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * POST有参(普通参数)
     */
    @Test
    public void doPostTestTwo() {

        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 参数
        StringBuilder params = new StringBuilder();
        try {
            // 字符数据最好encoding以下;这样一来，某些特殊字符才能传过去(如:某人的名字就是“&”,不encoding的话,传不过去)
            params.append("name=").append(URLEncoder.encode("&", "utf-8"));
            params.append("&");
            params.append("age=24");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        // 创建Post请求
        HttpPost httpPost = new HttpPost("http://localhost:12345/doPostControllerTwo" + "?" + params);

        // 设置ContentType(注:如果只是传普通参数的话,ContentType不一定非要用application/json)
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * POST有参(对象参数)
     */
    @Test
    public void doPostTestThree() {

        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 创建Post请求
        HttpPost httpPost = new HttpPost("http://localhost:12345/doPostControllerThree");
        User user = new User();
        user.setName("潘晓婷");
        user.setAge(18);
        user.setGender("女");
        user.setMotto("姿势要优雅~");
        // 利用阿里的fastjson，将Object转换为json字符串;
        String jsonString = JSON.toJSONString(user);

        StringEntity entity = new StringEntity(jsonString, "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);

        httpPost.setHeader("Content-Type", "application/json;charset=utf8");

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * POST有参(普通参数 + 对象参数)
     */
    @Test
    public void doPostTestFour() {

        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 创建Post请求
        // 参数
        URI uri = null;
        try {
            // 将参数放入键值对类NameValuePair中,再放入集合中
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("flag", "4"));
            params.add(new BasicNameValuePair("meaning", "这是什么鬼？"));
            // 设置uri信息,并将参数集合放入uri;
            // 注:这里也支持一个键值对一个键值对地往里面放setParameter(String key, String value)
            uri = new URIBuilder().setScheme("http").setHost("localhost").setPort(12345)
                    .setPath("/doPostControllerFour").setParameters(params).build();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }

        HttpPost httpPost = new HttpPost(uri);
//        HttpPost httpPost = new HttpPost("http://localhost:12345/doPostControllerThree1");

        // 创建user参数
        User user = new User();
        user.setName("潘晓婷");
        user.setAge(18);
        user.setGender("女");
        user.setMotto("姿势要优雅~");

        // 将user对象转换为json字符串，并放入entity中
        StringEntity entity = new StringEntity(JSON.toJSONString(user), "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);

        httpPost.setHeader("Content-Type", "application/json;charset=utf8");

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 表单测试
     */
    @Test
    public void doFormTestOne() {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://localhost:12345/form/data");
        CloseableHttpResponse response = null;
        try {
            httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
            List<BasicNameValuePair> params = new ArrayList<>(2);
            params.add(new BasicNameValuePair("name","任怡旭"));
            params.add(new BasicNameValuePair("age","25"));
            params.add(new BasicNameValuePair("gender","男"));
            params.add(new BasicNameValuePair("motto","我套死你猴子的"));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
            httpPost.setEntity(formEntity);
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为"+response.getStatusLine());
            if (responseEntity != null){
                System.out.println("响应长度为"+responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * 发送文件
     *
     * multipart/form-data传递文件(及相关信息)
     *
     * 注:如果想要灵活方便的传输文件的话，
     *    除了引入org.apache.httpcomponents基本的httpclient依赖外
     *    在额外引入org.apache.httpcomponents的httpmime依赖。
     *    追注:即便不引入httpmime依赖，也是能传输文件的，不过功能不够强大。
     *
     */
    @Test
    public void doFileUpload() {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://localhost:12345/file");
        CloseableHttpResponse response = null;
        try {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            // 第一个文件
            String filesKey = "files";
            File file1 = new File("/Users/wangmengyang/wmy-repositories/java-web-code/httpclient/src/main/resources/config.properties");
            multipartEntityBuilder.addBinaryBody(filesKey, file1);
            // 第二个文件(多个文件的话，使用可一个key就行，后端用数组或集合进行接收即可)
//            File file2 = new File("C:\\Users\\JustryDeng\\Desktop\\头像.jpg");
            // 防止服务端收到的文件名乱码。 我们这里可以先将文件名URLEncode，然后服务端拿到文件名时在URLDecode。就能避免乱码问题。
            // 文件名其实是放在请求头的Content-Disposition里面进行传输的，如其值为form-data; name="files"; filename="头像.jpg"
//            multipartEntityBuilder.addBinaryBody(filesKey, file2, ContentType.DEFAULT_BINARY, URLEncoder.encode(file2.getName(), "utf-8"));
            // 其它参数(注:自定义contentType，设置UTF-8是为了防止服务端拿到的参数出现乱码)
            ContentType contentType = ContentType.create("text/plain", StandardCharsets.UTF_8);
            multipartEntityBuilder.addTextBody("name", "邓沙利文", contentType);
            multipartEntityBuilder.addTextBody("age", "25", contentType);

            HttpEntity httpEntity = multipartEntityBuilder.build();
            httpPost.setEntity(httpEntity);

            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            System.out.println("HTTPS响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("HTTPS响应内容长度为:" + responseEntity.getContentLength());
                // 主动设置编码，来防止响应乱码
                String responseStr = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                System.out.println("HTTPS响应内容为:" + responseStr);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void doStream() {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://localhost:12345/is?name=邓沙利文");
        CloseableHttpResponse response = null;
        try {
            InputStream is = new ByteArrayInputStream("流啊流~".getBytes());
            InputStreamEntity ise = new InputStreamEntity(is);
            httpPost.setEntity(ise);

            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            System.out.println("HTTPS响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("HTTPS响应内容长度为:" + responseEntity.getContentLength());
                // 主动设置编码，来防止响应乱码
                String responseStr = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                System.out.println("HTTPS响应内容为:" + responseStr);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
