package com.xuecheng.media.service;

import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.model.PageParams;
import com.xuecheng.model.PageResult;
import com.xuecheng.model.RestResponse;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;

import java.io.File;
import java.util.List;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
  * @author Mr.M
  * @date 2022/9/10 8:57
 */
 public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);


 boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName);


 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath, String objectName);

 public MediaFiles addMediaFilesToDb(Long companyId , String fileMd5, UploadFileParamsDto uploadFileParamsDto , String bucket , String objectName);

 public RestResponse<Boolean> checkFile(String fileMd5);
 public RestResponse<Boolean> checkChunk(String fileMd5 , int chunkIndex);

 RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath);

 RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

 File downloadFileFromMinIO(String bucket, String objectName);

 MediaFiles getFileById(String mediaId);
}
