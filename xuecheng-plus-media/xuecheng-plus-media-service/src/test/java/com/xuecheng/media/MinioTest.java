package com.xuecheng.media;

import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.media
 * @className: MinioTest
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/2 22:14
 * @version: 1.0
 */
public class MinioTest {
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://120.48.50.36:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    public void test_upload() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        UploadObjectArgs objectArgs = UploadObjectArgs.builder()
                .bucket("testbucket")
                .filename("C:\\Users\\CAIXI\\Desktop\\temp.mp3")
                .object("temp.mp3")
                .build();
        minioClient.uploadObject(objectArgs);
    }


    // 将分块上传到minio
    @Test
    public void uploadChunk() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        for (int i = 0; i < 2; i++) {
            UploadObjectArgs objectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .filename("C:\\Users\\CAIXI\\Music\\VipSongsDownload\\chunk\\" + i)
                    .object("chunk/" + i)
                    .build();

            // 上传文件
            minioClient.uploadObject(objectArgs);
            System.out.println("上传分块" + i + "   成功!");
        }
    }

    // 调用minio合并分块接口

    @Test
    public void testMerge() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        //  指定分块文件信息
//        List<ComposeSource> sources = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            ComposeSource composeSource = ComposeSource.builder().bucket("testbucket").object("chunk/" + i).build();
//            sources.add(composeSource);
//        }
        List<ComposeSource> sourceList = Stream.iterate(0, i -> ++i).limit(2).map(i -> ComposeSource.builder().bucket("testbucket").object("chunk/" + i).build()).collect(Collectors.toList());
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge01.mgg")
                .sources(sourceList)
                .build();



        // 合并文件
        minioClient.composeObject(composeObjectArgs);
    }

    // 批量清理分块



}
