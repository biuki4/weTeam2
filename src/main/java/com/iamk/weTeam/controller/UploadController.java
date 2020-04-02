package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.UnicomResponseEnums;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.FtpUtils;
import com.iamk.weTeam.common.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.avatarPath}")
    private String filePath;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @PassToken
    @RequestMapping("/uploadImg")
    public ResultUtil upload(MultipartFile file, HttpServletRequest req){
        System.out.println("上传文件");
        System.out.println(file);
        try{
            if(file.isEmpty()){
                System.out.println("文件为空");
            }
            // 文件名
            String fileName = file.getOriginalFilename();
            System.out.println("fileName: " + fileName);

            //获取文件后缀名
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            System.out.println("suffixName: " + suffix);

            //重新生成文件名
            fileName = UUID.randomUUID() + suffix;
            System.out.println("fileName: " + fileName);

            //添加日期目录
            String format = sdf.format(new Date());
            System.out.println("format: " + format);

            //指定本地文件夹存储图片
            String path = filePath + format + "/";
            System.out.println("path: " + path);

            File dest = new File(path, fileName);
            if (!dest.getParentFile().exists()){
                dest.getParentFile().mkdirs();
            }
            //将图片保存到static文件夹里
            // dest.createNewFile();
            file.transferTo(new File(path + fileName));
            System.out.println("上传成功");
            String url =  path + fileName;
            System.out.println(url);
            System.out.println(fileName);
            System.out.println(url);
            return ResultUtil.success(UnicomResponseEnums.FILEUPLOAD_SUCCESS, url);
        }catch (Exception e){
            System.out.println("失败");
            return ResultUtil.error(UnicomResponseEnums.UPDATE_FAIL);
        }
    }

    @PassToken
    @RequestMapping("/test")
    public String uploadImg(@RequestParam("file") MultipartFile file,
                                          HttpServletRequest request) throws IOException {
        System.out.println("上传");
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        String filePath = null;

        Boolean flag = FtpUtils.uploadFile(fileName, inputStream);
        System.out.println("123");
        if (flag == true) {
            System.out.println("ftp上传成功！");
            filePath = fileName;
        }
        System.out.println(filePath);
        return filePath;
    }
}
