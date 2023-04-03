package com.xuecheng.media;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.util.*;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.media
 * @className: BigFileTest
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/3 15:07
 * @version: 1.0
 */
public class BigFileTest {

    @Test
    public void testChunk() throws IOException {
        File sourceFile = new File("C:\\Users\\CAIXI\\Music\\VipSongsDownload\\One_Day.mgg");
        String chunkFilePath = "C:\\Users\\CAIXI\\Music\\VipSongsDownload\\chunk";
        int chunkSize = 1024 * 1024 * 5;
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);

        RandomAccessFile raf_r = new RandomAccessFile(sourceFile,"r");

        byte[] bytes = new byte[1024];
        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFilePath+ "/" + i);

            RandomAccessFile raf_rw = new RandomAccessFile(chunkFile,"rw");
            int len = -1;
            while((len = raf_r.read(bytes)) != -1 ){
                raf_rw.write(bytes,0,len);
                if(chunkFile.length() >= chunkSize) {
                    break;
                }
            }
            raf_rw.close();
        }
        raf_r.close();
    }

    @Test
    public void testMerge() throws IOException {

        File chunkFolder = new File("C:\\Users\\CAIXI\\Music\\VipSongsDownload\\chunk");

        File sourceFile = new File("C:\\Users\\CAIXI\\Music\\VipSongsDownload\\One_Day.mgg");

        File mergeFile = new File("C:\\Users\\CAIXI\\Music\\VipSongsDownload\\One_Day222.mgg");

        File[] files = chunkFolder.listFiles();

        List<File> filesList = Arrays.asList(files);
        Collections.sort(filesList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName()) ;
            }
        });

        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile,"rw");

        byte[] bytes = new byte[1024];
        for(File file:filesList){
            RandomAccessFile raf_r = new RandomAccessFile(file , "r");
            int len = -1 ;
            while((len = raf_r.read(bytes ))!=-1){
                raf_rw.write(bytes,0,len);
            }
            raf_r.close();
        }
        raf_rw.close();

        FileInputStream fileInputStream_merge = new FileInputStream(mergeFile);
        FileInputStream fileInputStream_source = new FileInputStream(sourceFile);

        String md5_merge = DigestUtils.md5DigestAsHex(fileInputStream_merge);
        String md5_source = DigestUtils.md5DigestAsHex(fileInputStream_source);

        if(md5_merge.equals(md5_source)){
            System.out.println("文件合并成功");
        }
    }
}
