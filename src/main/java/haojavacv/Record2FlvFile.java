package haojavacv;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

/**
 * rtmp 拉流保存到flv文件
 */
public class Record2FlvFile {
    public String streamURL= "rtmp://192.168.66.162:1935/live/haotestA1";
    public String mediaFileName = "E:\\meeting_record_0819HHH.flv";
    //m 秒数
    public static void  frameRecord(String inputFile, String outputFile,Long m) throws Exception{
        boolean isStart = true;
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.setOption("rtsp_transport","tcp");
        //帧数
        grabber.setFrameRate(25);
        grabber.setVideoBitrate(2000000);
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720,1);
        recorder.setFrameRate(25);
        recorder.setVideoBitrate(2000000);
        recordByFrame(grabber, recorder, isStart,m);
    }

    private static void recordByFrame(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder, Boolean status,Long m)
            throws Exception{
        try {
            grabber.start();
            recorder.setVideoCodec(grabber.getVideoCodec());
            recorder.start();
            Frame frame = null;
            //计算帧数
            long index=m*25;
            //已经录制帧数
            long finishIndex=0L;
            //Long start=CommonUtil.getSecondTimestamp();
            while (status&& (frame = grabber.grabFrame()) != null) {
                recorder.record(frame);
                finishIndex++;
                //Long end=CommonUtil.getSecondTimestamp();
                if(index<=finishIndex){
                    System.out.println("完成录制");
                    status=false;
                    break;
                }
            }
            recorder.stop();
            grabber.stop();
        } finally {
            if (grabber != null) {
                grabber.stop();
            }
        }
    }
    public static void main(String args[]){
        Record2FlvFile recorder = new Record2FlvFile();
        try {
            recorder.frameRecord(recorder.streamURL,recorder.mediaFileName,120L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
