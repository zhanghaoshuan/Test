package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2018/4/10.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file ,String path) {
        String fileName = file.getOriginalFilename();
        
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;

        logger.info("开始上传文件，上传文件名：{}，上传的路径：{}，新文件名{}", fileName, path, uploadFileName);
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);//赋予创建的文件的权限
            fileDir.mkdir();
        }
        File targetFile=new File(path,uploadFileName);

        try{
            file.transferTo(targetFile);

            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            targetFile.delete();
        }catch (IOException e){
            logger.error("上传文件失败",e);
            return null;
        }
        return targetFile.getName();
    }
}
