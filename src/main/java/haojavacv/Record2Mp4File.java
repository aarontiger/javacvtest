package haojavacv;


import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Record2Mp4File extends Thread{
    private static Logger logger = LoggerFactory.getLogger(Record2Mp4File.class);
    public String streamURL= "rtmp://127.0.0.1:1935/live/haotestA1";//流地址 网上有自行百度
    public String mediaFileName = "E:\\meeting_record_0819.mp4";
    public String filePath;//文件路径
    public Integer id;//案件id
    public Integer audioChannel;//是否录制声音


    @Override
    public void run() {
// 获取视频源
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(streamURL);
        FFmpegFrameRecorder recorder = null;
        try {
            grabber.start();
            Frame frame = grabber.grabFrame();
            if (frame != null) {
                //保存到本地的文件
                File outFile = new File(this.mediaFileName);
                if (!outFile.isFile()) outFile.createNewFile();
// 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
                recorder = new FFmpegFrameRecorder(this.mediaFileName, frame.imageWidth, frame.imageHeight, 1);
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);//直播流格式
                recorder.setFormat("mp4");//录制的视频格式
                recorder.setFrameRate(25);//帧数
                recorder.start();
                int count = 1000;
                while ((frame != null) && count>0) {
                    count --;
                    recorder.record(frame);//录制
                    frame = grabber.grabFrame();//获取下一帧
                }
                recorder.record(frame);
//停止录制
                recorder.stop();
                grabber.stop();
            }
        } catch (FrameGrabber.Exception e) {
            logger.error("视频录制异常", e);
            e.printStackTrace();
        } catch (FrameRecorder.Exception e) {
            logger.error("视频录制异常", e);
            e.printStackTrace();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (null != grabber) {
                try {
                    grabber.stop();
                } catch (FrameGrabber.Exception e) {
                    logger.error("视频录制异常", e);
                    e.printStackTrace();
                }
            }
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (FrameRecorder.Exception e) {
                    logger.error("视频录制异常", e);
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String args[]){
        Record2Mp4File thread = new Record2Mp4File();
        thread.start();
    }
}
