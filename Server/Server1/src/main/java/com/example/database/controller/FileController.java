package com.example.database.controller;

import com.example.database.entity.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@RestController
@CrossOrigin
public class FileController {
    private Logger logger = LoggerFactory.getLogger(FileController.class);

    @Value("${file-save-path}")
    private String fileSavePath;

    @PostMapping("/upload")
    public String upload(@RequestParam(value = "file") MultipartFile file, Model model, HttpServletRequest request, @RequestParam(value = "type") int type, @RequestHeader(value = "user-agent") String userAgent) {
        if (file.isEmpty()) {
            System.out.println("文件为空");
        }

        logger.info("客户端请求上传文件");

        logger.info("获得的其他参数type=" + type);

        logger.info("获得的Header user-agent=" + userAgent.toString());

        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String key = names.nextElement().toString();
            logger.info("客户端传过来的参数＃key＝" + key + ",value=" + request.getParameterValues(key).toString());
        }
        Enumeration headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String key = headers.nextElement().toString();
            String info = "客户端传过来的Header参数:key＝" + key + ",value=" + request.getHeader(key);
            logger.info(info);
        }

        // BMP、JPG、JPEG、PNG、GIF
        String fileName = file.getOriginalFilename();  // 文件名
        logger.info("上传文件名：" + fileName);
        String suffixName = fileName.substring(fileName.lastIndexOf("."));  // 后缀名
        // 验证上传的文件是否图片
        if (!".bmp".equalsIgnoreCase(suffixName) && !".jpg".equalsIgnoreCase(suffixName)
                && !".jpeg".equalsIgnoreCase(suffixName)
                && !".png".equalsIgnoreCase(suffixName)
                && !".gif".equalsIgnoreCase(suffixName)) {
            return "上传失败，请选择BMP、JPG、JPEG、PNG、GIF文件！";
        }

        //创建一个本地路径m将上传的保存到本地
        String targetFile = fileSavePath + fileName;
        File dest = new File(targetFile);
        // 如果文件的父路径不存在，则创建
        if (fileName.startsWith("/") && !dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        // 开始存放文件到指定目录去
        try {
            file.transferTo(dest);
            //String url = request.getScheme() + "://" +request.getServerName() + ":" + request.getServerPort() + "/" + fileName;
            //logger.info("图片上传，访问URL为 " + url);
            return "上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/query",method = RequestMethod.GET)
    public List<FileInfo> query(){
        logger.info("客户端请求查询文件");
        File file = new File(fileSavePath);
        List<FileInfo> list = new ArrayList<>();
        File[] subFiles = file.listFiles();
        if(subFiles != null){
            for(File subFile:subFiles){
                FileInfo fileInfo = new FileInfo();
                String suffixName = subFile.getName().substring(subFile.getName().lastIndexOf("."));
                if(".bmp".equalsIgnoreCase(suffixName) || ".jpg".equalsIgnoreCase(suffixName)
                        || "jpeg".equalsIgnoreCase(suffixName) || "png".equalsIgnoreCase(suffixName)
                        || "gif".equalsIgnoreCase(suffixName))
                {
                    fileInfo.setFile_name(subFile.getName());
                    int size = (int)(subFile.length() / 1024) + 1;
                    fileInfo.setFile_size(size);
                    list.add(fileInfo);
                }
            }
        }
        return list;
    }


    @RequestMapping(value = "/download",method = RequestMethod.POST)
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @RequestParam(value = "imageName") String imageName)
    {

        logger.info("客户端请求下载文件");
        System.out.println(imageName);
        String filePath = fileSavePath + imageName;
        try {
            response.setContentType("image/jpeg;charset=UTF-8");
            OutputStream out = response.getOutputStream();
            FileInputStream fis = new FileInputStream(filePath);
            int len = -1;
            byte[] data = new byte[1024];

            while((len = fis.read(data)) != -1){
                out.write(data, 0 , len);
            }
            System.out.println("传输完成");
            fis.close();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

