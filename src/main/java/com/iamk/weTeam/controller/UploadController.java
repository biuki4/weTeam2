package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.Enum.UnicomResponseEnums;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.FtpUtils;
import com.iamk.weTeam.common.utils.QiNiuUtil;
import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.repository.ActivityRepository;
import com.iamk.weTeam.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.avatarPath}")
    private String filePath;
    @Value("${upload.avatarPath2}")
    private String filePath2;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Resource
    GameRepository gameRepository;
    @Resource
    ActivityRepository activityRepository;

    /**
     * 只能用于本地，未解决上传到服务器
     * @param file
     * @param req
     * @return
     */
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
            log.error(e.getMessage());
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

    @Autowired
    private QiNiuUtil qiniuUtil;

    /**
     * 上传竞赛图片
     * @param file
     * @param id
     * @return
     */
    @PassToken
    @RequestMapping("/upload/{id}")
    public ResultUtil upload(@RequestParam("file") MultipartFile file, @PathVariable Integer id) {
        if (file.isEmpty()) {
            return ResultUtil.error(UnicomResponseEnums.NO_FILE);
        }
        try {
            FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String fileExtend = originalFilename.substring(originalFilename.lastIndexOf("."));
            //默认不指定key的情况下，以文件内容的hash值作为文件名
            String format = sdf.format(new Date());
            // String fileKey = UUID.randomUUID().toString().replace("-", "") + fileExtend;
            String fileKey = format + "-" + UUID.randomUUID() + fileExtend;
            String url = qiniuUtil.upload(fileInputStream,fileKey);
            // 存入game
            gameRepository.updatePosterUrl(url, id);
            return ResultUtil.success();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtil.error(UnicomResponseEnums.SERVER_BUSY);
        }
    }

    /**
     * 上传活动图片
     * @param file
     * @param id
     * @return
     */
    @PassToken
    @RequestMapping("/uploadActivity/{id}")
    public ResultUtil uploadActivity(@RequestParam("file") MultipartFile file, @PathVariable Integer id) {
        System.out.println("上传文件");
        if (file.isEmpty()) {
            return ResultUtil.error(UnicomResponseEnums.NO_FILE);
        }
        try {
            FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String fileExtend = originalFilename.substring(originalFilename.lastIndexOf("."));
            //默认不指定key的情况下，以文件内容的hash值作为文件名
            String format = sdf.format(new Date());
            // String fileKey = UUID.randomUUID().toString().replace("-", "") + fileExtend;
            String fileKey = format + "-" + UUID.randomUUID() + fileExtend;
            qiniuUtil.setBucketName("iamk-weteam-activity");
            qiniuUtil.setFileDomain("http://cdn.activityimg.iamk.top");
            String url = qiniuUtil.upload(fileInputStream,fileKey);
            // 存入activity
            activityRepository.updatePosterUrl(url, id);
            return ResultUtil.success();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtil.error(UnicomResponseEnums.SERVER_BUSY);
        }
    }
}
