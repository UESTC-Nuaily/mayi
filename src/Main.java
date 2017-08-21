import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;



public class Main {
    public static void main(String[] args) throws IOException{
        for(int i=1;;i++) {
            String s = Main.sendGet("http://www.mayi.com/chengdu/"+i, "d1=2017-08-24&d2=2017-08-25");
            if(i!=1){
                if (!s.split("蚂蚁短租_第")[1].split("页")[0].equals(String.valueOf(i))) {
                    break;
                }
            }
            System.out.println("Page : "+i);
            String sonarProjects = s.split("var rooms = \"")[1].split("\"")[0];
            String[] roomList=sonarProjects.split(",");
            for(int m=0;m<roomList.length;m++) {
                String room = Main.sendGet("http://www.mayi.com/room/"+roomList[m], "");
                System.out.println(room.split("share_roomTitle='")[1].split("';")[0]);
                Object comments = Main.sendGet("http://www.mayi.com/room/getComment", "roomId="+roomList[m]);
                JSONObject jsonObject = JSONObject.fromObject(comments);
                RoomStatus rs = (RoomStatus)JSONObject.toBean(jsonObject,RoomStatus.class);
                System.out.print("房源总好评率: "+ Math.round(100*Float.parseFloat(rs.getLodgeunitStat().getIntegrated_praise_ratio()))+"%, ");
                System.out.print("整洁卫生: "+ Math.round(100*Float.parseFloat(rs.getLodgeunitStat().getSanitation_praise_ratio()))+"%, ");
                System.out.print("设施环境: "+ Math.round(100*Float.parseFloat(rs.getLodgeunitStat().getMatch_description_praise_ratio()))+"%, ");
                System.out.print("交通位置: "+ Math.round(100*Float.parseFloat(rs.getLodgeunitStat().getTraffic_praise_ratio()))+"%, ");
                System.out.print("房东服务: "+ Math.round(100*Float.parseFloat(rs.getLodgeunitStat().getSafety_praise_ratio()))+"%, ");
                System.out.print("性价比: "+ Math.round(100*Float.parseFloat(rs.getLodgeunitStat().getValue_praise_ratio()))+"%, ");
                System.out.print("本房源评价: "+String.valueOf(rs.getRoomCntCount())+", ");
                System.out.print("房东收到的所有评价: "+ String.valueOf(rs.getLandlordCntCount()));
                System.out.println(" ");
                System.out.println(" ");
            }
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
