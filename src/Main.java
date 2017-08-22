import net.sf.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;


public class Main {
    public static void main(String[] args) throws IOException{
        int roomCount=0;
        int commentCount=0;
        System.out.println("开始扫描");
        try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
                /* 写入Txt文件 */
            File writename = new File(".\\data.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            for(int i=1;;i++) {
                String s = Main.sendGet("http://www.mayi.com/chengdu/"+i, "d1=2017-08-24&d2=2017-08-25");
                if(i!=1){
                    if (!s.split("蚂蚁短租_第")[1].split("页")[0].equals(String.valueOf(i))) {
                        break;
                    }
                }
                System.out.println("Page : "+i);
                out.write("Page : "+i);
                out.newLine();
                String sonarProjects = s.split("var rooms = \"")[1].split("\"")[0];
                String[] roomList=sonarProjects.split(",");
                for(int m=0;m<roomList.length;m++) {
                    roomCount++;
                    String room = Main.sendGet("http://www.mayi.com/room/"+roomList[m], "");
                    out.write("----------------------------------------------------------");
                    out.newLine();
                    if(room.split("share_roomTitle='").length!=1) {
                        out.write(room.split("share_roomTitle='")[1].split("';")[0]);
                        out.newLine();
                        Object stats = Main.sendGet("http://www.mayi.com/room/getComment", "roomId="+roomList[m]);
                        JSONObject statsJsonObject = JSONObject.fromObject(stats);
                        RoomStatus rs = (RoomStatus)JSONObject.toBean(statsJsonObject,RoomStatus.class);
                        out.write("本房源评价: "+String.valueOf(rs.getRoomCntCount())+", ");
                        out.write("房源总好评率: "+ Math.round(100*Float.parseFloat(rs.getLodgeunitStat().getIntegrated_praise_ratio()))+"%, ");
                        out.write("整洁卫生: "+ Math.round(100*Float.parseFloat(rs.getLodgeunitStat().getSanitation_praise_ratio()))+"%, ");
                        out.write("设施环境: "+ Math.round(100*Float.parseFloat(rs.getLodgeunitStat().getMatch_description_praise_ratio()))+"%, ");
                        out.write("交通位置: "+ Math.round(100*Float.parseFloat(rs.getLodgeunitStat().getTraffic_praise_ratio()))+"%, ");
                        out.write("房东服务: "+ Math.round(100*Float.parseFloat(rs.getLodgeunitStat().getSafety_praise_ratio()))+"%, ");
                        out.write("性价比: "+ Math.round(100*Float.parseFloat(rs.getLodgeunitStat().getValue_praise_ratio()))+"%, ");
                        out.newLine();
                        out.write("房东名: "+ room.split("<font>")[1].split("</font>")[0]+" , ");
                        out.write("房东收到的所有评价: "+ String.valueOf(rs.getLandlordCntCount())+", ");
                        out.write("房东回复率: "+ String.valueOf(rs.getResponseRatio())+", ");
                        out.write("房东订单确认时间: "+rs.getConfirmMinute());
                        out.newLine();
                        out.write("房东头像: "+room.split("landlordDesL")[1].split("src=\"")[1].split("\"")[0]+" , ");
                        out.newLine();
                        out.newLine();
                        out.write("----------------------------------------------------------");
                        out.newLine();
                        Object comments = Main.sendGet("http://www.mayi.com/comment/id-"+roomList[m]+"/type-1/dataType-1/comments-p1-1000.json", "");
                        JSONObject commentsJsonObject = JSONObject.fromObject(comments);
                        roomComments cs = (roomComments)JSONObject.toBean(commentsJsonObject,roomComments.class);
                        if(cs.getData().getComments()!=null) {
                            for (int h = 0; h < cs.getData().getComments().size(); h++) {
                                Object t = cs.getData().getComments().get(h);
                                String a = t.toString();
                                out.write("评价" + (h + 1) + ": " + a.split("content=")[1].split(",")[0]);
                                commentCount++;
                                out.newLine();
                                out.write("评价时间: " + a.split("timeString=")[1].split(",")[0]);
                                out.newLine();
                                out.write("评价人: " + a.split("nickname=")[1].split(",")[0]);
                                out.newLine();
                                out.write("评价人电话号码: " + a.split("mobile=")[1].split(",")[0]);
                                out.newLine();
                                out.write("评价人头像: " + a.split("headimageurl=")[1].split(",")[0]);
                                out.newLine();
                                out.newLine();
                            }
                        }
                        out.newLine();
                        out.newLine();
                    }
                }
            }
            out.newLine();
            out.newLine();
            out.write("房源数 : "+roomCount);
            out.newLine();
            out.write("评价数 : "+commentCount);
            out.newLine();
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("authorization", "Basic aTA3NjYwNDpJbml0aWFsMA==");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
